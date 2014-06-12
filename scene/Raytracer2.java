/*package scene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import utils.math.Point;

public class Raytracer2 extends Raytracer
{
	protected ArrayList<Thread> threads;
	protected Point ptVue;
	protected Vecteur rayon;
	protected boolean continuer = false;

	protected int nThread;	
	protected int nNotThread;	
	
	protected Point[] intersections;
	protected double[] distances;
	protected Semaphore[] calculer;
	protected ArrayList<Integer> lightObjects;
	protected Semaphore reprendre;
	protected Semaphore mutex;
	protected int termines;
	
	public LancerRayonOptimise4(Scene scene, Vue vue) {
		super(scene, vue);				
		optimize(scene);
		//Creation de la liste des threads
		lightObjects = new ArrayList<Integer>();
		threads      = new ArrayList<Thread>();
		Iterator<Objet> it = objets.iterator();
		int i = 0;
		this.nThread    = 0;
		this.nNotThread = 0;
		while (it.hasNext())	
		{			
			if (it.next().getPriority() < - 4) 
			{
				threads.add(new Thread(new PlusProcheObjet(nThread + nNotThread,i++)));
				nThread++;
			}
			else 		
			{
				lightObjects.add(nThread + nNotThread);
				nNotThread++;
			}			
		}	
		
		this.calculer = new Semaphore[nThread];
		for (i=0 ; i<calculer.length ; i++)
			this.calculer[i] = new Semaphore(0);
		
		this.distances      = new double[nThread + nNotThread];
		this.intersections  = new Point [nThread + nNotThread];		
	}

	public void optimize(Scene scene)
	{
		this.objets = new ArrayList<Objet>();
		
		if (scene.getListeObjets().size() != 0)
		{
			ArrayList<Objet> objetsScene = scene.getListeObjets();
			int s = objetsScene.size();
		
			objets.add(objetsScene.get(0));
		
			for (int i=1 ; i<s ; i++)
				for (int j=0 ; j<i ; j++)
				{
					if (objetsScene.get(i).getPriority() >= objets.get(j).getPriority())
					{
						objets.add(j,objetsScene.get(i));
						break;
					}
					else if (j == i-1)
						objets.add(i,objetsScene.get(i));
				}
		}
	}

	public Objet plusProcheObjet(Point ptVue, Vecteur rayon, Point[] intersection)
	{	
		this.ptVue = ptVue;
		this.rayon = rayon;
		
		mutex     = new Semaphore(1);
		reprendre = new Semaphore(0);
		termines  = 0;
		
		for (int i=0 ; i<nThread + nNotThread ; i++)
		{
			intersections[i] = null;
			distances[i]     = -1;
		}				
		
		if (!continuer)
		{
			Iterator<Thread> it1 = threads.iterator();
			continuer = true;	
			while (it1.hasNext())	
				it1.next().start();			
		}
		else 
			for (int i=calculer.length-1 ; i>=0 ; i--)
				calculer[i].release();		
			
		for (int i=0 ; i<nNotThread ; i++) {
			int index = lightObjects.get(i);
			Objet obj = objets.get(index);
			computeIntersection(obj,index);
		}
				
		try {
			reprendre.acquire();
		} catch (InterruptedException e) { }	
		
		int k = 0, i = 0;
		double dist = -1;
		for (double d : distances)
		{
			boolean b = (d != -1 && (dist == -1 || d <= dist));
			k         = b ? i : k;
			dist      = b ? d : dist;
			i++;
		}			
		
		intersection[0] = intersections[k];
		
		if (dist == -1)	 
			return null;
		else 
			return objets.get(k);
	}
	

	private class PlusProcheObjet implements Runnable
	{
		private Objet obj;
		private int indexObj, indexSem;
		
		public PlusProcheObjet(int indexObj, int indexSem)
		{
			this.obj      = objets.get(indexObj);
			this.indexObj = indexObj;
			this.indexSem = indexSem;
		}
		
		public void run()
		{
			while (continuer)
			{	
				try {										
					computeIntersection(obj,indexObj);					
					mutex.acquire();
					termines++;
					if (termines == nThread) 
						reprendre.release(); 
					mutex.release();
					calculer[indexSem].acquire();
				} catch (InterruptedException e) { }				
			}
		}
	}
	
	public void computeIntersection(Objet obj, int index)
	{
		try	{		
			Point point          = obj.intersection(ptVue,rayon);					
			distances[index]     = point.distance(ptVue);
			intersections[index] = point;
		} catch (IntersectionVideException e) { }
	}
	public Color compoAmbiante(Objet obj, Point p)
	{		
		float[] defaultKa = { (float) .8,(float) .8,(float) .8 };
		int Ra = scene.getLumiereAmbiante().getRed();
		int Va = scene.getLumiereAmbiante().getGreen();
		int Ba = scene.getLumiereAmbiante().getBlue();
		
		if (obj == null)
			return new Color(defaultKa[0]*Ra/255,defaultKa[1]*Va/255,defaultKa[2]*Ba/255);
		else 
		{
			float[] Ka = obj.Ka(p);
			return new Color(Ka[0]*Ra/255,Ka[1]*Va/255,Ka[2]*Ba/255);
		}			
	}
	public boolean estEclaire(Source source, Point p)
	{
		Vecteur rayon      = new Vecteur(p,source.getPos());
		boolean obstacle   = false;
		Iterator<Objet> it = objets.iterator();
		
		//On arrete la recherche au premier obstacle trouve
		while (it.hasNext() && !obstacle)	
		{
			Objet obj = it.next();
			if (obj.isShown())
				try {
					Point intersection = obj.intersection(p,rayon);
					double sens = Vecteur.produitScalaire(new Vecteur(p,intersection),rayon);
					//Si on trouve une intersection, rien ne dit qu'elle n'est pas derriere la source et non devant :
					//il faut se livrer a un calcul de distances pour le savoir
					obstacle  = p.distance(intersection) < p.distance(source.getPos()) && sens > 0;					
				} catch (IntersectionVideException e) { }	
		}
			
		return !obstacle;
	}
	
	public Color compoDiffuse(Point p, Source source, Objet obj) throws IntersectionVideException
	{
		Color I = source.getIntensite();
		int R = I.getRed(), V = I.getGreen(), B = I.getBlue();	
				
		Vecteur rayon = new Vecteur(p,source.getPos());		
		Vecteur normale = obj.normale(p);
		
		float cosinus   = (float) (Vecteur.produitScalaire(rayon,normale) / (normale.getNorme()*rayon.getNorme()));		
		
		cosinus = Math.abs(cosinus);		
		float[] Ka      = obj.Ka(p);									
		return new Color(R*cosinus*Ka[0]/255,V*cosinus*Ka[1]/255,B*cosinus*Ka[2]/255);
	}
	
	public Color compoSpeculaire(Point p, Source source, Objet obj) throws IntersectionVideException
	{	
		Color I = source.getIntensite();
		int R = I.getRed(), V = I.getGreen(), B = I.getBlue();
		
		Vecteur rayon = new Vecteur(p,source.getPos());
		Vecteur rayonIncident = new Vecteur(vue.getPtVue(),p);
		Vecteur rayonMedian = Vecteur.combLin(rayon,rayonIncident,1,-1);
		Vecteur normale = obj.normale(p);		
		
		double cosinus = (float) (Vecteur.produitScalaire(normale,rayonMedian) / 
				(rayonMedian.getNorme()*normale.getNorme()));
		
		cosinus = Math.abs(cosinus);	
									
		float[] reflectance  = obj.reflectance();
		float[] brillance    = obj.brillance();							
		
		float Rspec =  (float) (reflectance[0]*R*((float) Math.pow(cosinus,brillance[0]))/255);
		float Vspec =  (float) (reflectance[1]*V*((float) Math.pow(cosinus,brillance[1]))/255);
		float Bspec =  (float) (reflectance[2]*B*((float) Math.pow(cosinus,brillance[2]))/255);
		
		return new Color(Rspec,Vspec,Bspec);
	}

	public Color pixelColor(Point p, Vecteur rayon, int depth, int depthMax, double refractionMilieu, int composantes) 
			throws IntersectionVideException
	{		
		int b4 = (composantes            >> 4);
		int b3 = ((composantes -= 16*b4) >> 3);
		int b2 = ((composantes -= 8 *b3) >> 2);	
		int b1 = ((composantes -= 4 *b2) >> 1);				
		int b0 = composantes - 2*b1;
		composantes = b0 + b1*2 + b2*4 + b3*8 + b4*16;
			
		Objet obj = plusProcheObjet(p,rayon,intersection);
		
		if (obj != null && p.equals(intersection[0])) System.out.println("Fuck it "+obj.toString().substring(0,10));
		
		Point saveIntersection = obj == null ? null :
			new Point(intersection[0].getX(),intersection[0].getY(),intersection[0].getZ());
		
		//Composante ambiante
		Color compoAmbiante = b0 == 1 ? compoAmbiante(obj,saveIntersection) : new Color(0,0,0);
		
		//Si on rencontre l'infini ou qu'on atteint la profondeur maximale de calcul, on arrete ici
		if ((depth == depthMax && obj == null) || depth < 0 ) return new Color(0,0,0);
		if (obj == null) return compoAmbiante;	
		
		//Composantes diffuses et speculaires
		Color compoDiff_Spec = new Color(0,0,0);
		int nbSources        = scene.getListeSources().size();
		
		for (int k=0 ; k<nbSources ; k++)
		{
			Source source = scene.getListeSources().get(k);			
			if (estEclaire(source,saveIntersection))
			{					
				Color compoDiffuse    = b1 == 1 ? compoDiffuse(saveIntersection,source,obj)    : new Color(0,0,0);
				Color compoSpeculaire = b2 == 1 ? compoSpeculaire(saveIntersection,source,obj) : new Color(0,0,0);
				compoDiff_Spec = Pixel.syntheseCouleurs(compoDiff_Spec,compoDiffuse);
				compoDiff_Spec = Pixel.syntheseCouleurs(compoDiff_Spec,compoSpeculaire);				
			}			
		}
				
		//Calcul de la composante reflechie
		Color compoReflechie;		
		Color color;

		try {
			if ((obj.Kr()[0] <= 0.001 && obj.Kr()[1] <= 0.001 && obj.Kr()[2] <= 0.001) || b3 == 0)
				color = new Color(0,0,0);
			else
				color = pixelColor(saveIntersection,obj.rayonReflechi(saveIntersection,rayon),
								   depth-1,depth,refractionMilieu,composantes);
			float R     = obj.Kr()[0]*color.getRed() / 255;
			float V     = obj.Kr()[1]*color.getGreen() / 255;
			float B     = obj.Kr()[2]*color.getBlue() /255;		
			compoReflechie = new Color(R,V,B);	
		}
		catch (IntersectionVideException e) {
		  	//Voir explication de cette bizarrerie dans le catch de
			//la composante refractee
			compoReflechie = new Color(0,0,0);
			//e.printStackTrace();
		}
						
		//Calcul de la composante refractee
		Color compoRefractee;	
		double nvRefraction;
		
		if (refractionMilieu != obj.refraction())
			nvRefraction = obj.refraction();		
		else nvRefraction = scene.getIndice();	
		
		try {
			if ((obj.Kt()[0] <= 0.001 && obj.Kt()[1] <= 0.001 && obj.Kt()[2] <= 0.001) || b4 == 0)
				color = new Color(0,0,0);
			else			
				color = pixelColor(saveIntersection,obj.rayonRefracte(saveIntersection,rayon,refractionMilieu),
								   depth-1,depthMax,nvRefraction,composantes);
			
			float R     = obj.Kt()[0]*color.getRed() / 255;
			float V     = obj.Kt()[1]*color.getGreen() / 255;
			float B     = obj.Kt()[2]*color.getBlue() /255;
			compoRefractee = new Color(R,V,B);	
			
		} catch (Exception e) {
			//e.printStackTrace();
		}			
		//System.out.println(compoAmbiante + "    " + compoDiff_Spec + "    " + compoReflechie + "    " + compoRefractee);
		Color result = Pixel.syntheseCouleurs(compoAmbiante,compoDiff_Spec);
		result = Pixel.syntheseCouleurs(result,compoReflechie);
		result = Pixel.syntheseCouleurs(result,compoRefractee);
		
		return result;		
	}
	
	public Color pixelColor(Point p, Vecteur rayon, int depth, int depthMax, double refractionMilieu) 
			throws IntersectionVideException
	{
		return pixelColor(p,rayon,depth,depthMax,refractionMilieu,31);
	}
	public void calculerRendu(Afficheur painter, int depth)
	{
		calculerRendu(painter,depth,31);
	}
	
	public void calculerRendu(int depth)
	{
		calculerRendu(depth,31);
	}
	
	public void calculerRendu(Afficheur painter, int depth, int composantes) 
	{
		for (int i=0; i<lines ; i++)		
			for (int j=0; j<columns ; j++)
			{
				Point ptEcran = vue.getPixels()[i][j].getPoint();
				Vecteur rayon = new Vecteur(vue.getPtVue(),ptEcran);
				try 
				{
					Color color = pixelColor(vue.getPtVue(),rayon,depth,depth,scene.getIndice(),composantes);
					vue.setPixelColor(i,j,color);
					painter.paint(color,i,j);
				} 
				catch (IntersectionVideException e) 
				{
				}				
			}	
	}	
	
	
	
	public void calculerRendu(int depth, int composantes) 
	{
		for (int i=0; i<lines ; i++)		
			for (int j=0; j<columns ; j++)
			{
				Point ptEcran = vue.getPixels()[i][j].getPoint();
				Vecteur rayon = new Vecteur(vue.getPtVue(),ptEcran);
				try 
				{
					Color color = pixelColor(vue.getPtVue(),rayon,depth,depth,scene.getIndice(),composantes);
					vue.setPixelColor(i,j,color);
				} 
				catch (IntersectionVideException e) 
				{
				}				
			}	
	}
}
*/


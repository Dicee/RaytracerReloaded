package objects;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import utils.math.CustomMath;
import XML.XMLable;

public class Texture implements XMLable
{
  public static final float epsilon = (float) 0.01;
	
  protected double refraction;
  protected float[] brillance;
  protected float[] reflectance; 
  protected float[] Kr;
  protected float[] Kt;
  
  //Texture basique
  protected float[] Ka;
  
  //Texture avancee
  protected BufferedImage texture;
  protected String path;
  protected int repeat = 1;
  protected boolean adapter = false;
  
  public static final Texture defaultTexture;
  
  static {
	  float[] tb0 = { (float) 0.5,(float) 0.5,(float) 0.5 };
	  float[] tb1 = { 0,0,1 };
	  float[] tb2 = { 5,5,5 };
	  defaultTexture = new Texture(1,tb2,tb0,tb0,tb0,tb1);
  }
  
  /**Creation d'une texture a partir de ses caracteristiques de refraction,
   * de reflexion et d'absorption.
   * @param refraction	indice de refraction.
   * @param brillance	brillance de la texture.
   * @param reflectance reflectance de la texture.
   * @param Ka caracteristique d'absorption par l'objet.
   * @param Kr caracteristique du rayon reflechi.
   * @param Kt caracteristique du rayon refracte (transmis).
   */
  public Texture(double refraction,float[] brillance,float[] reflectance,
      float[] Ka,float[] Kr,float[] Kt) {
    if (refraction < 1 || testParam(brillance,5,300) || testParam(reflectance,0,1)
	|| testParam(Ka,0,1) || testParam(Kr,0,1) || testParam(Kt,0,1)) {
      throw new IllegalArgumentException("\n\n. l'indice de refraction doit etre superieur a 1 \n. la brillance est comprise" +
					" entre 5 et 300 pour chaque composante R, V, B.\n. les autres parametres sont compris entre 0 et 1 pour " +
					"chaque composante R, V, B.");		}

    //Si les parametres ont passe le test a epsilon pres, il reste peut-etre necessaire de ramener leurs valeurs dans l'intervalle exact
    //en deplacant de +/- epsilon car la suite des operations ne tolerera pas des valeurs en dehors de cet intervalle.      
    this.refraction  = refraction;
    this.brillance   = recalage(brillance,5,300);
    this.reflectance = recalage(reflectance,0,1);
    this.Ka          = recalage(Ka,0,1);
    this.Kr          = recalage(Kr,0,1);
    this.Kt          = recalage(Kt,0,1);
    this.texture     = null;
    this.path        = null;
  }
  
  /**Creation d'une texture a partir de ses caracteristiques de refraction,
   * de reflexion et d'absorption.
   * @param refraction indice de refraction.
   * @param brillance brillance de la texture.
   * @param reflectance reflectance de la texture.
   * @param image fichier contenant une texture 2D sous forme d'image
   * @param Kr caracteristique du rayon reflechi.
   * @param Kt caracteristique du rayon refracte (transmis).
   */
  public Texture(double refraction,float[] brillance,float[] reflectance,
      File image, float[] Kr, float[] Kt) throws IOException {
    if (refraction < 1 || testParam(brillance,5,300) || testParam(reflectance,0,1)
	|| testParam(Kr,0,1) || testParam(Kt,0,1)) {
      throw new IllegalArgumentException("\n\n. l'indice de refraction doit etre superieur a 1 \n. la brillance est comprise" +
					" entre 5 et 300 pour chaque composante R, V, B.\n. les autres parametres sont compris entre 0 et 1 pour " +
					"chaque composante R, V, B.");		}

    //Si les parametres ont passe le test a epsilon pres, il reste peut-etre necessaire de ramener leurs valeurs dans l'intervalle exact
    //en deplacant de +/- epsilon car la suite des operations ne tolerera pas des valeurs en dehors de cet intervalle.      
	this.texture     = ImageIO.read(image);
    this.refraction  = refraction;
    this.brillance   = recalage(brillance,5,300);
    this.reflectance = recalage(reflectance,0,1);
    this.Ka          = new float[] { 0,0,0 };
    this.Kr          = recalage(Kr,0,1);
    this.Kt          = recalage(Kt,0,1);
    this.path        = image.getAbsolutePath();
  }
  
   /**Creation d'une texture a partir de ses caracteristiques de refraction,
   * de reflexion et d'absorption.
   * @param refraction indice de refraction.
   * @param brillance brillance de la texture.
   * @param reflectance reflectance de la texture.
   * @param Ka caracteristiques d'absorption sous forme d'un ensemble de pixels.
   * @param path chemin d'acces du fichier image qui a servi a constituer Ka
   * @param Kr caracteristique du rayon reflechi.
   * @param Kt caracteristique du rayon refracte (transmis).
   * @param repeat nombre de repetitions du motif de la texture avancee
   * @param adapter 
   * @param adapter indique si la texture necessiste d'etre adaptee, c'est-a-dire s'il faut lui conferer
   *        des proprietes de symetrie spherique
   */
  private Texture(double refraction,float[] brillance,float[] reflectance,
      BufferedImage Ka, String path, float[] Kr, float[] Kt, int repeat, boolean adapter) {
    if (refraction < 1 || testParam(brillance,5,300) || testParam(reflectance,0,1)
	|| testParam(Kr,0,1) || testParam(Kt,0,1)) {
      throw new IllegalArgumentException("\n\n. l'indice de refraction doit etre superieur a 1 \n. la brillance est comprise" +
					" entre 5 et 300 pour chaque composante R, V, B.\n. les autres parametres sont compris entre 0 et 1 pour " +
					"chaque composante R, V, B.");		}

    //Si les parametres ont passe le test a epsilon pres, il reste peut-etre necessaire de ramener leurs valeurs dans l'intervalle exact
    //en deplacant de +/- epsilon car la suite des operations ne tolerera pas des valeurs en dehors de cet intervalle.      
  	
    this.texture     = Ka;   
    this.path        = path;
    this.adapter     = adapter;
    this.refraction  = refraction;
    this.brillance   = recalage(brillance,5,300);
    this.reflectance = recalage(reflectance,0,1);
    this.Ka          = new float[] { 0,0,0 };
    this.Kr          = recalage(Kr,0,1);
    this.Kt          = recalage(Kt,0,1);  
  }
  
  /**Creation d'une texture a partir de ses caracteristiques de refraction,
   * de reflexion et d'absorption.
   * @param refraction indice de refraction.
   * @param brillance brillance de la texture.
   * @param reflectance reflectance de la texture.
   * @param image fichier contenant une texture 2D sous forme d'image
   * @param Kr caracteristique du rayon reflechi.
   * @param Kt caracteristique du rayon refracte (transmis).
   * @param repeat nombre de repetitions du motif de la texture avancee
   * @param adapter indique si la texture necessiste d'etre adaptee, c'est-a-dire s'il faut lui conferer
   *        des proprietes de symetrie spherique
   */
  public Texture(double refraction,float[] brillance,float[] reflectance,
      File image, float[] Kr, float[] Kt, int repeat, boolean adapter) throws IOException {
	  
	  this(refraction,brillance,reflectance,image,Kr,Kt);
	  this.repeat  = repeat;
	  this.adapter = adapter;	
  }

  /**Methode servant a verifier la coherence des parametres brillance,
   * reflectance, Ka, Kr et Kt.Renvoie un booleen, true si le parametre est
   * a rejeter: ie mauvais format (taille) du parametre ou l'incorrection des
   * valeurs rencontrees.
   * @param param le parametre dont tester la coherence.
   * @param inf 	borne inferieure de l'intervalle.
   * @param sup		borne superieure de l'intervalle.
   * @return boolean, true si le parametre est a rejeter.
   */
  private boolean testParam(float[] param, float inf,
      float sup) {
    return !(param.length == 3 && intervalle(param[0],inf,sup) && 
	intervalle(param[1],inf,sup) && intervalle(param[2],inf,sup));
  }

  /**Methode servant a verifier l'appartenance d'un float a u intervalle de
   * float avec une precision de epsilon
   * @param nb		float dont tester l'appartenance a un intervalle.
   * @param borneInf 	borne inferieure de l'intervalle.
   * @param borneSup	borne superieure de l'intervalle.
   * @return boolean, true si le nombre est dans l'intervalle.
   */
  private boolean intervalle(float nb,float borneInf,float borneSup) {
    return (nb >= borneInf - epsilon && nb <= borneSup + epsilon);
  }
  
  /**Methode permettant de ramener dans l'intervalle exact [inf,sup] des triplets de valeurs de l'intervalle
   * [inf - epsilon,sup + epsilon] en arrondissant si necessaire.
   * @return triplet de valeurs recalees dans l'intervalle [inf,sup]
   */
  private float[] recalage(float[] param, float inf, float sup) {
    return new float[] { param[0] <= inf ? inf : param[0] >= sup ? sup : param[0],
    					 param[1] <= inf ? inf : param[1] >= sup ? sup : param[1],
    					 param[2] <= inf ? inf : param[2] >= sup ? sup : param[2] };
  }

  /* Accesseurs */

  /**Accesseur pour l'indice de refraction.
   * @return	l'indice de refraction de la texture.
   */
  public double refraction() {
    return this.refraction;
  }

  /**Accesseur pour la brillance.
   * @return	la brillance de la texture.
   */
  public float[] brillance() {
    return this.brillance;
  }

  /**Accesseur pour la reflectance.
   * @return	la reflectance de la structure.
   */
  public float[] reflectance() {
    return this.reflectance;
  }

  /**Accesseur pour le Ka constant.
   * @return le Ka de la texture.
   */
  public float[] Ka() {
    return this.Ka;
  }
  
  /**Retourner la hauteur de la texture 2D definissant le Ka si elle existe, 0 sinon.
   * @return hauteur de la texture 2D definissant le Ka si elle existe, 0 sinon
   */
  public int getHeight() {
	 
	  return texture != null ? adapter ? 2*texture.getHeight() : texture.getHeight() : 0;
  }
  
  /**Retourner la largeur de la texture 2D definissant le Ka si elle existe, 0 sinon.
   * @return largeur de la texture 2D definissant le Ka si elle existe, 0 sinon
   */
  public int getWidth() {
	  return texture != null ? adapter ? 2*texture.getWidth() : texture.getWidth() : 0;
  }
  
  /**Renvoyer la couleur du pixel (u,v) de la texture 2D definissant le Ka si elle existe, { 0,0,0 } sinon.
   * @return le Ka de la texture au point (u,v).
   */
  public float[] Ka(int u, int v) 
  {	
	  if (texture != null)  
	  {
		  int w = texture.getWidth(), h = texture.getHeight();
		  int i = CustomMath.mod(u,(adapter ? w - 1 : 0) + w);
		  int j = CustomMath.mod(v,(adapter ? h - 1 : 0) + h);
		 
		  int[] coords;
		  if (i < w && j < h) 	    			
				coords = new int[] { i,j };    			
			else if (i < w && j >= h)
				coords = new int[] { i,2*h-j-1 };
			else if (i >= w && j < h)
				coords = new int[] { 2*w-i-1,j };
			else
				coords = new int[] { 2*w-i-1,2*h-j-1 };	
		  
		  int rgb = texture.getRGB(coords[0],coords[1]);
		  Color c = new Color(rgb);
		  int r = c.getRed();
		  int g = c.getGreen();
		  int b = c.getBlue(); 
		  return new float[] { ((float) r)/255,((float) g)/255,((float) b)/255 };
	  } 
	  else   
		  return Ka;	  	   
  }

  /**Accesseur pour le Kr.
   * @return   le Kr de la texture.
   */
    public float[] Kr() {
    return this.Kr;
  }

  /**Accesseur pour le Kt.
   * @return	le Kt de la texture.
   */
  public float[] Kt() {
    return this.Kt;
  }

  /**Methode de description textuelle des caracteristiques de la texture.
   * @return une chaine de caracteres donnant les caracteristiques de la
   * 	     la texture.
   */
  public String toString() {
    return 
      "   Indice de refraction : " + refraction                                   + "\n" +
      "   Reflectance : "          + tripletToString(reflectance)                 + "\n" +
      "   Brillance : "            + tripletToString(brillance)                   + "\n" +
      "   Ka : "                   + (path == null ?  tripletToString(Ka) : path) + "\n" +
      "   Kr : "                   + tripletToString(Kr)                          + "\n" +
      "   Kt : "                   + tripletToString(Kt);
  }

  /**Methode de description textuelle des tableaux de floats a trois entrees
   * @param triplet		le tableau a 3 entrees, "triplet", dont on
   * 				veut la description textuelle.
   * @return une chaine de caracteres decrivant textuellement le tableau.
   */
  private String tripletToString(float[] triplet) {
    return
      "\tR : " + triplet[0] + 
      "\tV : " + triplet[1] + 
      "\tB : " + triplet[2];
  }
  
	/**Convertit un triplet de reels en chaine de caracteres dans un mode condense.*/
	private String tripletToStringCondense(float[] triplet)
	{
		return triplet[0] + " " +
			   triplet[1] + " " +
			   triplet[2];
	}
  
	/**Condenser les caracteristiques de la texture (necessaires a sa construction).
	 * @param align caractere d'espacement a utiliser entre les differents elements et en debut de chaine
	 * @param attributes attributs a placer dans la balise
	 * @retun les caracteristiques de l'objet (necessaires a sa construction) dans une balise XML
	 */
	public String toXML(String align, String attributes)
	{
		if (texture == null)
			return align + "<Texture>"                                 + "\n" + 
			   	   align + "\t" + refraction                           + "\n" +
			   	   align + "\t" + tripletToStringCondense(brillance)   + "\n" +
			   	   align + "\t" + tripletToStringCondense(reflectance) + "\n" +
			   	   align + "\t" + tripletToStringCondense(Ka)          + "\n" +
			   	   align + "\t" + tripletToStringCondense(Kr)          + "\n" +
			   	   align + "\t" + tripletToStringCondense(Kt)          + "\n" +
			   	   align + "</Texture>";
		else
			return align  + "<Texture>"                                + "\n" + 
		   	   	   align + "\t" + refraction                           + "\n" +
		   	   	   align + "\t" + tripletToStringCondense(brillance)   + "\n" +
		   	   	   align + "\t" + tripletToStringCondense(reflectance) + "\n" +
		   	   	   align + "\t" + "<url>" + path + "</url>"            + "\n" +
		   	       align + "\t" + repeat  + (adapter ? " 1" : " 0")    + "\n" +
		   	   	   align + "\t" + tripletToStringCondense(Kr)          + "\n" +
		   	   	   align + "\t" + tripletToStringCondense(Kt)          + "\n" +
		   	   	   align + "</Texture>";
	}
	
	/**Condenser les caracteristiques de la texture (necessaires a sa construction).
	 * @param align caractere d'espacement a utiliser entre les differents elements et en debut de chaine
	 * @retun les caracteristiques de l'objet (necessaires a sa construction) dans une balise XML
	 */
	public String getCarac(String align)
	{
		return toXML(align,"");
	}

	/**Creer une copie de la Texture.
	 * @return copie de la Texture
	 */
	public Texture clone()
	{
		if (texture == null)
			return new Texture(refraction,brillance,reflectance,Ka,Kr,Kt);
		else 
			return new Texture(refraction,brillance,reflectance,texture,path,Kr,Kt,repeat,adapter);
	}
	
	/**Acceder au nom du fichier servant a definir le Ka de la texture, s'il existe.
	 * @return chemin absolu du fichier servant a definir le Ka de la texture s'il existe, null sinon
	 */
	public String getAdvancedKa()
	{
		return path;
	}

	/**Fixer le nombre de repetitions du motif de la texture. N'a d'effet que sur une texture avancee.
	 * @param n nombre de repetitions du motif
	 */
	public void setPatternRepeat(int n) 
	{
		repeat = n;		
	}
	
	/**Renvoyer le nombre de repetitions du motif de la texture. N'a d'effet que sur une texture avancee.
	 * @return nombre de repetitions du motif
	 */
	public int getPatternRepeat() 
	{
		return repeat;		
	}
}

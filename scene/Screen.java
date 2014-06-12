package scene;

import javafx.scene.paint.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import objects.PlaneSurface;

import org.jdom2.Element;

import utils.math.Point;
import utils.math.Rotatable;
import utils.math.Translatable;
import utils.math.Vector3D;
import XML.XMLable;
import XML.basicTypes.XMLInteger;
import XML.basicTypes.XMLVector;
import utils.Copiable;

public class Screen implements XMLable, Translatable, Rotatable, Copiable<Screen> {	
	private int				density;
	private double			Xsize;
	private double			Ysize;
	private Pixel[][]		pix;
	private PlaneSurface	containingPlane;
	public Point			pView;	
	
	public Screen(Point X, Point O, Point Y, Point pView) {		
		this(X,O,Y,pView,4);
	}	
	
	public Screen(Point X, Point O, Point Y, Point pView, int density) {		
		this.containingPlane = new PlaneSurface(X,O,Y,null);			
		this.Xsize           = X.distance(O);
		this.Ysize           = O.distance(Y);
		this.density         = density;	
		this.pView           = pView;
		constructPixels();		
	}
	
	protected void constructPixels() {		
		Point[] pts        = containingPlane.getPoints();
		Point   X          = pts[0], O = pts[1], Y = pts[2];
		
		int dimX           = 1 + ((int) Xsize)*(density - 1);
		int dimY           = 1 + ((int) Ysize)*(density - 1);
		this.pix           = new Pixel[dimX][dimY];			
		
		Vector3D stepOnRow = (new Vector3D(O,X)).scale(1/(double)(dimX - 1));	
		Vector3D stepOnCol = (new Vector3D(O,Y)).scale(1/(double)(dimY - 1));
		
		double x = O.getX(), y = O.getY(), z = O.getZ();
		
		for (int i=0 ; i<dimX ; i++) {	
			double x1 = x, y1 = y, z1 = z;
			for (int j=0 ; j<dimY ; j++) {					
				pix[i][j] = new Pixel(new Point(x1,y1,z1),new Color(0,0,0,1));
				x1 += stepOnCol.getX();
				y1 += stepOnCol.getY(); 
				z1 += stepOnCol.getZ();
				
			}			
			x += stepOnRow.getX();
			y += stepOnRow.getY();
			z += stepOnRow.getZ();
		}
	}
		
	public Pixel[][] getPixels() {
		return pix;
	}
	
	public void save(File file, String formatName) throws IOException {
		Color[][] colors = getColors();					
		
		int longueur = colors.length;
		int largeur  = colors.length;			
		
		BufferedImage img = new BufferedImage(longueur,largeur,BufferedImage.TYPE_INT_RGB);
		      System.out.println("coucou");
		for (int i=0; i<longueur; i++) 
			for (int j=0; j<largeur; j++) 				
				img.setRGB(i,largeur-j-1,Pixel.toRGB(colors[i][j]));
        System.out.println("yo");
		ImageIO.write(img,formatName,file);
	}
	
	public void setDensity(int density) {
		this.density = density;	
		constructPixels();
	}
	
	public void resize(int factor) {
		Point[]  pts    = containingPlane.getPoints();
		Point    X      = pts[0], O = pts[1], Y = pts[2];
		Vector3D v      = new Vector3D(O,X);
		Vector3D w      = new Vector3D(O,Y);
		double   d      = ((double) factor)/100;
		
		O               = O.translate(Vector3D.linearCombination(v,w,(1 - d)/2,(1 - d)/2));
		X               = O.translate(v.scale(d));
		Y               = O.translate(w.scale(d));
				
		Xsize           = X.distance(O);
		Ysize           = O.distance(Y);		
		containingPlane = new PlaneSurface(X,O,Y,null);
		
		constructPixels();
	}
	
	public Color[][] getColors() {
		int lines = getPixels().length, columns = getPixels()[0].length; 
		Color[][] result = new Color[lines][columns];
		
		for (int i=0 ; i<lines ; i++)
			for (int j=0 ; j<columns ; j++)
				result[i][j] = getPixels()[i][j].color;
		return result;
	}
	
	public String getCarac(String align, String attributes)	{		
		return String.format("%s\n%s%d",containingPlane,align,density);
	}
	
	public String getCarac(String align) {		
		return getCarac(align,"");
	}
	
	@Override
	public String toString() {
		Point[]  pts = containingPlane.getPoints();
		Point    X   = pts[0], O = pts[1], Y = pts[2];
		return 	   "ECRAN :\n"                        +
				   "        - Point 1 : "             + X + "\n" +
				   "        - Point 2 : "             + O + "\n" +
				   "        - Point 3 : "             + Y;
	}

	public void rotateXYZ(double u, double v, double w) {
		containingPlane.rotateXYZ(u,v,w);
		constructPixels();
	}		
	
	/**Deplacer l'ecran par une translation de vecteur v.
	 * @param v vecteur de translation
	 */
	public void translate(Vector3D v) {
		containingPlane.translate(v);
		constructPixels();
	}
	
	public Point[] getPoints() {
		return containingPlane.getPoints();
	}
	
	public Point getCenter() {
		return pix[pix.length/2][pix[0].length/2].pos;
	}
	
	public Screen clone() {
		Point[]  pts = containingPlane.getPoints();
		Point    X   = pts[0], O = pts[1], Y = pts[2];
		return new Screen(X.clone(),O.clone(),Y.clone(),pView.clone(),density);
	}

    public void setPixelColor(int i, int j, Color color) {
		getPixels()[i][j].color = color;		
	}
    
	@Override
	public void rotateX(double u) {
		containingPlane.rotateXYZ(u,0,0);
	}

	@Override
	public void rotateY(double u) {
		containingPlane.rotateXYZ(0,u,0);
	}

	@Override
	public void rotateZ(double u) {
		containingPlane.rotateXYZ(0,0,u);
	}

	@Override
	public Element toXML() {
		Element result = new Element("Screen");
		Point[] pts    = getPoints();
		result.addContent(new XMLVector("pointOfView",pView));
		result.addContent(new XMLVector("A",pts[0]));
		result.addContent(new XMLVector("B",pts[1]));
		result.addContent(new XMLVector("C",pts[2]));
		result.addContent(new XMLInteger("density",density));
		return result;
	}

	@Override
	public void copy(Screen screen) {
		containingPlane.copy(screen.containingPlane);
		pView   = screen.pView.clone();
		density = screen.density;
		Xsize   = screen.Xsize;
		Ysize   = screen.Ysize;
	}
}

package objects;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jdom2.Attribute;
import org.jdom2.Element;

import utils.math.CustomMath;
import XML.XMLable;
import XML.basicTypes.XMLColor;
import XML.basicTypes.XMLFloat;

/*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * This class is to be completely redesigned. In particular, it will become
 * abstract and be extend by the BasicTexture and AdvancedTexture classes
 *!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */

public class Texture implements XMLable, Cloneable {
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
  
  public Texture(double refraction,float[] brillance,float[] reflectance,
      File image, float[] Kr, float[] Kt, int repeat, boolean adapter) throws IOException {
	  
	  this(refraction,brillance,reflectance,image,Kr,Kt);
	  this.repeat  = repeat;
	  this.adapter = adapter;	
  }

  public Texture(float indice, float brillance, Color reflectance, Color Ka, Color Kr, Color Kt) {
	  this(indice,new float[] { brillance,brillance,brillance },getRGB(reflectance),getRGB(Ka),getRGB(Kr),getRGB(Kt));
  }
  
  private static float[] getRGB(Color c) {
	  return new float[] { ((float) c.getRed())/255,((float) c.getGreen())/255,((float) c.getBlue())/255 };
  }

private boolean testParam(float[] param, float inf,
      float sup) {
    return !(param.length == 3 && intervalle(param[0],inf,sup) && 
	intervalle(param[1],inf,sup) && intervalle(param[2],inf,sup));
  }

  private boolean intervalle(float nb,float borneInf,float borneSup) {
    return (nb >= borneInf - epsilon && nb <= borneSup + epsilon);
  }
  
  private float[] recalage(float[] param, float inf, float sup) {
    return new float[] { param[0] <= inf ? inf : param[0] >= sup ? sup : param[0],
    					 param[1] <= inf ? inf : param[1] >= sup ? sup : param[1],
    					 param[2] <= inf ? inf : param[2] >= sup ? sup : param[2] };
  }

  public double refraction() {
    return this.refraction;
  }

  public float[] brillance() {
    return this.brillance;
  }

  public float[] reflectance() {
    return this.reflectance;
  }

  public float[] Ka() {
    return this.Ka;
  }
  
  public int getHeight() {
	  return texture != null ? adapter ? 2*texture.getHeight() : texture.getHeight() : 0;
  }
  
  public int getWidth() {
	  return texture != null ? adapter ? 2*texture.getWidth() : texture.getWidth() : 0;
  }
  
  public float[] Ka(int u, int v) {	
	  if (texture != null)  {
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

    public float[] Kr() {
    return this.Kr;
  }

  public float[] Kt() {
    return this.Kt;
  }

  public String toString() {
    return 
      "   Indice de refraction : " + refraction                                   + "\n" +
      "   Reflectance : "          + tripletToString(reflectance)                 + "\n" +
      "   Brillance : "            + tripletToString(brillance)                   + "\n" +
      "   Ka : "                   + (path == null ?  tripletToString(Ka) : path) + "\n" +
      "   Kr : "                   + tripletToString(Kr)                          + "\n" +
      "   Kt : "                   + tripletToString(Kt);
  }

  private String tripletToString(float[] triplet) {
    return
      "\tR : " + triplet[0] + 
      "\tV : " + triplet[1] + 
      "\tB : " + triplet[2];
  }
  	
	@Override
	public Element toXML() {	
		Element result = new Element("Texture");
		result.addContent(new XMLFloat("indice",refraction));
		result.addContent(new XMLFloat("brillance",brillance[0]));
		result.addContent(new XMLColor("reflectance",reflectance));
		Element elt;
		if (texture == null)
			elt = new XMLColor("Ka",Ka);
		else {
			elt = new Element("Color");
			elt.setAttribute(new Attribute("url",path));
		}
		result.addContent(elt);
		result.addContent(new XMLColor("Kr",Kr));
		result.addContent(new XMLColor("Kt",Kt));
		return result;
	}
	
	public Texture clone() {
		if (texture == null)
			return new Texture(refraction,brillance,reflectance,Ka,Kr,Kt);
		else 
			return new Texture(refraction,brillance,reflectance,texture,path,Kr,Kt,repeat,adapter);
	}
	
	public String getAdvancedKa() {
		return path;
	}

	public void setPatternRepeat(int n) {
		repeat = n;		
	}
	
	public int getPatternRepeat() {
		return repeat;		
	}
}

package scene;

import static objects.Texture.*;

import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import objects.Object3D;
import objects.TotalReflectionException;
import utils.math.Point;
import utils.math.Vector3D;
import static java.lang.Math.*;
import java.util.ArrayList;

public class Raytracer {
	public static final int		AMBIENT		= 0;
	public static final int		SPECULAR	= 1;
	public static final int		DIFFUSE		= 2;
	public static final int		REFRACTED	= 3;
	public static final int		REFLECTED	= 4;

	protected Scene				scene;
	protected Screen			screen;
	public int					lines, columns;
	protected List<Object3D>	objects;
	protected List<Painter>		painters	= new ArrayList<>();
	private List<Source>		sources;

	public Raytracer(Scene scene, Screen screen) {
	  	this.screen  = screen;
		this.scene   = scene;
        this.lines   = screen.getPixels().length;
		this.columns = screen.getPixels()[0].length;		
        this.objects = scene.getObjects().stream().
            filter(object -> object.isShown()).
            sorted((Object3D o1, Object3D o2) -> Integer.compare(o2.getPriority(),o1.getPriority())).
            collect(Collectors.toList());
        this.sources = scene.getSources().stream().
        	filter(source -> source.isShown()).
        	collect(Collectors.toList());
	}
    
    public void addPainter(Painter painter) {
        painters.add(painter);
    }
    
    public void removePainter(Painter painter) {
        painters.remove(painter);
    }
    
	protected Pair<Point,Object3D> nearestObject(Point pView, Vector3D ray) {	
		double minDist        = Double.MAX_VALUE;
        Point intersection    = null;
        Object3D resultObject = null;
        
		for (Object3D obj : objects) {
			Point point = obj.intersection(pView,ray);
			double dist =  point == null ? Double.MAX_VALUE : point.distance(pView);
			if (dist < minDist) {
				minDist      = dist;
				intersection = point;
				resultObject = obj;
            }
		}
		return new Pair<>(intersection,resultObject);
	}	

    protected boolean isEnlighted(Source source, Point p) {
		Vector3D ray = new Vector3D(p,source.pos);	
		return objects.stream().allMatch(obj -> {
            Point intersection = obj.intersection(p,ray);
            if (intersection == null)
                return true;
			double direction = ray.dot(new Vector3D(p,intersection));
            //If we find an intersection, there is still to verify that it is between the source and the point and not
            //behind the point. If this intersection is indeed an obstacle, the test will return directly.
			return !(p.sqrDistance(intersection) < p.sqrDistance(source.pos) && direction > 0);
        });
	}

    protected Color ambientComponent(Object3D obj, Point p) {		
		float[] rgb = colorToArr(scene.getAmbientColor());
		float[] Ka = obj.Ka(p);
		return new Color(Ka[0]*rgb[0],Ka[1]*rgb[1],Ka[2]*rgb[2],1);
	}
    
	protected Color diffuseComponent(Point p, Source source, Object3D obj) {
		float[] rgb     = colorToArr(source.color);
		Vector3D ray    = new Vector3D(p,source.pos);		
		Vector3D normal = obj.normal(p);
     
        /*For a plane surface, it is impossible to compute the normal with the correct sign in all the cases
         *(it depends on where is the source) so we use Math.abs to ensure the correct sign will be used.
		 */
		double cosinus  = abs(ray.dot(normal) / (normal.norm()*ray.norm()));		
		float[] Ka      = obj.Ka(p);									
		return new Color(rgb[0]*cosinus*Ka[0],rgb[1]*cosinus*Ka[1],rgb[2]*cosinus*Ka[2],1);
    }
	
	protected Color specularComponent(Point p, Source source, Object3D obj) {	
		Color I  = source.color;
		double R = I.getRed(), V = I.getGreen(), B = I.getBlue();
		
		Vector3D ray         = new Vector3D(p,source.pos);
		Vector3D incidentRay = new Vector3D(screen.pView,p);
		Vector3D medianRay   = ray.sum(incidentRay.opposed());
		Vector3D normal      = obj.normal(p);		
		double cosinus       = abs(normal.dot(medianRay) / (medianRay.norm()*normal.norm()));
        
		float[] reflectance  = obj.reflectance();
		float   brilliance   = obj.brilliance();
        double  d            = pow(cosinus,brilliance);
		
		double  Rspec        =  reflectance[0]*R*d;
		double  Vspec        =  reflectance[1]*V*d;
		double  Bspec        =  reflectance[2]*B*d;
		return new Color(Rspec,Vspec,Bspec,1);
	}

	protected Color pixelColor(Point p, Vector3D ray, int depth, int depthMax, double envRefractiveIndex, boolean[] displayComponents) {		
		Pair<Point,Object3D> pair         = nearestObject(p,ray);
        Point                intersection = pair.getKey();
        Object3D             obj          = pair.getValue();
		
		if (intersection == null || depth < 0 ) 
            return Color.BLACK;
        
        //First, we compute the ambient component
        Color ambientComp = displayComponents[AMBIENT] ? ambientComponent(obj,intersection) : Color.BLACK;
        
        //Then, we determine the specular and diffuse components
        Color diffSpecComp = Color.BLACK;
		for (Source source : sources) {
			if (isEnlighted(source,intersection)) {					
				Color diffuseComp  = displayComponents[DIFFUSE ] ? diffuseComponent (intersection,source,obj) : Color.BLACK;
				Color specularComp = displayComponents[SPECULAR] ? specularComponent(intersection,source,obj) : Color.BLACK;
				diffSpecComp       = Pixel.colorSynthesis(diffSpecComp,diffuseComp,specularComp);
			}			
		}
				
        //Finally, we compute the reflected and refracted components
		Color reflectedComp;		
		Color color;
		if ((obj.Kr()[0] <= 0.001 && obj.Kr()[1] <= 0.001 && obj.Kr()[2] <= 0.001) || !displayComponents[REFLECTED])
			color = Color.BLACK;
		else
			color = pixelColor(intersection,obj.reflectedRay(intersection,ray),depth-1,depth,envRefractiveIndex,displayComponents);
		reflectedComp = new Color(obj.Kr()[0]*color.getRed(),obj.Kr()[1]*color.getGreen(),obj.Kr()[2]*color.getBlue(),1);	
						
		Color refractedComp;	
		double nvRefraction = envRefractiveIndex != obj.refractiveIndex() ? obj.refractiveIndex() : scene.getRefractiveIndex();
		try {
			if ((obj.Kt()[0] <= 0.001 && obj.Kt()[1] <= 0.001 && obj.Kt()[2] <= 0.001) || !displayComponents[REFRACTED])
				color = Color.BLACK;
			else			
				color = pixelColor(intersection,obj.refractedRay(intersection,ray,envRefractiveIndex),depth-1,depthMax,nvRefraction,displayComponents);
			refractedComp = new Color(obj.Kt()[0]*color.getRed(),obj.Kt()[1]*color.getGreen(),obj.Kt()[2]*color.getBlue(),1);	
			
		} catch (TotalReflectionException e) {
			refractedComp = Color.BLACK;
		}			
		return Pixel.colorSynthesis(ambientComp,diffSpecComp,reflectedComp,refractedComp);
	}
	
	protected Color pixelColor(Point p, Vector3D ray, int depth, int depthMax, double envRefractiveIndex) {
		return pixelColor(p,ray,depth,depthMax,envRefractiveIndex,new boolean[] { true,true,true,true,true });
	}
	
	public void render(int depth) {
		render(depth,new boolean[] { true,true,true,true,true });
	}
	
	public void render(int depth, boolean[] displayComponents) {
		for (int i=0; i<lines ; i++)		
			for (int j=0; j<columns ; j++) {
				Vector3D ray = new Vector3D(screen.pView,screen.getPixels()[i][j].pos);
				Color color  = pixelColor(screen.pView,ray,depth,depth,scene.getRefractiveIndex(),displayComponents);
				screen.setPixelColor(i,j,color);
                for (Painter painter : painters)
                    try {
                        painter.paint(color,i,j);
                    } catch (Throwable t) { }
			} 
	}	
}
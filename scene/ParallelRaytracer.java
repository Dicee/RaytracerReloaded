package scene;

import javafx.scene.paint.Color;
import utils.math.Vector3D;

public class ParallelRaytracer extends Raytracer {
	private Thread[]			threads		= new Thread[4];

	public ParallelRaytracer(Scene scene, Screen screen) {
		super(scene,screen);
	}

	@Override
	public void render(int depth) {
		boolean[] comp = new boolean[] { true,true,true,true,true };
		int imean = lines/2, jmean = columns/2;
		(threads[0] = new Thread(() -> render(depth,comp,0    ,imean,0     ,jmean  ))).start();
		(threads[1] = new Thread(() -> render(depth,comp,0    ,imean,jmean,columns))).start();
		(threads[2] = new Thread(() -> render(depth,comp,imean,lines,0    ,jmean  ))).start();
		(threads[3] = new Thread(() -> render(depth,comp,imean,lines,jmean,columns))).start();
		for (Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	@Override
	public void render(int depth, boolean[] displayComponents) {
		render(depth,displayComponents,0,lines,0,columns);
	}
	
	private void render(int depth, boolean[] displayComponents, int imin, int imax, int jmin, int jmax) {
		for (int i=imin ; i<imax ; i++)		
			for (int j=jmin ; j<jmax ; j++) {
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
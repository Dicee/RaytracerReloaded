package utils.math;

public class Point {
	public double x, y, z;
	
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}		
	
	public Point(Point p) {
		this(p.x,p.y,p.z);
	}
	
	public Point() {
		this(0,0,0);
	}
	
	public void setLocation(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}		
	
	public void setLocation(Point p) {
		setLocation(p.x,p.y,p.z);
	}
	
	public double distance(Point p) {					
	  return (new Vector3D(p,this)).norm(); 
	}
	
	public double sqrDistance(Point p) {					
	  return (new Vector3D(p,this)).sqrNorm(); 
	}
	
	public Point translate(Vector3D v) {		
	  return new Point(x + v.x,y + v.y,z + v.z);
	}
	
	@Override
	public String toString() {		  
	  return String.format("Point(%f,%f,%f)",x,y,z);
	}
	
	public Point rotateX(double u) {
		double nvY, nvZ;
		nvY = y*Math.cos((u*Math.PI)/180) - z*Math.sin((u*Math.PI)/180);
		nvZ = y*Math.sin((u*Math.PI)/180) + z*Math.cos((u*Math.PI)/180);
		return new Point(x,nvY,nvZ);
	}

	public Point rotateY(double u) {
		double nvX, nvZ;
		nvX = x*Math.cos((u*Math.PI)/180) - z*Math.sin((u*Math.PI)/180);
		nvZ = x*Math.sin((u*Math.PI)/180) + z*Math.cos((u*Math.PI)/180);
		return new Point(nvX,y,nvZ);
	}

	public Point rotateZ(double u) {
		double nvX, nvY;
		nvX = x*Math.cos((u*Math.PI)/180) - y*Math.sin((u*Math.PI)/180);
		nvY = x*Math.sin((u*Math.PI)/180) + y*Math.cos((u*Math.PI)/180);
		return new Point(nvX,nvY,z);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return false;
		if (!(o instanceof Point))
			return false;
		Point other = (Point) o;
		return distance(other) <= Vector3D.epsilon;
	}
	
	public Point clone() {
		return new Point(x,y,z);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
}



//vector class

public class Vector2d {
	protected double x,y;
	
	Vector2d()
	{
		x=0;
		y=0;
	}
	
	Vector2d(double newX, double newY)
	{
		x=newX;
		y=newY;
	}
	
	public double getX(){return x;}
	public double getY(){return y;}
	public void setX(double newX){x=newX;}
	public void setY(double newY){y=newY;}
	public void incX(double i){x+=i;}
	public void incY(double i){y+=i;}
	public void multX(double m){x*=m;}
	public void multY(double m){y*=m;}
}

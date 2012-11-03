import java.awt.Color;
import java.awt.Graphics2D;


public class AreaPulse {
	int x,y;
	int radius;
	int range;
	
	AreaPulse(int newx,int newy,int newRange)
	{
		x=newx;
		y=newy;
		radius = 0;
		range = newRange;
	}
	
	public void setX(int newx){x = newx;}
	public void setY(int newy){y = newy;}
	
	public int getX(){return x;}
	public int getY(){return y;}
	public boolean isAlive(){return radius<range;}
	
	public void updateRadius()
	{
		radius+=(int)(range/20);
	}

	public void draw(Graphics2D g2d)
	{
		int alpha = (int) (255*(1.0-(double)radius/range));
		if (alpha>255) alpha = 255;
		g2d.setColor(new Color(255,0,0,alpha));
		g2d.fillOval(x-radius, y-radius, radius*2, radius*2);
	}
}
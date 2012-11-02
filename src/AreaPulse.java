import java.awt.Color;
import java.awt.Graphics2D;


public class AreaPulse {
	int x,y;
	int radius;
	int range;
	
	AreaPulse(int newx,int newy)
	{
		x=newx;
		y=newy;
		radius = 0;
		range = 50;
	}
	
	public void setX(int newx){x = newx;}
	public void setY(int newy){y = newy;}
	
	public int getX(){return x;}
	public int getY(){return y;}
	public boolean isAlive(){return radius<range;}
	
	public void updateRadius()
	{
		radius+=4;
	}

	public void draw(Graphics2D g2d)
	{
		g2d.setColor(new Color(0x80FF0000, true));
		g2d.fillOval(x-radius, y-radius, radius*2, radius*2);
	}
}
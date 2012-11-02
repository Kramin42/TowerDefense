import java.awt.Color;
import java.awt.Graphics2D;


public class Missile {
	Vector2d pos;
	Vector2d vel;
	int width,height;
	boolean alive;
	
	Missile()
	{
		pos = new Vector2d();
		vel = new Vector2d();
		width = 4;
		height = 10;
		alive = true;
	}
	
	public void setX(int x){pos.x = x-width/2;}
	public void setY(int y){pos.y = y-height/2;}
	public void setPos(Vector2d newPos){pos = newPos;}
	public void setVelX(double x){vel.x = x;}
	public void setVelY(double y){vel.y = y;}
	public void setVel(Vector2d newVel){vel = newVel;}
	
	public double getX(){return pos.x;}
	public double getY(){return pos.y;}
	public Vector2d getPos(){return pos;}
	public int getCenterX(){return (int) (pos.x+width/2);}
	public int getCenterY(){return (int) (pos.y+height/2);}
	public double getVelX(){return vel.x;}
	public double getVelY(){return vel.y;}
	public Vector2d getVel(){return vel;}
	public boolean isAlive(){return alive;}
	
	public void updatePos()
	{
		pos.incX(vel.x);
		pos.incY(vel.y);
	}
	
	public void kill()
	{
		alive = false;
	}
	
	public void draw(Graphics2D g2d)
	{
		double angle = Math.atan2(getVelX(), -getVelY());
		g2d.rotate(angle, getCenterX(), getCenterY());
		g2d.setColor(Color.ORANGE);
		g2d.fillOval((int)getX(), (int)getY(), 4, 10);
		g2d.fillRect((int)getX(), (int)getY()+5, 4, 5);
		g2d.setColor(Color.RED);
		g2d.fillRect((int)getX()+2, (int)getY()+10, 2, 2);
		g2d.drawLine(getCenterX(), getCenterY(), getCenterX(), getCenterY());
	}
}
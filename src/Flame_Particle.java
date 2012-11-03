import java.awt.Color;
import java.awt.Graphics2D;


public class Flame_Particle {
	Vector2d pos;
	Vector2d strtPos;
	Vector2d vel;
	int width,height;
	double dist;
	boolean alive;
	
	Flame_Particle()
	{
		pos = new Vector2d();
		strtPos = new Vector2d();
		vel = new Vector2d();
		dist = 0;
		width = 1;
		height = 1;
		alive = true;
	}
	
	public void setX(int x){pos.x = x; strtPos.x=x;}
	public void setY(int y){pos.y = y; strtPos.y=y;}
	public void setPos(Vector2d newPos){pos = newPos;}
	public void setVelX(double x){vel.x = x;}
	public void setVelY(double y){vel.y = y;}
	public void setVel(Vector2d newVel){vel = newVel;}
	
	public Vector2d getPos(){return pos;}
	public int getX(){return (int) (pos.x);}
	public int getY(){return (int) (pos.y);}
	public double getVelX(){return vel.x;}
	public double getVelY(){return vel.y;}
	public Vector2d getVel(){return vel;}
	public boolean isAlive(){return alive;}
	
	public void updatePos()
	{
		pos.incX(vel.x);
		pos.incY(vel.y);
		dist = pos.distanceTo(strtPos);
		if (dist>=60){
			kill();
		}
	}
	
	public void kill()
	{
		alive = false;
	}
	
	public void draw(Graphics2D g2d)
	{
		int w = (int) (width*dist/10);
		int h = (int) (height*dist/10);
		int alpha = (int)(255*(60-dist)/60);
		g2d.setColor(new Color(255,0,0,alpha));
		g2d.fillOval((int)getX()-w/2, (int)getY()-h/2, w, h);
	}
}
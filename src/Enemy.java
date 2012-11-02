import java.awt.Color;
import java.awt.Graphics2D;


public class Enemy {
	Vector2d pos;
	Vector2d vel;
	int width,height;
	boolean alive;
	int health;
	int maxHealth;
	
	Enemy()
	{
		pos = new Vector2d();
		vel = new Vector2d();
		width = 10;
		height = 10;
		alive = true;
		health = 10;
		maxHealth = 10;
	}
	
	public void setX(int x){pos.x = x;}
	public void setY(int y){pos.y = y;}
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
	
	public boolean damage(int amount)//returns true if it dies
	{
		health-=amount;
		if (health<=0){kill();return true;}
		else return false;
	}
	
	public void draw(Graphics2D g2d)
	{
		g2d.setColor(Color.YELLOW);
		g2d.fillOval((int)getX(), (int)getY(), width, height);
		g2d.setColor(Color.GREEN);
		g2d.drawLine((int)getX(), (int)getY()+height, (int)getX()+(width*health)/maxHealth-1, (int)getY()+height);
		if (health != maxHealth){
			g2d.setColor(Color.RED);
			g2d.drawLine((int)getX()+(width*health)/maxHealth, (int)getY()+height, (int)getX()+width, (int)getY()+height);
		}
		//g2d.fillOval((int)getX()+(width-(width*health)/10)/2, (int)getY()+(height-(height*health)/10)/2, (width*health)/10, (height*health)/10);
	}
}
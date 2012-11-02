import java.awt.Color;
import java.awt.Graphics2D;


public class Tower {
	protected Vector2d pos;
	protected Vector2d direction;
	protected int width,height;
	protected int fireCounter;
	protected int fireDelay;
	protected int range;
	protected int type;
	protected boolean canFire;
	protected boolean removable;
	protected Color color;
	
	Tower()
	{
		pos = new Vector2d();
		direction = new Vector2d(0,-10);
		width = 20;
		height = 20;
		fireCounter = 0;
		fireDelay = 50;
		range = 100;
		canFire = true;
		color = Color.BLUE;
		type = 0;
		removable = true;
	}
	
	public void setX(int x){pos.x = x;}
	public void setY(int y){pos.y = y;}
	public void setPos(Vector2d newPos){pos = newPos;}
	public void resetFireCounter(){fireCounter = 0;}
	public void incFireCounter(){fireCounter++;}
	public void setWidth(int w){width = w;}
	public void setHeight(int h){height = h;}
	public void setCanFire(boolean b){canFire = b;}
	public void setColor(Color c){color = c;}
	public void setRange(int r){range = r;}
	public void setType(int t){type = t;}
	
	public double getX(){return pos.x;}
	public double getY(){return pos.y;}
	public Vector2d getPos(){return pos;}
	public int getCenterX(){return (int) (pos.x+width/2);}
	public int getCenterY(){return (int) (pos.y+height/2);}
	public int getWidth(){return width;}
	public int getHeight(){return height;}
	public boolean getCanFire(){return canFire;}
	public int getRange(){return range;}
	public int getType(){return type;}
	
	public void draw(Graphics2D g2d)
	{
		g2d.setColor(color);
		//g2d.fillRect((int)getX(), (int)getY(), width, height);
		g2d.fillRoundRect((int)getX(), (int)getY(), width, height, width/2, height/2);
		if (canFire && type != 3){
			g2d.setColor(Color.GREEN);
			int[] xpoints = {getCenterX()-(int)(direction.y/2),getCenterX()+(int)direction.x,getCenterX()+(int)(direction.y/2)};
			int[] ypoints = {getCenterY()+(int)(direction.x/2),getCenterY()+(int)direction.y,getCenterY()-(int)(direction.x/2)};
			g2d.fillPolygon(xpoints, ypoints, 3);
		} else if (type == 3){
			g2d.setColor(Color.YELLOW);
			g2d.fillOval(getCenterX()-5, getCenterY()-5, 10, 10);
		}
	}

	public void drawRange(Graphics2D g2d)
	{
		if (canFire){
			g2d.setColor(new Color(0xFF008000));
			g2d.drawOval(getCenterX()-range, getCenterY()-range, range*2, range*2);
		}
	}
	
	public boolean updateFireCounter()//returns true if fireCounter resets
	{
		if (canFire)
			incFireCounter();
		if (fireCounter >= fireDelay)
		{
			resetFireCounter();
			return true;
		}
		else
		{
			return false;
		}
	}
}

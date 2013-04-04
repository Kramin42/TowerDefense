import java.applet.Applet;
import java.awt.Event;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.text.StyleContext.SmallAttributeSet;

public class game extends Applet implements Runnable, KeyListener, MouseListener, MouseMotionListener {
	
	boolean antialiasing=true;
	
	boolean drawRange = false;
	boolean drawVectorField = false;
	
	int mX=0,mY=0;
	boolean showMouse = true;
	boolean mouseB1 = false;
	
	double displayvar = 0;
	
	Random rand = new Random();

	//create the identity transform(0.0)
	AffineTransform identity = new AffineTransform();

	//decimal format
	DecimalFormat df = new DecimalFormat("#.###");
	
	//fonts
	Font normalFont = new Font("Ariel",Font.PLAIN,12);
	Font largeFont = new Font("Ariel",Font.PLAIN,60);
	Font mediumFont = new Font("Ariel",Font.PLAIN,30);
	Font hugeFont = new Font("Ariel", Font.PLAIN, 120);
	
	//constants
	int w = 800, h = 600;
	static int cellwidth = 10;
	static int PAW = 500;//play area width
	static int offsetX = 150;
	static int offsetY = 50;
	int money = 0;
	static final int startingMoney = 10000000;
	int refundDivider = 2;
	int minutes = 0,seconds = 0;
	int prevSeconds=0,prevMinutes=0;
	int numEscaped = 0;
	int numKilled = 0,prevNumKilled=0;
	boolean paused = false,gameOver=false,gameStarted=false;
	
	static final int startingEnemies = 2;
	static final int enemyStartingHealth = 5;
	static final int wavesPerEnemyNumInc = 2;
	static final int wavesPerEnemyHealthInc = 3;
	static final double EnemySpeedInc = 0.005;
	static final double startingEnemySpeed = 0.3;// TODO: change back to 0.05
	static final double maxEnemySpeed = 0.2;
	static final int enemyWaveDelay = 600; //in number of frames (at 60 fps)
	static final int moneyPerEnemy = 5;
	static final int towerCost = 20;
	static final int areaPulseCost = 200;
	static final int wallCost = 5;
	static final int missileTurretCost = 150;
	static final int towerAccuracy = 3;
	
	//button constants
	int btnW=120;
	int btnH=15;
	int btnSpc=20;
	int btnOffX=15;
	int btnOffY=37;
	int btnTextOffX=5;
	int btnTextOffY=12;

	//2d array that stores occupation booleans
	boolean[][] occupation = new boolean[50][50];
	
	//towers arraylist
	ArrayList<Tower> twrs = new ArrayList<Tower>();
	static final int towerFireDelay = 60;//80
	static final int missileFireDelay = 120;
	static final int areaPulseFireDelay = 100;
	static final int towerRange = 400;// TODO: change back to 100
	static final int missileRange = 200;
	static final int areaPulseRange = 50;
	
	//bullets arraylist
	ArrayList<Bullet> blts = new ArrayList<Bullet>();
	int BULLET_SPEED = 4;
	int BULLET_DAMAGE = 1;
	
	//missile arraylist
	ArrayList<Missile> msls = new ArrayList<Missile>();
	int MISSILE_SPEED = 3;
	int MISSILE_DAMAGE = 20;
	int MISSILE_ROTATION = 4;
	
	//area pulse arraylist
	ArrayList<AreaPulse> apls = new ArrayList<AreaPulse>();
	int PULSE_DAMAGE = 2;

	//enemies arraylist
	ArrayList<Enemy> nmys = new ArrayList<Enemy>();
	double enemySpeed = 0.04;
//	boolean createEnemies = false;
	int enemyCreationCounter = 0;
	int enemiesToBeCreated = 0;
	int enemyWave = 0;
	int enemyWaveCounter = 0;
	int enemyHealth = 0;
	String[] towerNames = {"Gun tower ("+towerCost+")","Wall ("+wallCost+")","Missile turret ("+missileTurretCost+")","Area pulser ("+areaPulseCost+")"};
	int selectedTower = 0;
	int numOfTowerTypes = 4;

	//pathfinding A* method
	ArrayList<Point> path = new ArrayList<Point>();
	ArrayList<PathNode> open = new ArrayList<PathNode>();
	ArrayList<PathNode> closed = new ArrayList<PathNode>();
	boolean pathValid = false;

	//pathfinding vector field method
	ArrayList<VectorNode> openv = new ArrayList<VectorNode>();
	ArrayList<VectorNode> closedv = new ArrayList<VectorNode>();
	Point[][] vectorField = new Point[50][50];

	boolean blocked = false;
	
	// Some variables to use for the fps.
	int tick = 0, fps = 0, acc = 0;
	long lastTime = System.nanoTime();
	
	public void start() {
		new Thread(this).start();
	}

	public void run() {
		setSize(w, h); // For AppletViewer, remove later.

		// Set up the graphics stuff, double-buffering.
		BufferedImage screen = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) screen.getGraphics();
		Graphics2D appletGraphics = (Graphics2D) getGraphics();
		
		BufferedImage cursor = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) cursor.getGraphics();
		g.setColor(Color.cyan);
		g.drawLine(0, 6, 12, 6);
		g.drawLine(6, 0, 6, 12);
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursor , new Point(6,6), ""));
		
		//initialise the listeners
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		newGame();

		// Game loop.
		while (true) {
			long now = System.nanoTime();
			acc += now - lastTime;
			tick++;
			if (acc >= 1000000000L) {
				acc -= 1000000000L;
				fps = tick;
				tick = 0;
				seconds++;
				if (seconds>=60){
					seconds-=60;
					minutes++;
				}
			}

			//game update
			if (!paused){
				gameUpdate();
			}
//			else {
//				if (gameOver){
//					if (mouseB1 && mX>=350 && mX<=450 && mY >=437 && mY<= 452){
//						newGame();
//						mouseB1=false;
//					}
//				}
//				if (!gameOver && !gameStarted){
//					if (mouseB1 && mX>=15 && mX<=135 && mY >=287 && mY<= 302){
//						startGame();
//					}
//				}
//			}

			lastTime = now;

			// Render
			if (antialiasing)
			{
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}
			else
			{
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			}

			updateGraphics(g2d);

			// Draw the entire results on the screen.
			appletGraphics.drawImage(screen, 0, 0, null);

			do {
				Thread.yield();
			} while (System.nanoTime() - lastTime < 16000000L);

			if (!isActive()) {
				return;
			}
		}
	}
	
	public void startGame()
	{
		paused = false;
		gameStarted = true;
		minutes = 0;
		seconds = 0;
		refundDivider = 2;
		enemyCreationCounter = 0;
		enemiesToBeCreated = 0;
		enemyWave = 0;
		enemyWaveCounter = 0;
		enemySpeed = startingEnemySpeed;
	}

	public void newGame()
	{
		gameOver = false;
		paused = true;
		twrs.clear();
		nmys.clear();
		blts.clear();
		msls.clear();
		openv.clear();
		closedv.clear();
		enemyHealth = enemyStartingHealth;
		money = startingMoney;
		refundDivider = 1;
		numEscaped = 0;
		numKilled = 0;
		gameStarted=false;
		for (int i=0;i<50;i++){
			for (int j=0;j<50;j++){
				occupation[i][j] = false;
				vectorField[i][j] = new Point(7, 7);
			}
		}

		for (int i=0;i<50;i++){
			createTower(i,0,1);
			createTower(i,49,1);
			if (i < 24 || i>26){
				createTower(0,i,1);
				createTower(49,i,1);
			}
		}
		for (int i=0;i<twrs.size();i++){
			twrs.get(i).removable = false;
		}
		calcVectorField();
	}

	//move and animate the objects in the game
	private void gameUpdate()
	{
		updateEnemyCreation();
		updateTowers();
		updateBullets();
		updateMissiles();
		updateAreaPulses();
		updateEnemies();
		checkCollisions();
		if (numEscaped>=20){
			paused = true;
			gameOver = true;
			prevMinutes = minutes;
			prevSeconds = seconds;
			prevNumKilled = numKilled;
		}
	}

	public void updateGraphics(Graphics2D g2d)
	{
		g2d.setTransform(identity);

		//draw the background of play area
		//g2d.drawImage(background, 0, 0, w-1, h-1, this);
		g2d.setColor(Color.BLACK);
		g2d.fillRect(offsetX, offsetY, PAW, PAW);
		g2d.setColor(Color.DARK_GRAY);
		for (int i = offsetX; i <= w-offsetX; i+=10){
			g2d.drawLine(i, offsetY, i, h-offsetY);
		}
		for (int i = offsetY; i <= h-offsetY; i+=10){
			g2d.drawLine(offsetX, i, w-offsetX, i);
		}

		if (drawVectorField){
			drawVectorField(g2d);
		}

		drawTowers(g2d);
		drawBullets(g2d);
		drawMissiles(g2d);
		drawAreaPulses(g2d);
		drawEnemies(g2d);
		
		drawMouse(g2d);
		
		//draw surrounding black area
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, offsetX, h);//left pane
		g2d.fillRect(offsetX+PAW+1, 0, offsetX, h);//right pane
		g2d.fillRect(offsetX, 0, PAW+1, offsetY);//top pane
		g2d.fillRect(offsetX, offsetY+PAW+1, PAW+1, offsetY);//bottom pane
		
		//draw the fps
		g2d.setColor(Color.white);
		g2d.drawString("Fps: "+fps, 0, 10);

		//draw tower options
		for (int i=0;i<numOfTowerTypes;i++)
		{
			g2d.setColor(Color.WHITE);
			g2d.drawString(towerNames[i], btnOffX+btnTextOffX, btnTextOffY+btnOffY+btnSpc*i);
			g2d.setColor(Color.GRAY);
			if (i == selectedTower){g2d.setColor(Color.YELLOW);}
			g2d.drawRect(btnOffX, btnOffY+btnSpc*i, btnW, btnH);
		}
		
		//draw the antialiasing button
		g2d.setColor(Color.WHITE);
		g2d.drawString("Anti-Aliasing", btnOffX+btnTextOffX, offsetY+PAW-btnH+btnTextOffY);
		g2d.setColor(Color.GRAY);
		if (antialiasing){g2d.setColor(Color.YELLOW);}
		g2d.drawRect(btnOffX, offsetY+PAW-btnH, btnW, btnH);
		
		//draw the pause button
		if (!gameOver && gameStarted)
		{
			g2d.setColor(Color.WHITE);
			String t = "pause";
			if (paused) t = "unpause";
			g2d.drawString(t, btnOffX+btnTextOffX, h/2 + btnTextOffY);
			g2d.setColor(Color.GRAY);
			if (paused){g2d.setColor(Color.YELLOW);}
			g2d.drawRect(btnOffX, h/2, btnW, btnH);
		}

		//draw other info
		g2d.setColor(Color.WHITE);
		//g2d.drawString("Blocked: "+blocked, 10, 200);
		g2d.drawString("Money: "+money, offsetX+PAW+20, 50);
		g2d.drawString("Time: "+minutes+":"+seconds, offsetX+PAW+20, 80);
		g2d.drawString("Escaped: "+numEscaped, offsetX+PAW+20, 110);
		g2d.drawString("Killed: "+numKilled, offsetX+PAW+20, 130);
		g2d.drawString("Wave: "+enemyWave, offsetX+PAW+20, 150);

		if (paused){
			if (gameOver){
				g2d.setColor(Color.BLACK);
				g2d.fillRect(200, 100, 400, 400);
				g2d.setColor(Color.WHITE);
				g2d.setFont(largeFont);
				g2d.drawString("Game Over", 250, 300);
				g2d.setFont(mediumFont);
				g2d.drawString("Time Taken: "+prevMinutes+":"+prevSeconds, 300, 350);
				g2d.drawString("Enemies Killed: "+prevNumKilled, 300, 400);
				g2d.setFont(normalFont);
				g2d.drawRect(350, 437, 100, 15);
				g2d.drawString("New Game", 370, 450);
			}
			if (!gameOver && !gameStarted){
				g2d.drawRect(btnOffX, h/2 - btnSpc, btnW, btnH);
				g2d.drawString("Start Game", btnOffX+btnTextOffX, h/2 - btnSpc + btnTextOffY);
			}
			g2d.drawString("Paused", offsetX+PAW+20, 180);
		}
		
//		g2d.setColor(Color.WHITE);
//		g2d.drawString("min angle: "+displayvar, 700, 500);
	}

	public void updateTowers()
	{
		for (int i=0;i<twrs.size();i++){
			if (twrs.get(i).updateFireCounter())
			{
				if (twrs.get(i).getType()!=3){
					double dist;
					double mindist = twrs.get(i).range;
					double x=0,y=0;
					int index=0;
					boolean found = false;
					for (int n = 0; n<nmys.size(); n++)
					{
						x = nmys.get(n).getCenterX() - twrs.get(i).getCenterX();
						y = nmys.get(n).getCenterY() - twrs.get(i).getCenterY();
						dist = Math.sqrt(x*x+y*y);
						if (dist < mindist){
							found = true;
							mindist = dist;
							index = n;
						}
					}
					if (found){
						Enemy nmy = nmys.get(index);
						dist = mindist;
						if (twrs.get(i).type==0){
							//use a few iterations to accurately predict the future position of the enemy
//							for (int k=0;k<towerAccuracy;k++){
//								x = nmy.getCenterX() + nmy.getVelX()*(mindist/BULLET_SPEED) - twrs.get(i).getCenterX();
//								y = nmy.getCenterY() + nmy.getVelY()*(mindist/BULLET_SPEED) - twrs.get(i).getCenterY();
//								mindist = Math.sqrt(x*x+y*y);
//							}
//							Bullet blt = new Bullet();
//							blt.setX(twrs.get(i).getCenterX());
//							blt.setY(twrs.get(i).getCenterY());
//							x/=mindist;
//							y/=mindist;
//							blt.setVelX(x*BULLET_SPEED);
//							blt.setVelY(y*BULLET_SPEED);
//							twrs.get(i).direction.x = x*10;
//							twrs.get(i).direction.y = y*10;
							//better, exact method
							//System.out.println("enemy: pos: "+nmy.getCenterX()+","+nmy.getCenterY()+" vel: "+nmy.getVelX()+","+nmy.getVelY());
							//System.out.println("tower: pos: "+twrs.get(i).getCenterX()+","+twrs.get(i).getCenterY());
							double a = nmy.getVelX()*nmy.getVelX() + nmy.getVelY()*nmy.getVelY() - BULLET_SPEED*BULLET_SPEED;
							double b = 2*nmy.getVelX()*(nmy.getCenterX()-twrs.get(i).getCenterX()) + 2*nmy.getVelY()*(nmy.getCenterY()-twrs.get(i).getCenterY());
							double c = dist*dist;
							//System.out.println("a = "+a+", b = "+b+", c = "+c);
							//System.out.println("b*b = "+b*b+", 4*a*c = "+4*a*c);
							
							double disc = b*b - 4*a*c;
							//System.out.println("disc = "+disc);
							if (disc >= 0){
								double q = (b + Math.signum(b)*Math.sqrt(disc))/-2.0;
								double t1 = q/a;
								double t2 = c/q;
								//System.out.println("t1: "+t1+", t2: "+t2);
								double t = 0;
								if (t1>=0 && t2>=0)
									t = Math.min(t1, t2);
								else if (t1>=0)
									t = t1;
								else if (t2>=0)
									t = t2;
								else
									continue;
								Bullet blt = new Bullet();
								//t+=50;
								blt.setX(twrs.get(i).getCenterX());
								blt.setY(twrs.get(i).getCenterY());
								x = nmy.getVelX() + (nmy.getCenterX() - blt.getCenterX())/t;
								y = nmy.getVelY() + (nmy.getCenterY() - blt.getCenterY())/t;
								//System.out.println("bullet vel x = "+x+", y = "+y);
								//System.out.println("actual, theoretical bullet speed: "+Math.sqrt(x*x+y*y)+", "+BULLET_SPEED);
								blt.setVelX(x);
								blt.setVelY(y);
								blts.add(blt);
								twrs.get(i).direction.x = x*10/BULLET_SPEED;
								twrs.get(i).direction.y = y*10/BULLET_SPEED;
							}
						}
						else {//if (msls.size()==0){
							Missile msl = new Missile();
							msl.setX(twrs.get(i).getCenterX());
							msl.setY(twrs.get(i).getCenterY());
							x = nmy.getCenterX() - twrs.get(i).getCenterX();
							y = nmy.getCenterY() - twrs.get(i).getCenterY();
							mindist = Math.sqrt(x*x+y*y);
							x/=mindist;
							y/=mindist;
							msl.setVelX(x*MISSILE_SPEED);
							msl.setVelY(y*MISSILE_SPEED);
							msls.add(msl);
							twrs.get(i).direction.x = x*10;
							twrs.get(i).direction.y = y*10;
						}
					}
					else
						twrs.get(i).fireCounter=1000;
				}
				else {
					boolean found = false;
					for (int n = 0; n<nmys.size(); n++)
					{
						double x = nmys.get(n).getCenterX() - twrs.get(i).getCenterX();
						double y = nmys.get(n).getCenterY() - twrs.get(i).getCenterY();
						if (x*x+y*y < twrs.get(i).range*twrs.get(i).range){
							found = true;
							if (nmys.get(n).damage(PULSE_DAMAGE)){
								money+=10;
								numKilled++;
							}
						}
					}
					if (found){
						apls.add(new AreaPulse(twrs.get(i).getCenterX(),twrs.get(i).getCenterY(),areaPulseRange));
					} else {
						twrs.get(i).fireCounter=1000;
					}
				}
			}
		}
	}

	public void updateBullets()
	{
		for (Bullet blt : blts){
			blt.updatePos();
			if (blt.getX()>offsetX+PAW || blt.getX()<offsetX || blt.getY()>offsetY+PAW || blt.getY()<offsetY)
				blt.kill();
		}
		for (int n = 0; n<blts.size(); n++)
		{
			if (!blts.get(n).isAlive()){
				blts.remove(n);
				n--;
			}
		}
	}
	
	public void updateAreaPulses()
	{
		for (int n = 0; n<apls.size(); n++){
			apls.get(n).updateRadius();
			if (!apls.get(n).isAlive()){
				apls.remove(n);
				n--;
			}
		}
	}
	
	public void updateMissiles()
	{
		for (int i=0;i<msls.size();i++){
			//fancy algorithm to make it work
			int mslFaceAngle = (int)Math.toDegrees(Math.atan2(msls.get(i).getVelY(), msls.get(i).getVelX()));
			//System.out.println(mslFaceAngle);
			//int mslFaceAngle = (int) arctan(msls.get(i).getVelX(), msls.get(i).getVelY());
			double xdist;
			double ydist;
			double anglediff;
			double minanglediff = 400;
			boolean found = false;
			for (int j=0;j<nmys.size();j++){
				xdist = nmys.get(j).getCenterX()-msls.get(i).getCenterX();
				ydist = nmys.get(j).getCenterY()-msls.get(i).getCenterY();
				//System.out.println(Math.toDegrees(Math.atan2(ydist,xdist)));
				anglediff = -(Math.toDegrees(Math.atan2(ydist,xdist))) + mslFaceAngle;
				if (anglediff>180){anglediff-=360;}
				if (anglediff<-180){anglediff+=360;}
				if (anglediff*anglediff < minanglediff*minanglediff){
					minanglediff = anglediff;
					found = true;
				}
			}

			if (found){
				//System.out.println(minanglediff);
				//displayvar = minanglediff;
				if (minanglediff<0){
					mslFaceAngle+=MISSILE_ROTATION;
				} else {
					mslFaceAngle-=MISSILE_ROTATION;
				}
			}

			if (mslFaceAngle<0){mslFaceAngle+=360;}
			if (mslFaceAngle>360){mslFaceAngle-=360;}

			msls.get(i).setVelX(calcAngleMoveX(mslFaceAngle)*MISSILE_SPEED);
			msls.get(i).setVelY(calcAngleMoveY(mslFaceAngle)*MISSILE_SPEED);
			
			msls.get(i).updatePos();
			if (msls.get(i).getX()>offsetX+PAW || msls.get(i).getX()<offsetX || msls.get(i).getY()>offsetY+PAW || msls.get(i).getY()<offsetY)
				msls.get(i).kill();
		}
		for (int i = 0; i<msls.size(); i++)
		{
			if (!msls.get(i).isAlive()){
				msls.remove(i);
				i--;
			}
		}
	}

	public void updateEnemies()
	{
		for (int n = 0; n<nmys.size(); n++)
		{
			int i = (nmys.get(n).getCenterX()-offsetX)/cellwidth;
			int j = (nmys.get(n).getCenterY()-offsetY)/cellwidth;
			int cellx = i*cellwidth+offsetX;
			int celly = j*cellwidth+offsetY;
			if (i >= 50){
				nmys.get(n).kill();
				numEscaped++;
				continue;
			}
			//only change its velocity if it is near the center of the cell to prevent cutting corners
//			if (nmys.get(n).getCenterX()>cellx && nmys.get(n).getCenterX()<cellx+cellwidth && nmys.get(n).getCenterY()>celly && nmys.get(n).getCenterY()<celly+cellwidth){
//				nmys.get(n).vel.x=ENEMY_SPEED*vectorField[i][j].x;
//				nmys.get(n).vel.y=ENEMY_SPEED*vectorField[i][j].y;
//			}
			if (vectorField[i][j].x == 0){
				if (nmys.get(n).getCenterX()>cellx+3 && nmys.get(n).getCenterX()<cellx+cellwidth-3 && nmys.get(n).getCenterY()>celly && nmys.get(n).getCenterY()<celly+cellwidth){
					nmys.get(n).vel.x=enemySpeed*vectorField[i][j].x;
					nmys.get(n).vel.y=enemySpeed*vectorField[i][j].y;
				}
			}
			else if (vectorField[i][j].y == 0){
				if (nmys.get(n).getCenterX()>cellx && nmys.get(n).getCenterX()<cellx+cellwidth && nmys.get(n).getCenterY()>celly+3 && nmys.get(n).getCenterY()<celly+cellwidth-3){
					nmys.get(n).vel.x=enemySpeed*vectorField[i][j].x;
					nmys.get(n).vel.y=enemySpeed*vectorField[i][j].y;
				}
			}
			else {
				if (nmys.get(n).getCenterX()>cellx+1 && nmys.get(n).getCenterX()<cellx+cellwidth-1 && nmys.get(n).getCenterY()>celly+1 && nmys.get(n).getCenterY()<celly+cellwidth-1){
					nmys.get(n).vel.x=enemySpeed*vectorField[i][j].x;
					nmys.get(n).vel.y=enemySpeed*vectorField[i][j].y;
				}
			}
			nmys.get(n).updatePos();
		}
		for (int n = 0; n<nmys.size(); n++)
		{
			if (!nmys.get(n).isAlive()){
				nmys.remove(n);
				n--;
			}
		}
	}
	
	public void drawTowers(Graphics2D g2d)
	{
		for (int n = 0; n<twrs.size(); n++)
		{
			twrs.get(n).draw(g2d);
		}
		if (drawRange){
			for (int n = 0; n<twrs.size(); n++)
			{
				twrs.get(n).drawRange(g2d);
			}
		}
	}

	public void drawBullets(Graphics2D g2d)
	{
		for (int n = 0; n<blts.size(); n++)
		{
			blts.get(n).draw(g2d);
		}
	}
	
	public void drawMissiles(Graphics2D g2d)
	{
		for (int n = 0; n<msls.size(); n++)
		{
			g2d.setTransform(identity);
			msls.get(n).draw(g2d);
		}
		g2d.setTransform(identity);
	}
	
	public void drawAreaPulses(Graphics2D g2d)
	{
		for (int n = 0; n<apls.size(); n++)
		{
			apls.get(n).draw(g2d);
		}
	}

	public void drawEnemies(Graphics2D g2d)
	{
		for (int n = 0; n<nmys.size(); n++)
		{
			nmys.get(n).draw(g2d);
		}
	}

	public void drawMouse(Graphics2D g2d)
	{
		//draw mouse
		if (mX>offsetX && mX<w-offsetX-cellwidth && mY>offsetY && mY<h-offsetY-cellwidth && !gameOver)
		{
			g2d.setColor(Color.CYAN);
			switch (selectedTower){
			case 0:
				g2d.drawRect(mX-mX%cellwidth, mY-mY%cellwidth, cellwidth*2, cellwidth*2);
				g2d.drawOval(mX-mX%cellwidth+cellwidth-towerRange, mY-mY%cellwidth+cellwidth-towerRange, 2*towerRange, 2*towerRange);
				break;
			case 1:
				g2d.drawRect(mX-mX%cellwidth, mY-mY%cellwidth, cellwidth, cellwidth);
				break;
			case 2:
				g2d.drawRect(mX-mX%cellwidth, mY-mY%cellwidth, cellwidth*2, cellwidth*2);
				g2d.drawOval(mX-mX%cellwidth+cellwidth-missileRange, mY-mY%cellwidth+cellwidth-missileRange, 2*missileRange, 2*missileRange);
				break;
			case 3:
				g2d.drawRect(mX-mX%cellwidth, mY-mY%cellwidth, cellwidth*2, cellwidth*2);
				g2d.drawOval(mX-mX%cellwidth+cellwidth-areaPulseRange, mY-mY%cellwidth+cellwidth-areaPulseRange, 2*areaPulseRange, 2*areaPulseRange);
				break;
			}
//			if (selectedTower!=1){
//				g2d.drawRect(mX-mX%cellwidth, mY-mY%cellwidth, cellwidth*2, cellwidth*2);
//				if (selectedTower != 3)
//					g2d.drawOval(mX-mX%cellwidth+cellwidth-100, mY-mY%cellwidth+cellwidth-100, 200, 200);
//				else
//					g2d.drawOval(mX-mX%cellwidth+cellwidth-50, mY-mY%cellwidth+cellwidth-50, 100, 100);
//			}
//			else
//				g2d.drawRect(mX-mX%cellwidth, mY-mY%cellwidth, cellwidth, cellwidth);
		}
		//g2d.setColor(Color.CYAN);
		//g2d.drawLine(mX-5, mY, mX+5, mY);
		//g2d.drawLine(mX, mY-5, mX, mY+5);
	}

	public void checkCollisions()
	{
		for (int j=0;j<nmys.size();j++){
			if (nmys.get(j).isAlive()){
				Rectangle r = new Rectangle();
				r.x = (int) nmys.get(j).getX();
				r.y = (int) nmys.get(j).getY();
				r.width = nmys.get(j).width;
				r.height = nmys.get(j).height;
				for (int i=0;i<blts.size();i++){
					if (r.contains(blts.get(i).getCenterX(),blts.get(i).getCenterY())){
						blts.get(i).kill();
						if (nmys.get(j).damage(BULLET_DAMAGE)){
							money+=moneyPerEnemy;
							numKilled++;
						}
						break;
					}
				}
				if (!nmys.get(j).isAlive()){continue;}
				for (int i=0;i<msls.size();i++){
					if (r.contains(msls.get(i).getCenterX(),msls.get(i).getCenterY())){
						msls.get(i).kill();
						if (nmys.get(j).damage(MISSILE_DAMAGE)){
							money+=moneyPerEnemy;
							numKilled++;
						}
						break;
					}
				}
			}
		}
	}
	
	public void updateEnemyCreation()
	{
		if (gameStarted)
		{
			if (enemiesToBeCreated == 0 && ++enemyWaveCounter>enemyWaveDelay)//10 seconds @ 60 Hz
			{
				enemyWaveCounter = 0;
				enemyWave++;
				enemiesToBeCreated+=startingEnemies+enemyWave/wavesPerEnemyNumInc;
				if (enemySpeed<maxEnemySpeed) enemySpeed+=EnemySpeedInc;
			}
			if (enemiesToBeCreated>0 && ++enemyCreationCounter>30/(enemySpeed/startingEnemySpeed))
			{
				enemyCreationCounter = 0;
				enemiesToBeCreated--;
				createEnemy();
			}
		}
	}

	public void createEnemy()
	{
		Enemy nmy = new Enemy();
		nmy.setX(offsetX);
		nmy.setY(offsetY+25*cellwidth);
		nmy.maxHealth = nmy.health = enemyStartingHealth + enemyWave/wavesPerEnemyHealthInc;
		nmys.add(nmy);
	}

	public int createTower(int i, int j, int type)
	{
		if (type != 1){
			if (!(occupation[i][j] || occupation[i][j+1] || occupation[i+1][j] || occupation[i+1][j+1]))
			{
				occupation[i][j]=true;
				occupation[i][j+1]=true;
				occupation[i+1][j]=true;
				occupation[i+1][j+1]=true;
				Tower twr = new Tower();
				twr.setRange(towerRange);
				twr.setX(i*cellwidth + offsetX);
				twr.setY(j*cellwidth + offsetY);
				twr.fireDelay = towerFireDelay;
				if (type == 2){
					twr.setRange(missileRange);
					twr.fireDelay = missileFireDelay;
					twr.type = 2;
					twr.color = new Color(128, 64, 64);
					twrs.add(twr);
					return missileTurretCost;//cost of missile tower
				} else if (type == 3){
					twr.setRange(areaPulseRange);
					twr.fireDelay = areaPulseFireDelay;
					twr.type = 3;
					twr.color = new Color(64, 192, 64);
					twrs.add(twr);
					return areaPulseCost;//cost of missile tower
				} else if (type == 4){//TODO add flame turret code
					
				}
				twrs.add(twr);
				return towerCost;//cost of normal tower
			}
		}
		else {
			if (!occupation[i][j])
			{
				Tower twr = new Tower();
				twr.setX(i*cellwidth + offsetX);
				twr.setY(j*cellwidth + offsetY);
				twr.setWidth(cellwidth);
				twr.setHeight(cellwidth);
				twr.setCanFire(false);
				twr.setColor(new Color(128,96,96));
				twr.setRange(0);
				twr.setType(1);
				twrs.add(twr);
				occupation[i][j]=true;
				return wallCost;//cost
			}
		}
		return 0;
	}

	public int deleteTower(int i, int j)
	{
		int x = offsetX+i*cellwidth;
		int y = offsetY+j*cellwidth;
		for (int n=0;n<twrs.size();n++){
			if (twrs.get(n).pos.x == x && twrs.get(n).pos.y == y){
				if (twrs.get(n).removable == true){
					if (twrs.get(n).getType()!=1){
						occupation[i][j]=false;
						occupation[i][j+1]=false;
						occupation[i+1][j]=false;
						occupation[i+1][j+1]=false;
						if (twrs.get(n).getType()==0){
							twrs.remove(n);
							return towerCost/refundDivider;//refund
						} else if (twrs.get(n).getType()==2){
							twrs.remove(n);
							return missileTurretCost/refundDivider;//refund
						} else if (twrs.get(n).getType()==3){
							twrs.remove(n);
							return areaPulseCost/refundDivider;//refund
						}
					}
					else {
						occupation[i][j]=false;
						twrs.remove(n);
						return wallCost/refundDivider;//refund
					}
				}
				else
				{
					return 0;
				}
			}
		}
		return 0;//no refund
	}

	public void drawVectorField(Graphics2D g2d)
	{
		g2d.setColor(Color.RED);
		for (int i=0;i<50;i++){
			for (int j=0;j<50;j++){
				int x1 = cellwidth*i+offsetX+cellwidth/2;
				int y1 = cellwidth*j+offsetY+cellwidth/2;
				int x2 = x1 + vectorField[i][j].x/2;
				int y2 = y1 + vectorField[i][j].y/2;
				g2d.drawLine(x1, y1, x2, y2);
			}
		}
	}

	public boolean calcVectorField()
	{
		boolean pathBlocked = true;
		int F,G,H;
		VectorNode start = new VectorNode(49, 25,0,0,0,10,0);
		VectorNode end = new VectorNode(0, 25,0,0,0,0,0);
		VectorNode p = start;//current pathnode

		openv = new ArrayList<VectorNode>();
		closedv = new ArrayList<VectorNode>();
		boolean[][] closedArray = new boolean[50][50];
		boolean[][] openArray = new boolean[50][50];
		for (int i=0;i<50;i++){
			for (int j=0;j<50;j++){
				closedArray[i][j] = false;
				openArray[i][j] = false;
			}
		}

		openv.add(start);
		openArray[start.x][start.y] = true;

		while (!openv.isEmpty())
		{
			int index = 0;
			p = openv.get(index);
			openv.remove(index);
			closedv.add(p);
			closedArray[p.x][p.y] = true;
			openArray[p.x][p.y]= false;
			if (p.x == end.x && p.y == end.y){
				pathBlocked = false;
			}

			for (int i=-1;i<=1;i++){
				for (int j=-1;j<=1;j++){
					if (i == 0 && j == 0){continue;}
					int x = p.x + i;
					int y = p.y + j;

					if (x<0 || x>49 || y<0 || y>49){continue;}

					if (occupation[x][y]){continue;}//checks if the square is occupied
					
					if (occupation[p.x][y]){continue;}//prevent diagonal movement past towers
					if (occupation[x][p.y]){continue;}

					if (closedArray[x][y]){continue;}

					if (openArray[x][y]){
						for (int n=0;n<openv.size();n++){       //check if it is in the open list
							if (openv.get(n).x == x && openv.get(n).y == y){
								index = n;
								break;
							}
						}
						G = p.G;
						if (i==0 || j==0){//not diagonal
							G += 10;
						}
						else//diagonal
						{
							G += 14;
						}

						if (openv.get(index).G > G){//new path is better
							VectorNode temp = openv.get(index);
							openv.remove(index);
							temp.F = G + temp.H;
							int vX=0,vY=0;
							if (p.x == x){
								vY = 10*(p.y-y);
							}
							else if (p.y == y){
								vX = 10*(p.x-x);
							}
							else {
								vX = 7*(p.x-x);
								vY = 7*(p.y-y);
							}
							temp.vX = vX;
							temp.vY = vY;
							boolean added = false;
							for (int n=index-1;n>=0;n--){
								if (openv.get(n).F > temp.F){
									openv.add(n, temp);
									added = true;
									break;
								}
							}
							if (!added){
								openv.add(index, temp);
							}
						}
					}
					else {
						G = p.G;
						if (i==0 || j==0){//not diagonal
							G += 10;
						}
						else//diagonal
						{
							G += 14;
						}
						H = 0;//10*(Math.abs(end.x - x) + Math.abs(end.y - y));//calculate heuristic
						F = G + H;
						int vX=0,vY=0;
						if (p.x == x){
							vY = 10*(p.y-y);
						}
						else if (p.y == y){
							vX = 10*(p.x-x);
						}
						else {
							vX = 7*(p.x-x);
							vY = 7*(p.y-y);
						}
						boolean added = false;
						for (int n=0;n<openv.size();n++){
							if (openv.get(n).F > F){
								openv.add(n, new VectorNode(x,y,F,G,H,vX,vY));
								added =  true;
								break;
							}
						}
						if (!added){//add onto the end
							openv.add(new VectorNode(x,y,F,G,H,vX,vY));
						}
						openArray[x][y] = true;
					}
				}
			}
		}

		for (int i=0;i<closedv.size();i++){
			vectorField[closedv.get(i).x][closedv.get(i).y] = new Point(closedv.get(i).vX,closedv.get(i).vY);
		}
		return pathBlocked;
	}

	public void keyTyped(KeyEvent k){}
	public void keyPressed(KeyEvent k)
	{
		switch (k.getKeyCode())
		{
		//				case KeyEvent.VK_P:
		//					pathValid = findPath();
		//					//findPath = true;
		//					break;
		case KeyEvent.VK_V:
			blocked = calcVectorField();
			drawVectorField = !drawVectorField;
			break;
		case KeyEvent.VK_R:
			drawRange = !drawRange;
			break;
		case KeyEvent.VK_P:
		case KeyEvent.VK_PAUSE:
			if (!gameOver && gameStarted){
				paused = !paused;
			}
			break;
		}
	}
	public void keyReleased(KeyEvent k)
	{
		switch (k.getKeyCode())
		{
		case KeyEvent.VK_E:
			enemyWaveCounter = enemyWaveDelay;
			break;
		}
	}

	public void mouseMoved(MouseEvent m) 
	{
		mX = m.getX();
		mY = m.getY();
	}

	public void mouseDragged(MouseEvent m) 
	{
		mX = m.getX();
		mY = m.getY();
		if (mouseB1)
		{
			mX = m.getX();
			mY = m.getY();
			handleMousePress();
		}
	}

	public void mousePressed(MouseEvent m) 
	{
		mX = m.getX();
		mY = m.getY();
		if (m.getButton() == MouseEvent.BUTTON1)
		{
			if (mX>btnOffX && mX<btnOffX+btnW){
				if (mY>offsetY+PAW-btnH && mY<offsetY+PAW)//antiAiliasing button g2d.drawRect(btnOffX, offsetY+PAW-btnH, btnW, btnH);
				{
					antialiasing=!antialiasing;
					//mouseB1=false;
				}
				if (!gameOver) {
					if (mY > h/2 && mY < h/2+btnH && !gameOver && gameStarted) {//pause button g2d.drawRect(btnOffX, h/2, btnW, btnH);
						paused = !paused;
						//mouseB1 = false;
					}
				}
				if (!gameOver && !gameStarted){
					if (mY >h/2-btnSpc && mY< h/2+btnH-btnSpc){
						startGame();
					}
				}
			}
			if (gameOver){
				if (mX>=350 && mX<=450 && mY >=437 && mY<= 452){
					newGame();
					//mouseB1=false;
				}
			}
			handleMousePress();
			mouseB1 = true;
		}
		else if (m.getButton() == MouseEvent.BUTTON3)
		{
			int i = (mX-offsetX)/cellwidth;
			int j = (mY-offsetY)/cellwidth;
			money += deleteTower(i,j);
			blocked = calcVectorField();
		}
	}

	public void handleMousePress()
	{
		if (!gameOver){
			if (mX>offsetX && mX<w-offsetX-cellwidth && mY>offsetY && mY<h-offsetY-cellwidth)
			{
				int prevMoney = money;
				int i = (mX-offsetX)/cellwidth;
				int j = (mY-offsetY)/cellwidth;
				if (i==0 || i==49 || (i==48 &&selectedTower==0)){return;}//dont allow creation of towers in the gaps along the edge
				money -= createTower(i,j,selectedTower);
				blocked = calcVectorField();
				if (blocked || money<0){
					deleteTower(i, j);//destroy the tower that caused it to be blocked
					blocked = calcVectorField();
					money = prevMoney;//refund the player
				}
			}
			else
			{
				if (mX>btnOffX && mX<btnOffX+btnW){
					for (int i=0;i<numOfTowerTypes;i++)
					{
						if (mY>btnOffY+btnSpc*i && mY<btnOffY+btnH+btnSpc*i)
						{
							selectedTower = i;
						}
					}
				}
			}
		}
	}

	public void mouseReleased(MouseEvent m) 
	{
		if (m.getButton() == MouseEvent.BUTTON1)
		{
			mouseB1 = false;
		}
	}

	public void mouseClicked(MouseEvent m) {}
	public void mouseEntered(MouseEvent m) {}
	public void mouseExited(MouseEvent m) {}
	
	//calculate x movement value based on direction angle
	public double calcAngleMoveX(double angle)
	{
		return (double)(Math.cos(angle*Math.PI/180));
	}

	//calculate y movement value based on direction angle
	public double calcAngleMoveY(double angle)
	{
		return (double)(Math.sin(angle*Math.PI/180));
	}

	public double arctan(double x, double y){//returns the angle in degrees
		double angle = Math.toDegrees(Math.atan2(y, x))-180;
		if (angle>=360){angle-=360;}
		if (angle<0){angle+=360;}
		return angle;
	}
	
}
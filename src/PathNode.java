
public class PathNode {
	protected int x,y,F,G,H,pX,pY;//pX and pY are the parent coordinates
	//protected boolean open,closed;
	
	PathNode()
	{
		x=y=F=G=H=pX=pY=0;
		//open=closed=false;
	}
	
	PathNode(int newX, int newY, int newF, int newG, int newH, int newPX, int newPY)
	{
		x = newX;
		y = newY;
		F = newF;
		G = newG;
		H = newH;
		pX = newPX;
		pY = newPY;
		//open=closed=false;
	}
}

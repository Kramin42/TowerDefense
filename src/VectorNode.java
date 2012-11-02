
public class VectorNode {
	protected int x,y,F,G,H;
	protected int vX,vY;//vX and vY are the vector
	
	VectorNode()
	{
		x=y=F=G=H=0;
		vX=vY=0;
	}
	
	VectorNode(int newX, int newY, int newF, int newG, int newH, int newVX, int newVY)
	{
		x = newX;
		y = newY;
		F = newF;
		G = newG;
		H = newH;
		vX = newVX;
		vY = newVY;
	}
}

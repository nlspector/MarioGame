package objects.blocks;

import main.MainWindow;
import objects.Object;
import processing.core.PImage;
import processing.core.PVector;

public class SolidBlock extends Object{
	
	PImage sprite;
	MainWindow mw;
	
	public static SolidBlock[][] collisionMask=new SolidBlock[100][20];
	
	public static void initCollisionMask(int x, int y) {
		collisionMask=new SolidBlock[x][y];
	}
	
	public SolidBlock(PImage sprite, PVector pos, MainWindow mw) {
		super(10, pos, new PVector(0,0), new PVector(0,0));
		this.mw=mw;
		this.sprite=sprite;
		collisionMask[(int) pos.x][(int) pos.y]=this;
		// TODO Auto-generated constructor stub
	}
	
	public void hit() {
		return;
	}
	
	@Override
	public void render() {
		update(MainWindow.dt);
		if(sprite==null)return;
		mw.image(sprite, r.x*MainWindow.scale, r.y*MainWindow.scale);
	}

}

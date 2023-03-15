package objects.moving;

import forces.Friction;
import forces.Gravity;
import forces.Normal;
import main.MainWindow;
import objects.Object;
import objects.blocks.SolidBlock;
import processing.core.PVector;

public class CollisionObject extends Object{
	
	MainWindow mw;
	
	public CollisionObject(double m, PVector ir, PVector iv, PVector ia, MainWindow mw) {
		super(m, ir, iv, ia);
		this.mw=mw;
		addForce(new Gravity(this, mw.g));
		addForce(new Normal(this));
		addForce(new Friction(this));
	}

	public int collisionStatus=0;
	public static final int TOP_MASK=1;
	public static final int RIGHT_MASK=2;
	public static final int BOTTOM_MASK=4;
	public static final int LEFT_MASK=8;
	
	public static final int BOTTOM=1;
	public static final int TOP=2;
	public static final int RIGHT=3;
	public static final int LEFT=4;
	
	protected int height=1;
	protected int width=1;
	
	protected float tolerance=0.2f;
	
	public float getMuS() {
		return 0.7f;
	}
	
	public float getMuK() {
		return 0.5f;
	}
	
	public boolean isColliding(int side) {
		switch(side) {
			case BOTTOM:
				return (collisionStatus&BOTTOM_MASK)==BOTTOM_MASK;
			case TOP:
				return (collisionStatus&TOP_MASK)==TOP_MASK;
			case RIGHT:
				return (collisionStatus&RIGHT_MASK)==RIGHT_MASK;
			case LEFT:
				return (collisionStatus&LEFT_MASK)==LEFT_MASK;
		}
		return false;
	}
	
	public void setColliding(int side) {
		switch(side) {
			case BOTTOM:
				collisionStatus|=BOTTOM_MASK;
				break;
			case TOP:
				collisionStatus|=TOP_MASK;
				break;
			case RIGHT:
				collisionStatus|=RIGHT_MASK;
				break;
			case LEFT:
				collisionStatus|=LEFT_MASK;
				break;
		}
	}
	
	public void setFree(int side) {
		switch(side) {
			case BOTTOM:
				collisionStatus=~(~collisionStatus|BOTTOM_MASK);
				break;
			case TOP:
				collisionStatus=~(~collisionStatus|TOP_MASK);
				break;
			case RIGHT:
				collisionStatus=~(~collisionStatus|RIGHT_MASK);
				break;
			case LEFT:
				collisionStatus=~(~collisionStatus|LEFT_MASK);
				break;
		}
	}

	@Override
	public void update(float dt) {
		checkBlockCollisions();
		if(r.x*MainWindow.scale>-mw.transVal-mw.loadRad*MainWindow.scale && r.x*MainWindow.scale < -mw.transVal + mw.width+mw.loadRad*MainWindow.scale) 
		super.update(dt);		
		else if(this instanceof Mario) super.update(dt);

	}
	
	protected void checkBlockCollisions() {
		//throw away the collision check if we're out of range
		if(Math.floor(r.x) >=0 && Math.ceil(r.x) < SolidBlock.collisionMask.length && Math.floor(r.y) >= 0 && Math.ceil(r.y) < SolidBlock.collisionMask[0].length) {
			//TODO: update with height
			if(SolidBlock.collisionMask[(int) Math.floor(r.x+1)][(int) Math.ceil(r.y+height-1)]!=null || SolidBlock.collisionMask[(int) Math.floor(r.x)][(int) Math.ceil(r.y+height-1)]!=null) setColliding(BOTTOM); else setFree(BOTTOM);
			if(SolidBlock.collisionMask[(int) Math.floor(r.x+1)][(int) Math.floor(r.y+0.8)]!=null) setColliding(RIGHT); else setFree(RIGHT);
			if(Math.floor(r.y) >=1) 
				if(SolidBlock.collisionMask[(int) Math.floor(r.x)][(int) Math.floor(r.y)] !=null|| SolidBlock.collisionMask[(int) Math.floor(r.x+1)][(int) Math.floor(r.y)]!=null) setColliding(TOP); else setFree(TOP);
			if(Math.floor(r.x) >=1)
				if(SolidBlock.collisionMask[(int) Math.floor(r.x)][(int) Math.floor(r.y+0.8)]!=null) setColliding(LEFT); else setFree(LEFT);			
		}
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	@Override
	public void render() {
		
	}
	
	public boolean isWalking() {
		return true;
	}

}

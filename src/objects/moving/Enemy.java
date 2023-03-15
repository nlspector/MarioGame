package objects.moving;

import main.MainWindow;
import processing.core.PVector;

public abstract class Enemy extends CollisionObject{

	public Enemy(double m, PVector ir, PVector iv, PVector ia, MainWindow mw) {
		super(m, ir, iv, ia, mw);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		//if we've run into a wall let's turn around
		if(isColliding(RIGHT)) { r.x -=0.1; v.x*=-1; }
		if(isColliding(LEFT)) { r.x +=0.1; v.x*=-1; }
	}
	public abstract void handleStomp(Mario m);

}

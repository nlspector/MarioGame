package forces;

import objects.moving.CollisionObject;
import objects.moving.Mario;
import processing.core.PVector;

public class Normal implements Force{
	CollisionObject target;
	PVector force;
	
	public Normal(CollisionObject target) {
		this.target=target;
	}

	@Override
	public PVector getForce() {
		
		// TODO Auto-generated method stub
		return force;
	}

	@Override
	public void update() {
		if(target.isColliding(CollisionObject.BOTTOM)) {
			target.setVelY(0);
			force = new PVector(0, (float)(-target.getNonNormalForce().y)); 
		}
		
		
		else {

			force=new PVector(0,0);
		}
		
		
	}

}

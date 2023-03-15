package forces;

import objects.moving.CollisionObject;
import objects.moving.Mario;
import processing.core.PVector;

public class Friction implements Force{
	
	PVector force = new PVector(0,0);
	
	CollisionObject target;
	
	public Friction(CollisionObject target) {
		this.target=target;
	}
	
	@Override
	public PVector getForce() {
		return force;
	}

	@Override
	public void update() {
		//yes this only works on a grid shh
		//it it's walking then friction technically is what's propelling him forward
		//but it's not really resisting motion which is what this force is for
		//so no force if it's walking
		if(target instanceof Mario) {
			if((target.isWalking() && !(Math.abs(((Mario) target).getVel().x) > Math.abs(((Mario) target).currSpeed)))) force = new PVector(0,0);
			else if(Math.abs(target.getNetForce().x) < Math.abs(target.getMuS()*target.getNormalForce().getForce().y) && Math.abs(target.getVel().x) < 0.1) {
				target.setVelX(0);
				force.x= -target.getNetForce().x;
			}
			else force.x=-Math.copySign(target.getNormalForce().getForce().y*target.getMuK(), target.getVel().x);
		} else if(target.isWalking()) force = new PVector(0,0); //so we're not a mario--no need to adjust
		//if we're still in static friction then it can still give

		else if(Math.abs(target.getNetForce().x) < Math.abs(target.getMuS()*target.getNormalForce().getForce().y) && Math.abs(target.getVel().x) < 0.1) {
			target.setVelX(0);
			force.x= -target.getNetForce().x;
		}
		else force.x=-Math.copySign(target.getNormalForce().getForce().y*target.getMuK(), target.getVel().x);
		
	}
	

}

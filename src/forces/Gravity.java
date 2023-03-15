package forces;

import objects.Object;
import processing.core.PVector;

public class Gravity implements Force {

	private PVector force;
	//gravitational constant
	private double G = 6.67 * Math.pow(10,-11);
	private Object o1, o2;
	
	public Gravity(Object obj1, Object obj2) {
		o1=obj1;
		o2=obj2;
	}

	public PVector getForce() {
		return force;
		
	}
	
	public void update() {
		//the masses
		double m1 = o1.getMass();
		double m2 = o2.getMass();
		//get the vector between the two objects
		PVector rvec = PVector.sub(o2.getPos(), o1.getPos());
		//pythagorean theorem to get the distance squared (instead of square rooting then un-square rooting)
		double r2 = (rvec.x*rvec.x + rvec.y*rvec.y);
		//now normalize the vector for direction
		if(rvec.mag() != 0) rvec.normalize();
		//apply universal gravitation
		force = PVector.mult(rvec, (float) (m1*m2*G/r2));
	}
	
}

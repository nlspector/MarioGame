package objects;

import java.util.ArrayList;
import java.util.List;

import forces.Force;
import forces.Normal;
import processing.core.PVector;

public abstract class Object {

	//list of forces acting on this object
	protected List<Force> forces = new ArrayList<>();
	protected Force normal;
	
	protected double mass;
	protected PVector a,v,r;
	
	//this vector should tell the relative position of where the object impacts the ground
	protected PVector offset = new PVector(0,0);
	
	
	public Object(double m, PVector ir, PVector iv, PVector ia) {
		mass = m;
		//r,v,a shorthands for position, velocity, acceleration
		r=ir;
		v=iv;
		a=ia;
	}
	
	public void update(float dt) {
		a = new PVector(0,0);
		//for each force
		for(Force force : forces) {
			//update it
			force.update();
			//and use F=ma to calculate resultant acceleration
			a.add(PVector.mult(force.getForce(), (float) (1/mass)));
		}
		v.add(PVector.mult(a, dt));
		r.add(PVector.mult(v,dt));
			
	}
	
	public PVector getPos() {
		return r;
	}
	
	public PVector getVel() {
		return v;
	}
	
	public void setVel(PVector vel) {
		v=vel;
	}
	
	public void setVelX(float x) {
		v.x=x;
	}
	
	public void setVelY(float y) {
		v.y=y;
	}
	
	
	public void setPos(PVector pos) {
		r = pos;
	}

	public void setPosX(float x) {
		r.x = x;
	}
	
	public void addPosX(float x) {
		r.x += x;
	}

	public void setPosY(float y) {
		r.y = y;
	}
	
	public PVector getNonNormalForce() {
		PVector sum = new PVector(0,0);
		for(Force f : forces) {
			if(!(f instanceof Normal)) sum.add(f.getForce());
		}
		return sum;
	}
	
	public PVector getNetForce() {
		PVector sum = new PVector(0,0);
		for (Force f : forces) {
			sum.add(f.getForce());
		}
		return sum;
	}
	
	
	public PVector getAccel() {
		return a;
	}
	
	public abstract void render();
	
	public void addForce(Force f) {
		if(f instanceof Normal) normal=f;
		forces.add(f);
	}
	
	public double getMass() {
		return mass;
	}
	
	public PVector getOffset() {
		return offset;
	}
	
	public Force getNormalForce() {
		return normal;
	}

	
}

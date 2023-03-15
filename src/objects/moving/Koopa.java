package objects.moving;

import main.MainWindow;
import objects.blocks.SolidBlock;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;

public class Koopa extends Enemy{
	PImage[] spriteList = new PImage[5];
	
	boolean dead = false;
	
	int deathMessage=0;
	
	int startFrameCount=0;
	
	float bounceCoeff=1f;
	boolean isSliding=false;
	
	SoundFile sf;
					
	double[] angles = {3.14/12d,0,-3.14/8d,-3.14/16d,-3.14/16d,3.14/8d, 3.14/15d,-3.14/6d,-3.14/24d};
	
	public Koopa(double m, PVector ir, PVector iv, PVector ia, MainWindow mw) {
		super(m, ir, iv, ia, mw);
		spriteList[0] = mw.loadImage("koopal1.png");
		spriteList[0].resize(MainWindow.scale,0);
		spriteList[1] = mw.loadImage("koopal2.png");
		spriteList[1].resize(MainWindow.scale,0);
		spriteList[2] = mw.loadImage("koopar1.png");
		spriteList[2].resize(MainWindow.scale,0);
		spriteList[3] = mw.loadImage("koopar2.png");
		spriteList[3].resize(MainWindow.scale,0);
		spriteList[4] = mw.loadImage("koopashell.png");
		spriteList[4].resize(MainWindow.scale,0);
		double rand = Math.random();
		if(rand < 0.3) deathMessage=0;
		else if(rand < 0.6) deathMessage=2;
		else if(rand < 0.9) deathMessage=3;
		else deathMessage=1;
		sf=new SoundFile(mw, "stomp.wav");
	}
	@Override
	public float getMuK() {
		return (dead ? 0.2f : 0.5f);
	}

	@Override
	public void render() {
		update(MainWindow.dt);
		if(!dead) {
			mw.pushMatrix();
			mw.translate(0, -0.4f*MainWindow.scale);
			mw.image(spriteList[(v.x < 0 ? 0 : 2) + (mw.frameCount/20)%2], r.x*MainWindow.scale, r.y*MainWindow.scale);
			mw.popMatrix();
			
			//else mw.image(spriteList[(v.x < 0 ? 0 : 2)], r.x*MainWindow.scale, r.y*MainWindow.scale);
		} else mw.image(spriteList[4], r.x*MainWindow.scale, r.y*MainWindow.scale);

	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		if(Math.abs(v.x) < 0.1 && isSliding && !dead) {
			isSliding=false;
			v.x=Math.copySign(1, mw.mario.getPos().x-r.x);
			
		}
		
	}
	
	public void checkMovingCollision() {
		for(CollisionObject o : mw.getMovingObjects()) {
			if(!(o instanceof Koopa)) {
				//throw away anything where the x value doesn't line up asap -- this is the majority of possible collisions
				if(!(Math.abs(o.getPos().x-getPos().x) < 1)) continue;
				if(!(Math.abs(o.getPos().y-getPos().y) < 1)) continue;
				if(o instanceof Enemy) {
					((Enemy) o).getVel().x*=-1;
				}
			}
		}
		
	}
	
	@Override
	public boolean isWalking() {
		return !isSliding;
	}

	@Override
	public void handleStomp(Mario m) {

		//if they are facing the same way we are going to stomp on the koopa's shell
		if (v.x*m.getVel().x>0) doBounce(m);
		else if(!isWalking() || dead) return;
		else if ((getPos().y < m.getPos().y || PVector.sub(m.getVel(),getVel()).mag() <= 3.01)&&(isWalking())) m.die();
		else {
			doBounce(m);
			m.killStreak++;
			dead=true;
		}

	}
	
	public void doBounce(Mario m) {

		PVector rVel = PVector.sub(m.getVel(), getVel());
		PVector mVel = PVector.mult(rVel,-bounceCoeff);
		//momentum time tm
		float koopaVelX = (float) (((1f+bounceCoeff)*rVel.x*m.getMass())/mass);
		setVelX(v.x+koopaVelX);
		m.getVel().add(mVel);
		isSliding=true;
		sf.play();
	}
}

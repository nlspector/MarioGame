package objects.moving;

import main.MainWindow;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;

public class Goomba extends Enemy{
	
	PImage[] spriteList = new PImage[6];
	
	boolean dead = false;
	
	int deathMessage=0;
	
	int startFrameCount=0;
	
	String[][] pityPlease = {{"ouch...", "Why me?!?", "You monster!", "Such a senseless crime!"},
			{"Welcome to Duloc", "such a perfect town", "Here we have some rules", "let us lay them down", "Don't stand out, stay in line", "and we'll get along fine", "Duloc is a perfect place", "Duloc is a perfect place", "Please keep off of the grass", "Shine your shoes wipe your...", "face", "Duloc is, Duloc is", "Duloc is a perfect place", "Duloc is a perfect place", ""},
			{"I'm gonna kill you!", "Come back here you coward!", "Running away, eh?", "I once beat Ryan Leventhal at a handshake!"},
			{"sjhsdkjsaha", "asjgsjsaisddgh", "siagheyrgyqyvghed", "saihdbyurebvtavTVEJHYG", "IUDGFYEWGFRYUWEFQGL"}};
	
	SoundFile sf;
					
	double[] angles = {3.14/12d,0,-3.14/8d,-3.14/16d,-3.14/16d,3.14/8d, 3.14/15d,-3.14/6d,-3.14/24d};
	
	int liveCount = -1;
	public Goomba(double m, PVector ir, PVector iv, PVector ia, MainWindow mw) {
		super(m, ir, iv, ia, mw);
		spriteList[0] = mw.loadImage("goomba1.png");
		spriteList[0].resize(0, MainWindow.scale);
		spriteList[1] = mw.loadImage("goomba2.png");
		spriteList[1].resize(0, MainWindow.scale);
		spriteList[2] = mw.loadImage("goombastomp.png");
		spriteList[2].resize(0, MainWindow.scale);
		spriteList[3] = mw.loadImage("lsdgoomba1.png");
		spriteList[3].resize(0, MainWindow.scale);
		spriteList[4] = mw.loadImage("lsdgoomba2.png");
		spriteList[4].resize(0, MainWindow.scale);
		spriteList[5] = mw.loadImage("lsdgoombastomp.png");
		spriteList[5].resize(0, MainWindow.scale);
		double rand = Math.random();
		if(rand < 0.3) deathMessage=0;
		else if(rand < 0.6) deathMessage=2;
		else if(rand < 0.9) deathMessage=3;
		else deathMessage=1;
		sf=new SoundFile(mw, "stomp.wav");
	}

	@Override
	public void render() {
		if(!dead) {
			update(MainWindow.dt);

			if(liveCount==-1) mw.image(spriteList[(mw.isHallucinogenEnabled() ? 3 : 0) + (mw.frameCount/20)%2], r.x*MainWindow.scale, r.y*MainWindow.scale);
			else mw.image(spriteList[mw.isHallucinogenEnabled() ? 5 : 2], r.x*MainWindow.scale, r.y*MainWindow.scale);
		}else iHaveAFamilyAndKids();

	}
	
	public void iHaveAFamilyAndKids() {
		mw.image(spriteList[mw.isHallucinogenEnabled() ? 5 : 2], r.x*MainWindow.scale, r.y*MainWindow.scale);
		mw.fill(255);
		mw.textSize(24);
		mw.pushMatrix();
		mw.translate(r.x*MainWindow.scale, r.y*MainWindow.scale);
		mw.rotate((float) (angles[((mw.frameCount-startFrameCount)/120)%angles.length]));
		mw.text(pityPlease[deathMessage][((mw.frameCount-startFrameCount)/120)%pityPlease[deathMessage].length], 0,0);
		mw.popMatrix();
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		if(liveCount != -1) liveCount++;
		if(liveCount > 120) destroy();
	}
	
	@Override
	public boolean isWalking() {
		return liveCount==-1;
	}
	
	public void destroy() {
		startFrameCount=mw.frameCount;
		dead=true;
		
	}
	
	public void stomp(Mario m) {
		//momentum time
		PVector mi = PVector.mult(getVel(), (float) getMass()).add(PVector.mult(m.getVel(), (float) m.getMass()));
		PVector vf = PVector.mult(mi, (float) (1/(m.getMass()+getMass())));
		vf.y = (vf.y < 0 && isColliding(BOTTOM)) ? 0 : vf.y;
		sf.play();
		liveCount++;
		setVel(vf);
		m.setVel(vf);
		m.killStreak++;
	}

	@Override
	public void handleStomp(Mario m) {
		if(!isWalking()) return;
		if(getPos().y -m.getPos().y >0.5) return;
		System.out.println( PVector.sub(m.getVel(),getVel()).mag());
		if ((getPos().y < m.getPos().y || PVector.sub(m.getVel(),getVel()).mag() <= 3.01)&&(isWalking())) m.die();
		else stomp(m);
	}
	
	

}

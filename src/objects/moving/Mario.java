package objects.moving;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import forces.Force;
import forces.Normal;
import main.MainWindow;
import main.MainWindow.LevelManager;
import objects.blocks.SolidBlock;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;

public class Mario extends CollisionObject{
	
	public int health=100;
	
	public int collisionStatus=0;
	
	private List<PImage> sprites = new ArrayList<>();
		
	public int ind;
	
	public boolean lastDir=true;
	
	private boolean isJumping=false;
	
	public boolean isWalking=true;
	
	private float jumpForce = 2200;
	
	private boolean isDead=false;
	
	private int counter=-1;
	
	public int killStreak=0;
	
	private SoundFile jump;
	private SoundFile die;
	
	private double om;
	
	public float currSpeed;
	
	//http://soundbible.com/925-Bone-Crushing.html
	private SoundFile ouch;
	//http://soundbible.com/1791-Torture.html
	private SoundFile no;
	
	
	public Mario(double m, PVector ir, PVector iv, PVector ia, MainWindow mw) {		
		super(m, ir, iv, ia,mw);
		this.mw=mw;
		om=m;
		
	}
	
	public String[] getSpriteNames() {
		return new String[]{"standsmallright.png", "smallrun1right.png", "smallrun2right.png", "smallrun3right.png", "jumpright.png","standsmallleft.png", "smallrun1left.png", "smallrun2left.png", "smallrun3left.png", "jumpleft.png", "smallmariodie.png"};
	}
	
	public void loadData() {
		String[] spriteNames = getSpriteNames();
		for(String s : spriteNames) {
			PImage im = mw.loadImage(s);
			im.resize(0, MainWindow.scale);
			sprites.add(im);
		}
		//https://themushroomkingdom.net/media/smb/wav
		jump=new SoundFile(mw, "jump.wav");
		die=new SoundFile(mw, "die.wav");
		ouch=new SoundFile(mw, "ouch.wav");
		no=new SoundFile(mw, "no.wav");
	}

	@Override
	public void render() {
		update(MainWindow.dt);
		if(!isDead()) { 

			ind++;
			int mod = lastDir ? 0 : 5;
			if(isJumping) mw.image(sprites.get(4+mod), r.x*MainWindow.scale, r.y*MainWindow.scale);
			else if(Math.abs(v.x) > 0.1 && isWalking) mw.image(sprites.get((int) ((ind/4)%3+1+mod)), r.x*MainWindow.scale, r.y*MainWindow.scale);
			else mw.image(sprites.get(mod), r.x*MainWindow.scale, r.y*MainWindow.scale);
		}
		else mw.image(sprites.get(10), r.x*MainWindow.scale, r.y*MainWindow.scale);		
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		if(isColliding(RIGHT)) {v.x=0;r.x-=0.05;}
		else if(isColliding(LEFT)){v.x=0;r.x+=0.05;}
		else if(isColliding(TOP)) {
			SolidBlock sb = SolidBlock.collisionMask[(int) Math.floor(r.x)][(int) Math.floor(r.y)]; 
			SolidBlock sb2 = SolidBlock.collisionMask[(int) Math.floor(r.x+1)][(int) Math.floor(r.y)];
			if(sb!=null){
				sb.hit();
			}
			if(sb2!=null) {
				sb2.hit();
			}

			collisionDamage(v.y,-0.8f*v.y,3*3*25f,0.3f/50f);
			v.y*=-0.8f;
			r.y+=0.2;
		}
		
		
		
		if((health<1|| r.y > mw.deathLevel || (r.y < -1 && r.x < 160)) && counter==-1) die();
		else if((health<1|| r.y > mw.deathLevel || (r.y < 1 && r.x >= 160)) && counter==-1) win();
		
		
		
		if(counter!=-1) counter++;
		if(counter==180) mw.flagReset();
		checkMovingCollision();
		//this is here instead of in the mushroom class because otherwise every instance of a mushroom decrements the countr :(
		if(Mushroom.count > -1) Mushroom.count--;


	}
	
	public void win() {
		if(!mw.win) {
			try {
				Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=k9iYm9PEAHg"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mw.win=true;
	}
	
	@Override
	public void setColliding(int side) {
		super.setColliding(side);
		stopJump();
	}
	
	@Override
	public void checkBlockCollisions() {
		if(isDead()) {setFree(BOTTOM); setFree(TOP); setFree(RIGHT); setFree(LEFT);}
		else super.checkBlockCollisions();
	}
	
	public void checkMovingCollision() {
		if(isDead()) return;
		for(CollisionObject o : mw.getMovingObjects()) {
			if(!(o instanceof Mario)) {
				//throw away anything where the x value doesn't line up asap -- this is the majority of possible collisions
				if(!(Math.abs(o.getPos().x-getPos().x) < 1)) continue;
				if(!(Math.abs(o.getPos().y-getPos().y) < 1)) continue;
				if(o instanceof Enemy) {
					((Enemy) o).handleStomp(this);
					no.stop();
					
				} else if(o instanceof Item) {
					((Item) o).handleItem();
				}
			}
		}
		
	}
	
	public void die() {
		setDead(true);
		v=new PVector(0,-4.5f);
		die.play();
		counter++;
		
	}
	
	@Override
	public void setVelY(float y) {
		if(v.y > 0) collisionDamage(y,v.y, 6*6*0.5*mass, 0.12f/50f);
		super.setVelY(y);
	}
	
	public void collisionDamage(float a, float b, double threshold, float factor) {
		if(mw.isHallucinogenEnabled()) return;
		float diff = Math.abs(a-b);
		double ke=0.5*diff*diff*mass;
		//use kinetic energy time ooh fun
		if(ke > threshold) {
			health-=0.5*mass*diff*diff*factor;
			ouch.play();
		}
	}
	
	public void move(float speed) {
		if(isDead()) return;
		if(speed == 0f) {
			isWalking=false;
			return;
		}
		isWalking=true;
		if(!isColliding(BOTTOM)) return;
		boolean isGoingRight = Math.abs(speed) == speed;
		lastDir=isGoingRight;
		float effSpeed = (float) (speed*om/mass);
		if(isGoingRight && !isColliding(RIGHT)) {
			if(isColliding(LEFT)) r.x+=0.1;
			v.x=effSpeed;
			currSpeed=effSpeed;
		}
		else if(!isGoingRight && !isColliding(LEFT)) {
			if(isColliding(RIGHT)) r.x-=0.1;
			v.x=effSpeed;
			//don't need to up
			currSpeed=effSpeed;
		}
	}
	
	public void jump(float dt) {
		if(isDead() || mw.win) return;
		if(isColliding(BOTTOM)) {
			if(dt > 0.2) dt=0.08f;
			r.add(new PVector(0,-0.1f));
			v.y=(float) (-jumpForce*dt/this.getMass());
			isJumping=true;
			boolean no = false;
			for(CollisionObject o : mw.getMovingObjects()) {
				if(!(o instanceof Mario)) {
					if(Math.abs(r.x - o.getPos().x) < 4.5 && (v.x*o.getVel().x) < 0) no = true;
				}
			}
			if(no)this.no.play(); 
			else jump.play();

		}

	}
	
	public void jumpHallucinogen() {
		if(isDead()) return;
		if(isColliding(BOTTOM)) {
			r.add(new PVector(0,-0.1f));
			v.y=(float) (-jumpForce*0.3/this.getMass());
			isJumping=true;
			jump.play();

		}

	}
	
	@Override
	public float getMuK() {
		return (mw.isHallucinogenEnabled() ? 3 : 0.5f);
	}
	
	public void moveHallucinogen(float speed) {
		if(isDead()) return;
		if(speed == 0f) {
			isWalking=false;
			v.x=0;
		}
		isWalking=true;
		boolean isGoingRight = Math.abs(speed) == speed;
		lastDir=isGoingRight;
		if(isGoingRight && !isColliding(RIGHT)) {
			if(isColliding(LEFT)) r.x+=0.1;
			v.x=speed;
			currSpeed=speed;
		}
		else if(!isGoingRight && !isColliding(LEFT)) {
			if(isColliding(RIGHT)) r.x-=0.1;
			v.x=speed;
			currSpeed=speed;
		}
	}
	
	public void stopJump() {
		if(isJumping==true && isColliding(BOTTOM))	r.y=(float)Math.floor(r.y);
		isJumping=false;
	}
	
	@Override
	public boolean isWalking() {
		return isWalking;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}
	
	public void addMass(double coinMass) {
		mass+=coinMass;
	}
	

}

package objects.moving;

import main.MainWindow;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;

public class Coin extends CollisionObject implements Item{

	int count=-1;
	
	PImage[] sprite= new PImage[4];
	SoundFile sf;
	
	public Coin(double m, PVector ir, PVector iv, PVector ia, MainWindow mw) {
		super(m, ir, iv, ia, mw);
		for(int i = 0; i < 4; i++) {
			sprite[i]=mw.loadImage("coin" + (i+1) + ".png");
			sprite[i].resize(0, MainWindow.scale);
		}
		sf=new SoundFile(mw, "coin.wav");

	}
	
	@Override
	public void render() {
		update(MainWindow.dt);
		mw.image(sprite[(mw.frameCount/10)%4], r.x*MainWindow.scale, r.y*MainWindow.scale);
		
	}
	
	@Override 
	public void update(float dt) {
		super.update(dt);
		if(isColliding(TOP)) { v.y*=-0.8f; r.y+=0.1;}
	}
	
	public void destroy() {
		if(count==-1) {
			sf.play();
			r=new PVector(-20,0);
		}
		if(count!=-2)count++;
		
	}

	@Override
	public void handleItem() {
		mw.mario.addMass(mass);
		destroy();
		
	}
	
	@Override
	public boolean isWalking() {
		return false;
	}
	
}

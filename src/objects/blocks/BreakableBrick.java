package objects.blocks;

import main.MainWindow;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;

public class BreakableBrick extends SolidBlock{
	
	SoundFile sf;
	
	public BreakableBrick(PImage sprite, PVector pos, MainWindow mw) {
		super(sprite, pos, mw);
		sf=new SoundFile(mw, "smash.wav");
	}

	boolean isHit=false;
	
	public void hit() {
		collisionMask[(int) r.x][(int) r.y]=null;
		isHit=true;
		sf.play();
	}
	
	public void render() {
		if(isHit) return;
		super.render();
	}
}

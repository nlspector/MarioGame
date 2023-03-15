package objects.blocks;

import main.MainWindow;
import objects.moving.Coin;
import objects.moving.Mushroom;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;

public class ItemBlock extends SolidBlock{

	SoundFile sf;
	PImage hitSprite;
	int item;
	
	public static final int MUSHROOM=1;
	public static final int COIN=2;
	public static final int COINLIGHT=3;
	
	float mushroomExitSpeed=1;
	float coinExitSpeed=0.5f;
	
	public ItemBlock(PImage sprite, PImage sprite2, PVector pos, int item, MainWindow mw) {
		super(sprite, pos, mw);
		if(item == 1) sf=new SoundFile(mw, "powerup1.wav");
		else sf = new SoundFile(mw, "coin.wav");
		hitSprite=sprite2;
		this.item=item;
	}

	boolean isHit=false;
	
	public void hit() {
		sf.play();
		if(!isHit) {
			generateItem();
			isHit=true;
			sf=new SoundFile(mw,"bump.wav");
			sprite=hitSprite;
		}

	}
	
	public void generateItem() {
		boolean light =false;
		switch(item) {
			case MUSHROOM: 
				float mushExitVel=(float) (2*Math.sqrt((0.02f*mw.mario.getVel().magSq()*mw.mario.getMass()) - 0.5f*mw.mushroomMass*mushroomExitSpeed*mushroomExitSpeed)/mw.mushroomMass);

				if(mushExitVel < 0 || Float.isNaN(mushExitVel)) mushExitVel =0;
				mw.getMovingObjects().add(new Mushroom(mw.mushroomMass,new PVector(r.x,r.y-1), new PVector(Math.copySign(1,r.x-mw.mario.getPos().x),-mushExitVel), new PVector(0,0), mw));
				break;
			case COINLIGHT:
				light = true;
			case COIN:
				double cMass = light ? mw.coinLightMass : mw.coinMass;
				float coinExitVel=(float) (2*Math.sqrt((0.02f*mw.mario.getVel().magSq()*mw.mario.getMass()) - 0.5f*cMass*coinExitSpeed*coinExitSpeed)/cMass);
				if(coinExitVel < 0 || Float.isNaN(coinExitVel)) coinExitVel =0;

				mw.getMovingObjects().add(new Coin(cMass,new PVector(r.x,r.y-1), new PVector(Math.copySign(1,r.x-mw.mario.getPos().x)*coinExitSpeed,-coinExitVel), new PVector(0,0), mw));

				break;
		}
	}
	
}

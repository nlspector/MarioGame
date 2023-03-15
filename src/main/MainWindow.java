package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import objects.Object;
import objects.blocks.BreakableBrick;
import objects.blocks.ItemBlock;
import objects.blocks.SolidBlock;
import objects.moving.CollisionObject;
import objects.moving.Goomba;
import objects.moving.Ground;
import objects.moving.Koopa;
import objects.moving.Mario;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class MainWindow extends PApplet{
	
	//http://www.mariomayhem.com/downloads/sprites/super_mario_bros_sprites.php
	
	public static void main(String[] args) {
		PApplet.main(new String[] {MainWindow.class.getName()});
	}
	PImage brick;
	PImage stand;	
	
	//lists of objects that need to update and render
	private List<CollisionObject> moving = new ArrayList<>();
	List<Object> nonMoving = new ArrayList<>();
	List<Scenery> scenery = new ArrayList<>();
	
	//scaling variables
	public static int scale=64;
	public static float dt = 0.016666f;
	
	//buttons
	int jumpButton=UP;
	int leftButton=LEFT;
	int rightButton=RIGHT;
	
	//hallucinogen stuff
	boolean hallucinogen=false;
	float[] nmColors = new float[0];
	float[] mColors = new float[0];
	float[] sColors = new float[0];
	float bgColor=0;
	int startFrame=0;
	
	//win stuff
	float dx=10f;
	public boolean win = false;
	PVector textPos=new PVector(360,360);
	PVector textVel=new PVector(scale,scale);
		
	//masses of objects
	public double coinMass=200;
	public double coinLightMass=2;
	public double mushroomMass=10;
	
	//important objects
	public Ground g;
	public Mario mario;
	
	public int deathLevel=17;
	
	//for jumping
	int then = 0;
	
	boolean doClear=false;
	
	//stuff for scrolling the screen
	public float transVal=0;
	int bufferAmt=9;
	public int loadRad=3;
	public boolean isRunning=false;
	
	List<CollisionObject> addQueue = new ArrayList<>();

	
	public void settings() {
		size(1280,850,P3D);
		LevelManager.setWindow(this);
		
		LevelManager.genLevel1();

	}
	
	public void flagReset() {
		doClear=true;
	}
	
	
	public void keyPressed() {
			if(keyCode==jumpButton) {
				then = this.millis();
				if(hallucinogen) mario.jumpHallucinogen();
			}
			if(keyCode==leftButton) {
				if(hallucinogen) mario.moveHallucinogen(-5);
				else mario.move(isRunning ? -4  : -2);
			}
			if(keyCode==rightButton) {
				if(hallucinogen) mario.moveHallucinogen(5);
				else mario.move(isRunning ? 4 : 2);
			}
			if(keyCode=='R' && !hallucinogen) mario.die();
	}
	
	public void keyReleased() {
		if(keyCode==leftButton || keyCode==rightButton)
			mario.move(0);
		if(keyCode==jumpButton && !hallucinogen)
			mario.jump((this.millis()-then)/1000f);
		
		//this makes it unplayable but hilarious
		if(isHallucinogenEnabled()) {
			if(keyCode==leftButton) do leftButton=genRandomKey(); while(jumpButton==leftButton || leftButton==rightButton);
			else if(keyCode==rightButton) do rightButton=genRandomKey(); while(jumpButton==rightButton || leftButton==rightButton);
			//else if(keyCode==jumpButton) do jumpButton=genRandomKey(); while(jumpButton==rightButton || leftButton==jumpButton);

			//System.out.println((char) jumpButton + " = jump");
			System.out.println((char) rightButton + " = right");
			System.out.println((char) leftButton + " = left");
		}		
	}
	
	@Override
	public void setup() {
		frameRate(60);

	}
	
	public void draw() {
		//first check if we need to go into a different draw method
		if(win) {drawWin(); return;}
		if(hallucinogen) {drawHallucinogen(); return;}
		scale = (int) (64f*height/850f);
		
		background(94,145,254);
		//if we have gone too far start adding to transval (scroll the screen--negative is further along the level)
		if(mario.getPos().x*scale + transVal > this.width - bufferAmt*scale && mario.getVel().x > 0)transVal-=mario.getVel().x*scale*dt;
		if(mario.getPos().x*scale + transVal < bufferAmt*scale && mario.getVel().x < 0 && transVal < 0)transVal-=mario.getVel().x*scale*dt;
		pushMatrix();
		translate(transVal, 0);
		
		//we are clear to reset the level without getting a concurrent modification error
		//and we want to reset the level so do it
		if(doClear) {
			doClear=false;
			LevelManager.genLevel1();
		}
		//render everything
		//nonMoving and scenery should not change as the level progresses
		for(Scenery s : scenery) {
			s.render();
		}
		for(Object o : nonMoving) {
			o.render();
		}
		for(int i = 0; i < moving.size(); i++) {
			moving.get(i).render();
		}
		popMatrix();
		
		//now render health bar
		textSize(24);
		int displayInd=1;
		fill(1,100);
		noStroke();
		rect(0,0,480,24*3.5f);
		fill(255);
		text("HP: " + mario.health, 0, 24*(displayInd++));
		if(mario.getMass() > 52) text("Mass: " + (int)mario.getMass() + " kg", 0, 24*(displayInd++));
		if(mario.killStreak > 0) text("Kill streak: " + (int)mario.killStreak, 0, 24*(displayInd++));

	}
	
	
	
	
	public void drawWin() {
		pushStyle();
		pushMatrix();
		colorMode(HSB,1,1,1);
		//cycle through colors
		bgColor=(bgColor+0.01f)%1;
		background(bgColor,1,1);
		
		//if there is another object added to the list expand our list of colors
		if(nmColors.length < nonMoving.size()) {
			float[] newColors = Arrays.copyOf(nmColors, nonMoving.size());
			for(int i = nmColors.length; i < newColors.length; i++) newColors[i] = (float)Math.random();
			nmColors=newColors;
		}
		
		if(mColors.length < moving.size()) {
			float[] newColors = Arrays.copyOf(mColors, moving.size());
			for(int i = mColors.length; i < newColors.length; i++) newColors[i] = (float)Math.random();
			mColors=newColors;
		}
		if(sColors.length < scenery.size()) {
			float[] newColors = Arrays.copyOf(sColors, scenery.size());
			for(int i = sColors.length; i < newColors.length; i++) newColors[i] = (float)Math.random();
			sColors=newColors;
		}
		
		//trippy rotation stuff
		this.rotateY((float)(Math.sin((frameCount-startFrame)/240f)*3.14f/8f));
		this.rotateX((float)(Math.sin((frameCount-startFrame)/300f)*3.14f/6f));

		//start panning throughout the level
		transVal-=dx;
		//if we're at either end turn around
		if(transVal < -160*MainWindow.scale || transVal >0) {dx*=-1; transVal-=dx;}
		translate(transVal,0);
		
		
		if(doClear) {
			doClear=false;
			LevelManager.genLevel1();
		}
		
		//now render everything trippily (this is commented, the others are more or less the same)
		for(int ind = 0; ind < scenery.size(); ind++) {
			//take a small step for this color
			sColors[ind]=(sColors[ind]+0.01f)%1;
			//tint it 
			tint(sColors[ind],1,1);
			
			pushMatrix();
			//translate it back 1.5 so the waves dont interfere
			translate(0, 0, (float)(MainWindow.scale*-1.5));

			//now render
			scenery.get(ind).render();
			popMatrix();
		}
		int ind=0;
		for(Object o : nonMoving) {
			nmColors[ind]=(nmColors[ind]+0.01f)%1;
			tint(nmColors[ind++],1,1);
			
			pushMatrix();
			//now make waves
			translate(0, 0, (float)(MainWindow.scale*Math.sin( (frameCount+3*o.getPos().x) /60f*3.1415f)));

			o.render();
			popMatrix();
		}
		ind=0;
		//check against the color list--if something has been added between the point when the color list
		//was last updated and the actual rendering then bad things happen :(
		for(int i = 0; i < Math.min(mColors.length, getMovingObjects().size()); i++) {
			mColors[ind]=(mColors[ind]+0.01f)%1;
			tint(mColors[ind++],1,1);
			pushMatrix();
			translate(0, 0, (float)(MainWindow.scale*Math.sin( (frameCount+3*getMovingObjects().get(i).getPos().x) /60f*3.1415f)));

			getMovingObjects().get(i).render();
			popMatrix();
		}
		
		
		popStyle();
		popMatrix();
		fill(255);
		textSize(72);
		text("YOU WIN!", textPos.x, textPos.y);
		textPos.add(PVector.mult(textVel, dt));
		if(textPos.x < 0 || textPos.x > width-48*6) textVel.x*=-1;
		if(textPos.y < 72 || textPos.y > height) textVel.y*=-1;
	}

	
	
	
	
	public void drawHallucinogen() {
		//i mean this is more or less the same as drawWin()
		pushMatrix();
		pushStyle();
		colorMode(HSB,1,1,1);
		bgColor=(bgColor+0.01f)%1;
		background(bgColor,1,1);
		
		if(nmColors.length < nonMoving.size()) {
			float[] newColors = Arrays.copyOf(nmColors, nonMoving.size());
			for(int i = nmColors.length; i < newColors.length; i++) newColors[i] = (float)Math.random();
			nmColors=newColors;
		}
		
		if(mColors.length < moving.size()) {
			float[] newColors = Arrays.copyOf(mColors, moving.size());
			for(int i = mColors.length; i < newColors.length; i++) newColors[i] = (float)Math.random();
			mColors=newColors;
		}
		if(sColors.length < scenery.size()) {
			float[] newColors = Arrays.copyOf(sColors, scenery.size());
			for(int i = sColors.length; i < newColors.length; i++) newColors[i] = (float)Math.random();
			sColors=newColors;
		}
		
		//this is panning the screen left or right if we need to
		if(mario.getPos().x*scale + transVal > this.width - bufferAmt*scale && mario.getVel().x >0)transVal-=mario.getVel().x*scale*dt;
		if(mario.getPos().x*scale + transVal < bufferAmt*scale && mario.getVel().x < 0 && transVal < 0)transVal-=mario.getVel().x*scale*dt;

		//trippy rotations
		this.rotateY((float)(Math.sin((frameCount-startFrame)/240f)*3.14f/8f));
		this.rotateX((float)(Math.sin((frameCount-startFrame)/300f)*3.14f/6f));
		
		translate(transVal,0);

		
		if(doClear) {
			doClear=false;
			LevelManager.genLevel1();
		}
		
		for(int ind = 0; ind < scenery.size(); ind++) {
			sColors[ind]=(sColors[ind]+0.01f)%1;
			tint(sColors[ind],1,1);
			
			pushMatrix();
			translate(0, 0, (float)(MainWindow.scale*-1.5));

			scenery.get(ind).render();
			popMatrix();
		}
		int ind=0;
		for(Object o : nonMoving) {
			nmColors[ind]=(nmColors[ind]+0.01f)%1;
			tint(nmColors[ind++],1,1);
			
			pushMatrix();
			translate(0, 0, (float)(MainWindow.scale*Math.sin( (frameCount+3*o.getPos().x) /60f*3.1415f)));

			o.render();
			popMatrix();
		}
		ind=0;
		//check against the color list--if something has been added between the point when the color list
		//was last updated and the actual rendering then bad things happen :(
		for(int i = 0; i < Math.min(mColors.length, getMovingObjects().size()); i++) {
			mColors[ind]=(mColors[ind]+0.01f)%1;
			tint(mColors[ind++],1,1);
			pushMatrix();
			translate(0, 0, (float)(MainWindow.scale*Math.sin( (frameCount+3*getMovingObjects().get(i).getPos().x) /60f*3.1415f)));

			getMovingObjects().get(i).render();
			popMatrix();
		}
		
		
		popStyle();
		popMatrix();
		
		//draw the status bar
		fill(1,100);
		noStroke();
		rect(0,0,480,24*3.5f);
		fill(255);
		textSize(24);
		int displayInd=1;
		text("HP: Photosmart 6520 All-in-One Printer", 0, 24*(displayInd++));
		if(mario.getMass() > 50) text("Mass: achussetts", 0, 24*(displayInd++));
		if(mario.killStreak > 0) text("Kill streak: well kill is a harsh word", 0, 24*(displayInd++));

	}

	public List<CollisionObject> getMovingObjects() {
		return moving;
	}


	public void setMovingObjects(List<CollisionObject> moving) {
		this.moving = moving;
	}
	
	public void clearLevel() {
		//kill everything
		moving.clear();
		nonMoving.clear();
		scenery.clear();
		unHallucinogen();
		transVal=0;
	}
	
	public static class LevelManager {

		static MainWindow mw;
		
		public static void setWindow(MainWindow mwi) {
			mw=mwi;
		}
		
		public static void genLevel1() {
			mw.clearLevel();
			
			mw.deathLevel=14;
			
			SolidBlock.initCollisionMask(200, 20);
			
			//lots of textures
			
			PImage ground = mw.loadImage("ground.png");
			ground.resize(0, scale);
			
			PImage brick = mw.loadImage("brick.png");
			brick.resize(0, scale);
			
			PImage qbrick = mw.loadImage("questionblock.png");
			qbrick.resize(0, scale);
			
			PImage hitbrick = mw.loadImage("hitblock.png");
			hitbrick.resize(0,scale);
			
			PImage stairblock = mw.loadImage("stairblock.png");
			stairblock.resize(0, scale);
			
			PImage hill = mw.loadImage("bighill.png");
			hill.resize(5*scale,0);
			
			PImage cloud32 = mw.loadImage("cloud32.png");
			cloud32.resize(2*scale,0);
			
			PImage cloud48 = mw.loadImage("cloud48.png");
			cloud48.resize(3*scale,0);
				

			//tint for bushes = 128 208 16
			PImage cloud64 = mw.loadImage("cloud64.png");
			cloud64.resize(4*scale,0);
			
			mw.g=new Ground(mw);
			
			mw.nonMoving.add(mw.g);

			
			
			mw.mario = new Mario(50,new PVector(1,10), new PVector(0,0), new PVector(0,0),mw);
			mw.mario.loadData();			
			//mw.transVal=-126*64;
			
			mw.getMovingObjects().add(mw.mario);
			mw.getMovingObjects().add(new Goomba(50,new PVector(12,10), new PVector(-1,0), new PVector(0,0), mw));

			//i mean a lot of this is self-explanatory. Just add hills and clouds and bushes and whatnot
			//small hills use the same texture as big hills bt translated 1 down
			//bushes and clouds are same texture but tinted
			//and yeah
			//happy spaghetti code time
			
			mw.scenery.add(mw.new Scenery(new PVector(0,9),hill,mw));
			mw.scenery.add(mw.new Scenery(new PVector(43,9),hill,mw));
			mw.scenery.add(mw.new Scenery(new PVector(34,10),hill,mw));
			mw.scenery.add(mw.new Scenery(new PVector(75.5f,9),hill,mw));
			mw.scenery.add(mw.new Scenery(new PVector(130,10),hill,mw));
			mw.scenery.add(mw.new Scenery(new PVector(120,9),hill,mw));

			mw.scenery.add(mw.new Scenery(new PVector(13.5f,10),cloud64,mw, 128,208,16));
			mw.scenery.add(mw.new Scenery(new PVector(29.5f,10),cloud48,mw, 128,208,16));
			mw.scenery.add(mw.new Scenery(new PVector(93.5f,10),cloud32,mw, 128,208,16));
			mw.scenery.add(mw.new Scenery(new PVector(110.5f,10),cloud48,mw, 128,208,16));
			mw.scenery.add(mw.new Scenery(new PVector(65.5f,10),cloud64,mw, 128,208,16));
			mw.scenery.add(mw.new Scenery(new PVector(144.5f,10),cloud64,mw, 128,208,16));



			mw.scenery.add(mw.new Scenery(new PVector(2.5f,0.5f), cloud32, mw));
			mw.scenery.add(mw.new Scenery(new PVector(13.5f,1.5f), cloud48, mw));
			mw.scenery.add(mw.new Scenery(new PVector(27f,0.5f), cloud64, mw));
			mw.scenery.add(mw.new Scenery(new PVector(36.5f,1.5f), cloud32, mw));
			mw.scenery.add(mw.new Scenery(new PVector(50f,0.5f), cloud64, mw));
			mw.scenery.add(mw.new Scenery(new PVector(61.5f,1.5f), cloud48, mw));
			mw.scenery.add(mw.new Scenery(new PVector(70.5f,0.5f), cloud32, mw));

			mw.scenery.add(mw.new Scenery(new PVector(82.5f,0.5f), cloud32, mw));
			mw.scenery.add(mw.new Scenery(new PVector(93.5f,1.5f), cloud48, mw));
			mw.scenery.add(mw.new Scenery(new PVector(107f,0.5f), cloud64, mw));
			mw.scenery.add(mw.new Scenery(new PVector(116.5f,1.5f), cloud32, mw));
			mw.scenery.add(mw.new Scenery(new PVector(130f,0.5f), cloud64, mw));
			mw.scenery.add(mw.new Scenery(new PVector(142.5f,1.5f), cloud48, mw));
			mw.scenery.add(mw.new Scenery(new PVector(150.5f,0.5f), cloud32, mw));
			mw.scenery.add(mw.new Scenery(new PVector(160.5f,1.5f), cloud32, mw));
			
			for(int i = 0; i < 52; i++) {
				for(int j = 11; j<14;j++)
				mw.nonMoving.add(new SolidBlock(ground,new PVector(i,j),mw));
			}
			
			
			mw.nonMoving.add(new ItemBlock(qbrick,hitbrick,new PVector(6,8),ItemBlock.COINLIGHT,mw));

			
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(10,8),mw));
			mw.nonMoving.add(new ItemBlock(qbrick,hitbrick,new PVector(11,8),ItemBlock.COIN,mw));
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(12,8),mw));
			mw.nonMoving.add(new ItemBlock(qbrick,hitbrick,new PVector(13,8),ItemBlock.MUSHROOM,mw));
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(14,8),mw));
			
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(11,5),mw));
			mw.nonMoving.add(new ItemBlock(qbrick,hitbrick,new PVector(12,5),ItemBlock.COIN,mw));
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(13,5),mw));
			
			mw.genPipe(new PVector(18,9), 2);
			mw.genPipe(new PVector(25,8), 3);
			mw.genPipe(new PVector(32,8), 3);
			mw.genPipe(new PVector(42,9), 2);
			
			mw.getMovingObjects().add(new Goomba(10,new PVector(30,10), new PVector(-1,0), new PVector(0,0), mw));

			mw.getMovingObjects().add(new Goomba(10,new PVector(37,10), new PVector(-1,0), new PVector(0,0), mw));
			mw.getMovingObjects().add(new Goomba(10,new PVector(40,10), new PVector(-1,0), new PVector(0,0), mw));

			
			for(int i = 54; i < 72; i++) {
				for(int j = 11; j<14;j++)
				mw.nonMoving.add(new SolidBlock(ground,new PVector(i,j),mw));
			}
			
			for(int i = 75; i < 135; i++) {
				for(int j = 11; j<14;j++)
				mw.nonMoving.add(new SolidBlock(ground,new PVector(i,j),mw));
			}
			
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(62,7),mw));
			mw.nonMoving.add(new ItemBlock(qbrick,hitbrick,new PVector(63,7),ItemBlock.MUSHROOM,mw));
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(64,7),mw));
			
			for(int i = 65; i < 74; i++) mw.nonMoving.add(new BreakableBrick(brick,new PVector(i,3),mw));
			mw.getMovingObjects().add(new Goomba(10,new PVector(65,2), new PVector(-1,0), new PVector(0,0), mw));
			mw.getMovingObjects().add(new Goomba(10,new PVector(67,2), new PVector(-1,0), new PVector(0,0), mw));

			
			for(int i = 77; i < 80; i++) mw.nonMoving.add(new BreakableBrick(brick,new PVector(i,3),mw));

			mw.nonMoving.add(new ItemBlock(qbrick,hitbrick,new PVector(80,3),ItemBlock.MUSHROOM,mw));
			mw.nonMoving.add(new ItemBlock(brick,hitbrick,new PVector(80,7),ItemBlock.COIN,mw));
			
			mw.getMovingObjects().add(new Goomba(10,new PVector(81,10), new PVector(-1,0), new PVector(0,0), mw));
			mw.getMovingObjects().add(new Goomba(15,new PVector(83,10), new PVector(-1,0), new PVector(0,0), mw));
			
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(84,7),mw));
			mw.nonMoving.add(new ItemBlock(brick,hitbrick,new PVector(85,7),ItemBlock.COIN,mw));
			
			mw.nonMoving.add(new ItemBlock(qbrick,hitbrick,new PVector(90,7),ItemBlock.COIN,mw));
			mw.nonMoving.add(new ItemBlock(qbrick,hitbrick,new PVector(93,7),ItemBlock.COIN,mw));
			mw.nonMoving.add(new ItemBlock(qbrick,hitbrick,new PVector(93,3),ItemBlock.MUSHROOM,mw));
			mw.nonMoving.add(new ItemBlock(qbrick,hitbrick,new PVector(96,7),ItemBlock.COIN,mw));
			
			mw.getMovingObjects().add(new Koopa(50,new PVector(92,10), new PVector(-1,0), new PVector(0,0), mw));
			mw.getMovingObjects().add(new Goomba(10,new PVector(98,10), new PVector(-1,0), new PVector(0,0), mw));
			mw.getMovingObjects().add(new Goomba(15,new PVector(100,10), new PVector(-1,0), new PVector(0,0), mw));
			
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(101,7),mw));
			
			for(int i = 104; i < 107; i++) 	mw.nonMoving.add(new BreakableBrick(brick,new PVector(i,3),mw));
			
			mw.getMovingObjects().add(new Goomba(10,new PVector(108,10), new PVector(-1,0), new PVector(0,0), mw));
			mw.getMovingObjects().add(new Goomba(15,new PVector(110,10), new PVector(-1,0), new PVector(0,0), mw));
			
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(110,3),mw));
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(111,7),mw));
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(112,7),mw));
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(113,3),mw));
			mw.nonMoving.add(new ItemBlock(qbrick,hitbrick,new PVector(111,3),ItemBlock.COIN,mw));
			mw.nonMoving.add(new ItemBlock(qbrick,hitbrick,new PVector(112,3),ItemBlock.COIN,mw));
			
			mw.getMovingObjects().add(new Goomba(10,new PVector(113,10), new PVector(-1,0), new PVector(0,0), mw));
			mw.getMovingObjects().add(new Goomba(15,new PVector(114,10), new PVector(-1,0), new PVector(0,0), mw));
			
			for(int i = 0; i < 4; i++) for(int j = i; j < 4; j++) mw.nonMoving.add(new SolidBlock(stairblock,new PVector(115+j,10-i),mw));
			for(int i = 0; i < 4; i++) for(int j = 4; j > i; j--) mw.nonMoving.add(new SolidBlock(stairblock,new PVector(125-j,10-i),mw));

			for(int i = 0; i < 4; i++) for(int j = i; j < 4; j++) mw.nonMoving.add(new SolidBlock(stairblock,new PVector(130+j,10-i),mw));
			for(int i = 10; i > 6; i--) mw.nonMoving.add(new SolidBlock(stairblock,new PVector(134,i),mw));
			for(int i = 0; i < 4; i++) for(int j = 4; j > i; j--) mw.nonMoving.add(new SolidBlock(stairblock,new PVector(141-j,10-i),mw));
			
			for(int i = 137; i < 200; i++) {
				for(int j = 11; j<14;j++)
				mw.nonMoving.add(new SolidBlock(ground,new PVector(i,j),mw));
			}
			
			mw.genPipe(new PVector(145,9), 2);
			
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(150,7),mw));
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(151,7),mw));
			mw.nonMoving.add(new ItemBlock(qbrick,hitbrick,new PVector(152,7),ItemBlock.COIN,mw));
			mw.nonMoving.add(new BreakableBrick(brick,new PVector(153,7),mw));
			
			mw.getMovingObjects().add(new Goomba(10,new PVector(149,10), new PVector(-1,0), new PVector(0,0), mw));
			mw.getMovingObjects().add(new Goomba(15,new PVector(151,10), new PVector(-1,0), new PVector(0,0), mw));
			
			for(int i = 0; i < 11; i++) for(int j = i; j < 11; j++) mw.nonMoving.add(new SolidBlock(stairblock,new PVector(158+j,10-i),mw));
			for(int i = 0; i < 15; i++) for(int j = 0; j < 11; j++) mw.nonMoving.add(new SolidBlock(stairblock,new PVector(168+i,10-j),mw));
		}
		
	}
	
	
	//quick class to hold static scenery objects
	public class Scenery {
		PVector pos;
		PImage sprite;
		MainWindow mw;
		
		int r=-1,g=-1,b=-1;
		
		public Scenery(PVector pos, PImage sprite, MainWindow mw) {
			this.mw=mw;
			this.pos=pos;
			this.sprite=sprite;
		}
		
		public Scenery(PVector pos, PImage sprite, MainWindow mw, int r, int g, int b) {
			this.mw=mw;
			this.pos=pos;
			this.sprite=sprite;
			this.r=r;
			this.g=g;
			this.b=b;
		}
		
		public void render() {
			if(r!= -1) {mw.pushStyle(); mw.tint(r,g,b);}
			mw.image(sprite, pos.x*MainWindow.scale, pos.y*MainWindow.scale);
			if(r!=-1)mw.popStyle();
		}
		
		public PVector getPos() {
			return pos;
		}

	}
	
	//helper method to generate a pipe with top left corner at r and length downward len
	public void genPipe(PVector r, int len) {
		PImage top = loadImage("pipetop.png");
		top.resize(0, MainWindow.scale);
		PImage body = loadImage("pipebody.png");
		body.resize(0, MainWindow.scale);
		nonMoving.add(new SolidBlock(top,r,this));
		nonMoving.add(new SolidBlock(null,PVector.add(r, new PVector(1,0)),this));
		for(int i = 1; i < len; i++) {
			nonMoving.add(new SolidBlock(body, PVector.add(r, new PVector(0,i)), this));
			nonMoving.add(new SolidBlock(null, PVector.add(r, new PVector(1,i)), this));

		}

	}
	
	//start the trip
	public void hallucinogen() {
		if(hallucinogen==false) {
			jumpButton=genRandomKey();
			startFrame=frameCount;
			do leftButton=genRandomKey(); while(jumpButton==leftButton);
			do rightButton=genRandomKey(); while(jumpButton==rightButton || leftButton==rightButton);
			mario.move(0);
		}
		hallucinogen=true;

	}
	
	public boolean isHallucinogenEnabled() {
		return hallucinogen;
	}
	
	//back to normal
	public void unHallucinogen() {
		hallucinogen=false;
		jumpButton=UP;
		rightButton=RIGHT;
		leftButton=LEFT;
	}
	
	public int genRandomKey() {
		int randval = (int)Math.floor(Math.random()*36);
		if(randval < 10) return randval+48;
		else return randval+55;
	}
	
	
}

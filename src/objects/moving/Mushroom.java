package objects.moving;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import main.MainWindow;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;

public class Mushroom extends CollisionObject implements Item{
	
	PImage sprite;
		
	SoundFile sf;
	
	static int count = -1;
	boolean hasHandled=false;
	
	String[] appropriateSongs = {"https://www.youtube.com/watch?v=Y7lmAc3LKWM","https://www.youtube.com/watch?v=naoknj1ebqI","https://www.youtube.com/watch?v=1ZtR_-TPYIc","https://www.youtube.com/watch?v=Y_V6y1ZCg_8","https://www.youtube.com/watch?v=l8WMGBuNaus"};
		
	public Mushroom(double m, PVector ir, PVector iv, PVector ia, MainWindow mw) {
		super(m, ir, iv, ia, mw);
		sprite=mw.loadImage("mushroom.png");
		sprite.resize(0, MainWindow.scale);
		sf=new SoundFile(mw, "powerup2.wav");

	}

	@Override
	public void render() {
		if(!hasHandled) {
			update(MainWindow.dt);
			mw.image(sprite, r.x*MainWindow.scale, r.y*MainWindow.scale);
		}
		if(count == 0) mw.unHallucinogen();
		
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		//if we've run into a wall let's turn around
		if(isColliding(RIGHT)) { r.x -=0.1; v.x*=-1; }
		if(isColliding(LEFT)) { r.x +=0.1; v.x*=-1; }
	}
	
	
	@Override
	public float getMuK() {
		return 0.05f;
	}
	
	public float getMuS() {
		return 0.2f;
	}
	
	@Override
	public boolean isWalking() {
		return false;
	}

	public void destroy() {
		if(!hasHandled) {
			sf.play();
			r=new PVector(-20,0);
			hasHandled=true;
			try {
				Desktop.getDesktop().browse(new URI(appropriateSongs[(int)Math.floor(Math.random()*(float)(appropriateSongs.length))]));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
		}
		count=60*30;
		
	}

	@Override
	public void handleItem() {
		destroy();
		mw.hallucinogen();
		
	}
}

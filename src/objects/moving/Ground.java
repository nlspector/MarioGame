package objects.moving;

import main.MainWindow;
import objects.Object;
import processing.core.PVector;

public class Ground extends Object{

	MainWindow main;
	
	//just make it a generic object idk
	public Ground(double mass, PVector r, MainWindow mw) {
		super(mass, r, new PVector(0,0,0), new PVector(0,0,0));
		this.main = mw;
	}
	//make a generic earth ground constructor
	public Ground(MainWindow mw) {
		super(5.972*Math.pow(10,24), new PVector(0,(float) (6.371*Math.pow(10,6))), new PVector(0,0,0), new PVector(0,0,0));
		this.main = mw;
	}

	@Override
	public void render() {
		//do nothing, this is just a placeholder
	}

}

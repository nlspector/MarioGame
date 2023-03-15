package forces;

import processing.core.PVector;

public interface Force {
	//oh boy interface time
	
	//this method should pretty much just return a precalculated value for the force.
	public PVector getForce();
	
	//this method should (as stated) update the value of the force based on new information (velocity,
	//acceleration, position, number of lines in Mr. Conn's code with no breaks)
	public void update();

}

package ijaproject23.robot;

import ijaproject23.position.Position;

public interface Robot {
	
	/** Returns position of the robot
	 * @return Position of the robot
	 */
	public Position getPosition();
	
	/** Returns angle of robot
	 * @return Angle of the robot
	 */
	public int angle();
	
}

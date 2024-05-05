package ijaproject23.robot;

import java.util.ArrayList;

import ijaproject23.position.Position;
import javafx.scene.image.ImageView;

public interface Robot {
	
	/**
	 * Returns ImageView of a robot, used for visualization
	 * @return ImageView of a robot
	 */
    public ImageView getImageView();
	
	/** Returns position of the robot
	 * @return Position of the robot
	 */
	public Position getPosition();
	
	/** Returns angle of robot
	 * @return Angle of the robot
	 */
	public int angle();
	
	/**
	 * Move the robot one tile in its direction
	 */
	public void move();
	
	/**
	 * Get old position
	 * @return old position
	 */
	public Position getOld();

	/**
	 * Set old position to new position
	 * @param newPosition
	 */
	public void setOld(Position newPosition);

	public ArrayList<Integer> getArray();

	public void setAngle(int angle);
	public void setPosition(Position pos);
	public void setPosition(int x, int y);
	public int getWhen();
}

package ijaproject23.robot;

import ijaproject23.position.Position;
import ijaproject23.environment.Environment;
import java.util.ArrayList;

public class ControlledRobot implements Robot {
	
	// TODO NOTFY
	
	private Position pos;
	private int angle;
	private Environment env;
	private boolean canMove;
	private ArrayList<Integer> ListPositions;
	
	/**
	 * Creates robot with given angle and position
	 * @param pos
	 * @param angle
	 */
	public ControlledRobot(Position pos, int angle, Environment env) {
		this.pos = pos;
		this.angle = angle;
		this.env = env;
		this.canMove = false;
		this.ListPositions = new ArrayList<>();
		this.ListPositions.add(pos.getCol());
		this.ListPositions.add(pos.getRow());
		this.ListPositions.add(angle);
	}
	
	/**
	 * Returns position of the robot
	 */
	public Position getPosition() {
		return this.pos;
	}
	/**
	 * Returns angle of the robot
	 */
	public int angle() {
		return this.angle;
	}
	
	/**
	 * Turns robot left
	 */
	public void turnleft() {
		this.angle = this.angle - 45;
		if(this.angle < 0) {this.angle = this.angle + 360;}
		if(this.angle >= 360) {this.angle = this.angle%360;}
		// Notify?
		this.env.update();
		this.ListPositions.add(pos.getCol());
		this.ListPositions.add(pos.getRow());
		this.ListPositions.add(angle);
		//System.out.println("Controlled Robot {" + this.toString() + "} is currently facing " + this.angle + " degrees.");
	}
	
	/**
	 * Turns robot right
	 */
	public void turnright() {
		this.angle = this.angle + 45;
		if(this.angle < 0) {this.angle = this.angle + 360;}
		if(this.angle >= 360) {this.angle = this.angle%360;}
		// Notify?
		this.env.update();
		this.ListPositions.add(pos.getCol());
		this.ListPositions.add(pos.getRow());
		this.ListPositions.add(angle);
		//System.out.println("Controlled Robot {" + this.toString() + "} is currently facing " + this.angle + " degrees.");
	}
	
	public void allow_move() {
		this.canMove = true;
		//System.out.println("Robot " + this.toString() + " can move.");
	}
	public void disallow_move() {
		this.canMove = false;
		//System.out.println("Robot " + this.toString() + " can not move.");
	}
	
	
	/**
	 * Moves the robot forward (if it can move)
	 */
	public void move() {
		if(!canMove) {
			this.ListPositions.add(pos.getCol());
			this.ListPositions.add(pos.getRow());
			this.ListPositions.add(angle);
			return;
		}
		
		int x_change = 0, y_change = 0;
		// X coord change
        if(this.angle > 0+22 && this.angle < 180-22){
            x_change = 1; 
        }
        if(this.angle > 180+22 && this.angle < 360-22){
            x_change = -1;
        }

        // Y coord change
        if(this.angle > 90+22 && this.angle < 270-22){
            y_change = 1;
        }else if(this.angle%360 < 90-22 || this.angle%360 > 270+22){
            y_change = -1;
        }
        
        
        Position nw = new Position(this.pos.getRow() + y_change, this.pos.getCol() + x_change); 
        if(check(nw)) {
        	this.pos = nw;
        	// Notify
        	this.env.update();
        	this.ListPositions.add(pos.getCol());
			this.ListPositions.add(pos.getRow());
			this.ListPositions.add(angle);
        }else {
			//System.out.println("Controlled Robot {" + this.toString() + "} can not move this direction");
			this.ListPositions.add(pos.getCol());
			this.ListPositions.add(pos.getRow());
			this.ListPositions.add(angle);}
        //System.out.println("Controlled Robot {" + this.toString() + "} currently stands at (" + this.pos.toString() + ").");
	}
	
	/**
	 * Check position for obstacles/robots
	 * @param pos - position to be checked
	 * @return true if robot can move
	 */
	private boolean check(Position pos) {
		// Check for robot/obstacles 
		//System.out.println("Checking position (" + pos.toString() + ") for collisions.");
		if(this.env.containsPosition(pos) && !this.env.obstacleAt(pos) && !this.env.robotAt(pos)){/*System.out.println("Tile is empty. Robot can move.");*/ ;return true;}
		/*System.out.println("Tile is occupied. Robot can not move.");*/return false;
	}

	public ArrayList<Integer> getArray() {
		return ListPositions;
	}

	public void newPos(int x, int y, int angle) {
		this.pos = new Position(x, y);
		this.angle = angle;
	}
}

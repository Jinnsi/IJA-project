package ijaproject23.robot;

import ijaproject23.position.Position;

import java.util.ArrayList;
import java.util.List;

import ijaproject23.environment.Environment;

public class ProgrammedRobot implements Robot{
	private Position pos;
	private int angle;
	private Environment env;
	private int distance;
	private boolean turnLeft = false;
	private ArrayList<Integer> ListPositions;
	
	/**
	 * Creates robot with given angle and position
	 * @param pos
	 * @param angle
	 */
	public ProgrammedRobot(Position pos, int angle, Environment env, int viewing_distance) {
		this.pos = pos;
		this.angle = angle;
		this.env = env;
		this.distance = viewing_distance;
		this.ListPositions = new ArrayList<>();
		this.ListPositions.add(pos.getCol());
		this.ListPositions.add(pos.getRow());
		this.ListPositions.add(angle);
	}
	
	public ProgrammedRobot(Position pos, int angle, Environment env, int viewing_distance, boolean turnleft) {
		this.pos = pos;
		this.angle = angle;
		this.env = env;
		this.distance = viewing_distance;
		this.turnLeft = turnleft;
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
	private void turnleft() {
		this.angle = this.angle - 45;
		if(this.angle < 0) {this.angle = this.angle + 360;}
		if(this.angle >= 360) {this.angle = this.angle%360;}
		// Notify?
		this.env.update();
		//System.out.println("Programmed Robot {" + this.toString() + "} is currently facing " + this.angle + " degrees.");
		this.ListPositions.add(pos.getCol());
		this.ListPositions.add(pos.getRow());
		this.ListPositions.add(angle);
	}
	
	/**
	 * Turns robot right
	 */
	private void turnright() {
		this.angle = this.angle + 45;
		if(this.angle < 0) {this.angle = this.angle + 360;}
		if(this.angle >= 360) {this.angle = this.angle%360;}
		// Notify?
		this.env.update();
		//System.out.println("Programmed Robot {" + this.toString() + "} is currently facing " + this.angle + " degrees.");
		this.ListPositions.add(pos.getCol());
		this.ListPositions.add(pos.getRow());
		this.ListPositions.add(angle);
	}
	
	/**
	 * Moves the robot 
	 */
	public boolean move() {
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
        
        /**
         * Check positions in distance. If space is not occupied, move. Else turn and repeat
         */
        ArrayList<Position> postocheck = new ArrayList<Position>();
        
        for(int i = 1; i < this.distance+1; i++) {
        	Position pos = new Position(this.pos.getRow() + y_change*i, this.pos.getCol() + x_change*i);
        	//System.out.println("Tile " + pos.toString() + " will be checked.");
        	postocheck.add(pos);
        }
        
        List<Position> positions = postocheck;
        if(check(positions)) {
        	this.pos = positions.get(0);
        }else {
        	if(this.turnLeft) {this.turnleft();}else {this.turnright();}
        	return false;
        }   
        
        this.env.update();
        //System.out.println("Programmed Robot {" + this.toString() + "} currently stands at (" + this.pos.toString() + ").");
        this.ListPositions.add(pos.getCol());
		this.ListPositions.add(pos.getRow());
		this.ListPositions.add(angle);
        return true;
	}
	
	/**
	 * Check position for obstacles/robots. Rotates if necessary and calls move()
	 * @param pos - positions to be checked
	 */
	private boolean check(List<Position> pos) {
		// Check for robot/obstacles 
		for(Position position : pos) {
			if(!(this.env.containsPosition(position) && !this.env.obstacleAt(position) && !this.env.robotAt(position))){
	        	//System.out.println("Programmed Robot {" + this.toString() + "} can not move this direction");
				return false;
			}
		}
		return true;
	}

	public ArrayList<Integer> getArray() {
		return ListPositions;
	}

	public void newPos(int x, int y, int angle) {
		this.ListPositions = new ArrayList<>();
		this.pos = new Position(x, y);
		this.angle = angle;
	}
	
}

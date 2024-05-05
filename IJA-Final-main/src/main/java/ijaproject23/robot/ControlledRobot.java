package ijaproject23.robot;

import ijaproject23.position.Position;
import ijaproject23.environment.Environment;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;

public class ControlledRobot implements Robot {
	
	private Position pos;
	private int angle;
	private Environment env;
	private boolean canMove;
	public ImageView imageView;
	private int cellSize;
	public Position old;
	private ArrayList<Integer> ListPositions;
	private int Clicks;
	
	/**
	 * Creates robot with given angle and position
	 * @param pos
	 * @param angle
	 */
	public ControlledRobot(Position pos, int angle, int cellSize, Environment env, int clicks) {
		this.pos = pos;
		this.angle = angle;
		this.env = env;
		this.canMove = false;
		this.old = pos;
		this.cellSize = cellSize;
		this.ListPositions = new ArrayList<>();
		this.ListPositions.add(pos.getCol());
		this.ListPositions.add(pos.getRow());
		this.ListPositions.add(angle);
		this.Clicks=clicks;
    }
	
	/**
	 * @return old position
	 */
	public Position getOld() {
		return this.old;
	}
	
	public void createImageView() {
        // Load the PNG image
        Image image = new Image("file:lib/controlled_transparent.png");
        
        // Create an ImageView with the image
        this.imageView = new ImageView(image);
        this.imageView.setFitWidth(this.cellSize);
        this.imageView.setFitHeight(this.cellSize);
        
        // Set the position of the ImageView
        this.imageView.setLayoutX(this.pos.getCol() * this.cellSize);
        this.imageView.setLayoutY(this.pos.getRow() * this.cellSize);
        
        // Set rotation
        this.imageView.setRotate(this.angle);
    }
	
	/**
	 * Set old position
	 * @param new position to be set
	 */
	public void setOld(Position newPosition) {
		this.old = newPosition;
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
		// this.env.update();
		this.ListPositions.add(pos.getCol());
		this.ListPositions.add(pos.getRow());
		this.ListPositions.add(angle);
		this.imageView.setRotate(this.imageView.getRotate() - 45);
		System.out.println("Controlled Robot {" + this.toString() + "} is currently facing " + this.angle + " degrees.");
	}
	
	/**
	 * Turns robot right
	 */
	public void turnright() {
		this.angle = this.angle + 45;
		if(this.angle < 0) {this.angle = this.angle + 360;}
		if(this.angle >= 360) {this.angle = this.angle%360;}
		// Notify?
		// this.env.update();
		this.ListPositions.add(pos.getCol());
		this.ListPositions.add(pos.getRow());
		this.ListPositions.add(angle);
		this.imageView.setRotate(this.imageView.getRotate() + 45);
		System.out.println("Controlled Robot {" + this.toString() + "} is currently facing " + this.angle + " degrees.");
	}
	
	public void allow_move() {
		this.canMove = true;
		System.out.println("Robot " + this.toString() + " can move.");
	}
	public void disallow_move() {
		this.canMove = false;
		System.out.println("Robot " + this.toString() + " can not move.");
	}
	
	
	/**
	 * Moves the robot forward (if it can move)
	 */
	public void move() {
		this.old = this.pos;
		if(!canMove) {
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
        	this.env.update();

			this.ListPositions.add(pos.getCol());
			this.ListPositions.add(pos.getRow());
			this.ListPositions.add(angle);
        }else {
			this.ListPositions.add(pos.getCol());
			this.ListPositions.add(pos.getRow());
			this.ListPositions.add(angle);
			System.out.println("Controlled Robot {" + this.toString() + "} can not move this direction");}
        System.out.println("Controlled Robot {" + this.toString() + "} currently stands at (" + this.pos.toString() + ").");
	}
	
	/**
	 * Check position for obstacles/robots
	 * @param pos - position to be checked
	 * @return true if robot can move
	 */
	private boolean check(Position pos) {
		// Check for robot/obstacles 
		System.out.println("Checking position (" + pos.toString() + ") for collisions.");
		if(this.env.containsPosition(pos) && !this.env.obstacleAt(pos) && !this.env.robotAt(pos)){System.out.println("Tile is empty. Robot can move."); ;return true;}
		System.out.println("Tile is occupied. Robot can not move.");return false;
	}

	public ArrayList<Integer> getArray() {
		return ListPositions;
	}

	@Override
    public ImageView getImageView() {
        return imageView;
    }

	public void newPos(int x, int y, int angle) {
		this.pos = new Position(x, y);
		this.angle = angle;
	}

	public void setAngle(int angle){
		this.angle = angle;
		this.imageView.setRotate(angle);
	}

	public void setPosition(Position pos){
		this.pos = pos;
	}

	public void setPosition(int x, int y){
		this.pos = new Position(y, x);
	}

	public int getWhen() {
		return Clicks;
	}
}

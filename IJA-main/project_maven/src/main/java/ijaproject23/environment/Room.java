package ijaproject23.environment;

import ijaproject23.position.Position;
import ijaproject23.robot.Robot;
import ijaproject23.obstacle.Obstacle;
import java.util.ArrayList;
import ijaproject23.simulation.RobotSimulation;

public class Room implements Environment{
	
	// TODO ROBOT MOVEMENT OBSERVATION
	
    private int rows;
    private int cols;
    public ArrayList<Robot> robots;
    public ArrayList<Obstacle> obstacles; 
    private RobotSimulation sim;


    // Initializator
    public Room(int rows, int cols, RobotSimulation sim){
        this.rows = rows;
        this.cols = cols;
        this.sim = sim;
        robots = new ArrayList<Robot>();
        obstacles = new ArrayList<Obstacle>();
    }

    // Creates a room
    public static Room create(int rows, int cols, RobotSimulation sim){
        if(rows > 0 && cols > 0){
            Room room = new Room(rows, cols, sim);
            return room;
        }else{
            throw new IllegalArgumentException();
        }
    }

    // Adds robot to room
    public boolean addRobot(Robot robot){
        robots.add(robot);
        return true;
    }
    
    public void printObstacles() {
    	for(int i = 0; i < this.obstacles.size(); i++){
            System.out.println("Obstacle {" + this.obstacles.get(i).toString() + "} at " + this.obstacles.get(i).getPosition().toString());
        }
    }

    // Adds obstacle to room
    public boolean createObstacleAt(int row, int col, int when){
        Obstacle obs = new Obstacle(this, new Position(row, col), when);
        obstacles.add(obs);
        return true;
    }

    // returns true if position is inside room
    public boolean containsPosition(Position pos){
        if(pos.getRow() <= rows && pos.getCol() <= cols && pos.getRow() >= 0 && pos.getCol() >= 0){return true;}
        return false;
    }

    // is robot at position
    public boolean robotAt(Position pos){
        for(int i = 0; i < this.robots.size(); i++){
            if(robots.get(i).getPosition().equals(pos)){return true;}
        }
        return false;
    }

    // is obstacle at position
    public boolean obstacleAt(Position pos){
        for(int i = 0; i < this.obstacles.size(); i++){
            if(obstacles.get(i).getPosition().equals(pos)){return true;}
        }
        return false;
    }

    // is obstacle at position
    public boolean obstacleAt(int x, int y){
        for(int i = 0; i < this.obstacles.size(); i++){
            if(obstacles.get(i).getPosition().getRow() == x && obstacles.get(i).getPosition().getCol() == y){return true;}
        }
        return false;
    }

    public ArrayList<Obstacle> getObstacles(){
        return this.obstacles;
    }

    public ArrayList<Robot> getRobots(){
        return this.robots;
    }

	@Override
	public void update() {
		// Redraw this.sim
		this.sim.redraw();
	}   
}


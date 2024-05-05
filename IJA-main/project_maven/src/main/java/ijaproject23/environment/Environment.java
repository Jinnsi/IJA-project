package ijaproject23.environment;

import ijaproject23.position.Position;
import ijaproject23.robot.*;

public interface Environment {
    
    /**
     * Adds a robot to the Environment
     * @param robot - robot that will be added
     * @return true if position is inside environment and is empty
     */
    public boolean addRobot(Robot robot);
    
    /**
     * Method for updating environment
     */
    public void update();

    /**
     * Check that position exists inside enviroment
     * @param pos - position to be checked
     * @returns true if position exists
     */
    public boolean containsPosition(Position pos);
    
    /**
     * Created an obstacle at specified coordinates
     * @param row
     * @param col
     */
    public boolean createObstacleAt(int row, int col, int when);
    
    /**
     * Checks whether an obstacle exist at specified coordinates
     * @param row
     * @param col
     * @return true if position is inside space and has an obstacle
     */
    public boolean obstacleAt(int row, int col);
    
    /**
     * Checks whether an obstacle exist at specified coordinates
     * @param p
     * @return true if position is inside space and has an obstacle
     */
    public boolean obstacleAt(Position p);
    
    /**
     * Checks whether a robot is at specified coordinates
     * @param p
     * @return true if position is inside space and has a robot
     */
    public boolean robotAt(Position p);
    

}

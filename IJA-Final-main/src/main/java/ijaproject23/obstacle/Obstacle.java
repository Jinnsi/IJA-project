package ijaproject23.obstacle;

import ijaproject23.environment.Environment;
import ijaproject23.position.Position;
import javafx.scene.shape.Rectangle;

public class Obstacle {
    public int x;
    public int y;
    public Position pos;
    public Environment env;
    public Rectangle rectangle;
    public int when;

    public Obstacle(Environment env, Position pos, int timeClicks){
        this.x = pos.getCol();
        this.y = pos.getRow();
        this.pos = pos;
        this.env = env;
        this.when = timeClicks;
    }

    // Returns obstacle position
    public Position getPosition(){
        return this.pos;
    }
    
    public void createRectangle(int CELL_SIZE) {
    	this.rectangle = new Rectangle(this.x * CELL_SIZE, this.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }

    public int getWhen(){
        return this.when;
    }
}

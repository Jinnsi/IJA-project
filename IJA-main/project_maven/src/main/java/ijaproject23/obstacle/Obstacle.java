package ijaproject23.obstacle;

import ijaproject23.environment.Environment;
import ijaproject23.position.Position;

public class Obstacle {
    public int x;
    public int y;
    public Position pos;
    public Environment env;
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

    public int getWhen(){
        return this.when;
    }
}

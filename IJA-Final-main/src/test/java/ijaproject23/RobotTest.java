package ijaproject23;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import ijaproject23.environment.*;
import ijaproject23.position.*;
import ijaproject23.robot.ControlledRobot;
import ijaproject23.robot.ProgrammedRobot;

public class RobotTest {
	
	// Helper function
    private Room setup() {
        Room room = Room.create(10, 10);
        // createObstacleAt takes (row,col)
        // Position takes (y,x)
        room.createObstacleAt(3, 5);
        room.createObstacleAt(2, 5);
        room.createObstacleAt(5, 2);
        room.createObstacleAt(4, 6);
        assertTrue(room.obstacleAt(3, 5));
        assertTrue(room.obstacleAt(2, 5));
        assertTrue(room.obstacleAt(4, 6));
        assertFalse(room.obstacleAt(1, 3));
        assertFalse(room.obstacleAt(2, 1));
        room.printObstacles();
        return room;
    }
    
    @Test
    public void testControlledRobot() {
    	System.out.println("Controlled Robot");
        Room room = setup();
        // Top left corner, facing up
        ControlledRobot ctrlRbt = new ControlledRobot(new Position(0,0), 0, room);
        ProgrammedRobot prgRbt = new ProgrammedRobot(new Position(3,3), 45, room, 2);
        
        room.addRobot(ctrlRbt);
        room.addRobot(prgRbt);
        
        ctrlRbt.move();
        // Robot can not move outside of Room, position should not change
        assertEquals("ControlledRobot failed to stay within room boundaries.", new Position(0,0), ctrlRbt.getPosition());
        
        ctrlRbt.turnleft();
        // Robot angle -45=315
        assertEquals("ControlledRobot failed to turn left.", 315, ctrlRbt.angle());
        
        ctrlRbt.turnright();
        ctrlRbt.turnright();
        ctrlRbt.turnright();
        ctrlRbt.move();
        // Turn right 3 times = 90 degrees, move forward 
        // Move from 0,0 to 0,1
        assertEquals("ControlledRobot failed to move forward after turning right.", new Position(0,1), ctrlRbt.getPosition());
        
        ctrlRbt.turnright();
        ctrlRbt.turnright();
        ctrlRbt.move();
        ctrlRbt.turnleft();
        ctrlRbt.move();
        // move diagonally
        assertEquals("ControlledRobot failed to move diagonally.", new Position(2,2), ctrlRbt.getPosition());
        
        ctrlRbt.move();
        // Can not move because prgRbt occupies 3,3
        assertEquals("ControlledRobot moved despite obstruction.", new Position(2,2), ctrlRbt.getPosition());
        
        ctrlRbt.turnright();
        ctrlRbt.move();
        ctrlRbt.move();
        assertEquals("ControlledRobot failed to move correctly.", new Position(4,2), ctrlRbt.getPosition());
        
        ctrlRbt.move();
        // Can not move because obstacle at 5,2
        assertEquals("ControlledRobot moved despite obstruction.", new Position(4,2), ctrlRbt.getPosition());
        
    }
    
    @Test
    public void testProgrammedRobot_turnright() {
    	System.out.println("Programmed right");
        Room room = setup();
        room.createObstacleAt(4, 10);
        ProgrammedRobot prgRbt = new ProgrammedRobot(new Position(3,3), 45, room, 1);
        
        assertEquals("ProgrammedRobot initialized at incorrect position.", new Position(3,3), prgRbt.getPosition());
        
        int i = 0;
        while(i < 20) {
            i++;
            prgRbt.move();
        }
        // I dont know what the final position should be, will figure out later
        // But sysout's look good
    }
    
    @Test
    public void testProgrammedRobot_turnleft() {
    	System.out.println("Programmed left");
        Room room = setup();
        room.createObstacleAt(4, 10);
        ProgrammedRobot prgRbt = new ProgrammedRobot(new Position(3,3), 45, room, 1, true);
        
        assertEquals("ProgrammedRobot initialized at incorrect position.", new Position(3,3), prgRbt.getPosition());
        
        int i = 0;
        while(i < 20) {
            i++;
            prgRbt.move();
        }
        // I dont know what the final position should be, will figure out later
        // But sysout's look good
    }
    
    @Test
    public void testProgrammedRobot_viewdistance2() {
    	System.out.println("Programmed view distance 2");
        Room room = setup();
        room.createObstacleAt(4, 10);
        ProgrammedRobot prgRbt = new ProgrammedRobot(new Position(3,3), 45, room, 2);
        
        assertEquals("ProgrammedRobot initialized at incorrect position.", new Position(3,3), prgRbt.getPosition());
        
        int i = 0;
        while(i < 20) {
            i++;
            prgRbt.move();
        }
        // I dont know what the final position should be, will figure out later
        // But sysout's look good
    }
    
    @Test
    public void testProgrammedRobot_viewdistance3() {
    	System.out.println("Programmed view distance 3");
        Room room = setup();
        room.createObstacleAt(4, 10);
        ProgrammedRobot prgRbt = new ProgrammedRobot(new Position(3,3), 45, room, 3);
        
        assertEquals("ProgrammedRobot initialized at incorrect position.", new Position(3,3), prgRbt.getPosition());
        
        int i = 0;
        while(i < 20) {
            i++;
            prgRbt.move();
        }
        // I dont know what the final position should be, will figure out later
        // But sysout's look good
    }
    
    @Test
    public void testProgrammedRobot_occupiedpath() {
    	System.out.println("Programmed path occupied by robots");
        Room room = setup();
        room.createObstacleAt(4, 10);
        ControlledRobot rbt = new ControlledRobot(new Position(2, 4), 0, room);
        ProgrammedRobot rbtt = new ProgrammedRobot(new Position(3, 4), 0, room, 1);
        room.addRobot(rbt);
        room.addRobot(rbtt);
        ProgrammedRobot prgRbt = new ProgrammedRobot(new Position(3,3), 45, room, 1);
        
        assertEquals("ProgrammedRobot initialized at incorrect position.", new Position(3,3), prgRbt.getPosition());
        
        int i = 0;
        while(i < 20) {
            i++;
            prgRbt.move();
        }
        // I dont know what the final position should be, will figure out later
        // But sysout's look good
    }
	
}

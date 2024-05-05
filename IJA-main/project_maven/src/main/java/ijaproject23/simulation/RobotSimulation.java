package ijaproject23.simulation;

import javafx.application.Application;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Button;

import ijaproject23.robot.ControlledRobot;
import ijaproject23.robot.ProgrammedRobot;
import ijaproject23.environment.Room;
import ijaproject23.position.Position;
import ijaproject23.simulation.LoggerFile;
import ijaproject23.obstacle.Obstacle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/*import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;*/


/**
 * 
 * Ty circle entity by se asi mely ukladat jako atribut robota
 *
 */

public class RobotSimulation extends Application {
    private static final int GRID_SIZE_X = 10; // Number of columns
    private static final int GRID_SIZE_Y = 10; // Number of rows
    private static final int CELL_SIZE = 50; // Size of each cell
    private static final Color CONTROLLED_ROBOT_COLOR = Color.RED;
    private static final Color PROGRAMMED_ROBOT_COLOR = Color.BLUE;

    private Room room;
    private Circle controlledRobotCircle;
    private Circle prgRobotCircle;
    private Group root = new Group();
    private boolean isPaused = false;
    private Timeline timeline;
    private boolean isDrawingCircle = false;
    private boolean isDrawingObstacle = false;
    private int ctrlRobot_CNT = 0;
	private boolean isDrawingControlled = false;
	private boolean moveBackwards = false;
	private LoggerFile logger = new LoggerFile();
	private int timeClicks = 0;



    @Override
    public void start(Stage primaryStage) {

        logger.info("The program started.");
        // Create output scene
        Scene scene = new Scene(root, GRID_SIZE_X * CELL_SIZE, GRID_SIZE_Y * CELL_SIZE);
        createGrid(root);

        // Initialize Timeline for continuous animation
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (!isPaused) {
                timeClicks++;
                moveRobots(); // Move robots every second
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // Run indefinitely

        // Create env
        this.room = new Room(GRID_SIZE_X, GRID_SIZE_Y, this);

        // Create a controlled robot
        ControlledRobot rbt = new ControlledRobot(new Position(5, 5), 0, this.room);
        this.room.addRobot(rbt);
        this.ctrlRobot_CNT++;
        
        Position initialPosition = rbt.getPosition(); // Get initial position
        controlledRobotCircle = new Circle(CELL_SIZE / 2, CONTROLLED_ROBOT_COLOR);
        controlledRobotCircle.setCenterX(initialPosition.getCol() * CELL_SIZE + CELL_SIZE / 2);
        controlledRobotCircle.setCenterY(initialPosition.getRow() * CELL_SIZE + CELL_SIZE / 2);

        // Create a programmed robot with view dist 2
        ProgrammedRobot rbtPRG = new ProgrammedRobot(new Position(2, 3), 0, this.room, 2);
        this.room.addRobot(rbtPRG);
        Position initialPositionPRG = rbtPRG.getPosition(); // Get initial position
        prgRobotCircle = new Circle(CELL_SIZE / 2, PROGRAMMED_ROBOT_COLOR);
        prgRobotCircle.setCenterX(initialPositionPRG.getCol() * CELL_SIZE + CELL_SIZE / 2);
        prgRobotCircle.setCenterY(initialPositionPRG.getRow() * CELL_SIZE + CELL_SIZE / 2);

        // Add the circles to the root after creating the grid
        root.getChildren().addAll(controlledRobotCircle, prgRobotCircle);
        
        // Create buttons
        Button addRobotButton = new Button("Add programmed robot");
        addRobotButton.setPrefSize(100, 100);
        addRobotButton.setLayoutX(GRID_SIZE_X * CELL_SIZE + 20);
        addRobotButton.setLayoutY(GRID_SIZE_Y * CELL_SIZE / 2 - 50);
        addRobotButton.setOnAction(e -> setRob_draw());
        addRobotButton.setFocusTraversable(false);
        
        Button addObstacleButton = new Button("Add obstacle");
        addObstacleButton.setPrefSize(100, 100);
        addObstacleButton.setLayoutX(GRID_SIZE_X * CELL_SIZE + 20);
        addObstacleButton.setLayoutY(GRID_SIZE_Y * CELL_SIZE / 2 + 50);
        addObstacleButton.setOnAction(e -> setObs_draw());
        addObstacleButton.setFocusTraversable(false);
        
        Button addControlledRobotButton = new Button("Add user controlled robot");
        addControlledRobotButton.setPrefSize(100, 100);
        addControlledRobotButton.setLayoutX(GRID_SIZE_X * CELL_SIZE + 20);
        addControlledRobotButton.setLayoutY(GRID_SIZE_Y * CELL_SIZE / 2 + 150);
        addControlledRobotButton.setOnAction(e -> setCTRL_draw());
        addControlledRobotButton.setFocusTraversable(false);

        Button createRoomFromFile = new Button("Load room from file");
        createRoomFromFile.setPrefSize(100, 100);
        createRoomFromFile.setLayoutX(GRID_SIZE_X * CELL_SIZE + 20);
        createRoomFromFile.setLayoutY(GRID_SIZE_Y * CELL_SIZE / 2 + 250);
        createRoomFromFile.setOnAction(e -> loadRoom());
        createRoomFromFile.setFocusTraversable(false);

        root.getChildren().addAll(addRobotButton, addObstacleButton, addControlledRobotButton, createRoomFromFile);
        
        // Handle user input
        scene.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();
            switch (key) {
                case UP:
                	((ControlledRobot) this.room.robots.get(0)).allow_move();
                	logger.info("The controlled robot can now move.");
                    break;
                case DOWN:
                	((ControlledRobot) this.room.robots.get(0)).disallow_move();
                	logger.info("The controlled robot cannot move.");
                    break;
                case LEFT:
                    ((ControlledRobot) this.room.robots.get(0)).turnleft();
                    logger.info("The controlled robot turned left.");
                    break;
                case RIGHT:
                    ((ControlledRobot) this.room.robots.get(0)).turnright();
                    logger.info("The controlled robot turned right.");
                    break;
                case SPACE:
                    togglePause();
                    logger.info("The program has been paused.");
                    break;
                case B:
                    moveBackwards = true;
                    logger.info("Going backwards.");
                    togglePause();
                    break;
                case F:
                    logger.info("Going forward.");
                    moveBackwards = false;
                    togglePause();
                    break;
                case W:
                	((ControlledRobot) this.room.robots.get(0)).allow_move();
                	logger.info("The controlled robot can now move.");
                    break;
                case S:
                	((ControlledRobot) this.room.robots.get(0)).disallow_move();
                	logger.info("The controlled robot cannot move.");
                    break;
                case A:
                    ((ControlledRobot) this.room.robots.get(0)).turnleft();
                    logger.info("The controlled robot turned left.");
                    break;
                case D:
                    ((ControlledRobot) this.room.robots.get(0)).turnright();
                    logger.info("The controlled robot turned right.");
                    break;
                default:
                    break;
            }
        });

        
     // Handle mouse click to draw circles or fill with color
        scene.setOnMouseClicked(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            int col = (int) (mouseX / CELL_SIZE);
            int row = (int) (mouseY / CELL_SIZE);

            if (this.isDrawingCircle) {
            	// TODO handle programmed robot addition
            	logger.info("Programmed robot has been added.");
                Circle circle = new Circle(col * CELL_SIZE + CELL_SIZE / 2, row * CELL_SIZE + CELL_SIZE / 2, CELL_SIZE / 2, Color.BLUE);
                root.getChildren().add(circle);
            } else if(this.isDrawingObstacle) {
                logger.info("Obstacle has been added.");
                Rectangle rect = new Rectangle(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                rect.setFill(Color.PURPLE);
                root.getChildren().add(rect);
                this.room.createObstacleAt(row, col, timeClicks);
            } else if(this.isDrawingControlled) {
            	// TODO handle controlled robot addition
            	logger.info("Controlled robot has been added.");
                Circle circle = new Circle(col * CELL_SIZE + CELL_SIZE / 2, row * CELL_SIZE + CELL_SIZE / 2, CELL_SIZE / 2, Color.RED);
                root.getChildren().add(circle);
            }
        });
        primaryStage.setTitle("Robot Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();

        timeline.play(); // Start the animation
    }

    // Button controll functions
    private void loadRoom() {
        for (int y = 0; y < GRID_SIZE_Y; y++) {
            for (int x = 0; x < GRID_SIZE_X; x++) {
                Rectangle cell = new Rectangle(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.WHITE);
                cell.setStroke(Color.BLACK);
                root.getChildren().add(cell);
            }
        }

        ArrayList<Obstacle> obstacleList = this.room.getObstacles();
        for (int j = 0; j < obstacleList.size(); j++) {
            Rectangle cell = new Rectangle((obstacleList.get(j)).getPosition().getCol() * CELL_SIZE, (obstacleList.get(j)).getPosition().getRow() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            cell.setFill(Color.WHITE);
            cell.setStroke(Color.BLACK);
            root.getChildren().add(cell);
        }
        this.room.obstacles.clear();


        for (int y = 0; y < GRID_SIZE_Y; y++) {
            for (int x = 0; x < GRID_SIZE_X; x++) {
                root.getChildren().remove(controlledRobotCircle);
                root.getChildren().remove(prgRobotCircle);
                //root.getChildren().remove(controlledRobotCircle);
            }
        }
        String fileName = "./src/main/java/ijaproject23/simulation/Room.txt";
        ArrayList<String> allWords = new ArrayList<>();

        logger.info("Trying to open a file.");
        /*logger.warning("This is a warning message.");
        logger.severe("This is a severe message.");*/

        try {
            FileReader f = new FileReader(fileName);
            BufferedReader b = new BufferedReader(f);
            String line;

            while ((line = b.readLine()) != null) {
                String[] words = line.split("\\s+");

                for (String word : words) {
                    allWords.add(word);
                }
            }

            b.close();
        }
        catch (FileNotFoundException e) {
            logger.severe("File not found.");
        } catch (IOException e) {
            logger.severe("Could not open the file.");
        }

        boolean processingObstacles = false;
        boolean processingControlledRobots = false;
        boolean processingProgramedRobots = false;

        for (int i = 0; i < allWords.size(); i++) {

            String word = allWords.get(i);
            if (processingObstacles) {
                if (word.equals("ControlledRobots:")) {
                    processingObstacles = false;
                    processingControlledRobots = true;
                    processingProgramedRobots = false;
                    i++;
                } else if (word.equals("ProgrammedRobots:")) {
                    processingObstacles = false;
                    processingControlledRobots = false;
                    processingProgramedRobots = true;
                    i++;
                }
                else {

                    if (i + 1 < allWords.size()) {

                        String x = allWords.get(i);
                        String y = allWords.get(i + 1);

                        int col = Integer.parseInt(x);
                        int row = Integer.parseInt(y);

                        Rectangle rect = new Rectangle(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        rect.setFill(Color.PURPLE);
                        root.getChildren().add(rect);
                        this.room.createObstacleAt(row, col, timeClicks);

                        i ++;
                    }
                }
            }
            if (processingControlledRobots) {
                if (word.equals("Obstacles:")) {
                    processingObstacles = true;
                    processingControlledRobots = false;
                    processingProgramedRobots = false;
                    i++;
                } else if (word.equals("ProgrammedRobots:")) {
                    processingObstacles = false;
                    processingControlledRobots = false;
                    processingProgramedRobots = true;
                    i++;
                }
                else {
                    if (i + 2 < allWords.size()) {
                        String x = allWords.get(i);
                        String y = allWords.get(i + 1);
                        String angle = allWords.get(i + 2);

                        int col = Integer.parseInt(x);
                        int row = Integer.parseInt(y);
                        int ang = Integer.parseInt(angle);

                        //Circle circle = new Circle(col * CELL_SIZE + CELL_SIZE / 2, row * CELL_SIZE + CELL_SIZE / 2, CELL_SIZE / 2, Color.RED);
                        //root.getChildren().add(circle);

                        ControlledRobot rbt = new ControlledRobot(new Position(col, row), ang, this.room);
                        this.room.robots.set(0, rbt);
                        this.room.addRobot(rbt);
                        this.ctrlRobot_CNT++;

                        Position initialPosition = rbt.getPosition(); // Get initial position
                        controlledRobotCircle = new Circle(CELL_SIZE / 2, CONTROLLED_ROBOT_COLOR);
                        controlledRobotCircle.setCenterX(initialPosition.getCol() * CELL_SIZE + CELL_SIZE / 2);
                        controlledRobotCircle.setCenterY(initialPosition.getRow() * CELL_SIZE + CELL_SIZE / 2);

                        root.getChildren().addAll(controlledRobotCircle);


                        i+=2;
                    }
                }

            }
            if (processingProgramedRobots) {
                if (word.equals("ControlledRobots:")) {
                    processingObstacles = false;
                    processingControlledRobots = true;
                    processingProgramedRobots = false;
                } else if (word.equals("Obstacles:")) {
                    processingObstacles = true;
                    processingControlledRobots = false;
                    processingProgramedRobots = false;
                }
                else {
                    if (i + 3 < allWords.size()) {

                        this.prgRobotCircle = null;
                        String x = allWords.get(i);
                        String y = allWords.get(i + 1);
                        String angle = allWords.get(i + 2);
                        String distance = allWords.get(i + 3);

                        int col = Integer.parseInt(x);
                        int row = Integer.parseInt(y);
                        int ang = Integer.parseInt(angle);
                        int dist = Integer.parseInt(distance);

                        //Circle circle = new Circle(col * CELL_SIZE + CELL_SIZE / 2, row * CELL_SIZE + CELL_SIZE / 2, CELL_SIZE / 2, Color.BLUE);
                        //root.getChildren().add(circle);

                        ProgrammedRobot rbtPRG = new ProgrammedRobot(new Position(col, row), ang, this.room, dist);
                        this.room.robots.set(1, rbtPRG);
                        this.room.addRobot(rbtPRG);
                        Position initialPositionPRG = rbtPRG.getPosition(); // Get initial position
                        prgRobotCircle = new Circle(CELL_SIZE / 2, PROGRAMMED_ROBOT_COLOR);
                        prgRobotCircle.setCenterX(initialPositionPRG.getCol() * CELL_SIZE + CELL_SIZE / 2);
                        prgRobotCircle.setCenterY(initialPositionPRG.getRow() * CELL_SIZE + CELL_SIZE / 2);

                        // Add the circles to the root after creating the grid
                        root.getChildren().addAll(prgRobotCircle);

                        i += 3;
                    }
                }
            }

            if (word.equals("Obstacles:")) {
                processingObstacles = true;
                processingControlledRobots = false;
                processingProgramedRobots = false;
            }
            if (word.equals("ControlledRobots:")) {
                processingObstacles = false;
                processingControlledRobots = true;
                processingProgramedRobots = false;
            }
            if (word.equals("ProgrammedRobots:")) {
                processingObstacles = false;
                processingControlledRobots = false;
                processingProgramedRobots = true;
            }


        }
        logger.info("The environment was loaded from the file.");
    }

    private Object setCTRL_draw() {
    	if(this.ctrlRobot_CNT > 0) {
    		this.isDrawingCircle = false;
        	this.isDrawingObstacle = false;
        	this.isDrawingControlled = false;
        	return null;
    	}
    	this.isDrawingCircle = false;
    	this.isDrawingObstacle = false;
    	this.isDrawingControlled = true;
		return null;
	}

	private Object setObs_draw() {
    	this.isDrawingCircle = false;
    	this.isDrawingObstacle = true;
    	this.isDrawingControlled  = false;
		return null;
	}

	private Object setRob_draw() {
		this.isDrawingCircle = true;
		this.isDrawingObstacle = false;
		this.isDrawingControlled = false;
		return null;
	}

	// Function for controlling robot movement visualization
	private void moveRobots() {
        ((ControlledRobot) this.room.robots.get(0)).move();
        ((ProgrammedRobot) this.room.robots.get(1)).move();
        redraw();
    }
	
	// Pause controll
    private void togglePause() {

        if (moveBackwards) {
            timeline.pause();
            ArrayList<Integer> ListPositionsProgrammed = ((ProgrammedRobot) this.room.robots.get(1)).getArray();
            ArrayList<Integer> ListPositionsControlled = ((ControlledRobot) this.room.robots.get(0)).getArray();
            redrawBackwards(ListPositionsProgrammed, ListPositionsControlled);
        } else if (moveBackwards == false){
            if (isPaused = !isPaused) {
            timeline.pause();
            } else {
                timeline.play();
            }
        }
    }
    
    // Grid setup
    private void createGrid(Group root) {
        Pane grid = new Pane(); 
        for (int y = 0; y < GRID_SIZE_Y; y++) {
            for (int x = 0; x < GRID_SIZE_X; x++) {
                Rectangle cell = new Rectangle(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.WHITE);
                cell.setStroke(Color.BLACK);
                grid.getChildren().add(cell);
            }
        }
        root.getChildren().add(grid); // Add grid to the root group
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Redraw new robot positions
    // Currently work with two robots for testing purposes
    public void redraw() {

        Position newPosition = ((ControlledRobot) this.room.robots.get(0)).getPosition();
        logger.info("Controlled robot to position: " + newPosition + " Angle: " + ((ControlledRobot) this.room.robots.get(0)).angle());

        if (newPosition.getRow() >= 0 && newPosition.getRow() < GRID_SIZE_Y &&
                newPosition.getCol() >= 0 && newPosition.getCol() < GRID_SIZE_X) {
            root.getChildren().remove(controlledRobotCircle);
            controlledRobotCircle = new Circle(CELL_SIZE / 2, CONTROLLED_ROBOT_COLOR);
            controlledRobotCircle.setCenterX(newPosition.getCol() * CELL_SIZE + CELL_SIZE / 2);
            controlledRobotCircle.setCenterY(newPosition.getRow() * CELL_SIZE + CELL_SIZE / 2);
            root.getChildren().add(controlledRobotCircle);
        }

        Position newPositionPRG = ((ProgrammedRobot) this.room.robots.get(1)).getPosition();
        logger.info("Programmed robot to position: " + newPositionPRG + " Angle: " + ((ControlledRobot) this.room.robots.get(0)).angle());

        if (newPositionPRG.getRow() >= 0 && newPositionPRG.getRow() < GRID_SIZE_Y &&
                newPositionPRG.getCol() >= 0 && newPositionPRG.getCol() < GRID_SIZE_X) {
            root.getChildren().remove(prgRobotCircle);
            prgRobotCircle = new Circle(CELL_SIZE / 2, PROGRAMMED_ROBOT_COLOR);
            prgRobotCircle.setCenterX(newPositionPRG.getCol() * CELL_SIZE + CELL_SIZE / 2);
            prgRobotCircle.setCenterY(newPositionPRG.getRow() * CELL_SIZE + CELL_SIZE / 2);
            root.getChildren().add(prgRobotCircle);
        }
    }

    public void redrawBackwards(ArrayList<Integer> ListPositionsProgrammed, ArrayList<Integer> ListPositionsControlled) {
        int currentPositionIndex = ListPositionsProgrammed.size() - 1;
        int dif = ListPositionsProgrammed.size() - ListPositionsControlled.size();


        final int[] currentPositionIndexWrapper = {currentPositionIndex}; // Wrapper array to make it effectively final
        Timeline backTimeline = new Timeline();
        backTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            int index = currentPositionIndexWrapper[0];

            if (index >= 0) {
                int xProgrammed = ListPositionsProgrammed.get(index - 2);
                int yProgrammed = ListPositionsProgrammed.get(index - 1);

                if (xProgrammed >= 0 && xProgrammed < GRID_SIZE_Y && yProgrammed >= 0 && yProgrammed < GRID_SIZE_X) {
                    root.getChildren().remove(prgRobotCircle);
                    prgRobotCircle = new Circle(CELL_SIZE / 2, PROGRAMMED_ROBOT_COLOR);
                    prgRobotCircle.setCenterX(xProgrammed * CELL_SIZE + CELL_SIZE / 2);
                    prgRobotCircle.setCenterY(yProgrammed * CELL_SIZE + CELL_SIZE / 2);
                    root.getChildren().add(prgRobotCircle);
                }

                if (index - dif <= ListPositionsControlled.size() - 1 && index-dif > 0) {
                    int xControlled = ListPositionsControlled.get(index - dif - 2);
                    int yControlled = ListPositionsControlled.get(index - dif - 1);

                    if (xControlled >= 0 && xControlled < GRID_SIZE_Y && yControlled >= 0 && yControlled < GRID_SIZE_X) {
                        root.getChildren().remove(controlledRobotCircle);
                        controlledRobotCircle = new Circle(CELL_SIZE / 2, CONTROLLED_ROBOT_COLOR);
                        controlledRobotCircle.setCenterX(xControlled * CELL_SIZE + CELL_SIZE / 2);
                        controlledRobotCircle.setCenterY(yControlled * CELL_SIZE + CELL_SIZE / 2);
                        root.getChildren().add(controlledRobotCircle);
                    }
                }

                currentPositionIndexWrapper[0] -= 3;
            } else {
                backTimeline.stop();
                ((ProgrammedRobot) this.room.robots.get(1)).newPos(ListPositionsProgrammed.get(1), ListPositionsProgrammed.get(0), ListPositionsProgrammed.get(2));
                ((ControlledRobot) this.room.robots.get(0)).newPos(ListPositionsControlled.get(1), ListPositionsControlled.get(0), ListPositionsControlled.get(2));
            }
            ArrayList<Obstacle> obstacleList = this.room.getObstacles();
            ArrayList<Obstacle> removeList = new ArrayList<>();

            for (int i = obstacleList.size() - 1; i >= 0; i--) {
                int when = (obstacleList.get(i)).getWhen();
                if (when == timeClicks) {
                    removeList.add(obstacleList.get(i));
                    Rectangle cell = new Rectangle((obstacleList.get(i)).getPosition().getCol() * CELL_SIZE, (obstacleList.get(i)).getPosition().getRow() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    cell.setFill(Color.WHITE);
                    cell.setStroke(Color.BLACK);
                    root.getChildren().add(cell);
                }
            }
            if(removeList != null){
            for (int i = 0; i < removeList.size(); i++){
                obstacleList.remove(removeList.get(i));
            }
            }
            timeClicks--;
        }));
        backTimeline.setCycleCount(Timeline.INDEFINITE);
        backTimeline.play();
    }
}

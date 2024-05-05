package ijaproject23.simulation;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import ijaproject23.robot.ControlledRobot;
import ijaproject23.robot.ProgrammedRobot;
import ijaproject23.robot.Robot;
import ijaproject23.environment.Room;
import ijaproject23.obstacle.Obstacle;
import ijaproject23.position.Position;
import ijaproject23.simulation.LoggerFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class RobotSimulation extends Application {
    private int GRID_SIZE_X = 10; // Number of columns
    private int GRID_SIZE_Y = 10; // Number of rows
    private static final int CELL_SIZE = 50; // Size of each cell
    private static final Color CONTROLLED_ROBOT_COLOR = Color.RED;
    private static final Color PROGRAMMED_ROBOT_COLOR = Color.BLUE;

    private Room room;
    private Group root = new Group();
    // Start application paused
    private boolean isPaused = true;
    private Timeline timeline;
	// Start with no buttons pressed
    private boolean isRemoving = false;
    private boolean isDrawingProgrammed = false;
    private boolean isDrawingObstacle = false;
	private boolean isDrawingControlled = false;
    private int ctrlRobot_CNT = 0;
    private int robot_angle = 0;
    private Pane grid;
    private boolean moveBackwards = false;
	private LoggerFile logger = new LoggerFile();
	private int timeClicks = 0;

    @Override
    public void start(Stage primaryStage) {
	// Parse args as grid size

        List<String> raw = getParameters().getRaw();
        if(raw.size() > 0){
            try {
                this.GRID_SIZE_X = Integer.parseInt(raw.get(raw.size()-2));
            }
            catch (NumberFormatException e)
            {
                logger.severe("Invalid input for GRID_SIZE_X: " + raw.get(raw.size()-2));
                System.err.println("Invalid input for GRID_SIZE_X: " + raw.get(raw.size()-2));
                this.GRID_SIZE_X = 8;
            }
        }
        if(raw.size() > 1){
            try {
                this.GRID_SIZE_Y = Integer.parseInt(raw.get(raw.size()-1));
            }
            catch (NumberFormatException e)
            {
                logger.severe("Invalid input for GRID_SIZE_Y: " + raw.get(raw.size()-1));
                System.err.println("Invalid input for GRID_SIZE_Y: " + raw.get(raw.size()-1));
                this.GRID_SIZE_Y = 8;
            }
        }

        logger.info(raw.toString());

        logger.info("The program started.");
        // Create output scene
        Scene scene;
        if(this.GRID_SIZE_Y * CELL_SIZE + 120 < 600){
            scene = new Scene(root, this.GRID_SIZE_X  * CELL_SIZE + 330, 600);
        }else{
            scene = new Scene(root, this.GRID_SIZE_X * CELL_SIZE + 330, this.GRID_SIZE_Y * CELL_SIZE + 120);
        }
        createGrid(root);

        // Initialize Timeline for continuous animation
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeClicks++;
            if (!isPaused) {

                moveRobots(); // Move robots every second
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // Run indefinitely

        // Create env
        this.room = new Room(this.GRID_SIZE_Y-1, this.GRID_SIZE_X-1, this);
        
        // Handle user input, control first ControlledRobot in environment
        scene.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();
            switch (key) {
                case UP:
                case W:
                    logger.info("Up arrow pressed.");
                	System.out.println("Up arrow pressed.");
                	for (Robot rbt : this.room.robots) {
                		if( rbt instanceof ControlledRobot) {
                			((ControlledRobot) rbt).allow_move();
                			break;
                		}
                	}
                    break;
                case DOWN:
                case S:
                    logger.info("The controlled robot cannot move.");
                	for (Robot rbt : this.room.robots) {
                		if( rbt instanceof ControlledRobot) {
                			((ControlledRobot) rbt).disallow_move();
                			break;
                		}
                	}
                    break;
                case LEFT:
                case A:
                    logger.info("The controlled robot turned left.");
                	for (Robot rbt : this.room.robots) {
                		if( rbt instanceof ControlledRobot) {
                			((ControlledRobot) rbt).turnleft();
                			break;
                		}
                	}
                    break;
                case RIGHT:
                case D:
                    logger.info("The controlled robot turned right.");
                	for (Robot rbt : this.room.robots) {
                		if( rbt instanceof ControlledRobot) {
                			((ControlledRobot) rbt).turnright();
                			break;
                		}
                	}
                    break;
                case SPACE:
                    moveBackwards = false;
                    if (isPaused) {
                        logger.info("The program has been un-paused.");
                    }
                    else {
                        logger.info("The program has been paused.");
                    }
                    togglePause();
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
                default:
                    break;
            }
            event.consume();
        });
        
        
        // Create buttons
        Button addRobotButton = new Button("Add programmed robot");
        addRobotButton.setPrefSize(300, 100);
        addRobotButton.setLayoutX(this.GRID_SIZE_X * CELL_SIZE + 20);
        addRobotButton.setLayoutY(0);
        addRobotButton.setFocusTraversable(false);
        addRobotButton.setOnAction(e -> setRob_draw());
        
        Button addObstacleButton = new Button("Add obstacle");
        addObstacleButton.setPrefSize(300, 100);
        addObstacleButton.setLayoutX(this.GRID_SIZE_X * CELL_SIZE + 20);
        addObstacleButton.setLayoutY(110);
        addObstacleButton.setFocusTraversable(false);
        addObstacleButton.setOnAction(e -> setObs_draw());
        
        Button addControlledRobotButton = new Button("Add user controlled robot");
        addControlledRobotButton.setPrefSize(300, 100);
        addControlledRobotButton.setLayoutX(this.GRID_SIZE_X * CELL_SIZE + 20);
        addControlledRobotButton.setLayoutY(220);
        addControlledRobotButton.setFocusTraversable(false);
        addControlledRobotButton.setOnAction(e -> setCTRL_draw());
        
        Button DeleteButton = new Button("Delete object");
        DeleteButton.setPrefSize(300, 100);
        DeleteButton.setLayoutX(this.GRID_SIZE_X * CELL_SIZE + 20);
        DeleteButton.setLayoutY(330);
        DeleteButton.setFocusTraversable(false);
        DeleteButton.setOnAction(e -> setRemove());

        Button LoadButton = new Button("Load from file");
        LoadButton.setPrefSize(300, 100);
        LoadButton.setLayoutX(this.GRID_SIZE_X * CELL_SIZE + 20);
        LoadButton.setLayoutY(440);
        LoadButton.setFocusTraversable(false);
        LoadButton.setOnAction(e -> loadRoom());
        
        
        // Create a label to display the input box value
        Label angleLabel = new Label("Robot angle:");

        // Create an anchor pane for input box and label
        AnchorPane ap = new AnchorPane();
        ap.setFocusTraversable(false);
        AnchorPane.setLeftAnchor(angleLabel, this.GRID_SIZE_X * CELL_SIZE + 20.0); // Adjust as needed
        AnchorPane.setTopAnchor(angleLabel, 560.0);

        // Create a text field for input
        TextField angleInput = new TextField();
        angleInput.setFocusTraversable(false);
        angleInput.setPrefWidth(60);
        angleInput.setText("0"); // Initial value
        angleInput.setPromptText("0-359"); // Prompt text
        angleInput.setAlignment(Pos.CENTER_RIGHT); // Align text to the right
        angleInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) { // Allow only numeric input
                angleInput.setText(newValue.replaceAll("[^\\d]", ""));
            } else if (newValue.length() > 3 || Integer.parseInt(newValue) > 359) { // Limit input to 0-359
                angleInput.setText(oldValue);
                this.robot_angle = Integer.parseInt(oldValue);
            } else {
            	this.robot_angle = Integer.parseInt(newValue);
            }
        });

        AnchorPane.setLeftAnchor(angleInput, this.GRID_SIZE_X * CELL_SIZE + 120.0); // Adjust as needed
        AnchorPane.setTopAnchor(angleInput, 560.0);

        // Add label and input box to AnchorPane
        ap.getChildren().addAll(angleLabel, angleInput);

        root.getChildren().addAll(addRobotButton, addObstacleButton, addControlledRobotButton, DeleteButton, LoadButton, ap);
        
        // Handle mouse click to draw circles or fill with color
        scene.setOnMouseClicked(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            int col = (int) (mouseX / CELL_SIZE);
            int row = (int) (mouseY / CELL_SIZE);
            if(col < 0 || col >= this.GRID_SIZE_X || row < 0 || row >= this.GRID_SIZE_Y) {
            	//System.err.println("Trying to place an object outside of grid." + col + " " + row);
            }else{
	            if (this.isDrawingProgrammed) {
	            	// Create a programmed robot with view dist 2
	            	logger.info("Checking " + new Position(row, col).toString());
	            	System.out.println("Checking " + new Position(row, col).toString());
	            	if(! this.room.obstacleAt(new Position(row, col)) && ! this.room.robotAt(new Position(row, col))) {
	            		System.out.println("Adding a programmed robot.");
	            		ProgrammedRobot rbtPRG = new ProgrammedRobot(new Position(row, col), this.robot_angle, CELL_SIZE, this.room, 2, timeClicks);
		                rbtPRG.createImageView();
		                this.room.addRobot(rbtPRG);
		                root.getChildren().add(rbtPRG.getImageView());
                        logger.info("Programmed robot has been added to " + new Position(row, col).toString());
                    }else {
                        logger.warning("Position occupied.");
	            		System.out.println("Position occupied.");
	            	}
	            } else if(this.isDrawingObstacle) {
	            	// Create an obstacle
	            	logger.info("Checking " + new Position(row, col).toString());
	            	System.out.println("Checking " + new Position(row, col).toString());
	            	if(! this.room.obstacleAt(new Position(row, col)) && ! this.room.robotAt(new Position(row, col))) {
	            		System.out.println("Adding an obstacle.");
	            		this.room.createObstacleAt(row, col, timeClicks);
		                this.room.obstacles.get(this.room.obstacles.size() - 1).createRectangle(CELL_SIZE);
		                root.getChildren().add(this.room.obstacles.get(this.room.obstacles.size() - 1).rectangle);
                        logger.info("Obstacle has been added to " + new Position(row, col).toString());
                    }else{
                        logger.warning("Position occupied.");
	            		System.out.println("Position occupied.");
	            	}
	            } else if(this.isDrawingControlled) {
	                // Create a controlled robot
	                logger.info("Checking " + new Position(row, col).toString());
	            	System.out.println("Checking " + new Position(row, col).toString());
	            	if(! this.room.obstacleAt(new Position(row, col)) && ! this.room.robotAt(new Position(row, col))) {
	            		System.out.println("Adding a controlled robot.");
	            		ControlledRobot rbt = new ControlledRobot(new Position(row, col), this.robot_angle, CELL_SIZE, this.room, timeClicks);
		                rbt.createImageView();
		                this.room.addRobot(rbt);
		                root.getChildren().addAll(rbt.getImageView());
		                this.ctrlRobot_CNT++;
		                this.isDrawingControlled = false;
                        logger.info("Controlled robot has been added to " + new Position(row, col).toString());
                    }else {
                        logger.warning("Position occupied.");
	            		System.out.println("Position occupied.");
	            	}
	            } else if(this.isRemoving) {
	            	for (Robot rbt : this.room.robots) {
	            		if(rbt.getPosition().equals(new Position(row, col))) {
                            logger.info("Removing robot at " + new Position(row, col).toString());
	            			System.out.println("Removing robot at " + new Position(row, col).toString());
	            			rbt.getImageView().setVisible(false);
	            			System.out.println("RT IM VIEW: " + rbt.getImageView().toString());
	            			root.getChildren().remove(rbt.getImageView());
	            			this.room.robots.remove(rbt);
	            			Rectangle cell = new Rectangle(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                            cell.setFill(Color.WHITE);
                            cell.setStroke(Color.BLACK);
                            root.getChildren().add(cell);
	            			// Refresh the scene
	            			root.requestLayout();
	            			if(rbt instanceof ControlledRobot) {
	            				this.ctrlRobot_CNT--;
	            			}
	            			return;
	            		}
	            	}
	            	// else find object at position
	            	for (Obstacle obs : this.room.obstacles) {
	            		if(obs.getPosition().equals(new Position(row, col))) {
                            logger.info("Removing obstacle at " + new Position(row, col).toString());
	            			System.out.println("Removing obstacle at " + new Position(row, col).toString());
	            			obs.rectangle.setVisible(false);
	            			System.out.println("OB REC: " + obs.rectangle.toString());
	            			root.getChildren().remove(obs.rectangle);
	            			this.room.obstacles.remove(obs);
	            			Rectangle cell = new Rectangle(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                            cell.setFill(Color.WHITE);
                            cell.setStroke(Color.BLACK);
                            root.getChildren().add(cell);
	            			// Refresh the scene
	            			root.requestLayout();
	            			return;
	            		}
	            	}
	            }
            }
        });
        
        primaryStage.setTitle("Robot Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();

        timeline.play(); // Start the animation
    }

    // Button control functions
    private Object setCTRL_draw() {
		this.isDrawingProgrammed = false;
    	this.isDrawingObstacle = false;
    	this.isRemoving = false;
    	this.grid.requestFocus();
    	if(this.ctrlRobot_CNT > 0) {
        	this.isDrawingControlled = false;
        	return null;
    	}
    	this.isDrawingControlled = true;
		return null;
	}

	private Object setObs_draw() {
    	this.isDrawingProgrammed = false;
    	this.isDrawingObstacle = true;
    	this.isDrawingControlled  = false;
    	this.isRemoving = false;
    	this.grid.requestFocus();
		return null;
	}

	private Object setRob_draw() {
		this.isDrawingProgrammed = true;
		this.isDrawingObstacle = false;
		this.isDrawingControlled = false;
		this.isRemoving = false;
    	this.grid.requestFocus();
		return null;
	}
	
	private Object setRemove() {
		this.isDrawingProgrammed = false;
		this.isDrawingObstacle = false;
		this.isDrawingControlled = false;
		this.isRemoving = true;
    	this.grid.requestFocus();
		return null;
	}

    
    private void loadRoom() {
        for (int y = 0; y < this.GRID_SIZE_Y; y++) {
            for (int x = 0; x < this.GRID_SIZE_X; x++) {
                Rectangle cell = new Rectangle(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.WHITE);
                cell.setStroke(Color.BLACK);
                root.getChildren().add(cell);
            }
        }

        ArrayList<Obstacle> obstacleList = this.room.getObstacles();
        for (int j = 0; j < obstacleList.size(); j++) {
            if(obstacleList.get(j).getPosition().getCol() > this.GRID_SIZE_X || obstacleList.get(j).getPosition().getRow() > this.GRID_SIZE_Y){

            }else{
                Rectangle cell = new Rectangle((obstacleList.get(j)).getPosition().getCol() * CELL_SIZE, (obstacleList.get(j)).getPosition().getRow() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.BLACK);
                cell.setStroke(Color.BLACK);
                root.getChildren().add(cell);
            }
        }
        this.room.obstacles.clear();

        for (Robot rbt : this.room.robots) {
            root.getChildren().remove(rbt.getImageView());
        }

        String fileName = "./src/main/java/ijaproject23/simulation/Room.txt";
        ArrayList<String> allWords = new ArrayList<>();

        logger.info("Trying to open a file.");

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

                        if(row > this.GRID_SIZE_Y || col > this.GRID_SIZE_X){}
                        else{
                            this.room.createObstacleAt(row, col, timeClicks);
                            this.room.obstacles.get(this.room.obstacles.size()-1).createRectangle(CELL_SIZE);
                            root.getChildren().add(this.room.obstacles.get(this.room.obstacles.size()-1).rectangle);
                        }
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

                        if(row > this.GRID_SIZE_Y || col > this.GRID_SIZE_X){}
                        else{

                            ControlledRobot rbt = new ControlledRobot(new Position(col, row), ang, CELL_SIZE, this.room, timeClicks);
                            this.room.addRobot(rbt);
                            this.ctrlRobot_CNT++;

                            rbt.createImageView();

                            root.getChildren().addAll(rbt.getImageView());
                        }
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
                        String x = allWords.get(i);
                        String y = allWords.get(i + 1);
                        String angle = allWords.get(i + 2);
                        String distance = allWords.get(i + 3);

                        int col = Integer.parseInt(x);
                        int row = Integer.parseInt(y);
                        int ang = Integer.parseInt(angle);
                        int dist = Integer.parseInt(distance);

                        if(row > this.GRID_SIZE_Y || col > this.GRID_SIZE_X){}
                        else{

                            ProgrammedRobot rbtPRG = new ProgrammedRobot(new Position(col, row), ang, CELL_SIZE, this.room, dist, timeClicks);

                            this.room.addRobot(rbtPRG);
                            
                            rbtPRG.createImageView();

                            root.getChildren().addAll(rbtPRG.getImageView());
                            
                        }

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

	// Function for controlling robot movement
	private void moveRobots() {
        for (Robot rbt : this.room.robots) {
        	rbt.move();
        }
    }
	
	// Pause controll
    private void togglePause() {
        if (moveBackwards) {
            timeline.pause();
            redrawBackwards(this.room.robots);
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
        for (int y = 0; y < this.GRID_SIZE_Y; y++) {
            for (int x = 0; x < this.GRID_SIZE_X; x++) {
                Rectangle cell = new Rectangle(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.WHITE);
                cell.setStroke(Color.BLACK);
                grid.getChildren().add(cell);
            }
        }
        this.grid = grid;
        root.getChildren().add(grid); // Add grid to the root group
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void redraw() {
        for (Robot rbt : this.room.robots) {
            logger.info("Robot " + rbt.toString() + " to position: " + rbt.getPosition() + " Angle: " + rbt.angle());
            Position newPosition = rbt.getPosition();
            Position oldPosition = rbt.getOld();

            // Check if the robot is actually moving
            if (!newPosition.equals(oldPosition)) {
                double oldX = oldPosition.getCol() * CELL_SIZE + CELL_SIZE / 2;
                double oldY = oldPosition.getRow() * CELL_SIZE + CELL_SIZE / 2;
                double newX = newPosition.getCol() * CELL_SIZE + CELL_SIZE / 2;
                double newY = newPosition.getRow() * CELL_SIZE + CELL_SIZE / 2;

                // Calculate intermediate positions
                double deltaX = (newX - oldX) / 10; // Divide the movement into 10 steps
                double deltaY = (newY - oldY) / 10;

                // Create a timeline for animation
                Timeline timeline = new Timeline();
                for (int i = 1; i <= 10; i++) {
                    final double posX = oldX + deltaX * i;
                    final double posY = oldY + deltaY * i;
                    KeyFrame keyFrame = new KeyFrame(Duration.millis(i * 100), event -> {
                        // Update robot's position
                        rbt.getImageView().setLayoutX(posX - CELL_SIZE/2);
                        rbt.getImageView().setLayoutY(posY - CELL_SIZE/2);
                        
                        // Pause
                        if(isPaused) {
                        	timeline.stop();
                        }else {
                        	timeline.play();
                        }
                    });
                    timeline.getKeyFrames().add(keyFrame);
                }
                
                // Add a final key frame to set the position to the destination
                KeyFrame finalKeyFrame = new KeyFrame(Duration.millis(1000), event -> {
                    rbt.getImageView().setLayoutX(newX - CELL_SIZE/2);
                    rbt.getImageView().setLayoutY(newY - CELL_SIZE/2);
                    rbt.setOld(newPosition);
                });
                timeline.getKeyFrames().add(finalKeyFrame);

                if(isPaused) {
                	timeline.stop();
                }else {
                	timeline.play();
                }
            }
        }
    }
    
    public void redrawBackwards(ArrayList<Robot> robots) {
        int currentPositionIndex = robots.get(0).getArray().size()-1;


        final int[] currentPositionIndexWrapper = {currentPositionIndex}; // Wrapper array to make it effectively final
        Timeline backTimeline = new Timeline();
        backTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            int index = currentPositionIndexWrapper[0];
            ArrayList<Robot> removeRobots = new ArrayList<>();

            for(Robot rbt : this.room.robots){
                int when = (rbt.getWhen());
                if (when == timeClicks) {
                    removeRobots.add(rbt);
                    Rectangle cell = new Rectangle(rbt.getPosition().getCol() * CELL_SIZE, rbt.getPosition().getRow() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    cell.setFill(Color.WHITE);
                    cell.setStroke(Color.BLACK);
                    root.getChildren().add(cell);
                } else {

                    int dif = this.room.robots.get(0).getArray().size() - rbt.getArray().size();
                    if(index - dif <= rbt.getArray().size() - 1 && index - dif > 0) {
                        rbt.setPosition(rbt.getArray().get(index - dif - 2), rbt.getArray().get(index - dif - 1));
                        rbt.setAngle(rbt.getArray().get(index - dif));
                        rbt.getImageView().setLayoutX(rbt.getArray().get(index - dif - 2) * CELL_SIZE);
                        rbt.getImageView().setLayoutY(rbt.getArray().get(index - dif - 1) * CELL_SIZE);
                    }
                }
            }
            if(removeRobots != null){
                for (int i = 0; i < removeRobots.size(); i++){
                    robots.remove(removeRobots.get(i));
                }
            }
            currentPositionIndexWrapper[0] -= 3;

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

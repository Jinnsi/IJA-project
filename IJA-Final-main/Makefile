build:
	clear && javac --module-path ./lib/ --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web,javafx.swt --class-path ./src/main/java/ ./src/main/java/ijaproject23/simulation/RobotSimulation.java -d ./target/classes
	clear ; java -Dprism.order=sw --module-path ./lib/ --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web,javafx.swt --add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED --add-exports javafx.base/com.sun.javafx.event=ALL-UNNAMED -cp ./target/classes ijaproject23.simulation.RobotSimulation

run:
	clear ; java -Dprism.order=sw --module-path ./lib/ --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web,javafx.swt --add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED --add-exports javafx.base/com.sun.javafx.event=ALL-UNNAMED -cp ./target/classes ijaproject23.simulation.RobotSimulation

compile:
	clear && javac --module-path ./lib/ --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web,javafx.swt --class-path ./src/main/java/ ./src/main/java/ijaproject23/simulation/RobotSimulation.java

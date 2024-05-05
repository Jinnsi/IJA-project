Funguje vykresleni, pohyb (chybi animace prechodu mezi policky; programmedRobot ma nejakou chybu v pohybu), pauza, tlacitka pro pridavani objektu (musi se pridat check, jestli je pole volny). Je tam defaultne testovaci ProgrammedRobot a ControlledRobot a jsou podle toho i psany fce (napr. moveRobots - budou se muset upravit, aby pracovali se vsema robotama v this.room.robots). Dale se asi budou muset objekty pro vizualizaci (Circle) dat jako atribut k robotum, at je to prehlednejsi a jednodussi, a pridat ukazatele natoceni robotu. 

V lib chybi libjfxwebkit.so z javafx. Jestli je to potreba nevim. Nesel nahrat.

build:
clear && javac --module-path ./lib/ --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web,javafx.swt --class-path ./src/main/java/ ./src/main/java/ijaproject23/simulation/RobotSimulation.java
run:
clear ; java -Dprism.order=sw --module-path ./lib/ --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web,javafx.swt --add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED --add-exports javafx.base/com.sun.javafx.event=ALL-UNNAMED -cp ./target/classes ijaproject23.simulation.RobotSimulation

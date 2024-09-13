module group.battleship {
    requires javafx.controls;
    requires transitive javafx.graphics;


    opens group.battleship to javafx.fxml;
    exports group.battleship;
    exports group.battleship.domain;
    exports group.battleship.logic;
    opens group.battleship.logic to javafx.fxml;
    exports group.battleship.ui;
    opens group.battleship.ui to javafx.fxml;
}
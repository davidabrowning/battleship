package group.battleship.ui;

import java.util.ArrayList;
import java.util.List;

import group.battleship.logic.GameController;
import group.battleship.domain.*;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;



public class GameApplication extends Application {

    private final GameController gameController;
    private Stage stage;
    private Scene newPlayerScene;
    private Scene shipPlacementScene;
    private Scene gameplayScene;

    public GameApplication() {
        gameController = new GameController();
    }

    public void go() {
        Application.launch(GameApplication.class);
    }

    @Override
    public void start(Stage stage) {
        // Create the Scenes
        newPlayerScene = createNewPlayerScene();
        shipPlacementScene = null;
        gameplayScene = null;

        // Configure the Stage
        this.stage = stage;
        stage.setTitle("Desktop Battleship");
        stage.setScene(newPlayerScene);
        stage.show();
    }

    // This method creates the Scene where new players enter their names
    private Scene createNewPlayerScene() {
        // Configure player name Label
        Label newPlayerLabel = new Label("Player 1:");
        newPlayerLabel.setFont(Style.FONT_DEFAULT);

        // Configure player name input
        TextField newPlayerTextField = new TextField();
        newPlayerTextField.setFont(Style.FONT_DEFAULT);

        // Configure submit
        Button submitButton = new Button("Submit");
        submitButton.setFont(Style.FONT_DEFAULT);

        // On submit action...
        submitButton.setOnAction(event -> {

            // Create player
            gameController.createPlayer(newPlayerTextField.getText());

            // If < 2 players... get second player
            // Else... ask players to place ships
            if (gameController.getNumPlayers() < 2) {
                newPlayerLabel.setText("Player 2:");
                newPlayerTextField.clear();
                newPlayerTextField.requestFocus();
            } else {
                stage.setScene(createShipPlacementScene());
            }
        });

        // Configure container
        HBox newPlayerHBox = new HBox();
        newPlayerHBox.setMinHeight(Style.MIN_LAYOUT_HEIGHT);
        newPlayerHBox.setMinWidth(Style.MIN_LAYOUT_WIDTH);
        newPlayerHBox.setAlignment(Pos.CENTER);
        newPlayerHBox.setPadding(Style.INSETS_DEFAULT);
        newPlayerHBox.setSpacing(Style.SPACING_DEFAULT);
        newPlayerHBox.getChildren().addAll(newPlayerLabel, newPlayerTextField, submitButton);

        return new Scene(newPlayerHBox);
    }

    // This method creates the Scene where players place their Ships
    // at the beginning of the game
    private Scene createShipPlacementScene() {

        // If all Ships are placed, advance to gameplay Scene
        if (gameController.allShipsArePlaced()) {
            gameplayScene = createGameplayScene();
            stage.setScene(gameplayScene);
            return gameplayScene;
        }

        // If this active Player has placed all of their Ships, then swap to the other Player
        if (gameController.allShipsArePlaced(gameController.getActivePlayer())) {
            gameController.swapActivePlayer();
        }

        // Get Player info
        Player player = gameController.getActivePlayer();
        Fleet fleet = player.getFleet();
        Ship ship = gameController.getFirstUnplacedShip(player);
        List<Button> placeShipTiles = new ArrayList<>();

        // Create instruction label
        Label placeShipLabel = new Label(player + ", please place your " + ship + " (R to rotate):");
        placeShipLabel.setFont(Style.FONT_DEFAULT);

        // Create input grid
        GridPane placeShipsGrid = new GridPane();
        placeShipsGrid.setAlignment(Pos.CENTER);
        for (int i = 0; i < 100; i++) {
            int tileNum = i;

            // Configure this location Button
            Button shipButton = new Button();
            shipButton.setMinWidth(50);
            shipButton.setMinHeight(50);
            shipButton.setBorder(new Border(Style.BORDER_BLACK));

            // Set already placed Ships to GRAY
            if (player.getFleet().containsLocation(tileNum)) {
                shipButton.setBackground(Background.fill(Color.DARKGRAY));
            } else {
                shipButton.setBackground(Background.fill(Color.LIGHTBLUE));
            }

            // Add this Button to the Ship placement grid
            GridPane.setRowIndex(shipButton, tileNum / 10);
            GridPane.setColumnIndex(shipButton, tileNum % 10);
            placeShipsGrid.getChildren().add(shipButton);
            placeShipTiles.add(shipButton);

            // On hover...
            shipButton.setOnMouseEntered(event -> {
                // Set other Buttons back to WHITE
                for (int k = 0; k < 100; k++) {
                    if (!fleet.containsLocation(k)) {
                        placeShipTiles.get(k).setBackground(Background.fill(Color.LIGHTBLUE));
                    }
                }
                // Set this Button to GRAY
                if (gameController.isValidShipPlacementLocation(tileNum, ship.getSize(), fleet)) {
                    for (int j = 0; j < ship.getSize(); j++) {
                        placeShipTiles.get(tileNum + j).setBackground(Background.fill(Color.GRAY));
                    }
                }
            });

            // On click...
            shipButton.setOnAction(event -> {
                // Place the Ship at this location and set the Scene to place the next Ship
                if (gameController.isValidShipPlacementLocation(tileNum, ship.getSize(), fleet)) {
                    gameController.placeShip(ship, tileNum);
                    shipPlacementScene = createShipPlacementScene();
                    stage.setScene(shipPlacementScene);
                }
            });

        }

        // Put Label and input grid on layout
        VBox shipPlacementLayout = new VBox();
        shipPlacementLayout.setMinHeight(Style.MIN_LAYOUT_HEIGHT);
        shipPlacementLayout.setMinWidth(Style.MIN_LAYOUT_WIDTH);
        shipPlacementLayout.setAlignment(Pos.CENTER);
        shipPlacementLayout.setSpacing(Style.SPACING_DEFAULT);
        shipPlacementLayout.setPadding(Style.INSETS_DEFAULT);
        shipPlacementLayout.getChildren().addAll(placeShipLabel, placeShipsGrid);

        return new Scene(shipPlacementLayout);
    }

    // This method creates the Scene where both Players attempt to hit each other's Ships
    private Scene createGameplayScene() {
        // Update logic
        gameController.setActivePlayer(gameController.getPlayers().get(0));

        // Create components: Instruction Labels
        Label playerOneAttackLabel = new Label(gameController.getPlayers().get(0) + "'s attempts:");
        Label playerTwoAttackLabel = new Label(gameController.getPlayers().get(1) + "'s attempts:");
        playerOneAttackLabel.setFont(Style.FONT_DEFAULT);
        playerTwoAttackLabel.setFont(Style.FONT_DEFAULT);

        // Create components: Sea grids where other player's Ships are hiding
        GridPane playerOneGameplayGrid = createGameplayGrid(gameController.getPlayers().get(0));
        GridPane playerTwoGameplayGrid = createGameplayGrid(gameController.getPlayers().get(1));

        // Create components: Container for Player 1's Label and grid
        VBox playerOneAttackVBox = new VBox();
        playerOneAttackVBox.setAlignment(Pos.CENTER);
        playerOneAttackVBox.setPadding(Style.INSETS_LARGE);
        playerOneAttackVBox.setSpacing(Style.SPACING_LARGE);
        playerOneAttackVBox.getChildren().addAll(playerOneAttackLabel, playerOneGameplayGrid);

        // Create components: Container for Player 2's Label and grid
        VBox playerTwoAttackVBox = new VBox();
        playerTwoAttackVBox.setAlignment(Pos.CENTER);
        playerTwoAttackVBox.setPadding(Style.INSETS_LARGE);
        playerTwoAttackVBox.setSpacing(Style.SPACING_LARGE);
        playerTwoAttackVBox.getChildren().addAll(playerTwoAttackLabel, playerTwoGameplayGrid);

        // Add components to layout
        HBox gameplayLayout = new HBox();
        gameplayLayout.setSpacing(Style.SPACING_DEFAULT);
        gameplayLayout.setPadding(Style.INSETS_DEFAULT);
        gameplayLayout.getChildren().addAll(playerOneAttackVBox, playerTwoAttackVBox);

        return new Scene(gameplayLayout);
    }

    // This method creates the grid where the other Player's ships are hiding
    private GridPane createGameplayGrid(Player player) {

        Player otherPlayer = gameController.getOtherPlayer(player);
        Fleet otherFleet = otherPlayer.getFleet();
        List<Button> seaButtons = new ArrayList<>();

        // Create input grid
        GridPane gameplayGrid = new GridPane();
        gameplayGrid.setAlignment(Pos.CENTER);
        for (int i = 0; i < 100; i++) {
            int tileNum = i;

            // Create and configure this button
            Button seaButton = new Button();
            seaButton.setFont(Style.FONT_SMALL);
            seaButton.setMinWidth(50);
            seaButton.setMinHeight(50);
            seaButton.setBackground(Background.fill(Color.LIGHTBLUE));
            seaButton.setBorder(new Border(Style.BORDER_BLACK));

            // Add this Button to the grid and the List of Buttons
            GridPane.setRowIndex(seaButton, tileNum / 10);
            GridPane.setColumnIndex(seaButton, tileNum % 10);
            gameplayGrid.getChildren().add(seaButton);
            seaButtons.add(seaButton);

            // On hover...
            seaButton.setOnMouseEntered(event -> {
                // If game is over, don't do anything
                if (gameController.isGameOver()) {
                    return;
                }

                if (gameController.getActivePlayer() == player) {
                    // Reset button colors
                    for (int j = 0; j < 100; j++) {
                        if (otherFleet.containsHitLocation(j)) {
                            continue;
                        } else if (otherPlayer.getShotsSustained().contains(j)) {
                            continue;
                        } else if (otherFleet.containsLocation(j)) {
                            seaButtons.get(j).setBackground(Background.fill(Color.LIGHTBLUE));
                        } else {
                            seaButtons.get(j).setBackground(Background.fill(Color.LIGHTBLUE));
                        }
                    }

                    // Update the hovered button's color
                    if (!otherFleet.containsHitLocation(tileNum) && !otherPlayer.getShotsSustained().contains(tileNum)) {
                        seaButton.setBackground(Background.fill(Color.ORANGE));
                    }
                }
            });

            seaButton.setOnAction(event -> {
                if (gameController.isGameOver()) {
                    return;
                }

                if (gameController.getActivePlayer() != player) {
                    return;
                }

                if (otherFleet.containsHitLocation(tileNum)) {
                    return;
                }

                if (otherPlayer.getShotsSustained().contains(tileNum)) {
                    return;
                }

                gameController.processAttack(otherPlayer, tileNum);
                if (otherFleet.containsHitLocation(tileNum)) {
                    seaButton.setBackground(Background.fill(Color.RED));
                } else {
                    seaButton.setBackground(Background.fill(Color.WHITE));
                }

                // Change any sunk ships to dark red
                for (int k = 0; k < 100; k++) {
                    if (otherFleet.containsSunkShip(k)) {
                        if (gameController.isGameOver()) {
                            seaButtons.get(k).setBackground(Background.fill(Color.GREEN));
                        } else {
                            seaButtons.get(k).setBackground(Background.fill(Color.DARKRED));
                        }
                    }
                }
            });

        }
        return gameplayGrid;
    }
}
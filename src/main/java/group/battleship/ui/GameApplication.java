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

    // This method runs when the Application starts
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        buildGUI();
    }

    public void buildGUI() {
        buildScenes();
        buildStage();
    }

    public void buildScenes() {
        buildNewPlayerScene();
        buildShipPlacementScene();
        buildGameplayScene();
    }

    public void buildStage() {
        stage.setTitle("Desktop Battleship");
        stage.setScene(newPlayerScene);
        stage.show();
    }


    // This method creates the Scene where new players enter their names
    private void buildNewPlayerScene() {

        // Configure components
        Label newPlayerLabel = new Label("Player 1:");
        newPlayerLabel.setFont(Style.FONT_DEFAULT);
        TextField newPlayerTextField = new TextField();
        newPlayerTextField.setFont(Style.FONT_DEFAULT);
        Button submitButton = createNewPlayerSubmitButton(newPlayerLabel, newPlayerTextField);

        // Configure container
        HBox newPlayerHBox = new HBox();
        newPlayerHBox.setMinHeight(Style.MIN_LAYOUT_HEIGHT);
        newPlayerHBox.setMinWidth(Style.MIN_LAYOUT_WIDTH);
        newPlayerHBox.setAlignment(Pos.CENTER);
        newPlayerHBox.setPadding(Style.INSETS_DEFAULT);
        newPlayerHBox.setSpacing(Style.SPACING_DEFAULT);
        newPlayerHBox.getChildren().addAll(newPlayerLabel, newPlayerTextField, submitButton);

        stage.setScene(new Scene(newPlayerHBox));
    }

    // This method creates the new Player submit Button
    private Button createNewPlayerSubmitButton(Label newPlayerLabel, TextField newPlayerTextField) {

        Button submitButton = new Button("Submit");
        submitButton.setFont(Style.FONT_DEFAULT);
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
                stage.setScene(shipPlacementScene);
            }
        });

        return submitButton;
    }

    // This method creates the Scene where players place their Ships
    // at the beginning of the game
    private void buildShipPlacementScene() {

        // Swap active Player if necessary
        if (gameController.allShipsArePlaced(gameController.getActivePlayer())) {
            gameController.swapActivePlayer();
        }

        // Get Player info
        Player player = gameController.getActivePlayer();
        Fleet fleet = player.getFleet();
        Ship ship = gameController.getFirstUnplacedShip(player);
        List<Button> seaTiles = new ArrayList<>();

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
            shipButton.setBackground(Background.fill(Color.LIGHTBLUE));
            shipButton.setBorder(new Border(Style.BORDER_BLACK));

            // Add this Button to the Ship placement grid
            GridPane.setRowIndex(shipButton, tileNum / 10);
            GridPane.setColumnIndex(shipButton, tileNum % 10);
            placeShipsGrid.getChildren().add(shipButton);
            seaTiles.add(shipButton);

            // On hover, reset open tiles and show updated Ship placement shadow
            shipButton.setOnMouseEntered(event -> {
                resetOpenSeaTilesDuringShipPlacement(fleet, seaTiles);
                if (gameController.isValidShipPlacementLocation(tileNum, ship.getSize(), fleet)) {
                    showPotentialShipPlacementShadow(player, ship, tileNum, seaTiles);
                }
            });

            // On click...
            shipButton.setOnAction(event -> {
                // Place the Ship at this location and set the Scene to place the next Ship
                if (gameController.isValidShipPlacementLocation(tileNum, ship.getSize(), fleet)) {
                    gameController.placeShip(ship, tileNum);
                    if (gameController.allShipsArePlaced()) {
                        stage.setScene(gameplayScene);
                    } else {
                        // Redraw shipPlacement Scene with updated Ship values
                        stage.setScene(shipPlacementScene);
                    }
                }
            });

        }

        // Update already placed tiles to GRAY
        updatePlacedShipTilesToGray(player, seaTiles);

        // Put Label and input grid on layout
        VBox shipPlacementLayout = new VBox();
        shipPlacementLayout.setMinHeight(Style.MIN_LAYOUT_HEIGHT);
        shipPlacementLayout.setMinWidth(Style.MIN_LAYOUT_WIDTH);
        shipPlacementLayout.setAlignment(Pos.CENTER);
        shipPlacementLayout.setSpacing(Style.SPACING_DEFAULT);
        shipPlacementLayout.setPadding(Style.INSETS_DEFAULT);
        shipPlacementLayout.getChildren().addAll(placeShipLabel, placeShipsGrid);

        shipPlacementScene = new Scene(shipPlacementLayout);

        shipPlacementScene.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("R")) {
                gameController.rotateShipPlacement(player);
            }
        });

        stage.setScene(shipPlacementScene);
    }

    public void resetOpenSeaTilesDuringShipPlacement(Fleet f, List<Button> seaTiles) {
        for (int k = 0; k < 100; k++) {
            if (!f.containsLocation(k)) {
                seaTiles.get(k).setBackground(Background.fill(Color.LIGHTBLUE));
            }
        }
    }

    public void showPotentialShipPlacementShadow(Player p, Ship s, int tileNum, List<Button> seaTiles) {
        if (gameController.placeShipHorizontally(p)) {
            for (int j = 0; j < s.getSize(); j++) {
                seaTiles.get(tileNum + j).setBackground(Background.fill(Color.GRAY));
            }
        } else {
            for (int j = 0; j < s.getSize(); j++) {
                seaTiles.get(tileNum + j * 10).setBackground(Background.fill(Color.GRAY));
            }
        }
    }

    public void updatePlacedShipTilesToGray(Player p, List<Button> seaTiles) {
        for (int i = 0; i < 100; i++) {
            if (p.getFleet().containsLocation(i)) {
                seaTiles.get(i).setBackground(Background.fill(Color.DARKGRAY));
            } else {
                seaTiles.get(i).setBackground(Background.fill(Color.LIGHTBLUE));
            }
        }
    }

    // This method creates the Scene where both Players attempt to hit each other's Ships
    private void buildGameplayScene() {
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
        gameplayLayout.setMinWidth(Style.MIN_LAYOUT_WIDTH);
        gameplayLayout.setMinHeight(Style.MIN_LAYOUT_HEIGHT);
        gameplayLayout.setSpacing(Style.SPACING_DEFAULT);
        gameplayLayout.setPadding(Style.INSETS_DEFAULT);
        gameplayLayout.setAlignment(Pos.CENTER);
        gameplayLayout.getChildren().addAll(playerOneAttackVBox, playerTwoAttackVBox);

        gameplayScene = new Scene(gameplayLayout);
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
                    // Set any unhit tiles to GRAY
                    for (int j = 0; j < 100; j++) {
                        if (otherFleet.containsLocation(j) && !otherFleet.containsHitLocation(j)) {
                            seaButtons.get(j).setBackground(Background.fill(Color.GRAY));
                        }
                    }
                    return;
                }

                // If it is actually this Player's turn right now...
                if (gameController.getActivePlayer() == player) {
                    // Reset button colors
                    for (int j = 0; j < 100; j++) {

                        // Reset unattempted tiles to LIGHTBLUE
                        if (!otherPlayer.getShotsSustained().contains(j)) {
                            seaButtons.get(j).setBackground(Background.fill(Color.LIGHTBLUE));
                        }
                    }

                    // Update the hovered Button's color to ORANGE
                    if (!otherPlayer.getShotsSustained().contains(tileNum)) {
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

                if (otherPlayer.getShotsSustained().contains(tileNum)) {
                    return;
                }

                gameController.processAttack(otherPlayer, tileNum);
                if (otherFleet.containsHitLocation(tileNum)) {
                    seaButton.setBackground(Background.fill(Color.RED));
                } else {
                    seaButton.setBackground(Background.fill(Color.WHITE));
                }

                // Update Ship colors
                for (int k = 0; k < 100; k++) {
                    if (gameController.isGameOver()) {
                        if (otherFleet.containsSunkShip(k)) {
                            seaButtons.get(k).setBackground(Background.fill(Color.GREEN));
                        }

                    } else {
                        if (otherFleet.containsSunkShip(k)) {
                            seaButtons.get(k).setBackground(Background.fill(Color.DARKRED));
                        }
                    }
                }
            });

        }
        return gameplayGrid;
    }
}
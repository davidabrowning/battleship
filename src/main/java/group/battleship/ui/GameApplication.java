package group.battleship.ui;

import java.util.ArrayList;
import java.util.List;

import group.battleship.logic.GameController;
import group.battleship.domain.*;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
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
    private HBox newPlayerHBox;
    private Label newPlayerLabel;
    private TextField newPlayerTextField;
    private Button newPlayerSubmitButton;

    private Scene shipPlacementScene;
    private VBox shipPlacementLayout;
    private Label placeShipLabel;
    private List<Button> shipPlacementSeaTiles;
    private GridPane shipPlacementGridPane;
    private Player playerToPlace;
    private Fleet fleetToPlace;
    private Ship shipToPlace;

    private Scene gameplayScene;
    private HBox gameplayLayout;
    private Label[] gameplayAttackLabels;
    private GridPane[] gameplayGrids;
    private VBox playerOneAttackVBox;
    private VBox playerTwoAttackVBox;

    public GameApplication() {
        gameController = new GameController();
    }

    public void go() {
        Application.launch(GameApplication.class);
    }

    // This method runs when the Application launches
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        buildGUI();
    }

    public void buildGUI() {
        buildNewPlayerScene();
        stage.setTitle("Desktop Battleship");
        stage.setScene(newPlayerScene);
        stage.show();
    }

    private void buildNewPlayerScene() {
        buildNewPlayerSubmissionInput();
        buildNewPlayerLayout();
        newPlayerScene = new Scene(newPlayerHBox);
    }

    public void buildNewPlayerSubmissionInput() {
        newPlayerLabel = new Label("Player 1:");
        newPlayerLabel.setFont(Style.FONT_DEFAULT);
        newPlayerTextField = new TextField();
        newPlayerTextField.setFont(Style.FONT_DEFAULT);
        newPlayerSubmitButton = new Button("Submit");
        newPlayerSubmitButton.setFont(Style.FONT_DEFAULT);
        newPlayerSubmitButton.setOnAction(event -> handleCreateNewPlayerSubmitClick());
    }

    public void handleCreateNewPlayerSubmitClick() {
        gameController.createPlayer(newPlayerTextField.getText());

        if (gameController.getNumPlayers() < 2) {
            newPlayerLabel.setText("Player 2:");
            newPlayerTextField.clear();
            newPlayerTextField.requestFocus();
            return;
        }

        buildShipPlacementScene();
        stage.setScene(shipPlacementScene);
    }

    private void buildNewPlayerLayout() {
        newPlayerHBox = new HBox();
        newPlayerHBox.setMinHeight(Style.MIN_LAYOUT_HEIGHT);
        newPlayerHBox.setMinWidth(Style.MIN_LAYOUT_WIDTH);
        newPlayerHBox.setAlignment(Pos.CENTER);
        newPlayerHBox.setPadding(Style.INSETS_DEFAULT);
        newPlayerHBox.setSpacing(Style.SPACING_DEFAULT);
        newPlayerHBox.getChildren().addAll(newPlayerLabel, newPlayerTextField, newPlayerSubmitButton);
    }

    private void buildShipPlacementScene() {

        shipPlacementSeaTiles = new ArrayList<Button>();

        swapActiveShipPlacementPlayerIfNecessary();
        updatePlayerAndFleetAndShipPlacementValues();
        buildShipPlacementInstructionLabel();
        buildShipPlacementGridPane();
        updatePlacedShipTilesToGray();
        buildShipPlacementLayout();
        shipPlacementScene = new Scene(shipPlacementLayout);
        shipPlacementScene.setOnKeyPressed(event -> handleShipPlacementKeyboardEntry(event));
        stage.setScene(shipPlacementScene);
    }

    private void swapActiveShipPlacementPlayerIfNecessary() {
        if (gameController.allShipsArePlaced(gameController.getActivePlayer())) {
            gameController.swapActivePlayer();
        }
    }

    private void updatePlayerAndFleetAndShipPlacementValues() {
        playerToPlace = gameController.getActivePlayer();
        fleetToPlace = playerToPlace.getFleet();
        shipToPlace = gameController.getFirstUnplacedShip(playerToPlace);
    }

    private void buildShipPlacementInstructionLabel() {
        Player p = gameController.getActivePlayer();
        Ship s = gameController.getFirstUnplacedShip(p);
        placeShipLabel = new Label(p + ", please place your " + s + " (R to rotate):");
        placeShipLabel.setFont(Style.FONT_DEFAULT);
    }

    private void buildShipPlacementGridPane() {
        // Create input grid
        shipPlacementGridPane = new GridPane();
        shipPlacementGridPane.setAlignment(Pos.CENTER);
        for (int i = 0; i < 100; i++) {
            buildShipPlacementSeaTileButton(i);
        }
    }

    private void buildShipPlacementSeaTileButton(int tileNum) {
        Button shipButton = new Button();
        shipButton.setMinWidth(50);
        shipButton.setMinHeight(50);
        shipButton.setBackground(Background.fill(Color.LIGHTBLUE));
        shipButton.setBorder(new Border(Style.BORDER_BLACK));

        // Add this Button to the Ship placement grid
        GridPane.setRowIndex(shipButton, tileNum / 10);
        GridPane.setColumnIndex(shipButton, tileNum % 10);
        shipPlacementGridPane.getChildren().add(shipButton);
        shipPlacementSeaTiles.add(shipButton);

        shipButton.setOnMouseEntered(event -> handleShipPlacementSeaTileButtonHoverEvent(tileNum));
        shipButton.setOnAction(event -> handleShipPlacementSeaTileButtonClick(tileNum));
    }

    private void handleShipPlacementSeaTileButtonHoverEvent(int tileNum) {
        resetOpenSeaTilesDuringShipPlacement(fleetToPlace, shipPlacementSeaTiles);
        if (gameController.isValidShipPlacementLocation(tileNum, shipToPlace.getSize(), fleetToPlace)) {
            showPotentialShipPlacementShadow(playerToPlace, shipToPlace, tileNum, shipPlacementSeaTiles);
        }
    }

    private void handleShipPlacementSeaTileButtonClick(int tileNum) {
        // If invalid location, do nothing
        if (!gameController.isValidShipPlacementLocation(tileNum, shipToPlace.getSize(), fleetToPlace)) {
            return;
        }

        // Else, place ship and either advance to gameplay
        // or redraw shipPlacement Scene with updated Ship values
        gameController.placeShip(shipToPlace, tileNum);
        if (gameController.allShipsArePlaced()) {
            buildGameplayScene();
            stage.setScene(gameplayScene);
        } else {
            buildShipPlacementScene();
            stage.setScene(shipPlacementScene);
        }
    }

    private void resetOpenSeaTilesDuringShipPlacement(Fleet f, List<Button> seaTiles) {
        for (int k = 0; k < 100; k++) {
            if (!f.containsLocation(k)) {
                seaTiles.get(k).setBackground(Background.fill(Color.LIGHTBLUE));
            }
        }
    }

    private void showPotentialShipPlacementShadow(Player p, Ship s, int tileNum, List<Button> seaTiles) {
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

    private void updatePlacedShipTilesToGray() {
        for (int i = 0; i < 100; i++) {
            if (fleetToPlace.containsLocation(i)) {
                shipPlacementSeaTiles.get(i).setBackground(Background.fill(Color.DARKGRAY));
            } else {
                shipPlacementSeaTiles.get(i).setBackground(Background.fill(Color.LIGHTBLUE));
            }
        }
    }

    private void buildShipPlacementLayout() {
        shipPlacementLayout = new VBox();
        shipPlacementLayout.setMinHeight(Style.MIN_LAYOUT_HEIGHT);
        shipPlacementLayout.setMinWidth(Style.MIN_LAYOUT_WIDTH);
        shipPlacementLayout.setAlignment(Pos.CENTER);
        shipPlacementLayout.setSpacing(Style.SPACING_DEFAULT);
        shipPlacementLayout.setPadding(Style.INSETS_DEFAULT);
        shipPlacementLayout.getChildren().addAll(placeShipLabel, shipPlacementGridPane);
    }

    public void handleShipPlacementKeyboardEntry(KeyEvent k) {
        if (k.getCode().toString().equals("R")) {
            gameController.rotateShipPlacement(playerToPlace);
        }
    }

    // This method creates the Scene where both Players attempt to hit each other's Ships
    private void buildGameplayScene() {
        // Update logic
        gameController.setActivePlayer(gameController.getPlayers().get(0));
        buildGameplayInputs();
        buildAndGetGameplayLayout();
        gameplayScene = new Scene(gameplayLayout);
    }

    private void buildGameplayInputs() {
        // Create components: Instruction Labels
        gameplayAttackLabels = new Label[2];
        gameplayAttackLabels[0] = new Label(gameController.getPlayers().get(0) + "'s attempts:");
        gameplayAttackLabels[1] = new Label(gameController.getPlayers().get(1) + "'s attempts:");
        gameplayAttackLabels[0].setFont(Style.FONT_DEFAULT);
        gameplayAttackLabels[1].setFont(Style.FONT_DEFAULT);

        // Create components: Sea grids where other player's Ships are hiding
        gameplayGrids = new GridPane[2];
        buildGameplayGrid(0);
        buildGameplayGrid(1);
    }

    private void buildAndGetGameplayLayout() {
        buildPlayerAttackVBoxes();
        buildGameplayLayout();
    }

    private void buildPlayerAttackVBoxes() {
        // Create components: Container for Player 1's Label and grid
        playerOneAttackVBox = new VBox();
        playerOneAttackVBox.setAlignment(Pos.CENTER);
        playerOneAttackVBox.setPadding(Style.INSETS_LARGE);
        playerOneAttackVBox.setSpacing(Style.SPACING_LARGE);
        playerOneAttackVBox.getChildren().addAll(gameplayAttackLabels[0], gameplayGrids[0]);

        // Create components: Container for Player 2's Label and grid
        playerTwoAttackVBox = new VBox();
        playerTwoAttackVBox.setAlignment(Pos.CENTER);
        playerTwoAttackVBox.setPadding(Style.INSETS_LARGE);
        playerTwoAttackVBox.setSpacing(Style.SPACING_LARGE);
        playerTwoAttackVBox.getChildren().addAll(gameplayAttackLabels[1], gameplayGrids[1]);
    }

    private void buildGameplayLayout() {
        // Add components to layout
        gameplayLayout = new HBox();
        gameplayLayout.setMinWidth(Style.MIN_LAYOUT_WIDTH);
        gameplayLayout.setMinHeight(Style.MIN_LAYOUT_HEIGHT);
        gameplayLayout.setSpacing(Style.SPACING_DEFAULT);
        gameplayLayout.setPadding(Style.INSETS_DEFAULT);
        gameplayLayout.setAlignment(Pos.CENTER);
        gameplayLayout.getChildren().addAll(playerOneAttackVBox, playerTwoAttackVBox);
    }

    // This method creates the grid where the other Player's ships are hiding
    private void buildGameplayGrid(int playerNum) {

        Player thisPlayer = gameController.getPlayers().get(playerNum);
        Player otherPlayer = gameController.getOtherPlayer(thisPlayer);
        Fleet otherFleet = otherPlayer.getFleet();
        List<Button> seaButtons = new ArrayList<>();

        // Create input grid
        gameplayGrids[playerNum] = new GridPane();
        gameplayGrids[playerNum].setAlignment(Pos.CENTER);
        for (int i = 0; i < 100; i++) {
            buildAttackSeaTileButton(playerNum, otherPlayer, i, seaButtons);
        }
    }

    private void buildAttackSeaTileButton(int attackingPlayerNum, Player attackedPlayer, int tileNum, List<Button> seaButtons) {
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
        gameplayGrids[attackingPlayerNum].getChildren().add(seaButton);
        seaButtons.add(seaButton);

        seaButton.setOnMouseEntered(event -> handleAttackingMouseHover(attackedPlayer, tileNum, seaButtons));
        seaButton.setOnAction(event -> handleAttackingMouseClick(attackedPlayer, tileNum, seaButtons));
    }

    private void handleAttackingMouseHover(Player attackedPlayer, int tileNum, List<Button> seaButtons) {
        Fleet otherFleet = attackedPlayer.getFleet();

        // If it is not this player's turn, quit
        if (gameController.getActivePlayer() == attackedPlayer) {
            return;
        }

        // If game is over, quit
        if (gameController.isGameOver()) {
            // Set any unhit tiles to GRAY
            for (int j = 0; j < 100; j++) {
                if (otherFleet.containsLocation(j) && !otherFleet.containsHitLocation(j)) {
                    seaButtons.get(j).setBackground(Background.fill(Color.GRAY));
                }
            }
            return;
        }

        // Reset button colors
        for (int j = 0; j < 100; j++) {
            if (!attackedPlayer.getShotsSustained().contains(j)) {
                seaButtons.get(j).setBackground(Background.fill(Color.LIGHTBLUE));
            }
        }

        // Update the hovered Button's color to ORANGE
        if (!attackedPlayer.getShotsSustained().contains(tileNum)) {
            seaButtons.get(tileNum).setBackground(Background.fill(Color.ORANGE));
        }
    }

    private void handleAttackingMouseClick(Player attackedPlayer, int tileNum, List<Button> seaButtons) {
        Fleet attackedFleet = attackedPlayer.getFleet();

        if (gameController.isGameOver()) {
            return;
        }

        if (gameController.getActivePlayer() == attackedPlayer) {
            return;
        }

        if (attackedPlayer.getShotsSustained().contains(tileNum)) {
            return;
        }

        gameController.processAttack(attackedPlayer, tileNum);
        updateShipColorsDuringGameplay(attackedPlayer, seaButtons);
    }

    public void updateShipColorsDuringGameplay(Player attackedPlayer, List<Button> attackedTiles) {
        Fleet attackedFleet = attackedPlayer.getFleet();
        // Update Ship colors
        for (int k = 0; k < 100; k++) {
            if (attackedFleet.containsSunkShip(k)) {
                attackedTiles.get(k).setBackground(Background.fill((Color.DARKRED)));
            } else if (attackedFleet.containsHitLocation(k)) {
                attackedTiles.get(k).setBackground(Background.fill(Color.RED));
            } else if (attackedPlayer.getShotsSustained().contains(k)) {
                attackedTiles.get(k).setBackground(Background.fill(Color.WHITE));
            } else {
                attackedTiles.get(k).setBackground(Background.fill(Color.LIGHTBLUE));
            }
            if (gameController.isGameOver() && attackedFleet.containsSunkShip(k)) {
                attackedTiles.get(k).setBackground(Background.fill(Color.GREEN));
            }
        }
    }
}
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
    private VBox[] attackInputVBoxes;
    private List<List<Button>> attackSeaTileButtonLists;

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
        gameplayAttackLabels = new Label[2];
        gameplayGrids = new GridPane[2];
        attackSeaTileButtonLists = new ArrayList<List<Button>>();
        attackSeaTileButtonLists.add(new ArrayList<Button>());      // For player 1
        attackSeaTileButtonLists.add(new ArrayList<Button>());      // For player 2

        for (int playerNum = 0; playerNum < 2; playerNum++) {
            gameplayAttackLabels[playerNum] = new Label(gameController.getPlayers().get(playerNum) + "'s attempts:");
            gameplayAttackLabels[playerNum].setFont(Style.FONT_DEFAULT);
            buildGameplayGrid(gameController.getPlayer(playerNum));
        }
    }

    private void buildAndGetGameplayLayout() {
        buildPlayerAttackVBoxes();
        buildGameplayLayout();
    }

    private void buildPlayerAttackVBoxes() {
        attackInputVBoxes = new VBox[2];
        for (int playerNum = 0; playerNum < 2; playerNum++) {
            attackInputVBoxes[playerNum] = new VBox();
            attackInputVBoxes[playerNum].setAlignment(Pos.CENTER);
            attackInputVBoxes[playerNum].setPadding(Style.INSETS_LARGE);
            attackInputVBoxes[playerNum].setSpacing(Style.SPACING_LARGE);
            attackInputVBoxes[playerNum].getChildren().addAll(gameplayAttackLabels[playerNum], gameplayGrids[playerNum]);
        }
    }

    private void buildGameplayLayout() {
        gameplayLayout = new HBox();
        gameplayLayout.setMinWidth(Style.MIN_LAYOUT_WIDTH);
        gameplayLayout.setMinHeight(Style.MIN_LAYOUT_HEIGHT);
        gameplayLayout.setSpacing(Style.SPACING_DEFAULT);
        gameplayLayout.setPadding(Style.INSETS_DEFAULT);
        gameplayLayout.setAlignment(Pos.CENTER);
        gameplayLayout.getChildren().addAll(attackInputVBoxes[0], attackInputVBoxes[1]);
    }

    // This method creates the grid where the other Player's ships are hiding
    private void buildGameplayGrid(Player attackingPlayer) {
        int playerNum = gameController.getPlayers().indexOf(attackingPlayer);
        gameplayGrids[playerNum] = new GridPane();
        gameplayGrids[playerNum].setAlignment(Pos.CENTER);
        for (int i = 0; i < 100; i++) {
            buildAttackSeaTileButton(attackingPlayer, i);
        }
    }

    private void buildAttackSeaTileButton(Player attackingPlayer, int tileNum) {
        int attackingPlayerNum = gameController.getPlayers().indexOf(attackingPlayer);

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
        attackSeaTileButtonLists.get(attackingPlayerNum).add(seaButton);

        seaButton.setOnMouseEntered(event -> handleAttackingMouseHover(attackingPlayer, tileNum));
        seaButton.setOnAction(event -> handleAttackingMouseClick(attackingPlayer, tileNum));
    }

    private void handleAttackingMouseHover(Player attackingPlayer, int tileNum) {
        // If it is not this player's turn, quit
        if (gameController.getActivePlayer() != attackingPlayer) {
            return;
        }

        int attackingPlayerNum = gameController.getPlayers().indexOf(attackingPlayer);
        Player attackedPlayer = gameController.getOtherPlayer(attackingPlayer);

        // If game is over, quit
        if (gameController.isGameOver()) {
            return;
        }

        // Reset any previously highlighted hovered tiles
        updateShipColorsDuringGameplay();

        // Update the hovered Button's color to ORANGE
        if (!attackedPlayer.getShotsSustained().contains(tileNum)) {
            attackSeaTileButtonLists.get(attackingPlayerNum).get(tileNum).setBackground(Background.fill(Color.ORANGE));
        }
    }

    private void handleAttackingMouseClick(Player attackingPlayer, int tileNum) {
        Player attackedPlayer = gameController.getOtherPlayer(attackingPlayer);

        if (gameController.isGameOver()) {
            return;
        }

        if (gameController.getActivePlayer() != attackingPlayer) {
            return;
        }

        if (attackedPlayer.getShotsSustained().contains(tileNum)) {
            return;
        }

        gameController.processAttack(attackedPlayer, tileNum);
        updateShipColorsDuringGameplay();
    }

    public void updateShipColorsDuringGameplay() {
        // Update Ship colors
        for (int attackingPlayerNum = 0; attackingPlayerNum < 2; attackingPlayerNum++) {
            Player attackingPlayer = gameController.getPlayer(attackingPlayerNum);
            Player attackedPlayer = gameController.getOtherPlayer(attackingPlayer);
            Fleet attackedFleet = attackedPlayer.getFleet();

            for (int k = 0; k < 100; k++) {
                Button thisSeaTileButton = attackSeaTileButtonLists.get(attackingPlayerNum).get(k);
                if (attackedFleet.containsSunkShip(k)) {
                    thisSeaTileButton.setBackground(Background.fill((Color.DARKRED)));
                } else if (attackedFleet.containsHitLocation(k)) {
                    thisSeaTileButton.setBackground(Background.fill(Color.RED));
                } else if (attackedPlayer.getShotsSustained().contains(k)) {
                    thisSeaTileButton.setBackground(Background.fill(Color.WHITE));
                } else {
                    thisSeaTileButton.setBackground(Background.fill(Color.LIGHTBLUE));
                }
                if (gameController.isGameOver()) {
                    // Set winning ships to GREEN
                    if (attackedFleet.containsSunkShip(k) && attackedFleet.isSunk()) {
                        thisSeaTileButton.setBackground(Background.fill(Color.GREEN));
                    }
                    // Reveal any unhit ships with GRAY
                    if (attackedFleet.containsLocation(k) && !attackedFleet.containsHitLocation(k)) {
                        thisSeaTileButton.setBackground(Background.fill(Color.GRAY));
                    }
                }
            }
        }
    }
}
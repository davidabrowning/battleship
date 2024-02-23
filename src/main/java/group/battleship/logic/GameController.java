package group.battleship.logic;

import group.battleship.domain.*;

import java.util.List;

public class GameController {

    private final Game game;

    public GameController() {
        game = new Game();
    }

    public int getNumPlayers() {
        return game.numPlayers();
    }
    public Player getActivePlayer() {
        return game.getActivePlayer();
    }
    public void setActivePlayer(Player p) { game.setActivePlayer(p); }
    public Player getPlayer(int playerNum) { return game.getPlayer(playerNum); }
    public List<Player> getPlayers() {
        return game.getPlayers();
    }
    public Player getOtherPlayer(Player player) { return game.getOtherPlayer(player);}
    public boolean isGameOver() { return game.isGameOver(); }
    public boolean placeShipHorizontally(Player p) { return p.placeShipHorizontally(); }
    public void rotateShipPlacement(Player p) { p.rotateShipPlacement(); }
    public void swapActivePlayer() { game.swapActivePlayer(); }

    // This method creates a new Player and adds it to the Game
    public void createPlayer(String playerName) {
        Player p = new Player (playerName);
        game.addPlayer(p);
    }

    // This method returns the next Ship a given Player needs to place
    public Ship getFirstUnplacedShip(Player p) {
        for (Ship ship : p.getFleet().getShips()) {
            if (ship.hasNotBeenPlaced()) {
                return ship;
            }
        }
        return null;
    }

    // This method checks if a Ship can be placed here legally
    public boolean isValidShipPlacementLocation(int tileNum, int shipLength, Fleet alreadyPlacedFleet) {
        int row = tileNum / 10;
        int col = tileNum % 10;
        boolean horizontalOrientation = game.getActivePlayer().placeShipHorizontally();

        // Check if this placement is on the grid
        if (horizontalOrientation && col + shipLength > 10 || !horizontalOrientation && row + shipLength > 10) {
            return false;
        }

        // Check if this placement overlaps with an already placed Ship
        int[] desiredTiles = new int[shipLength];
        if (horizontalOrientation) {
            for (int i = 0; i < shipLength; i++) {
                desiredTiles[i] = tileNum + i;
            }
        } else {
            for (int i = 0; i < shipLength; i++) {
                desiredTiles[i] = tileNum + i * 10;
            }
        }
        for (int desiredTile : desiredTiles) {
            if (alreadyPlacedFleet.containsLocation(desiredTile)) {
                return false;
            }
        }

        return true;
    }

    // Places a Ship at a given location
    public void placeShip(Ship ship, int tileNum) {
        if (game.getActivePlayer().placeShipHorizontally()) {
            for (int i = 0; i < ship.getSize(); i++) {
                ship.addLocation(tileNum + i);
            }
        } else {
            for (int i = 0; i < ship.getSize(); i++) {
                ship.addLocation(tileNum + i * 10);
            }
        }
    }

    public boolean allShipsArePlaced(Player p) {
        return !p.getFleet().hasNotBeenPlaced();
    }

    public boolean allShipsArePlaced() {
        if (getNumPlayers() < 2) {
            return false;
        }
        for (Player p : game.getPlayers()) {
            if (!allShipsArePlaced(p)) {
                return false;
            }
        }
        return true;
    }

    public void processAttack(Player p, int tileNum) {
        p.sustainShot(tileNum);
        for (Ship ship : p.getFleet().getShips()) {
            if (ship.containsLocation(tileNum)) {
                ship.addHit(tileNum);
            }
        }
        if (p.getFleet().isSunk()) {
            game.setGameOver(true);
        } else {
            swapActivePlayer();
        }
    }

}
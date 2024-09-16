package group.battleship.logic;

import group.battleship.domain.*;

import java.util.List;

public class GameController {

    private final Game game;

    public GameController() {
        game = new Game();
    }

    public int getNumPlayers() { return game.numPlayers(); }
    public Player getActivePlayer() { return game.getActivePlayer(); }
    public void setActivePlayer(Player p) { game.setActivePlayer(p); }
    public Player getPlayer(int playerNum) { return game.getPlayer(playerNum); }
    public List<Player> getPlayers() { return game.getPlayers(); }
    public Player getOtherPlayer(Player player) { return game.getOtherPlayer(player);}
    public boolean isGameOver() { return game.isGameOver(); }
    public boolean placeShipHorizontally(Player p) { return p.placeShipHorizontally(); }
    public void rotateShipPlacement(Player p) { p.rotateShipPlacement(); }
    public void swapActivePlayer() { game.swapActivePlayer(); }
    public int getBoardSize() { return game.getBoardSize(); }

    /**
     * Creates a new Player and adds the Player to the Game.
     * @param playerName The name of the Player to be created.
     */
    public void createPlayer(String playerName) {
        Player p = new Player (playerName);
        game.addPlayer(p);
    }

    /**
     * Identifies which Ship to place next.
     * @param p The Player placing a Ship.
     * @return The next Ship the Player p needs to place.
     */
    public Ship getFirstUnplacedShip(Player p) {
        for (Ship ship : p.getFleet().getShips()) {
            if (ship.hasNotBeenPlaced()) {
                return ship;
            }
        }
        return null;
    }

    /**
     * Checks if the Ship can be placed here.
     * @param tileNum The intended/hovered tile on the game board.
     * @param shipLength The length of the Ship to be placed.
     * @param alreadyPlacedFleet The Ships already placed on the game board.
     * @return True if the Ship can be placed here, false otherwise.
     */
    public boolean isValidShipPlacementLocation(int tileNum, int shipLength, Fleet alreadyPlacedFleet) {
        int row = tileNum / game.getBoardSize();
        int col = tileNum % game.getBoardSize();
        boolean horizontalOrientation = game.getActivePlayer().placeShipHorizontally();

        // Check if this placement is on the grid
        if (horizontalOrientation && col + shipLength > game.getBoardSize() || !horizontalOrientation && row + shipLength > game.getBoardSize()) {
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
                desiredTiles[i] = tileNum + i * game.getBoardSize();
            }
        }
        for (int desiredTile : desiredTiles) {
            if (alreadyPlacedFleet.containsLocation(desiredTile)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Places a Ship.
     * @param ship The Ship to be placed.
     * @param tileNum The tile for the Ship to be placed at.
     */
    public void placeShip(Ship ship, int tileNum) {
        if (game.getActivePlayer().placeShipHorizontally()) {
            for (int i = 0; i < ship.getSize(); i++) {
                ship.addLocation(tileNum + i);
            }
        } else {
            for (int i = 0; i < ship.getSize(); i++) {
                ship.addLocation(tileNum + i * game.getBoardSize());
            }
        }
    }

    /**
     * Checks if Player p has placed all of her Ships.
     * @param p The Player in question.
     * @return True if Player p has placed all of her ships, false otherwise.
     */
    public boolean allShipsArePlaced(Player p) {
        return !p.getFleet().hasNotBeenPlaced();
    }

    /**
     * Checks if all Players have placed all Ships.
     * @return True if all Ships have been placed, false otherwise.
     */
    public boolean allShipsArePlaced() {
        if (getNumPlayers() < game.getNumPlayersNeeded()) {
            return false;
        }
        for (Player p : game.getPlayers()) {
            if (!allShipsArePlaced(p)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Handles an attack on Player p's Fleet.
     * @param p The Player being attacked.
     * @param tileNum The location of the attack.
     */
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
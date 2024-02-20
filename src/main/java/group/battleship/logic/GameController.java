package group.battleship.logic;

import group.battleship.domain.*;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private final Game game;

    public GameController() {
        game = new Game();
    }

    public void createPlayer(String playerName) {
        Player p = new Player (playerName);
        game.addPlayer(p);
    }

    public int getNumPlayers() {
        return game.numPlayers();
    }


    public Player getActivePlayer() {
        return game.getActivePlayer();
    }
    public void setActivePlayer(Player p) { game.setActivePlayer(p); }

    public List<Player> getPlayers() {
        return game.getPlayers();
    }

    public Ship getFirstUnplacedShip(Player p) {
        for (Ship ship : p.getFleet().getShips()) {
            if (!ship.hasBeenPlaced()) {
                return ship;
            }
        }
        return null;
    }

    public boolean isValidShipPlacementLocation(int tileNum, int shipLength, Fleet alreadyPlacedFleet) {
        int row = tileNum / 10;
        int col = tileNum % 10;

        if (col + shipLength > 10) {
            return false;
        }

        int[] desiredTiles = new int[shipLength];
        for (int i = 0; i < shipLength; i++) {
            desiredTiles[i] = tileNum + i;
        }
        for (int desiredTile : desiredTiles) {
            if (alreadyPlacedFleet.containsLocation(desiredTile)) {
                return false;
            }
        }

        return true;
    }

    public void placeShip(Ship ship, int tileNum) {
        for (int i = 0; i < ship.getSize(); i++) {
            ship.addLocation(tileNum + i);
        }
    }

    public void swapActivePlayer() {
        game.swapActivePlayer();
    }

    public boolean allShipsArePlaced(Player p) {
        return p.getFleet().hasBeenPlaced();
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

    public Player getOtherPlayer(Player player) {
        return game.getOtherPlayer(player);
    }

    public boolean isGameOver() {
        return game.isGameOver();
    }

}
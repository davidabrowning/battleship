package group.battleship.logic;

import group.battleship.domain.*;

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
            if (ship.hasNotBeenPlaced()) {
                return ship;
            }
        }
        return null;
    }

    public boolean isValidShipPlacementLocation(int tileNum, int shipLength, Fleet alreadyPlacedFleet) {
        int row = tileNum / 10;
        int col = tileNum % 10;
        boolean horizontalOrientation = game.getActivePlayer().placeShipHorizontally();

        if (horizontalOrientation && col + shipLength > 10) {
            return false;
        }
        if (!horizontalOrientation && row + shipLength > 10) {
            return false;
        }

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

    public void swapActivePlayer() {
        game.swapActivePlayer();
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

    public Player getOtherPlayer(Player player) {
        return game.getOtherPlayer(player);
    }

    public boolean isGameOver() {
        return game.isGameOver();
    }

    public boolean placeShipHorizontally(Player p) { return p.placeShipHorizontally(); }

    public void rotateShipPlacement(Player p) { p.rotateShipPlacement(); }

}
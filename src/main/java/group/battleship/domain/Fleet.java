package group.battleship.domain;

import java.util.ArrayList;
import java.util.List;

public class Fleet {

    private final List<Ship> ships;

    public Fleet() {
        ships = new ArrayList<>();
        ships.add(new Ship("Carrier", 5));
        ships.add(new Ship("Battleship", 4));
        ships.add(new Ship("Cruiser", 3));
        ships.add(new Ship("Submarine", 3));
        ships.add(new Ship("Destroyer", 2));
    }

    public List<Ship> getShips() { return ships; }

    public boolean isSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

    public boolean containsLocation(int tileNum) {
        for (Ship ship : ships) {
            if (ship.containsLocation(tileNum)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsHitLocation(int tileNum) {
        for (Ship ship : ships) {
            if (ship.containsHit(tileNum)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsSunkShip(int tileNum) {
        for (Ship ship : ships) {
            if (ship.containsHit(tileNum) && ship.isSunk()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNotBeenPlaced() {
        for (Ship ship : ships) {
            if (ship.hasNotBeenPlaced()) {
                return true;
            }
        }
        return false;
    }

}

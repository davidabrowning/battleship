package group.battleship.domain;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String name;
    private final Fleet fleet;
    private final List<Integer> shotsSustained;
    private boolean placeShipHorizontally;

    public Player(String name) {
        this.name = name;
        this.fleet = new Fleet();
        this.shotsSustained = new ArrayList<>();
        this.placeShipHorizontally = true;
    }

    public String getName() { return name; }
    public Fleet getFleet() { return fleet; }
    public List<Integer> getShotsSustained() { return shotsSustained; }
    public void sustainShot(int tileNum) { shotsSustained.add(tileNum); }
    public boolean placeShipHorizontally() { return placeShipHorizontally; }
    public void rotateShipPlacement() { placeShipHorizontally = !placeShipHorizontally; }
    @Override
    public String toString() { return getName(); }
}

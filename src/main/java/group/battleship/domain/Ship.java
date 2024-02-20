package group.battleship.domain;

import java.util.HashSet;
import java.util.Set;

public class Ship {

    private final String name;
    private final int size;
    private final Set<Integer> locations;
    private final Set<Integer> hits;
    private boolean isSunk;

    public Ship(String name, int size) {
        this.name = name;
        this.size = size;
        locations = new HashSet<>();
        hits = new HashSet<>();
        isSunk = false;
    }

    public int getSize() { return size; }
    public boolean containsLocation(int location) { return locations.contains(location); }
    public void addLocation(int newLocation) { locations.add(newLocation); }
    public boolean containsHit(int hitLocation) { return hits.contains(hitLocation); }
    public void addHit(int newHitLocation) {
        hits.add(newHitLocation);
        if (hits.size() == locations.size()) {
            isSunk = true;
        }
    }
    public boolean isSunk() { return isSunk; }
    public boolean hasNotBeenPlaced() { return locations.isEmpty(); }
    @Override public String toString() { return name; }

}

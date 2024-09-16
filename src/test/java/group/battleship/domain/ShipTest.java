package group.battleship.domain;

import group.battleship.domain.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShipTest {

    private Ship testBattleship;

    @BeforeEach
    public void setup() {
        testBattleship = new Ship("Battleship", 4);
    }

    @Test
    public void getSizeReturnsBattleshipSize() {
        Ship testShip = new Ship("Battleship", 4);
        assertEquals(testShip.getSize(), 4);
    }

    @Test
    public void getSizeReturnsDestroyerSize() {
        Ship testShip = new Ship("Destroyer", 2);
        assertEquals(testShip.getSize(), 2);
    }

    @Test
    public void shipContainsLocationAfterLocationIsAdded() {
        testBattleship.addLocation(55);
        assertTrue(testBattleship.containsLocation(55));
    }

    @Test
    public void shipDoesNotContainLocationIfDifferentLocationIsAdded() {
        testBattleship.addLocation(56);
        assertFalse(testBattleship.containsLocation(55));
    }

    @Test
    public void shipContainsHitLocationAfterBeingHitAtThatLocation() {
        testBattleship.addHit(20);
        assertTrue(testBattleship.containsHit(20));
    }

    @Test
    public void shipDoesNotContainHitAfterDifferentHitLocationIsAdded() {
        testBattleship.addHit(32);
        assertFalse(testBattleship.containsHit(20));
    }
    
}

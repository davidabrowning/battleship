package group.battleship.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private GameController gc;

    @BeforeEach
    void setup() {
        gc = new GameController();
        gc.createPlayer("John Doe");
        gc.createPlayer("Jane Doe");
    }

    @Test
    void createPlayerCreatesAPlayer() {
        GameController newGC = new GameController();
        newGC.createPlayer("John Doe");
        assertTrue(newGC.getActivePlayer().toString().equals("John Doe"));
    }

    @Test
    void okToPlaceShipLength5AtOrigin() {
        assertTrue(gc.isValidShipPlacementLocation(0, 5, gc.getActivePlayer().getFleet()));
    }

    @Test
    void tileWithShipPlacedIsNotValidLocationForAnotherShip() {
        gc.placeShip(gc.getActivePlayer().getFleet().getShips().get(0), 15);
        assertFalse(gc.isValidShipPlacementLocation(15, 1, gc.getActivePlayer().getFleet()));
    }

    @Test
    void tileRoRightOfDesiredTileIsNotValidLocationForAnotherShipIfAlreadyClaimed() {
        gc.placeShip(gc.getActivePlayer().getFleet().getShips().get(0), 2);
        assertFalse(gc.isValidShipPlacementLocation(3, 2, gc.getActivePlayer().getFleet()));
    }



}
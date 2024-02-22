package group.battleship;

import group.battleship.ui.GameApplication;

public class GameLauncher {

    public static void main(String[] args) {
        System.out.println("Starting program.");
        GameApplication gameApplication = new GameApplication();
        gameApplication.go();
        System.out.println("Ending program.");
    }

}
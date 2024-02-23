package group.battleship.domain;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final List<Player> players;
    private Player activePlayer;
    private boolean gameOver;

    public Game() {
        players = new ArrayList<>();
        activePlayer = null;
        gameOver = false;
    }

    public Player getPlayer(int playerNum) { return players.get(playerNum); }

    public void addPlayer(Player p) {
        players.add(p);
        if (activePlayer == null) {
            activePlayer = p;
        }
    }

    public List<Player> getPlayers() { return players; }

    public int numPlayers() {
        return players.size();
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(Player p) { activePlayer = p; }

    public void setGameOver(boolean isGameOver) { gameOver = isGameOver; }

    public boolean isGameOver() { return gameOver; }

    public void swapActivePlayer() {
        if (activePlayer == players.get(0)) {
            activePlayer = players.get(1);
        } else {
            activePlayer = players.get(0);
        }
    }

    public Player getOtherPlayer(Player player) {
        if (player == players.get(0)) {
            return players.get(1);
        } else {
            return players.get(0);
        }
    }
}

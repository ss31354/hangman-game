package com.example.hangman;

import java.util.ArrayList;
import java.util.List;

public class GameRoom {
    private Game game;
    private List<String> players = new ArrayList<>();
    private String currentPlayer;

    public GameRoom(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void addPlayer(String playerName) {
        if (!players.contains(playerName)) {
            players.add(playerName);
        }
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}

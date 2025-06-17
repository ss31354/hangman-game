package com.example.hangman.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameUpdate {
    private String maskedWord;
    private String hint;
    private String currentPlayer;
    private String players;
    private boolean correctGuess;
    private boolean gameOver;
    private String winner;
    private int yourHangmanState;
    private int opponentHangmanState;
    private String yourName;
    private String opponentName;

}


package com.example.hangman;

import java.util.HashSet;
import java.util.Set;

public class Game {
    private String wordToGuess;
    private Set<Character> guessedLetters = new HashSet<>();
    private int maxAttempts = 6;
    private int wrongAttempts = 0;
    private boolean gameOver = false;
    private boolean won = false;

    public Game(String wordToGuess) {
        this.wordToGuess = wordToGuess.toLowerCase();
    }

    public String getWordToGuess() {
        return wordToGuess;
    }

    public Set<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public int getWrongAttempts() {
        return wrongAttempts;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWon() {
        return won;
    }

    public String getMaskedWord() {
        StringBuilder masked = new StringBuilder();
        for (char c : wordToGuess.toCharArray()) {
            if (guessedLetters.contains(c)) {
                masked.append(c).append(" ");
            } else {
                masked.append("_ ");
            }
        }
        return masked.toString().trim();
    }

    public String guess(char letter) {
        if (gameOver) {
            return "Game is over.";
        }

        letter = Character.toLowerCase(letter);

        if (guessedLetters.contains(letter)) {
            return "Letter already guessed.";
        }

        guessedLetters.add(letter);

        if (!wordToGuess.contains(String.valueOf(letter))) {
            wrongAttempts++;
            if (wrongAttempts >= maxAttempts) {
                gameOver = true;
                won = false;
                return "Wrong guess. Game over!";
            }
            return "Wrong guess.";
        } else {
            boolean allGuessed = true;
            for (char c : wordToGuess.toCharArray()) {
                if (!guessedLetters.contains(c)) {
                    allGuessed = false;
                    break;
                }
            }
            if (allGuessed) {
                gameOver = true;
                won = true;
                return "You won!";
            }
            return "Correct guess!";
        }
    }
}

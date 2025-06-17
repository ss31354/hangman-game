package com.example.hangman.model;

import lombok.Data;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class Room {
    private String id;
    private String word;
    private String hint;
    private Set<Character> guessedLetters = new HashSet<>();
    private List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private final Lock roomLock = new ReentrantLock();
    private Map<String, Integer> playerAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 6;

    public Room(String word, String hint) {
        this.id = UUID.randomUUID().toString();
        this.word = word.toUpperCase();
        this.hint = hint;
    }

    public boolean guessLetter(char letter) {
        roomLock.lock();
        try {
            letter = Character.toUpperCase(letter);
            if (guessedLetters.contains(letter)) return true;

            guessedLetters.add(letter);
            boolean correct = word.contains(String.valueOf(letter));
            if (!correct) {
                String currentId = getCurrentPlayer().getSessionId();
                playerAttempts.put(currentId, getAttempts(currentId) - 1);
            }
            return correct;
        } finally {
            roomLock.unlock();
        }
    }

    private int getAttempts(String sessionId) {
        return playerAttempts.getOrDefault(sessionId, MAX_ATTEMPTS);
    }

    public Player getCurrentPlayer() {
        roomLock.lock();
        try {
            return players.get(currentPlayerIndex);
        } finally {
            roomLock.unlock();
        }
    }

    public void nextTurn() {
        roomLock.lock();
        try {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } finally {
            roomLock.unlock();
        }
    }

    public boolean isWordGuessed() {
        roomLock.lock();
        try {
            for (char c : word.toCharArray()) {
                if (!guessedLetters.contains(c)) return false;
            }
            return true;
        } finally {
            roomLock.unlock();
        }
    }

    public String getMaskedWord() {
        roomLock.lock();
        try {
            StringBuilder masked = new StringBuilder();
            for (char c : word.toCharArray()) {
                masked.append(guessedLetters.contains(c) ? c : '_');
            }
            return masked.toString();
        } finally {
            roomLock.unlock();
        }
    }

    public void addPlayer(Player player) {
        roomLock.lock();
        try {
            players.add(player);
            playerAttempts.put(player.getSessionId(), MAX_ATTEMPTS);
        } finally {
            roomLock.unlock();
        }
    }

    public String getPlayersList() {
        roomLock.lock();
        try {
            return String.join(",", players.stream().map(Player::getName).toList());
        } finally {
            roomLock.unlock();
        }
    }

    public int getHangmanState(String sessionId) {
        return MAX_ATTEMPTS - getAttempts(sessionId);
    }
}
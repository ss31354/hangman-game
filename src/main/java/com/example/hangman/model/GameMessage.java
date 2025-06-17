package com.example.hangman.model;

import lombok.Data;

@Data
public class GameMessage {
    private String type; // "join", "guess", "chat"
    private String playerName;
    private String roomId;
    private String content; // letter for guess, message for chat
}
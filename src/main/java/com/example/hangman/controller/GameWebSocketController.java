package com.example.hangman.controller;

import com.example.hangman.model.*;
import com.example.hangman.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class GameWebSocketController<LastGuess> {
    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;

    @Autowired
    public GameWebSocketController(SimpMessagingTemplate messagingTemplate, GameService gameService) {
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
    }

    @MessageMapping("/game")
    public void handleMessage(GameMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Optional<Room> roomOpt = gameService.getRoom(message.getRoomId());
        if (roomOpt.isEmpty()) return;

        Room room = roomOpt.get();
        switch (message.getType()) {
            case "join" -> handleJoin(message, room, sessionId);
            case "guess" -> handleGuess(message, room, sessionId);
        }
    }

    private void handleJoin(GameMessage message, Room room, String sessionId) {
        Player player = new Player(message.getPlayerName(), sessionId);
        room.addPlayer(player);
        broadcastGameUpdate(room, sessionId);
    }

    private void handleGuess(GameMessage message, Room room, String sessionId) {
        if (!room.getCurrentPlayer().getSessionId().equals(sessionId)) return;

        char letter = message.getContent().toUpperCase().charAt(0);
        boolean correct = room.guessLetter(letter);

        if (!correct && room.getHangmanState(sessionId) >= 6) {
            GameUpdate update = createGameUpdate(room, sessionId);
            update.setGameOver(true);
            update.setWinner("Game Over! Word: " + room.getWord());
            messagingTemplate.convertAndSend("/topic/game/" + room.getId(), update);
            return;
        }

        if (room.isWordGuessed()) {
            GameUpdate update = createGameUpdate(room, sessionId);
            update.setGameOver(true);
            update.setWinner(room.getCurrentPlayer().getName() + " wins!");
            messagingTemplate.convertAndSend("/topic/game/" + room.getId(), update);
            return;
        }

        if (!correct) room.nextTurn();
        broadcastGameUpdate(room, sessionId);
    }

    private void broadcastGameUpdate(Room room, String currentSessionId) {

        messagingTemplate.convertAndSend("/topic/game/" + room.getId(),
                createGameUpdate(room, currentSessionId));
    }

    private GameUpdate createGameUpdate(Room room, String currentSessionId) {
        String otherPlayerId = room.getPlayers().stream()
                .filter(p -> !p.getSessionId().equals(currentSessionId))
                .findFirst()
                .map(Player::getSessionId)
                .orElse("");

        return new GameUpdate(
                room.getMaskedWord(),
                room.getHint(),
                room.getCurrentPlayer().getName(),
                room.getPlayersList(),
                false,
                false,
                (String) null,
                room.getHangmanState(currentSessionId),
                otherPlayerId.isEmpty() ? 0 : room.getHangmanState(otherPlayerId),
                room.getPlayers().stream()
                        .filter(p -> p.getSessionId().equals(currentSessionId))
                        .findFirst()
                        .map(Player::getName)
                        .orElse(""),
                room.getPlayers().stream()
                        .filter(p -> !p.getSessionId().equals(currentSessionId))
                        .findFirst()
                        .map(Player::getName)
                        .orElse("")
        );
    }
}
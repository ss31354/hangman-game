package com.example.hangman.controller;

import com.example.hangman.model.Room;
import com.example.hangman.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createRoom() {
        Room room = gameService.createRoom();

        Map<String, String> response = new HashMap<>();
        response.put("roomId", room.getId());
        response.put("word", room.getMaskedWord());
        response.put("hint", room.getHint());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/join/{roomId}")
    public ResponseEntity<?> joinRoom(@PathVariable String roomId) {
        Optional<Room> roomOpt = gameService.getRoom(roomId);
        if (roomOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Room room = roomOpt.get();
        Map<String, String> response = new HashMap<>();
        response.put("word", room.getMaskedWord());
        response.put("hint", room.getHint());
        response.put("players", room.getPlayersList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/exists/{roomId}")
    public ResponseEntity<Boolean> roomExists(@PathVariable String roomId) {
        return ResponseEntity.ok(gameService.roomExists(roomId));
    }
}
package com.example.hangman;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/game")
public class GameController {

    private Map<String, GameRoom> gameRooms = new HashMap<>();
    private Map<String, Game> singlePlayerGames = new HashMap<>();

    // ---- SINGLE PLAYER ----

    @PostMapping("/start-single")
    public String startSinglePlayer() {
        String[] words = {"computer", "java", "spring", "hangman", "distributed"};
        String randomWord = words[new Random().nextInt(words.length)];

        String gameId = UUID.randomUUID().toString();
        Game game = new Game(randomWord);
        singlePlayerGames.put(gameId, game);

        return gameId;
    }

    @PostMapping("/guess-single/{gameId}")
    public String guessSingle(@PathVariable String gameId, @RequestParam char letter) {
        Game game = singlePlayerGames.get(gameId);
        if (game == null) {
            return "Game not found!";
        }
        return game.guess(letter);
    }

    @GetMapping("/status-single/{gameId}")
    public Map<String, Object> statusSingle(@PathVariable String gameId) {
        Game game = singlePlayerGames.get(gameId);
        if (game == null) {
            return Map.of("error", "Game not found!");
        }
        return Map.of(
                "maskedWord", game.getMaskedWord(),
                "wrongAttempts", game.getWrongAttempts(),
                "maxAttempts", game.getMaxAttempts(),
                "gameOver", game.isGameOver(),
                "won", game.isWon()
        );
    }

    // ---- MULTIPLAYER ----

    @PostMapping("/start")
    public String startMultiplayer(@RequestParam String wordToGuess, @RequestParam String player1Name) {
        String gameId = UUID.randomUUID().toString();
        Game game = new Game(wordToGuess);
        GameRoom room = new GameRoom(game);
        room.addPlayer(player1Name);
        room.setCurrentPlayer(player1Name);
        gameRooms.put(gameId, room);

        return gameId;
    }

    @PostMapping("/join")
    public String joinRoom(@RequestParam String gameId, @RequestParam String player2Name) {
        GameRoom room = gameRooms.get(gameId);
        if (room == null) {
            return "Room not found!";
        }
        if (room.getPlayers().size() >= 2) {
            return "Room full!";
        }
        room.addPlayer(player2Name);
        return "Joined successfully!";
    }

    @PostMapping("/guess/{gameId}")
    public String guessMultiplayer(@PathVariable String gameId, @RequestParam String playerName, @RequestParam char letter) {
        GameRoom room = gameRooms.get(gameId);
        if (room == null) {
            return "Room not found!";
        }
        if (!room.getCurrentPlayer().equals(playerName)) {
            return "Not your turn!";
        }

        String result = room.getGame().guess(letter);
        if (room.getGame().isGameOver()) {
            room.setCurrentPlayer(null);
        } else {
            for (String p : room.getPlayers()) {
                if (!p.equals(playerName)) {
                    room.setCurrentPlayer(p);
                    break;
                }
            }
        }
        return result;
    }

    @GetMapping("/status/{gameId}")
    public Map<String, Object> statusMultiplayer(@PathVariable String gameId) {
        GameRoom room = gameRooms.get(gameId);
        if (room == null) {
            return Map.of("error", "Room not found!");
        }
        Game game = room.getGame();
        return Map.of(
                "maskedWord", game.getMaskedWord(),
                "wrongAttempts", game.getWrongAttempts(),
                "maxAttempts", game.getMaxAttempts(),
                "gameOver", game.isGameOver(),
                "won", game.isWon(),
                "currentPlayer", room.getCurrentPlayer(),
                "players", room.getPlayers()
        );
    }
}

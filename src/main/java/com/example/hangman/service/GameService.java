package com.example.hangman.service;

import org.springframework.stereotype.Service;
import com.example.hangman.model.Room;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final List<String> words = Collections.synchronizedList(Arrays.asList(
            "ELEPHANT:Has a trunk",
            "COMPUTER:Electronic device",
            "SUNFLOWER:Yellow flower",
            "MOUNTAIN:Natural elevation",
            "KEYBOARD:Input device",
            "NOTEBOOK:Writing material",
            "AIRPLANE:Flying vehicle",
            "TELEPHONE:Communication device",
            "CHOCOLATE:Sweet treat",
            "LIBRARY:Book collection"
    ));

    public Room createRoom() {
        String[] wordAndHint = words.get(new Random().nextInt(words.size())).split(":");
        Room room = new Room(wordAndHint[0], wordAndHint[1]);
        rooms.put(room.getId(), room);
        return room;
    }

    public Optional<Room> getRoom(String roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    public boolean roomExists(String roomId) {
        return rooms.containsKey(roomId);
    }
}
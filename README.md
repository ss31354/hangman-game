# Multiplayer Hangman Game

This project is a **Multiplayer Hangman** game built using **Spring Boot 3.2.0** and **Java 21**. It enables multiple players to connect, create or join game rooms, and play Hangman in real-time using WebSocket communication.

---

## Features

- Create or join multiplayer game rooms with unique room IDs.
- Real-time gameplay updates through WebSocket.
- Players take turns guessing letters to reveal a hidden word.
- Simple frontend served from the backend (HTML, CSS, JavaScript).
- Basic word and hint management within the backend.

---

## Technologies Used

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring WebSocket** for real-time communication
- **Lombok** to reduce boilerplate code
- **Maven** as the build and dependency management tool

---

## Project Structure

src/
├── main/
│ ├── java/com/example/hangman/ # Backend source code (controllers, models, services)
│ └── resources/
│ ├── static/ # Frontend static files (index.html, CSS, JS)
│ └── application.properties # Application configuration file
pom.xml # Maven project descriptor with dependencies and build plugins

### Prerequisites

- Java Development Kit (JDK) 21 installed
- Maven installed and configured on your system
- IDE with Lombok support (optional but recommended)

### Installation and Running

1. Clone the repository:

   ```bash
   git clone https://github.com/ss31354/hangman.git
   cd hangman
mvn clean package
mvn spring-boot:run
http://localhost:8080/

### How to Play
- Create Room: Click to create a new game room. A random word and hint will be generated.

- Join Room: Enter an existing room ID and your player name to join a game.

- Players take turns guessing letters.

- All players receive real-time updates on the current word state, guessed letters, and game progress via WebSocket.

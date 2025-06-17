let stompClient = null;
let playerName = '';
let roomId = '';
let currentPlayer = '';
const maxAttempts = 6;

const hangmanArt = [
    "  +---+\n  |   |\n      |\n      |\n      |\n      |\n=========",
    "  +---+\n  |   |\n  O   |\n      |\n      |\n      |\n=========",
    "  +---+\n  |   |\n  O   |\n  |   |\n      |\n      |\n=========",
    "  +---+\n  |   |\n  O   |\n /|   |\n      |\n      |\n=========",
    "  +---+\n  |   |\n  O   |\n /|\\  |\n      |\n      |\n=========",
    "  +---+\n  |   |\n  O   |\n /|\\  |\n /    |\n      |\n=========",
    "  +---+\n  |   |\n  O   |\n /|\\  |\n / \\  |\n      |\n========="
];

function connectWebSocket() {
    const socket = new SockJS('/hangman-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/topic/game/' + roomId, function(message) {
            const update = JSON.parse(message.body);
            updateGameUI(update);
        });

        stompClient.send("/app/game", {},
            JSON.stringify({
                'type': 'join',
                'playerName': playerName,
                'roomId': roomId,
                'content': ''
            })
        );
    }, function(error) {
        console.error('WebSocket error:', error);
        alert('Connection error. Please refresh and try again.');
    });
}

function initializeGameUI(data) {
    document.getElementById('login-container').classList.add('hidden');
    document.getElementById('game-container').classList.remove('hidden');
    document.getElementById('room-id-display').textContent = roomId;
    document.getElementById('word-display').textContent = data.word;
    document.getElementById('hint').textContent = 'Hint: ' + data.hint;

    // Initialize hangman displays
    document.getElementById('player-hangman-art').textContent = hangmanArt[0];
    document.getElementById('opponent-hangman-art').textContent = hangmanArt[0];
    document.getElementById('player-attempts').textContent = maxAttempts;
    document.getElementById('opponent-attempts').textContent = maxAttempts;
}

function updateGameUI(update) {
    // Update game state
    document.getElementById('word-display').textContent = update.maskedWord;
    document.getElementById('hint').textContent = 'Hint: ' + update.hint;
    document.getElementById('players-list').textContent = 'Players: ' + update.players;

    // Update hangman displays and attempts
    document.getElementById('player-hangman-art').textContent = hangmanArt[update.yourHangmanState];
    document.getElementById('opponent-hangman-art').textContent = hangmanArt[update.opponentHangmanState];
    document.getElementById('player-attempts').textContent = maxAttempts - update.yourHangmanState;
    document.getElementById('opponent-attempts').textContent = maxAttempts - update.opponentHangmanState;

    // Update player names
    document.querySelector('.player-hangman h3').textContent = playerName + "'s Hangman";
    if (update.opponentName) {
        document.querySelector('.opponent-hangman h3').textContent = update.opponentName + "'s Hangman";
    }

    // Update turn indicator or winner message
    const turnElement = document.getElementById('player-turn');
    if (update.gameOver) {
        turnElement.textContent = update.winner;
        turnElement.className = 'player-turn winner-message';
        document.getElementById('message').textContent = '';
        document.getElementById('letters').innerHTML = '';
        return;
    } else {
        turnElement.textContent = update.currentPlayer === playerName
            ? "⭐ Your Turn! Guess a letter"
            : `Waiting for ${update.currentPlayer}...`;
        turnElement.className = update.currentPlayer === playerName
            ? 'player-turn current-turn'
            : 'player-turn';
    }

    currentPlayer = update.currentPlayer;

    // FIXED: Proper guess feedback synchronization
    const messageElement = document.getElementById('message');
    if (update.lastGuess) {
        const lastGuessPlayer = update.lastGuess.playerName;
        if (lastGuessPlayer === playerName) {
            messageElement.textContent = update.lastGuess.correct
                ? 'Correct guess!'
                : 'Incorrect guess!';
            messageElement.className = update.lastGuess.correct
                ? 'correct'
                : 'incorrect';
        } else {
            messageElement.textContent = `${lastGuessPlayer} guessed ${update.lastGuess.letter}`;
            messageElement.className = update.lastGuess.correct
                ? 'correct'
                : 'incorrect';
        }
    } else {
        messageElement.textContent = '';
        messageElement.className = '';
    }

    renderLetterButtons();
}

function renderLetterButtons() {
    const lettersDiv = document.getElementById('letters');
    lettersDiv.innerHTML = '';

    for (let i = 0; i < 26; i++) {
        const letter = String.fromCharCode(65 + i);
        const button = document.createElement('button');
        button.textContent = letter;
        button.className = 'letter-btn';
        button.disabled = playerName !== currentPlayer;
        button.onclick = () => makeGuess(letter);
        lettersDiv.appendChild(button);
    }
}

function makeGuess(letter) {
    if (playerName === currentPlayer) {
        stompClient.send("/app/game", {},
            JSON.stringify({
                type: 'guess',
                playerName: playerName,
                roomId: roomId,
                content: letter
            })
        );
        // Disable the button immediately for better UX
        document.querySelectorAll('.letter-btn').forEach(btn => {
            if (btn.textContent === letter) btn.disabled = true;
        });
    }
}

function createGame() {
    playerName = document.getElementById('player-name').value.trim();
    if (!playerName) {
        alert('Please enter your name');
        return;
    }

    fetch('/api/game/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
    })
        .then(response => {
            if (!response.ok) throw new Error('Server error: ' + response.status);
            return response.json();
        })
        .then(data => {
            console.log("Game created:", data);
            roomId = data.roomId;
            initializeGameUI(data);
            connectWebSocket();
        })
        .catch(error => {
            console.error("Create Game Error:", error);
            alert("Failed to create game. See console for details.");
        });
}

function joinGame() {
    playerName = document.getElementById('player-name').value.trim();
    roomId = document.getElementById('room-id').value.trim();

    if (!playerName || !roomId) {
        alert('Please enter your name and room ID');
        return;
    }

    fetch(`/api/game/join/${roomId}`)
        .then(response => {
            if (!response.ok) throw new Error('Room not found');
            return response.json();
        })
        .then(data => {
            initializeGameUI(data);
            connectWebSocket();
        })
        .catch(error => {
            console.error('Error joining game:', error);
            alert(error.message);
        });
}

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('create-game').addEventListener('click', createGame);
    document.getElementById('join-game').addEventListener('click', joinGame);
});
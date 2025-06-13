// View Navigation
function goToSinglePlayer() {
    document.getElementById("landing").style.display = "none";
    document.getElementById("single-player").style.display = "block";
    fetch('/game/start')
        .then(res => res.json())
        .then(data => {
            document.getElementById("sp-hint").innerText = data.hint || "No hint";
            document.getElementById("sp-word").innerText = data.maskedWord;
        });
}

function goToMultiplayerLobby() {
    document.getElementById("landing").style.display = "none";
    document.getElementById("multiplayer-lobby").style.display = "block";
}

function goToLanding() {
    document.querySelectorAll("div").forEach(div => div.style.display = "none");
    document.getElementById("landing").style.display = "block";
}

// Game Logic
function guessSingle() {
    const letter = document.getElementById("sp-input").value;
    fetch(`/game/guess?letter=${letter}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById("sp-word").innerText = data.maskedWord;
            document.getElementById("sp-result").innerText = data.message;
        });
}

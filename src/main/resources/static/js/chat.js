'use strict'

let messageForm = document.querySelector("#message-form");

let stompClient;

let chatColor;

const availableColors = [
    "#3498db", // blau
    "#e74c3c", // rot
    "#9b59b6", // lila
    "#1abc9c", // türkis
    "#f1c40f"  // gelb
];


function connect() {

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, frame => {
        console.log("Connected: " + frame);

        stompClient.subscribe('/topic/public', onMessageReceived);

        stompClient.send('/app/chat.addUser', {}, JSON.stringify(
            {
                content: username + " joined",
                sender: username,
                color: chatColor
            }
        ));
    });
}

function sendMessage(event) {
    let content = document.querySelector("#message-input").value.trim();
    if (content && stompClient) {
        let message = {
            sender: username,
            content: content,
            color: chatColor,
        }
        stompClient.send(
            '/app/chat.sendMessage',
            {},
            JSON.stringify(message)
        );
        document.querySelector("#message-input").value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);

    const messageList = document.getElementById("chat-messages");

    // Neues <li>
    const li = document.createElement("li");
    li.classList.add("chat-message-card");

    // Container
    const container = document.createElement("div");
    container.classList.add("message-attributes");

    container.setAttribute("data-username", message.sender.charAt(0).toUpperCase());

    container.style.setProperty("--bubble-color", message.color);

    // Username
    const usernameEl = document.createElement("h4");
    usernameEl.textContent = message.sender;

    // Nachricht
    const contentEl = document.createElement("p");
    contentEl.textContent = message.content;

    const timeEl = document.createElement("p");
    timeEl.classList.add("message-time");
    timeEl.textContent = message.time;

    // Elemente zusammenbauen
    container.appendChild(usernameEl);
    container.appendChild(contentEl);
    container.appendChild(timeEl);
    li.appendChild(container);

    // In die Liste einfügen
    messageList.appendChild(li);

}

document.addEventListener("DOMContentLoaded", () => {
    chatColor = availableColors[Math.floor(Math.random() * availableColors.length)];

    connect();
});

messageForm.addEventListener('submit', sendMessage);
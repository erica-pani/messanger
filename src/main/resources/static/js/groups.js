
const groupname = document.querySelector('#groupname');
const chatMessageList = document.querySelector('#chat-message-list');
const groupList = document.querySelector('#group-list-container');
const messageForm = document.querySelector('#message-form');

let stompClient;

let activeGroup;

let chatColor;

const availableColors = [
    "#3498db", // blau
    "#e74c3c", // rot
    "#9b59b6", // lila
    "#1abc9c", // türkis
    "#f1c40f"  // gelb
];


function loadChatInfo(groupName) {

    while(chatMessageList.firstChild) {
        chatMessageList.removeChild(chatMessageList.firstChild);
    }

    fetch('/groups/' + encodeURIComponent(groupName))
        .then(res => {
            if(!res.ok) {
                console.log("Problem");
                return
            }

            return res.json()
        })
        .then(data => {
            data.forEach(message => {
                renderChatMessage(message);
            });
        })
        .catch(error => {
            console.log(error);
        })
}

function renderChatMessage(message) {
    const newMessage = document.createElement('li');

    newMessage.innerHTML = `
        <div class="message-attributes" style="--bubble-color: ${message.color}">
            <div>
                <h4>${message.sender.username}</h4>
                <p>${message.content}</p>
            </div>
            <p class="message-time"></p>
        </div>`;

    chatMessageList.appendChild(newMessage);
}

function loadGroups() {
    fetch('/groups?username=' + username)
        .then(res => {
            if(!res.ok) {
                console.log("Problem");
                return
            }

            return res.json()
        })
        .then(data => {
            data.forEach(group => {
                renderGroups(group);
                stompClient.subscribe("/topic/public/" + group.name, onMessageReceived);
            });
        })
        .catch(error => {
            console.log(error);
        })
}

function renderGroups(group) {
    
    const newGroup = document.createElement("div");
    newGroup.classList.add("group");

    newGroup.innerHTML = `
        <div class="group-attributes">
            <div class="group-attributes-wrap">
                 <h3>${group.name}</h3>
                <p class="last-send-message">
                    Letzte Nachricht
                </p>
            </div>
        </div>`;

    groupList.appendChild(newGroup);
}

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnect);
}

function onConnect() {
    console.log("WebSocket verbunden");

    loadGroups();
}

function onMessageReceived(payload) {

    let message = JSON.parse(payload.body);

    if(activeGroup === message.groupName) {
        renderChatMessage(message);
    }

    return
}

function sendMessage(event) {
    let content = document.querySelector('#message-input').value.trim();

    if(content && stompClient) {
        let message = {
            senderName: username,
            content: content,
            color: chatColor,
            groupName: activeGroup,
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

groupList.addEventListener('click', (event) => {
    const clicked = event.target.closest('.group');
    if(!clicked) return;

    document.querySelectorAll('.group').forEach(el => {
        el.classList.remove('group-selected');
    });

    clicked.classList.add('group-selected');

    let groupNameSelected  = clicked.querySelector('h3').textContent;

    activeGroup = groupNameSelected;

    groupname.textContent = groupNameSelected;

    loadChatInfo(groupNameSelected);
});

document.addEventListener('DOMContentLoaded', () => {
    chatColor = availableColors[Math.floor(Math.random() * availableColors.length)];

    connect();
});

messageForm.addEventListener('submit', sendMessage);

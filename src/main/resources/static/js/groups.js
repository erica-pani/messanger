
const groupname = document.querySelector('#groupname');
const chatMessageList = document.querySelector('#chat-message-list');
const groupList = document.querySelector('#group-list-container');
const messageForm = document.querySelector('#message-form');
const startScreen = document.querySelector('.start-screen');
const chatArea = document.querySelector('.chat-area');
const createGroupButton = document.querySelector('#create-group-button');
const closeGroupButton = document.querySelectorAll('.close-window-button'); 
const chatCache = {};

let stompClient;

let groupListOfUser; // Liste alle Gruppen des Users in json Format
let activeGroup; // html element .group von der aktiven Gruppe

let chatColor;

const availableColors = [
    "#3498db", // blau
    "#e74c3c", // rot
    "#9b59b6", // lila
    "#1abc9c", // türkis
    "#f1c40f"  // gelb
];


function loadChatInfo(groupName) {

    chatMessageList.innerHTML = "";

    if(chatCache[groupName]) {
        chatCache[groupName].forEach(msg => renderChatMessage(msg));
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
            const cached = chatCache[groupName] || [];

            const merged = [...data, ...cached]
                .reduce((acc, msg) => {
                    acc[msg.id] = msg;
                    return acc;
                }, {});

            const finalMessages = Object.values(merged)
                .sort((a, b) => a.id - b.id);

            chatCache[groupName] = finalMessages;
            chatMessageList.innerHTML = "";

            finalMessages.forEach(msg => renderChatMessage(msg));
            scrollToBottom();
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


async function loadGroups() {
    try {
        const res = await fetch('/groups?username=' + username);

        if (!res.ok) {
            console.log("Problem");
            return;
        }

        groupListOfUser = await res.json();

        for (const group of groupListOfUser) {
            const lastMessage = await getLastMessage(group); 
            renderGroups(group, lastMessage);
            stompClient.subscribe("/topic/public/" + group.name, onMessageReceived);
        }

    } catch (error) {
        console.log(error);
    }
}


async function getLastMessage(group) {
    
    const res = await fetch('/groups/' + encodeURIComponent(group.name));

    if(!res.ok) {
        console.log("Problem");
        return null;
    }

    const data = await res.json();

    return data.sort((a, b) => b.id - a.id)[0] || null;
}


//last message wenn es keine Messages in der Gruppe gibt
function renderGroups(group, lastMessage) {
    const lastMessageString = lastMessage
        ? `${lastMessage.sender.username}: ${lastMessage.content}`
        : "";

    const newGroup = document.createElement("div");
    newGroup.classList.add("group");

    newGroup.innerHTML = `
        <div class="group-attributes alert-dot-hidden">
            <div class="group-attributes-wrap">
                 <h3>${group.name}</h3>
                <p class="last-send-message">
                    ${lastMessageString}
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

    groupList.querySelectorAll('.group').forEach(element => {
        if (element.querySelector('h3').textContent === message.group.name) {

            element.querySelector('.last-send-message').textContent =
                `${message.sender.username}: ${message.content}`;

            if (!isActiveGroup(element)) {
                let groupAttributes = element.querySelector('.group-attributes');
                groupAttributes.classList.remove('alert-dot-hidden');
            }
        }
    });

    if(!activeGroup) null;

    if(!chatCache[message.group.name]) {
        chatCache[message.group.name] = [];
    }
    chatCache[message.group.name].push(message);

    if(activeGroup.querySelector('h3').textContent === message.groupName) {
        renderChatMessage(message);
        scrollToBottom();
    }

    return
}

function sendMessage(event) {
    event.preventDefault();

    if (!activeGroup) {
        console.warn("Keine Gruppe ausgewählt");
        return;
    }

    const content = document.querySelector('#message-input').value.trim();
    if (!content || !stompClient) return;

    const message = {
        senderName: username,
        content: content,
        color: chatColor,
        groupName: activeGroup.querySelector('h3').textContent
    };

    stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(message));

    activeGroup.querySelector('.last-send-message').textContent =
        `${username}: ${content}`;

    document.querySelector("#message-input").value = '';
}

function isActiveGroup(group) {
    if (!activeGroup) return false;
    return group.querySelector('h3').textContent === activeGroup.querySelector('h3').textContent;
}

function scrollToBottom() {
    chatMessageList.scrollTop = chatMessageList.scrollHeight;
}

groupList.addEventListener('click', (event) => {
    const clicked = event.target.closest('.group');
    if(!clicked) return;

    document.querySelectorAll('.group').forEach(el => {
        el.classList.remove('group-selected');
    });

    startScreen.classList.add('hidden');
    chatArea.classList.remove('hidden');

    clicked.classList.add('group-selected');
    clicked.querySelector('.group-attributes').classList.add('alert-dot-hidden');

    let groupNameSelected  = clicked.querySelector('h3').textContent;

    activeGroup = clicked;

    groupname.textContent = groupNameSelected;

    loadChatInfo(groupNameSelected);
    setTimeout(scrollToBottom, 50);
});

document.addEventListener('DOMContentLoaded', () => {
    chatColor = availableColors[Math.floor(Math.random() * availableColors.length)];

    connect();
});

messageForm.addEventListener('submit', sendMessage);

createGroupButton.addEventListener('click', function() {
    const createGroupWindow = document.querySelector('#create-group-window');
    const groupAndFilterCont = document.querySelector('.group-and-filter-cont');

    createGroupWindow.classList.remove('hidden');
    groupAndFilterCont.classList.add('hidden');
});

closeGroupButton.forEach( button => {
    button.addEventListener('click', function() {
        const createGroupWindow = document.querySelector('#create-group-window');
        const groupAndFilterCont = document.querySelector('.group-and-filter-cont');
        const friendships = document.querySelector('#friendship-window');

        createGroupWindow.classList.add('hidden');
        friendships.classList.add('hidden');
        groupAndFilterCont.classList.remove('hidden');
    });
})

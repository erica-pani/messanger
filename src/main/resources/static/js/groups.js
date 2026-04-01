
const groupname = document.querySelector('#groupname');
const chatMessageList = document.querySelector('#chat-message-list');
const groupList = document.querySelector('#group-list-container');

let stompClient;

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

    fetch('/groups/' + groupName)
        .then(res => {
            if(!res.ok) {
                console.log("Problem");
                return
            }

            return res.json()
        })
        .then(data => {
            data.forEach(message => {
                renderGroups(message);
            });
        })
        .catch(error => {
            console.log(error);
        })
}

function renderChatMessage(message) {
    const newMessage = document.createElement('li');

    newMessage.innerHTML = `
        <div class="message-attributes">
            <div>
                <h4>${message.senderName}</h4>
                <p>${message.content}</p>
            </div>
            <p class="message-time"></p>
        </div>`;

    chatMessageList.appendChild(newMessage);
}

function laodGroups() {
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
    
}

groupList.addEventListener('click', (event) => {
    const clicked = event.target.closest('.group');
    if(!clicked) return;

    document.querySelectorAll('.group').forEach(el => {
        el.classList.remove('group-selected');
    });

    clicked.classList.add('group-selected');

    let groupNameSelected  = clicked.querySelector('h3').textContent;

    groupname.textContent = groupNameSelected;

    loadChatInfo(groupNameSelected);
});

document.addEventListener('DOMContentLoaded', () => {
    chatColor = availableColors[Math.floor(Math.random() * availableColors.length)];

    laodGroups();
});

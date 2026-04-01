
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
            console.log(data);
        })
        .catch(error => {
            console.log(error);
        })
}

function renderChatMessage(message) {

}

function laodGroups() {
    fetch('/groups?username=${username}')
        .then(res => {
            if(!res.ok) {
                console.log("Problem");
                return
            }

            return res.json()
        })
        .then(data => {
            console.log(data);
        })
        .catch(error => {
            console.log(error);
        })
}

function renderGroups(group) {

}

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, frame => {
        console.log("Connected: " + frame);

        stompClient.subscribe('/topic/public', onMessageReceived);

        stompClient.send('/app/chat.addUser', {}, JSON.stringify(
            {
                content: username + " joined",
                senderName: username,
                color: chatColor
            }
        ));
    });
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

    connect();
});

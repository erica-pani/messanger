
const friendshipForm = document.querySelector('#friendship-form');
const requestedId = document.querySelector('#requested-id');
const requestList = document.querySelector('#pending-friendship-requests');
const friendRequestButton = document.querySelector('#friendrequest-button');
const createGroupButton = document.querySelector('#create-group-button');
const possibleMemberList = document.querySelector('#possible-member-list');
const createGroupForm = document.querySelector('#create-group-form');

const groupMemberList = [];

//polling von requests damit es aktuell bleibt
async function sendFriendshipRequest(requestToInId) {

    const url = `/friendship/request/to?sender=${id}&receiver=${requestToInId}`

    await fetchData(url, 'POST', null);

}

async function fetchData(url, method, body) {

    const fetchInfo = {
        method: method,
        headers: {"Content-Type": "application/json"},
    }

    if(body) {
        fetchInfo.body = JSON.stringify(body);
    }

    const res = await fetch(url, fetchInfo);

    if (!res.ok) {
        const errorText = await res.text(); 
        console.error("Fetch error:", res.status, errorText);
        return null;
    }

    let data = await res.json();

    return data;
}

async function loadRequests() {

    const url = `/friendship/requests?id=${id}`;

    const requests = await fetchData(url, 'GET', null);

    if(!requests) {
        return;
    }

    requestList.innerHTML = '';

    requests.forEach(request => {
        renderRequests(request);
    });
}

async function loadFriends() {
    const url = `/friendship/friends?id=${id}`;

    const friendships = await fetchData(url, 'GET', null);

    if (!friendships) {
        return
    }

    console.log(friendships);

    possibleMemberList.innerHTML = "";

    friendships.forEach(friendship => {
        if (friendship.user1.id == id) {

            renderPossibleMember(friendship.user2);

        } else {
            renderPossibleMember(friendship.user1);
        }
    });
}

async function createNewGroup(name) {
    
    const url = '/groups/create'

    const groupdto = {
        name: name,
        userIds: groupMemberList,
    };

    fetchData(url, 'POST', groupdto);
}

function replyToRequest(button) {

    const acceptBtn = button.target.closest(".accept-request-button");
    const declineBtn = button.target.closest(".decline-request-button");

    if (!acceptBtn && !declineBtn) {
        return;
    }

    const parent = button.target.closest(".possible-friend");
    const requestId = parent.querySelector('input[name="requestId"]').value;

    const reply = acceptBtn ? true : false;

    const url = `/friendship/request/reply?id=${requestId}&reply=${reply}`;

    fetchData(url, 'POST', null);

    parent.remove();
}

function renderPossibleMember(possibleMember) {

    const newPossibleMember = document.createElement('div');

    newPossibleMember.classList.add('possible-member');

    newPossibleMember.innerHTML = `
            <input name="friendId" type="hidden" value="${possibleMember.id}">

            <div class="possible-member-attributes">
                <h3>${possibleMember.username}</h3>
             </div>`

    possibleMemberList.appendChild(newPossibleMember);
}

function renderRequests(request) {
    const newRequest = document.createElement('div');

    newRequest.innerHTML = `
        <div class="possible-friend">
            <input name="requestId" type="hidden" value="${request.id}">

            <div class="possible-friend-attributes">
                <h3>${request.sender.username}</h3>
                <div class="request-button-wrap">
                    <button class="request-button accept-request-button" type="button">
                        <img src="/img/icons/checkIcon.svg">
                    </button>
                    <button class="request-button decline-request-button" type="button">
                        <img src="/img/icons/decline.svg">
                    </button>
                </div>
            </div>
            <p class="message-time"></p>
        </div>`;

    requestList.appendChild(newRequest);
}

friendRequestButton.addEventListener('click', loadRequests);

friendshipForm.addEventListener('submit', function(event) {
    event.preventDefault();

    if(requestedId) {
        const id = requestedId.value.trim();
        
        sendFriendshipRequest(id);

        requestedId.value = "";
    }
});

createGroupButton.addEventListener('click', loadFriends);

document.addEventListener('click', button => {
    replyToRequest(button);
});

createGroupForm.addEventListener('submit', function(event) {
    
    event.preventDefault();

    let name = createGroupForm.querySelector('.groupname-input').value.trim();

    if (!name) {
        return
    }

    groupMemberList.push(id);

    createNewGroup(name);
});

possibleMemberList.addEventListener('click', (event) => {
    const clicked = event.target.closest('.possible-member');

    if(!clicked) return;

    if (clicked.classList.contains('group-selected')) {

        clicked.classList.remove('group-selected');

        const memberId = clicked.querySelector('input[name="friendId"]').value;

        const index = groupMemberList.indexOf(memberId);
        
        if(index !== -1) {
            groupMemberList.splice(index, 1);
        }
    } else {

        clicked.classList.add('group-selected');
        groupMemberList.push(clicked.querySelector('input[name="friendId"]').value);
    }

});


const friendshipForm = document.querySelector('#friendship-form');
const requestedId = document.querySelector('#requested-id');
const requestList = document.querySelector('#pending-friendship-requests');
const friendRequestButton = document.querySelector('#friendrequest-button');

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

function replyToRequest() {

    
}

function renderRequests(request) {
    const newRequest = document.createElement('div');

    newRequest.innerHTML = `
        <div class="possible-friend">
            <div class="possible-friend-attributes">
                <h3>${request.sender.username}</h3>
                <div class="request-button-wrap">
                    <button id="accept-request-button" class="request-button" type="button">
                        <img src="/img/icons/checkIcon.svg">
                    </button>
                    <button id="decline-request-button" class="request-button" type="button">
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
    }
});

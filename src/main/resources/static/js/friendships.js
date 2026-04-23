
const friendshipForm = document.querySelector('#friendship-form');
const requestedId = document.querySelector('#requested-id');




async function sendFriendshipRequest(requestToInId) {

    const res = await fetch(`/friendship/request/to?sender=${userId}&receiver=${requestToInId}`, {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(user)
                        })
}
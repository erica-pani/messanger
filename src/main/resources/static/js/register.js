
const registerForm = document.querySelector('#register-form');

function register() {

    const firstname = document.querySelector('[name="firstname"]').value;
    const lastname = document.querySelector('[name="lastname"]').value;
    const username = document.querySelector('[name="username"]').value;
    const password = document.querySelector('[name="password"]').value;
    const passwordCorrect = document.querySelector('[name="password-second-time"]').value;
    const birthdate = document.querySelector('[name="birth-date"]').value;
    const dsgvoChecked = document.querySelector('#stay-logged-in-chekbox').checked;

    if (password !== passwordCorrect) {
        alert("Passwörter stimmen nicht überein");
        return;
    }

    if (!dsgvoChecked) {
        alert("Bitte akzeptiere die Datenschutzbestimmungen");
        return;
    }

    let user = {
        firstname: firstname,
        lastname: lastname,
        username: username,
        hashed_password: password,
        birthDate: birthdate,
    }

    fetch("/user/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(user)
    })
    .then(res => {
        if (res.ok) {
            alert("Registrierung erfolgreich!");
            window.location.href = "/login"
        } else {
            alert("Fehler bei der Registrierung");
        }
    });
}

registerForm.addEventListener('submit', function(event) {
    event.preventDefault();

    register();
}); 
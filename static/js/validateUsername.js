function validateUsername(name) {
    const username = name.value;

    if (username === "") {
        document.getElementById("messageFromServer").innerHTML = "Please enter a username ";
        return;
    }
    fetch('validateUsername?username=' + username, {method: 'GET'}).then(res => res.text()).then(data => {
        document.getElementById("messageFromServer").innerHTML = data;
    }).catch(err => {
        document.getElementById("messageFromServer").innerHTML = "An error occurred. Please try again.";
    });
}


function validatePassword(pass) {
    const password = pass.value;

    if (password === "") {
        document.getElementById("messageFromServerForPass").innerHTML = "Please enter a password ";
        return;
    }
    fetch('validatePassword?password=' + password, {method: 'GET'}).then(res => res.text()).then(data => {
        const formattedData = data.replace(/\r\n/g, "<br>");
        document.getElementById("messageFromServerForPass").innerHTML = formattedData;
    }).catch(err => {
        document.getElementById("messageFromServerForPass").innerHTML = "An error occurred. Please try again.";
    });
}


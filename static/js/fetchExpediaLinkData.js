function fetchExpediaLinkData(entry) {
    fetch('expediaLink?link=' + entry, {method: 'GET'}).then(res => res.text()).then(data => {
        if (data) {
            alert("successfully add the link to your visited links \n You can visit and modify them in your dashboard");
        }
    })
}
// When the page loads, get the ID of the currently logged in user and display it.
document.addEventListener("DOMContentLoaded", () => {
    getCurrentUser();
});

function getCurrentUser() {

    const authToken = localStorage.getItem('access_token'); // 인증 토큰 가져오기

    // Send a GET request to the server using httpRequest().
    httpRequest('GET', '/current-user', function success(responseData) {
                                                    const data = JSON.parse(responseData);
                                                    const username = data.username;
                                                    // Put the fetched user ID into the current-user tag.
                                                    document.getElementById("current-user").textContent = username;
                                                }, function fail() {
        console.error("Failed to get current user.");
        alert('Failed to get current user');
    });

}


// function to get cookies
function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function (item) {
        item = item.replace(' ', '');

        var dic = item. split('=');

        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });

    return result;
}

function httpRequest(method, url,success, fail) {
debugger;
    fetch(url, {
        method: method,
        headers: {
            // Get the access token value from local storage and add it to headers
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
            'Content-Type': 'application/json',
        },

    })
    .then(response => {
        if (response.status === 200 || response.status === 201) {
            return response.json();
        }
        const refresh_token = getCookie('refresh_token');
        if (response.status === 401 && refresh_token) {
            fetch('/api/token', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    refreshToken: getCookie('refresh_token'),
                }),
            })
            .then(res => {
                if (res.ok) {
                    return res.json();
                }
            })
            .then(result => {
                // If reissuance succeeds, replace local storage value with new access token
                localStorage.setItem('access_token', result.accessToken);
                // Retry the original request with the new access token
                httpRequest(method, url, success, fail);
            })
            .catch(error => fail());
        } else {
            return fail();
        }
    })
     .then(data => success(data))
     .catch(error => fail());
}
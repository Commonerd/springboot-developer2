// When the page loads, get the ID of the currently logged in user and display it.
document.addEventListener("DOMContentLoaded", () => {
    getCurrentUser();
});

function getCurrentUser() {
    const authToken = localStorage.getItem('access_token'); // 인증 토큰 가져오기

    // Send a GET request to the server using currentUserHttpRequest().
    currentUserHttpRequest('GET', '/current-user', function success(responseData) {
        const username = responseData.username;
        // Put the fetched user ID into the current-user tag.
        document.getElementById("current-user").textContent = username;

        // Send a GET request to the server to get follower count.
        currentUserHttpRequest('GET', '/follower-count', function success(responseData) {
            const followerCount = responseData;
            document.getElementById("follower-count").textContent = followerCount;
        }, function fail() {
            alert('Failed to get follower count');
        });

        // Send a GET request to the server to get following count.
        currentUserHttpRequest('GET', '/following-count', function success(responseData) {
            const followingCount = responseData;
            document.getElementById("following-count").textContent = followingCount;
        }, function fail() {
            alert('Failed to get following count');
        });
    }, function fail() {
        //console.error("Failed to get current user.");
        alert('Failed to get current user');
    });

}


/*// function to get cookies
function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function(item) {
        item = item.replace(' ', '');

        var dic = item.split('=');

        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });

    return result;
}*/

function currentUserHttpRequest(method, url, success, fail) {
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
                        currentUserHttpRequest(method, url, success, fail);
                    })
                    .catch(error => fail());
            } else {
                return fail();
            }
        })
        .then(data => success(data))
        .catch(error => fail());
}
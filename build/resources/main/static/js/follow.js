// 팔로우
function addFollow(button) {
    const id = parseInt(button.dataset.itemId, 10);
    button.addEventListener('click', event => {
        body = JSON.stringify({});

        function success() {
            alert('登録が完了しました。');
        };

        function fail() {
            alert('登録に失敗しました。');
        };
        httpRequest('POST', '/follow/' + id, body, success, fail)
    });
}

    /*return fetch(`/follow/${id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + localStorage.getItem('access_token'),
      },
      body: JSON.stringify({}),
    }).then((response) => {
      if (!response.ok) {
        throw new Error('서버 오류로 팔로우 추가에 실패했습니다.');
      }
      // 서버에서 반환된 데이터를 JSON 형식으로 파싱
          return response.text();
        }).then((data) => {
          // 서버에서 반환된 데이터를 사용하여 원하는 동작 수행
          alert(data); // 예시: 서버에서 반환된 데이터를 콘솔에 출력
        }).catch((error) => {
          // 오류 처리
          console.error(error);
          alert('오류가 발생했습니다.');
    });

    followUser(id)
      .catch((error) => console.error('fail to follow:', error));
}*/


// 언팔로우
function deleteFollow(button) {
    const id = parseInt(button.dataset.itemId, 10);
    button.addEventListener('click', event => {
        body = JSON.stringify({});

        function success() {
            alert('登録が完了しました。');
        };

        function fail() {
            alert('登録に失敗しました。');
        };
        httpRequest('DELETE', '/unfollow/' + id, body, success, fail)
    });
}

/*// 팔로우 상태를 토글하는 함수
function toggleFollowStatus(button) {
console.log(button.dataset.itemId);
    const id = parseInt(button.dataset.itemId, 10);
  *//*const followButton = document.getElementById('follow-btn');*//*
  const isFollowing = button.dataset.following*//* === 'true'*//*;

  // 팔로우 상태에 따라 동작
  if (isFollowing) {
    // 팔로우
    followUser(id)
      .then(() => {
        button.textContent = 'Follow';
        button.dataset.following = 'false';
      })
      .catch((error) => console.error('fail to unfollow:', error));
  } else {
    // 언팔로우
    followUser(id)
      .then(() => {
        button.textContent = 'Unfollow';
        button.dataset.following = 'true';
      })
      .catch((error) => console.error('fail to follow:', error));
  }
}*/

/*
// 서버에 팔로우 추가 요청
function followUser(id) {
  return fetch(`/follow/${id}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + localStorage.getItem('access_token'),
    },
    body: JSON.stringify({}),
  }).then((response) => {
    if (!response.ok) {
      throw new Error('서버 오류로 팔로우 추가에 실패했습니다.');
    }
    // 서버에서 반환된 데이터를 JSON 형식으로 파싱
        return response.text();
      }).then((data) => {
        // 서버에서 반환된 데이터를 사용하여 원하는 동작 수행
        alert(data); // 예시: 서버에서 반환된 데이터를 콘솔에 출력
      }).catch((error) => {
        // 오류 처리
        console.error(error);
        alert('오류가 발생했습니다.');
  });
}
*/


/*// 서버에 언팔로우 요청
function unfollowUser(id, refreshToken) {
  return fetch(`/unfollow/${id}`, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
       Authorization: 'Bearer ' + localStorage.getItem('access_token'),
    },
    body: JSON.stringify({}),
  }).then((response) => {
    if (!response.ok) {
      throw new Error('서버 오류로 언팔로우에 실패했습니다.');
    }
    // 서버에서 반환된 데이터를 JSON 형식으로 파싱
        return response.text();
      }).then((data) => {
        // 서버에서 반환된 데이터를 사용하여 원하는 동작 수행
        alert(data); // 예시: 서버에서 반환된 데이터를 콘솔에 출력
      }).catch((error) => {
        // 오류 처리
        console.error(error);
        alert('오류가 발생했습니다.');
  });

}*/


// 쿠키를 가져오는 함수
function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function (item) {
        item = item.replace(' ', '');

        var dic = item.split('=');

        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });

    return result;
}

// HTTP 요청을 보내는 함수
function httpRequest(method, url, body, success, fail) {
    fetch(url, {
        method: method,
        headers: { // 로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
            'Content-Type': 'application/json',
        },
        body: body,
    }).then(response => {
        if (response.status === 200 || response.status === 201) {
            return success();
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
                .then(result => { // 재발급이 성공하면 로컬 스토리지값을 새로운 액세스 토큰으로 교체
                    localStorage.setItem('access_token', result.accessToken);
                    httpRequest(method, url, body, success, fail);
                })
                .catch(error => fail());
        } else {
            return fail();
        }
    });
}
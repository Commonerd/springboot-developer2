// 삭제 기능
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;

        function success() {
            alert('削除が完了しました。');
            location.replace('/articles');
        }

        function fail() {
            alert('削除に失敗しました。');
            location.replace('/articles');
        }

        httpRequest('DELETE', `/api/articles/${id}`, null, success, fail);
    });
}

//// 수정 기능
//const modifyButton = document.getElementById('modify-btn');
//
//if (modifyButton) {
//    modifyButton.addEventListener('click', event => {
//        let params = new URLSearchParams(location.search);
//        let id = params.get('id');
//
//        body = JSON.stringify({
//            title: document.getElementById('title').value,
//            content: document.getElementById('content').value
//        })
//
//        function success() {
//            alert('修正が完了しました。');
//            location.replace(`/articles/${id}`);
//        }
//
//        function fail() {
//            alert('修正に失敗しました。');
//            location.replace(`/articles/${id}`);
//        }
//
//        httpRequest('PUT', `/api/articles/${id}`, body, success, fail);
//    });
//}

// 수정 기능
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
debugger;
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        const formData = new FormData();
        formData.append('title', document.getElementById('title').value);
        formData.append('content', document.getElementById('content').value);

        // 이미지 파일 추가
        const imageFiles = document.querySelector('input[name="imageFiles"]').files;

        for (const imageFile of imageFiles) {
                    formData.append('imageFiles[]', imageFile);
                }


        function success() {
            alert('修正が完了しました。');
            location.replace(`/articles/${id}`);
        }

        function fail() {
            alert('修正に失敗しました。');
            location.replace(`/articles/${id}`);
        }

        httpRequestWithFormData('POST', `/api/articles/${id}`, formData, success, fail);
    });
}


//// 생성 기능
//const createButton = document.getElementById('create-btn');
//
//if (createButton) {
//    // 등록 버튼을 클릭하면 /api/articles로 요청을 보낸다
//    createButton.addEventListener('click', event => {
//        body = JSON.stringify({
//            title: document.getElementById('title').value,
//            content: document.getElementById('content').value
//        });
//
//        function success() {
//            alert('登録が完了しました。');
//            location.replace('/articles');
//        };
//
//        function fail() {
//            alert('登録に失敗しました。');
//            location.replace('/articles');
//        };
//
//        httpRequest('POST', '/api/articles', body, success, fail)
//    });
//}

// 생성 기능
const createButton = document.getElementById('create-btn');

if (createButton) {
debugger;
    createButton.addEventListener('click', event => {

        const formData = new FormData();
        formData.append('title', document.getElementById('title').value);
        formData.append('content', document.getElementById('content').value);

        // 이미지 파일 추가
        const imageFiles = document.querySelector('input[name="imageFiles"]').files;


//        // 비디오 파일을 다른 키로 추가
//        for (const videoFile of videoFiles) {
//            formData.append('videoFiles[]', videoFile);
//        }

        // 이미지 파일을 다른 키로 추가
        for (const imageFile of imageFiles) {
            formData.append('imageFiles[]', imageFile);
        }

        function success() {
            alert('등록이 완료되었습니다.');
            location.replace('/articles');
        }

        function fail() {
            alert('등록에 실패하였습니다.');
            location.replace('/articles');
        }

        httpRequestWithFormData('POST', '/api/articles', formData, success, fail);
    });
}

// FormData를 사용하여 파일 업로드를 포함한 HTTP 요청을 보내는 함수
function httpRequestWithFormData(method, url, formData, success, fail) {
debugger;
    fetch(url, {
        method: method,
        headers: {
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
        },
        body: formData,
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
                                'Content-Type': 'multipart/form-data',
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
                            httpRequestWithFormData(method, url, formData, success, fail);
                        })
                        .catch(error => fail());
                } else {
                    return fail();
                }
    });
}



// 쿠키를 가져오는 함수
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


window.onload = function() {
    getRankings();
};

function getRankings() {

    // 랭킹을 가져오기 위해 Ajax 요청 보내기
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "/rankings");
    xhr.onload = function() {
        if (xhr.status === 200) {
            // 화면에 랭킹 표시하기
            var data = xhr.responseText;
            data = data.replace(/<[^>]*>/g, "");
            for (var i = 0; i < data.length; i++) {
                var ranking = data[i];
                var li = document.createElement("li");
                li.innerHTML = `${ranking.author} : ${ranking.score}`;
                $("#ranking-list").append(li);
            }
        } else {
            console.log("Error: " + xhr.status);
        }
    };
    xhr.send();
}


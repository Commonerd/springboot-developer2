function toggleLike(likeButton) {
    const articleId = parseInt(likeButton.dataset.itemId, 10);
    const author = likeButton.dataset.author;

    async function success(response) {
        try {
            const data = await response.json();

            if (data.error) {
                // 에러 메시지가 있는 경우에만 처리
                alert(data.error);
                return;
            }

            const likeCount = document.getElementById(`likeCount-${articleId}`);
            if (likeCount) {
                likeCount.textContent = data.likeCount;
                likeButton.disabled = false;

                const isLiked = isButtonLiked(likeButton);
                updateButtonState(likeButton, isLiked);
            } else {
                console.error(`Element 'likeCount-${articleId}' not found.`);
            }
        } catch (error) {
            console.error('Error parsing JSON:', error);
        }
    }

    function fail(response) {
        alert('좋아요 처리 중 문제가 발생했습니다.');
    }

    const isLiked = isButtonLiked(likeButton);
    const method = isLiked ? 'DELETE' : 'POST';
    const url = isLiked ? '/cancelLikeArticle' : '/likeArticle';
    likeHttpRequest(method, url, JSON.stringify({ articleId: articleId, userId: author }), success, fail);
}



function isButtonLiked(likeButton) {
    return likeButton.textContent === '♡' || likeButton.querySelector('.liked');
}

function updateButtonState(likeButton, isLiked) {
    likeButton.textContent = isLiked ? '♥' : '♡';
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
async function likeHttpRequest(method, url, body, success, fail) {
    const response = await fetch(url, {
        method: method,
        headers: {
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
            'Content-Type': 'application/json',
        },
        body: body,
    });

    if (response.status === 200 || response.status === 201) {
        success(response);
    } else if (response.status === 401) {
        const refresh_token = getCookie('refresh_token');
        if (refresh_token) {
            const tokenResponse = await fetch('/api/token', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    refreshToken: getCookie('refresh_token'),
                }),
            });

            if (tokenResponse.ok) {
                const tokenData = await tokenResponse.json();
                localStorage.setItem('access_token', tokenData.accessToken);
                return likeHttpRequest(method, url, body);
            } else {
                throw new Error('토큰 재발급 중 문제가 발생했습니다.');
            }
        }
    } else {
        throw new Error('좋아요 처리 중 문제가 발생했습니다.');
    }
}

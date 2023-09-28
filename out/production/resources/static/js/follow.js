    // フォローボタンを押すとfollowHttpRequest呼び出し
    function addFollow(button) {
        const id = parseInt(button.dataset.itemId, 10);
        button.addEventListener('click', event => {
            body = JSON.stringify({});

            function success(response) {
                alert("フォロー登録を成功しました。");
            };

            function fail(response) {
                alert(response);
            };
            followHttpRequest('POST', '/follow/' + id, body, success, fail)
        });
    }



    // アンフォローボタンを押すとfollowHttpRequest呼び出し
    function deleteFollow(button) {

        const id = parseInt(button.dataset.itemId, 10);
        button.addEventListener('click', event => {
            body = JSON.stringify({});

            function success(response) {
                alert("フォロー削除を成功しました。");
            };

            function fail(response) {
                alert(response);
            };
            followHttpRequest('DELETE', '/unfollow/' + id, body, success, fail)
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
    function followHttpRequest(method, url, body, success, fail) {
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
                        followHttpRequest(method, url, body, success, fail);
                    })
                    .catch(error => fail());
            } else {
                return fail();
            }
        });
    }


    // 팔로잉 목록 보기
    document.getElementById("following-count").addEventListener("click", function() {
        // 현재 로그인한 사용자 아이디 가져오기
        const currentUserId = document.getElementById("current-user").textContent;

        // Send a GET request to the server to fetch the following users list
        currentUserHttpRequest("GET", "/get-following/" + currentUserId, function(responseData) {
            // 팔로잉 목록을 보여주는 팝업창을 생성하고, 목록을 채워넣습니다.
            showListPopup("フォロー中", "/get-following/", responseData);
        }, function() {
            alert("팔로잉 목록을 가져오는데 실패했습니다.");
        });
    });

    // 팔로워 목록 보기
    document.getElementById("follower-count").addEventListener("click", function() {
        // 현재 로그인한 사용자 아이디 가져오기
        const currentUserId = document.getElementById("current-user").textContent;

        // Send a GET request to the server to fetch the follower users list
        currentUserHttpRequest("GET", "/get-followers/" + currentUserId, function(responseData) {
            // 팔로워 목록을 보여주는 팝업창을 생성하고, 목록을 채워넣습니다.
            showListPopup("フォロワー", "/get-followers/", responseData);
        }, function() {
            alert("팔로워 목록을 가져오는데 실패했습니다.");
        });
    });

    // 팔로잉/팔로워 목록을 보여주는 팝업창을 생성하고, 목록을 채워넣는 함수
    function showListPopup(title, url, dataList) {
        // 팝업창 요소 생성
        const popupContainer = document.createElement("div");
        popupContainer.classList.add("popup-container");

        const popup = document.createElement("div");
        popup.classList.add("popup");

        const popupTitle = document.createElement("h3");
        popupTitle.textContent = title;

        const closeBtn = document.createElement("button");
        closeBtn.textContent = "戻る";
        closeBtn.style.backgroundColor = "rgb(80, 80, 80)";
        closeBtn.style.color = "#fff";
        closeBtn.style.border = "none";
        closeBtn.style.borderRadius = "4px";
        closeBtn.style.padding = "8px 16px";
        closeBtn.style.right = "15px"
        closeBtn.style.cursor = "pointer";

        closeBtn.addEventListener("click", function() {
            closePopup(popupContainer);
        });

        // 팝업창에 목록 채우기
        const list = document.createElement("ul");
        dataList.forEach(function(item) {
            const listItem = document.createElement("li");
            listItem.textContent = item;

            // 언팔로우 버튼을 생성하여 목록에 추가
            const unfollowButton = createUnfollowButton(item);
            listItem.appendChild(unfollowButton);

            list.appendChild(listItem);
        });

        // 팝업창 구조 생성
        popup.appendChild(popupTitle);
        popup.appendChild(list);
        popup.appendChild(closeBtn);
        popupContainer.appendChild(popup);

        // 팝업창에 스타일 적용
        //applyPopupStyles(popupContainer, popup);

        // body에 팝업창 추가
        document.body.appendChild(popupContainer);
        debugger;
        // 리프레시 및 페이지처리
        url === "/get-following/" ? refreshFollowingList() : refreshFollowerList();

        // 팝업창에 스타일 적용
        applyPopupStyles(popupContainer, popup);

    }


    // 팝업창에 스타일을 적용하는 함수
    function applyPopupStyles(popupContainer, popup) {
        // 팝업창 스타일 적용
        popupContainer.style.position = "fixed";
        popupContainer.style.top = "0";
        popupContainer.style.left = "0";
        popupContainer.style.width = "100%";
        popupContainer.style.height = "100%";
        popupContainer.style.display = "flex";
        popupContainer.style.justifyContent = "center";
        popupContainer.style.alignItems = "center";

        // 팝업창 내용 스타일 적용
        popup.style.backgroundColor = "#fff";
        popup.style.padding = "30px";
        popup.style.borderRadius = "10px";
        popup.style.boxShadow = "0 0 10px rgba(0, 0, 0, 0.3)";
        popup.style.backgroundColor = "#f8f9fa"; // 옅은 회색

        // 닫기 버튼 위치 조정
        const closeBtn = popup.querySelector("button");
        closeBtn.style.position = "absolute";
        closeBtn.style.top = "10px";
        closeBtn.style.right = "10px";

        // 이전, 다음 버튼 위치 조정
        const btnSection = popup.querySelector(".btn-section");
        if (btnSection) {
            btnSection.style.position = "absolute";
            btnSection.style.bottom = "20px";
            btnSection.style.right = "3px";
        }
    }

    // 팝업창을 닫는 함수
    function closePopup(popupContainer) {
        popupContainer.remove();
    }

    // Function to create unfollow button
    function createUnfollowButton(userId) {
        const unfollowButton = document.createElement("button");
        unfollowButton.textContent = "アンフォロー";
        unfollowButton.classList.add("unfollow-button");
        unfollowButton.style.backgroundColor = "#007bff";
        unfollowButton.style.color = "#fff";
        unfollowButton.style.fontSize = "18px";
        unfollowButton.style.border = "none";
        unfollowButton.style.borderRadius = "5px";
        unfollowButton.style.padding = "8px 16px";
        unfollowButton.style.margin = "5px 0px 5px 20px";
        unfollowButton.style.cursor = "pointer";
        unfollowButton.addEventListener("click", function() {
            // Call a function that sends an unfollow request to the server
            deleteListFollow(userId);
        });
        return unfollowButton;
    }

    // フォローリストでアンフォローボタンを押す時、followHttpRequest関数呼び出し
    function deleteListFollow(userId, popupContainer) {

        const body = JSON.stringify({
            userId: userId
        });

        function success(popupContainer, response) {
            alert(response);

            refreshFollowingList(); // 팔로잉 목록 새로고침
        };

        function fail(response) {
            alert(response);
        };
        followHttpRequest('DELETE', '/list-unfollow/', body, success(popupContainer), fail);
    }

    // 팔로잉 목록 페이징 관련 변수
    let currentPage = 1;

    // 팔로잉 목록 페이징 처리 함수
    function refreshFollowingList() {
        const currentUserId = document.getElementById("current-user").textContent;
        currentUserHttpRequest("GET", "/get-following/" + currentUserId, function(responseData) {
            const popup = document.querySelector(".popup");
            const list = popup.querySelector("ul");
            list.innerHTML = "";

            const itemsPerPage = 5;
            const startIndex = (currentPage - 1) * itemsPerPage;
            const endIndex = startIndex + itemsPerPage;
            const paginatedData = responseData.slice(startIndex, endIndex);

            paginatedData.forEach(function(item) {
                const listItem = document.createElement("li");
                listItem.textContent = item;
                const unfollowButton = createUnfollowButton(item);
                listItem.appendChild(unfollowButton);
                list.appendChild(listItem);
            });

            // 기존 버튼 삭제
            removePaginationButtons();

            const previousBtn = document.createElement("button");
            previousBtn.textContent = "以前";

            previousBtn.addEventListener("click", function() {
                if (currentPage > 1) {
                    currentPage--;
                    refreshFollowingList();
                }
            });

            const nextBtn = document.createElement("button");
            nextBtn.textContent = "以後";
            nextBtn.addEventListener("click", function() {
                if (currentPage < Math.ceil(responseData.length / itemsPerPage)) {
                    currentPage++;
                    refreshFollowingList();
                }
            });

            const btnSection = document.createElement("div");
            btnSection.classList.add("btn-section");
            btnSection.appendChild(previousBtn);
            btnSection.appendChild(nextBtn);
            popup.appendChild(btnSection);

            // 버튼에 스타일 적용
            applyButtonStyles(previousBtn);
            applyButtonStyles(nextBtn);
            positionPaginationButtons(popup, btnSection);

        }, function() {
            alert("팔로잉 목록을 가져오지 못했습니다.");
        });




        // 팝업창을 닫을 때 기존 버튼들도 삭제하는 함수
        function removePaginationButtons() {
            const popup = document.querySelector(".popup");
            const btnSection = popup.querySelector(".btn-section");
            if (btnSection) {
                btnSection.remove();
            }
        }


        // 버튼에 스타일 적용하는 함수
        function applyButtonStyles(button) {
            button.style.padding = "8px 16px";
            button.style.margin = "0 4px";
            button.style.backgroundColor = "rgb(80, 80, 80)";
            button.style.color = "#fff";
            button.style.border = "none";
            button.style.borderRadius = "5px";
            button.style.cursor = "pointer";
        }

        // 페이징 버튼을 우하단에 위치시키는 함수
        function positionPaginationButtons(popup, btnSection) {
            popup.style.position = "relative";
            btnSection.style.position = "absolute";
            btnSection.style.bottom = "20px";
            btnSection.style.right = "3px";
        }
    }

    // 팔로워 목록 페이징 처리 함수
    function refreshFollowerList() {
        const currentUserId = document.getElementById("current-user").textContent;
        currentUserHttpRequest("GET", "/get-followers/" + currentUserId, function(responseData) {
            const popup = document.querySelector(".popup");
            const list = popup.querySelector("ul");
            list.innerHTML = "";

            const itemsPerPage = 5;
            const startIndex = (currentPage - 1) * itemsPerPage;
            const endIndex = startIndex + itemsPerPage;
            const paginatedData = responseData.slice(startIndex, endIndex);

            paginatedData.forEach(function(item) {
                const listItem = document.createElement("li");
                listItem.textContent = item;
                const unfollowButton = createUnfollowButton(item);
                /*listItem.appendChild(unfollowButton);*/
                list.appendChild(listItem);
            });

            // 기존 버튼 삭제
            removePaginationButtons();

            const previousBtn = document.createElement("button");
            previousBtn.textContent = "以前";

            previousBtn.addEventListener("click", function() {
                if (currentPage > 1) {
                    currentPage--;
                    refreshFollowerList();
                }
            });

            const nextBtn = document.createElement("button");
            nextBtn.textContent = "以後";
            nextBtn.addEventListener("click", function() {
                if (currentPage < Math.ceil(responseData.length / itemsPerPage)) {
                    currentPage++;
                    refreshFollowerList();
                }
            });

            const btnSection = document.createElement("div");
            btnSection.classList.add("btn-section");
            btnSection.appendChild(previousBtn);
            btnSection.appendChild(nextBtn);
            popup.appendChild(btnSection);

            // 버튼에 스타일 적용
            applyButtonStyles(previousBtn);
            applyButtonStyles(nextBtn);
            positionPaginationButtons(popup, btnSection);

        }, function() {
            alert("팔로잉 목록을 가져오지 못했습니다.");
        });

        // 팝업창을 닫을 때 기존 버튼들도 삭제하는 함수
        function removePaginationButtons() {
            const popup = document.querySelector(".popup");
            const btnSection = popup.querySelector(".btn-section");
            if (btnSection) {
                btnSection.remove();
            }
        }


        // 버튼에 스타일 적용하는 함수
        function applyButtonStyles(button) {
            button.style.padding = "8px 16px";
            button.style.margin = "0 4px";
            button.style.backgroundColor = "rgb(80, 80, 80)";
            button.style.color = "#fff";
            button.style.border = "none";
            button.style.borderRadius = "5px";
            button.style.cursor = "pointer";
        }

        // 페이징 버튼을 우하단에 위치시키는 함수
        function positionPaginationButtons(popup, btnSection) {
            popup.style.position = "relative";
            btnSection.style.position = "absolute";
            btnSection.style.bottom = "20px";
            btnSection.style.right = "3px";
        }

    }

function searchUserArticles() {
    // 이메일 ID 클릭 이벤트 연결
    const emailIds = popup.querySelectorAll("li");
    emailIds.forEach(emailId => {
        emailId.addEventListener("click", function() {
            const keyword = this.textContent;
            searchArticles(keyword);
        });
    });
}
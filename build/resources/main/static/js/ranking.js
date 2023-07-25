document.addEventListener('DOMContentLoaded', function() {
  // /top-posts API를 호출하고 데이터를 가져옵니다.
  fetch('/rankings')
    .then(response => response.json())
    .then(data => {
      const topPublishers = document.getElementById('topPublishers');

      // 이전에 추가된 랭킹 목록을 지웁니다.
      topPublishers.innerHTML = '';

      // 데이터를 순회하며 리스트 아이템을 생성하여 HTML에 추가합니다.
      data.forEach((data, index) => {
        const listItem = document.createElement('li');
        // 데이터를 받아 문자열로 바꾸고 ,는 :로, [,],"는 공백으로 만듭니다. 순위에 대한 유저정보를 꺠끗하게 보이게 하기 위함.
        modifiedData1 = JSON.stringify(data);
        const modifiedData2 = modifiedData1.replace(',', ' : ')
        const charactersToRemove = /[\[\],"]/g;
        const modifiedData3 = modifiedData2.replace(charactersToRemove, '');

        // 1위는 금메달, 2위는 은메달, 3위는 동메달을 표시합니다.
                if (index === 0) {
                  listItem.innerHTML = `<img src="/img/gold.png" class="medal" style="width: 15px;"> ${index + 1}. ${modifiedData3}점`;
                } else if (index === 1) {
                  listItem.innerHTML = `<img src="/img/silver.png" class="medal" style="width: 15px;"> ${index + 1}. ${modifiedData3}점`;
                } else if (index === 2) {
                  listItem.innerHTML = `<img src="/img/bronze.png" class="medal" style="width: 15px;"> ${index + 1}. ${modifiedData3}점`;
                } else {
                  listItem.textContent = `${index + 1}. ${modifiedData3}점`;
                }

        topPublishers.appendChild(listItem);
      });
    })
    .catch(error => {
      console.error('데이터를 가져오는 중 오류가 발생했습니다.', error);
    });
});
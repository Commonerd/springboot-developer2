document.addEventListener('DOMContentLoaded', function() {
  // /top-posts API를 호출하고 데이터를 가져옵니다.
  fetch('/top-posts')
    .then(response => response.json())
    .then(data => {
      const topPublishers = document.getElementById('topPublishers');

      // 데이터를 순회하며 리스트 아이템을 생성하여 HTML에 추가합니다.
      data.forEach((article, index) => {
        const listItem = document.createElement('li');
        listItem.textContent = `${index + 1}. ${article.title} (${article.postCount} 개의 게시물)`;
        topPublishers.appendChild(listItem);
      });
    })
    .catch(error => {
      console.error('데이터를 가져오는 중 오류가 발생했습니다.', error);
    });
});
 document.querySelector('.custom-file-input').addEventListener('change', function (e) {
    var files = e.target.files;
    var fileNames = Array.from(files).map(file => file.name).join(', ');
    var nextSibling = e.target.nextElementSibling;
    if (files.length > 3) {
      alert('3つ以下のファイルしか選択できません。');
      e.target.value = '';
      nextSibling.innerText = '파일 선택...';
    } else {
      nextSibling.innerText = fileNames || '파일 선택...';
    }
  });


 // 이미지 파일 추가 및 삭제 기능
 document.querySelector('input[name="imageFiles"]').addEventListener('change', event => {
     const fileList = event.target.files;
     const imageList = document.querySelector('.image-list .row');

     for (const file of fileList) {
         const imageContainer = document.createElement('div');
         imageContainer.className = 'col-md-3 image-container';

         const imageItem = document.createElement('img');
         imageItem.className = 'image-item img-fluid';
         imageItem.src = URL.createObjectURL(file);

         const deleteButton = document.createElement('button');
         deleteButton.type = 'button';
         deleteButton.className = 'btn btn-danger btn-sm mt-2';
         deleteButton.textContent = '削除';
         deleteButton.addEventListener('click', function() {
             imageContainer.remove();
         });

         imageContainer.appendChild(imageItem);
         imageContainer.appendChild(deleteButton);
         imageList.appendChild(imageContainer);
     }
 });

 // 이미지 삭제 함수
 function removeImage(button) {
     const imageId = button.getAttribute('data-image-id');
     const imageContainer = button.closest('.image-container');
     imageContainer.remove();
 }

 // 이미지 삭제 함수
 function removeImage(button) {
     const imageId = button.getAttribute('data-image-id');
     const imageContainer = button.closest('.image-container');

     // 서버에 이미지 삭제 요청 보내기
         fetch(`/api/images/${imageId}`, {
             method: 'DELETE',
             headers: {
                  Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                 'Content-Type': 'application/json'
             },
         })
         .then(response => {
             if (response.status === 200) {
                 imageContainer.remove(); // 클라이언트에서 이미지 삭제
             } else {
                 console.error('Failed to delete image on server.');
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
         })
         .catch(error => {
             console.error('Error deleting image:', error);
         });
 }
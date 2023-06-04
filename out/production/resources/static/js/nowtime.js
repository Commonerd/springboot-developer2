function getCurrentTime() {
    var now = new Date();
    var year = now.getFullYear();
    var month = ("0" + (now.getMonth() + 1)).slice(-2);
    var day = ("0" + now.getDate()).slice(-2);
    var dayOfWeek = ['일', '월', '화', '수', '목', '금', '토'][now.getDay()];
    var hour = ("0" + now.getHours()).slice(-2);
    var minute = ("0" + now.getMinutes()).slice(-2);
    var second = ("0" + now.getSeconds()).slice(-2);
    var currentTime = year + "년 " + month + "월 " + day + "일 " + dayOfWeek + "요일 " + hour + "시 " + minute + "분 " + second + "초";
    document.getElementById("current-time").textContent = currentTime;
  }

  setInterval(getCurrentTime, 500);
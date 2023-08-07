function getCurrentTime() {
    var now = new Date();
    var year = now.getFullYear();
    var month = ("0" + (now.getMonth() + 1)).slice(-2);
    var day = ("0" + now.getDate()).slice(-2);
    var dayOfWeek = ['日', '月', '火', '水', '木', '金', '土'][now.getDay()];
    var hour = ("0" + now.getHours()).slice(-2);
    var minute = ("0" + now.getMinutes()).slice(-2);
    var second = ("0" + now.getSeconds()).slice(-2);
    var currentTime = year + "年 " + month + "月 " + day + "日 " + dayOfWeek + "曜日 " + hour + "時 " + minute + "分 " + second + "秒";
    document.getElementById("current-time").textContent = currentTime;
  }

  setInterval(getCurrentTime, 500);
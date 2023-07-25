 // 타이머 상태
  const TimerState = {
    STOPPED: "stopped",
    RUNNING: "running",
    PAUSED: "paused",
  };

  // 카운트다운 타이머 객체
  let countdownTimer = null;
  let timerState = TimerState.STOPPED;
  let remainingTime = 0;

  // DOM 요소 가져오기
  const hoursElement = document.getElementById("countdown-hours");
  const minutesElement = document.getElementById("countdown-minutes");
  const secondsElement = document.getElementById("countdown-seconds");
  const setTimeButton = document.getElementById("set-time-btn");
  const startButton = document.getElementById("start-btn");
  const pauseButton = document.getElementById("pause-btn");
  const stopButton = document.getElementById("stop-btn");
  const resetButton = document.getElementById("reset-btn");

  // 이벤트 리스너 등록
  setTimeButton.addEventListener("click", setTime);
  startButton.addEventListener("click", startTimer);
  pauseButton.addEventListener("click", pauseTimer);
  stopButton.addEventListener("click", stopTimer);
  resetButton.addEventListener("click", resetTimer);

  // 시간 설정
  function setTime() {
    const hours = parseInt(prompt("時間を入力してください (0~23)"));
    const minutes = parseInt(prompt("分を入力してください (0~59)"));

    if (isNaN(hours) || isNaN(minutes) || hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
      alert("正しい時間を入力してください。");
      return;
    }

    remainingTime = (hours * 60 * 60) + (minutes * 60);
    updateCountdownDisplay();
  }

  // 타이머 시작
  function startTimer() {
    if (timerState === TimerState.RUNNING) {
      return;
    }

    if (remainingTime <= 0) {
      alert("タイマー時間を設定してください。");
      return;
    }

    countdownTimer = setInterval(updateTimer, 1000);
    timerState = TimerState.RUNNING;
  }

  // 타이머 일시정지
  function pauseTimer() {
    if (timerState !== TimerState.RUNNING) {
      return;
    }

    clearInterval(countdownTimer);
    timerState = TimerState.PAUSED;
  }

  // 타이머 중지
  function stopTimer() {
    clearInterval(countdownTimer);
    remainingTime = 0;
    timerState = TimerState.STOPPED;
    updateCountdownDisplay();
  }

  // 타이머 업데이트
  function updateTimer() {
    remainingTime--;

    if (remainingTime <= 0) {
      clearInterval(countdownTimer);
      timerState = TimerState.STOPPED;
      alert("タイマーが終了しました。");
    }

    updateCountdownDisplay();
  }

  // 카운트다운 디스플레이 업데이트
  function updateCountdownDisplay() {
    const hours = Math.floor(remainingTime / 3600);
    const minutes = Math.floor((remainingTime % 3600) / 60);
    const seconds = remainingTime % 60;

    hoursElement.textContent = padZero(hours);
    minutesElement.textContent = padZero(minutes);
    secondsElement.textContent = padZero(seconds);
  }

  // 숫자가 10보다 작을 경우 앞에 0을 붙여 반환
  function padZero(num) {
    return num.toString().padStart(2, "0");
  }
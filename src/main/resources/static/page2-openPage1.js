function openPage1(){
    const width = 850;
    const height = 800;

    //팝업창이 화면 중간에 오도록
    const left = (window.screen.width / 2) - (width / 2);
    const top = (window.screen.height / 2) - (height / 2) -100;

    window.open('page1.html', 'popupWindow', `width=${width},height=${height},left=${left},top=${top},scrollbars=no`);
}
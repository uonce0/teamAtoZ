function openPage1(){ //팝업창 띄우기
    const width = 1300;
    const height = 700;

    //팝업창이 화면 중간에 오도록
    const left = (window.screen.width / 2) - (width / 2);
    const top = (window.screen.height / 2) - (height / 2) -90;

    window.open('page1.html', 'popupWindow', `width=${width},height=${height},left=${left},top=${top},scrollbars=no`);
}

function updateImage(imageUrl) {
    const gImage = document.getElementById("generated-image");
    gImage.src = imageUrl;

    gImage.style.width = "93%";
    gImage.style.height = "93%";
}
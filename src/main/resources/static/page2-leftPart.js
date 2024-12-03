//글자수 최대 제한
const maxLengths={
    title:25,
    content:200
};
function loadPage2(){
    const content = document.getElementById("content");

    content.innerHTML = `모바일 탑승권 도착\n\n[대한항공]KIMHANSUNG\n여정: 2024.11.29, KE005\n서울/인천(ICN) ->라스베이거스(LAS)`
}
//input 창 글자수 계산
function updateCharCount(field){
    const inputField = document.getElementById(field);
    const charCountDisplay = document.getElementById(`${field}-charcount`);
    const currentLength = inputField.value.length;
    const maxLength = maxLengths[field];

    charCountDisplay.textContent = `${currentLength}/${maxLength}`;
}


//input창 클릭시 border색이 진해짐
const inputs = document.querySelectorAll("#title, #content");

inputs.forEach(input => {
    input.addEventListener("focus", function() {
       this.parentNode.classList.add("focused");
    });
    input.addEventListener("blur", function() {
        this.parentNode.classList.remove("focused");
    });
});


//복붙했을때 제한된 글자수보다 클 경우, 복붙이 안됨
function handlePaste(event,field){
    const maxLength = maxLengths[field];
    const contentField = document.getElementById(field);

    //복붙된 내용
    const pastedData = event.clipboardData.getData('text');

    //복붙내용 + 쓰여진 내용> 최대가능한 길이
    if ((contentField.value.length + pastedData.length) > maxLength) {
    event.preventDefault(); // 복붙 방지
    const remainingLength = maxLength - contentField.value.length;
    contentField.value += pastedData.slice(0, remainingLength); // 남은 길이만큼만 붙여넣기
}
}


//제목과 내용에 focus했을 떄 placeholder 사라짐
function handleFocus(field) {
    const inputField = document.getElementById(field);
    if (inputField.value === "") {
        inputField.setAttribute("placeholder", "");
    }
}

//focus를 풀었을때 palceholder 다시 나타남
function handleBlur(field) {
    const inputField = document.getElementById(field);
    const placeholderText = field === "title" ? "제목을 입력해주세요." : "내용을 입력해주세요.";

    if (inputField.value === "") {
        inputField.setAttribute("placeholder", placeholderText); // 입력이 없으면 placeholder 다시 설정
    }
}
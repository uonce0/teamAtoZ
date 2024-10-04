


//이미지 생성 키워드 입력창에서 엔터키를 누르면 태그 추가 --이미지 생성 키워드 파트
function addKey(event) {
    //입력창에서 엔터키를 누르면 & 필드가 비어있지 않을 때만 태그 추가 & 태그 개수가 10개 미만일 때만 태그 추가 & 중복된 태그가 없을 때만 태그 추가-----(앞에서부터 순서대로 기능에 대해 설명함)
    if (event.key === "Enter" && event.target.value.trim() !== "" && document.getElementsByClassName("key").length < 10 && !Array.from(document.getElementsByClassName("key")).some(key => key.textContent.includes(`#${event.target.value.trim()}`))) {
        const keyValue = event.target.value.trim();
        const keyContainer = document.getElementById("key-container");

        // 새 태그 생성
        const newKey = document.createElement("div");
        newKey.classList.add("key");
        newKey.innerHTML = `<span>#${keyValue}</span><button onclick="removeKey(this)">x</button>`;

        // 태그 추가
        keyContainer.insertBefore(newKey, event.target); //생성된 태그를 입력필드 앞에 추가
        event.target.value = "";  // 입력창 초기화
    }
}

function removeKey(button) {
    const key = button.parentElement; //태그 생성 시에 넣었던 button의 부모태그(div)를 가져옴
    key.remove(); //태그 삭제
}


//이미지 생성 버튼을 누르면 이미지 생성 & 버튼 텍스트를 '다시 생성'으로 변경 --이미지 생성 파트
function makeImage() {
    // 버튼 텍스트 변경
    document.getElementById("make-button").textContent = "다시 생성";
}
let keyWords = []; // 백으로 전달할 키워드를 배열에 저장함

// 이미지 생성 키워드 입력창에서 엔터키를 누르면 태그 추가
function addKey(event) {
    const keyValue = event.target.value.trim();
    const keyContainer = document.getElementById("key-container");

    // 입력창에서 엔터키를 누르면 & 필드가 비어있지 않을 때만 태그 추가
    if (event.key === "Enter" && keyValue !== "" && keyWords.length < 10) {
        if (!keyWords.includes(keyValue)) {
            keyWords.push(keyValue); // 키워드 배열에 추가

            // 새 태그 생성
            const newKey = document.createElement("div");
            newKey.classList.add("key");
            newKey.innerHTML = `<span>#${keyValue}</span><button onclick="removeKey(this)">x</button>`;

            // 태그 추가
            keyContainer.insertBefore(newKey, event.target); // 생성된 태그를 입력필드 앞에 추가
            console.log(keyWords); // 배열 상태 출력
            event.target.value = ""; // 입력창 초기화
        } else {
            alert("이미 추가된 키워드입니다."); // 중복된 키워드 경고
            event.target.value = ""; // 입력창 초기화
        }
    } else if (keyWords.length >= 10) {
        alert("최대 10개까지 입력 가능합니다.");
        event.target.value = ""; // 입력창 초기화
    }
}

function removeKey(button) {
    const key = button.parentElement; // 태그 생성 시에 넣었던 button의 부모태그(div)를 가져옴
    const keyValue = key.textContent.slice(1,-1); // # 문자를 제거와 뒤에생기는 x표시 삭제

    // 키워드를 배열에서 삭제
    keyWords = keyWords.filter(k => k !== keyValue);
    console.log(keyWords); // 현재 배열 상태 출력
    key.remove(); // 태그 삭제
}

//이미지 생성 버튼을 누르면 이미지 생성 & 버튼 텍스트를 '다시 생성'으로 변경 --이미지 생성 파트
function makeImage() {
    // 버튼 텍스트 변경
    document.getElementById("make-button").textContent = "다시 생성";
}

//사용자가 프롬프트를 수정할 경우 자동으로 높이 조정
const userInputprompt = document.getElementById("generated-prompt");
userInputprompt.addEventListener('input',()=>{
    userInputprompt.style.height = 'auto';
    userInputprompt.style.height = `${userInputprompt.scrollHeight}px`; //내용에 맞게 높이 조정
});


//로딩바
const loader1 = document.getElementById('spinner1');
const loader2 = document.getElementById('spinner2');

const promptButton = document.getElementById('make-prompt');
const aiButton = document.getElementById('make-button');

//왼쪽 프롬프트 생성 로딩바
function showLoader1() {
    loader1.style.display = 'block';
    aiButton.disabled = true;
}
function hideLoader1() {
    loader1.style.display = 'none';
    aiButton.disabled = false;
}


//오른쪽 이미지 생성 로딩바
function showLoader2() {
    loader2.style.display = 'block';
    promptButton.disabled = true;
    aiButton.disabled = true;
}
function hideLoader2() {
    loader2.style.display = 'none';
    promptButton.disabled = false;
    aiButton.disabled = false;
}
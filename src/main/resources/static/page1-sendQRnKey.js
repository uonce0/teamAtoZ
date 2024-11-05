const fileInput = document.getElementById("qr-file-input");
const imgInfo = document.getElementById("qr-img-info");
const removeButton = document.getElementById("remove-qrfile-button");
const img = document.getElementById("generated-image");
const qrCombineButton = document.getElementById('combine-button');


document.addEventListener("DOMContentLoaded", () => {
    //파일 선택시 - 이미지크기, 위치 텍스트칸 보여줌
    fileInput.addEventListener("change", () =>{
        if(fileInput.files.length>0){
            removeButton.style.display = "inline-block";
            imgInfo.style.display= "block";
            if(img.src !== ""){ //*********문제 있음.
                qrCombineButton.style.display = "block";
            }
        }else{
            removeButton.style.display = "none";
            imgInfo.style.display= "none";
        }
    });
});

//선택된 파일 삭제
function removeFile() {
    fileInput.value = "";
    removeButton.style.display = "none";
    imgInfo.style.display = "none"; // 이미지 설정 필드 숨기기
    qrCombineButton.style.display = "none"; // QR 합성 버튼 숨기기
    console.log("QR 파일을 삭제했습니다.");
}

//백으로 QR 이미지 정보 전달
const handleImageCombine = async () => {
    const formData = new FormData();

    //Qr 코드 파일 유무 확인하고 전송
    //QR 크기, 위치 정보를 백으로 전송
    if (fileInput.files.length > 0) {
        const imgSize = document.getElementById("qr-img-size").value;
        const imgLocation = document.getElementById("qr-img-location").value;
        
        formData.append('qrFile', fileInput.files[0]);
        formData.append('qrPresent',true); //qr 이미지가 있음을 백으로 전송
        formData.append('imgSize', imgSize);
        formData.append('imgLocation', imgLocation);
    }else {
        formData.append('qrPresent',false); //qr 이미지가 없음을 백으로 전송
        formData.append('imgSize', "");
        formData.append('imgLocation', "");
        alert("QR 파일을 업로드해주세요"); //qr 파일이 없는데, QR 합성 버튼을 누른경우
    }

    // 서버로 QR 이미지 정보 전송
    try {
        showLoader(); // 로딩바 표시
        img.style.opacity = 0.5; // 이미지 투명도 조절

        const response = await fetch('http://localhost:8080/image/qrCombine', {
            method: 'POST',
            body: formData,
        });

        if (response.ok) {
            hideLoader(); // 로딩바 숨김

            console.log("QR 이미지 정보 전송 성공");
            const responseData = await response.text();
            console.log("서버 응답:", responseData);

            //QR 이미지 출력
            img.src = responseData;
            img.style.opacity = 1; // 이미지 투명도 조절
        } else {
            console.error("전송 실패:", response.status, response.statusText);
            img.style.opacity = 1; // 이미지 투명도 조절
            hideLoader(); // 로딩바 숨김
        }
    } catch (error) {
        console.error("전송 중 오류:", error);
        img.style.opacity = 1; // 이미지 투명도 조절
        hideLoader(); // 로딩바 숨김
    }
};


//이미지 생성 프롬프트 저장
let currentPrompt = "";

//백으로 키워드 정보 전송&프롬프트 반환
const handlePromptGeneration = async () => {
    const formData = new FormData();

    document.getElementById("generated-prompt").style.display = "none"; // 프롬프트 숨기기

    // 필요한 필드 추가
    formData.append('userCommand', '명령어 입력');
    formData.append('destination', '목적지 입력');
    formData.append('season', '계절 입력');

    //키워드 배열 유무 확인하고 전송
    if(keyWords.length>0){
        //키워드가 있을 경우 백으로 키워드배열 전송
        keyWords.forEach((keyword) => {
            formData.append('keyWords',keyword);
        });
        document.getElementById("make-prompt").textContent = "프롬프트 재생성"; // 버튼 텍스트 변경
    }else{
        //키워드 입력이 없을 경우
        alert("이미지 생성 키워드를 입력해주세요.");
        return;
    }

    //서버로 이미지 조건 정보 post
    try {
        showLoader(); // 로딩바 표시

        const response = await fetch('http://localhost:8080/image/prompt', {
            method: 'POST',
            body: formData,
        });

        if (response.ok) {
            hideLoader(); // 로딩바 숨김

            console.log("키워드 정보 전송 성공");
            const responseData = await response.text(); // 응답 데이터 처리
            console.log("서버 응답:", responseData);

            // 프롬프트 출력
            let prompt = document.getElementById("generated-prompt");
            prompt.textContent = responseData;
            prompt.style.display = "block";

            currentPrompt = responseData; // 프롬프트 저장
        } else {
            console.error("전송 실패:", response.status, response.statusText);
            hideLoader(); // 로딩바 숨김
        }
    } catch (error) {
        console.error("전송 중 오류:", error);
        hideLoader(); // 로딩바 숨김
    }
};




const handleImageGeneration = async () => {
    const formData = new FormData();

    document.getElementById("generated-image").style.display = "none"; // 이미지 숨기기

    // 이미지 생성 요청에 사용할 프롬프트가 있는지 확인
    if (currentPrompt.length > 0) {
        formData.append('generatedPrompt', currentPrompt);
        document.getElementById("make-button").textContent = "이미지 재생성"; // 버튼 텍스트 변경
    } else {
        alert("먼저 프롬프트를 생성해주세요.");
        return;
    }

    //서버로 이미지 조건 정보 post
    try {
        showLoader(); // 로딩바 표시

        const response = await fetch('http://localhost:8080/image/generate', {
            method: 'POST',
            body: formData,
        });

        if (response.ok) {
            hideLoader(); // 로딩바 숨김

            console.log("프롬프트 정보 전송 성공");
            const responseData = await response.text(); // 응답 데이터 처리
            console.log("서버 응답:", responseData);

            //배경 이미지 출력
            img.src = responseData;
            img.style.display = "block";

            //이미지 생성 시 선택 버튼 보임
            let chooseImg = document.getElementById("choose-button");
            chooseImg.style.display=  "block";

            //QR 이미지가 있을 경우 QR 합성 버튼 보임
            if(fileInput.files.length>0){
                qrCombineButton.style.display = "block";
            }else{
                qrCombineButton.style.display = "none";
            }

        } else {
            console.error("전송 실패:", response.status, response.statusText);
            hideLoader(); // 로딩바 숨김
        }
    } catch (error) {
        console.error("전송 중 오류:", error);
        hideLoader(); // 로딩바 숨김
    }
};





//로딩바
const loader = document.getElementById('spinner');
const aiButton = document.getElementById('make-button');
const qrButton = document.getElementById('combine-button');

function showLoader() {
    loader.style.display = 'block';
    aiButton.disabled = true;
    qrButton.disabled = true;
}

function hideLoader() {
    loader.style.display = 'none';
    aiButton.disabled = false;
    qrButton.disabled = false;
}
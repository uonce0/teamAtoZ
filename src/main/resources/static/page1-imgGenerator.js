//프롬프트 글자수에 맞게 height 조정
function adjustPromptHeight(textarea) {
    textarea.style.height = 'auto'; // height를 초기화
    textarea.style.height = `${textarea.scrollHeight}px`;
}

//백으로 키워드 정보 전송&프롬프트 반환
const handlePromptGeneration = async () => {
    const formData = new FormData();
    document.getElementById("generated-prompt").style.display = "none"; // 프롬프트 숨기기

    //키워드 배열 유무 확인
	if (keyWords.length === 0) {
        alert("이미지 생성 키워드를 입력해주세요.");
        return;
    }
	keyWords.forEach(keyword => formData.append('keyWords', keyword));

	const promptButton = document.getElementById("make-prompt");
    promptButton.disabled = true; //프롬프트 생성버튼 비활성화
    promptButton.textContent = "프롬프트 재생성";

	document.getElementById("generated-image").style.display = "none"; // 생성된 이미지 숨기기
    document.getElementById("container-modifying").style.display = "none"; // 이미지 편집 부분 숨기기
    document.getElementById("container-btn").style.display = "none"; // 편집 및 선택버튼 숨기기

    //서버로 이미지 조건 정보 post
    try {
        showLoader1(); // 로딩바 표시

        const response = await fetch('http://localhost:8080/image/prompt', {
            method: 'POST',
            body: formData,
        });
		hideLoader1(); // 로딩바 숨김

        if (response.ok) {
            console.log("키워드 정보 전송 성공");
            const responseData = await response.json(); // 응답 데이터 처리
            korPrompt = responseData.korPrompt;
            console.log("한국어 프롬프트:", korPrompt);

            // 프롬프트 UI 출력
            let prompt = document.getElementById("generated-prompt");
            prompt.value = korPrompt;
            prompt.style.display = "block";
            adjustPromptHeight(prompt); //텍스트 수대로 prompt height조절
        } else {
            console.error("전송 실패:", response.status, response.statusText);
        }
    } catch (error) {
        console.error("전송 중 오류:", error);
    } finally {
    promptButton.disabled = false; //프롬프트 생성버튼 활성화
    }
};

//영어로 번역후 이미지 생성
const handleImageGeneration = async () => {
    const formData = new FormData();

    document.getElementById("welcome-word").style.display = "none"; // 환영 문구 숨기기
    document.getElementById("generated-image").style.display = "none"; // 이미지 숨기기
    document.getElementById("container-modifying").style.display = "none"; // 이미지 편집 부분 숨기기
    document.getElementById("container-btn").style.display = "none"; // 편집 및 선택버튼 숨기기
    document.getElementById("konva-container").style.display = "none"; // Konva 컨테이너 숨기기
    document.getElementById("container-stickerColor").style.display = "none"; // 스티커 색상 선택 숨기기
    document.getElementById("container-sticker").style.display = "none"; // 스티커 선택 숨기기

    const inputPrompt = document.getElementById("generated-prompt");

    try{
        showLoader2(); // 로딩바 표시
        const response = await fetch('http://localhost:8080/image/translateAndGenerate',{
            method:'POST',
            body: JSON.stringify({ text: inputPrompt.value }),
            headers: { 'Content-Type': 'application/json' },
        });

        if (response.ok) {
            const responseData = await response.json();
            console.log("생성된 이미지 URL:", responseData.imageUrl);

            // 이미지 표시
            const img = document.getElementById("generated-image");
            img.src = responseData.imageUrl;
            img.style.display = "block";

            document.getElementById("container-btn").style.display = "block"; // 편집 및 선택버튼 활성화
            document.getElementById("container-modifying").style.display = "block"; // 이미지 편집 부분 활성화
            document.getElementById("container-stickerColor").style.display = "block"; // 스티커 색상 선택 숨기기
            document.getElementById("container-sticker").style.display = "block"; // 스티커 선택 숨기기

        } else {
            console.error("영어로 번역과 이미지 생성 오류가 발생했습니다.", response.status, response.statusText);
        }
    } catch (error) {
        console.error("번역 및 이미지 생성 중 오류:", error);
    }finally {
        hideLoader2();
    }
};

const handleSendImage = async () => {
	const imageSrc = document.getElementById("generated-image").src;

    if (!imageSrc) {
        alert("먼저 이미지를 생성해주세요.");
        return;
    }

    try {
        // 이미지 전달 후 page1 팝업 닫기
        if (window.opener) {
            window.opener.updateImage(imageSrc);
            window.close();
        }
    } catch (error) {
        console.error("이미지 전달 중 오류:", error);
        alert("이미지 전달 중 오류가 발생했습니다.");
    }
};

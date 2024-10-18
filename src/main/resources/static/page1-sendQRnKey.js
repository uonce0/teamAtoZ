const handleImageGeneration = async () => {
    const formData = new FormData();
    const fileInput = document.getElementById("qr-file-input");

    // 필요한 필드 추가
    formData.append('userCommand', '명령어 입력');
    formData.append('destination', '목적지 입력'); 
    formData.append('season', '계절 입력');

    //Qr 코드 파일 유무 확인
    if (fileInput.files.length > 0) {
        formData.append('qrFile', fileInput.files[0]);
    } else {
        //파일이 없으면 함수 종료
        console.log("파일이 선택되지 않았습니다.");
        return; 
    }

    //키워드 배열 전송
    keyWords.forEach((keyword) => {
        formData.append('keyWords',keyword);
    })

    //서버로 이미지 조건 정보 post
    try {
        const response = await fetch('http://localhost:8080/image/generate', {
            method: 'POST',
            body: formData,
        });

        if (response.ok) {
            console.log("이미지 관련 조건 정보 전송 성공");
            const responseData = await response.text(); // 응답 데이터 처리
            console.log("서버 응답:", responseData);
        } else {
            console.error("전송 실패:", response.status, response.statusText);
        }
    } catch (error) {
        console.error("전송 중 오류:", error);
    }
};

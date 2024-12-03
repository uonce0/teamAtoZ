function addReceiver() { //번호 추가 버튼 or textarea에서 enter키 입력

    let num = document.getElementById("contacts_list").value;
    const list = document.getElementById("receiver_list");
    const notice = document.getElementById("default_txt");

    //번호 추가 : 010으로 시작하고 11자리인 경우만 추가
    if (num !== "" && num.startsWith("010") && num.length === 11) {
        const newList = document.createElement("div");
        const newNumber = document.createElement("p");
        const newButton = document.createElement("button");
        

        newList.className = "t_row list";
        newList.style.display = "flex";
        list.appendChild(newList);

        newNumber.innerHTML = num;
        newList.appendChild(newNumber);

        //삭제 버튼 클릭 시 해당 번호 삭제
        newButton.innerHTML = "삭제";
        newButton.onclick = function() {
            list.removeChild(newList);
            if (list.children.length === 1) {
                notice.style.display = "block";
            }
        };
        newList.appendChild(newButton);

        notice.style.display = "none";

        document.getElementById("contacts_list").value = "";
        alert("전화번호가 추가되었습니다.")
    } else {
        alert("휴대폰 번호를 정확히 입력해주세요.");
        document.getElementById("contacts_list").value = "";
    }
}

function removeAll() { //전체 제거 버튼
    const list = document.getElementById("receiver_list");
    const notice = document.getElementById("default_txt");

    while (list.children.length > 1) {
        list.removeChild(list.lastChild);
    }

    notice.style.display = "block";
}

//번호 입력 후 엔터키 입력 시 addReceiver() 함수 실행
function addNumber(event) {
    if(event.key == "Enter"){
        addReceiver();
    }
}
//번호 입력창(textarea)에 숫자 외의 문자 입력 제한
function removeNonNumber(event) {
    if (event.keyCode <48 || event.keyCode > 57) {
        event.target.value = event.target.value.replace(/[^0-9]/g, "");
    }
}




//메세지 발송
const handleMessageSend = async () => {
    const content = document.getElementById("content").value;
    const title = document.getElementById("title").value;
    const image = document.getElementById("generated-image").src;
    const list = document.getElementById("receiver_list");

    const numbers = [];
    const formData = new FormData();

    if (list.children.length === 1) {
        alert("전화번호를 추가해주세요.");
        return;
    } else if (content === "") {
         alert("메세지를 입력해주세요.");
         return;
    } else if (title === "") {
        alert("제목을 입력해주세요.");
        return;
    } else {
        for (let i = 1; i < list.children.length; i++) { //리스트의 첫번째는 안내문구이므로 제외&번호만 추출
            numbers.push(list.children[i].children[0].textContent);
        }
        console.log("수신자 :"+ numbers);
        numbers.forEach((phoneNum) => {
            formData.append('phoneNumber',phoneNum);
        });

        //받은 번호들을 formData에 추가
        //content도 formData에 추가
        //fileData도 formData에 추가
        //fetch로 post 요청

        // 필수 파라미터 추가
        formData.append("title", title);
        formData.append("content", content);
        formData.append("image", image);

        try {
                const response = await fetch('http://localhost:8080/api/messages/send', {
                    method: 'POST',
                    body: formData,
                });

                if (response.ok) {
                    console.log("메세지 데이터 전송 성공");
                    const responseData = await response.text(); // 응답 데이터 처리
                    console.log("서버 응답:", responseData);

                    alert("메세지가 발송되었습니다.");
                    removeAll(); // 전송 후 리스트 초기화
                } else {
                    console.error("전송 실패:", response.status, response.statusText);
                    alert("메세지 발송에 실패했습니다.");
                }
            } catch (error) {
                console.error("전송 중 오류:", error);
                alert("메세지 발송 중 오류가 발생했습니다.");
            }
    }



    



    
}
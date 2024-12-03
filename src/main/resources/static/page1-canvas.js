let stage, layer1, layer2, qrImage, generatedImage, transformer, currentText;
let originalImage; // 원본 qrFIle

const removeButton = document.getElementById("remove-qrfile-button");
const fileInput = document.getElementById("qr-file-input");

// Konva 초기화 함수
function initKonvaCanvas() {
    const konvaContainer = document.getElementById('konva-container');
    const konvaWidth = konvaContainer.offsetWidth;
    const konvaHeight = konvaContainer.offsetHeight;

    stage = new Konva.Stage({
        container: 'konva-container', // HTML의 캔버스 컨테이너
        width: konvaWidth,
        height: konvaHeight,
    });
	
	// 레이어 1 (배경 이미지)
	layer1 = new Konva.Layer();
	layer1.listening(false);
	stage.add(layer1);

	// 레이어 2 (QR 이미지와 텍스트)
	layer2 = new Konva.Layer();
	stage.add(layer2);

	// Transformer 레이어 2에 추가
	transformer = new Konva.Transformer();
	layer2.add(transformer);
	
	stage.on('click', (e) => {
	    if (e.target === stage) {
	        transformer.nodes([]); //stage 클릭시 Transformer 비활성화
			return;
	    }
	});
}

// AI 생성 이미지 관련 함수
// AI 이미지 생성 후 호출 -> 캔버스에 이미지 표시
function displayGeneratedImage() {
    document.getElementById("generated-image").style.display = "none";
    const selectedImage = document.getElementById("generated-image");
    const imageSrc = selectedImage.src;

    // 이미지가 설정되지 않았다면 경고 출력
    if (!selectedImage || !selectedImage.src) {
        console.error("이미지를 불러올 수 없습니다. src가 비어있습니다.");
        alert("이미지를 먼저 생성하거나 선택하세요!");
        return;
    }

    document.getElementById("konva-container").style.display = "block";

	// 이미지 로드 함수 호출
    loadGeneratedImage(imageSrc);
}

// 생성된 이미지 로드 및 표시
function loadGeneratedImage(src) {
    const img = new Image();
    img.src = src;

    img.onload = () => {
        const konvaContainer = document.getElementById('konva-container');
        const konvaWidth = konvaContainer.offsetWidth;
        const konvaHeight = konvaContainer.offsetHeight;

        if (generatedImage) generatedImage.destroy(); // 기존 이미지 삭제

        const aspectRatio = img.width / img.height;
        let newWidth, newHeight;

        if (konvaWidth / konvaHeight > aspectRatio) {
            // 컨테이너가 더 넓은 경우
            newHeight = konvaHeight;
            newWidth = newHeight * aspectRatio;
        } else {
            // 컨테이너가 더 좁은 경우
            newWidth = konvaWidth;
            newHeight = newWidth / aspectRatio;
        }

        generatedImage = new Konva.Image({
            x: (konvaWidth - newWidth) / 2, // 가운데 정렬
            y: (konvaHeight - newHeight) / 2, // 가운데 정렬
            image: img,
            width: newWidth,
            height: newHeight,
        });

        layer1.add(generatedImage);
        layer1.draw();
    };
    img.onerror = () => {
        console.error("이미지를 로드할 수 없습니다:", src);
        alert("이미지를 로드하는 데 실패했습니다.");
    };
}

// QR 코드 관련 이미지 처리 함수
//QR 파일 입력 시
document.addEventListener("DOMContentLoaded", () => {
    //파일 선택시 - 파일 삭제 버튼 보여줌.
    fileInput.addEventListener("change", () =>{
        let prompt = document.getElementById("generated-prompt");
        if(fileInput.files.length>0){
            removeButton.style.display = "inline-block";

        }else{
            removeButton.style.display = "none";
        }
    });
});

//qr 파일 삭제 버튼 눌렀을 경우
function removeFile() {
    fileInput.value = "";
    removeButton.style.display = "none";
    if (qrImage) {
        qrImage.destroy();
        transformer.nodes([]);
        layer2.draw(); 
        qrImage = null;
    }
}

// QR 파일 업로드 시 이벤트 처리
const qrColorPicker = document.getElementById('qrColorPicker');
fileInput.addEventListener("change", () => {
    if (fileInput.files.length > 0) {
        loadQRImage(fileInput.files[0]); // QR 이미지 로드
    }
});

// QR 이미지 로드 및 표시
function loadQRImage(file) {
    const reader = new FileReader();

    reader.onload = () => {
        const img = new Image();
        img.src = reader.result; //e.target.result;

        img.addEventListener ('load', () => {
            if (qrImage) qrImage.destroy(); // 기존 QR 이미지 삭제

            qrImage = new Konva.Image({
                x: 50,
                y: 50,
                image: img,
                width: 200,
                height: 200,
                draggable: true, // 드래그 가능
            });
            bgRed = 255;
            bgGreen = 255;
            bgBlue = 255;
            layer2.add(qrImage);
            layer2.draw();

            // 마우스 오버 시 커서 변경
            qrImage.on('mouseenter', function () {
                stage.container().style.cursor = 'move';
            });
            qrImage.on('mouseleave', function () {
                stage.container().style.cursor = 'default';
            });

            qrImage.on('click', () => {
                transformer.nodes([qrImage]); // 클릭 시 크기조절
            });

            // Delete,Backspace 키로 QR 코드 삭제
            qrImage.on('click', function () {
                window.addEventListener('keydown', (e) => {
                    const selectedNode = transformer.nodes()[0];
                    if (e.key === 'Delete') {
                        if (selectedNode === qrImage) {
                            if (confirm('QR 코드를 삭제하시겠습니까?')) {
                                qrImage.destroy();
                                transformer.nodes([]);
                                layer2.draw();
                                removeFile();
                            } else {
                                alert('취소되었습니다.');
                                return;
                            }
                        }
                    }
                });
            });
        });
    };
    reader.readAsDataURL(file);
}

//QR 코드 색상 변경
let red,green,blue;
let trans = document.getElementById("toggleTransparent"); //투명화 true/false
qrColorPicker.addEventListener("input", (event) => {
     const rgb = hexToRgb(event.target.value);
     red = rgb.r;
     green = rgb.g;
     blue = rgb.b;
 
     qrImage.cache();
     if(trans.checked) {
         //QR코드 색상이 흰색과 유사 시
         if(rgb.r >= 240 && rgb.g >= 240 && rgb.b >= 240) {
             qrImage.filters([WhenQrWhite, qrColorChange]);
         } else {
             qrImage.filters([qrColorChange,Transparent]);
         }
     } else {
         if(bgRed <= 50 && bgGreen <= 50 && bgBlue <= 50 && rgb.r >= 240 && rgb.g >= 240 && rgb.b >= 240) {
             qrImage.filters([Konva.Filters.Invert]);
         } else if(bgRed <= 50 && bgGreen <= 50 && bgBlue <= 50){
             qrImage.filters([WhenBgBlack, bgColorChange]);
         } else {
             qrImage.filters([bgColorChange, qrColorChange]);
         }
     }
 });
//QR 코드 색상 변경 커스텀 필터
const qrColorChange = function (imageData) {
    const data = imageData.data;
    for (let i = 0; i < data.length; i += 4) {
        const r = data[i];
        const g = data[i + 1];
        const b = data[i + 2];
        const alpha = data[i + 3];

        // 검정 픽셀만 색 변경 (red, green, blue 값이 0이면 검정)
        if (r < 50 && g < 50 && b < 50 && alpha > 0) {
            data[i] = red; // 새로운 R 값
            data[i + 1] = green; // 새로운 G 값
            data[i + 2] = blue; // 새로운 B 값
        }
    }
};
// QR 배경색이 검은색과 유사 시의 코드 색상 변경 커스텀 필터
const WhenBgBlack = function (imageData) {
    const data = imageData.data;
    for (let i = 0; i < data.length; i += 4) {
        const r = data[i];
        const g = data[i + 1];
        const b = data[i + 2];

        // 픽셀이 흰색이 아닐 시(QR코드 기본 이미지에서 배경 색상이 흰색이므로)
        if (r <= 240 && g <= 240 && b <= 240) {
            data[i] = red; // 새로운 R 값
            data[i + 1] = green; // 새로운 G 값
            data[i + 2] = blue; // 새로운 B 값
        }
    }
};
//QR 이미지 배경 색상 변경
let bgRed, bgGreen, bgBlue;
const qrBgColorPicker = document.getElementById('qrBgColorPicker');
qrBgColorPicker.addEventListener("input", (event) => {
    const rgb = hexToRgb(event.target.value);
    bgRed = rgb.r;
    bgGreen = rgb.g;
    bgBlue = rgb.b;

    qrImage.cache();
    if(bgRed <= 50 && bgGreen <= 50 && bgBlue <= 50 && red >= 240 && green >= 240 && blue >= 240) {
        qrImage.filters([Konva.Filters.Invert]);
    } else if(bgRed <= 50 && bgGreen <= 50 && bgBlue <= 50){
        qrImage.filters([WhenBgBlack, bgColorChange])
    } else {
        qrImage.filters([bgColorChange, qrColorChange]);
    }
    document.getElementById("toggleTransparent").checked = false;
})
//QR 배경 색상 변경 커스텀 필터
const bgColorChange = function (imageData) {
    const data = imageData.data;
    for (let i = 0; i < data.length; i += 4) {
        const r = data[i];
        const g = data[i + 1];
        const b = data[i + 2];
        const alpha = data[i + 3];

        // 흰색 픽셀만 색 변경 (red, green, blue 값이 255이면 흰색)
        if (r > 240 && g > 240 && b > 240 && alpha > 0) {
            data[i] = bgRed; // 새로운 R 값
            data[i + 1] = bgGreen; // 새로운 G 값
            data[i + 2] = bgBlue; // 새로운 B 값
        }
    }
};
function hexToRgb(hex) {
    // 색상 코드가 #으로 시작하지 않으면 오류
    if (hex[0] !== '#' || (hex.length !== 7 && hex.length !== 4)) {
        throw new Error('RGB 값이 아닙니다.');
    }

    // 짧은 형식(#RGB) 지원
    if (hex.length === 4) {
        hex = '#' + hex[1] + hex[1] + hex[2] + hex[2] + hex[3] + hex[3];
    }

    const bigint = parseInt(hex.slice(1), 16); // # 이후의 16진수 값을 숫자로 변환
    const r = (bigint >> 16) & 255;           // 상위 8비트
    const g = (bigint >> 8) & 255;            // 중간 8비트
    const b = bigint & 255;                   // 하위 8비트

    return { r, g, b };
}

//투명화 시도
function qrTransparent(checkBox) {
    if(checkBox.checked) {
        qrImage.cache();
        if(red >= 240 && green >= 240 && blue >= 240) {
            qrImage.filters([WhenQrWhite, qrColorChange]);
        } else {
            qrImage.filters([qrColorChange,Transparent]);
        }
    } else {
        qrImage.filters([bgColorChange, qrColorChange]);
        qrImage.cache();
    }
}
//투명화 필터 커스텀
const Transparent = function (imageData) {
    const data = imageData.data;
    for (let i = 0; i < data.length; i += 4) {
        const r = data[i];
        const g = data[i + 1];
        const b = data[i + 2];

        // 픽셀이 흰색&흰색과 유사 시
        if (r > 240 && g > 240 && b > 240) {
            data[i + 3] = 0; // 배경 제거
        }
    }
};
//배경 투명화 & QR코드 색상이 흰색과 유사 시
const WhenQrWhite = function (imageData) {
    const data = imageData.data;
    for (let i = 0; i < data.length; i += 4) {
        const r = data[i];
        const g = data[i + 1];
        const b = data[i + 2];

        // 픽셀이 검정이 아닐 시(QR코드 기본 이미지에서 코드 색상이 검정이므로)
        if (r > 50 && g > 50 && b > 50) {
            data[i + 3] = 0; // 배경 제거
        }
    }
};

//텍스트 입력 관련 함수 모음
// 텍스트 추가 함수 호출 함수
function addTextToCanvasformInput(){
    const text = textInput.value.trim(); // 사용자 입력 텍스트
    const fontFamily = document.getElementById('font-select').value; // 폰트 선택

    let fontStyle;
    if (text) {
        if(fontFamily === 'NanumGothic ExtraBold') {
            fontStyle = '800';
        } else {
            fontStyle = 'normal';
        }
        addTextToCanvas(text, fontFamily, fontStyle); // 텍스트를 Canvas에 추가
        
        textInput.value = ''; // 입력 필드 초기화
    }
}
// 텍스트 추가 함수
function addTextToCanvas(text, fontFamily, fontStyle) {
    const color = colorPicker.value; // 색상 선택

    const newText = new Konva.Text({
        x: 50,
        y: 50,
        text: text,
        fontSize: 25,
        fontFamily: fontFamily,
        fontStyle: fontStyle,
        fill: color,
        draggable: true, // 드래그 가능
        width: 'auto', // 텍스트 너비 자동 조정
        align: 'left',
    });

	layer2.add(newText);
	layer2.draw();


    // 마우스 오버 시 커서 변경
    newText.on('mouseenter', function () {
        stage.container().style.cursor = 'move';
    });
    newText.on('mouseleave', function () {
        stage.container().style.cursor = 'default';
    });
	// 텍스트에 Transformer 추가
	newText.on('click', () => {
	    transformer.nodes([newText]);
	});

	// 더블클릭시 텍스트 수정
    newText.on('dblclick', function () {
        newText.hide();

        let textPosition = newText.absolutePosition(); //stage에서 텍스트 위치 찾기
        const stageBox = stage.container().getBoundingClientRect(); // 캔버스 위치 가져오기

        // 정확한 위치 계산
        const areaPosition = {
            x: stageBox.left + textPosition.x,
            y: stageBox.top + textPosition.y,
        };

        let textarea = document.createElement("textarea");
        document.body.appendChild(textarea);

        textarea.value = newText.text();
        textarea.style.position = 'absolute';
        textarea.style.top = areaPosition.y + 'px';
        textarea.style.left = areaPosition.x + 'px';
        textarea.style.width = newText.width() - newText.padding() * 2 + 'px';
        textarea.style.height = newText.height() - newText.padding() * 2 + 5 + 'px';
        textarea.style.fontSize = newText.fontSize() + 'px';
        textarea.style.border = 'none';
        textarea.style.padding = '0px';
        textarea.style.margin = '0px';
        textarea.style.overflow = 'hidden';
        textarea.style.background = 'none';
        textarea.style.outline = 'none';
        textarea.style.resize = 'none';
        textarea.style.lineHeight = newText.lineHeight();
        textarea.style.fontFamily = newText.fontFamily();
        textarea.style.transformOrigin = 'left top';
        textarea.style.textAlign = newText.align();
        textarea.style.color = newText.fill();

        textarea.focus();

        // 높이 자동 조절
        textarea.style.height = 'auto';
        textarea.style.height = `${textarea.scrollHeight}px`;

        // 입력 중 텍스트 크기와 Transformer 업데이트
        textarea.addEventListener('input', function () {
            textarea.style.height = 'auto';
            textarea.style.height = `${textarea.scrollHeight}px`; // 높이 동적 조정
            newText.text(textarea.value); // 텍스트 업데이트
            newText.width('auto'); // 텍스트 너비를 동적으로 업데이트
            transformer.nodes([newText]); // Transformer 업데이트
            layer2.batchDraw();
        });

        //textarea 제거 함수
        function removeTextarea() {
            textarea.parentNode.removeChild(textarea);
            window.removeEventListener('click', handleOutsideClick);
            newText.show();
            transformer.forceUpdate();
        }

        function setTextareaWidth(newWidth) {
            if (!newWidth) {
              // placeholder & fontsize로 너비 계산
              newWidth = newText.placeholder.length * newText.fontSize();
            }
            textarea.style.width = newWidth + 'px';
        }

        //enter키 입력시 textarea 제거
        textarea.addEventListener('keydown', function (e) {
            //shift+enter 시 종료 안함
            if (e.key === 'Enter' && !e.shiftKey) {
                newText.text(textarea.value);
              removeTextarea();
            }
            // esc 누르면 변경 사항 저장 없이 입력 취소
            if (e.key === 'Escape') {
              removeTextarea();
            }
        });

        textarea.addEventListener('keydown', function (e) {
            setTextareaWidth(newText.width());
            textarea.style.height = 'auto';
            textarea.style.height =
            textarea.scrollHeight + newText.fontSize() + 'px';
        });

        //textarea 외부 클릭 시 textarea 삭제
        function handleOutsideClick(e) {
            if (e.target !== textarea) {
                newText.text(textarea.value);
                removeTextarea();
            }
        }
        // 웹페이지 전체적으로 클릭을 인식해 텍스트 입력 중지
        setTimeout(() => {
            window.addEventListener('click', handleOutsideClick);
        });
    })

    // Delete 키로 텍스트 삭제
    newText.on('click', function () {
        window.addEventListener('keydown', function deleteText(e) {
            if (e.key === 'Delete') {
                if (transformer.nodes()[0] === newText) {
                    if (confirm('텍스트를 삭제하시겠습니까?')) {
                        newText.destroy();
                        transformer.nodes([]);
                        layer2.draw();
                    }
                }
            }
        });
    });
}
// 색상 선택 시 텍스트 색상 변경
const colorPicker = document.getElementById("colorPicker");
colorPicker.addEventListener("input", (e) => {
    changeTextColor(e.target.value); // 선택한 색상 적용
}); 

// 텍스트 색상 변경 함수
function changeTextColor(color) {
    const selectedNode = transformer.nodes()[0];
    if (selectedNode && selectedNode.getClassName() === 'Text') {
        selectedNode.fill(color); // 텍스트 색상 변경
        layer2.draw();
    }
}

// 폰트 선택 시 텍스트 폰트 변경
function changeFont() {
    const selectedNode = transformer.nodes()[0];
    const fontFamily = document.getElementById('font-select').value;
    const fontBold = document.getElementById('font-bold').checked;
    const fontItalic = document.getElementById('font-italic').checked;

    if (selectedNode && selectedNode.getClassName() === 'Text') {
        //fontStyle 설정
        if(fontBold || fontItalic) {
        // Bold와 Italic 상태에 맞춰 fontStyle 설정
            let currentFontStyle = "";
            if (fontBold) {
                if (fontFamily === 'NanumGothic ExtraBold') {
                    currentFontStyle += '1000 ';  // ExtraBold 추가
                } else {
                currentFontStyle += 'bold ';
                }// Bold 추가
            } else {
                currentFontStyle += '';
            }

            if (fontItalic) {
                currentFontStyle += 'italic';  // Italic 추가
            } else {
                currentFontStyle += '';
            }

            selectedNode.fontStyle(currentFontStyle.trim());
        } else {
        // bold, italic 설정 안됨
            if(fontFamily === 'NanumGothic ExtraBold') {
                selectedNode.fontStyle('800');
            } else {
                selectedNode.fontStyle('normal');
            }
        }
        selectedNode.fontFamily(fontFamily); // 폰트 변경
        layer2.draw();
    }
}

// 체크박스를 기반으로 스타일을 적용하는 코드
document.querySelectorAll("#font-bold, #font-italic").forEach(function(checkbox) {
    checkbox.addEventListener("change", function() {
        const fontFamily = document.getElementById('font-select').value;
        const selectedNode = transformer.nodes()[0];  // 선택된 노드 가져오기

        if (selectedNode) {
            let currentFontStyle = "";

            // Bold와 Italic 상태에 맞춰 fontStyle 설정
            if (document.getElementById("font-bold").checked) {
                if (fontFamily === 'NanumGothic ExtraBold') {
                    currentFontStyle += '900 ';  // ExtraBold 추가
                } else {
                currentFontStyle += 'bold ';
                }// Bold 추가
            } else {
                currentFontStyle += '';
            }

            if (document.getElementById("font-italic").checked) {
                currentFontStyle += 'italic';  // Italic 추가
            } else {
                currentFontStyle += '';
            }

            if (currentFontStyle === '') {
                if (fontFamily === 'NanumGothic ExtraBold') {
                currentFontStyle = '800';  // ExtraBold 추가
                } else {
                currentFontStyle = 'normal';  }// 기본값 설정
            }

            selectedNode.fontStyle(currentFontStyle.trim());
            selectedNode.getLayer().batchDraw();  // 성능 최적화를 위해 배치 드로우
        }
    });
});


//사용자 유용성 향상 기능 함수
// 텍스트 입력 및 추가 버튼 이벤트 처리
const textInput = document.getElementById("textInput");
const addTextButton = document.getElementById("addTextButton");

//버튼 클릭시 텍스트 추가
addTextButton.addEventListener("click", addTextToCanvasformInput);

//enter키로 텍스트 추가
textInput.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
        addTextToCanvasformInput();
        event.preventDefault(); // 폼 제출 방지 및 기본 동작 취소
    }
});

// 이미지 스티커 기능 추가
function addSticker(sticker) {
    if (sticker.src) {
        loadSticker(sticker.src); // QR 이미지 로드
    }
}

// 스티커 이미지 로드 및 표시
function loadSticker(src) {
    const img = new Image();
    img.src = src //스티커 이미지 url

    const originalWidth = img.width;
    const originalHeight = img.height;

    // 크기를 80으로 조정하면서 비율 유지
    const scaleFactor = 220 / Math.max(originalWidth, originalHeight);
    const adjustedWidth = originalWidth * scaleFactor;
    const adjustedHeight = originalHeight * scaleFactor;
    
    const sticker = new Konva.Image({
        x: 50,
        y: 50,
        image: img,
        width: adjustedWidth,
        height: adjustedHeight,
        draggable: true, // 드래그 가능
    });
    layer2.add(sticker);
    layer2.draw();

    // 마우스 오버 시 커서 변경
    sticker.on('mouseenter', function () {
        stage.container().style.cursor = 'move';
    });
    sticker.on('mouseleave', function () {
        stage.container().style.cursor = 'default';
    });
    sticker.on('click', () => {
        transformer.nodes([sticker]); // 클릭 시 크기조절
    });

    // Delete 키로 QR 코드 삭제
    sticker.on('click', function () {
        window.addEventListener('keydown', (e) => {
            const selectedNode = transformer.nodes()[0];
            if (e.key === 'Delete') {
                if (selectedNode === sticker) {
                    if (confirm('스티커를 삭제하시겠습니까?')) {
                        sticker.destroy();
                        transformer.nodes([]);
                        layer2.draw();
                    } else {
                        return;
                    }
                }
            }
        });
    });
}
const stickerColor = document.getElementById("stickerColor");
stickerColor.addEventListener("input", (event) => {
    changeStickerColor(event.target.value);//색상 적용
});
// 스티커 색상 변경 함수
let sRed, sGreen, sBlue;
function changeStickerColor(color) {
    const selectedNode = transformer.nodes()[0];
    if (selectedNode && selectedNode.getClassName() === 'Image') {
        const rgb = hexToRgb(color);
        sRed = rgb.r;
        sGreen = rgb.g;
        sBlue = rgb.b;  
        
        selectedNode.cache();

        selectedNode.filters([stickerColorChange]);
        // 고품질 렌더링 설정
        selectedNode.perfectDrawEnabled(false);

        layer2.draw();


    }
}
// 스티커 색상 변경 커스텀 필터
const stickerColorChange = function (imageData) {
    const data = imageData.data;

    for (let i = 0; i < data.length; i += 4) {
        const r = data[i];
        const g = data[i + 1];
        const b = data[i + 2];
        const alpha = data[i + 3];

        // 검정 픽셀만 색 변경 (red, green, blue 값이 0이면 검정)
        if (r < 50 && g < 50 && b < 50 && alpha > 0) {
            data[i] = sRed; // 새로운 R 값
            data[i + 1] = sGreen; // 새로운 G 값
            data[i + 2] = sBlue; // 새로운 B 값
        }
    }
}


// 수정된 이미지 저장
function saveEditedImage() {
    // 선택된 요소 다 풀기
    transformer.nodes([]); // Transformer 비활성화
    const textareas = document.querySelectorAll('textarea');
    textareas.forEach(textarea => {
        textarea.style.display = 'none'; // textarea 숨기기
    });
    const dataURL = stage.toDataURL({ pixelRatio: 3 }); // 고해상도 이미지 캡처

    if (!dataURL) {
        console.error("이미지를 생성할 수 없습니다.");
        alert("이미지를 생성할 수 없습니다.");
        return;
    }

    // 서버로 이미지 데이터 전송
    fetch('http://localhost:8080/image/saveImage', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ dataURL })  // Data URL을 JSON 형태로 전송
    })
    .then(response => response.json())
    .then(data => {
        const imageUrl = data.imageUrl;  // 서버에서 받은 이미지 경로
        console.log("이미지가 저장되었습니다:", imageUrl);
        alert('이미지가 저장되었습니다.');
        // generatedImageElement에 서버에서 받은 이미지 URL 설정
        const generatedImageElement = document.getElementById("generated-image");
        if (generatedImageElement) {
            generatedImageElement.src = "generated/"+imageUrl; // 이미지 URL을 src에 설정
        }
    })
    .catch(error => {
        console.error('이미지 저장 실패:', error);
        alert('이미지 저장 실패');
    });
}

// Konva 초기화 호출
document.addEventListener("DOMContentLoaded", initKonvaCanvas);
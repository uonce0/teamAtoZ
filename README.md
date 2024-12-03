# TeamAtoZ - SpringBoot 맞춤형 스마트티켓 전송 서비스

## 프로젝트 개요
- **기간**: 2024.09.10 ~ 2024.12.08  
- **목적**: 이 프로젝트는 항공사 및 관련 기업들이 고객에게 맞춤형 정보를 효율적으로 전달할 수 있도록 설계되었습니다.<br>AI 기반 이미지 생성과 QR 코드 결합을 통해 사용자 요구에 맞는 맞춤형 콘텐츠를 생성하고, 이를 문자 메시지로 간편하게 전송함으로써 고객 경험을 향상시키고 기업의 고객 서비스 품질을 개선하는 것을 목표로 합니다.

## 기술 아키텍처
- **프로그래밍 언어 및 프레임워크**&nbsp;&nbsp;
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
- **기술 스택**&nbsp;&nbsp;
  <img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white"> 
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white"/>
  <img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
  <img src="https://img.shields.io/badge/Konva-0D83CD?style=for-the-badge&logo=konva&logoColor=white">
- **API 연동**&nbsp;&nbsp;
  <img width="65" alt="ppurio" src="https://github.com/user-attachments/assets/65d4f240-21bb-4f17-8158-520490cc950a" style="border: 2px solid #D3D3D3; border-radius: 8px; display: inline-block;">
  <img src="https://img.shields.io/badge/OpenAI-412991?style=for-the-badge&logo=OpenAI&logoColor=white">
- **버전 관리**&nbsp;&nbsp;
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">

## 배포 주소
(선택사항: 배포된 서비스 링크가 있을 경우 추가)

## 팀원 소개
| 이름     | 역할   |
|----------|--------|
| 신유빈   | 팀장   |
| 구정현   | 팀원   |
| 송유원   | 팀원   |
| 송지윤   | 팀원   |

## 프로젝트 페이지 소개

| Page1   | Page2   |
|---------|---------|
| <img src="https://github.com/user-attachments/assets/c2bafc6c-45e5-4441-a246-9507972805eb" alt="page1" style="width: 300px"> | <img src="https://github.com/user-attachments/assets/e2f395db-7b5d-4cb0-ab09-47b951f17bd1" alt="page2" style="width: 300px"> |


AI 기반 이미지 생성 기술을 활용하여 사용자가 제공한 조건(예: 장소, 시간대, 스타일 등)을 바탕으로 이미지를 자동으로 생성하고, 이를 QR 코드와 결합하여 문자 메시지로 고객에게 전송하는 시스템입니다. <br>이 시스템은 생성된 이미지를 다양한 요구에 맞게 제공하며, QR 코드와 결합하여 효율적으로 정보 전달을 할 수 있습니다.

## 주요 기능
1. **AI 이미지 생성**: 사용자가 제공하는 조건(예: 장소, 시간대, 스타일 등)을 바탕으로 AI 시스템이 프롬프트와 이미지를 생성합니다. 사용자는 생성된 프롬프트와 이미지를 확인하고 수정할 수 있습니다.
2. **요소와 이미지 결합**: 생성된 이미지는 사용자가 QR 코드, 텍스트, 스티커를 배치해 결합시킬 수 있습니다. QR 코드는 예약 번호, 프로모션 코드, 티켓 정보 등 다양한 데이터를 포함하며, 이를 사용자가 쉽게 스캔하고 확인할 수 있습니다.
3. **문자 메시지 발송 기능**: 결합된 이미지와 QR 코드는 다수의 고객에게 문자 메시지(SMS)로 전송할 수 있습니다.

## 시작 가이드

### Requirements
- **JDK 17** 이상
- **Gradle** 빌드 도구
- **Git** 및 **GitHub** 사용

### 설치
```bash
git clone https://github.com/uonce0/teamAtoZ.git
cd teamAtoZ
./gradlew build
```

### application.properties 설정
application.properties 파일에 다음과 같은 정보를 추가합니다:
```java
openai.key=YOUR_OPENAI_API_KEY
ppurio.api.key=YOUR_PPURIO_API_KEY
ppurio.api.token=YOUR_PPURIO_API_TOKEN
ppurio.api.expired=YOUR_PPURIO_TOKEN_EXPIRED
ppurio.messageKey=YOUR_MSG_KEY
ppurio.account=YOUR_ACCOUNT_INFO
ppurio.from=YOUR_PHONE_NUM
```

## 향후 계획
- 다양한 스타일의 이미지 생성 기능 추가
- 다국어 지원 기능 구현
- 사용자 맞춤형 템플릿 기능 추가

## 시연 영상
(선택 사항: 프로젝트 기능을 보여주는 유튜브 링크 추가)

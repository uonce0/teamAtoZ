package pre_capstone.teamAtoZ.controller;

import pre_capstone.teamAtoZ.service.GPTService;
import pre_capstone.teamAtoZ.service.ImgGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@CrossOrigin(origins = "http://192.168.45.244:5500") 
//CORS 에러는 브라우저와 서버의 포트번호가 서로 다르기 떄문에 나타난다.
//떄문에 브라우저 포트번호를 먼저 설정하고 하면 에러가 나지 않는데 나중에 확인 필요

@RestController
@RequestMapping("/image")
public class ImgGenerationController {

    @Autowired
    private GPTService gptService;

    @Autowired
    private ImgGenerationService imgGenerationService;

    //브라우저의 POST 요청으로 qr이미지와 키워드를 받아옴
    @PostMapping("/generate")
    public ResponseEntity<String> generateImage(
        @RequestParam("userCommand") String userCommand,
        @RequestParam("destination") String destination,
        @RequestParam("season") String season,
        @RequestParam("qrFile") MultipartFile qrFile,
        @RequestParam(value = "additionalFile", required = false) MultipartFile additionalFile,
        @RequestParam(value = "keyWords") List<String> keyWords) {

        // QR 파일 여부 확인
        if (qrFile != null && !qrFile.isEmpty()) {
            String qrFileName = qrFile.getOriginalFilename();
            System.out.println("QR 파일 이름: " + qrFileName);
        } else {
            System.out.println("QR 파일이 전송되지 않았습니다.");
            return ResponseEntity.badRequest().body("QR 파일이 필요합니다.");
        }

     // 키워드 출력 형식 조정
        String formattedKeywords = String.join(" ", keyWords);
        System.out.println("키워드: " + formattedKeywords);

        // 이미지 생성 부분은 주석 처리
        // String imageDescription = gptService.getImagePrompt(userCommand);
        // String imageUrl = imgGenerationService.generateImage(
        //     imageDescription,
        //     destination,
        //     season,
        //     qrFile,
        //     additionalFile // 추가 파일 처리 로직 필요
        // );

        return ResponseEntity.ok("이미지 생성 요청이 성공적으로 처리되었습니다.");
    }
}

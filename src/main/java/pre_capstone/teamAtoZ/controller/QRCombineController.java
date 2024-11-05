package pre_capstone.teamAtoZ.controller;

import pre_capstone.teamAtoZ.service.ImgGenerationService;
import pre_capstone.teamAtoZ.service.QRCombineService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

@CrossOrigin(origins = "http://192.168.45.244:5500")
@RestController
@RequestMapping("/image")
public class QRCombineController {
    // QR 코드 합성을 위한 코드

    @Autowired
    private QRCombineService qrCombineService;

    @Autowired
    private ImgGenerationService imgGenerationService;


    @PostMapping("/qrCombine")
    public ResponseEntity<String> combineQRImage(
            @RequestParam(value = "qrFile", required = false) MultipartFile qrFile,
            @RequestParam("qrPresent") boolean qrPresent,  // QR 여부 확인
            @RequestParam("imgSize") String imgSize,
            @RequestParam("imgLocation") String imgLocation) throws IOException {

        // QR 파일 여부 확인
        if (qrPresent) {
            if (qrFile != null && !qrFile.isEmpty()) {
                String qrFileName = qrFile.getOriginalFilename();
                System.out.println("QR 파일 이름: " + qrFileName);
                System.out.println("이미지 크기: " + imgSize);
                System.out.println("이미지 위치: " + imgLocation);
            } else {
                System.out.println("QR 파일이 전송되지 않았습니다.");
                return ResponseEntity.badRequest().body("QR 파일이 전송되지 않았습니다.");
            }
        } else {
            System.out.println("QR 파일 없음");
        }

        //ImageGenerationService에서 이미 생성된 이미지 url 가져오기
        String aiImage = imgGenerationService.getGeneratedImageUrl();
        System.out.println("이미지 url: " + aiImage);


        //이미지 파일을 BUfferedImage로 변환
        BufferedImage generatedImage = null;
        BufferedImage qrImage = null;
        try {
            generatedImage = ImageIO.read(new URL(aiImage));
            qrImage = ImageIO.read(qrFile.getInputStream());
            System.out.println("이미지 변환 성공");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("이미지 변환 실패");
        }

        // 받아온 이미지 url과 qr코드 합성
        String qrCombinedImage = qrCombineService.combineQRWithImage(qrImage, generatedImage, imgSize,imgLocation);


        // 이미지 URL 반환
        return ResponseEntity.ok(qrCombinedImage);

    }
}



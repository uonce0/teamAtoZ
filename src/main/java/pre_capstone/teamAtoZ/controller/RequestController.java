package pre_capstone.teamAtoZ.controller;

import pre_capstone.teamAtoZ.dto.FilePathDTO;
import pre_capstone.teamAtoZ.service.CompressImageService;
import pre_capstone.teamAtoZ.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class RequestController {

    private final RequestService requestService;

    @Autowired
    private FilePathDTO filePathDTO;

    @Autowired
    private CompressImageService compressImageService;


    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    // 문자 발송 요청
    @PostMapping("/send")
    public Map<String, Object> sendMessage(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("image") String imageUrl,
            @RequestParam("phoneNumber") List<String> phoneNumber) throws IOException {


        System.out.println("제목: " + title);
        System.out.println("내용: " + content);
        System.out.println("이미지Url: " + imageUrl);
        System.out.println("수신자: " + phoneNumber);

        File imageFile = null;

        //이미지 url이 qr합성 이미지인지 단순 이미지인지 확인
        if (filePathDTO.getEditedFile() != null && imageUrl.contains(filePathDTO.getEditedFile())) { // 편집된 이미지일 경우
            String userDir = System.getProperty("user.dir");
            imageFile = new File(userDir + "/src/main/generated/edited/" + filePathDTO.getEditedFile());
            System.out.println("이미지 url: " + imageFile);
            // qr합성 이미지가 아닌 경우: 파일에서 이미지 가져옴
        } else if (imageUrl.contains(filePathDTO.getOnlyImageFile())) {
            String userDir = System.getProperty("user.dir");
            imageFile = new File(userDir + "/src/main/generated/original/" + filePathDTO.getOnlyImageFile());
            System.out.println("이미지 url: " + imageFile);
        }

        try {
            assert imageFile != null;
            requestService.requestSend(title,content,imageFile.getPath(),phoneNumber);
            return Map.of("status", "success", "message", "Message sent successfully");
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    // 예약 취소 요청
    @PostMapping("/cancel")
    public Map<String, Object> cancelMessage() {
        try {
            requestService.requestCancel();
            return Map.of("status", "success", "message", "Message cancellation successful");
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }
}

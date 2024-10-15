package pre_capstone.teamAtoZ.controller;

import pre_capstone.teamAtoZ.dto.GenerateImageRequestDTO;
import pre_capstone.teamAtoZ.service.GPTService;
import pre_capstone.teamAtoZ.service.ImgGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Controller
@RestController
@RequestMapping("/image")
public class ImgGenerationController {

    @Autowired
    private GPTService gptService;

    @Autowired
    private ImgGenerationService imgGenerationService;

//    @GetMapping("/generate")
//    public String generateImageForm() {
//        return "basic/page1";
//    }

    //public ResponseEntity<String>
    @PostMapping("/generate")
    public ResponseEntity<String> generateImage(GenerateImageRequestDTO generateImageRequestDTO) {

        // 사용자가 입력한 명령어를 바탕으로 GPT-4에서 이미지 설명 생성
        String imageDescription = gptService.getImagePrompt(generateImageRequestDTO.getUserCommand());

        // DALL·E 이미지 생성
        String imageUrl = imgGenerationService.generateImage(imageDescription, generateImageRequestDTO.getDestination(), generateImageRequestDTO.getSeason());

        // 이미지 URL 반환
        return ResponseEntity.ok(imageUrl);

    }

//    //테스트데이터
//    @PostConstruct
//    public void init() {
//
//    }
}

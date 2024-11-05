package pre_capstone.teamAtoZ.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import pre_capstone.teamAtoZ.service.GPTService;
import pre_capstone.teamAtoZ.service.ImgGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://192.168.45.240:5500") 
//CORS 에러는 브라우저와 서버의 포트번호가 서로 다르기 떄문에 나타난다.
//떄문에 브라우저 포트번호를 먼저 설정하고 하면 에러가 나지 않는데 나중에 확인 필요

@RestController
@RequestMapping("/image")
public class ImgGenerationController {

    @Autowired
    private GPTService gptService;

    @Autowired
    private ImgGenerationService imgGenerationService;

	//브라우저의 POST 요청으로 키워드를 받아옴
	@PostMapping("/prompt")
	public ResponseEntity<String> generatePrompt(@RequestParam(name = "keyWords") List<String> keyWords) throws JsonProcessingException {

		// 키워드 출력 형식 조정
		String formattedKeywords;
		if (keyWords != null && !keyWords.isEmpty()) {
			formattedKeywords = String.join(" ", keyWords);
			System.out.println("키워드: " + formattedKeywords);
		} else {
			return ResponseEntity.badRequest().body("키워드를 입력해주세요.");
		}

		// 사용자가 입력한 명령어를 바탕으로 GPT-4에서 이미지 설명 생성
		String generatedPrompt = gptService.getImagePrompt(formattedKeywords);
		
		return ResponseEntity.ok(generatedPrompt);
	}
	
	@PostMapping("/generate")
	public ResponseEntity<String> generateImage(@RequestParam(name = "generatedPrompt") String generatedPrompt) throws JsonProcessingException {
		
		// DALL·E 이미지 생성
		String imageUrl = imgGenerationService.generateImage(generatedPrompt);

		// 이미지 URL 반환
		return ResponseEntity.ok(imageUrl);
	}
}

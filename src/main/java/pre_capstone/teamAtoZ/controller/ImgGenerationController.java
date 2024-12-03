package pre_capstone.teamAtoZ.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import pre_capstone.teamAtoZ.dto.FilePathDTO;
import pre_capstone.teamAtoZ.service.CompressImageService;
import pre_capstone.teamAtoZ.service.GPTService;
import pre_capstone.teamAtoZ.service.ImgGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

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

	@Autowired
	private CompressImageService compressImageService;

	@Autowired
	private FilePathDTO filePathDTO;

	//키워드로 프롬프트 생성
	@PostMapping("/prompt")
	public ResponseEntity<Map<String,String>> generatePrompt(@RequestParam(name = "keyWords") List<String> keyWords) throws JsonProcessingException {

		// 키워드가 유효하지 않으면 오류 응답
        if (keyWords == null || keyWords.isEmpty()) {
        	Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", "키워드를 입력하세요.");
			return ResponseEntity.badRequest().body(errorResponse);
        }
		
        // 키워드 출력 형식 조절
		String formattedKeywords = String.join(" ", keyWords);
		System.out.println("키워드: " + formattedKeywords);
		
		// 사용자가 입력한 명령어를 바탕으로 GPT-4에서 이미지 설명 생성
		String korPrompt = gptService.getImagePrompt(formattedKeywords);

		// 생성된 한국어 프롬프트 바로 표시
		return ResponseEntity.ok(Map.of("korPrompt", korPrompt));
	}

	//생성된 이미지 주소
	@GetMapping("/{uniqueFileName}")
	public ResponseEntity<Resource> getImage(@PathVariable("uniqueFileName") String uniqueFileName) {
		System.out.println("이미지 반환 요청이 들어옴");  // 요청이 들어오는지 확인
		try {
			// 이미지 경로 설정
			String projectDir = System.getProperty("user.dir"); // 현재 프로젝트 루트 디렉토리 경로
			Path imagePath = Paths.get(projectDir, "src", "main", "generated", "original", uniqueFileName).normalize();  // 이미지 경로 설정
			Resource resource = new FileSystemResource(imagePath.toFile());  // FileSystemResource로 변경

			System.out.println("파일 경로: " + imagePath);

			if (resource.exists()) {
				return ResponseEntity.ok()
						.contentType(MediaType.IMAGE_JPEG)  // 이미지 타입 설정 (예: JPEG)
						.body(resource);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	//사용자가 수정한 한국어 프롬프트를 영어로 번역하고 이미지 생성
	@PostMapping("/translateAndGenerate")
	public ResponseEntity<Map<String,String>> translatePrompt(@RequestBody Map<String , String> request) throws JsonProcessingException{
		String userInputPrompt = request.get("text");

		if (userInputPrompt == null || userInputPrompt.trim().isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "프롬프트를 입력해주세요."));
		}
		try {
			// 1. 한국어 프롬프트를 영어로 번역
			String enPrompt = gptService.translateToEnglish(userInputPrompt);
			System.out.println("이미지 생성 영어 프롬프트: " + enPrompt);

			// 2. 영어 프롬프트로 이미지 생성
			String imageUrl = imgGenerationService.generateImage(enPrompt);
			System.out.println("생성된 이미지 URL: " + imageUrl);

			// 3. 이미지 URL 반환
			return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "번역 및 이미지 생성 중 오류가 발생했습니다."));
		}
	}
	
	@PostMapping("/send")
    public ResponseEntity<String> sendImage(@RequestParam("image") MultipartFile image) {
        try {
            // 이미지 확인
            System.out.println("이미지 파일 이름: " + image.getOriginalFilename());
            System.out.println("이미지 파일 크기: " + image.getSize());

            return ResponseEntity.ok("이미지 처리 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 처리 중 오류가 발생했습니다.");
        }
    }

	// 이미지 저장 후 경로 반환
	@PostMapping("/saveImage")
	public ResponseEntity<Map<String, String>> saveImage(@RequestBody Map<String, String> requestData) throws IOException {
		String dataURL = requestData.get("dataURL"); // 클라이언트에서 전송된 Base64 이미지 데이터
		try {
			// "data:image/jpeg;base64," 등의 헤더 부분 제거
			String base64Data = dataURL.split(",")[1];  // 쉼표 이후의 Base64 데이터만 추출
			// Base64 디코딩
			byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

			// 이미지로 변환
			BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));
			if (originalImage == null) {
				throw new IOException("이미지를 읽을 수 없습니다. 데이터 형식을 확인하세요.");
			}

			// 로컬 저장 경로 설정
			String userDir = System.getProperty("user.dir");
			String savePath = userDir + "/src/main/generated/edited/";
			File directory = new File(savePath);
			if (!directory.exists()) {
				try {
					if (directory.mkdirs()) {
						System.out.println("디렉토리가 성공적으로 생성되었습니다.");
					} else {
						System.out.println("디렉토리 생성에 실패했습니다.");
					}
				} catch (SecurityException e) {
					System.out.println("디렉토리 생성 중 보안 예외가 발생했습니다: " + e.getMessage());
				}
			}

			// 고유한 파일 이름 생성 (예: timestamp 기반으로)
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
			String uniqueFileName = simpleDateFormat.format(new Date()) + ".jpg";
			File outputFile = new File(savePath + uniqueFileName);

			filePathDTO.setEditedFile(uniqueFileName);

			// 이미지 압축
			compressImageService.compressImage(originalImage, outputFile, 0.7f);

			// 저장된 이미지의 URL을 JSON 형식으로 반환
			Map<String, String> response = new HashMap<>();
			response.put("imageUrl", "edited/" + uniqueFileName);
			return ResponseEntity.ok(response);

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body(Collections.singletonMap("error", "이미지 저장 실패"));
		}

	}
}

package pre_capstone.teamAtoZ.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GPTService {
    private final String gptApiKey;
    private final String gptApiUrl = "https://api.openai.com/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();


    public GPTService(String gptApiKey) {
        this.gptApiKey = gptApiKey;
    }

    public String getImagePrompt(String userCommand) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + gptApiKey);
        headers.set("Content-Type", "application/json");

//        String requestBody = String.format("{\"model\":\"gpt-4\",\"prompt\":\"Generate a detailed description for a travel promotional image without any text. The image should include: a beautiful beach with turquoise waters, palm trees, and a clear blue sky, along with people enjoying various activities like swimming and sunbathing.\",\"max_tokens\":100}");
        String requestBody = String.format("{\"model\":\"gpt-4\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}],\"max_tokens\":100}", userCommand);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(gptApiUrl, HttpMethod.POST, request, String.class);

        return extractImagePromptFromResponse(response.getBody());

    }

    private String extractImagePromptFromResponse(String gptResponse) {
        // JSON 파싱을 통해 설명을 추출 (예시)
        return "A beautiful beach with turquoise waters, palm trees, and a clear blue sky, with people enjoying swimming and sunbathing.";
    }
    
    public String scanDocument(MultipartFile file) {
        // GPT API 연동 로직
        // 파일에서 항공 정보 추출
        String extractedText = ""; // GPT로부터 받은 정보
        return extractedText;
    }
}

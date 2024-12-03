package pre_capstone.teamAtoZ.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GPTService {
    private final String gptApiKey;
    private final String gptApiUrl = "https://api.openai.com/v1/chat/completions";

    @Autowired
    private RestTemplate restTemplate;

    public GPTService(String gptApiKey) {
        this.gptApiKey = gptApiKey;
    }

    // GPT 호출
    public String callGPT(String prompt) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + gptApiKey);
        headers.set("Content-Type", "application/json");

        // ObjectMapper로 JSON 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode requestBody = objectMapper.createObjectNode()
                .put("model", "gpt-4")
                .put("max_tokens", 200)
                .set("messages", objectMapper.createArrayNode().add(objectMapper.createObjectNode()
                        .put("role", "user")
                        .put("content", prompt)));

        // JSON 형식으로 직렬화하여 요청 본문으로 사용
        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        HttpEntity<String> request = new HttpEntity<>(jsonRequestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(gptApiUrl, HttpMethod.POST, request, String.class);

        // 응답 처리
        ObjectMapper responseMapper = new ObjectMapper();
        JsonNode rootNode = responseMapper.readTree(response.getBody());
        return rootNode.path("choices").get(0).path("message").path("content").asText();
    }


    //최초 프롬프트 생성
    public String getImagePrompt(String userCommand) throws JsonProcessingException {
        System.out.println("사용자 명령어: " + userCommand);

        //프롬프트 조건 : 실제 이미지와 유사하게 나오도록.
        String promptMessage = String.format(
                "Write a descriptive and creative Korean prompt to generate an image based on \\\"%s\\\" according to the following user command. \" +\n" +
                "\"Use the following rules: \" +\n" +
                "\"1. If the input contains a 3-letter airport code (e.g., DBX, LAX, ICN), infer an iconic landmark or tourist attraction of that location and set it as the primary background of the image. \" +\n" +
                "\"2. If there are seasonal keywords (e.g., spring, summer, fall, winter), incorporate them seamlessly into the image’s theme and atmosphere, reflecting the feeling of the season. \" +\n" +
                "\"3. If the input does not specify local specialties, souvenirs, or signature foods, focus solely on landmarks and avoid adding distinct foreground objects. \" +\n" +
                "\"4. Exclude explicit references to airplanes, airports, or airport codes in the generated prompt. \" +\n" +
                "\"5. Create an emotionally and artistically styled picture, using warm and soft colors to evoke a peaceful and inviting atmosphere. Avoid overly sharp or exaggerated expressions. \" +\n" +
                "\"6. Write the final output in only one concise sentence in Korean!! Keep it simple and focused. \" +\n" +
                "\"7. Ensure the main landmarks stand out prominently from the background while maintaining harmony with the surrounding elements.\"+\n" +
                "\"8. Exclude words that means 'drawing' or any describing words as 'drawing', 'depicting' or 'portraying'.",
                userCommand
        );

        String korContent = callGPT(promptMessage);
        System.out.println("생성된 한국어 프롬프트: " + korContent);
        return korContent;
    }

    // 수정된 한국어 프롬프트를 영어로 번역
    public String translateToEnglish(String korContent) throws JsonProcessingException {
        String translatedPromptMessage = String.format("Translate the following prompt into natural English. Make prompt always start with 'A realistic scene'. Exclude 'illustration' or any describing words as 'drawing', 'depicting' or 'portraying'.: \"%s\"", korContent);

        String EnContent = callGPT(translatedPromptMessage);
        System.out.println("영어로 번역된 프롬프트: " + EnContent);

        return EnContent;
    }
}

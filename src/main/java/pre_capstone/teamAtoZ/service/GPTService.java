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
import org.springframework.web.multipart.MultipartFile;

@Service
public class GPTService {
    private final String gptApiKey;
    private final String gptApiUrl = "https://api.openai.com/v1/chat/completions";

    @Autowired
    private RestTemplate restTemplate;

    public GPTService(String gptApiKey) {
        this.gptApiKey = gptApiKey;
    }
    
    public String getImagePrompt(String userCommand) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + gptApiKey);
        headers.set("Content-Type", "application/json");

        System.out.println("사용자 명령어: " + userCommand);
        
        // 프롬프트 조건
        String promptMessage = String.format(
                "%s. 이 키워드와 공항 코드로 특정 국가의 랜드마크와 배경을 바탕으로 계절감과 분위기가 조화된 이미지를 만들어줘. " +
                "1. 영어 대문자 세 글자는 공항 코드야. 공항 코드나 도시명이 들어가면 그 장소의 랜드마크가 크게 가운데에 놓인 느낌으로 해줘. " +
                "2. 계절을 암시하는 키워드가 있으면 계절감도 자연스럽게 표현해줘. " +
                "3. 나머지 키워드는 자연스럽게 조화롭게 배치하고, 과장 없이 사실적인 느낌으로 해줘. " +
                "생성되는 프롬프트에는 공항이나 비행기, 공항 코드가 들어가면 안 돼. " +
                "전체 배경보다는 주요 대상이 강조되고, 무드 있는 분위기를 유지하면서도 너무 선명하지 않은 색감으로 따뜻하고 부드러운 느낌으로 부탁해. " +
                "부연 설명 없이 프롬프트만 간단히 영어로 작성해줘.",
                userCommand
        );

        String requestBody = String.format(
                "{\"model\":\"gpt-4\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}],\"max_tokens\":150}",
                promptMessage
        );

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(gptApiUrl, HttpMethod.POST, request, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        String content = rootNode.path("choices").get(0).path("message").path("content").asText();
        System.out.println("생성된 프롬프트: " + content);

        return content;
    }
}

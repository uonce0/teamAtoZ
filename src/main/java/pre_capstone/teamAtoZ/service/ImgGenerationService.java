package pre_capstone.teamAtoZ.service;

import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImgGenerationService {

	@Autowired
    private OpenAiService openAiService;

    public ImgGenerationService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }


    public String generateImage(String imageDescription, String destination, String season) {

    	String enhancedDescription = enhanceDescription(imageDescription, destination, season);

        // 이미지 생성 요청
        CreateImageRequest request = CreateImageRequest.builder()
                .prompt(enhancedDescription)
                .n(1)
                .size("1024x1024")
                .responseFormat("url")
                .build();

        // 이미지 생성
        List<Image> images = openAiService.createImage(request).getData();

        // 생성된 이미지의 URL 반환
        return images.isEmpty() ? null : images.get(0).getUrl();
    }

    // 키워드 추가
    private String enhanceDescription(String imageDescription, String destination, String season) {
        return String.format("%s. The scene is set in %s during %s.", imageDescription, destination, season);
    }
}

package pre_capstone.teamAtoZ.service;

import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pre_capstone.teamAtoZ.dto.FilePathDTO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ImgGenerationService {

	@Autowired
    private OpenAiService openAiService;

    @Autowired
    private CompressImageService compressImageService;

    @Autowired
    private FilePathDTO filePathDTO;

    private static String uniqueFileName;

    private static List<Image> images;

    public ImgGenerationService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public String generateImage(String imageDescription) throws IOException {
        // 이미지 생성 요청
        CreateImageRequest request = CreateImageRequest.builder()
                .prompt(imageDescription)
                .n(1)
                .size("1024x1024")
                .responseFormat("url")
                .build();

        // 이미지 생성
        images = openAiService.createImage(request).getData();

        // 생성된 이미지의 URL을 통해 이미지를 다운로드하고 압축
        if (!images.isEmpty()) {
            String imageUrl = images.get(0).getUrl();
            BufferedImage originalImage = ImageIO.read(new URL(imageUrl));

            System.out.println("생성된 이미지 url:" +imageUrl);

            // 내부 디렉토리 폴더 경로 얻기
            String userDir = System.getProperty("user.dir");
            String downloadsPath = userDir + "/src/main/generated/original/";

            // main 폴더 경로에 generated 폴더를 추가 (없으면 자동으로 생성)
            File directory = new File(downloadsPath);
            // images 폴더가 없으면 생성
            if (!directory.exists()) {
                directory.mkdirs();  // 자동으로 디렉토리 생성
            }

            // 고유한 파일 이름 생성 : 현재 시간을 기준으로 생성
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            uniqueFileName = simpleDateFormat.format(new Date()) + ".jpg";
            File compressedImageFile = new File(downloadsPath + uniqueFileName);

            // 이미지 압축
            compressImageService.compressImage(originalImage, compressedImageFile, 0.7f); // 0.7f는 압축 품질

            filePathDTO.setOnlyImageFile(uniqueFileName);
            // 압축된 이미지 파일을 URL로 변환하여 반환 (또는 파일 경로 반환)
            return "generated/original/" + uniqueFileName;
        }
        System.out.println("이미지 생성 안됨");
        return null;
    }

    // 생성된 이미지 url 반환
    public String getGeneratedImageUrl() {
        return images.isEmpty() ? null : "generated/original/" + uniqueFileName;
    }
}

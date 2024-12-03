package pre_capstone.teamAtoZ.config;

import org.springframework.web.client.RestTemplate;
import pre_capstone.teamAtoZ.service.*;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


@Configuration
public class AppConfig {

    @Value("${openai.key}")
    private String gptApiKey;

    @Bean
    public String gptApiKey() {
        return gptApiKey;
    }

    @Bean
    public GPTService gptService() {
        return new GPTService(gptApiKey);
    }

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(gptApiKey, Duration.ofSeconds(60));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

package pre_capstone.teamAtoZ.config;

import pre_capstone.teamAtoZ.service.*;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

    @Value("${openai.key}")
    private String gptApiKey;

//    @Value("${poorio.api.key}")
//    private String poorioApiKey;

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
        return new OpenAiService(gptApiKey);
    }

//    @Bean
//    public PooRioService pooRioService() {
//        return new PooRioService(poorioApiKey);
//    }
}

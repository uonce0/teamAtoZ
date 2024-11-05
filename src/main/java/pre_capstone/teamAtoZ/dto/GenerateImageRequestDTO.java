package pre_capstone.teamAtoZ.dto;

import lombok.Data;

import java.util.List;

@Data
public class GenerateImageRequestDTO {
    private String userCommand;
    private String destination;
    private String season;
    private List<String> keyWords; // 키워드를 리스트로 추가
}

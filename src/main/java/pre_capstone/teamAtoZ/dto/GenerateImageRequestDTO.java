package pre_capstone.teamAtoZ.dto;

import lombok.Data;

@Data
public class GenerateImageRequestDTO {
    private String userCommand;
    private String destination;
    private String season;
}

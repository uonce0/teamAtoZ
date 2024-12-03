package pre_capstone.teamAtoZ.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class FilePathDTO {
    //이미지 정보 저장을 위한 DTO
    private String onlyImageFile;
    private String editedFile;
}

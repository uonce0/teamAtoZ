package pre_capstone.teamAtoZ.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class FilePathDTO {
    private String onlyImageFile;
    private String editedFile;
}

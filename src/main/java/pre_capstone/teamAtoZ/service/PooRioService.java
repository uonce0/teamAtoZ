package pre_capstone.teamAtoZ.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class PooRioService {
    //private final String poorioApiKey;

    public PooRioService() {
		//this.poorioApiKey = poorioApiKey;
        // TODO Auto-generated constructor stub
	}

	public boolean sendTextWithImage(String phoneNumber, String message, MultipartFile image) {
        // PooRio API 연동하여 문자와 이미지를 전송하는 로직
        // 성공 여부 반환
        return true; // 성공 여부에 따라 true/false 반환
    }
}

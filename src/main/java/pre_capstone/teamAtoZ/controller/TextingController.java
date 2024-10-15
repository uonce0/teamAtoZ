package pre_capstone.teamAtoZ.controller;

import pre_capstone.teamAtoZ.service.PooRioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/texting")
public class TextingController {
    @Autowired
    private PooRioService pooRioService;

    @PostMapping("/send")
    public ResponseEntity<String> sendText(@RequestParam("phoneNumber") String phoneNumber, 
                                           @RequestParam("message") String message, 
                                           @RequestParam("image") MultipartFile image) {
        boolean success = pooRioService.sendTextWithImage(phoneNumber, message, image);
        if (success) {
            return ResponseEntity.ok("Message sent successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Message sending failed.");
        }
    }
}

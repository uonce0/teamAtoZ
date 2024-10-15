package pre_capstone.teamAtoZ.controller;

import pre_capstone.teamAtoZ.service.GPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/doc-scan")
public class DocScanController {
    @Autowired
    private GPTService gptService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        String extractedInfo = gptService.scanDocument(file);
        return ResponseEntity.ok(extractedInfo);
    }
}

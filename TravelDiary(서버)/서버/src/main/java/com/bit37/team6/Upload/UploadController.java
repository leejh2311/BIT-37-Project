package com.bit37.team6.Upload;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UploadController {
    @GetMapping("/upload")
    public String hello() {
        return "업로드 페이지 GET 응답";
    }
}

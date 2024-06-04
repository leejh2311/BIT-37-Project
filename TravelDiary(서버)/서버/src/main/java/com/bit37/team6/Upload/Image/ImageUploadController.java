package com.bit37.team6.Upload.Image;

import com.bit37.team6.Upload.Article.ArticleEntity;
import com.bit37.team6.register.AccountEntity;
import com.bit37.team6.register.AccountRepository;
import com.bit37.team6.response.JSONResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@RestController
public class ImageUploadController {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ImageRepository imageRepository;

    @GetMapping("/upload/image")
    public String hello() {
        return "이미지 업로드 페이지 GET 응답";
    }

    @Transactional
    @PostMapping("/upload/image")
    public JSONResponse<String> UploadImage(@RequestBody UploadImageDTO imageDTO) {
        String filePath = System.getProperty("user.dir")
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "resources"
                + File.separator + "image"
                + File.separator;

        String imageBase64 = imageDTO.getImageBase64();
        String imageName = imageDTO.getImageName();
        String uploaderIdString = imageDTO.getUploaderIdString();
        long articleId = imageDTO.getArticleId();
        float gps_la = imageDTO.getGps_la();
        float gps_lo = imageDTO.getGps_lo();

        //UUID 적용
        String uuid = UUID.randomUUID().toString();
        String imageNameChanged = uuid + "_" + imageName;

        System.out.println(filePath + imageNameChanged);
        //이미지 변환 및 저장
        try {
            //저장 경로 지정
            File file = new File(filePath + imageNameChanged);

            //Base64 디코딩
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decodedByte = decoder.decode(imageBase64.getBytes());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(decodedByte);
            fileOutputStream.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        System.out.println("Save Picture: " + imageNameChanged);

        AccountEntity uploader = accountRepository.findByUserId(uploaderIdString);
        ImageEntity newImage = new ImageEntity();
        newImage.setUploader(uploader);

        ArticleEntity newArticle = new ArticleEntity();
        newArticle.setArticleId(articleId);

        newImage.setArticle(newArticle);
        newImage.setImageName(imageNameChanged);
        newImage.setImageAddress(filePath + imageNameChanged);

        if (gps_la != -1000.0) {
            newImage.setGps_la(gps_la);
            newImage.setGps_lo(gps_lo);
        }
        else {
            newImage.setGps_la(null);
            newImage.setGps_lo(null);
        }

        imageRepository.save(newImage);

        return new JSONResponse<String>(0, "OK", imageNameChanged);
    }
}

package com.bit37.team6.Upload.Article;

import com.bit37.team6.Upload.Image.ImageEntity;
import com.bit37.team6.Upload.Image.ImageRepository;
import com.bit37.team6.Upload.Package.PackageEntity;
import com.bit37.team6.Upload.Package.PackageRepository;
import com.bit37.team6.Upload.Package.PackageUploadController;
import com.bit37.team6.Upload.Section.SectionEntity;
import com.bit37.team6.Upload.Section.SectionRepository;
import com.bit37.team6.register.AccountEntity;
import com.bit37.team6.register.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ArticleUploadController {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    PackageRepository packageRepository;
    @Autowired
    EntityManager entityManager;

    @GetMapping("/upload/article")
    public String hello() {
        return "글 업로드 페이지 GET 응답";
    }

    @Transactional
    @PostMapping("/upload/article")
    public String uploadArticle(@RequestBody UploadArticleDTO articleDTO) {
        if (articleDTO.getArticleId() != -1) {
            return uploadDone(articleDTO);
        }

        long travelId = articleDTO.getTravelId();
        String articleText = articleDTO.getText();
        String uploaderIdString = articleDTO.getUploaderIdString();

        AccountEntity uploader = accountRepository.findByUserId(uploaderIdString);

        ArticleEntity newArticle = new ArticleEntity();
        newArticle.setUploader(uploader);
        newArticle.setTravelId(20230727); //임시 처리
        newArticle.setArticleText(articleText);

        System.out.println("Save Text: " + articleText);

        ArticleEntity inputArticle = articleRepository.save(newArticle);

        //Section 분리
        String[] sectionArg = articleText.split("\n\n");

        //데이터 확인
        for (int i = 0; i < sectionArg.length; i++) {
            sectionArg[i] = sectionArg[i].replace("\n", "");
            System.out.println(sectionArg[i] + "\t");
        }

        for (int i = 0; i < sectionArg.length; i++) {
            SectionEntity newSection = new SectionEntity();
            newSection.setArticle(newArticle);
            newSection.setSectionText(sectionArg[i]);

            sectionRepository.save(newSection);
        }

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("code", 0);
        jsonObj.put("msg", "OK");

        JSONObject item = new JSONObject();
        item.put("travelId", travelId);
        item.put("articleId", inputArticle.getArticleId());
        item.put("uploaderIdString", uploaderIdString);
        item.put("text", articleText);

        jsonObj.put("item", item);

        return jsonObj.toString(4);
    }

    public String uploadDone(@RequestBody UploadArticleDTO articleDTO) {
        long articleId = articleDTO.getArticleId();
        ArticleEntity article = new ArticleEntity();
        article.setArticleId(articleId);

        List<ImageEntity> findImages = imageRepository.findByArticle(article);
        if (findImages.size() == 1) {
            ArticleEntity currentArticle = articleRepository.findByArticleId(articleId);

            List<SectionEntity> deleteSections = sectionRepository.findByArticle(article);
            for (SectionEntity deleteSection : deleteSections) {
                sectionRepository.deleteById(deleteSection.getSectionId());
            }

            SectionEntity newSection = new SectionEntity();
            newSection.setArticle(article);
            newSection.setSectionText(currentArticle.getArticleText());

            SectionEntity saveSection = sectionRepository.save(newSection);

            PackageEntity newPackage = new PackageEntity();
            newPackage.setArticle(article);
            newPackage.setImage(findImages.get(0));
            newPackage.setSection(saveSection);

            packageRepository.save(newPackage);

            return "OK one";
        }

        articlePythonThread articlePythonThread = new articlePythonThread();
        articlePythonThread.setArticleId(articleId);
        articlePythonThread.run();

        return "OK";
    }

    /* 미사용 */
    public long getNextArticleId() {
        Query query = entityManager.createQuery(
                "SELECT AUTO_INCREMENT" +
                        "FROM information_schema.tables" +
                        "WHERE table_name = 'article'" +
                        "AND table_schema = DATABASE();"
        );

        long nextArticleId = query.getFirstResult();

        return nextArticleId;
    }
}
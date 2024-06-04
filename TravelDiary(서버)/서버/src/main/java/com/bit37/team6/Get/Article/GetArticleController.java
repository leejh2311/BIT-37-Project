package com.bit37.team6.Get.Article;

import com.bit37.team6.Upload.Article.ArticleEntity;
import com.bit37.team6.Upload.Article.ArticleRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetArticleController {
    @Autowired
    ArticleRepository articleRepository;

    @GetMapping("/get/article")
    public String hello() {
        return "Article 조회 페이지 GET 응답";
    }

    @PostMapping("/get/article")
    public String getArticle(@RequestBody GetArticleDTO getArticleDTO) {
        Long TravelId = getArticleDTO.getTravelId();
        String uploaderIdString = getArticleDTO.getUploaderIdString(); //미사용

        List<ArticleEntity> articleEntity = articleRepository.findByTravelId(TravelId);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("TravelId", getArticleDTO.getTravelId());

        JSONArray jsonArr = new JSONArray();

        for(ArticleEntity articleValue : articleEntity) {
            JSONObject articleObj = new JSONObject();
            articleObj.put("articleId", articleValue.getArticleId());
            articleObj.put("articleText", articleValue.getArticleText());
            articleObj.put("uploaderIdString", (articleValue.getUploader()).getUserId());
            jsonArr.put(articleObj);
        }

        jsonObj.put("Item", jsonArr);

        return jsonObj.toString(4);
    }
}

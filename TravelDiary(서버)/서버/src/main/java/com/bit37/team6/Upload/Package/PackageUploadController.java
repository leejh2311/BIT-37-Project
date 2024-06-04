package com.bit37.team6.Upload.Package;

import com.bit37.team6.Upload.Article.ArticleEntity;
import com.bit37.team6.Upload.Article.ArticleRepository;
import com.bit37.team6.Upload.Image.ImageEntity;
import com.bit37.team6.Upload.Section.SectionEntity;
import com.bit37.team6.response.JSONResponse;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PackageUploadController {
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    PackageRepository packageRepository;

    @GetMapping("/upload/package")
    public String hello() {
        return "패키지 업로드 페이지 GET 응답";
    }

    @Transactional
    @PostMapping("/upload/package")
    public String uploadPackage(@RequestBody PackageDTO packageDTO) {
        long articleId = packageDTO.getArticleId();

        int countItem = packageDTO.getCountItem();
        List<PackageItemDTO> packageItems = packageDTO.getItems();

        for (PackageItemDTO packItem : packageItems) {
            long sectionId = packItem.getSectionId();
            long imageId = packItem.getImageId();

            ArticleEntity newArticle = articleRepository.findByArticleId(articleId);
            System.out.println(newArticle.getArticleId());
            SectionEntity newSection = new SectionEntity();
            newSection.setSectionId(sectionId);

            ImageEntity newImage = new ImageEntity();
            newImage.setImageId(imageId);

            PackageEntity newPackage = new PackageEntity();
            newPackage.setArticle(newArticle);
            newPackage.setSection(newSection);
            newPackage.setImage(newImage);

            packageRepository.save(newPackage);
        }

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("articleId", articleId);

        JSONArray item = new JSONArray();

        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setArticleId(articleId);

        List<PackageEntity> findPackages = packageRepository.findByArticle(articleEntity);
        for (PackageEntity resultPackage : findPackages) {
            JSONObject packageObj = new JSONObject();
            packageObj.put("packageId", resultPackage.getPackageId());
            packageObj.put("sectionId", resultPackage.getSectionId());
            packageObj.put("imageId", resultPackage.getImageId());

            System.out.println(item.toString());

            item.put(packageObj);
        }

        jsonObj.put("item", item);

        return jsonObj.toString(4);
    }
}

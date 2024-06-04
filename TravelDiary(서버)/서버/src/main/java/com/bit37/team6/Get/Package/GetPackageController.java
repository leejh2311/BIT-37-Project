package com.bit37.team6.Get.Package;

import com.bit37.team6.Upload.Article.ArticleEntity;
import com.bit37.team6.Upload.Image.ImageRepository;
import com.bit37.team6.Upload.Package.PackageEntity;
import com.bit37.team6.Upload.Package.PackageRepository;
import com.bit37.team6.Upload.Section.SectionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetPackageController {
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    PackageRepository packageRepository;
    @PersistenceContext
    EntityManager entityManager;

    @GetMapping("/get/package")
    public String hello() {
        return "Package 조회 페이지 GET 응답";
    }

    @PostMapping("/get/package")
    public String getPackage(@RequestBody GetPackageDTO getPackageDTO) {
        long travelId = getPackageDTO.getTravelId();
        long articleId = getPackageDTO.getArticleId();

        if (travelId != -1) {
            Query query = entityManager.createQuery(
                    "SELECT package " +
                            "FROM PackageEntity package " +
                            "WHERE package.article.travelId=:travelId"
            );
            query.setParameter("travelId", travelId);

            List<PackageEntity> findPackages = query.getResultList();

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("countPackage", findPackages.size());
            jsonObj.put("travelId", travelId);

            JSONArray jsonArray = generatePackageJson(findPackages);
            jsonObj.put("item", jsonArray);

            return jsonObj.toString(4);
        } else if (articleId != -1) {
            ArticleEntity articleEntity = new ArticleEntity();
            articleEntity.setArticleId(articleId);

            List<PackageEntity> findPackages = packageRepository.findByArticle(articleEntity);

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("countPackage", findPackages.size());
            jsonObj.put("articleId", articleId);

            JSONArray jsonArray = generatePackageJson(findPackages);
            jsonObj.put("item", jsonArray);

            return jsonObj.toString(4);
        }

        return "No Data";
    }

    @PostMapping("/get/package/detail")
    public String getPackageDetail(@RequestBody GetPackageDTO getPackageDTO) {
        long travelId = getPackageDTO.getTravelId();
        long articleId = getPackageDTO.getArticleId();

        if (travelId != -1) {
            Query query = entityManager.createQuery(
                    "SELECT package " +
                            "FROM PackageEntity package " +
                            "JOIN FETCH package.section " +
                            "JOIN FETCH package.image " +
                            "WHERE package.article.travelId=:travelId"
            ).setParameter("travelId", travelId);

            List<PackageEntity> findPackages = query.getResultList();

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("countPackage", findPackages.size());
            jsonObj.put("travelId", travelId);

            JSONArray jsonArray = generatePackageDetailJson(findPackages);
            jsonObj.put("item", jsonArray);

            return jsonObj.toString(4);
        }
        return "No Data";
    }

    public JSONArray generatePackageJson(List<PackageEntity> findPackages) {
        JSONArray jsonArr = new JSONArray();

        for (PackageEntity resultPackage : findPackages) {
            JSONObject packageObj = new JSONObject();
            packageObj.put("packageId", resultPackage.getPackageId());
            packageObj.put("imageId", resultPackage.getImageId());
            packageObj.put("sectionId", resultPackage.getSectionId());

            jsonArr.put(packageObj);
        }

        return jsonArr;
    }

    public JSONArray generatePackageDetailJson(List<PackageEntity> findPackages) {
        JSONArray jsonArr = new JSONArray();

        for (PackageEntity resultPackage : findPackages) {
            JSONObject packageObj = new JSONObject();
            packageObj.put("packageId", resultPackage.getPackageId());
            packageObj.put("imageId", resultPackage.getImageId());
            packageObj.put("imageName", resultPackage.getImage().getImageName());
            packageObj.put("sectionId", resultPackage.getSectionId());
            packageObj.put("sectionText", resultPackage.getSection().getSectionText());

            jsonArr.put(packageObj);
        }

        return jsonArr;
    }
}

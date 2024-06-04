package com.bit37.team6.Get.Image;

import com.bit37.team6.Upload.Image.ImageEntity;
import com.bit37.team6.Upload.Image.ImageRepository;
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

import java.util.ArrayList;
import java.util.List;

@RestController
public class GetImageController {
    @Autowired
    ImageRepository imageRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/get/image")
    public String hello() { return "Image 조회 페이지 GET 응답"; }

    @PostMapping("/get/image")
    public String getImage(@RequestBody GetImageDTO getImageDTO) {
        System.out.println("/get/image POST 요청ㄴ");
        long travelId = getImageDTO.getTravelId();
        long articleId = getImageDTO.getArticleId();
        long imageId = getImageDTO.getImageId();

        if (travelId != -1) {
            //travelId로 조회하는 경우
            Query query = entityManager.createQuery(
                    "SELECT image " +
                            "FROM ImageEntity image " +
                            "WHERE image.article.travelId=:travelId"
            );
            query.setParameter("travelId", travelId);

            List<ImageEntity> findImages = query.getResultList();

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("countImage", findImages.size());
            jsonObj.put("travelId", getImageDTO.getTravelId());

            JSONArray jsonArr = generateImagesJson(findImages);
            jsonObj.put("Item", jsonArr);

            return jsonObj.toString(4);
        } else if (articleId != -1) {
            //articleId로 조회하는 경우
            Query query = entityManager.createQuery(
                    "SELECT image " +
                            "FROM ImageEntity image " +
                            "WHERE image.article.articleId=:articleId"
            );
            query.setParameter("articleId", articleId);

            List<ImageEntity> findImages = query.getResultList();

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("countImage", findImages.size());
            jsonObj.put("articleId", articleId);

            JSONArray jsonArr = generateImagesJson(findImages);
            jsonObj.put("Item", jsonArr);

            return jsonObj.toString(4);
        } else if (imageId != -1) {
            //imageId로 조회하는 경우(단일 조회)
            ImageEntity imageEntity = imageRepository.findByImageId(imageId);

            //임시 변수 생성
            List<ImageEntity> findImages = new ArrayList<ImageEntity>();
            findImages.add(imageEntity);

            JSONObject jsonObj = generateImagesJson(findImages).getJSONObject(0);

            return jsonObj.toString(4);
        }

        return "No Data";
    }

    @PostMapping("/get/image/")

    public JSONArray generateImagesJson(List<ImageEntity> findImages) {
        JSONArray jsonArr = new JSONArray();

        if (!findImages.isEmpty()) {
            for (ImageEntity imageValue : findImages) {
                JSONObject imageObj = new JSONObject();
                imageObj.put("imageId", imageValue.getImageId());
                imageObj.put("imageName", imageValue.getImageName());
                imageObj.put("uploaderIdString", imageValue.getUploader().getUserId());
                imageObj.put("imageAddress", imageValue.getImageAddress());
                imageObj.put("gps_la", (imageValue.getGps_la() == null) ? "null" : imageValue.getGps_la());
                imageObj.put("gps_lo", (imageValue.getGps_lo() == null) ? "null" : imageValue.getGps_lo());

                jsonArr.put(imageObj);
            }
        }

        return jsonArr;
    }

}
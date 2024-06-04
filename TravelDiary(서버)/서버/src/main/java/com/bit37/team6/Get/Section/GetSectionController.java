package com.bit37.team6.Get.Section;

import com.bit37.team6.Upload.Section.SectionEntity;
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
public class GetSectionController {
    @Autowired
    SectionRepository sectionRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @GetMapping("/get/section")
    public String hello() {
        return "Section 조회 페이지 GET 응답";
    }

    @PostMapping("/get/section")
    public String getSection(@RequestBody GetSectionDTO getSectionDTO) {
        Long articleId = getSectionDTO.getArticleId();
        Long travelId = getSectionDTO.getTravelId();

        Query query = null;

        if (articleId != -1) {
            query = entityManager.createQuery(
                    "SELECT section " +
                    "FROM SectionEntity section " +
                    "WHERE section.article.articleId=:articleId"
            ).setParameter("articleId", articleId);
        } else if (travelId != -1) {
            query = entityManager.createQuery(
                    "SELECT section " +
                            "FROM SectionEntity section " +
                            "WHERE section.article.travelId=:travelId"
            ).setParameter("travelId", travelId);
        }

        List<SectionEntity> findSection = query.getResultList();

        //travelId 필요시 활성화
        /*
        if (articleId != -1) {
            getSectionDTO.setTravelId(findSection.get(0).getArticle().getTravelId());
        }
        */

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("countSection", findSection.size());
        jsonObj.put("travelId", getSectionDTO.getTravelId());

        JSONArray jsonArr = new JSONArray();

        if (!findSection.isEmpty()) {
            for (SectionEntity sectionValue : findSection) {
                JSONObject sectionObj = new JSONObject();
                sectionObj.put("sectionId", sectionValue.getSectionId());
                sectionObj.put("articleId", sectionValue.getArticle().getArticleId());
                sectionObj.put("sectionText", sectionValue.getSectionText());

                jsonArr.put(sectionObj);
            }
        }

        jsonObj.put("Item", jsonArr);

        return jsonObj.toString(4);
    }
}

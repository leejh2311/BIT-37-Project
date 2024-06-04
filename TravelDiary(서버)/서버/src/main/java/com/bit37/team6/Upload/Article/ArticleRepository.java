package com.bit37.team6.Upload.Article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
    List<ArticleEntity> findByTravelId(Long travelId);

    ArticleEntity findByArticleId(Long articleId);
}
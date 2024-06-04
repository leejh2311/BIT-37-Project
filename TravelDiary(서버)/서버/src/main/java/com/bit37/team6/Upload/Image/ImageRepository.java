package com.bit37.team6.Upload.Image;

import com.bit37.team6.Upload.Article.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    ImageEntity findByImageId(Long imageId);
    List<ImageEntity> findByArticle(ArticleEntity article);
}

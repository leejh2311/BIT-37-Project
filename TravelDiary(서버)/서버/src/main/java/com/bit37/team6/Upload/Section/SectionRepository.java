package com.bit37.team6.Upload.Section;

import com.bit37.team6.Upload.Article.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<SectionEntity, Long> {
    List<SectionEntity> findAll();
    List<SectionEntity> findByArticle(ArticleEntity article);
}

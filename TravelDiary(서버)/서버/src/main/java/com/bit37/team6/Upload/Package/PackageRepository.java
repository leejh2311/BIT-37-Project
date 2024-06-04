package com.bit37.team6.Upload.Package;

import com.bit37.team6.Upload.Article.ArticleEntity;
import com.bit37.team6.Upload.Image.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<PackageEntity, Long> {
    List<PackageEntity> findByArticle(ArticleEntity article);
}

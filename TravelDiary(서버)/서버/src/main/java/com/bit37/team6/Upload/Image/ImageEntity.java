package com.bit37.team6.Upload.Image;

import com.bit37.team6.Upload.Article.ArticleEntity;
import com.bit37.team6.register.AccountEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "image")
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long imageId;

    @Column(nullable = false)
    private String imageAddress;

    @Column(nullable = false)
    private String imageName;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private ArticleEntity article;

    @ManyToOne
    @JoinColumn(name = "uploader_id")
    private AccountEntity uploader;

    //위도
    @Column
    private Float gps_la;

    //경도
    @Column
    private Float gps_lo;

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public String getImageAddress() {
        return imageAddress;
    }

    public void setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public ArticleEntity getArticle() {
        return article;
    }

    public void setArticle(ArticleEntity article) {
        this.article = article;
    }

    public long getArticleId() {
        return this.article.getArticleId();
    }

    public void setArticleId(long articleId) {
        this.article.setArticleId(articleId);
    }

    public AccountEntity getUploader() {
        return uploader;
    }

    public void setUploader(AccountEntity uploader) {
        this.uploader = uploader;
    }

    public Float getGps_la() {
        if (gps_la == null) {
            return null;
        }
        return gps_la;
    }

    public void setGps_la(Float gps_la) {
        this.gps_la = gps_la;
    }

    public Float getGps_lo() {
        return gps_lo;
    }

    public void setGps_lo(Float gps_lo) {
        this.gps_lo = gps_lo;
    }
}

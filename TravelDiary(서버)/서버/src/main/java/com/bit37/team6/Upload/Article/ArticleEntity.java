package com.bit37.team6.Upload.Article;

import com.bit37.team6.register.AccountEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "article")
public class ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long articleId;

    @Column(nullable = false)
    private long travelId;

    @ManyToOne
    @JoinColumn(name = "uploader_id")
    private AccountEntity uploader;

    @Column(nullable = false)
    private String articleText;

    public ArticleEntity() {
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    public long getTravelId() {
        return travelId;
    }

    public void setTravelId(long travelId) {
        this.travelId = travelId;
    }

    public AccountEntity getUploader() {
        return uploader;
    }

    public void setUploader(AccountEntity uploader) {
        this.uploader = uploader;
    }

    public String getArticleText() {
        return articleText;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }
}

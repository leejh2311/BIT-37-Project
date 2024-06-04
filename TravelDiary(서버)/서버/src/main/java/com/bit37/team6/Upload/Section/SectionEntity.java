package com.bit37.team6.Upload.Section;

import com.bit37.team6.Upload.Article.ArticleEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "section")
public class SectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long sectionId;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private ArticleEntity article;

    @Column(nullable = false)
    private String sectionText;

    public SectionEntity() {
    }

    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(long sectionId) {
        this.sectionId = sectionId;
    }

    public ArticleEntity getArticle() {
        return article;
    }

    public void setArticle(ArticleEntity article) {
        this.article = article;
    }

    public String getSectionText() {
        return sectionText;
    }

    public void setSectionText(String sectionText) {
        this.sectionText = sectionText;
    }
}

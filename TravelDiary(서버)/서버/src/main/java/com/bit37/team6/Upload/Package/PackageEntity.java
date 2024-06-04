package com.bit37.team6.Upload.Package;

import com.bit37.team6.Upload.Article.ArticleEntity;
import com.bit37.team6.Upload.Image.ImageEntity;
import com.bit37.team6.Upload.Section.SectionEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "package")
public class PackageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long packageId;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private ArticleEntity article;

    @OneToOne
    @JoinColumn(name = "image_id")
    private ImageEntity image;

    @OneToOne
    @JoinColumn(name = "section_id")
    private SectionEntity section;

    public PackageEntity() {
    }

    public long getPackageId() {
        return packageId;
    }

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }

    public ArticleEntity getArticle() {
        return article;
    }

    public void setArticle(ArticleEntity article) {
        this.article = article;
    }

    public ImageEntity getImage() {
        return image;
    }

    public void setImage(ImageEntity image) {
        this.image = image;
    }

    public SectionEntity getSection() {
        return section;
    }

    public void setSection(SectionEntity section) {
        this.section = section;
    }

    public long getSectionId() {
        return this.section.getSectionId();
    }

    public long getImageId() {
        return this.image.getImageId();
    }
}

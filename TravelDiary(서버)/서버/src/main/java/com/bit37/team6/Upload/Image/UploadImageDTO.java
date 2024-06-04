package com.bit37.team6.Upload.Image;

public class UploadImageDTO {
    private Long articleId;
    private String imageName;
    private String imageBase64;
    private String uploaderIdString;
    private float gps_la = -1000;
    private float gps_lo = -1000;

    public UploadImageDTO() {
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getUploaderIdString() {
        return uploaderIdString;
    }

    public void setUploaderIdString(String uploaderIdString) {
        this.uploaderIdString = uploaderIdString;
    }

    public float getGps_la() {
        return gps_la;
    }

    public void setGps_la(float gps_la) {
        this.gps_la = gps_la;
    }

    public float getGps_lo() {
        return gps_lo;
    }

    public void setGps_lo(float gps_lo) {
        this.gps_lo = gps_lo;
    }
}
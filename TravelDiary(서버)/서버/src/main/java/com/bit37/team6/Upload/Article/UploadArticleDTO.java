package com.bit37.team6.Upload.Article;

public class UploadArticleDTO {
    private long travelId;
    private long articleId = -1;
    private String uploaderIdString;
    private String text;

    public UploadArticleDTO() {
    }

    public long getTravelId() {
        return travelId;
    }

    public void setTravelId(long travelId) {
        this.travelId = travelId;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    public String getUploaderIdString() {
        return uploaderIdString;
    }

    public void setUploaderIdString(String uploaderIdString) {
        this.uploaderIdString = uploaderIdString;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

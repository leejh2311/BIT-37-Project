package com.bit37.team6.Get.Article;

public class GetArticleDTO {
    private long articleId;
    private long travelId;
    private String uploaderIdString;

    public GetArticleDTO() {
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

    public String getUploaderIdString() {
        return uploaderIdString;
    }

    public void setUploaderIdString(String uploaderIdString) {
        this.uploaderIdString = uploaderIdString;
    }
}

package com.bit37.team6.Get.Image;

public class GetImageDTO {
    private long travelId;
    private long articleId;
    private long imageId;

    public GetImageDTO() {
        travelId = -1;
        articleId = -1;
        imageId = -1;
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

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }
}

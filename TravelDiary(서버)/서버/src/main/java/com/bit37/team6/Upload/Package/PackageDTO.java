package com.bit37.team6.Upload.Package;

import java.util.List;

public class PackageDTO {
    private long articleId;
    private int countItem;
    private List<PackageItemDTO> items;

    public PackageDTO() {
    }

    public int getCountItem() {
        return countItem;
    }

    public void setCountItem(int countItem) {
        this.countItem = countItem;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    public List<PackageItemDTO> getItems() {
        return items;
    }

    public void setItems(List<PackageItemDTO> items) {
        this.items = items;
    }
}

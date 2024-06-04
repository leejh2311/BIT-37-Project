package com.bit37.team6.Upload.Package;

public class PackageItemDTO {
    private long sectionId;
    private long imageId;

    public PackageItemDTO() {
    }

    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(long sectionId) {
        this.sectionId = sectionId;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }
}

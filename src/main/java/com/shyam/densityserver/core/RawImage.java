package com.shyam.densityserver.core;

public class RawImage {

    private String cameraId;

    private String rawImage;

    private String locationId;

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getRawImage() {
        return rawImage;
    }

    public void setRawImage(String rawImage) {
        this.rawImage = rawImage;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}

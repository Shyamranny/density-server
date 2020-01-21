package com.shyam.densityserver.core;

public class CameraDensity {

    private String cameraId;
    private int density;
    private String locationId;

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public int getDensity() {
        return density;
    }

    public void setDensity(int density) {
        this.density = density;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    @Override
    public String toString() {
        return "{" +
                "\"cameraId\":\"" + cameraId + "\"" +
                ", \"density\":" + density +
                ", \"locationId\":\"" + locationId + "\"" +
                '}';
    }
}

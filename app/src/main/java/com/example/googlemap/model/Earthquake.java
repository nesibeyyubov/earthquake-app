package com.example.googlemap.model;

public class Earthquake {
    private String place;
    private String type;
    private String detailLink;
    private double magnitude;
    private double lat;
    private double lng;
    private long time;

    public Earthquake(String place, String type, String detailLink, double magnitude, double lat, double lng, long time) {
        this.place = place;
        this.type = type;
        this.detailLink = detailLink;
        this.magnitude = magnitude;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }

    public Earthquake() {
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

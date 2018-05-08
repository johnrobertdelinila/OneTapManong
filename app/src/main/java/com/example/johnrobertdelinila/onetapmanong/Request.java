package com.example.johnrobertdelinila.onetapmanong;

public class Request {

    private String questions;
    private String answers;
    private String images;
    private String serviceName;
    private Double latitude;
    private Double longtitude;
    private String locationName;

    public Request() { }

    public Request(String questions, String answers, String images, String serviceName, Double latitude, Double longtitude, String locationName) {
        this.questions = questions;
        this.answers = answers;
        this.images = images;
        this.serviceName = serviceName;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.locationName = locationName;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}

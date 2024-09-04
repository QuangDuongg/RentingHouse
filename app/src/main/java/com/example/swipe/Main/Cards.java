package com.example.swipe.Main;


import java.util.List;

public class Cards {
    private String DPD;
    private String district, address;
    private List<String> roomImageUrl;
    private double distance, latitude, longitude;
    private int price; // in ratio 1K

    public Cards(String DPD, String district, List<String> roomImageUrl, String address, int price, double distance) {
        this.DPD = DPD;
        this.district = district;
        this.roomImageUrl = roomImageUrl;
        this.address = address;
        this.price = price;
        this.distance = distance;
    }

    public Cards(String DPD, String district, List<String> roomImageUrl, String address, int price, double latitude, double longitude) {
        this.DPD = DPD;
        this.district = district;
        this.roomImageUrl = roomImageUrl;
        this.address = address;
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Cards(List<String> roomImageUrl) {
        this.roomImageUrl = roomImageUrl;
    }

    public String getDPD() {
        return DPD;
    }

    public void setDPD(String DPD) {
        this.DPD = DPD;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getRoomImageUrl() {
        return roomImageUrl;
    }

    public void setRoomImageUrl(List<String> roomImageUrl) {
        this.roomImageUrl = roomImageUrl;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isAnyImageRoom (){
        return !this.roomImageUrl.isEmpty();
    }
}

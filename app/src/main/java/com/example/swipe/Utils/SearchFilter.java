package com.example.swipe.Utils;

import java.util.ArrayList;
import java.util.List;

public class SearchFilter {

    // Singleton instance
    private static SearchFilter instance;

    // Class variables
    boolean isForMan, isForWoman;
    ArrayList<Boolean> isDistrict;
    double maxDistance, budget; // budget ratio 1K
    double latitudeUser, longtitudeUser;

    // Private constructor to prevent instantiation
    private SearchFilter() {
        isForMan = false;
        isForWoman = true;
        isDistrict = new ArrayList<>(13);
        for(int i = 1; i <= 12; i++)
            isDistrict.set(i, true);
        maxDistance = 4.0;
        budget = 2000;
        latitudeUser = 0;
        longtitudeUser = 0;
    }

    // Public method to provide access to the instance
    public static SearchFilter getInstance() {
        if (instance == null) {
            instance = new SearchFilter();
        }
        return instance;
    }

    // Additional methods to manipulate the singleton instance can be added here
    // For example, getters and setters for the class variables

    public boolean isForMan() {
        return isForMan;
    }

    public void setForMan(boolean forMan) {
        isForMan = forMan;
    }

    public boolean isForWoman() {
        return isForWoman;
    }

    public void setForWoman(boolean forWoman) {
        isForWoman = forWoman;
    }

    public ArrayList<Boolean> getIsDistrict() {
        return isDistrict;
    }

    public void setIsDistrict(ArrayList<Boolean> isDistrict) {
        this.isDistrict = isDistrict;
    }

    public void setIsDistrictIndex (int index, boolean status){
        this.isDistrict.set(index, status);
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getLatitudeUser() {
        return latitudeUser;
    }

    public double getLongtitudeUser() {
        return longtitudeUser;
    }

    public void setLatitudeUser(double latitudeUser) {
        this.latitudeUser = latitudeUser;
    }

    public void setLongtitudeUser(double longtitudeUser) {
        this.longtitudeUser = longtitudeUser;
    }

    public double calculateDistance(double lat2, double lon2) {
        double theta = this.longtitudeUser - lon2;
        double dist = Math.sin(deg2rad(this.latitudeUser)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(this.latitudeUser)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        double dis = (double) Math.floor(dist);
        if (dis < 1) {
            return 1;
        }
        return dis;
    }

    public boolean checkInDistance(double lat2, double lon2){
        if(calculateDistance(lat2, lon2) <= this.maxDistance)
            return true;
        return false;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}

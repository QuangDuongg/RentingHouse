package com.example.swipe.Utils;

import java.util.ArrayList;
import java.util.List;

public class SearchFilter {

    // Singleton instance
    private static SearchFilter instance;

    // Class variables
    String userID;
    boolean isForMan, isForWoman;
    ArrayList<Boolean> isDistrict;
    double maxDistance, budget; // budget ratio 1K
    double latitudeUser, longitudeUser;

    // Private constructor to prevent instantiation
    private SearchFilter() {
        isForMan = false;
        isForWoman = true;
        isDistrict = new ArrayList<>();
        isDistrict.add(false);
        for(int i = 1; i <= 12; i++)
            isDistrict.add(i, true);
        maxDistance = 4.0;
        budget = 2000;
        latitudeUser = 0;
        longitudeUser = 0;
        userID = "ObTze76baPUz9kkXzguIlCg2u7F3";
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
    public boolean getIsDistrictIndex (int index){
        return this.isDistrict.get(index);
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

    public double getLongitudeUser() {
        return longitudeUser;
    }

    public void setLatitudeUser(double latitudeUser) {
        this.latitudeUser = latitudeUser;
    }

    public void setLongitudeUser(double longitudeUser) {
        this.longitudeUser = longitudeUser;
    }

    public double calculateDistance(double lat2, double lon2) {
        // Radius of the Earth in kilometers
        final double R = 6371.0;

        // Convert latitude and longitude from degrees to radians
        double lat1 = Math.toRadians(this.latitudeUser);
        double lon1 = Math.toRadians(this.longitudeUser);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        // Difference between the two points
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Haversine formula
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distance in kilometers
        double distance = R * c;
        distance = ((distance * 10) - (distance * 10) % 5) / 10;

        // Return the distance
        return (distance < 1) ? 1.0 : distance;
    }


    public boolean checkInDistance(double lat2, double lon2){
        if(calculateDistance(lat2, lon2) <= this.maxDistance)
            return true;
        return false;
    }

    public String ManipPrice (int price){
        int bound_progress = price - price % 100;
        String manip_budget_text = String.valueOf(bound_progress);
        manip_budget_text += "000";
        String tmp = manip_budget_text;
        manip_budget_text = "";
        int cnt = 0;
        for (int i = tmp.length() - 1; i >=0; i--)
        {
            if(cnt == 3) {
                manip_budget_text = " " + manip_budget_text;
                cnt = 0;
            }
            cnt++;
            manip_budget_text = Character.toString(tmp.charAt(i)) + manip_budget_text ;
        }
        manip_budget_text += " VND";
        return manip_budget_text;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

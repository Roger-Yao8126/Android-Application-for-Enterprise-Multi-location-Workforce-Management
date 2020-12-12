package com.example.a277project;

public class Store {

    String name;
    String id;
    String latitude;
    String longitude;
    String employee;

    public Store(){
    }

    public Store(String storeName, String storeLat, String storeLng, String storeID,Integer employee) {
        this.name = storeName;
        this.id = storeID;
        this.latitude = storeLat;
        this.longitude = storeLng;
        this.employee = employee.toString();
    }

    public String getName() {
        return name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(Integer employee) {
        this.employee = employee.toString();
    }
}

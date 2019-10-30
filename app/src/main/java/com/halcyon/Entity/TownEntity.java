package com.halcyon.Entity;

public class TownEntity {


    private  int townID;
    private  String town;
    private  int districtID;


    public TownEntity(int townID, String town, int districtID) {
        this.townID = townID;
        this.town = town;
        this.districtID = districtID;
    }

    public int getTownID() {
        return townID;
    }

    public void setTownID(int townID) {
        this.townID = townID;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public int getDistrictID() {
        return districtID;
    }

    public void setDistrictID(int districtID) {
        this.districtID = districtID;
    }
}

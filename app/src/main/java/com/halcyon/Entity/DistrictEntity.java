package com.halcyon.Entity;

public class DistrictEntity {



    private  int districtID;
    private  String district;


    public DistrictEntity(int districtID, String district) {
        this.districtID = districtID;
        this.district = district;
    }

    public int getDistrictID() {
        return districtID;
    }

    public void setDistrictID(int districtID) {
        this.districtID = districtID;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}

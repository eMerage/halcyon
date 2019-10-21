package com.halcyon.Entity;

/**
 * Created by Amila on 11/13/15.
 */
public class TargetEntity {



    private  String principle;
    private  String productCode;
    private  String productName;
    private  int target;
    private  int achievement;
    private  String different;
    private  Double achievementPresentage;


    public TargetEntity() {
    }

    public TargetEntity(String principle, String productCode, int target, int achievement, String different, Double achievementPresentage,String proName) {
        this.principle = principle;
        this.productCode = productCode;
        this.target = target;
        this.achievement = achievement;
        this.different = different;
        this.achievementPresentage = achievementPresentage;

        this.productName = proName;
    }


    public String getPrinciple() {
        return principle;
    }

    public void setPrinciple(String principle) {
        this.principle = principle;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getAchievement() {
        return achievement;
    }

    public void setAchievement(int achievement) {
        this.achievement = achievement;
    }

    public String getDifferent() {
        return different;
    }

    public void setDifferent(String different) {
        this.different = different;
    }

    public Double getAchievementPresentage() {
        return achievementPresentage;
    }

    public void setAchievementPresentage(Double achievementPresentage) {
        this.achievementPresentage = achievementPresentage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}

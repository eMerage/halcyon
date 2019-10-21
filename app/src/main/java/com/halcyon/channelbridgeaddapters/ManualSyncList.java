package com.halcyon.channelbridgeaddapters;

/**
 * Created by Himanshu on 5/14/2016.
 */
public class ManualSyncList {

    String titel,subtitel;
    int numberOfDone,numbaerOfToDone,progressStatus,synctype,progressColor;
    String syncResult;


    public ManualSyncList(String titel, String subtitel, int numberOfDone, int numbaerOfToDone, int progressStatus, int synctype, String syncResult, int progresscolor) {
        this.titel = titel;
        this.subtitel = subtitel;
        this.numberOfDone = numberOfDone;
        this.numbaerOfToDone = numbaerOfToDone;
        this.progressStatus = progressStatus;
        this.synctype = synctype;
        this.syncResult = syncResult;
        this.progressColor =progresscolor;

    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getSubtitel() {
        return subtitel;
    }

    public void setSubtitel(String subtitel) {
        this.subtitel = subtitel;
    }

    public int getNumberOfDone() {
        return numberOfDone;
    }

    public void setNumberOfDone(int numberOfDone) {
        this.numberOfDone = numberOfDone;
    }

    public int getNumbaerOfToDone() {
        return numbaerOfToDone;
    }

    public void setNumbaerOfToDone(int numbaerOfToDone) {
        this.numbaerOfToDone = numbaerOfToDone;
    }

    public int getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(int progressStatus) {
        this.progressStatus = progressStatus;
    }

    public int getSynctype() {
        return synctype;
    }

    public void setSynctype(int synctype) {
        this.synctype = synctype;
    }

    public String getSyncResult() {
        return syncResult;
    }

    public void setSyncResult(String syncResult) {
        this.syncResult = syncResult;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }
}

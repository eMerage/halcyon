package com.halcyon.channelbridgeaddapters;

/**
 * Created by Himanshu on 8/3/2016.
 */
public class AvarageItem {

    public String pCode,pDescription,pAVG,cusID;
    int type;

    public AvarageItem(String pCode,String dis,String avg,String custemrID,int type) {
        this.pCode = pCode;
        this.pDescription=dis;
        this.pAVG=avg;
        this.cusID=custemrID;
        this.type=type;
    }
}

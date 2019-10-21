package com.halcyon.channelbridge;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableObject implements Parcelable {

    public static final Parcelable.Creator<ParcelableObject> CREATOR = new Parcelable.Creator<ParcelableObject>() {
        public ParcelableObject createFromParcel(Parcel in) {
            return new ParcelableObject(in);
        }

        public ParcelableObject[] newArray(int size) {
            return new ParcelableObject[size];
        }
    };
    private String string1;
    private String string2;
    private String string3;
    private String string4;
    private String string5;
    private String string6;
    private String string7;
    private String string8;
    private String string9;
    private String string10;
    private String string11;

    public ParcelableObject(Parcel source) {
        /*
		 * Reconstruct from the Parcel
		 */
        string1 = source.readString();
        string2 = source.readString();
        string3 = source.readString();
        string4 = source.readString();
        string5 = source.readString();
        string6 = source.readString();
        string7 = source.readString();
        string8 = source.readString();
        string9 = source.readString();
        string10 = source.readString();
        string11 = source.readString();


    }

    public ParcelableObject() {
        // TODO Auto-generated constructor stub
    }

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public String getString2() {
        return string2;
    }

    public void setString2(String string2) {
        this.string2 = string2;
    }

    public String getString3() {
        return string3;
    }

    public void setString3(String string3) {
        this.string3 = string3;
    }

    public String getString4() {
        return string4;
    }

    public void setString4(String string4) {
        this.string4 = string4;
    }

    public String getString5() {
        return string5;
    }

    public void setString5(String string5) {
        this.string5 = string5;
    }

    public String getString6() {
        return string6;
    }

    public void setString6(String string6) {
        this.string6 = string6;
    }

    public String getString7() {
        return string7;
    }

    public void setString7(String string7) {
        this.string7 = string7;
    }

    public String getString8() {
        return string8;
    }

    public void setString8(String string8) {
        this.string8 = string8;
    }

    public String getString9() {
        return string9;
    }

    public void setString9(String string9) {
        this.string9 = string9;
    }

    public String getString10() {
        return string10;
    }

    public void setString10(String string10) {
        this.string10 = string10;
    }

    public String getString11() {
        return string11;
    }

    public void setString11(String string11) {
        this.string11 = string11;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeString(string1);
        dest.writeString(string2);
        dest.writeString(string3);
        dest.writeString(string4);
        dest.writeString(string5);
        dest.writeString(string6);
        dest.writeString(string7);
        dest.writeString(string8);
        dest.writeString(string9);
        dest.writeString(string10);
        dest.writeString(string11);

    }
}

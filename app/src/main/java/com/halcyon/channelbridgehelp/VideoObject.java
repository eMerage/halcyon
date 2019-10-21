package com.halcyon.channelbridgehelp;

/**
 * Created by Hasitha on 4/29/15.
 */
public class VideoObject {


    private String text;
    private String id;
    public VideoObject() {
    }

    public VideoObject(String text, String id) {
        this.text = text;
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

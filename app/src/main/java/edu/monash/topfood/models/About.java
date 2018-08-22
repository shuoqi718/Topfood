package edu.monash.topfood.models;

import android.os.Parcelable;

public class About {
    private String title;
    private String source;

    public About(){

    }

    public About(String title, String source){
        this.title = title;
        this.source = source;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

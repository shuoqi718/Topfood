package edu.monash.topfood.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Notification implements Parcelable{
    private String time;
    private String title;
    private String content;

    protected Notification(Parcel in){
        time = in.readString();
        title = in.readString();
        content = in.readString();
    }

    public Notification(){

    }

    public Notification(String time, String title,String content){
        this.time = time;
        this.title = title;
        this.content = content;
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel parcel) {
            return new Notification(parcel);
        }

        @Override
        public Notification[] newArray(int i) {
            return new Notification[i];
        }
    };

    @Override
    public int describeContents(){ return 0;}

    @Override
    public void writeToParcel(Parcel parcel, int i){
        parcel.writeString(time);
        parcel.writeString(title);
        parcel.writeString(content);
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("time", time);
        result.put("title", title);
        result.put("content", content);
        return  result;
    }
}

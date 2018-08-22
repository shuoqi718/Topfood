package edu.monash.topfood.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Feedback implements Parcelable{

    private String time;
    private String content;
    protected Feedback(Parcel in){
        time = in.readString();
        content = in.readString();
    }

    public Feedback(){

    }

    public Feedback(String time, String content){
        this.time = time;
        this.content = content;
    }

    public static final Creator<Feedback> CREATOR = new Creator<Feedback>() {
        @Override
        public Feedback createFromParcel(Parcel parcel) {
            return new Feedback(parcel);
        }

        @Override
        public Feedback[] newArray(int i) {
            return new Feedback[i];
        }
    };

    @Override
    public int describeContents(){return 0;}

    @Override
    public void writeToParcel(Parcel parcel, int i){
        parcel.writeString(time);
        parcel.writeString(content);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("time", time);
        result.put("content", content);
        return result;
    }
}

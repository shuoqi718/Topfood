package edu.monash.topfood.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by You on 24/04/2018.
 */

public class User implements Parcelable {

    private String id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private ArrayList<Notification> notifications;


    public User(){

    }

    public User(String id,String name, String phone, String email, String address){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    protected User(Parcel in){
        id = in.readString();
        name = in.readString();
        phone = in.readString();
        email = in.readString();
        address = in.readString();
        notifications = in.readArrayList(null);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int i) {
            return new User[i];
        }
    };

    @Override
    public int describeContents(){ return 0;}

    @Override
    public void writeToParcel(Parcel parcel, int i){
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(email);
        parcel.writeString(address);
        parcel.writeList(notifications);
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("phone", phone);
        result.put("email", email);
        result.put("address", address);
        result.put("notifications",notifications);
        return  result;
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    public String getId(){ return id;}

    public void setId(String id){ this.id = id;}

    public String getName(){ return name;}

    public String getPhone(){ return phone;}

    public String getEmail(){ return email;}


    public void setName(String name){ this.name = name;}

    public void setPhone(String phone){ this.phone = phone;}

    public void setEmail(String email){ this.email = email;}


    public String getAddress(){ return address;}

    public void setAddress(String address){ this.address =address;}
}

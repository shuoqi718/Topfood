package edu.monash.topfood.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by You on 3/05/2018.
 */

public class Order implements Parcelable{

    private String uid;
    private String time;
    private String status;
    private String phone;
    private String address;
    private ArrayList<Food> foods;
    private String totalPrice;
    private String comments;

    public Order(){

    }

    public Order(String uid,String time, String status, String totalPrice, String comments,String phone, String address, ArrayList<Food> foods){
        this.uid = uid;
        this.time = time;
        this.status = status;
        this.totalPrice = totalPrice;
        this.phone = phone;
        this.address = address;
        this.foods = foods;
        this.comments = comments;
    }

    public Order(Parcel in){
        uid = in.readString();
        time = in.readString();
        phone = in.readString();
        address = in.readString();
        status = in.readString();
        totalPrice = in.readString();
        comments = in.readString();
        foods = in.readArrayList(null);
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel parcel) {
            return new Order(parcel);
        }

        @Override
        public Order[] newArray(int i) {
            return new Order[i];
        }
    };

    @Override
    public int describeContents(){ return 0;}

    @Override
    public void writeToParcel(Parcel parcel, int i){
        parcel.writeString(uid);
        parcel.writeString(time);
        parcel.writeString(status);
        parcel.writeString(totalPrice);
        parcel.writeString(phone);
        parcel.writeString(address);
        parcel.writeString(comments);
        parcel.writeList(foods);
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public ArrayList<Food> getFoods(){ return foods;}

    public void setFoods(ArrayList<Food> foodsAndNumber){ this.foods = foodsAndNumber;}

    public String getUid(){return uid;}

    public void setUid(String uid){ this.uid = uid;}

    public String getTime(){ return time;}

    public void setTime(String time){ this.time = time;}

    public String getStatus(){ return status;}

    public void setStatus(String status){this.status = status;}

    public String getTotalPrice(){ return totalPrice;}

    public void setTotalPrice(String totalPrice){ this.totalPrice = totalPrice;}

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid",uid);
        result.put("time", time);
        result.put("status", status);
        result.put("price", totalPrice);
        result.put("phone",phone);
        result.put("address",address);
        result.put("comments", comments);
        result.put("foods",foods);
        return  result;
    }

}

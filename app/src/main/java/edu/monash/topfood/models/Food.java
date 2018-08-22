package edu.monash.topfood.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by You on 30/04/2018.
 */

public class Food implements Parcelable{

    private String name;
    private String image;
    private String price;
    private String category;
    private String number;

    public Food(){

    }

    protected Food(Parcel in){
//        _id = in.readLong();
        category = in.readString();
        name = in.readString();
        image = in.readString();
        price = in.readString();
        number = in.readString();
    }

    public Food(String name, String price, String category){
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = "";
        this.number = "0";
    }


    public Food(String name, String price, String category, String image, String number){
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
        this.number = number;
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel parcel) {
            return new Food(parcel);
        }

        @Override
        public Food[] newArray(int i) {
            return new Food[i];
        }
    };

    @Override
    public int describeContents(){ return 0;}

    @Override
    public void writeToParcel(Parcel parcel, int i){
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeString(price);
        parcel.writeString(number);
        parcel.writeString(category);
    }


    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("image", image);
        result.put("price", price);
        result.put("number",number);
        result.put("category",category);
        return  result;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName(){ return name;}
    public void setName(String name){ this.name = name;}
    public String getImage(){ return image;}
    public void setImage(String image){ this.image = image;}
    public String getPrice(){ return price;}
    public void setPrice(String price){ this.price = price;}
}

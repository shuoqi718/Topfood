package edu.monash.topfood.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import edu.monash.topfood.MainActivity;

/**
 * Created by You on 24/04/2018.
 */

public class TopFoodDatabase {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRefUser;
    private DatabaseReference mDatabaseRefFood;
    private DatabaseReference mDatabaseRefOrder;
    private DatabaseReference mDatabaseRefNote;
    private DatabaseReference mDatabaseRefFeed;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mListener;

    public TopFoodDatabase(){
        mDatabase = FirebaseDatabase.getInstance();
//        mDatabase.setPersistenceEnabled(true);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRefUser = mDatabase.getReference().child("Users");
        mDatabaseRefFood = mDatabase.getReference().child("Foods");
        mDatabaseRefOrder = mDatabase.getReference().child("Orders");
        mDatabaseRefNote = mDatabase.getReference().child("Note");
        mDatabaseRefFeed = mDatabase.getReference().child("Feedback");
    }

    public DatabaseReference getmDatabaseRefUser(){ return mDatabaseRefUser;}

    public DatabaseReference getmDatabaseRefFood(){ return mDatabaseRefFood;}

    public DatabaseReference getmDatabaseRefOrder(){ return mDatabaseRefOrder;}

    public DatabaseReference getmDatabaseRefNote() {
        return mDatabaseRefNote;
    }

    public DatabaseReference getmDatabaseRefFeed() {
        return mDatabaseRefFeed;
    }

    public void addUser(User user){
        mDatabaseRefUser.child(user.getId()).setValue(user);
    }

    public void updateUser(User user){
        mDatabaseRefUser.child(user.getId()).updateChildren(user.toMap());
    }

    public void delUser(String uid){
        mDatabaseRefUser.child(uid).removeValue();
    }

    public void addFood(String category,Food food){ mDatabaseRefFood.child(category).child(food.getName()).setValue(food);}

    public void updateFood(String category, Food food){
        mDatabaseRefFood.child(category).child(food.getName()).updateChildren(food.toMap());
    }

    public void addOrder(Order order){ mDatabaseRefOrder.child(order.getUid()).child(order.getTime()).setValue(order);}

    public void updateOrder(Order order){
        mDatabaseRefOrder.child(order.getUid()).child(order.getTime()).updateChildren(order.toMap());
    }

    public void addNote(Notification notification){ mDatabaseRefNote.child(notification.getTime()).setValue(notification);}

    public void updateNote(Notification notification){
        mDatabaseRefNote.child(notification.getTime()).updateChildren(notification.toMap());
    }
    public void delNote(Notification notification){
        mDatabaseRefNote.child(notification.getTime()).removeValue();
    }

    public void addFeed(String uid, Feedback feedback){
        mDatabaseRefFeed.child(uid).child(feedback.getTime()).setValue(feedback);
    }

    public void delFeed(String uid, Feedback feedback){
        mDatabaseRefFeed.child(uid).child(feedback.getTime()).removeValue();
    }

}

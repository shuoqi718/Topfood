package edu.monash.topfood;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import edu.monash.topfood.maps.LocatorFragment;
import edu.monash.topfood.models.Notification;
import edu.monash.topfood.models.TopFoodDatabase;
import edu.monash.topfood.models.User;
import edu.monash.topfood.order.OrderFragment;
import edu.monash.topfood.order.PaymentFragment;


public class HomepageFragment extends Fragment implements View.OnClickListener{


    private Button orderButton;
    private Button locatorButton;
    private FirebaseAuth mAuth;


    public HomepageFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);



        mAuth = FirebaseAuth.getInstance();
        orderButton = (Button)view.findViewById(R.id.homepage_make_order_button);
        locatorButton = (Button)view.findViewById(R.id.homepage_locate_button);

        PaymentFragment.cleanData();
        orderButton.setOnClickListener(this);
        locatorButton.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.homepage_make_order_button:
                if(mAuth.getCurrentUser() == null){
                    Intent newIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(newIntent);
                }
                else {
                    Fragment fragment = new OrderFragment();
                    FragmentTransaction mFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                    mFragmentTransaction.replace(R.id.main_frame,fragment);
                    mFragmentTransaction.addToBackStack(null);
                    mFragmentTransaction.commit();
                    break;
                }

            case R.id.homepage_locate_button:
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION);
                if(permissionCheck != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
                    return;
                }
                Fragment fragment = new LocatorFragment();
                FragmentTransaction mFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                mFragmentTransaction.replace(R.id.main_frame,fragment);
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();
                break;
        }
    }

}

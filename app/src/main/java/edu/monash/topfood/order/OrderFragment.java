package edu.monash.topfood.order;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import edu.monash.topfood.R;
import edu.monash.topfood.models.Food;
import edu.monash.topfood.models.MainAdapter;
import edu.monash.topfood.models.TopFoodDatabase;



/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "OrderFragment";

    private static final String IMAGE_DIRECTORY = "images";

    public static final ArrayList<Food> foods = new ArrayList<>();

    private ImageButton coldButton;
    private ImageButton mainButton;
    private ImageButton dessertButton;
    private ImageButton drinkButton;
//    private Button initializeButton;
    private Button finishButton;
    private Bitmap bitmap;
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private TextView totalView;
    private FirebaseStorage mStorage;
    private TopFoodDatabase mDatabase;
    private StorageReference mStorageRef;
    private ProgressDialog mProgressDialog;
    FragmentListener mCallback;

    public OrderFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        coldButton = (ImageButton)view.findViewById(R.id.main_order_cold_button);
        mainButton = (ImageButton)view.findViewById(R.id.main_order_main_button);
        dessertButton = (ImageButton)view.findViewById(R.id.main_order_dessert_button);
        drinkButton = (ImageButton)view.findViewById(R.id.main_order_drink_button);
        finishButton = (Button)view.findViewById(R.id.main_order_finish_button);
        totalView = (TextView)view.findViewById(R.id.main_order_total);
//        initializeButton = (Button)view.findViewById(R.id.order_initialize_button);


        mStorage = FirebaseStorage.getInstance();

        initializeImage();

        mDatabase = new TopFoodDatabase();

        updateTotal();

        coldButton.setOnClickListener(this);
        mainButton.setOnClickListener(this);
        dessertButton.setOnClickListener(this);
        drinkButton.setOnClickListener(this);
        finishButton.setOnClickListener(this);
    }

    /**
     * Initialize images, if files have already downloaded, it will load from internal storage.
     * Otherwise, it will download from database.
     */
    private void initializeImage(){
        mStorageRef = mStorage.getReference().child("images").child("menu");

        String[] menu = {"main", "cold", "dessert", "drinks"};
        for( String str: menu){
            if(localFileExist("menu", str)){
                Bitmap bitmap = loadFile("menu", str);
                if(bitmap != null){
                    bitmaps.add(bitmap);
                }
            }
        }
        if(bitmaps.size() != 0){
            mainButton.setImageBitmap(bitmaps.get(0));
            coldButton.setImageBitmap(bitmaps.get(1));
            dessertButton.setImageBitmap(bitmaps.get(2));
            drinkButton.setImageBitmap(bitmaps.get(3));
        }
        else {
            downloadImage();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Processing");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Update total price and display at the fragment.
     */
    private void updateTotal(){
        if(foods.isEmpty()){
            showProgressDialog();
            ValueEventListener foodListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot1: dataSnapshot.getChildren()){
                        for(DataSnapshot snapshot2: snapshot1.getChildren()){
                            foods.add(snapshot2.getValue(Food.class));
                        }
                    }
                    hideProgressDialog();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read", databaseError.toException());
                }
            };
            mDatabase.getmDatabaseRefFood().addValueEventListener(foodListener);
        }
        double total = 0;
        double price;
        int number;
        for(Food food: foods){
            price = Double.parseDouble(food.getPrice());
            number = Integer.parseInt(food.getNumber());
            total += price *  number;
        }
        totalView.setText(Double.toString(total));
    }


    /**
     * Download images from database
     */
    private void downloadImage(){
        final long ONE_MEGABYTE = Long.MAX_VALUE;
        mStorageRef.child("main.jpg").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                saveFile("menu","main",bytes);
                mainButton.setImageBitmap(bitmap);
            }
        });

        mStorageRef.child("cold.jpg").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                saveFile("menu","cold",bytes);
                coldButton.setImageBitmap(bitmap);
            }
        });
        mStorageRef.child("dessert.jpg").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                saveFile("menu","dessert",bytes);
                dessertButton.setImageBitmap(bitmap);
            }
        });
        mStorageRef.child("drinks.jpg").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                saveFile("menu","drinks",bytes);
                drinkButton.setImageBitmap(bitmap);
            }
        });
    }

    /**
     * Validate if file exists or not
     * @param directory
     * @param fileName
     * @return
     */
    private boolean localFileExist(String directory, String fileName){
        boolean mLocalFileExist = false;

        try {
            File localFile = new File(getActivity().getApplicationContext().getDir(IMAGE_DIRECTORY +
                directory, 0), fileName + ".jpg");
            mLocalFileExist = localFile.exists();
        }catch (Exception e){
            Log.e("ORDER FRAGMENT", e.getLocalizedMessage());
        }
        return mLocalFileExist;
    }


    /**
     * Save file in the phone at Specific directory with specific filename
     * @param directory
     * @param fileName
     * @param imageBytes
     * @return
     */
    private boolean saveFile(String directory, String fileName, byte[] imageBytes){
        boolean didSave = false;

        try {
            File outputFile = new File(getActivity().getApplicationContext().getDir(IMAGE_DIRECTORY +
            directory,0), fileName + ".jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            fileOutputStream.write(imageBytes);
            fileOutputStream.close();
            didSave = true;
        }
        catch (Exception e){
            Log.e("ORDER FRAGMENT", e.getLocalizedMessage());
        }
        return didSave;
    }

    /**
     * Load file from phone in specific directory
     * @param directory
     * @param fileName
     * @return
     */
    private Bitmap loadFile(String directory, String fileName){
        Bitmap bitmap = null;

        try {
            File inputFile = new File(getActivity().getApplicationContext().getDir(IMAGE_DIRECTORY +
            directory, 0), fileName + ".jpg");
            InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
        }catch (Exception e){
            Log.e("ORDER FRAGMENT", e.getLocalizedMessage());
        }
        return bitmap;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            mCallback = (FragmentListener)activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() +
            "must implement FragmentListener");
        }
    }


    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.main_order_main_button:
                mCallback.orderAdapterSelected("Main");
                break;

            case R.id.main_order_cold_button:
                mCallback.orderAdapterSelected("Cold");
                break;
            case R.id.main_order_dessert_button:
                mCallback.orderAdapterSelected("Dessert");
                break;
            case R.id.main_order_drink_button:
                mCallback.orderAdapterSelected("Drink");
                break;
            case R.id.main_order_finish_button:
                if(Double.parseDouble(totalView.getText().toString())!=0)
                {
                    Fragment fragment = new OrderConfirmFragment();
                    FragmentTransaction mFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                    mFragmentTransaction.replace(R.id.main_frame,fragment);
                    mFragmentTransaction.addToBackStack(null);
                    mFragmentTransaction.commit();
                }
                else {
                    Toast.makeText(getContext(),"Please order first", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public interface FragmentListener{
        void orderAdapterSelected(String adapter);
    }

}

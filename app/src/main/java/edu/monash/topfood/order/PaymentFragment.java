package edu.monash.topfood.order;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.sql.BatchUpdateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import edu.monash.topfood.HomepageFragment;
import edu.monash.topfood.R;
import edu.monash.topfood.models.Food;
import edu.monash.topfood.models.MainAdapter;
import edu.monash.topfood.models.Order;
import edu.monash.topfood.models.TopFoodDatabase;
import edu.monash.topfood.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "PaymentFragment";

    private Spinner timeSpinner;
    private ArrayList<String> timeList;
    private TextView phoneTextView;
    private TextView addressTextView;
    private TextView commentTextView;
    private Button proceedButton;
    private Button cancelButton;
    private ArrayList<Food> foodList;
    private TextView totalPriceTextView;
    private FirebaseAuth mAuth;
    private TopFoodDatabase mTopFoodDatabase;


    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        phoneTextView = (TextView)view.findViewById(R.id.payment_phone_text_view);
        addressTextView = (TextView)view.findViewById(R.id.payment_address_text_view);
        commentTextView = (TextView)view.findViewById(R.id.payment_comment_text_view);
        proceedButton = (Button)view.findViewById(R.id.payment_proceed_button);
        cancelButton = (Button)view.findViewById(R.id.payment_cancel_button);
        totalPriceTextView = (TextView) view.findViewById(R.id.payment_total_price_text_view);
        timeList = new ArrayList<>();
        timeSpinner = (Spinner)view.findViewById(R.id.payment_spinner);
        mTopFoodDatabase = new TopFoodDatabase();
        initialiseTime();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, timeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);

        updateData();

        proceedButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        return view;
    }

    /**
     * Set default phone number and address to the page
     */
    private void updateData(){
        final String uid = mAuth.getCurrentUser().getUid();
        mTopFoodDatabase.getmDatabaseRefUser().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User newUser = dataSnapshot.getValue(User.class);
                phoneTextView.setText(newUser.getPhone());
                addressTextView.setText(newUser.getAddress());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG,"Failed",databaseError.toException());
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            foodList = args.getParcelableArrayList("foods");
            double total = 0;
            for(Food food: foodList){
                double price = Double.parseDouble(food.getPrice());
                int number = Integer.parseInt(food.getNumber());
                total += price * number;
            }
            totalPriceTextView.setText(Double.toString(total));
        }
    }

    @Override
    public void onClick(View view){
        Fragment fragment = null;
        if(view.getId() == R.id.payment_proceed_button){
            if(validForm()){
                completeOrder();
                fragment = new HomepageFragment();
                FragmentTransaction mFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                mFragmentTransaction.replace(R.id.main_frame,fragment);
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();
            }
        }
        else {
            fragment = new OrderFragment();
            FragmentTransaction mFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
            mFragmentTransaction.replace(R.id.main_frame,fragment);
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.commit();
        }
    }

    /**
     * Generate an order and upload to database
     */
    private void completeOrder(){
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.setTimeZone(TimeZone.getTimeZone("Australia/Victoria"));
        Date date = currentDate.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String time = simpleDateFormat.format(date);
        String uid = mAuth.getCurrentUser().getUid();
        String status = "on the way";
        String phone = phoneTextView.getText().toString();
        String address = addressTextView.getText().toString();
        String comments = commentTextView.getText().toString();
        String totalPrice = totalPriceTextView.getText().toString();
        Order order = new Order(uid, time, status, totalPrice, comments,phone, address, foodList);
        mTopFoodDatabase.addOrder(order);
        cleanData();
        Toast.makeText(getContext(),"Successfully making order!",Toast.LENGTH_SHORT).show();
    }

    private boolean validForm(){
        boolean valid = true;
        if(TextUtils.isEmpty(phoneTextView.getText().toString().trim())){
            phoneTextView.setError("Required");
            valid = false;
        }
        if (TextUtils.isEmpty(addressTextView.getText().toString().trim())) {
            addressTextView.setError("Required");
            valid = false;
        }
        return valid;
    }

    /**
     * Set all foods to the original number
     */
    public static void cleanData(){
        for(Food food: OrderFragment.foods){
            food.setNumber("0");
        }
    }

    private void initialiseTime(){
        timeList.add("Immediately");
        timeList.add("30 minutes later");
        timeList.add("1 hour later");
        timeList.add("1.5 hour later");
        timeList.add("2 hours later");
        timeList.add("3 hours later");
        timeList.add("4 hours later");
    }

}

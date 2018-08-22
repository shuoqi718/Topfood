package edu.monash.topfood.order;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import edu.monash.topfood.R;
import edu.monash.topfood.models.Food;
import edu.monash.topfood.models.Order;
import edu.monash.topfood.models.OrderAdapter;
import edu.monash.topfood.models.OrderComparator;
import edu.monash.topfood.models.TopFoodDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements AdapterView.OnItemClickListener{

    private static final String TAG = "HistoryFragment";

    private ListView mListView;
    private OrderAdapter mAdapter;
    private ArrayList<Order> mOrderList;
    private FirebaseAuth mAuth;
    private TopFoodDatabase mDatabase;
    OrderDetailListener mCallback;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            mCallback = (OrderDetailListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() +
                    "must implement OrderDetailListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        // Inflate the layout for this fragment

        mOrderList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = new TopFoodDatabase();

        mListView = (ListView)view.findViewById(R.id.history_list);
        mAdapter = new OrderAdapter(getActivity(), mOrderList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);

        readData();
        return view;
    }

    /**
     * When time has passed 60 minutes, system will set order being completed and updated in the database
     * @param order
     */
    private void freshOrder(Order order){
        GregorianCalendar currentTime = new GregorianCalendar();
        currentTime.setTimeZone(TimeZone.getTimeZone("Australia/Victoria"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        try{
            Date orderDate = simpleDateFormat.parse(order.getTime());
            GregorianCalendar orderTime = new GregorianCalendar();
            orderTime.setTime(orderDate);
            orderTime.add(Calendar.MINUTE, 60);
            if(currentTime.compareTo(orderTime) > 0){
                order.setStatus("Complete");
                mDatabase.updateOrder(order);
            }
        }catch (Exception e){
            Log.d("Transfer failed", e.getMessage());
        }
    }

    /**
     * Download all user's orders from database and display in the list view.
     */
    private void readData(){
        String uid = mAuth.getCurrentUser().getUid();
        mDatabase.getmDatabaseRefOrder().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot orderSnapshot: dataSnapshot.getChildren()){
                    Order order = orderSnapshot.getValue(Order.class);
                    freshOrder(order);
                    mOrderList.add(order);
                }
                Collections.sort(mOrderList,new OrderComparator());
                mAdapter = new OrderAdapter(getActivity(),mOrderList);
                mListView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Order order = (Order)mAdapter.getItem(position);
        mCallback.orderDetailSelected(order);
    }


    /**
     * Transfer the order clicked to the OrderDetailFragment.
     */
    public interface OrderDetailListener{
        void orderDetailSelected(Order order);
    }
}

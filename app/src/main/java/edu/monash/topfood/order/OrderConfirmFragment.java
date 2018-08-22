package edu.monash.topfood.order;


import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.monash.topfood.R;
import edu.monash.topfood.models.Food;
import edu.monash.topfood.models.MainAdapter;
import edu.monash.topfood.models.OrderConfirmAdapter;
import edu.monash.topfood.models.TopFoodDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderConfirmFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "OrderConfirmFragment";

    private ListView mListView;
    private Button cancelButton;
    private Button placeOrderButton;
    private ArrayList<Food> mFoodList;
    private OrderConfirmAdapter mAdapter;
    private TopFoodDatabase mDatabase;
    private ArrayList<Food> updatedFoodList;
    OrderEditListener mCallback;

    public OrderConfirmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_confirm, container, false);
        mListView = (ListView)view.findViewById(R.id.order_confirm_list_view);
        cancelButton = (Button) view.findViewById(R.id.order_confirm_cancel_button);
        placeOrderButton = (Button)view.findViewById(R.id.order_confirm_place_button);

        mFoodList = new ArrayList<>();
        mDatabase = new TopFoodDatabase();
        updatedFoodList = new ArrayList<>();
        updateFood();

        cancelButton.setOnClickListener(this);
        placeOrderButton.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }


    /**
     * Display all foods user has ordered before
     */
    private void updateFood(){
        ArrayList<Food> foods = new ArrayList<>();
        for(Food food: OrderFragment.foods){
            if(!food.getNumber().equals("0"))
                foods.add(food);
        }
        mAdapter = new OrderConfirmAdapter(getActivity(),foods);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.order_confirm_place_button:
                updateData();
                if(updatedFoodList.isEmpty()){
                    PaymentFragment.cleanData();
                    Toast.makeText(getContext(),"Please order something.",Toast.LENGTH_SHORT).show();
                    Fragment fragment = new OrderFragment();
                    FragmentTransaction mFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                    mFragmentTransaction.replace(R.id.main_frame,fragment);
                    mFragmentTransaction.addToBackStack(null);
                    mFragmentTransaction.commit();
                }
                else
                    mCallback.orderSelected(updatedFoodList);
                break;
            case R.id.order_confirm_cancel_button:
                Fragment fragment = new OrderFragment();
                FragmentTransaction mFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                mFragmentTransaction.replace(R.id.main_frame,fragment);
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();
                break;
        }


    }

    /**
     * Update all foods after user editing.
     */
    private void updateData(){
        for(int i=0; i<mAdapter.getCount();i++){
            Food food = (Food) mAdapter.getItem(i);
            updatedFoodList.add(food);
        }
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            mCallback = (OrderEditListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() +
                    "must implement FragmentListener");
        }
    }

    /**
     * Transfer food list to the payment fragment.
     */
    public interface OrderEditListener{
        void orderSelected(ArrayList<Food> foods);
    }
}

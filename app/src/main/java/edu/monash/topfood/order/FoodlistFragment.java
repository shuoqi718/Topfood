package edu.monash.topfood.order;


import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.monash.topfood.R;
import edu.monash.topfood.models.Food;
import edu.monash.topfood.models.MainAdapter;
import edu.monash.topfood.models.TopFoodDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoodlistFragment extends Fragment implements SearchView.OnQueryTextListener,View.OnClickListener{

    private static final String TAG = "FoodlistFragment";

    private ListView mListView;
    private MainAdapter mAdapter;
    private ArrayList<Food> mFoodList;
    private SearchView mSearchView;
    private String adapter;
    private TopFoodDatabase mDatabase;
    private Button addButton;
    private Button cancelButton;
//    OrderListener mCallback;

    public FoodlistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_list, container, false);
        setHasOptionsMenu(true);

        mFoodList = new ArrayList<>();

        addButton = (Button)view.findViewById(R.id.food_list_add_button);
        cancelButton = (Button)view.findViewById(R.id.food_list_cancel_button);


        addButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }


    /**
     * Get which category should be displayed
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mDatabase = new TopFoodDatabase();
        Bundle args = getArguments();
        if(args != null) {
            adapter = args.getString("adapter");
            for(Food food: OrderFragment.foods){
                if(food.getCategory().equals(adapter))
                    mFoodList.add(new Food(food.getName(), food.getPrice(), food.getCategory(), food.getImage(), food.getNumber()));
            }
            View view = getView();
            mListView = view.findViewById(R.id.foodlist);
            mAdapter = new MainAdapter(getActivity(), mFoodList);
            mListView.setAdapter(mAdapter);
        }
    }


    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.food_list_add_button:
                getAllValues();
                break;
            case R.id.food_list_cancel_button:

                break;
        }
        Fragment fragment = new OrderFragment();
        FragmentTransaction mFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
        mFragmentTransaction.replace(R.id.main_frame,fragment);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.homepage, menu);

        MenuItem search = menu.findItem(R.id.list_food_search_menu);
        search.setVisible(true);
        SearchManager manager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.list_food_search_menu);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query){ return false;}

    @Override
    public boolean onQueryTextChange(String newText){
        mAdapter.getFilter().filter(newText);
        return true;
    }




    /**
     * Update all foods with number
     */
    private void getAllValues(){
        for(int i=0; i < mAdapter.getCount(); i++){
            Food food = (Food)mAdapter.getItem(i);
            for(Food fo: OrderFragment.foods){
                if(fo.getName().equals(food.getName())){
                    fo.setNumber(food.getNumber());
                }
            }
        }
    }



}

package edu.monash.topfood.account;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.monash.topfood.R;
import edu.monash.topfood.models.TopFoodDatabase;
import edu.monash.topfood.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetAddressFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "SetAddressFragment";
    private TextView streetText;
    private TextView suburbText;
    private Spinner stateSpinner;
    private ArrayList<String> mStates;
    private Button changeButton;
    private Button cancelButton;
    private FirebaseAuth mAuth;
    private TopFoodDatabase mTopFoodDatabase;
    private TextView mCurrentAddress;

    public SetAddressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_address, container, false);
        mTopFoodDatabase = new TopFoodDatabase();

        streetText = (TextView)view.findViewById(R.id.set_address_street_text_view);
        suburbText = (TextView)view.findViewById(R.id.set_address_suburb_text_view);
        stateSpinner = (Spinner) view.findViewById(R.id.set_address_state_spinner);
        mCurrentAddress = (TextView)view.findViewById(R.id.set_address_current_address_text_view);

        mAuth = FirebaseAuth.getInstance();

        displayAddress();

        initialiseStateArray();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mStates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        stateSpinner.setAdapter(adapter);

        changeButton = (Button)view.findViewById(R.id.set_address_change_button);
        cancelButton = (Button)view.findViewById(R.id.set_address_cancel_button);
        changeButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }


    /**
     * Display default address at top of page
     */
    private void displayAddress(){
        final String uid = mAuth.getCurrentUser().getUid();
        mTopFoodDatabase.getmDatabaseRefUser().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(!user.getAddress().equals("")){
                    mCurrentAddress.setText(user.getAddress());
                }
                else {
                    mCurrentAddress.setText("Not set yet");
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG,"Failed",databaseError.toException());
            }
        });
    }

    /**
     * Initialize spinner states
     */
    private void initialiseStateArray(){
        mStates = new ArrayList<>();
        mStates.add("Victoria");
        mStates.add("New South Wales");
        mStates.add("Queensland");
        mStates.add("South Australia");
        mStates.add("Tasmania");
        mStates.add("Western Australia");
    }

    /**
     * Validate form
     * @return
     */
    private boolean validForm(){
        boolean valid = true;
        if(TextUtils.isEmpty(streetText.getText().toString().trim())){
            streetText.setError("Required");
            valid = false;
        }
        if (TextUtils.isEmpty(suburbText.getText().toString().trim())) {
            suburbText.setError("Required");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.set_address_change_button:
                if(validForm()){
                    updateAddress();
                }
                else
                    return;
                break;
            case R.id.set_address_cancel_button:
                break;
        }
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
        ft.replace(R.id.main_frame, new AccountFragment());
        ft.addToBackStack(null);
        ft.commit();
    }


    /**
     * Update default address for user
     */
    private void updateAddress(){
        final String address = streetText.getText().toString().trim() + "," + suburbText.getText().toString().trim() + "," + stateSpinner.getSelectedItem().toString();
        final String uid = mAuth.getCurrentUser().getUid();
        mTopFoodDatabase.getmDatabaseRefUser().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User newUser = dataSnapshot.getValue(User.class);
                newUser.setAddress(address);
                mTopFoodDatabase.updateUser(newUser);
                Toast.makeText(getContext(),"Update successful!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG,"Failed",databaseError.toException());
            }
        });
    }

}

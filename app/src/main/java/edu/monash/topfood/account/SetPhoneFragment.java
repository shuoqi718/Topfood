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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import edu.monash.topfood.R;
import edu.monash.topfood.models.TopFoodDatabase;
import edu.monash.topfood.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetPhoneFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "SetPhoneFragment";

    private TextView currentPhoneTextView;
    private TextView newPhoneTextView;
    private Button cancelButton;
    private Button changeButton;
    private TopFoodDatabase mDatabase;
    private FirebaseAuth mAuth;

    public SetPhoneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_phone, container, false);
        // Inflate the layout for this fragment
        currentPhoneTextView = (TextView)view.findViewById(R.id.set_phone_current_phone_text_view);
        newPhoneTextView = (TextView)view.findViewById(R.id.set_phone_new_phone_text_view);
        cancelButton = (Button) view.findViewById(R.id.set_phone_cancel_button);
        changeButton = (Button)view.findViewById(R.id.set_phone_change_button);

        mDatabase = new TopFoodDatabase();
        mAuth = FirebaseAuth.getInstance();

        displayPhone();

        cancelButton.setOnClickListener(this);
        changeButton.setOnClickListener(this);
        return view;
    }

    /**
     * Display default phone number to the top of page
     */
    private void displayPhone(){
        final String uid = mAuth.getCurrentUser().getUid();
        mDatabase.getmDatabaseRefUser().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                currentPhoneTextView.setText(user.getPhone());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG,"Failed",databaseError.toException());
            }
        });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.set_phone_change_button:
                if(!validForm())
                    return;
                changePhone();
                Toast.makeText(getContext(),"Phone number changed successfully!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.set_phone_cancel_button:
                break;
        }
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
        ft.replace(R.id.main_frame, new AccountFragment());
        ft.addToBackStack(null);
        ft.commit();
    }

    private boolean validForm(){
        boolean valid = true;
        if(TextUtils.isEmpty(newPhoneTextView.getText().toString().trim())){
            newPhoneTextView.setError("Required");
            valid = false;
        }
        return valid;
    }

    /**
     * Update phone number to the database
     */
    private void changePhone(){
        final String phone = newPhoneTextView.getText().toString();
        String uid = mAuth.getCurrentUser().getUid();
        mDatabase.getmDatabaseRefUser().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User newUser = dataSnapshot.getValue(User.class);
                newUser.setPhone(phone);
                mDatabase.updateUser(newUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG,"Failed",databaseError.toException());
            }
        });
    }

}

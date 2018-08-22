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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.monash.topfood.R;
import edu.monash.topfood.models.TopFoodDatabase;
import edu.monash.topfood.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetNameFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "SetNameFragment";

    private FirebaseAuth mAuth;
    private TopFoodDatabase mTopFoodDatabase;
    private Button changeButton;
    private Button cancelButton;
    private TextView currentName;
    private TextView newName;
    private DatabaseReference mDatabase;

    public SetNameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_name, container, false);

        mAuth = FirebaseAuth.getInstance();

        mTopFoodDatabase = new TopFoodDatabase();
        changeButton = (Button)view.findViewById(R.id.set_name_change_button);
        cancelButton = (Button)view.findViewById(R.id.set_name_cancel_button);
        currentName = (TextView)view.findViewById(R.id.set_name_current_name_text_view);
        newName = (TextView)view.findViewById(R.id.set_phone_new_phone_text_view);


        displayName();



        changeButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Display defaul name at top of the page
     */
    private void displayName(){
        final String uid = mAuth.getCurrentUser().getUid();
        mTopFoodDatabase.getmDatabaseRefUser().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                currentName.setText(user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG,"Failed",databaseError.toException());
            }
        });
    }

    private boolean validForm(){
        boolean valid = true;
        String name = newName.getText().toString();
        if(TextUtils.isEmpty(name.trim())){
            valid = false;
            newName.setError("Required");
        }
        return valid;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.set_name_change_button:
                if(validForm()){
                    updateName();
                }else
                    return;
                break;
            case R.id.set_name_cancel_button:

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
     * Update new name to the database
     */
    private void updateName(){
        final String name = newName.getText().toString();
        final String uid = mAuth.getCurrentUser().getUid();
        mTopFoodDatabase.getmDatabaseRefUser().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User newUser = dataSnapshot.getValue(User.class);
                newUser.setName(name);
                mTopFoodDatabase.updateUser(newUser);
                Toast.makeText(getActivity().getApplicationContext(),"Update successful!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG,"Failed",databaseError.toException());
            }
        });
    }
}

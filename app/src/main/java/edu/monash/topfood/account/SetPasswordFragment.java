package edu.monash.topfood.account;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeoutException;

import edu.monash.topfood.R;
import edu.monash.topfood.models.TopFoodDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetPasswordFragment extends Fragment implements View.OnClickListener{

    private TextView newPassword;
    private TextView confirmPassword;
    private Button changeButton;
    private Button cancelButton;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    public SetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_set_password, container, false);
        // Inflate the layout for this fragment

        mAuth = FirebaseAuth.getInstance();

        newPassword = (TextView)view.findViewById(R.id.set_password_new_password_text_view);
        confirmPassword = (TextView)view.findViewById(R.id.set_password_confirm_password_text_view);

        changeButton = (Button)view.findViewById(R.id.set_password_change_button);
        cancelButton = (Button)view.findViewById(R.id.set_password_cancel_button);
        changeButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.set_password_change_button:
                if(!validForm())
                    return;
                showProgressDialog();
                updatePassword();
                break;
            case R.id.set_password_cancel_button:
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
     * Update password to the authentication system
     */
    private void updatePassword(){
        FirebaseUser user = mAuth.getCurrentUser();
        String password = newPassword.getText().toString();

        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(),"Password update successfully", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
                else {
                    Toast.makeText(getContext(),"Password update failed!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validForm(){
        boolean valid = true;
        if(TextUtils.isEmpty(newPassword.getText().toString())){
            newPassword.setError("Required");
            valid = false;
        }
        if(newPassword.getText().toString().length() < 6){
            newPassword.setError("At least 6 characters");
            valid = false;
        }
        if(TextUtils.isEmpty(confirmPassword.getText().toString())){
            confirmPassword.setError("Required");
            valid = false;
        }
        if(!newPassword.getText().toString().equals(confirmPassword.getText().toString())){
            valid = false;
            Toast.makeText(getContext(),"Two password is not the same", Toast.LENGTH_SHORT).show();
        }
        return valid;
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

}

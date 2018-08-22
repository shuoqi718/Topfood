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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.monash.topfood.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetPasswordReAuthFragment extends Fragment implements View.OnClickListener{

    private TextView oldPassword;
    private Button nextButton;
    private Button cancelButton;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    public SetPasswordReAuthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_set_password_re_auth, container, false);
        // Inflate the layout for this fragment

        mAuth = FirebaseAuth.getInstance();

        oldPassword = (TextView)view.findViewById(R.id.re_auth_password_text_view);

        nextButton = (Button)view.findViewById(R.id.re_auth_next_button);
        cancelButton = (Button)view.findViewById(R.id.re_auth_cancel_button);
        nextButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        return view;
    }


    private boolean validForm(){
        boolean valid = true;
        String password = oldPassword.getText().toString();
        if(TextUtils.isEmpty(password)){
            oldPassword.setError("Required");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onClick(View view){

        switch (view.getId()){
            case R.id.re_auth_next_button:
                if(!validForm())
                    return;
                validPassword();
                break;
            case R.id.re_auth_cancel_button:
                FragmentManager fm1 = getActivity().getSupportFragmentManager();
                FragmentTransaction ft1 = fm1.beginTransaction();
                ft1.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                ft1.replace(R.id.main_frame, new AccountFragment());
                ft1.addToBackStack(null);
                ft1.commit();
                break;
        }
    }

    /**
     * Check the password, if it is correct, jump to new password setting page
     */
    private void validPassword(){
        FirebaseUser user = mAuth.getCurrentUser();
        String password = oldPassword.getText().toString();
        if(user != null){
            showProgressDialog();
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                        ft.replace(R.id.main_frame, new SetPasswordFragment());
                        ft.addToBackStack(null);
                        hideProgressDialog();
                        ft.commit();
                    }
                    else {
                        Toast.makeText(getContext(),"Wrong password!",Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                }
            });
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


}

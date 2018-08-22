package edu.monash.topfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mEmail;
    private TextView mPassword;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private Button registerButton;
//    private Button forgetButton;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mEmail = (TextView)findViewById(R.id.login_email_edit_text);
        mPassword = (TextView)findViewById(R.id.login_password_edit_text);
        loginButton = (Button)findViewById(R.id.login_login_button);
        registerButton = (Button)findViewById(R.id.login_register_button);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

    }

    /**
     * Jump the homepage fragment
     * @param user
     */
    private void updateUI(FirebaseUser user){
        hideProgressDialog();
        Intent newIntent = new Intent(this, HomepageActivity.class);
        startActivity(newIntent);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Processing");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    /**
     * Login to the system
     * @param email
     * @param password
     */
    private void login(String email, String password){
        if(!validateForm()){
            return;
        }

        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Email or password is wrong!", Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }
                });
    }


    private boolean validateForm(){
        boolean valid = true;

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            mEmail.setError("Required.");
            valid = false;
        }
        else {
            mEmail.setError(null);
        }
        if(TextUtils.isEmpty(password)){
            mPassword.setError("Required.");
            valid = false;
        }
        else {
            mPassword.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login_login_button:
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString();
                login(email, password);
                break;
            case R.id.login_register_button:
                Intent newIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(newIntent);
                break;
        }

    }
}

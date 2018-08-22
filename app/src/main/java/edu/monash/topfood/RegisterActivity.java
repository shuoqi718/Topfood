package edu.monash.topfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

import edu.monash.topfood.R;
import edu.monash.topfood.models.Notification;
import edu.monash.topfood.models.TopFoodDatabase;
import edu.monash.topfood.models.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";


    private TextView mName;
    private TextView mPhone;
    private TextView mEmail;
    private TextView mPassword;
    private TextView mConfirmPassword;
    private Button registerButton;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private TopFoodDatabase topFoodDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();



        mName = (TextView)findViewById(R.id.register_name_edit_text);
        mPhone = (TextView)findViewById(R.id.register_phone_edit_text);
        mEmail = (TextView)findViewById(R.id.register_email_edit_text);
        mPassword = (TextView)findViewById(R.id.register_password_edit_text);
        mConfirmPassword = (TextView)findViewById(R.id.register_confirm_password_edit_text);

        registerButton = (Button)findViewById(R.id.register_register_button);
        registerButton.setOnClickListener(this);

        loginButton = (Button)findViewById(R.id.register_login_button);
        loginButton.setOnClickListener(this);


    }



    private boolean validateForm(){
        boolean valid = true;

        String name = mName.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();
        if(TextUtils.isEmpty(name)){
            mName.setError("Required.");
            valid = false;
        }
        else {
            mName.setError(null);
        }
        if(TextUtils.isEmpty(phone)){
            mPhone.setError("Required.");
            valid = false;
        }
        else {
            mPhone.setError(null);
        }
        if(TextUtils.isEmpty(email)){
            mEmail.setError("Required.");
            valid = false;
        }
        else {
            mEmail.setError(null);
        }
        if(!Pattern.matches(REGEX_EMAIL,email)){
            mEmail.setError("Please enter the corret email!");
            valid = false;
        }
        else {
            mEmail.setError(null);
        }
        if(TextUtils.isEmpty(password)){
            mPassword.setError("Required.");
            valid = false;
        } else if(password.length() < 6){
            mPassword.setError("Password need at least 6 characters.");
            valid = false;
        }
        else {
            mPassword.setError(null);
        }

        if(TextUtils.isEmpty(confirmPassword)){
            mConfirmPassword.setError("Required.");
            valid = false;
        }else if(!password.equals(confirmPassword)){
            Toast.makeText(getApplicationContext(),"Password should be the same with confirm password!",Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }



    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
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

    /**
     * Create account according to the email and password
     * @param email
     * @param password
     */
    private void createAccount(String email, String password){
        if(!validateForm()){
            return;
        }

        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(),"Sign in successful!",Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Email has already been registerd!",Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }
                });
    }


    /**
     * Once user has registered, system will jump to the homepage fragment and upload user information to the database
     * @param user
     */
    private void updateUI(FirebaseUser user){
        hideProgressDialog();
        if(user != null){
            final ArrayList<Notification> notifications = new ArrayList<>();
            String uid = mAuth.getCurrentUser().getUid();
            String name = mName.getText().toString().trim();
            String phone = mPhone.getText().toString().trim();
            String email = mAuth.getCurrentUser().getEmail();
            final User user1 = new User(uid, name, phone, email, "");
            topFoodDatabase = new TopFoodDatabase();
            topFoodDatabase.getmDatabaseRefNote().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                        notifications.add(singleSnapshot.getValue(Notification.class));
                    }
                    user1.setNotifications(notifications);
                    topFoodDatabase.addUser(user1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Intent newIntent = new Intent(this, HomepageActivity.class);
            startActivity(newIntent);
        }

    }


    @Override
    public void onClick(View v){

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString();

        switch (v.getId()){
            case R.id.register_register_button:
                createAccount(email, password);
                break;
            case R.id.register_login_button:
                Intent newIntent = new Intent(this, LoginActivity.class);
                startActivity(newIntent);
                break;
        }
    }
}

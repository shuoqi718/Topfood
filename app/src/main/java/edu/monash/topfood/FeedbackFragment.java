package edu.monash.topfood;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import edu.monash.topfood.models.Feedback;
import edu.monash.topfood.models.TopFoodDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener{

    private TextView feedbackTextView;
    private Button cancelButton;
    private Button submitButton;
    private TopFoodDatabase mDatabase;
    private FirebaseAuth mAuth;

    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        feedbackTextView = (TextView)view.findViewById(R.id.feedback_feedback_text_view);
        cancelButton = (Button)view.findViewById(R.id.feedback_cancel_button);
        submitButton = (Button)view.findViewById(R.id.feedback_submit_button);

        mDatabase = new TopFoodDatabase();
        mAuth = FirebaseAuth.getInstance();

        cancelButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.feedback_submit_button:
                if(!validForm())
                    return;
                completeFeedback();
                break;
            case R.id.feedback_cancel_button:
                break;
        }
        Fragment fragment = new HomepageFragment();
        FragmentTransaction mFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
        mFragmentTransaction.replace(R.id.main_frame,fragment);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
    }

    private boolean validForm(){
        boolean valid = true;
        String content = feedbackTextView.getText().toString();
        if(TextUtils.isEmpty(content)){
            Toast.makeText(getContext(),"Please give us some advice.",Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    /**
     * Upload feedback to the database
     */
    private void completeFeedback(){
        String content = feedbackTextView.getText().toString();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.setTimeZone(TimeZone.getTimeZone("Australia/Victoria"));
        Date date = currentDate.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String time = simpleDateFormat.format(date);
        String uid = mAuth.getCurrentUser().getUid();
        Feedback feedback = new Feedback(time, content);
        mDatabase.addFeed(uid, feedback);
        Toast.makeText(getContext(),"Thank you for your opinion!", Toast.LENGTH_SHORT).show();
    }
}

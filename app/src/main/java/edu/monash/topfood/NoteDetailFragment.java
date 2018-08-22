package edu.monash.topfood;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import edu.monash.topfood.R;
import edu.monash.topfood.models.Notification;
import edu.monash.topfood.models.TopFoodDatabase;
import edu.monash.topfood.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoteDetailFragment extends Fragment implements View.OnClickListener{

    private TextView titleTextView;
    private TextView timeTextView;
    private TextView contentTextView;
    private Notification notification;
    private Button deleteButton;
    private Button returnButton;
    private FirebaseAuth mAuth;
    private TopFoodDatabase mDatabase;

    public NoteDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_detail, container, false);
        // Inflate the layout for this fragment

        timeTextView = (TextView) view.findViewById(R.id.note_detail_time_text_view);
        titleTextView = (TextView) view.findViewById(R.id.note_detail_title_text_view);
        contentTextView = (TextView)view.findViewById(R.id.note_detail_content_text_view);

        mDatabase = new TopFoodDatabase();

        mAuth = FirebaseAuth.getInstance();
        deleteButton = (Button)view.findViewById(R.id.note_detail_delete_button);
        returnButton = (Button)view.findViewById(R.id.note_detail_return_button);

        deleteButton.setOnClickListener(this);
        returnButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.note_detail_delete_button:
                deleteNote();
                break;
            case R.id.note_detail_return_button:
                Fragment fragment = new NoteFragment();
                FragmentTransaction mFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                mFragmentTransaction.replace(R.id.main_frame,fragment);
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();
                break;
        }

    }

    /**
     * Delete notification of user and update it to the database
     */
    private void deleteNote(){
        View view = getView();
        final String uid = mAuth.getCurrentUser().getUid();
        AlertDialog.Builder builder =
                new AlertDialog.Builder(view.getContext());
        builder.setTitle("Remove " + notification.getTitle() + "?");
        builder.setMessage("Are you sure you wish to remove this notification?");
        builder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        mDatabase.getmDatabaseRefUser().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                ArrayList<Notification> notifications = user.getNotifications();
                                for(Notification note: notifications){
                                    if(note.getTime().equals(notification.getTime())){
                                        notifications.remove(note);
                                        break;
                                    }
                                }
                                user.setNotifications(notifications);
                                mDatabase.updateUser(user);
                                Toast.makeText(getContext(),"Notification is deleted", Toast.LENGTH_SHORT).show();
                                Fragment fragment = new NoteFragment();
                                FragmentTransaction mFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                                mFragmentTransaction.replace(R.id.main_frame,fragment);
                                mFragmentTransaction.addToBackStack(null);
                                mFragmentTransaction.commit();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            notification = args.getParcelable("note");
            String time = notification.getTime();
            time = time.substring(0,4) + "/" + time.substring(4,6) + "/" + time.substring(6,8) +
                    " " + time.substring(8,10) + ":" + time.substring(10);
            timeTextView.setText(time);
            titleTextView.setText(notification.getTitle());
            contentTextView.setText(notification.getContent());
        }
    }

}

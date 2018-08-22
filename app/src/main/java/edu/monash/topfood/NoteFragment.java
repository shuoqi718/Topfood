package edu.monash.topfood;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.monash.topfood.models.NoteAdapter;
import edu.monash.topfood.models.Notification;
import edu.monash.topfood.models.TopFoodDatabase;
import edu.monash.topfood.models.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoteFragment extends Fragment implements AdapterView.OnItemClickListener{

    private static final String TAG = "PaymentFragment";

    private ListView mListView;
    private NoteAdapter mAdapter;
    private ArrayList<Notification> notifications;
    private TopFoodDatabase mDatabase;
    private NoteListener mCallback;
    private FirebaseAuth mAuth;

    public NoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        // Inflate the layout for this fragment
        mListView = (ListView)view.findViewById(R.id.note_list_view);
        mDatabase = new TopFoodDatabase();
        notifications = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        mListView.setOnItemClickListener(this);

        readData();

        return view;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            mCallback = (NoteListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() +
                    "must implement NoteListener");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Notification notification = (Notification)mAdapter.getItem(position);
        mCallback.noteSelected(notification);
    }

//    private void initialiseNote(){
//        notifications.add(new Notification("201805241320", "New discount!","Test message"));
//        notifications.add(new Notification("201805241322", "New discount!","Test message"));
//        notifications.add(new Notification("201805241323", "New discount!","Test message"));
//        notifications.add(new Notification("201805241324", "New discount!","Test message"));
//        notifications.add(new Notification("201805241325", "New discount!","Test message"));
//        for(Notification notification: notifications){
//            mDatabase.addNote(notification);
//        }
//    }

    /**
     * Download notification from database and display in the list view
     */
    private void readData(){
        final String uid = mAuth.getCurrentUser().getUid();
        mDatabase.getmDatabaseRefUser().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                notifications = user.getNotifications();
                if(notifications != null){
                    mAdapter = new NoteAdapter(getActivity(), notifications);
                    mListView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public interface NoteListener{
        void noteSelected(Notification notification);
    }

}

package edu.monash.topfood.models;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.security.acl.NotOwnerException;
import java.util.ArrayList;

import edu.monash.topfood.R;

public class NoteAdapter extends BaseAdapter {
    private Context mCurrentContext;
    private ArrayList<Notification> notifications;

    public NoteAdapter(Context con, ArrayList<Notification> notifications){
        this.notifications = notifications;
        mCurrentContext = con;
    }

    @Override
    public int getCount(){return notifications.size();}

    @Override
    public Object getItem(int i){ return notifications.get(i);}

    @Override
    public long getItemId(int i){ return i;}

    @Override
    public View getView(int i, View view, ViewGroup viewGroup){
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)mCurrentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_note_item, null);
        }

        TextView timeTextView = (TextView)view.findViewById(R.id.list_note_time_text_view);
        TextView titleTextView = (TextView)view.findViewById(R.id.list_note_item_title_text_view);

        String time = notifications.get(i).getTime();
        time = time.substring(0,4) + "/" + time.substring(4,6) + "/" + time.substring(6,8) +
                " " + time.substring(8,10) + ":" + time.substring(10);
        timeTextView.setText(time);
        titleTextView.setText(notifications.get(i).getTitle());

        return view;
    }

}

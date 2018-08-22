package edu.monash.topfood.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.monash.topfood.R;

public class AboutAdapter extends BaseAdapter {
    private Context mCurrentContext;
    private ArrayList<About> abouts;

    public AboutAdapter(Context con, ArrayList<About> abouts){
        this.abouts = abouts;
        mCurrentContext = con;
    }

    @Override
    public int getCount(){return abouts.size();}

    @Override
    public Object getItem(int i){ return abouts.get(i);}

    @Override
    public long getItemId(int i){ return i;}

    @Override
    public View getView(int i, View view, ViewGroup viewGroup){
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)mCurrentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_about_item, null);
        }

        TextView titleView = (TextView) view.findViewById(R.id.list_about_title_text_view);
        TextView sourceView = (TextView) view.findViewById(R.id.list_about_source_text_view);

        String title = abouts.get(i).getTitle();
        String source = abouts.get(i).getSource();

        titleView.setText(title);
        sourceView.setText(source);

        return view;
    }
}

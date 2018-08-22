package edu.monash.topfood.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.monash.topfood.R;

/**
 * Created by You on 3/05/2018.
 */

public class OrderAdapter extends BaseAdapter {
    private Context mCurrentContext;
    private ArrayList<Order> mOrderList;

    public OrderAdapter(Context con, ArrayList<Order> orders){
        mCurrentContext = con;
        mOrderList = orders;
    }

    @Override
    public int getCount(){ return mOrderList.size();}

    @Override
    public Object getItem(int i){ return mOrderList.get(i);}

    @Override
    public long getItemId(int i){return i;}

    @Override
    public View getView(int i, View view, ViewGroup viewGroup){
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) mCurrentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_order_item,null);
        }

        TextView timeView = (TextView)view.findViewById(R.id.list_note_item_title_text_view);
        TextView statusView = (TextView)view.findViewById(R.id.list_order_status_text_view);
        TextView totalPriceView = (TextView)view.findViewById(R.id.list_order_price_text_view);

        String time = mOrderList.get(i).getTime();
        time = time.substring(0,4) + "/" + time.substring(4,6) + "/" + time.substring(6,8) +
                " " + time.substring(8,10) + ":" + time.substring(10);
        timeView.setText(time);
        statusView.setText(mOrderList.get(i).getStatus());
        totalPriceView.setText(mOrderList.get(i).getTotalPrice());
        return view;
    }
}

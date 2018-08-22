package edu.monash.topfood.order;


import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.monash.topfood.R;
import edu.monash.topfood.models.Food;
import edu.monash.topfood.models.Order;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderDetailFragment extends Fragment {

    private static final String TAG = "OrderDetailFragment";

    private Order order;
    private TextView timeTextView;
    private TextView phoneTextView;
    private TextView addressTextView;
    private TextView commentTextView;
    private ArrayList<Food> foods;
    private TextView totalPriceTextView;
    private TextView nameListTextView;
    private TextView xListTextView;
    private TextView numberListTextView;


    public OrderDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
        // Inflate the layout for this fragment

        timeTextView = (TextView)view.findViewById(R.id.order_detail_order_time_text_view);
        phoneTextView = (TextView) view.findViewById(R.id.order_detail_phone_text_view);
        addressTextView = (TextView)view.findViewById(R.id.order_detail_address_text_view);
        commentTextView = (TextView) view.findViewById(R.id.order_detail_comment_text_view);
        foods = new ArrayList<>();
        totalPriceTextView = (TextView)view.findViewById(R.id.order_detail_total_price_text_view);
        nameListTextView = (TextView)view.findViewById(R.id.order_detail_food_name_list);
        xListTextView = (TextView)view.findViewById(R.id.order_detail_x_list);
        numberListTextView = (TextView)view.findViewById(R.id.order_detail_number_list);

        return view;
    }

    /**
     * Receive order and initialize the view
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            order = args.getParcelable("order");
            String time = order.getTime();
            time = time.substring(0,4) + "/" + time.substring(4,6) + "/" + time.substring(6,8) +
                    " " + time.substring(8,10) + ":" + time.substring(10);
            timeTextView.setText(time);
            phoneTextView.setText(order.getPhone());
            addressTextView.setText(order.getAddress());
            commentTextView.setText(order.getComments());
            foods = order.getFoods();
            String nameList = "";
            String xList = "";
            String numberList ="";
            for(Food food: foods){
                nameList += food.getName() + "\n";
                xList += "X\n";
                numberList += food.getNumber() + "\n";
            }
            nameListTextView.setText(nameList);
            xListTextView.setText(xList);
            numberListTextView.setText(numberList);
            totalPriceTextView.setText(order.getTotalPrice());
        }
    }

}

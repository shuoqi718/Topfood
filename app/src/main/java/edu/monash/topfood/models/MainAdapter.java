package edu.monash.topfood.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import edu.monash.topfood.R;

/**
 * Created by You on 2/05/2018.
 */

public class MainAdapter extends BaseAdapter implements Filterable{

    final long ONE_MEGABYTE = Long.MAX_VALUE;

    private Context mCurrentContext;
    private ArrayList<Food> mFilteredList;
    private FoodFilter mFilter;
    private ArrayList<Food> mFoodList;
    private FirebaseStorage mStorage;
    private Bitmap bitmap;

    public MainAdapter(Context con, ArrayList<Food> foods){
        mCurrentContext = con;
        mFilteredList = foods;
        mFoodList = foods;
    }

    @Override
    public int getCount(){ return mFilteredList.size();}

    @Override
    public Object getItem(int i){ return mFilteredList.get(i);}

    @Override
    public long getItemId(int i){return i;}



    @Override
    public View getView(final int i, View view, ViewGroup viewGroup){
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) mCurrentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_food_item,null);
        }

        TextView nameView = (TextView)view.findViewById(R.id.list_order_confirm_name_text_view);
        TextView priceView = (TextView)view.findViewById(R.id.list_food_price_text_view);
        final TextView foodNumber = (TextView)view.findViewById(R.id.list_order_confirm_number_text_view);
        final ImageView foodImage = (ImageView) view.findViewById(R.id.list_order_confirm_image);

        Button addButton = (Button)view.findViewById(R.id.list_order_confirm_add_button);
        Button minusButton = (Button)view.findViewById(R.id.list_order_confirm_minus_button);

        mStorage = FirebaseStorage.getInstance();
        if(!mFilteredList.get(i).getImage().isEmpty()){
            StorageReference storageRef = mStorage.getReferenceFromUrl(mFilteredList.get(i).getImage());
            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    foodImage.setImageBitmap(bitmap);
                }
            });
        }

        nameView.setText(mFilteredList.get(i).getName());
        priceView.setText(mFilteredList.get(i).getPrice());
        foodNumber.setText(mFilteredList.get(i).getNumber());


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(foodNumber.getText().toString());
                count++;
                foodNumber.setText(count + "");
                mFilteredList.get(i).setNumber(count + "");
                notifyDataSetChanged();
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(foodNumber.getText().toString());
                if (count > 0){
                    count--;
                    foodNumber.setText(count + "");
                    mFilteredList.get(i).setNumber(count + "");
                    notifyDataSetChanged();
                }
            }
        });
        return view;
    }


    @Override
    public Filter getFilter(){
        if(mFilter == null){
            mFilter = new FoodFilter();
        }
        return mFilter;
    }

    private class FoodFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint){
            FilterResults results = new FilterResults();
            if(constraint != null && constraint.length() > 0){
                ArrayList<Food> tempList = new ArrayList<>();

                for(Food food: mFoodList){
                    if(food.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        tempList.add(food);
                    }
                }

                results.count = tempList.size();
                results.values = tempList;
            }
            else {
                results.count = mFoodList.size();
                results.values = mFoodList;
            }

            return results;
        }

        @Override
        public void publishResults(CharSequence constraint, FilterResults results){
            mFilteredList = (ArrayList<Food>) results.values;
            notifyDataSetChanged();
        }
    }
}

package edu.monash.topfood.models;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReferenceArray;

import edu.monash.topfood.R;

public class OrderConfirmAdapter extends BaseAdapter {

    final long ONE_MEGABYTE = Long.MAX_VALUE;
    private Context mCurrentContext;
    private ArrayList<Food> mFoodList;
    private FirebaseStorage mStorage;
    private Bitmap bitmap;

    public OrderConfirmAdapter(Context con, ArrayList<Food> foods){
        mCurrentContext = con;
        mFoodList = foods;
    }

    @Override
    public int getCount(){ return mFoodList.size();}

    @Override
    public Object getItem(int i){ return mFoodList.get(i);}

    @Override
    public long getItemId(int i){return i;}

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup){
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) mCurrentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_order_confirm_item,null);
        }


        TextView nameView = (TextView)view.findViewById(R.id.list_order_confirm_name_text_view);
        TextView priceView = (TextView)view.findViewById(R.id.list_order_confirm_price_text_view);
        final TextView foodNumber = (TextView)view.findViewById(R.id.list_order_confirm_number_text_view);
        final ImageView foodImage = (ImageView)view.findViewById(R.id.list_order_confirm_image);

        Button addButton = (Button)view.findViewById(R.id.list_order_confirm_add_button);
        Button minusButton = (Button)view.findViewById(R.id.list_order_confirm_minus_button);
        Button deleteButton = (Button)view.findViewById(R.id.list_order_confirm_delete_button);


        nameView.setText(mFoodList.get(i).getName());
        priceView.setText(mFoodList.get(i).getPrice());
        foodNumber.setText(mFoodList.get(i).getNumber());

        mStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = mStorage.getReferenceFromUrl(mFoodList.get(i).getImage());
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                foodImage.setImageBitmap(bitmap);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(foodNumber.getText().toString());
                count++;
                foodNumber.setText(count + "");
                mFoodList.get(i).setNumber(count + "");
                notifyDataSetChanged();
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(foodNumber.getText().toString());
                if (count > 1){
                    count--;
                    foodNumber.setText(count + "");
                    mFoodList.get(i).setNumber(count + "");
                    notifyDataSetChanged();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(view.getContext());
                builder.setTitle("Remove " + mFoodList.get(i).getName() + "?");
                builder.setMessage("Are you sure you wish to remove this food?");
                builder.setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                mFoodList.remove(i);
                                notifyDataSetChanged();
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
        });
        return view;
    }

}

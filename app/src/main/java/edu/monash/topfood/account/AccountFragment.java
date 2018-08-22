package edu.monash.topfood.account;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.monash.topfood.R;
import edu.monash.topfood.order.HistoryFragment;
import edu.monash.topfood.order.PaymentFragment;


public class AccountFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Button setNameButton;
    private Button setAddressButton;
    private Button setPasswordButton;
    private Button setPhoneButton;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        setNameButton = (Button)view.findViewById(R.id.account_edit_name_button);
        setAddressButton = (Button)view.findViewById(R.id.account_set_address_button);
        setPasswordButton = (Button)view.findViewById(R.id.account_change_password_button);
        setPhoneButton = (Button) view.findViewById(R.id.account_set_phone_button);


        setNameButton.setOnClickListener(this);
        setAddressButton.setOnClickListener(this);
        setPasswordButton.setOnClickListener(this);
        setPhoneButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view){
        Fragment fragment = null;
        switch (view.getId()){
            case R.id.account_edit_name_button:
                fragment = new SetNameFragment();
                break;
            case R.id.account_set_address_button:
                fragment = new SetAddressFragment();
                break;
            case R.id.account_change_password_button:
                fragment = new SetPasswordReAuthFragment();
                break;
            case R.id.account_set_phone_button:
                fragment = new SetPhoneFragment();
        }
        if(fragment != null){
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
            ft.replace(R.id.main_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }


}

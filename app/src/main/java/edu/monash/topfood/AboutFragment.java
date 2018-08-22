package edu.monash.topfood;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.monash.topfood.models.About;
import edu.monash.topfood.models.AboutAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    private static final String TAG = "AboutFragment";

    private ListView mListView;
    private AboutAdapter mAdapter;
    private ArrayList<About> abouts;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_about, container, false);
        // Inflate the layout for this fragment

        mListView = (ListView)view.findViewById(R.id.about_list_view);
        abouts = new ArrayList<>();
        initializeAbout();

        mAdapter = new AboutAdapter(getActivity(), abouts);
        mListView.setAdapter(mAdapter);

        return view;
    }

    private void initializeAbout(){
        abouts.add(new About("Fragment Communication", "http://www.vogella.com/tutorials/AndroidFragments/article.html"));
        abouts.add(new About("Add button in item in list view","https://blog.csdn.net/sweiqin/article/details/45799049"));
        abouts.add(new About("Pass data between fragments","https://inducesmile.com/android/android-fragment-how-to-pass-data-between-fragments-in-an-activity/"));
        abouts.add(new About("Get current position by using mapview","https://stackoverflow.com/questions/38473246/how-can-i-get-current-location-on-mapview-using-fragment-class"));
        abouts.add(new About("Using email and password signing in firebase", "https://github.com/firebase/quickstart-android/blob/da5e1c8a6174b42fb9cb559a5ce5249ea27d8bf7/auth/app/src/main/java/com/google/firebase/quickstart/auth/EmailPasswordActivity.java#L71-L77"));
        abouts.add(new About("Read and write data to firebase","https://firebase.google.com/docs/database/android/read-and-write?authuser=0"));
        abouts.add(new About("Sort Arraylist with comparator", "https://stackoverflow.com/questions/43165163/sort-an-arraylist-using-comparator"));
    }
}

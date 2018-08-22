package edu.monash.topfood.maps;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import edu.monash.topfood.HomepageFragment;
import edu.monash.topfood.LoginActivity;
import edu.monash.topfood.Manifest;
import edu.monash.topfood.R;
import edu.monash.topfood.account.SetPasswordFragment;
import edu.monash.topfood.order.OrderFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocatorFragment extends Fragment implements OnMapReadyCallback,LocationListener, ActivityCompat.OnRequestPermissionsResultCallback,View.OnClickListener{

    private MapView mapView;
    private GoogleMap googleMap;
    private Button orderButton;
    private FirebaseAuth mAuth;
    private ArrayList<LocDetails> mSavedLocations;
    private LatLng mCurrentLoc;
    private MarkerOptions markers;
    private LocationManager locationManager;
    private Spinner locateSpinner;
    private ArrayList<String> locateList;
    private GpsStatus mStatus;
    private static final int MY_PERMISSIONS_REQUEST_CALL_MAP = 1;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    public LocatorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locator, container, false);

        mAuth = FirebaseAuth.getInstance();

        mSavedLocations = new ArrayList<>();
        locateList = new ArrayList<>();
        initialiseLocation();
        orderButton = (Button)view.findViewById(R.id.locator_order_button);
        mapView = (MapView)view.findViewById(R.id.locator_map_view);
        mapView.onCreate(savedInstanceState);
        locateSpinner = (Spinner)view.findViewById(R.id.locator_spinner);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, locateList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locateSpinner.setAdapter(adapter);

        locateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String location = locateList.get(i);
                for(LocDetails locDetails: mSavedLocations){
                    if(locDetails.getmLocName().contains(location)){
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locDetails.getmLatLng(),15));
                        break;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mapView.getMapAsync(this);

        orderButton.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }


    /**
     * Add all restaurants to the map
     */
    private void updateMapMarkers(){
        if(googleMap != null){
            googleMap.clear();
            for(LocDetails locDetails: mSavedLocations){
                googleMap.addMarker(new MarkerOptions().position(locDetails.getmLatLng()).
                        title(locDetails.getmLocName()));
            }
        }
    }

    /**
     * Initialize locations and restaurant name for the spinner
     */
    private void initialiseLocation(){
        locateList.add("Please choose location");
        locateList.add("Caulfield");
        locateList.add("Clayton");
        locateList.add("Berwick");
        locateList.add("Peninsula");
        locateList.add("Parkville");
        locateList.add("Gippsland");
        mSavedLocations.add(new LocDetails("Caulfield Restaurant",
                new LatLng(-37.8770, 145.0443)));
        mSavedLocations.add(new LocDetails("Clayton Restaurant",
                new LatLng(-37.9150, 145.1300)));
        mSavedLocations.add(new LocDetails("Berwick Restaurant",
                new LatLng(-38.0405, 145.3399)));
        mSavedLocations.add(new LocDetails("Peninsula Restaurant",
                new LatLng(-38.1536, 145.1344)));
        mSavedLocations.add(new LocDetails("Parkville Restaurant",
                new LatLng(-37.7838, 144.9587)));
        mSavedLocations.add(new LocDetails("Gippsland Restaurant",
                new LatLng(-38.3112, 146.4294)));
    }

    @Override
    public void onClick(View view){
        if(mAuth.getCurrentUser() == null){
            Intent newIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(newIntent);
        }
        else {
            Fragment fragment = new OrderFragment();
            FragmentTransaction mFragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
            mFragmentTransaction.replace(R.id.main_frame,fragment);
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.commit();
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();;
        mapView.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap gmap){
        googleMap = gmap;
        updateMapMarkers();
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        googleMap.setMyLocationEnabled(true);

        locationManager = (LocationManager)getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(bestProvider);
        if(location != null){
            onLocationChanged(location);
        }

    }


    @Override
    public void onLocationChanged(Location location){
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    @Override
    public void onProviderDisabled(String provider){
        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getActivity().getBaseContext(),"GPS is turned off", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider){
        Toast.makeText(getActivity().getBaseContext(),"GPS is turned on", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){

    }

}

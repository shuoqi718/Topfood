package edu.monash.topfood;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import edu.monash.topfood.account.AccountFragment;
import edu.monash.topfood.maps.LocatorFragment;
import edu.monash.topfood.models.Food;
import edu.monash.topfood.models.Notification;
import edu.monash.topfood.models.Order;
import edu.monash.topfood.models.TopFoodDatabase;
import edu.monash.topfood.order.FoodlistFragment;
import edu.monash.topfood.order.HistoryFragment;
import edu.monash.topfood.order.OrderConfirmFragment;
import edu.monash.topfood.order.OrderDetailFragment;
import edu.monash.topfood.order.OrderFragment;
import edu.monash.topfood.order.PaymentFragment;

public class HomepageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OrderFragment.FragmentListener,
        OrderConfirmFragment.OrderEditListener, HistoryFragment.OrderDetailListener,
        NoteFragment.NoteListener{

    private FirebaseAuth mAuth;
    private TopFoodDatabase mDatabase;
    private NavigationView mNavigationView;
    private TextView headText;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        Fragment fragment = new HomepageFragment();
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.add(R.id.main_frame,fragment);
        mFragmentTransaction.commit();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        View headerView = navigationView.getHeaderView(0);
//        headText = headerView.findViewById(R.id.nav_head_homepage_welcome_text);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Transfer categories to FoodlistFragment to display different food list
     * @param adapter
     */
    @Override
    public void orderAdapterSelected(String adapter){
        FragmentManager fm = getSupportFragmentManager();
        FoodlistFragment foodlistFragment = new FoodlistFragment();
        Bundle args = new Bundle();
        args.putString("adapter",adapter);
        foodlistFragment.setArguments(args);

        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
        mFragmentTransaction.replace(R.id.main_frame,foodlistFragment);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
    }

    /**
     * Transfer notification to NoteDetailFragment to display the detail
     * @param notification
     */
    @Override
    public void noteSelected(Notification notification){
        FragmentManager fm = getSupportFragmentManager();
        NoteDetailFragment noteDetailFragment = new NoteDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("note", notification);
        noteDetailFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.main_frame, noteDetailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Transfer order to the OrderDetailFragment to display the detail
     * @param order
     */
    @Override
    public void orderDetailSelected(Order order){
        FragmentManager fm = getSupportFragmentManager();
        OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("order", order);
        orderDetailFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.main_frame, orderDetailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Transfer food list to the PaymentFragment to display total price and make order
     * @param foods
     */
    @Override
    public void orderSelected(ArrayList<Food> foods){
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaymentFragment paymentFragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("foods", foods);
        paymentFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.main_frame, paymentFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();


        updateUI(user);
    }


    /**
     * update UI depending on user signed in or not
     * @param user
     */
    private void updateUI(FirebaseUser user){
        mNavigationView = (NavigationView)findViewById(R.id.nav_view);
        Menu nav_menu = mNavigationView.getMenu();
        if(user != null){

            nav_menu.findItem(R.id.nav_account).setVisible(true);
            nav_menu.findItem(R.id.nav_history).setVisible(true);
            nav_menu.findItem(R.id.nav_notification).setVisible(true);
            nav_menu.findItem(R.id.nav_feedback).setVisible(true);
            nav_menu.findItem(R.id.nav_sign_out).setVisible(true);
            nav_menu.findItem(R.id.nav_register).setVisible(false);
            nav_menu.findItem(R.id.nav_login).setVisible(false);

        }
        else {
            nav_menu.findItem(R.id.nav_account).setVisible(false);
            nav_menu.findItem(R.id.nav_history).setVisible(false);
            nav_menu.findItem(R.id.nav_notification).setVisible(false);
            nav_menu.findItem(R.id.nav_feedback).setVisible(false);
            nav_menu.findItem(R.id.nav_sign_out).setVisible(false);
            nav_menu.findItem(R.id.nav_register).setVisible(true);
            nav_menu.findItem(R.id.nav_login).setVisible(true);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homepage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut(){
        View view = findViewById(R.id.drawer_layout);
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Sign Out?");
        builder.setMessage("Are you sure you wish to sign out?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                updateUI(null);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = new HomepageFragment();
        } else if (id == R.id.nav_account){
            fragment = new AccountFragment();
        } else if (id == R.id.nav_history) {
            fragment = new HistoryFragment();
        } else if (id == R.id.nav_locator) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            if(permissionCheck != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
                return false;
            }
            fragment = new LocatorFragment();
        } else if (id == R.id.nav_notification) {
            fragment = new NoteFragment();
        } else if (id == R.id.nav_feedback) {
            fragment = new FeedbackFragment();
        } else if (id == R.id.nav_sign_out) {
            signOut();
            fragment = new HomepageFragment();
        } else if (id == R.id.nav_about){
            fragment = new AboutFragment();
        } else if (id == R.id.nav_login) {
            Intent newIntent = new Intent(HomepageActivity.this, LoginActivity.class);
            startActivity(newIntent);
        } else if (id == R.id.nav_register){
            Intent newIntent = new Intent(HomepageActivity.this, RegisterActivity.class);
            startActivity(newIntent);
        }

        if(fragment != null){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
            ft.replace(R.id.main_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}

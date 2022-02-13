package com.example.taxi.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.taxi.R;
import com.example.taxi.constant.Tools;
import com.example.taxi.database.DBConnection;
import com.example.taxi.database.VolleyCallback;
import com.example.taxi.databinding.ActivityMainBinding;
import com.example.taxi.modal.Driver;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity  {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public static DBConnection dbConnection;
    public static Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbConnection = new DBConnection(this);
        if (isLogged()!=true) {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        }


        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View header = navigationView.getHeaderView(0);
            setHeader(header);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile,R.id.nav_statistics)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //setSideHeader();
    }

    private void setHeader(View header) {
        ((TextView)  header.findViewById(R.id.side_driver_name)).setText(driver.getDriverName());

        ((TextView)  header.findViewById(R.id.side_email)).setText(driver.getEmail());
        MainActivity.dbConnection.uploadImage(new VolleyCallback() {


            @Override
            public void onSuccess(JSONObject result) throws Exception {

            }

            @Override
            public void onSuccessImage(Bitmap bitmap) throws Exception {
                ((ImageView)  header.findViewById(R.id.side_driver_photo)).setImageBitmap(bitmap);
            }

            @Override
            public void onError(String result) {

            }


        },"upload", driver.getImagePath());

    }


    public boolean isLogged() {
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(Tools.DRIVER, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedpreferences.getString("driver", "");
        System.out.println(json);
        this.driver = gson.fromJson(json, Driver.class);

        return sharedpreferences.getBoolean("isLogged", false);
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logoutAccount(MenuItem item) {

        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);

    }


}
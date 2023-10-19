package com.ccs114.fisda;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.ccs114.fisda.CaptureFragment;
import com.ccs114.fisda.CollectionFragment;
import com.ccs114.fisda.HomeFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.Interpreter;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    CaptureFragment captureFragment = new CaptureFragment();
    CollectionFragment collectionsFragment = new CollectionFragment();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView  = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.capture);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,captureFragment).commit();


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.home){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
                    return true;
                } else if (id == R.id.capture) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,captureFragment).commit();
                    return true;
                }else{
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,collectionsFragment).commit();
                    return true;
                }
            }
        });
    }







}
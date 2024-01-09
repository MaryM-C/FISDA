package com.ccs114.fisda.activities;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ccs114.fisda.R;
import com.ccs114.fisda.databinding.ActivityMainBinding;
import com.ccs114.fisda.fragments.CaptureFragment;
import com.ccs114.fisda.fragments.CollectionFragment;
import com.ccs114.fisda.fragments.HomeFragment;

/**
 * The main activity of the FiSDA application, responsible for managing the UI and navigation.
 */
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    HomeFragment homeFragment = new HomeFragment();
    CaptureFragment captureFragment = new CaptureFragment();
    CollectionFragment collectionsFragment = new CollectionFragment();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.bottomNavigation.setSelectedItemId(R.id.capture);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,captureFragment).commit();

        binding.bottomNavigation.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.home){
                getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
                return true;

            } else if (item.getItemId() == R.id.capture) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container,captureFragment).commit();
                return true;

            }else{
                getSupportFragmentManager().beginTransaction().replace(R.id.container,collectionsFragment).commit();
                return true;
            }
        });

    }
    public void setSelectedItem(int itemId) {
        binding.bottomNavigation.setSelectedItemId(itemId);
    }
}
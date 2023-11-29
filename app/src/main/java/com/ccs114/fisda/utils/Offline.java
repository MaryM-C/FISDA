package com.ccs114.fisda.utils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;



public class Offline extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //automatically handle temporary network interruptions
        //Cached data is available while offline and Firebase resends any writes when network connectivity is restored.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}

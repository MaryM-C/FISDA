package com.ccs114.fisda;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FishDataManager {
    private DatabaseReference fishDataRef;


    public FishDataManager() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        fishDataRef = database.getReference("fishdata");
        fishDataRef.keepSynced(true);
    }

    public void getFishData(String fishName, final FishDataListener listener) {
        fishDataRef.child(fishName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Fish fish = dataSnapshot.getValue(Fish.class);
                    listener.onFishDataLoaded(fish);
                } else {
                    listener.onFishDataNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFishDataError(databaseError.getMessage());
            }
        });
    }

    public interface FishDataListener {
        void onFishDataLoaded(Fish fish);
        void onFishDataNotFound();
        void onFishDataError(String errorMessage);
    }
}


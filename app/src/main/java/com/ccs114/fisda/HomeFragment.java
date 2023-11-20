package com.ccs114.fisda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    //Getting firebase database reference to communicate firebase database
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;


    //creating list for MyItems to store fish details
    private final List<MyItems> myItemsList = new ArrayList<>();

    private final List<MyItems> allItemsList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //To read or write data from the database, you need an instance of DatabaseReference
        databaseReference= FirebaseDatabase.getInstance().getReference();
        //Firebase Realtime Database synchronizes and stores a local copy of the data for active listeners
        databaseReference.keepSynced(true);


        //getting recyclerview in xml file
        recyclerView = view.findViewById(R.id.collectionRecyclerView);



        //setting recyclerview size fixed for every item in the recycler view
        recyclerView.setHasFixedSize(true);
        //setting layout manager to the recylerview ex. LinearLayoutManager (vertical view)
        //recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setOrientation(recyclerView.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);




        // Fetch data from Firebase and update the dataList.
        fetchFirebaseData();

        SearchView searchView = view.findViewById(R.id.searchView);
        // Add a TextChangeListener to the SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Not needed for this case, as we want to perform filtering as the user types
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform filtering based on the search query and update the RecyclerView
                filterData(newText);
                return true;
            }
        });



        return view;
    }

    private void filterData(String query) {
        // Create a new list to store filtered items based on the search query
        List<MyItems> filteredList = new ArrayList<>();



        // Loop through all items in the allItemsList and check if they match the search query
        for (MyItems item : allItemsList) {
            if (item.getCommonName().toLowerCase().contains(query.toLowerCase())
                    || item.getLocalName().toLowerCase().contains(query.toLowerCase())
                    || item.getCategory().toLowerCase().contains(query.toLowerCase())) {
                // If the item matches the search query, add it to the filteredList
                filteredList.add(item);
            }
        }

        // Update the RecyclerView with the filtered data
        recyclerView.setAdapter(new MyAdapter(filteredList, getActivity()));
    }

    private void fetchFirebaseData() {
        databaseReference.addValueEventListener(new ValueEventListener(){
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear old items
                myItemsList.clear();
                allItemsList.clear();

                for(DataSnapshot fishdata:snapshot.child("fishdata").getChildren()){
                    //check if the child exist

                    if(fishdata.hasChild("CommonName") && fishdata.hasChild("LocalName") && fishdata.hasChild("Category") && fishdata.hasChild("Image")){

                        //getting fish details from Firebase Database and store into the list one by one
                        final String getCommonName = fishdata.child("CommonName").getValue(String.class);
                        final String getLocalName = fishdata.child("LocalName").getValue(String.class);
                        final String getCategory = fishdata.child("Category").getValue(String.class);
                        final String getImage = fishdata.child("Image").getValue(String.class);


                        // Creating MyItems object with all attributes
                        MyItems myItems = new MyItems(getCommonName, getLocalName, getCategory, getImage);

                        //adding this to the list
                        myItemsList.add(myItems);
                        allItemsList.add(myItems);

                    }
                }
                recyclerView.setAdapter(new MyAdapter(myItemsList, getActivity()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something went wrong!!!"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





    }
}
package com.ccs114.fisda;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import java.util.ArrayList;

public class CollectionFragment extends Fragment {

    CollectionsDbHelper db;
    ArrayList<String> id, filename, data_taken, image_path, first_name, second_name, third_name, first_conf, second_conf, third_conf;
    CollectionsAdapter collectionsAdapter;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        recyclerView = view.findViewById(R.id.recCollection);

        db = new CollectionsDbHelper(getContext());
        id = new ArrayList<>();
        filename = new ArrayList<>();
        data_taken = new ArrayList<>();
        image_path = new ArrayList<>();
        first_name = new ArrayList<>();
        second_name = new ArrayList<>();
        third_name = new ArrayList<>();
        first_conf = new ArrayList<>();
        second_conf = new ArrayList<>();
        third_name = new ArrayList<>();

        storeDataInArrays();

        collectionsAdapter = new CollectionsAdapter(getActivity(), id, filename, data_taken, image_path);
        collectionsAdapter.getItemCount();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(collectionsAdapter);

        return view;

    }
    void storeDataInArrays() {
        Cursor cursor = db.readAllData();
        if(cursor.getCount() == 0) {
            Toast.makeText(getContext(), "Empty", Toast.LENGTH_SHORT).show();
        } else {
            int size = 0;
            while (cursor.moveToNext()) {
                id.add(cursor.getString(0));
                filename.add(cursor.getString(1));
                data_taken.add(cursor.getString(2));
                image_path.add(cursor.getString(3));
                Log.d("DBContents", id.get(size) + " " + filename.get(size) + " " + data_taken.get(size) + " " + image_path.get(size));
//                first_name.add(cursor.getString(4));
//                second_name.add(cursor.getString(5));
//                third_name.add(cursor.getString(6));
//                first_conf.add(cursor.getString(7));
//                second_conf.add(cursor.getString(8));
//                third_conf.add(cursor.getString(9));
                size++;
            }
        }
    }


}
package com.ccs114.fisda;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

//Data Binding
import androidx.databinding.DataBindingUtil;
import com.ccs114.fisda.databinding.FragmentCollectionBinding;

public class CollectionFragment extends Fragment {
    CollectionsDbHelper db;
    ArrayList<String> id, filename, data_taken, image_path, first_name, second_name, third_name, first_conf, second_conf, third_conf;
    CollectionsAdapter collectionsAdapter;
    FragmentCollectionBinding bindData;
    private final List<MyItems> allItemsList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bindData = DataBindingUtil.inflate(inflater, R.layout.fragment_collection, container, false);
        View view = bindData.getRoot();

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
        third_conf = new ArrayList<>();

        storeDataInArrays();

        collectionsAdapter = new CollectionsAdapter(getActivity(), id, filename, data_taken, image_path, first_name,
                second_name, third_name, first_conf, second_conf, third_conf);
        collectionsAdapter.getItemCount();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);

        bindData.recCollection.setLayoutManager(gridLayoutManager);
        bindData.recCollection.setAdapter(collectionsAdapter);

        bindData.titleToolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId()==R.id.toolbar_search) {
                MenuItem menuItem = bindData.titleToolbar.getMenu().findItem(R.id.toolbar_search);
                SearchView searchView = (SearchView) menuItem.getActionView();
                searchView.setQueryHint("Start typing to search...");

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

                return true;
            }
            return false;
        });

        return view;

    }

    private void filterData(String query) {
        // Create a new list to store filtered items based on the search query
        List<MyItems> filteredList = new ArrayList<>();


        // Loop through all items in the allItemsList and check if they match the search query
        for (MyItems item : allItemsList) {
            if (item.getCommonName().toLowerCase().contains(query.toLowerCase())
                    || item.getLocalName().toLowerCase().contains(query.toLowerCase())) {
                // If the item matches the search query, add it to the filteredList
                filteredList.add(item);
            }
        }

        // Update the RecyclerView with the filtered data
        bindData.recCollection.setAdapter(new MyAdapter(filteredList, getActivity()));
    }

    private void storeDataInArrays() {
        Cursor cursor = db.readAllData();
        if(cursor.getCount() == 0) {
            Toast.makeText(getContext(), "No Images Saved", Toast.LENGTH_SHORT).show();
        } else {
            int size = 0;
            while (cursor.moveToNext()) {
                id.add(cursor.getString(0));
                filename.add(cursor.getString(1));
                data_taken.add(cursor.getString(2));
                image_path.add(cursor.getString(3));
                first_name.add(cursor.getString(4));
                second_name.add(cursor.getString(5));
                third_name.add(cursor.getString(6));
                first_conf.add(cursor.getString(7));
                second_conf.add(cursor.getString(8));
                third_conf.add(cursor.getString(9));
                size++;
            }
        }
    }
}
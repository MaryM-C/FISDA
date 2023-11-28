package com.ccs114.fisda.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;

//Data Binding
import androidx.databinding.DataBindingUtil;

import com.ccs114.fisda.R;
import com.ccs114.fisda.activities.MainActivity;
import com.ccs114.fisda.adapters.CollectionsAdapter;
import com.ccs114.fisda.database.CollectionsDbHelper;
import com.ccs114.fisda.databinding.FragmentCollectionBinding;

public class CollectionFragment extends Fragment {
    private CollectionsDbHelper db;
    private ArrayList<String> id, filename, data_taken, imageUri, imagepath, first_name, second_name, third_name, first_conf, second_conf, third_conf;
    private CollectionsAdapter collectionsAdapter;
    CaptureFragment captureFragment = new CaptureFragment();
    private FragmentCollectionBinding bindData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bindData = DataBindingUtil.inflate(inflater, R.layout.fragment_collection, container, false);
        View view = bindData.getRoot();

        db = new CollectionsDbHelper(getContext());
        id = new ArrayList<>();
        filename = new ArrayList<>();
        data_taken = new ArrayList<>();
        imageUri = new ArrayList<>();
        imagepath = new ArrayList<>();
        first_name = new ArrayList<>();
        second_name = new ArrayList<>();
        third_name = new ArrayList<>();
        first_conf = new ArrayList<>();
        second_conf = new ArrayList<>();
        third_conf = new ArrayList<>();

        storeDataInArrays();

        collectionsAdapter = new CollectionsAdapter(
                id, filename, data_taken, imageUri, imagepath, first_name,
                second_name, third_name, first_conf, second_conf, third_conf);
        collectionsAdapter.getItemCount();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);

        bindData.nonempty.recCollection.setLayoutManager(gridLayoutManager);
        bindData.nonempty.recCollection.setAdapter(collectionsAdapter);

        bindData.empty.btnCapture.setOnClickListener(view1 -> {
            FragmentManager manager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.container, captureFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            ((MainActivity) requireActivity()).setSelectedItem(R.id.capture);
        });

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
                        //implement here what to do with query
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
        //implement search filter here
        ArrayList<String> filteredId = new ArrayList<>();
        ArrayList<String> filteredFilename = new ArrayList<>();
        ArrayList<String> filteredDataTaken = new ArrayList<>();
        ArrayList<String> filteredImageUri = new ArrayList<>();
        ArrayList<String> filteredImagePath = new ArrayList<>();
        ArrayList<String> filteredFirstName = new ArrayList<>();
        ArrayList<String> filteredSecondName = new ArrayList<>();
        ArrayList<String> filteredThirdName = new ArrayList<>();
        ArrayList<String> filteredFirstConf = new ArrayList<>();
        ArrayList<String> filteredSecondConf = new ArrayList<>();
        ArrayList<String> filteredThirdConf = new ArrayList<>();

        for (int i = 0; i < filename.size(); i++) {
            if (first_name.get(i).toLowerCase().contains(query.toLowerCase())) {
                // Add the matching data to the filtered lists
                filteredId.add(id.get(i));
                filteredFilename.add(filename.get(i));
                filteredDataTaken.add(data_taken.get(i));
                filteredImageUri.add(imageUri.get(i));
                filteredImagePath.add(imagepath.get(i));
                filteredFirstName.add(first_name.get(i));
                filteredSecondName.add(second_name.get(i));
                filteredThirdName.add(third_name.get(i));
                filteredFirstConf.add(first_conf.get(i));
                filteredSecondConf.add(second_conf.get(i));
                filteredThirdConf.add(third_conf.get(i));
            }
        }

        // Create a new adapter with the filtered data
        CollectionsAdapter filteredAdapter = new CollectionsAdapter(filteredId, filteredFilename,
                filteredDataTaken, filteredImageUri, filteredImagePath, filteredFirstName, filteredSecondName, filteredThirdName,
                filteredFirstConf, filteredSecondConf, filteredThirdConf);

        // Set the filtered adapter to the RecyclerView
        bindData.nonempty.recCollection.setAdapter(filteredAdapter);
    }

    private void storeDataInArrays() {
        Cursor cursor = db.readAllData();
        if(cursor.getCount() == 0) {
            bindData.setShowEmpty(true);
        } else {
            bindData.setShowCollections(true);
            int size = 0;
            while (cursor.moveToNext()) {
                id.add(cursor.getString(0));
                filename.add(cursor.getString(1));
                data_taken.add(cursor.getString(2));
                imageUri.add(cursor.getString(3));
                imagepath.add(cursor.getString(4));
                first_name.add(cursor.getString(5));
                second_name.add(cursor.getString(6));
                third_name.add(cursor.getString(7));
                first_conf.add(cursor.getString(8));
                second_conf.add(cursor.getString(9));
                third_conf.add(cursor.getString(10));
                size++;
            }
        }
    }
}
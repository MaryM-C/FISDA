package com.ccs114.fisda.adapters;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ccs114.fisda.fragments.OutputFragment;
import com.ccs114.fisda.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.CollectionsHolder>{

    private final ArrayList id, filename, date_taken, imageUri, imagepath, first_name, second_name, third_name, first_conf, second_conf, third_conf;

    public CollectionsAdapter(ArrayList<String> id,
                              ArrayList<String> filename, ArrayList<String> date_taken,
                              ArrayList<String> imageUri,ArrayList<String> imagepath,
                              ArrayList<String> first_name,
                              ArrayList<String> second_name, ArrayList<String> third_name,
                              ArrayList<String> first_conf, ArrayList<String> second_conf,
                              ArrayList<String> third_conf) {
        this.id = id;
        this.filename = filename;
        this.date_taken = date_taken;
        this.imagepath = imagepath;
        this.imageUri = imageUri;
        this.first_name = first_name;
        this.second_name = second_name;
        this.third_name = third_name;
        this.first_conf = first_conf;
        this.second_conf = second_conf;
        this.third_conf = third_conf;
    }

    @NonNull
    @Override
    public CollectionsAdapter.CollectionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CollectionsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_collections, null));
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionsHolder holder, int position) {
        holder.first_name.setText(String.valueOf(first_name.get(position)));
        holder.dateTaken.setText(String.valueOf(date_taken.get(position)));
        Uri imageURI = Uri.parse("file://" + imagepath.get(position));
        try {
            Picasso.get()
                    .load(imageURI)
                    .resize(140, 145)
                    .centerCrop()
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("Picasso", "Successful");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("Picasso", "Failed ");
                        }
                    })
            ;
            Picasso.get().setLoggingEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Collection", "Fail to retrieve image ");
        }


        holder.imageView.setOnClickListener(view -> {
            String imageURI1 = String.valueOf(imageUri.get(holder.getAdapterPosition()));
            String imagePath= String.valueOf(imagepath.get(holder.getAdapterPosition()));

            String[] topFishSpecies = new String[3];
            topFishSpecies[0] = String.valueOf(first_name.get(holder.getAdapterPosition()));
            topFishSpecies[1] = String.valueOf(second_name.get(holder.getAdapterPosition()));
            topFishSpecies[2] = String.valueOf(third_name.get(holder.getAdapterPosition()));

            String[] topConfidences = new String[3];
            topConfidences[0] = String.valueOf(first_conf.get(holder.getAdapterPosition()));
            topConfidences[1] = String.valueOf(second_conf.get(holder.getAdapterPosition()));
            topConfidences[2] = String.valueOf(third_conf.get(holder.getAdapterPosition()));

            Bundle args = new Bundle();
            args.putString("imagePath", imagePath);
            args.putString("uri", imageURI1);
            args.putStringArray("topFishSpecies", topFishSpecies);
            args.putStringArray("topConfidences", topConfidences);
            args.putBoolean("Saved", true);
            logBundleContents(args);

            OutputFragment outputFragment = new OutputFragment();
            outputFragment.setArguments(args);

            ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, outputFragment, "OutputFragment")
                    .addToBackStack("OutputFragment")
                    .commit();
        });

    }

    private void logBundleContents(Bundle args) {
        for (String key : args.keySet()) {
            Object value = args.get(key);
            Log.d("BundleCon_Collection", key + ": " + value);
        }
    }

    @Override
    public int getItemCount() {
        Log.d("IDsize", String.valueOf(id.size()));
        return id.size();
    }

    public static class CollectionsHolder extends RecyclerView.ViewHolder {

        TextView first_name, dateTaken;
        ImageView imageView;

        public CollectionsHolder(@NonNull View itemView) {
            super(itemView);

            first_name = itemView.findViewById(R.id.fileName);
            dateTaken = itemView.findViewById(R.id.dateTaken);
            imageView = itemView.findViewById(R.id.collectionImage);

        }
    }
}

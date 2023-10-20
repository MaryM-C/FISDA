package com.ccs114.fisda;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.CollectionsHolder>{

    private Context context;
    private ArrayList id, filename, date_taken, image_path, first_name, second_name, third_name, first_conf, second_conf, third_conf;

    CollectionsAdapter (Context context, ArrayList id, ArrayList filename, ArrayList date_taken, ArrayList image_path) {
        this.context = context;
        this.id = id;
        this.filename = filename;
        this.date_taken = date_taken;
        this.image_path = image_path;
    }

    @NonNull
    @Override
    public CollectionsAdapter.CollectionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CollectionsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_collections, null));
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionsHolder holder, int position) {
        holder.fileName.setText(String.valueOf(filename.get(position)));
        holder.dateTaken.setText(String.valueOf(date_taken.get(position)));
        Uri imageUri = Uri.parse("file://" + image_path.get(position));  // Convert to Uri
       try {
           Picasso.get()
                   .load(imageUri)
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
           Log.d("Collection", "Failed");
       }

    }

    @Override
    public int getItemCount() {
        Log.d("IDsize", String.valueOf(id.size()));
        return id.size();
    }

    public class CollectionsHolder extends RecyclerView.ViewHolder{

        TextView fileName, dateTaken;
        ImageView imageView;
        public CollectionsHolder(@NonNull View itemView) {
            super(itemView);

            fileName = itemView.findViewById(R.id.fileName);
            dateTaken = itemView.findViewById(R.id.dateTaken);
            imageView = itemView.findViewById(R.id.collectionImage);

        }
    }

    private Bitmap createBitmapFromPath(String filePath) {
        Log.d("FilePath" ,filePath);
        BitmapFactory.Options bmoptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmoptions);
        bitmap = Bitmap.createScaledBitmap(bitmap, 150, 163, true);

        if (bitmap == null) {
            //TODO: Add what to do if there is an error
            Log.d("Picture", "Bitmap is empty");
        }
        return bitmap;
    }
}

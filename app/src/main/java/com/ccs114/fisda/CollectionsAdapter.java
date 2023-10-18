package com.ccs114.fisda;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.CollectionsHolder>{

    private Context context;
    private ArrayList id, filename, data_taken, image_path, first_name, second_name, third_name, first_conf, second_conf, third_conf;

    CollectionsAdapter (Context context, ArrayList id, ArrayList filename, ArrayList data_taken, ArrayList image_path) {
        this.context = context;
        this.filename = filename;
        this.data_taken = data_taken;
        this.image_path = image_path;
    }

    @NonNull
    @Override
    public CollectionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CollectionsAdapter.CollectionsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_collections, null));
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionsHolder holder, int position) {
        holder.fileName.setText(String.valueOf(filename.get(position)));
        holder.dateTaken.setText(String.valueOf(data_taken.get(position)));
        holder.imageView.setImageBitmap(createBitmapFromPath(String.valueOf(image_path.get(position))));

    }

    @Override
    public int getItemCount() {
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
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (bitmap == null) {
            //TODO: Add what to do if there is an error
        }
        return bitmap;
    }
}

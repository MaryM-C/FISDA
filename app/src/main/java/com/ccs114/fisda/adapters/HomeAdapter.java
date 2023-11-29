package com.ccs114.fisda.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ccs114.fisda.fragments.InformationFragment;
import com.ccs114.fisda.models.HomeItems;
import com.ccs114.fisda.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {


    private final List<HomeItems> items; //item in array  list
    private final Context context; //context

    //constructor
    public HomeAdapter(List<HomeItems> items, Context context) {
        this.items = items;
        this.context = context;

    }

    @NonNull
    @Override
    public HomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.MyViewHolder holder, int position) {
        //getting single item/ fish details from list
        HomeItems myItems = items.get(position);
        String image =items.get(position).getImage();

        //setting user details to textviews
        holder.localName.setText(myItems.getLocalName());
        holder.commonName.setText(myItems.getCommonName());
        holder.category.setText(myItems.getCategory());


        //load the image using picaso from the web
        Picasso.get().load(image).into(holder.image);


        holder.image.setOnClickListener(view -> {
            Intent intent = new Intent(context,  InformationFragment.class);

            String commonName = myItems.getCommonName();
            intent.putExtra("commonname", commonName);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    //MyViewHolder class will hold view reference for every items in the recycler view
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //declaring 3 textviews
        ImageView image;
        private final TextView commonName, localName, category;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //getting textview from recyler_adapter_layout.xml
            commonName= itemView.findViewById(R.id.commonName);
            localName=itemView.findViewById(R.id.localName);
            category=itemView.findViewById(R.id.category);
            image=itemView.findViewById(R.id.imgViewFish);

        }

    }
}

package com.ccs114.fisda;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    private final List<MyItems> items; //item in array  list
    private final Context context; //context

    //constructor
    public MyAdapter(List<MyItems> items, Context context) {
        this.items = items;
        this.context = context;

    }



    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        //getting single item/ fish details from list
        MyItems myItems = items.get(position);
        String image =items.get(position).getImage();

        //setting user details to textviews
        holder.localName.setText(myItems.getLocalName());
        holder.commonName.setText(myItems.getCommonName());
        holder.category.setText(myItems.getCategory());



        try{
            //load the image using picaso from the web
            Picasso.get().load(image).resize(250, 250).into(holder.image);
        }
        catch (Exception e){

        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view) {
                Intent intent = new Intent(context,  InformationFragment.class);

                String commonName = myItems.getCommonName();
                String localName = myItems.getLocalName();
                String category = myItems.getCategory();
                String image = myItems.getImage();

                intent.putExtra("commonname", commonName);
                intent.putExtra("localname", localName);
                intent.putExtra("category", category);
                intent.putExtra("image", image);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }



        });


    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    //MyViewHolder class will hold view reference for every items in the recycler view
    static class MyViewHolder extends RecyclerView.ViewHolder {
        //declaring 3 textviews
        ImageView image;
        private final TextView lblCommonName, commonName, localName, category;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //getting textview from recyler_adapter_layout.xml
            commonName= itemView.findViewById(R.id.commonName);
            localName=itemView.findViewById(R.id.localName);
            category=itemView.findViewById(R.id.category);
            image=itemView.findViewById(R.id.image);
            lblCommonName=itemView.findViewById(R.id.lblCommonName);
;

        }

    }
}

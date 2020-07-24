package com.amansiol.messenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amansiol.messenger.models.Images;
import com.amansiol.messenger.models.PostModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class imagesadapter extends RecyclerView.Adapter<imagesadapter.imageviewholder>{
    Context ctx;
    List<Images> imageslist;
    int [] hgt={400,450,500,550,600};

    public imagesadapter(Context ctx, List<Images> imageslist) {
        this.ctx = ctx;
        this.imageslist = imageslist;
    }

    @NonNull
    @Override
    public imageviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(ctx).inflate(R.layout.staggeredpost,parent,false);
        return new imageviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull imageviewholder holder, int position) {

        String postpic=imageslist.get(position).getPimage();
        double h=getRandomIntegerBetweenRange(0,4);

        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,hgt[(int) h]);
        holder.image.setLayoutParams(parms);

        Picasso.get().load(postpic).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return imageslist.size();
    }

    public static double getRandomIntegerBetweenRange(double min, double max){
        double x = (int)(Math.random()*((max-min)+1))+min;
        return x;
    }

    class imageviewholder extends RecyclerView.ViewHolder{
            ImageView image;
        public imageviewholder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.starggerdimage);
        }
    }
}

package com.amansiol.messenger;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.github.siyamed.shapeimageview.HeartImageView;

public class AllUsers_ViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView status;
    CircularImageView image;

    public AllUsers_ViewHolder(@NonNull View itemView) {
        super(itemView);
       name=itemView.findViewById(R.id.allusers_name);
       status=itemView.findViewById(R.id.allusers_status);
       image=itemView.findViewById(R.id.allusers_pic);
    }

}

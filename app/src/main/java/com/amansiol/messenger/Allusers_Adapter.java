package com.amansiol.messenger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amansiol.messenger.models.Allusers_models;
import com.firebase.ui.auth.data.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Allusers_Adapter extends RecyclerView.Adapter<AllUsers_ViewHolder> {
    Context context;
    List<Allusers_models> Users_list;

    public Allusers_Adapter(Context context, List<Allusers_models> users_list) {
        this.context = context;
        Users_list = users_list;
    }

    @NonNull
    @Override
    public AllUsers_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_row,parent,false);
        return new AllUsers_ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull AllUsers_ViewHolder holder, int i) {
        final String his_UID= Users_list.get(i).getUID();
        final String user_name=Users_list.get(i).getName();
        final String user_image=Users_list.get(i).getImage();
        holder.name.setText(Users_list.get(i).getName());
        holder.status.setText(Users_list.get(i).getStatus());
        try{
            Picasso.get().load(Users_list.get(i).getImage()).into(holder.image);
        }
        catch (Exception e){

        }
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           Intent intent=new Intent(context,ChatActivity.class);
           intent.putExtra("hisUID",his_UID);
           context.startActivity(intent);
        }
    });
    holder.image.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder image=new AlertDialog.Builder(context);
            View dialog=LayoutInflater.from(context).inflate(R.layout.showuserimage,null);
            ImageView imageView=dialog.findViewById(R.id.dialog_user_image);
            try{
                Picasso.get().load(user_image).into(imageView);
            }catch (Exception e){
                Picasso.get().load(R.drawable.profilepic).into(imageView);
            }

            image.setView(dialog);
            image.setCancelable(true);
            image.create().show();
        }
    });
    }

    @Override
    public int getItemCount() {
        return Users_list.size();
    }
}

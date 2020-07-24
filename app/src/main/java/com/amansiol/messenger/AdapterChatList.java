package com.amansiol.messenger;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amansiol.messenger.models.Allusers_models;
import com.amansiol.messenger.models.ChatModel;
import com.amansiol.messenger.models.ChatlistModel;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.ChatListViewHolder> {

    List<Allusers_models> chatlist;
    Context context;
    private HashMap<String,String> lastMessageMap;

    public AdapterChatList( Context context,List<Allusers_models> chatlist) {
        this.chatlist = chatlist;
        this.context = context;
        lastMessageMap=new HashMap<>();
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.chatlist_row,parent,false);
        return new ChatListViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {

                final String hisUid=chatlist.get(position).getUID();
                final String userImage=chatlist.get(position).getImage();
                String userName=chatlist.get(position).getName();
                String lastMessage=lastMessageMap.get(hisUid);


                holder.chatlist_name.setText(userName);



        if(lastMessage==null||lastMessage.equals("default")){
            holder.chatlist_lastmessage.setVisibility(View.GONE);

        }else {

            if(lastMessage.length()>27)
            {
                holder.chatlist_lastmessage.setText(lastMessage.substring(0,27)+"...");
                holder.chatlist_lastmessage.setVisibility(View.VISIBLE);
            }else {
                holder.chatlist_lastmessage.setText(lastMessage);
                holder.chatlist_lastmessage.setVisibility(View.VISIBLE);
            }

        }
        try{
            Picasso.get().load(userImage).into(holder.chatlist_pic);

        }catch (Exception e){
            Picasso.get().load(R.drawable.profilepic).into(holder.chatlist_pic);
        }
     if(chatlist.get(position).getOnlineStatus().equals("online")){
         holder.chatlist_online.setText("online");
         holder.chatlist_online.setVisibility(View.VISIBLE);
     }else {
         holder.chatlist_online.setVisibility(View.GONE);
     }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ChatActivity.class);
                intent.putExtra("hisUID",hisUid);
                context.startActivity(intent);
            }
        });
        holder.chatlist_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder image=new AlertDialog.Builder(context);
                View dialog=LayoutInflater.from(context).inflate(R.layout.showuserimage,null);
                ImageView imageView=dialog.findViewById(R.id.dialog_user_image);
                try{
                    Picasso.get().load(userImage).into(imageView);
                }catch (Exception e){
                    Picasso.get().load(R.drawable.profilepic).into(imageView);
                }

                image.setView(dialog);
                image.setCancelable(true);
                image.create().show();
            }
        });

    }
    public void setLastMessageMap(String userId,String message){
        lastMessageMap.put(userId,message);
    }

    @Override
    public int getItemCount() {
        return chatlist.size();
    }


    class ChatListViewHolder extends RecyclerView.ViewHolder{

        TextView chatlist_name,chatlist_lastmessage,chatlist_online;
        CircularImageView chatlist_pic;
        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            chatlist_name=itemView.findViewById(R.id.chatlist_name);
            chatlist_lastmessage=itemView.findViewById(R.id.chatlist_lastmessage);
            chatlist_online=itemView.findViewById(R.id.chatlist_online);
            chatlist_pic=itemView.findViewById(R.id.chatlist_pic);
        }
    }
}

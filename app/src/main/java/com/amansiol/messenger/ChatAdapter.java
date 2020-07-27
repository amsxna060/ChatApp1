package com.amansiol.messenger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Animatable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.amansiol.messenger.models.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {
  private final static int CHAT_LEFT=0;
  private final static int CHAT_RIGHT=1;
  Context ctx;
  String hisimage;
  String myimage;
  List<ChatModel> chatList;
  boolean time=false;

    public ChatAdapter(Context ctx, List<ChatModel> chatlist, String hisimage,String myimage) {
        this.ctx = ctx;
        chatList = chatlist;
        this.hisimage = hisimage;
        this.myimage=myimage;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==CHAT_LEFT)
        {
            View v= LayoutInflater.from(ctx).inflate(R.layout.sender_chat,parent,false);
            return new ChatHolder(v);
        }
        else if (viewType==CHAT_RIGHT){
            View v= LayoutInflater.from(ctx).inflate(R.layout.receiver_chat,parent,false);
            return new ChatHolder(v);
        }
        else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatHolder holder, final int position) {

        final String message=chatList.get(position).getMessage();
        String timestamp=chatList.get(position).getTimestamp();
        Calendar cal= Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        final String date= DateFormat.format("hh:mm aa",cal).toString();
        holder.chat_msg.setText(message);
        holder.timestamp.setText(date);
        FirebaseUser muser=FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(muser.getUid())){

            try{
                Picasso.get().load(myimage).into(holder.chat_pic);

            }catch (Exception e){
                Picasso.get().load(R.drawable.profilepic).into(holder.chat_pic);
            }
        }else {

            try{
                Picasso.get().load(hisimage).into(holder.chat_pic);

            }catch (Exception e){
                Picasso.get().load(R.drawable.profilepic).into(holder.chat_pic);
            }
        }
        holder.messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!time){
                    time=true;
                    holder.timestamp.setVisibility(View.VISIBLE);

                }else {
                    holder.timestamp.setVisibility(View.GONE);
                    time=false;
                }

            }
        });

        if(position==chatList.size()-1){
            if(chatList.get(position).isIsseen()){
                holder.seenlistener.setVisibility(View.VISIBLE);
                holder.seenlistener.setText("Seen");
            }else {
                holder.seenlistener.setVisibility(View.VISIBLE);
                holder.seenlistener.setText("Delivered");
            }
        }else {
            holder.seenlistener.setVisibility(View.GONE);
        }
         holder.chat_msg.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 AlertDialog.Builder builder=new AlertDialog.Builder(ctx);
                 builder.setCancelable(true);
                 String items[]={"Unsend Message"};

                 builder.setItems(items, new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         delete(position);
                     }
                 });
                 builder.create().show();
             }
         });

//        Animation animation= AnimationUtils.loadAnimation(ctx,R.anim.animate_windmill_enter);
//        holder.itemView.startAnimation(animation);


    }

    private void delete(int i) {
        final String myUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        String msgtimestamp=chatList.get(i).getTimestamp();
        DatabaseReference mydbref= FirebaseDatabase.getInstance().getReference("Chats");
        Query query=mydbref.orderByChild("timestamp").equalTo(msgtimestamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.child("sender").getValue().equals(myUid)){
                          ds.getRef().removeValue();
                    }else {
                        Toast.makeText(ctx,"Unsend Only Your Message",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder{
       TextView chat_msg,timestamp;
       ImageView chat_pic;
       RelativeLayout messageLayout;
       TextView seenlistener;

      public ChatHolder(@NonNull View itemView) {
          super(itemView);
          chat_msg=itemView.findViewById(R.id.chat_msg);
          timestamp=itemView.findViewById(R.id.timestamp);
          chat_pic=itemView.findViewById(R.id.chat_pic);
          messageLayout=itemView.findViewById(R.id.messageLayout);
          seenlistener=itemView.findViewById(R.id.seenlistener);

      }
  }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(user.getUid())){
            return CHAT_RIGHT;
        }else {
            return CHAT_LEFT;
        }
    }
}

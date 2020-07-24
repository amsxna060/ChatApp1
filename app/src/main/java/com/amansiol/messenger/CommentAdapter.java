package com.amansiol.messenger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amansiol.messenger.models.CommentModel;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    Context context;
    List<CommentModel> CommentList;
    String postId;

    public CommentAdapter(Context context, List<CommentModel> commentList, String postId) {
        this.context = context;
        CommentList = commentList;
        this.postId = postId;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.commentrow,parent,false);
        return new CommentHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {

        final String Cid=CommentList.get(position).getcId();
        String Ctime=CommentList.get(position).getTimestamp();
        String Cusername=CommentList.get(position).getuName();
        String Cuserpic=CommentList.get(position).getuDp();
        String Comment=CommentList.get(position).getComment();
        final String uid=CommentList.get(position).getUid();

        try {
            Picasso.get().load(Cuserpic).into(holder.mypic_cmtshow);
        }catch (Exception e)
        {
            Picasso.get().load(R.drawable.profilepic).into(holder.mypic_cmtshow);
        }
        holder.nameusercmt.setText(Cusername);
        Calendar cal= Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(Ctime));
        final String date= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
        holder.commenttime.setText(date);
        holder.textboxcomments.setText(Comment);
        Animation cmtanimation= AnimationUtils.loadAnimation(context,R.anim.animate_split_enter);
        holder.itemView.startAnimation(cmtanimation);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uid)){
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure to delete this Comment?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference ref= FirebaseDatabase.getInstance()
                                    .getReference("Posts")
                                    .child(postId)
                                    .child("Comments");
                            ref.child(Cid).removeValue();
                            final DatabaseReference inc_cmt=FirebaseDatabase.getInstance()
                                    .getReference("Posts")
                                    .child(postId);
                            inc_cmt.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String comments=""+snapshot.child("comments").getValue();
                                    int newcmtval=Integer.parseInt(comments)-1;
                                    inc_cmt.child("comments").setValue(""+newcmtval);


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    });
                    builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return CommentList.size();
    }


    class CommentHolder extends RecyclerView.ViewHolder{

        TextView nameusercmt,textboxcomments,commenttime;
        CircularImageView mypic_cmtshow;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            nameusercmt=itemView.findViewById(R.id.nameusercmt);
            textboxcomments=itemView.findViewById(R.id.textboxcomments);
            commenttime=itemView.findViewById(R.id.commenttime);
            mypic_cmtshow=itemView.findViewById(R.id.mypic_cmtshow);

        }
    }
}

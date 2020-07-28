package com.amansiol.messenger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.amansiol.messenger.models.PostModel;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.load.resource.transcode.DrawableBytesTranscoder;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    Context ctx;
    List<PostModel> postModels;
    private DatabaseReference likesRef;
    private  DatabaseReference postsRef;
    boolean mProcessLike=false;
    String cmtusername;
    String cmtuserpic;
    String cmtuseremail;
    String cmtuseruid;
    boolean mProcessCmt=false;
    private int ani=0;

    public PostAdapter(Context ctx, List<PostModel> postModels) {
        this.ctx = ctx;
        this.postModels = postModels;
        likesRef=FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef=FirebaseDatabase.getInstance().getReference().child("Posts");

    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(ctx).inflate(R.layout.post_row,parent,false);
        return new PostHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostHolder holder, int i) {

        final String desc=postModels.get(i).getPdesc();
        String name=postModels.get(i).getPname();
        String icon=postModels.get(i).getPuserimage();
        String postpic=postModels.get(i).getPimage();
        String likes=postModels.get(i).getLikes();
        final int position=i;

        holder.post_his_name.setText(name);

        try {
            Picasso.get().load(icon).into(holder.post_his_pic);
        }catch (Exception e)
        {
            Picasso.get().load(R.drawable.profilepic).into(holder.post_his_pic);
        }
        if(!desc.equals("noDescription")){
            holder.read_thought.setVisibility(View.VISIBLE);
            holder.read_thought.setText(desc);
        }else {
            holder.read_thought.setVisibility(View.GONE);
        }

        if(postpic.equals("noImage")){
            holder.post_add_pic.setVisibility(View.GONE);
        }else {
            try {
                holder.post_add_pic.setVisibility(View.VISIBLE);
                Picasso.get().load(postpic).into(holder.post_add_pic);
            }catch (Exception e)
            {
                holder.post_add_pic.setVisibility(View.GONE);
            }
        }

        holder.option_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu menu=new PopupMenu(ctx,holder.option_menu, Gravity.END);
                if(postModels.get(position).getPuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    menu.getMenu().add(Menu.NONE,0,0,"Delete");
                    menu.getMenu().add(Menu.NONE,1,0,"Edit Post");
                }
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id=item.getItemId();
                        if(id==0)
                        {
                            deletePost(postModels.get(position).getPtimestamp(),postModels.get(position).getPimage(),postModels.get(position).getPdesc(),position);
                        }else if(id==1)
                        {
                            Intent intent=new Intent(ctx,EditPostActivity.class);
                            intent.putExtra("timestamp",postModels.get(position).getPtimestamp());
                            intent.putExtra("proname",postModels.get(position).getPname());
                            intent.putExtra("proimage",postModels.get(position).getPuserimage());
                            intent.putExtra("postimage",postModels.get(position).getPimage());
                            intent.putExtra("postdesc",postModels.get(position).getPdesc());
                            intent.putExtra("prouid",postModels.get(position).getPuid());
                            ctx.startActivity(intent);
                            Animatoo.animateSplit(ctx);
                        }
                        return false;
                    }
                });
                menu.show();
            }
        });
        holder.love_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String myUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                final int plikes= Integer.parseInt(postModels.get(position).getLikes());
                mProcessLike=true;
                final String postId=postModels.get(position).getPtimestamp();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(mProcessLike){
                            if(snapshot.child(postId).hasChild(myUid)){
                                postsRef.child(postId).child("likes").setValue(""+(plikes-1));
                                likesRef.child(postId).child(myUid).removeValue();
                                mProcessLike=false;
                            }else {

                                postsRef.child(postId).child("likes").setValue(""+(plikes+1));
                                likesRef.child(postId).child(myUid).setValue("Liked");
                                mProcessLike=false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        holder.post_add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ani<1){
                    ani++;
                    return;
                }
                holder.picLIkeheart.setAlpha(0.85f);
                Drawable d=holder.picLIkeheart.getDrawable();
                if(d instanceof AnimatedVectorDrawableCompat){
                    AnimatedVectorDrawableCompat drawableCompat=(AnimatedVectorDrawableCompat)d;
                    drawableCompat.start();
                }else if(d instanceof AnimatedVectorDrawable){
                    AnimatedVectorDrawable drawable=(AnimatedVectorDrawable)d;
                    drawable.start();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final String myUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        final int plikes= Integer.parseInt(postModels.get(position).getLikes());
                        mProcessLike=true;
                        final String postId=postModels.get(position).getPtimestamp();
                        likesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(mProcessLike){
                                    if(snapshot.child(postId).hasChild(myUid)){
                                        postsRef.child(postId).child("likes").setValue(""+(plikes-1));
                                        likesRef.child(postId).child(myUid).removeValue();
                                        mProcessLike=false;
                                    }else {

                                        postsRef.child(postId).child("likes").setValue(""+(plikes+1));
                                        likesRef.child(postId).child(myUid).setValue("Liked");
                                        mProcessLike=false;

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                },3000);


            }
        });
        holder.comment_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.commentbox.setVisibility(View.VISIBLE);
                Animation slidedown=AnimationUtils.loadAnimation(ctx,R.anim.animate_in_out_enter);
                holder.commentbox.startAnimation(slidedown);
            }
        });
        holder.comment_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ctx,CommentActivity.class);
                intent.putExtra("PostId",postModels.get(position).getPtimestamp());
                ctx.startActivity(intent);
                Animatoo.animateSplit(ctx);
            }
        });
        holder.share_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable sharepic=(BitmapDrawable)holder.post_add_pic.getDrawable();
                if(sharepic==null)
                {
                    ShareTextOnly(desc);

                }else {
                    Bitmap bitmap=sharepic.getBitmap();
                    SharetextnPic(desc,bitmap);
                }
            }
        });
        if(likes.equals("0")){
            holder.love_count.setText("Be the first to like🙂");
        }else {
            holder.love_count.setText(likes+" likes");
        }
        if(postModels.get(i).getComments().equals("0")){
            holder.comment_count.setText("Be the First one to Comment");
        }else {
            holder.comment_count.setText("View All "+postModels.get(i).getComments()+" Comments");
        }


         final String posttime=postModels.get(i).getPtimestamp();
        Calendar cal= Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(posttime));
        final String date= DateFormat.format("hh:mm aa",cal).toString();

        holder.post_time.setText("at "+date);
        setLikes(holder,posttime,position);

        FirebaseUser muser= FirebaseAuth.getInstance().getCurrentUser();
        cmtuseremail=muser.getEmail();
        cmtuseruid=muser.getUid();
        DatabaseReference myref=FirebaseDatabase.getInstance().getReference("Users");
        Query query=myref.orderByChild("UID").equalTo(muser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                     cmtusername=""+ds.child("name").getValue();
                   cmtuserpic =""+ds.child("image").getValue();
                    holder.editTextcmt.setHint("Comment as "+cmtusername+"...");
                    try {
                        Picasso.get().load(cmtuserpic).into(holder.mypiccomment);
                    }catch (Exception e)
                    {
                        Picasso.get().load(R.drawable.profilepic).into(holder.mypiccomment);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.commentsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.editTextcmt.getText().toString().isEmpty()){
                    Toast.makeText(ctx,"comment is empty",Toast.LENGTH_LONG).show();
                    return;
                }else {
                    InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    //Find the currently focused view, so we can grab the correct window token from it.
                    imm.hideSoftInputFromWindow(holder.itemView.getRootView().getWindowToken(), 0);
                    String cmttimestamp=String.valueOf(System.currentTimeMillis());
                    DatabaseReference cmtref=FirebaseDatabase.getInstance().getReference("Posts").child(posttime).child("Comments");
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("cId",cmttimestamp);
                    hashMap.put("comment",holder.editTextcmt.getText().toString());
                    hashMap.put("timestamp",cmttimestamp);
                    hashMap.put("uid",cmtuseruid);
                    hashMap.put("uEmail",cmtuseremail);
                    hashMap.put("uDp",cmtuserpic);
                    hashMap.put("uName",cmtusername);

                    cmtref.child(cmttimestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        holder.editTextcmt.setText("");
                        updateCommentCount(posttime);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ctx,""+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });


    }

    private void SharetextnPic(String desc, Bitmap bitmap) {
        Uri uri=savePic(bitmap);
        Intent Sharepictext=new Intent(Intent.ACTION_SEND);
        Sharepictext.putExtra(Intent.EXTRA_STREAM,uri);
        if(!desc.equals("noDescription")){
            Sharepictext.putExtra(Intent.EXTRA_TEXT,desc);
        }
        Sharepictext.putExtra(Intent.EXTRA_SUBJECT,"Subject here");
        Sharepictext.setType("image/png");
        ctx.startActivity(Intent.createChooser(Sharepictext,"Share Via"));


    }


    private Uri savePic(Bitmap bitmap) {
        Uri uri=null;
        File imageFolder=new File(ctx.getCacheDir(),"images");
        try {
            imageFolder.mkdirs();
            File file=new File(imageFolder,"share_image.png");
            FileOutputStream stream =new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
            stream.flush();
            stream.close();
            uri= FileProvider.getUriForFile(ctx,ctx.getPackageName() + ".provider",file);


        }catch (Exception e){
            Toast.makeText(ctx,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private void ShareTextOnly(String desc) {
        Intent sharetext=new Intent(Intent.ACTION_SEND);
        sharetext.setType("text/plain");
        sharetext.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        sharetext.putExtra(Intent.EXTRA_TEXT,desc);
        ctx.startActivity(Intent.createChooser(sharetext,"Share via"));
    }

    private void updateCommentCount(String posttime) {
        mProcessCmt=true;
        final DatabaseReference inc_cmt=FirebaseDatabase.getInstance()
                .getReference("Posts")
                .child(posttime);
        inc_cmt.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mProcessCmt){
                    String comments=""+snapshot.child("comments").getValue();
                    int newcmtval=Integer.parseInt(comments)+1;
                    inc_cmt.child("comments").setValue(""+newcmtval);
                    mProcessCmt=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLikes(final PostHolder holder, final String posttime, final int position) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(posttime).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    holder.love_icon.setImageResource(R.drawable.filllittleheart);
                    Drawable d=holder.love_icon.getDrawable();
                    if(d instanceof AnimatedVectorDrawableCompat){
                        AnimatedVectorDrawableCompat drawableCompat=(AnimatedVectorDrawableCompat)d;
                        drawableCompat.start();
                    }else if(d instanceof AnimatedVectorDrawable){
                        AnimatedVectorDrawable drawable=(AnimatedVectorDrawable)d;
                        drawable.start();
                    }
                }else{
                    holder.love_icon.setImageResource(R.drawable.littleheart);
                    Drawable d=holder.love_icon.getDrawable();
                    if(d instanceof AnimatedVectorDrawableCompat){
                        AnimatedVectorDrawableCompat drawableCompat=(AnimatedVectorDrawableCompat)d;
                        drawableCompat.start();
                    }else if(d instanceof AnimatedVectorDrawable){
                        AnimatedVectorDrawable drawable=(AnimatedVectorDrawable)d;
                        drawable.start();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deletePost(final String ptimestamp, final String pimage, String desc, final int position) {
        final ProgressDialog pd=new ProgressDialog(ctx);

         if(pimage.equals("noImage")){
             pd.setMessage("Deleting....");
             pd.show();
             Query query=FirebaseDatabase.getInstance().getReference("Posts").orderByChild("ptimestamp").equalTo(ptimestamp);
             query.addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                     for(DataSnapshot ds:snapshot.getChildren()){
                         ds.getRef().removeValue();

                     }
                     pd.dismiss();
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error) {

                 }
             });
         }else
             if(!pimage.equals("noImage")&&desc.equals("noDescription")){
                 pd.setMessage("Deleting....");
                 pd.show();
                 StorageReference storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(pimage);
                 storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {
                         //image deleted
                         Query query=FirebaseDatabase.getInstance().getReference("Posts").orderByChild("ptimestamp").equalTo(ptimestamp);
                         query.addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                 for(DataSnapshot ds:snapshot.getChildren()){
                                     ds.getRef().removeValue();

                                 }
                                 pd.dismiss();
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError error) {

                             }
                         });
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         pd.dismiss();
                         Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
                     }
                 });

         }else
             if(!pimage.equals("noImage")&&!desc.equals("noDescription")){

                 pd.setMessage("Deleting....");
                 pd.show();
                 StorageReference storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(pimage);
                 storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {
                         //image deleted
                         Query query=FirebaseDatabase.getInstance().getReference("Posts").orderByChild("ptimestamp").equalTo(ptimestamp);
                         query.addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds:snapshot.getChildren()){
                                    ds.getRef().removeValue();

                                }
                                pd.dismiss();
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError error) {

                             }
                         });
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                       pd.dismiss();
                       Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
                     }
                 });


             }
    }

    @Override
    public int getItemCount() {
        return postModels.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{
        TextView post_his_name,read_thought,love_count,comment_count,post_time;
        CircularImageView post_his_pic;
        ImageView option_menu,post_add_pic,love_icon,comment_icon,share_icon,mypiccomment,picLIkeheart;
        ImageButton commentsend;
        EditText editTextcmt;
        RelativeLayout commentbox;


        public PostHolder(@NonNull View itemView) {
            super(itemView);
            post_his_name=itemView.findViewById(R.id.post_his_name);
            read_thought=itemView.findViewById(R.id.read_thought);
            love_count=itemView.findViewById(R.id.love_count);
            comment_count=itemView.findViewById(R.id.comment_count);
            post_time=itemView.findViewById(R.id.post_time);
            post_his_pic=itemView.findViewById(R.id.post_his_pic);
            option_menu=itemView.findViewById(R.id.option_menu);
            post_add_pic=itemView.findViewById(R.id.post_add_pic);
            love_icon=itemView.findViewById(R.id.love_icon);
            comment_icon=itemView.findViewById(R.id.comment_icon);
            share_icon=itemView.findViewById(R.id.share_icon);
            commentsend=itemView.findViewById(R.id.send_comments);
            editTextcmt=itemView.findViewById(R.id.editboxcomments);
            mypiccomment=itemView.findViewById(R.id.mypic_cmt);
            commentbox=itemView.findViewById(R.id.commentbox);
            picLIkeheart=itemView.findViewById(R.id.piclikeheart);



        }
    }
}

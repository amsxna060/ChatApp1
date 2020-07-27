package com.amansiol.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.amansiol.messenger.models.PostModel;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OtherProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ImageView ocover_pic;
    TextView ouser_name;
    TextView ouser_status;
    TextView ouser_email;
    TextView ouser_number;
    TextView ouser_verified;
    TextView ouser_add;
    CircularImageView ouser_profile_pic;
    TextView ouser_hobby1;
    TextView ouser_hobby2;
    TextView ouser_hobby3;
    TextView ouser_gender;
    TextView ouser_dob;
    String dialog_image_url;
    FloatingActionButton fab;
    String suser_isverified;
    String myUid;
    PostAdapter postAdapter;
    List<PostModel> postList;
    RecyclerView mypostrecycyler;
    String hisUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        //init
        ouser_name=findViewById(R.id.ouser_name);
        ouser_status=findViewById(R.id.ouser_status);
        ouser_email=findViewById(R.id.ouser_email);
        ouser_number=findViewById(R.id.ouser_number);
        ouser_verified=findViewById(R.id.ois_user_verified);
        ouser_add=findViewById(R.id.ouser_add);
        ouser_hobby1=findViewById(R.id.ouser_hobby1);
        ouser_hobby2=findViewById(R.id.ouser_hobby2);
        ouser_hobby3=findViewById(R.id.ouser_hobby3);
        ouser_profile_pic=findViewById(R.id.ouser_profile_pic);
        ocover_pic=findViewById(R.id.ocover_pic);
        ouser_gender=findViewById(R.id.ouser_gender);
        ouser_dob=findViewById(R.id.ouser_dob);
        mypostrecycyler=findViewById(R.id.onlyotherpost);
        //init firebase essentials
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        Intent intent=getIntent();
        hisUid=intent.getStringExtra("hisUID");
        databaseReference=firebaseDatabase.getReference("Users");
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        mypostrecycyler.setHasFixedSize(true);
        mypostrecycyler.setLayoutManager(linearLayoutManager);

        if(firebaseUser!=null){
            myUid=firebaseUser.getUid();
        }

        //query by Uid
        Query query=databaseReference.orderByChild("UID").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds :snapshot.getChildren())
                {
                    String scover_pic=""+ds.child("coverimage").getValue();
                    String suser_email=""+ds.child("email").getValue();
                    String suser_Uid=""+ds.child("UID").getValue();
                    String suser_name=""+ds.child("name").getValue();
                    String suser_dob=""+ds.child("dob").getValue();
                    String suser_number=""+ds.child("number").getValue();
                    String suser_profile_pic=""+ds.child("image").getValue();
                    String suser_status=""+ds.child("status").getValue();
                    suser_isverified=""+ds.child("isverified").getValue();
                    String suser_address=""+ds.child("address").getValue();
                    String suser_hobby1=""+ds.child("hobby1").getValue();
                    String suser_hobby2=""+ds.child("hobby2").getValue();
                    String suser_hobby3=""+ds.child("hobby3").getValue();
                    String suser_gender=""+ds.child("gender").getValue();
                    dialog_image_url=suser_profile_pic;
                    ouser_name.setText(suser_name);
                    ouser_email.setText(suser_email);
                    ouser_status.setText(suser_status);
                    ouser_number.setText(suser_number);
                    ouser_verified.setText(suser_isverified);
                    ouser_add.setText(suser_address);
                    ouser_dob.setText(suser_dob);
                    ouser_hobby1.setText(suser_hobby1);
                    ouser_hobby2.setText(suser_hobby2);
                    ouser_hobby3.setText(suser_hobby3);
                    ouser_gender.setText(suser_gender);
                    try {
                        Picasso.get().load(suser_profile_pic).into(ouser_profile_pic);
                    }catch (Exception e)
                    {
                        Picasso.get().load(R.drawable.profilepic).into(ouser_profile_pic);
                    }
                    try {
                        Picasso.get().load(scover_pic).into(ocover_pic);
                    }catch (Exception e)
                    {
                        Picasso.get().load(R.drawable.cover).into(ocover_pic);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        postList=new ArrayList<>();
        loadPost();
    }

    private void loadPost() {
        DatabaseReference chatdbref=FirebaseDatabase.getInstance().getReference("Posts");
        chatdbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    PostModel post=ds.getValue(PostModel.class);
                    if(post.getPuid().equals(hisUid))
                    {
                        postList.add(post);
                    }

                    postAdapter=new PostAdapter(OtherProfileActivity.this,postList);
                    mypostrecycyler.setAdapter(postAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
package com.amansiol.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.amansiol.messenger.models.PostModel;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OtherProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
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
        Intent intent=getIntent();
        hisUid=intent.getStringExtra("hisUID");
        db=FirebaseFirestore.getInstance();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        mypostrecycyler.setHasFixedSize(true);
        mypostrecycyler.setLayoutManager(linearLayoutManager);

        if(firebaseUser!=null){
            myUid=firebaseUser.getUid();
        }

        //query by Uid
        db.collection("Users").
                whereEqualTo("UID",firebaseUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot ds : queryDocumentSnapshots)
                        {

                            String scover_pic=""+ds.get("coverimage");
                            String suser_email=""+ds.get("email");
                            String suser_Uid=""+ds.get("UID");
                            String suser_name=""+ds.get("name");
                            String suser_dob=""+ds.get("dob");
                            String suser_number=""+ds.get("number");
                            String suser_profile_pic=""+ds.get("image");
                            String suser_status=""+ds.get("status");
                            suser_isverified=""+ds.get("isverified");
                            String suser_address=""+ds.get("address");
                            String suser_hobby1=""+ds.get("hobby1");
                            String suser_hobby2=""+ds.get("hobby2");
                            String suser_hobby3=""+ds.get("hobby3");
                            String suser_gender=""+ds.get("gender");
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
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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
package com.amansiol.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.amansiol.messenger.models.Allusers_models;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllUserActivity extends AppCompatActivity {

    RecyclerView allusers_recycler;
    FirebaseDatabase mfirebasedatabase;
    DatabaseReference mref;
    FirebaseUser muser;
    FirebaseAuth firebaseAuth;
    Allusers_Adapter allusers_adapter;
    List<Allusers_models> Userlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        allusers_recycler=findViewById(R.id.allusers_recycler);
        allusers_recycler.setLayoutManager(new LinearLayoutManager(this));
        allusers_recycler.setHasFixedSize(true);
        mfirebasedatabase=FirebaseDatabase.getInstance();
        mref=mfirebasedatabase.getReference("Users");
        firebaseAuth=FirebaseAuth.getInstance();
        muser=firebaseAuth.getCurrentUser();
        Userlist=new ArrayList<>();
        getAllUser();
    }
    private void getAllUser() {
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Userlist.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    Allusers_models allusers_models=ds.getValue(Allusers_models.class);
                    if(!allusers_models.getUID().equals(muser.getUid())){

                        Userlist.add(allusers_models);
                    }
                    allusers_adapter=new Allusers_Adapter(AllUserActivity.this,Userlist);
                    allusers_recycler.setAdapter(allusers_adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
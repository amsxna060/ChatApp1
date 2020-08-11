package com.amansiol.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.amansiol.messenger.models.Allusers_models;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllUserActivity extends AppCompatActivity {

    RecyclerView allusers_recycler;
    FirebaseFirestore AllUserDb;
    FirebaseUser muser;
    FirebaseAuth firebaseAuth;
    Allusers_Adapter allusers_adapter;
    List<Allusers_models> Userlist;
    public static boolean alluserActivityvar=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        allusers_recycler=findViewById(R.id.allusers_recycler);
        allusers_recycler.setLayoutManager(new LinearLayoutManager(this));
        allusers_recycler.setHasFixedSize(true);
        AllUserDb=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        muser=firebaseAuth.getCurrentUser();
        Userlist=new ArrayList<>();
        getAllUser();
    }
    private void getAllUser() {
        AllUserDb.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Userlist.clear();
                for(QueryDocumentSnapshot ds:value){
                    Allusers_models allusers_models=ds.toObject(Allusers_models.class);
                    if(!allusers_models.getUID().equals(muser.getUid())){

                        Userlist.add(allusers_models);
                    }
                    allusers_adapter=new Allusers_Adapter(AllUserActivity.this,Userlist);
                    alluserActivityvar=true;
                    allusers_recycler.setAdapter(allusers_adapter);
                }
            }
        });

    }

}
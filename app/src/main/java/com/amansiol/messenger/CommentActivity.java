package com.amansiol.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.amansiol.messenger.models.CommentModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    RecyclerView commentRecycler;
    List<CommentModel> commentModelList;
    CommentAdapter adapter;
    String PostId;
    ActionBar mactionbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mactionbar=getSupportActionBar();
        mactionbar.setTitle("Comments");

        Intent intent=getIntent();
        PostId=intent.getStringExtra("PostId");
        commentRecycler=findViewById(R.id.comment_recycler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentRecycler.setLayoutManager(linearLayoutManager);

        loadComment();

    }

    private void loadComment() {
        commentModelList=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance()
                .getReference("Posts")
                .child(PostId)
                .child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentModelList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    CommentModel model=ds.getValue(CommentModel.class);
                    commentModelList.add(model);
                    adapter=new CommentAdapter(CommentActivity.this,commentModelList,PostId);
                    adapter.notifyDataSetChanged();
                    commentRecycler.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
package com.amansiol.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditPostActivity extends AppCompatActivity {

    CircularImageView epost_my_pic;
    TextView epostmy_name;
    EditText ewrite_thought;
    Button update_post,add_pic;
    ImageView eshow_posted_pic;
    Uri editedpicuri;
    public static final int REQUEST_IMAGE = 500;
    StorageReference mStorageRef;
    String StoragePath="user_post_pic/";
    ProgressDialog pd;
    String ptimestamp;
    boolean firsttime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        epost_my_pic = findViewById(R.id.epost_my_pic);
        epostmy_name = findViewById(R.id.epostmy_name);
        ewrite_thought = findViewById(R.id.ewrite_thought);
        update_post = findViewById(R.id.update_post);
        eshow_posted_pic = findViewById(R.id.eshow_posted_pic);
        pd = new ProgressDialog(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Intent intent = getIntent();
        final String timestamp = "" + intent.getStringExtra("timestamp");
        String proname = "" + intent.getStringExtra("proname");
        String proimage = "" + intent.getStringExtra("proimage");
        final String postimage = "" + intent.getStringExtra("postimage");
        final String postdesc = "" + intent.getStringExtra("postdesc");
        String prouid = "" + intent.getStringExtra("prouid");
        ptimestamp = timestamp;
        if (postimage.equals("noImage")) {
            eshow_posted_pic.setVisibility(View.GONE);
            editedpicuri = null;
        } else {
            editedpicuri = Uri.parse(postimage);
            Picasso.get().load(postimage).into(eshow_posted_pic);
            eshow_posted_pic.setVisibility(View.VISIBLE);
            firsttime=false;
        }

        ewrite_thought.setText(postdesc);
        ewrite_thought.setVisibility(View.VISIBLE);

        epostmy_name.setText(proname);
        try {
            Picasso.get().load(proimage).into(epost_my_pic);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.profilepic).into(epost_my_pic);
        }


        update_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editedpicuri==null&&ewrite_thought.getText().toString().isEmpty())
                {
                   Toast.makeText(EditPostActivity.this,"Please Posted Something..",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!ewrite_thought.getText().toString().isEmpty()){
                  pd.setMessage("Updating post");
                  pd.show();
                  uploaddescription(ewrite_thought.getText().toString());
                }else {
                    pd.setMessage("Updating post");
                    pd.show();
                    uploaddescription("noDescription");
                }

            }
        });

    }

    private void uploaddescription(final String desc) {

        DatabaseReference updateref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = updateref.orderByChild("ptimestamp").equalTo(ptimestamp);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    HashMap<String, Object> postdetails = new HashMap<>();
                    postdetails.put("pdesc",desc);
                    ds.getRef().updateChildren(postdetails)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    pd.dismiss();
                                    startActivity(new Intent(EditPostActivity.this,ProfileActivity.class));
                                    Animatoo.animateSplit(EditPostActivity.this);
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(EditPostActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }





}

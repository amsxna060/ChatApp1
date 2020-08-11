package com.amansiol.messenger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amansiol.messenger.models.PostModel;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.github.siyamed.shapeimageview.HeartImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseDatabase;
    CollectionReference databaseReference;
    ImageView cover_pic;
    TextView user_name;
    TextView user_status;
    TextView user_email;
    TextView user_number;
    TextView user_verified;
    TextView user_add;
    CircularImageView user_profile_pic;
    TextView user_hobby1;
    TextView user_hobby2;
    TextView user_hobby3;
    TextView user_gender;
    TextView user_dob;
    String dialog_image_url;
    FloatingActionButton fab;
    String suser_isverified;
    String myUid;
    PostAdapter postAdapter;
    List<PostModel> postList;
    RecyclerView mypostrecycyler;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        //init
        user_name=view.findViewById(R.id.user_name);
        user_status=view.findViewById(R.id.user_status);
        user_email=view.findViewById(R.id.user_email);
        user_number=view.findViewById(R.id.user_number);
        user_verified=view.findViewById(R.id.is_user_verified);
        user_add=view.findViewById(R.id.user_add);
        user_hobby1=view.findViewById(R.id.user_hobby1);
        user_hobby2=view.findViewById(R.id.user_hobby2);
        user_hobby3=view.findViewById(R.id.user_hobby3);
        user_profile_pic=view.findViewById(R.id.user_profile_pic);
        cover_pic=view.findViewById(R.id.cover_pic);
        user_gender=view.findViewById(R.id.user_gender);
        user_dob=view.findViewById(R.id.user_dob);
        fab=view.findViewById(R.id.goto_edit);
        mypostrecycyler=view.findViewById(R.id.onlymypost);
       //init firebase essentials
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseFirestore.getInstance();
        databaseReference=firebaseDatabase.collection("Users");
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        mypostrecycyler.setHasFixedSize(true);
        mypostrecycyler.setLayoutManager(linearLayoutManager);

        if(firebaseUser!=null){
            myUid=firebaseUser.getUid();
        }

        //query by Uid
//        Query query=databaseReference.orderByChild("UID").equalTo(myUid);
        databaseReference.
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
                            user_name.setText(suser_name);
                            user_email.setText(suser_email);
                            user_status.setText(suser_status);
                            user_number.setText(suser_number);
                            user_verified.setText(suser_isverified);
                            user_add.setText(suser_address);
                            user_dob.setText(suser_dob);
                            user_hobby1.setText(suser_hobby1);
                            user_hobby2.setText(suser_hobby2);
                            user_hobby3.setText(suser_hobby3);
                            user_gender.setText(suser_gender);
                            try {
                                Picasso.get().load(suser_profile_pic).into(user_profile_pic);
                            }catch (Exception e)
                            {
                                Picasso.get().load(R.drawable.profilepic).into(user_profile_pic);
                            }
                            try {
                                Picasso.get().load(scover_pic).into(cover_pic);
                            }catch (Exception e)
                            {
                                Picasso.get().load(R.drawable.cover).into(cover_pic);
                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot ds :snapshot.getChildren())
//                {
//                   String scover_pic=""+ds.child("coverimage").getValue();
//                   String suser_email=""+ds.child("email").getValue();
//                   String suser_Uid=""+ds.child("UID").getValue();
//                   String suser_name=""+ds.child("name").getValue();
//                   String suser_dob=""+ds.child("dob").getValue();
//                   String suser_number=""+ds.child("number").getValue();
//                   String suser_profile_pic=""+ds.child("image").getValue();
//                   String suser_status=""+ds.child("status").getValue();
//                  suser_isverified=""+ds.child("isverified").getValue();
//                   String suser_address=""+ds.child("address").getValue();
//                   String suser_hobby1=""+ds.child("hobby1").getValue();
//                   String suser_hobby2=""+ds.child("hobby2").getValue();
//                   String suser_hobby3=""+ds.child("hobby3").getValue();
//                   String suser_gender=""+ds.child("gender").getValue();
//                   dialog_image_url=suser_profile_pic;
//                  user_name.setText(suser_name);
//                  user_email.setText(suser_email);
//                  user_status.setText(suser_status);
//                  user_number.setText(suser_number);
//                  user_verified.setText(suser_isverified);
//                  user_add.setText(suser_address);
//                  user_dob.setText(suser_dob);
//                  user_hobby1.setText(suser_hobby1);
//                  user_hobby2.setText(suser_hobby2);
//                  user_hobby3.setText(suser_hobby3);
//                  user_gender.setText(suser_gender);
//                    try {
//                        Picasso.get().load(suser_profile_pic).into(user_profile_pic);
//                    }catch (Exception e)
//                    {
//                        Picasso.get().load(R.drawable.profilepic).into(user_profile_pic);
//                    }
//                    try {
//                        Picasso.get().load(scover_pic).into(cover_pic);
//                    }catch (Exception e)
//                    {
//                        Picasso.get().load(R.drawable.cover).into(cover_pic);
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        user_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder image=new AlertDialog.Builder(getActivity());
                View dialog=LayoutInflater.from(getActivity()).inflate(R.layout.showuserimage,null);
                ImageView imageView=dialog.findViewById(R.id.dialog_user_image);
                try {
                    Picasso.get().load(dialog_image_url).into(imageView);
                }catch (Exception e)
                {
                    Picasso.get().load(R.drawable.profilepic).into(imageView);
                }
                image.setView(dialog);
                image.setCancelable(true);
                image.create().show();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),EditProfileActivity.class));
            }
        });
        postList=new ArrayList<>();
        loadPost();
        return view;
    }
// we have to edit this
    private void loadPost() {
        DatabaseReference chatdbref=FirebaseDatabase.getInstance().getReference("Posts");
//        DocumentReference chatdbref=FirebaseFirestore.getInstance().document("Posts");
        chatdbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    PostModel post=ds.getValue(PostModel.class);
                    if(post.getPuid().equals(myUid))
                    {
                        postList.add(post);
                    }



                    postAdapter=new PostAdapter(getActivity(),postList);
                    mypostrecycyler.setAdapter(postAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        chatdbref.addSnapshotListener(Objects.requireNonNull(getActivity()), new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                    if(error!=null){
//                        Toast.makeText(getActivity(), ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    if(value.exists()){
//
//                    }
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
        checkUserLoginState();
        FirebaseUser muser=FirebaseAuth.getInstance().getCurrentUser();
       if(muser!=null){
           if(muser.isEmailVerified()){
               HashMap<String, Object> results = new HashMap<>();
               results.put("isverified","Verified");
               firebaseDatabase.collection("Users").document(muser.getUid()).update(results);
           }else
           {
               final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
               builder.setTitle("Verification");
               builder.setMessage("Please Verify your account with google.");
               builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                       user.sendEmailVerification()
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()){
                                           Toast.makeText(getActivity(),"Verification email sent to"+user.getEmail(),Toast.LENGTH_LONG).show();
                                           AlertDialog.Builder builder1=new AlertDialog.Builder(getActivity());
                                           builder1.setMessage("Verification email sent to "+user.getEmail()+" Go to gmail and click on link..");
                                           builder1.setCancelable(false);
                                           builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialog, int which) {
                                                   FirebaseAuth.getInstance().signOut();

                                                   if(getActivity()!=null){
                                                       Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                                                       startActivity(intent);
                                                   }


                                               }
                                           });
                                           builder1.create().show();
                                       }else {
                                           Toast.makeText(getActivity(),"Email Verification failed..",Toast.LENGTH_LONG).show();
                                       }
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                           }
                       });
                   }
               });
               builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });
               builder.create().show();
           }

       }

    }


    private void checkUserLoginState() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){


        }else
        {
            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        checkUserLoginState();
        super.onResume();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profilemenu,menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchitem=menu.findItem(R.id.searchbar);
        final MenuItem logout=menu.findItem(R.id.logout);
        searchitem.setVisible(false);

    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout:
            {
                FirebaseAuth.getInstance().signOut();
                checkUserLoginState();
                return true;
            }

        }
        return false;
    }

}

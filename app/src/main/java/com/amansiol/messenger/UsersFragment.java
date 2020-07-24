package com.amansiol.messenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.amansiol.messenger.models.Allusers_models;
import com.amansiol.messenger.models.Images;
import com.amansiol.messenger.models.PostModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    RecyclerView post_recycler;
    FirebaseDatabase mfirebasedatabase;
    DatabaseReference mref;
    FirebaseUser muser;
    FirebaseAuth firebaseAuth;
    imagesadapter imagesadapter;
    List<Images> imagesList;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_users, container, false);
        post_recycler=v.findViewById(R.id.images_recycler);
        post_recycler.setLayoutManager(new StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL));
        post_recycler.setHasFixedSize(true);
        mfirebasedatabase=FirebaseDatabase.getInstance();
        mref=mfirebasedatabase.getReference("Posts");
        firebaseAuth=FirebaseAuth.getInstance();
        muser=firebaseAuth.getCurrentUser();
        imagesList=new ArrayList<>();


      loadPost();

        return v;
    }

    private void loadPost() {

        DatabaseReference chatdbref=FirebaseDatabase.getInstance().getReference("Posts");
        chatdbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imagesList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){

                    Images images=ds.getValue(Images.class);
                    if(!images.getPimage().equals("noImage")){
                       imagesList.add(images);
                    }
                    imagesadapter=new imagesadapter(getActivity(),imagesList);
                    imagesadapter.notifyDataSetChanged();
                    post_recycler.setAdapter(imagesadapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
    }

    @Override
    public void onStart() {
        checkUserLoginState();
        super.onStart();
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

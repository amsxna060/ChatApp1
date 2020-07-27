package com.amansiol.messenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.amansiol.messenger.models.Allusers_models;
import com.amansiol.messenger.models.Images;
import com.amansiol.messenger.models.PostModel;
import com.firebase.ui.auth.data.model.User;
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
    DatabaseReference umref;
    FirebaseUser muser;
    FirebaseAuth firebaseAuth;
    imagesadapter imagesadapter;
    List<Images> imagesList;
    RecyclerView allusers_recycler;
    Allusers_Adapter allusers_adapter;
    List<Allusers_models> Userlist;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_users, container, false);
        post_recycler=v.findViewById(R.id.images_recycler);
        StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL);
//        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        staggeredGridLayoutManager.setReverseLayout(true);
        post_recycler.setLayoutManager(staggeredGridLayoutManager);
        post_recycler.setHasFixedSize(true);
        mfirebasedatabase=FirebaseDatabase.getInstance();
        mref=mfirebasedatabase.getReference("Posts");
        firebaseAuth=FirebaseAuth.getInstance();
        muser=firebaseAuth.getCurrentUser();
        imagesList=new ArrayList<>();
        allusers_recycler=v.findViewById(R.id.searchusers);
        allusers_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        allusers_recycler.setHasFixedSize(true);
        mfirebasedatabase=FirebaseDatabase.getInstance();
        umref=mfirebasedatabase.getReference("Users");
        Userlist=new ArrayList<>();
        loadPost();
        return v;
    }
    private void getAllUser() {
        umref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Userlist.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    Allusers_models allusers_models=ds.getValue(Allusers_models.class);
                    if(!allusers_models.getUID().equals(muser.getUid())){
                        Userlist.add(allusers_models);
                    }
                    allusers_adapter=new Allusers_Adapter(getActivity(),Userlist);
                    AllUserActivity.alluserActivityvar=false;
                    allusers_recycler.setAdapter(allusers_adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
    private void searchUsers(final String newText) {
        umref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Userlist.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    Allusers_models allusers_models=ds.getValue(Allusers_models.class);
                    if(!allusers_models.getUID().equals(muser.getUid())){
                       if(allusers_models.getName().toLowerCase().contains(newText.toLowerCase())||
                          allusers_models.getEmail().toLowerCase().contains(newText.toLowerCase())) {
                           Userlist.add(allusers_models);
                       }
                    }
                    allusers_adapter=new Allusers_Adapter(getActivity(),Userlist);
                    AllUserActivity.alluserActivityvar=false;
                    allusers_adapter.notifyDataSetChanged();
                    allusers_recycler.setAdapter(allusers_adapter);
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
        MenuItem searchitem=menu.findItem(R.id.searchbar);
        final MenuItem logout=menu.findItem(R.id.logout);

        final SearchView searchView=(SearchView) MenuItemCompat.getActionView(searchitem);
        searchView.setQueryHint("Search User Here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query)){
                    post_recycler.setVisibility(View.GONE);
                    allusers_recycler.setVisibility(View.VISIBLE);
                    searchUsers(query);
                    logout.setVisible(false);
                }else {
                    post_recycler.setVisibility(View.VISIBLE);
                    allusers_recycler.setVisibility(View.GONE);
                    Userlist.clear();
                    logout.setVisible(false);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                if(!TextUtils.isEmpty(newText)){
                    post_recycler.setVisibility(View.GONE);
                    allusers_recycler.setVisibility(View.VISIBLE);
                    searchUsers(newText);
                    logout.setVisible(false);

                }else {
                    post_recycler.setVisibility(View.VISIBLE);
                    allusers_recycler.setVisibility(View.GONE);
                    Userlist.clear();
                    logout.setVisible(false);
                }
                return false;
            }
        });
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

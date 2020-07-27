package com.amansiol.messenger;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.amansiol.messenger.models.Allusers_models;
import com.amansiol.messenger.models.ChatModel;
import com.amansiol.messenger.models.ChatlistModel;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Chat_listFragment extends Fragment {


    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ChatlistModel> chatlistModels;
    List<Allusers_models> userList;
    DatabaseReference reference;
    FirebaseUser currentuser;
    AdapterChatList adapterChatList;
    String theLastMessage="default";
    FloatingActionButton gotouserfab;

    public Chat_listFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_chat_list, container, false);

        firebaseAuth=FirebaseAuth.getInstance();
        recyclerView=v.findViewById(R.id.chatlistRecyler);
        currentuser=firebaseAuth.getCurrentUser();
        chatlistModels=new ArrayList<>();
        gotouserfab=v.findViewById(R.id.gotouserfab);
        reference= FirebaseDatabase.getInstance().getReference("Chatlist").child(currentuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatlistModels.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ChatlistModel chatlistModel=ds.getValue(ChatlistModel.class);
                    chatlistModels.add(chatlistModel);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        gotouserfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),AllUserActivity.class));
                Animatoo.animateSlideUp(getActivity());
            }
        });
        return v;
    }

    private void loadChats() {
        userList=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    Allusers_models user=ds.getValue(Allusers_models.class);
                    for(ChatlistModel chatlist:chatlistModels){
                        assert user != null;
                        if(user.getUID()!=null&&user.getUID().equals(chatlist.getId())){
                            userList.add(user);
                            break;
                        }
                    }
                    //adapter
                    adapterChatList=new AdapterChatList(getContext(),userList);
                    adapterChatList.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChatList);
                    for(int i=0;i<userList.size();i++){
                        lastMessage(userList.get(i).getUID());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
   private void SearchChatList(final String s){
       userList=new ArrayList<>();
       reference=FirebaseDatabase.getInstance().getReference("Users");
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               userList.clear();
               for(DataSnapshot ds:snapshot.getChildren()){
                   Allusers_models user=ds.getValue(Allusers_models.class);
                   for(ChatlistModel chatlist:chatlistModels){
                       assert user != null;
                       if(user.getUID()!=null&&user.getUID().equals(chatlist.getId())){
                           if(user.getName().toLowerCase().contains(s.toLowerCase())){
                               userList.add(user);
                               break;
                           }

                       }
                   }
                   //adapter
                   adapterChatList=new AdapterChatList(getContext(),userList);
                   adapterChatList.notifyDataSetChanged();
                   recyclerView.setAdapter(adapterChatList);
                   for(int i=0;i<userList.size();i++){
                       lastMessage(userList.get(i).getUID());
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
   }
    private void lastMessage(final String uid) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage="default";
                for(DataSnapshot ds: snapshot.getChildren()){
                    ChatModel chat=ds.getValue(ChatModel.class);
                    if(chat==null){
                        continue;
                    }
                    String sender=chat.getSender();
                    String receiver=chat.getReceiver();
                    if(sender==null||receiver==null){
                        continue;
                    }
                    if(chat.getReceiver().equals(currentuser.getUid())
                            &&chat.getSender().equals(uid)
                            ||chat.getReceiver().equals(uid)
                            && chat.getSender().equals(currentuser.getUid())){
                        theLastMessage=chat.getMessage();


                    }

                }
                adapterChatList.setLastMessageMap(uid,theLastMessage);
                adapterChatList.notifyDataSetChanged();
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
            Objects.requireNonNull(getActivity()).finish();
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
                    SearchChatList(query);
                }else {
                    loadChats();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                if(!TextUtils.isEmpty(newText)){

                    SearchChatList(newText);

                }else {
                    loadChats();
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
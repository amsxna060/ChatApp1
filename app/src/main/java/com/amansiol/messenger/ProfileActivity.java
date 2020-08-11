package com.amansiol.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.amansiol.messenger.notification.FirebaseMessaging;
import com.amansiol.messenger.notification.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;


public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    ActionBar mactionbar;
    String mUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth=FirebaseAuth.getInstance();
        mactionbar=getSupportActionBar();
        //init view
        bottomNavigationView=findViewById(R.id.botm_navi_menu_id);
        frameLayout=findViewById(R.id.container_id);
        mactionbar.setTitle("Home");
        getSupportFragmentManager().beginTransaction().replace(R.id.container_id,new HomeFragment()).commit();
        checkUserLoginState();



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch (item.getItemId()){
                   case R.id.home:
                        mactionbar.setTitle("Home");
                       getSupportFragmentManager().beginTransaction().replace(R.id.container_id,new HomeFragment()).commit();
                       return true;
                   case R.id.profile:
                       mactionbar.setTitle("Profile");
                       getSupportFragmentManager().beginTransaction().replace(R.id.container_id,new ProfileFragment()).commit();
                       return true;
                   case R.id.users:
                       mactionbar.setTitle("Search");
                       getSupportFragmentManager().beginTransaction().replace(R.id.container_id,new UsersFragment()).commit();
                       return true;
                   case R.id.chatlist:
                       mactionbar.setTitle("Chats");
                       getSupportFragmentManager().beginTransaction().replace(R.id.container_id,new Chat_listFragment()).commit();
                       return true;
                   case R.id.settings:
                       mactionbar.setTitle("Settings");
                       getSupportFragmentManager().beginTransaction().replace(R.id.container_id,new SettingFragment()).commit();
                       return true;
               }
                return false;
            }
        });
        checkUserLoginState();
        //update token
    }
    public void updateToken(String token){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken=new Token(token);
        ref.child(mUID).setValue(mToken);
    }
    private void checkUserLoginState() {
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            mUID=user.getUid();
            updateToken(FirebaseInstanceId.getInstance().getToken());
            SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("CURRENT_USERID",mUID);
            editor.apply();
        }else
        {
           startActivity(new Intent(ProfileActivity.this,MainActivity.class));
           finish();
        }
    }

    @Override
    protected void onResume() {
        checkUserLoginState();
        super.onResume();
    }



    @Override
    protected void onStart() {
        checkUserLoginState();
        super.onStart();
    }


}

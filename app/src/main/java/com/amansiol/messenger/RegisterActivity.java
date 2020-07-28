package com.amansiol.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    TextView Login;
    String new_here="Already Have Account ? ";
    String rgstr="Login!";
    ImageView backbtn;
    Button Register_btn;
    EditText Register_pass;
    EditText Register_email;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#7f000000"));
        setContentView(R.layout.activity_register);
        //init views
        Login=findViewById(R.id.register_text);
        backbtn=findViewById(R.id.back_btn_register);
        Register_btn=findViewById(R.id.register_btn);
        Register_email=findViewById(R.id.register_email);
        Register_pass=findViewById(R.id.register_pass);
        progressBar=findViewById(R.id.progress_bar_reg);
        mAuth = FirebaseAuth.getInstance();

        Login.setText(Html.fromHtml(getColoredSpanned(new_here,"#4E4E4E")+getColoredSpanned(rgstr,"#FF1744")));
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Animatoo.animateShrink(RegisterActivity.this);
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                Animatoo.animateSplit(RegisterActivity.this);
                finish();
            }
        });
     Register_btn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
//             progressBar.setAlpha(1);
             String sEmail=Register_email.getText().toString().trim();
             String sPass=Register_pass.getText().toString().trim();
             if(!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()){
                 Register_email.setError("incorrect Email");
                 Register_email.setFocusable(true);
             }else
                 if(sPass.length()<6)

             {
                 Register_pass.setError("Password must be more than 6 character");
                 Register_pass.setFocusable(true);
             }else {
                     registerUser(sEmail,sPass);
                 }

         }
     });
    }

    private void registerUser(String sEmail, String sPass) {
        progressBar.setAlpha(1);
        mAuth.createUserWithEmailAndPassword(sEmail,sPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressBar.setAlpha(0);
                            FirebaseUser user = mAuth.getCurrentUser();
                            String useremail=user.getEmail();
                            String userUID=user.getUid();
                            HashMap<Object,String> userhashmap=new HashMap<>();
                            userhashmap.put("email",useremail);
                            userhashmap.put("UID",userUID);
                            userhashmap.put("name","User Name");
                            userhashmap.put("number","+91 0000000000");
                            userhashmap.put("dob","");
                            userhashmap.put("onlineStatus","online");
                            userhashmap.put("typing","noOne");
                            userhashmap.put("coverimage","");
                            userhashmap.put("image","");
                            userhashmap.put("gender","Male");
                            userhashmap.put("status","The purpose of our lives is to be happy");
                            userhashmap.put("isverified","Unverified");
                            userhashmap.put("address","Add Address");
                            userhashmap.put("hobby1","Add Hobby");
                            userhashmap.put("hobby2","Add Hobby");
                            userhashmap.put("hobby3","Add Hobby");


                            FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                            DatabaseReference myref=firebaseDatabase.getReference("Users");
                            myref.child(userUID).setValue(userhashmap);
                            Intent intent=new Intent(RegisterActivity.this,BasicInfoActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Animatoo.animateSplit(RegisterActivity.this);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setAlpha(0);
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setAlpha(0);
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    @Override
    public void onBackPressed() {
        Animatoo.animateShrink(RegisterActivity.this);
        finish();

    }

}

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

public class LoginActivity extends AppCompatActivity {
    TextView Register;
    String new_here="Are you new Here? ";
    String rgstr="Register!";
    ImageView backbtn;
    Button Login_btn;
    EditText login_pass;
    ProgressBar progressBar;
    EditText login_email;
    FirebaseAuth mAuth;
    TextView forget_pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        setContentView(R.layout.activity_login);
        //init
        Register=findViewById(R.id.register_btn);
        backbtn=findViewById(R.id.back_btn_login);
        Login_btn=findViewById(R.id.login_btn);
        login_email=findViewById(R.id.login_email);
        login_pass=findViewById(R.id.login_pass);
        progressBar=findViewById(R.id.progress_bar_lgn);
        forget_pass=findViewById(R.id.forget_text);
        mAuth=FirebaseAuth.getInstance();
        Register.setText(Html.fromHtml(getColoredSpanned(new_here,"#4E4E4E")+getColoredSpanned(rgstr,"#D93A6E")));
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Animatoo.animateWindmill(LoginActivity.this);
            }
        });
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
                Animatoo.animateSplit(LoginActivity.this);
                finish();
            }
        });
        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sEmail=login_email.getText().toString().trim();
                String sPass= login_pass.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()){
                    login_email.setError("incorrect Email");
                    login_email.setFocusable(true);
                }else
                if(sPass.length()<6)

                {
                    login_pass.setError("Password must be more than 6 character");
                    login_pass.setFocusable(true);
                }else {
                    LoginUser(sEmail,sPass);
                }
            }
        });
        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Patterns.EMAIL_ADDRESS.matcher(login_email.getText().toString()).matches()){
                    login_email.setError("Fill Email Address");
                    login_email.setFocusable(true);
                }else {
                    progressBar.setAlpha(1);
                    mAuth.sendPasswordResetEmail(login_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setAlpha(0);
                                Toast.makeText(LoginActivity.this,"Email sent...",Toast.LENGTH_LONG).show();
                            }else {
                                progressBar.setAlpha(0);
                                Toast.makeText(LoginActivity.this,"Email sending Failed...",Toast.LENGTH_LONG).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setAlpha(0);
                            Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        });


    }

    private void LoginUser(String sEmail, String sPass) {
        progressBar.setAlpha(1);
        mAuth.signInWithEmailAndPassword(sEmail, sPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressBar.setAlpha(0);
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent=new Intent(LoginActivity.this,ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Animatoo.animateSplit(LoginActivity.this);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setAlpha(0);
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setAlpha(0);
                Toast.makeText(LoginActivity.this, ""+e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });


    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }
    @Override
    public void onBackPressed() {
        Animatoo.animateWindmill(LoginActivity.this);
        finish();
    }
}

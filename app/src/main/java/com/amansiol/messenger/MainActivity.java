package com.amansiol.messenger;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class MainActivity extends AppCompatActivity {
    Button login_btn;
    TextView Register;
    String new_here="Are you new Here? ";
    String rgstr="Register!";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#11072D"));
        setContentView(R.layout.activity_main);
        login_btn=findViewById(R.id.login_btn);
        Register=findViewById(R.id.register_btn);
        Register.setText(Html.fromHtml(getColoredSpanned(new_here,"#4E4E4E")+getColoredSpanned(rgstr,"#D93A6E")));
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                Animatoo.animateDiagonal(MainActivity.this);

            }
        });
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
                Animatoo.animateSplit(MainActivity.this);
            }
        });

    }
    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    @Override
    public void onBackPressed() {
        Animatoo.animateSpin(MainActivity.this);
        finish();
    }
}

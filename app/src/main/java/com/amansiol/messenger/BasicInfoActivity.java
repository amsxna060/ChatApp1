package com.amansiol.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

public class BasicInfoActivity extends AppCompatActivity {
   Button btn_next;
   EditText basic_info_name;
   EditText basic_info_dob;
   EditText basic_info_gen;
   EditText basic_info_cno;
   ProgressBar progress_bar_info;
   String sbasic_info_name;
   String sbasic_info_dob;
   String sbasic_info_gen;
   String sbasic_info_cno;
   FirebaseAuth firebaseAuth;
   FirebaseUser user;
  FirebaseFirestore firebaseDatabase;
  DocumentReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);
        //init
        btn_next=findViewById(R.id.basic_info_next_btn);
        basic_info_name=findViewById(R.id.basic_info_name);
        basic_info_dob=findViewById(R.id.basic_info_dob);
        basic_info_gen=findViewById(R.id.basic_info_gen);
        basic_info_cno=findViewById(R.id.basic_info_cno);
        progress_bar_info=findViewById(R.id.progress_bar_info);
        //init firebase essential
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseFirestore.getInstance();
        databaseReference=firebaseDatabase.collection("Users").document(user.getUid());
        basic_info_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog;
                Calendar calendar=new GregorianCalendar(Locale.getDefault());
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                final int date=calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(BasicInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String dob;
                        int date=dayOfMonth;
                        int mnth=month;
                        int yer=year-1900;
                        Date date2=new Date();
                        date2.setDate(date);
                        date2.setMonth(mnth);
                        date2.setYear(yer);
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEE, MMM d, yyyy");
                        String d=simpleDateFormat.format(date2);
                        basic_info_dob.setText(d);
                    }
                },year,month+1,date);
                DatePicker datePicker=datePickerDialog.getDatePicker();
                datePicker.setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  if(basic_info_name.getText().toString().isEmpty()||!(basic_info_name.getText().toString().length()<25)){
                      basic_info_name.setError("Fill Name Or Invalid Name");
                      basic_info_name.requestFocus();
                  }else if(basic_info_gen.getText().toString().isEmpty()||!basic_info_gen.getText().toString().equalsIgnoreCase("Male")
                  &&!basic_info_gen.getText().toString().equalsIgnoreCase("Female")){
                      basic_info_gen.setError("Fill Gender Or invalid gender");
                      basic_info_gen.requestFocus();
                  }else if(basic_info_dob.getText().toString().isEmpty()){
                      basic_info_dob.setError("Fill DOB");
                      basic_info_dob.requestFocus();
                  }else if(basic_info_cno.getText().toString().isEmpty()||!(basic_info_cno.getText().toString().length()==10)){
                      basic_info_cno.setError("Fill Contact No. or invalid number");
                      basic_info_cno.requestFocus();
                  } else {
                      sbasic_info_name= basic_info_name.getText().toString();
                      sbasic_info_dob=basic_info_dob.getText().toString();
                      sbasic_info_gen=basic_info_gen.getText().toString();
                      sbasic_info_cno="+91 "+basic_info_cno.getText().toString();
                      progress_bar_info.setAlpha(1);
                      HashMap<String,Object> results=new HashMap<>();
                      results.put("name",sbasic_info_name);
                      results.put("number",sbasic_info_cno);
                      results.put("dob",sbasic_info_dob);
                      results.put("gender",sbasic_info_gen);
                      databaseReference.update(results)
                              .addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void aVoid) {
                                      progress_bar_info.setAlpha(0);
                                      Toast.makeText(BasicInfoActivity.this,"Welcome",Toast.LENGTH_LONG).show();
                                          Intent intent=new Intent(BasicInfoActivity.this,ProfileActivity.class);
                                          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                          startActivity(intent);
                                          Animatoo.animateSpin(BasicInfoActivity.this);
                                          finish();
                                  }
                              }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              Toast.makeText(BasicInfoActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                          }
                      });
                  }
            }
        });

    }


}
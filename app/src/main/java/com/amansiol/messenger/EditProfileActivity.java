package com.amansiol.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;



public class EditProfileActivity extends AppCompatActivity {

    public static final int REQUEST_IMAGE = 100;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    ImageView ecover_pic;
    EditText euser_name;
    EditText euser_status;
    TextView euser_email;
    EditText euser_number;
    TextView euser_verified;
    EditText euser_add;
    CircularImageView euser_profile_pic;
    EditText euser_hobby1;
    EditText euser_hobby2;
    EditText euser_hobby3;
    EditText euser_gender;
    EditText euser_dob;
    ImageView add_image_pic;
    Uri profile_uri_path;
    Uri cover_pic_uri;
    Button save_btn;
    ProgressBar progressBar_edit;
    String suser_name;
    String suser_dob;
    String suser_number;
    String suser_status;
    String suser_address;
    String suser_hobby1;
    String suser_hobby2;
    String suser_hobby3;
    String suser_gender;
    ProgressDialog pd;
    StorageReference mStorageRef;
    String StoragePath="user_profile_cover_pic/";
    private static final int COVER_PIC_CODE=105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //init
       euser_name=findViewById(R.id.euser_name);
       euser_status=findViewById(R.id.euser_status);
       euser_email=findViewById(R.id.euser_email);
       euser_number=findViewById(R.id.euser_number);
       euser_verified=findViewById(R.id.eis_user_verified);
       euser_add=findViewById(R.id.euser_add);
       euser_hobby1=findViewById(R.id.euser_hobby1);
       euser_hobby2=findViewById(R.id.euser_hobby2);
       euser_hobby3=findViewById(R.id.euser_hobby3);
       euser_profile_pic=findViewById(R.id.euser_profile_pic);
       ecover_pic=findViewById(R.id.ecover_pic);
       euser_gender=findViewById(R.id.euser_gender);
       euser_dob=findViewById(R.id.euser_dob);
       add_image_pic=findViewById(R.id.profile_add_icon);
       save_btn=findViewById(R.id.edit_save_btn);
       progressBar_edit=findViewById(R.id.progress_bar_edit);
       pd=new ProgressDialog(EditProfileActivity.this);
       euser_name.requestFocus();
        //init firebase essentials
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        db=FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //query by Uid
        db.collection("Users").whereEqualTo("UID",firebaseUser.getUid())
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
                            suser_number=suser_number.substring(4);
                            String suser_profile_pic=""+ds.get("image");
                            String suser_status=""+ds.get("status");
                            String suser_isverified=""+ds.get("isverified");
                            String suser_address=""+ds.get("address");
                            String suser_hobby1=""+ds.get("hobby1");
                            String suser_hobby2=""+ds.get("hobby2");
                            String suser_hobby3=""+ds.get("hobby3");
                            String suser_gender=""+ds.get("gender");
                            euser_name.setText(suser_name);
                            euser_email.setText(suser_email);
                            euser_status.setText(suser_status);
                            euser_number.setText(suser_number);
                            euser_verified.setText(suser_isverified);
                            euser_add.setText(suser_address);
                            euser_dob.setText(suser_dob);
                            euser_hobby1.setText(suser_hobby1);
                            euser_hobby2.setText(suser_hobby2);
                            euser_hobby3.setText(suser_hobby3);
                            euser_gender.setText(suser_gender);
                            try {
                                Picasso.get().load(suser_profile_pic).into(euser_profile_pic);
                            }catch (Exception e)
                            {
                                Picasso.get().load(R.drawable.profilepic).into(euser_profile_pic);
                            }
                            try {
                                Picasso.get().load(scover_pic).into(ecover_pic);
                            }catch (Exception e)
                            {
                                Picasso.get().load(R.drawable.cover).into(ecover_pic);
                            }

                        }
                    }
                });
        euser_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog;
                Calendar calendar=new GregorianCalendar(Locale.getDefault());
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                final int date=calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        euser_dob.setText(d);
                    }
                },year,month+1,date);
                DatePicker datePicker=datePickerDialog.getDatePicker();
                datePicker.setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.show();

            }
        });

        ecover_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Choose Picture"),COVER_PIC_CODE);
            }
        });
        add_image_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(EditProfileActivity.this)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    showImagePickerOptions();
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(euser_name.getText().toString().isEmpty()||!(euser_name.getText().toString().length()<25)){
                    euser_name.setError("Fill Name Or Invalid Name");
                    euser_name.requestFocus();
                }else if(euser_gender.getText().toString().isEmpty()||!euser_gender.getText().toString().equalsIgnoreCase("Male")
                        &&!euser_gender.getText().toString().equalsIgnoreCase("Female")){
                    euser_gender.setError("Fill Gender Or invalid gender");
                    euser_gender.requestFocus();
                }else if(euser_dob.getText().toString().isEmpty()){
                    euser_dob.setError("Fill DOB");
                    euser_dob.requestFocus();
                }else if(euser_number.getText().toString().isEmpty()||!(euser_number.getText().toString().length()==10)){
                    euser_number.setError("Fill Contact No. or invalid number");
                    euser_number.requestFocus();
                } else if (euser_status.getText().toString().isEmpty()||!(euser_status.getText().toString().length()<50)) {
                    euser_status.setError("Fill Status Or Invalid Status");
                    euser_status.requestFocus();
                } else if (euser_add.getText().toString().isEmpty()||!(euser_add.getText().toString().length()<50)) {
                    euser_add.setError("Fill Address Or Invalid Address");
                    euser_add.requestFocus();
                }else if (euser_hobby1.getText().toString().isEmpty()||!(euser_hobby1.getText().toString().length()<40)) {
                    euser_hobby1.setError("Invalid Hobby");
                    euser_hobby1.requestFocus();
                }else if (euser_hobby2.getText().toString().isEmpty()||!(euser_hobby2.getText().toString().length()<40)) {
                    euser_hobby2.setError("Invalid Hobby");
                    euser_hobby2.requestFocus();
                }else if (euser_hobby3.getText().toString().isEmpty()||!(euser_hobby3.getText().toString().length()<40)) {
                    euser_hobby3.setError("Invalid Hobby");
                    euser_hobby3.requestFocus();
                }
                else {
                    pd.setTitle("Data Uploading....");
                    pd.show();

                    suser_name = euser_name.getText().toString().trim();
                    suser_dob = euser_dob.getText().toString().trim();
                    suser_gender = euser_gender.getText().toString().trim();
                    suser_number = "+91 " + euser_number.getText().toString().trim();
                    suser_status=euser_status.getText().toString().trim();
                    suser_address=euser_add.getText().toString().trim();
                    suser_hobby1=euser_hobby1.getText().toString().trim();
                    suser_hobby2=euser_hobby2.getText().toString().trim();
                    suser_hobby3=euser_hobby3.getText().toString().trim();
                    HashMap<String, Object> results = new HashMap<>();
                    results.put("name", suser_name);
                    results.put("number", suser_number);
                    results.put("dob", suser_dob);
                    results.put("gender", suser_gender);
                    results.put("address", suser_address);
                    results.put("hobby1", suser_hobby1);
                    results.put("hobby2", suser_hobby2);
                    results.put("hobby3", suser_hobby3);

                    db.collection("Users").document(firebaseUser.getUid()).update(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(EditProfileActivity.this, "Welcome", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    Animatoo.animateSplit(EditProfileActivity.this);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(EditProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    updateNameToPost(suser_name);
                }
            }
        });
    }

    private void updateNameToPost(final String suser_name) {
        DatabaseReference updateref=FirebaseDatabase.getInstance().getReference("Posts");
        Query query=updateref.orderByChild("puid").equalTo(firebaseUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                     HashMap<String, Object> results = new HashMap<>();
                     results.put("pname", suser_name);
                      ds.getRef().updateChildren(results);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(EditProfileActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(EditProfileActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                profile_uri_path = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profile_uri_path);

                    // loading profile image from local cache
                    Picasso.get().load(profile_uri_path).into(euser_profile_pic);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(requestCode == COVER_PIC_CODE){
            if (resultCode == Activity.RESULT_OK) {
                 cover_pic_uri=  data.getData();
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), cover_pic_uri);

                    // loading profile image from local cache
                    Picasso.get().load(cover_pic_uri).into(ecover_pic);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                EditProfileActivity.this.openSettings();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(profile_uri_path!=null) {
            Uploading_profile_tofirebase();
        }
        if(cover_pic_uri!=null)
        {
            Uploading_cover_tofirebase();
        }
    }

    private void Uploading_cover_tofirebase(){
        pd.setTitle("Upload");
        pd.setMessage("Cover Pic Uploading....");
        pd.show();

        String FilePathNamecover=StoragePath+""+"cover"+"_"+firebaseUser.getUid();
        StorageReference storageref2cover = mStorageRef.child(FilePathNamecover);

        storageref2cover.putFile(cover_pic_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri cover_pic_uri_path=uriTask.getResult();
                        if(uriTask.isSuccessful()){
                            //image uploaded...
                            HashMap<String,Object> cover=new HashMap<>();
                            cover.put("coverimage",cover_pic_uri_path.toString());
                            db.collection("Users").document(firebaseUser.getUid()).update(cover).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                pd.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(EditProfileActivity.this, "Error while uploading image" , Toast.LENGTH_LONG).show();
                                }
                            });

                        }else {
                            pd.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Error while uploading image" , Toast.LENGTH_LONG).show();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        pd.dismiss();
                        Toast.makeText(EditProfileActivity.this, ""+exception.getMessage() , Toast.LENGTH_LONG).show();
                        // ...
                    }
                });

    }
    private void Uploading_profile_tofirebase(){
        pd.setTitle("Upload");
        pd.setMessage("Profile Pic Uploading....");
        pd.show();
    String FilePathName=StoragePath+""+"image"+"_"+firebaseUser.getUid();
                    StorageReference storageref2 = mStorageRef.child(FilePathName);

                    storageref2.putFile(profile_uri_path)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful());
                                    final Uri profile_pic_uri_path=uriTask.getResult();
                                    if(uriTask.isSuccessful()){
                                        //image uploaded...
                                        HashMap<String,Object> profile=new HashMap<>();
                                        profile.put("image",profile_pic_uri_path.toString());
                                        db.collection("Users").document(firebaseUser.getUid()).update(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                              updatePicToPost(profile_pic_uri_path.toString());
                                                pd.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(EditProfileActivity.this, "Error while uploading image" , Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    }else {
                                        pd.dismiss();
                                        Toast.makeText(EditProfileActivity.this, "Error while uploading image" , Toast.LENGTH_LONG).show();

                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    progressBar_edit.setAlpha(0);
                                    Toast.makeText(EditProfileActivity.this, ""+exception.getMessage() , Toast.LENGTH_LONG).show();
                                    // ...
                                }
                            });
}

    private void updatePicToPost(final String toString) {
        DatabaseReference updateref=FirebaseDatabase.getInstance().getReference("Posts");
        Query query=updateref.orderByChild("puid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    HashMap<String, Object> results = new HashMap<>();
                    results.put("puserimage", toString);
                    ds.getRef().updateChildren(results);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}

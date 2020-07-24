package com.amansiol.messenger;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amansiol.messenger.models.ChatModel;
import com.amansiol.messenger.models.PostModel;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public static final int REQUEST_IMAGE = 400;
    RecyclerView postrecycler;
    CircularImageView post_my_pic;
    ImageView load_post,show_posted_pic,add_pic;
    TextView postMy_name;
    Uri postUri=null;
    EditText write_thought;
    String spost_my_name;
    String spost_profile_pic;
    ProgressDialog pd;
    StorageReference mStorageRef;
    String StoragePath="user_post_pic/";
    List<PostModel> PostList;
    PostAdapter postAdapter;
    String timestamp;
    ProgressBar postprogress;
    public HomeFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View post= inflater.inflate(R.layout.fragment_home, container, false);

        postrecycler=post.findViewById(R.id.post_recyler);
        post_my_pic=post.findViewById(R.id.post_my_pic);
        load_post=post.findViewById(R.id.upload_post);
        show_posted_pic=post.findViewById(R.id.show_posted_pic);
        add_pic=post.findViewById(R.id.add_pic);
        postMy_name=post.findViewById(R.id.postmy_name);
        write_thought=post.findViewById(R.id.write_thought);
        postprogress=post.findViewById(R.id.post_progressbar);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        postrecycler.setHasFixedSize(true);
        postrecycler.setLayoutManager(linearLayoutManager);
        pd=new ProgressDialog(getActivity());
        mStorageRef= FirebaseStorage.getInstance().getReference();
        FirebaseUser muser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myref=FirebaseDatabase.getInstance().getReference("Users");
        Query query=myref.orderByChild("UID").equalTo(muser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                   spost_my_name =""+ds.child("name").getValue();
                   spost_profile_pic =""+ds.child("image").getValue();
                    postMy_name.setText(spost_my_name);
                    try {
                        Picasso.get().load(spost_profile_pic).into(post_my_pic);
                    }catch (Exception e)
                    {
                        Picasso.get().load(R.drawable.profilepic).into(post_my_pic);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(getActivity())
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
       load_post.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(write_thought.getText().toString().isEmpty()&&postUri==null){
                   Toast.makeText(getActivity(),"Please Make Sure you are going to Post Something...",Toast.LENGTH_LONG).show();
                   return;
               }
               timestamp=String.valueOf(System.currentTimeMillis());
               InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
               //Find the currently focused view, so we can grab the correct window token from it.
               View view = getActivity().getCurrentFocus();
               //If no view currently has focus, create a new one, just so we can grab a window token from it
               if (view == null) {
                   view = new View(getActivity());
               }
               imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
               if(postUri!=null && write_thought.getText().toString().isEmpty()){

                   postprogress.setVisibility(View.VISIBLE);

                   String FilePathNamecover=StoragePath+""+"postedpic"+"_"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"_"+timestamp;
                   StorageReference storageref2cover = mStorageRef.child(FilePathNamecover);

                   storageref2cover.putFile(postUri)
                           .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                   // Get a URL to the uploaded content
                                   Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                   while (!uriTask.isSuccessful());
                                   Uri cover_pic_uri_path=uriTask.getResult();
                                   if(uriTask.isSuccessful()){
                                       //image uploaded...
                                       final DatabaseReference addpostref=FirebaseDatabase.getInstance().getReference("Posts");

                                       HashMap<String,Object> postdetails=new HashMap<>();
                                       postdetails.put("pname",spost_my_name);
                                       postdetails.put("puid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                       postdetails.put("pimage",cover_pic_uri_path.toString());
                                       postdetails.put("pdesc","noDescription");
                                       postdetails.put("ptimestamp", timestamp);
                                       postdetails.put("puserimage",spost_profile_pic);
                                       postdetails.put("likes","0");
                                       postdetails.put("comments","0");
                                       addpostref.child(timestamp).setValue(postdetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
//                                               pd.dismiss();
                                               postprogress.setVisibility(View.GONE);
                                               Toast.makeText(getActivity(), "Post Uploaded..." , Toast.LENGTH_LONG).show();
                                               write_thought.setText("");
                                               postUri=null;
                                               if(postUri==null){
                                                   show_posted_pic.setVisibility(View.GONE);
                                               }else {
                                                   show_posted_pic.setVisibility(View.VISIBLE);
                                               }

                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
//                                               pd.dismiss();
                                               postprogress.setVisibility(View.GONE);
                                               Toast.makeText(getActivity(), ""+e.getMessage() , Toast.LENGTH_LONG).show();
                                           }
                                       });


                                   }else {
//                                       pd.dismiss();
                                       postprogress.setVisibility(View.GONE);
                                       Toast.makeText(getActivity(), "Error while uploading image" , Toast.LENGTH_LONG).show();

                                   }
                               }
                           })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception exception) {
                                   // Handle unsuccessful uploads
//                                   pd.dismiss();
                                   postprogress.setVisibility(View.GONE);
                                   Toast.makeText(getActivity(), ""+exception.getMessage() , Toast.LENGTH_LONG).show();
                                   // ...
                               }
                           });
               }else if(postUri==null && !write_thought.getText().toString().isEmpty()){
//                   pd.setTitle("Upload");
//                   pd.setMessage("Post Uploading....");
//                   pd.show();
                   postprogress.setVisibility(View.VISIBLE);
                   final DatabaseReference addpostref=FirebaseDatabase.getInstance().getReference("Posts");

                   HashMap<String,Object> postdetails=new HashMap<>();
                   postdetails.put("pname",spost_my_name);
                   postdetails.put("puid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                   postdetails.put("pimage","noImage");
                   postdetails.put("pdesc",write_thought.getText().toString());
                   postdetails.put("ptimestamp", timestamp);
                   postdetails.put("puserimage",spost_profile_pic);
                   postdetails.put("likes","0");
                   postdetails.put("comments","0");
                   addpostref.child(timestamp).setValue(postdetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
//                           pd.dismiss();
                           postprogress.setVisibility(View.GONE);
                           Toast.makeText(getActivity(), "Post Uploaded..." , Toast.LENGTH_LONG).show();
                           write_thought.setText("");
                           postUri=null;
                           if(postUri==null){
                               show_posted_pic.setVisibility(View.GONE);
                           }else {
                               show_posted_pic.setVisibility(View.VISIBLE);
                           }
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
//                           pd.dismiss();
                           postprogress.setVisibility(View.GONE);
                           Toast.makeText(getActivity(), ""+e.getMessage() , Toast.LENGTH_LONG).show();
                       }
                   });
               }else if(postUri!=null && !write_thought.getText().toString().isEmpty()) {
//                   pd.setTitle("Upload");
//                   pd.setMessage("Post Uploading....");
//                   pd.show();
                   postprogress.setVisibility(View.VISIBLE);
                   String FilePathNamecover=StoragePath+""+"postedpic"+"_"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"_"+timestamp;
                   StorageReference storageref2cover = mStorageRef.child(FilePathNamecover);

                   storageref2cover.putFile(postUri)
                           .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                   // Get a URL to the uploaded content
                                   Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                   while (!uriTask.isSuccessful());
                                   Uri cover_pic_uri_path=uriTask.getResult();
                                   if(uriTask.isSuccessful()){
                                       //image uploaded...
                                       final DatabaseReference addpostref=FirebaseDatabase.getInstance().getReference().child("Posts");

                                       HashMap<String,Object> postdetails=new HashMap<>();
                                       postdetails.put("pname",spost_my_name);
                                       postdetails.put("puid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                       postdetails.put("pimage",cover_pic_uri_path.toString());
                                       postdetails.put("pdesc",write_thought.getText().toString());
                                       postdetails.put("ptimestamp", timestamp);
                                       postdetails.put("puserimage",spost_profile_pic);
                                       postdetails.put("likes","0");
                                       postdetails.put("comments","0");
                                       addpostref.child(timestamp).setValue(postdetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
//                                               pd.dismiss();
                                               postprogress.setVisibility(View.GONE);
                                               Toast.makeText(getActivity(), "Post Uploaded..." , Toast.LENGTH_LONG).show();
                                               write_thought.setText("");
                                               postUri=null;
                                               if(postUri==null){
                                                   show_posted_pic.setVisibility(View.GONE);
                                               }else {
                                                   show_posted_pic.setVisibility(View.VISIBLE);
                                               }
                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
//                                               pd.dismiss();
                                               postprogress.setVisibility(View.GONE);
                                               Toast.makeText(getActivity(), ""+e.getMessage() , Toast.LENGTH_LONG).show();
                                           }
                                       });


                                   }else {
//                                       pd.dismiss();
                                       postprogress.setVisibility(View.GONE);
                                       Toast.makeText(getActivity(), "Error while uploading image" , Toast.LENGTH_LONG).show();

                                   }
                               }
                           })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception exception) {
                                   // Handle unsuccessful uploads
//                                   pd.dismiss();
                                   postprogress.setVisibility(View.GONE);
                                   Toast.makeText(getActivity(), ""+exception.getMessage() , Toast.LENGTH_LONG).show();
                                   // ...
                               }
                           });

               }


           }
       });
       PostList=new ArrayList<>();
       return post;
    }
    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(getActivity(), new ImagePickerActivity.PickerOptionListener() {
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
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
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
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 3); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 4);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                postUri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), postUri);

                    // loading profile image from local cache
                    Picasso.get().load(postUri).into(show_posted_pic);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
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





    private void loadPost() {

        DatabaseReference chatdbref=FirebaseDatabase.getInstance().getReference("Posts");
        chatdbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){

                    PostModel post=ds.getValue(PostModel.class);
                    PostList.add(post);
                    postAdapter=new PostAdapter(getActivity(),PostList);
                    postrecycler.setAdapter(postAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", "com.amansiol.messenger", null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
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
    }

    @Override
    public void onStart() {
        checkUserLoginState();
        if(postUri==null){
            show_posted_pic.setVisibility(View.GONE);
        }else {
            show_posted_pic.setVisibility(View.VISIBLE);
        }
        loadPost();
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

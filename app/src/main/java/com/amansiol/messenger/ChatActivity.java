package com.amansiol.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amansiol.messenger.models.Allusers_models;
import com.amansiol.messenger.models.ChatModel;
import com.amansiol.messenger.notification.Data;
import com.amansiol.messenger.notification.Sender;
import com.amansiol.messenger.notification.Token;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE = 700;
    Toolbar chattoolbar;
  CircularImageView chat_pic;
  TextView chat_name,chat_status;
  EditText  edit_msg_txt;
  FloatingActionButton fab_send;
  String hisUid;
  String myUid;
  String hisImage;
  String myimage;
  FirebaseAuth mAuth;
  DatabaseReference mref;
  FirebaseDatabase mdatabase;
  ValueEventListener SeenListener;
  DatabaseReference DbRefForSeen;
  List<ChatModel> chatList;
  ChatAdapter chatAdapter;
  RecyclerView chatrecycler;
  RelativeLayout hiswholetypinganimationlayout;
  ImageView hisimagetypinganimation;
  ImageView hisheartbeattypingani;
  ImageView chatActmenu;
//  ImageButton sendimagemsgbtn;
    private SoundPool soundPool;
    private int sound1, sound2, sound3;

  private  RequestQueue  requestQueue;
  private boolean notify=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //init
        chattoolbar=findViewById(R.id.chattoolbar);
        setSupportActionBar(chattoolbar);
        chattoolbar.setTitle("");
        chat_pic=findViewById(R.id.chat_pic);
        chat_name=findViewById(R.id.chat_name);
        chat_status=findViewById(R.id.chat_status);
        fab_send=findViewById(R.id.send_btn_msg);
        edit_msg_txt=findViewById(R.id.edit_txt_box);
        chatrecycler=findViewById(R.id.chatsmessagesscreen);
        hisimagetypinganimation=findViewById(R.id.histypinganichat_pic);
        hisheartbeattypingani=findViewById(R.id.histypinganim);
        hiswholetypinganimationlayout=findViewById(R.id.hiswholetypinganilayout);
//        chatActmenu=findViewById(R.id.chatActmenu);
//        sendimagemsgbtn=findViewById(R.id.send_pic_msg);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();
        sound1 = soundPool.load(ChatActivity.this, R.raw.got_it_done, 1);
        sound2 = soundPool.load(ChatActivity.this, R.raw.swiftly, 1);
        sound3 = soundPool.load(ChatActivity.this, R.raw.filling_your_inbox, 1);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatrecycler.setHasFixedSize(true);
        chatrecycler.setLayoutManager(linearLayoutManager);

        requestQueue= Volley.newRequestQueue(getApplicationContext());

        Intent intent=getIntent();
        hisUid=intent.getStringExtra("hisUID");
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        myUid=user.getUid();
        mdatabase=FirebaseDatabase.getInstance();
        mref=mdatabase.getReference("Users");
        Query query=mref.orderByChild("UID").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    String name=""+ds.child("name").getValue();
                    hisImage=""+ds.child("image").getValue();

                    String typingstatus=""+ds.child("typing").getValue();

                    if(typingstatus.equals(myUid)){
                        chat_status.setText("typing....");
                        hiswholetypinganimationlayout.setVisibility(View.VISIBLE);
                        try {
                            Picasso.get().load(hisImage).into(hisimagetypinganimation);
                        }catch (Exception e)
                        {
                            Picasso.get().load(R.drawable.profilepic).into(hisimagetypinganimation);
                        }
                        Drawable d=hisheartbeattypingani.getDrawable();
                        if(d instanceof AnimatedVectorDrawableCompat){
                            final AnimatedVectorDrawableCompat drawableCompat=(AnimatedVectorDrawableCompat)d;
                            drawableCompat.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                                @Override
                                public void onAnimationEnd(Drawable drawable) {
                                    super.onAnimationEnd(drawable);
                                    drawableCompat.start();
                                }
                            });
                            drawableCompat.start();
                        }else if(d instanceof AnimatedVectorDrawable){
                            final AnimatedVectorDrawable drawable=(AnimatedVectorDrawable)d;
                            drawable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                                @Override
                                public void onAnimationEnd(Drawable drawable2) {
                                    super.onAnimationEnd(drawable2);
                                    drawable.start();
                                }

                            });
                            drawable.start();
                        }



//                        d.registerAnimationCallback(new Animatable2.AnimationCallback() {
//                            @Override
//                            public void onAnimationEnd(Drawable drawable) {
//                                avd.start();
//                            }
//                        });
//
//                        drawable.start();
                    }else {
                        String onlinestatus=""+ds.child("onlineStatus").getValue();
                            if(onlinestatus.equals("online")){
                            chat_status.setText(onlinestatus);
                            hiswholetypinganimationlayout.setVisibility(View.GONE);
                        } else {
                            Calendar cal= Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(onlinestatus));
                            String date= DateFormat.format("hh:mm aa",cal).toString();
                            chat_status.setText("Last Seen at : "+date);
                        }
                    }
                    chat_name.setText(name);
                    try {
                        Picasso.get().load(hisImage).into(chat_pic);
                    }catch (Exception e)
                    {
                        Picasso.get().load(R.drawable.profilepic).into(chat_pic);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        query=mref.orderByChild("UID").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:snapshot.getChildren())
                {
                    myimage=""+ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        edit_msg_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.toString().trim().length()==0){
                        checkTypingStatus("noOne");
                    }else {
                        checkTypingStatus(hisUid);

                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
      fab_send.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              notify=true;
              String my_msg=edit_msg_txt.getText().toString().trim();
              if(TextUtils.isEmpty(my_msg)){
                  Toast.makeText(ChatActivity.this,"Sorry Empty message",Toast.LENGTH_LONG).show();
              }else {
                  SendMessage(my_msg);
                  edit_msg_txt.setText("");
                  soundPool.play(sound3, 1, 1, 0, 0, 1);

              }

          }

      });



        final DatabaseReference chatref1=FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(myUid)
                .child(hisUid);
        chatref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatref1.child("id").setValue(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final DatabaseReference chatref2=FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(hisUid)
                .child(myUid);
        chatref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatref2.child("id").setValue(myUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        sendimagemsgbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Dexter.withActivity(ChatActivity.this)
//                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        .withListener(new MultiplePermissionsListener() {
//                            @Override
//                            public void onPermissionsChecked(MultiplePermissionsReport report) {
//                                if (report.areAllPermissionsGranted()) {
//                                    showImagePickerOptions();
//                                }
//
//                                if (report.isAnyPermissionPermanentlyDenied()) {
//                                    showSettingsDialog();
//                                }
//                            }
//
//                            @Override
//                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                                token.continuePermissionRequest();
//                            }
//                        }).check();
//            }
//        });

//        PopupMenu popupMenuchat=new PopupMenu(ChatActivity.this,chatActmenu, Gravity.END);
//        popupMenuchat.getMenu().add(Menu.NONE,0,0,"Clear Chat");
//        popupMenuchat.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if(item.getItemId()==0){
//                    ClearChat();
//                }
//                return false;
//            }
//        });
//        popupMenuchat.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatactmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.clearchat){
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setTitle("Clear");
            alert.setCancelable(true);
            alert.setMessage("Are you Sure to Clear all Chat for Both?");
            alert.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ClearChat();
                }
            });
            alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
                }
            });
            alert.create().show();

            return true;
        }
        return false;
    }

    private void ClearChat() {
        final ProgressDialog pd=new ProgressDialog(ChatActivity.this);
        pd.setMessage("Deleting...");
        pd.show();
        DatabaseReference chatdbref=FirebaseDatabase.getInstance().getReference("Chats");
        chatdbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){

                    ChatModel chat=ds.getValue(ChatModel.class);
                    if(chat.getReceiver().equals(myUid)&&chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid)&&chat.getSender().equals(myUid)){
                        ds.getRef().removeValue();
                    }


                }
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void seenMessages() {
       DbRefForSeen=FirebaseDatabase.getInstance().getReference("Chats");
       SeenListener=DbRefForSeen.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot ds:snapshot.getChildren()){
                   ChatModel chat=ds.getValue(ChatModel.class);
                   if(chat.getReceiver().equals(myUid)&&chat.getSender().equals(hisUid)){
                       HashMap<String,Object> seenupdate=new HashMap<>();
                       seenupdate.put("isseen",true);
                       ds.getRef().updateChildren(seenupdate);
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }

    private void readMessages() {
        chatList=new ArrayList<>();
        DatabaseReference chatdbref=FirebaseDatabase.getInstance().getReference("Chats");
        chatdbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){

                    ChatModel chat=ds.getValue(ChatModel.class);
                    if(chat.getReceiver().equals(myUid)&&chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid)&&chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }
                    chatAdapter=new ChatAdapter(ChatActivity.this,chatList,hisImage,myimage);
                    chatAdapter.notifyDataSetChanged();
                    chatrecycler.setAdapter(chatAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void SendMessage(final String my_msg) {
        String timestamp=String.valueOf(System.currentTimeMillis());
    DatabaseReference msgref=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> chatsmsg=new HashMap<>();
        chatsmsg.put("sender",myUid);
        chatsmsg.put("receiver",hisUid);
        chatsmsg.put("message",my_msg);
        chatsmsg.put("timestamp",timestamp);
        chatsmsg.put("isseen",false);
        msgref.child("Chats").push().setValue(chatsmsg);

        final DatabaseReference database=FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Allusers_models user=snapshot.getValue(Allusers_models.class);
                if(notify){
                   sendNotification(hisUid,user.getName(),my_msg);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(final String hisUid, final String name, final String my_msg) {
        DatabaseReference allTokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    Token token=ds.getValue(Token.class);
                    Data data=new Data(myUid,name+" : "+my_msg,"New Message",hisUid,R.drawable.ic_baseline_message_24);
                    Sender sender=new Sender(data,token.getToken());
                    //fcm json object request
                    try {
                        JSONObject senderJsonObj=new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj,
                                new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ChatActivity.this,""+error.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String,String> headers=new HashMap<>();
                                headers.put("Content-Type","application/json");
                                headers.put("Authorization","key=AAAAsD2mTRM:APA91bFydrz1o0K_2PqpZyANEbBqxIk8Yh_GO-GOSG4sJug2M7EKkTUftkKfQlfUPjgb_kJ4pAMqc1kL6j-UILhLaz_x8iVRrkm51cdFgGK9LIkKBTtZ2QvulccmWjMu28ebDh7IxAsk");
                                return headers;
                            }
                        };
                       requestQueue.add(jsonObjectRequest);
                    } catch (JSONException e) {
                        Toast.makeText(ChatActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkUserLoginState() {
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            myUid=user.getUid();
        }else
        {
            startActivity(new Intent(ChatActivity.this,MainActivity.class));
            finish();
        }
    }

    private void checkOnlineStatus(String online){
        DatabaseReference onlineref=FirebaseDatabase.getInstance().getReference("Users");
        HashMap<String,Object> result=new HashMap<>();
        result.put("onlineStatus",online);
        onlineref.child(myUid).updateChildren(result);
    }
    private void checkTypingStatus(String typing){
        DatabaseReference onlineref=FirebaseDatabase.getInstance().getReference("Users");
        HashMap<String,Object> result=new HashMap<>();
        result.put("typing",typing);
        onlineref.child(myUid).updateChildren(result);
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkTypingStatus("noOne");
        checkOnlineStatus(String.valueOf(System.currentTimeMillis()));
        DbRefForSeen.removeEventListener(SeenListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        seenMessages();
        checkOnlineStatus("online");
    }

    @Override
    protected void onStart() {
        checkUserLoginState();
        checkOnlineStatus("online");
        readMessages();
        seenMessages();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            soundPool.release();
            soundPool=null;
        }
    }

    @Override
    protected void onRestart() {
        seenMessages();
        super.onRestart();
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
        Intent intent = new Intent(ChatActivity.this, ImagePickerActivity.class);
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
        Intent intent = new Intent(ChatActivity.this, ImagePickerActivity.class);
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
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

//                    sendedimageUri=uri;

                } catch (IOException e) {

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
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ChatActivity.this.openSettings();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
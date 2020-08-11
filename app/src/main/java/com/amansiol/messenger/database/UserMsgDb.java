package com.amansiol.messenger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.amansiol.messenger.models.ChatModel;

public class UserMsgDb extends SQLiteOpenHelper {

    final private static String DATABASE_NAME="com.amansiol.messenger.database.User_msg.db";
    final private static String TABLE_NAME="ALL_MSG";
    final private static String CLM_SENDER="SENDER";
    final private static String CLM_TIMESTAPM_iD="TIMESTAMP_ID";
    final private static String CLM_RECIEVER="RECIEVER";
    final private static String CLM_MESSAGE="MESSAGE";
    final private static String CLM_IS_SEEN="IS_SEEN";
    final private static int TABLE_VERSION=1;
    Context context;
    private static String CREATETABLE="CREATE TABLE "+TABLE_NAME+" ("+
            CLM_TIMESTAPM_iD+" TEXT,"+
            CLM_SENDER+" TEXT,"+
            CLM_RECIEVER+" TEXT,"+
            CLM_MESSAGE+" TEXT,"+
            CLM_IS_SEEN+" BOOLEAN" +")";


    public UserMsgDb(@Nullable Context context) {
        super(context, DATABASE_NAME, null, TABLE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATETABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public long Insert_Msg(ChatModel chatModel)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(CLM_TIMESTAPM_iD,chatModel.getTimestamp());
        contentValues.put(CLM_SENDER,chatModel.getSender());
        contentValues.put(CLM_RECIEVER,chatModel.getReceiver());
        contentValues.put(CLM_MESSAGE,chatModel.getMessage());
        contentValues.put(CLM_IS_SEEN,chatModel.isIsseen());
        long id=db.insert(TABLE_NAME,null,contentValues);
        db.close();
        return id;
    }
    public void UpdateisSeen(String timestamp, boolean isseen)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(CLM_IS_SEEN,isseen);
        db.update(TABLE_NAME,contentValues,CLM_TIMESTAPM_iD+"=?",new String[]{String.valueOf(timestamp)});
    }
//    public Task getRowbyId(int id)
//    {
//        SQLiteDatabase db=this.getReadableDatabase();
//        Cursor res = null;
//        Task temp=new Task();
//        try {
//            res= db.rawQuery("SELECT * FROM " + DATABASE_TABLE_NAME + " WHERE " + ID + "=?", new String[]{String.valueOf(id)});
//        }catch (Exception e)
//        {
//            Toast.makeText(context,e+"",Toast.LENGTH_SHORT).show();
//        }
//        if(res!=null)
//        {   res.moveToFirst();
//            temp.setId(res.getInt(0));
//            temp.setTitle(res.getString(1));
//            temp.setMessage(res.getString(2));
//            temp.setDate(res.getString(3));
//            temp.setWeek(res.getString(4));
//            temp.setTime(res.getString(5));
//            temp.setSmile(res.getInt(6));
//        }
//        return temp;
//    }
    public Cursor getAllMsg(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        return res;
    }
}

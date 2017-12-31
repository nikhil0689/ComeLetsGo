package com.nikhil.sdsu.comeletsgo.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.nikhil.sdsu.comeletsgo.Pojo.SignUpDetailsPOJO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikhil on 12/25/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    public static final String DATABASE_SIGN_UP_DETAILS = "sign_up_database.db";
    public static final String TABLE_NAME_SIGN_UP_DETAILS = "sign_up_table";
    public static final String CONTACT = "contact";
    public static String EMAIL = "emailId";
    public static final String NAME = "name";
    public static final String CAR_NAME = "carName";
    public static final String CAR_COLOR = "carColor";
    public static final String CAR_LICENSE = "carLicense";
    public static final String ADDRESS = "address";
    Context context;
    public static final String CREATE_SIGN_UP_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME_SIGN_UP_DETAILS+" " +
            "("+CONTACT+" TEXT PRIMARY KEY," +
            ""+EMAIL+" TEXT," +
            ""+NAME+" TEXT," +
            ""+CAR_NAME+" TEXT," +
            ""+CAR_COLOR+" TEXT," +
            ""+CAR_LICENSE+" TEXT," +
            ""+ADDRESS+" TEXT)";

    public DatabaseHelper(Context context) {
        super(context,DATABASE_SIGN_UP_DETAILS,null,1);
        Log.d("rew","in database helper constructor");
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("rew","in database helper on create");
        sqLiteDatabase.execSQL(CREATE_SIGN_UP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d("rew","in database helper upgrade");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_SIGN_UP_DETAILS);
        onCreate(sqLiteDatabase);
    }

    public boolean insertUser(SignUpDetailsPOJO signUpDetailsPOJO){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACT,signUpDetailsPOJO.getContact());
        contentValues.put(NAME,signUpDetailsPOJO.getName());
        contentValues.put(EMAIL,signUpDetailsPOJO.getEmailId());
        contentValues.put(CAR_NAME,signUpDetailsPOJO.getCarName());
        contentValues.put(CAR_COLOR,signUpDetailsPOJO.getCarColor());
        contentValues.put(CAR_LICENSE,signUpDetailsPOJO.getCarLicence());
        //contentValues.put(ADDRESS,signUpDetailsPOJO.getAddress());
        try{
            Log.d("rew","In Insert user method");
            sqLiteDatabase.insertOrThrow(TABLE_NAME_SIGN_UP_DETAILS,null,contentValues);
        }catch (SQLiteConstraintException e){
            Log.d("rew","Exception: insert Failure: "+e);
            return false;
        }
        return true;
    }

    public List<SignUpDetailsPOJO> getUserData(String sql){
        Log.d("rew","In Get User Data list Method");
        ArrayList<SignUpDetailsPOJO> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        cursor.moveToFirst();
        SignUpDetailsPOJO signUpDetailsPOJO = new SignUpDetailsPOJO();
        signUpDetailsPOJO.setContact(cursor.getString(0));
        signUpDetailsPOJO.setEmailId(cursor.getString(1));
        signUpDetailsPOJO.setName(cursor.getString(2));
        signUpDetailsPOJO.setCarName(cursor.getString(3));
        signUpDetailsPOJO.setCarColor(cursor.getString(4));
        signUpDetailsPOJO.setCarLicence(cursor.getString(5));
        //signUpDetailsPOJO.setAddress(cursor.getString(6));
        list.add(signUpDetailsPOJO);
        Log.d("rew","List value of object: "+list.get(0).getContact());
        sqLiteDatabase.close();
        return list;
    }

    public boolean updateProfileData(SignUpDetailsPOJO signUpDetailsPOJO){
        Log.d("rew","In updateProfileData Method");
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACT,signUpDetailsPOJO.getContact());
        contentValues.put(CAR_NAME,signUpDetailsPOJO.getCarName());
        contentValues.put(CAR_COLOR,signUpDetailsPOJO.getCarColor());
        contentValues.put(CAR_LICENSE,signUpDetailsPOJO.getCarLicence());
        //contentValues.put(ADDRESS,signUpDetailsPOJO.getAddress());
        try{
            sqLiteDatabase.update(TABLE_NAME_SIGN_UP_DETAILS,contentValues,EMAIL+"=?",
                    new String[] {signUpDetailsPOJO.getEmailId()});
        }catch (SQLException e){
            Log.d("rew","Exception: "+e);
            return false;
        }
        return true;
    }
}

package com.example.bloggingapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bloggingapp.model.MessageDataModel;
import com.example.bloggingapp.model.UsersModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager";
    private static final String TABLE_MESSAGES = "messages";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DETAILS = "details";
    private static final String KEY_IMAGEURI = "image_uri";
    private static final String KEY_TIME = "time";


    private static final String TABLE_USERS = "users";
    private static final String KEY_ID_User = "id";
    private static final String KEY_USERNAME = "first_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        //UsersTable
        db.execSQL(" CREATE TABLE " + TABLE_USERS + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_USERNAME + " TEXT NOT NULL, " +
                KEY_EMAIL + " TEXT NOT NULL, " +
                KEY_PASSWORD + " TEXT NOT NULL)"
        );

        //MessagesTable
        db.execSQL(" CREATE TABLE " + TABLE_MESSAGES + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_TITLE + " TEXT NOT NULL, " +
                KEY_DETAILS + " TEXT NOT NULL, " +
                KEY_TIME + " TEXT NOT NULL, " +
                KEY_IMAGEURI + " TEXT NOT NULL);"
        );

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        // Create tables again
        onCreate(db);
    }

    // code to add the new user
    public void register(UsersModel users) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, users.getUserName());
        values.put(KEY_EMAIL, users.getEmail());
        values.put(KEY_PASSWORD, users.getPassword());

        // Inserting Row
        db.insert(TABLE_USERS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get all users in a list view
    public List<UsersModel> getAllUsers() {
        List<UsersModel> usersList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UsersModel users = new UsersModel();
                users.setId(Integer.parseInt(cursor.getString(0)));
                users.setUserName(cursor.getString(1));
                users.setEmail(cursor.getString(2));
                users.setPassword(cursor.getString(3));
                // Adding contact to list
                usersList.add(users);
            } while (cursor.moveToNext());
        }

        // return contact list
        return usersList;
    }

    // code to add the new user
    public void AddMessage(MessageDataModel messageDataModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, messageDataModel.getMessageTitle());
        values.put(KEY_DETAILS, messageDataModel.getDetail());
        values.put(KEY_TIME, messageDataModel.getTime());
        values.put(KEY_IMAGEURI, messageDataModel.getImage_uri());
        // Inserting Row
        db.insert(TABLE_MESSAGES, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single user
    MessageDataModel getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MESSAGES, new String[]{KEY_ID,
                        KEY_TITLE, KEY_DETAILS, KEY_TIME, KEY_IMAGEURI}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        // return contact
        return new MessageDataModel(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
    }

    // code to get all users in a list view
    public List<MessageDataModel> getALlMessages() {
        List<MessageDataModel> meesageList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MessageDataModel messageDataModel = new MessageDataModel();
                messageDataModel.setId(Integer.parseInt(cursor.getString(0)));
                messageDataModel.setMessageTitle(cursor.getString(1));
                messageDataModel.setDetail(cursor.getString(2));
                messageDataModel.setTime(cursor.getString(3));
                messageDataModel.setImage_uri(cursor.getString(4));


                // Adding contact to list
                meesageList.add(messageDataModel);
            } while (cursor.moveToNext());
        }

        // return contact list
        return meesageList;
    }

    // code to update the single user
    public int updateMessage(MessageDataModel messageDataModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, messageDataModel.getMessageTitle());
        values.put(KEY_DETAILS, messageDataModel.getDetail());
        values.put(KEY_TIME, messageDataModel.getTime());
        values.put(KEY_IMAGEURI, messageDataModel.getImage_uri());


        // updating row
        return db.update(TABLE_MESSAGES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(messageDataModel.getId())});
    }

    public void deleteMessage(MessageDataModel messageDataModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGES, KEY_ID + " = ?",
                new String[]{String.valueOf(messageDataModel.getId())});
        db.close();
    }


}
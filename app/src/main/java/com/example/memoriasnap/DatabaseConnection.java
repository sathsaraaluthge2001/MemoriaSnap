package com.example.memoriasnap;



import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseConnection extends SQLiteOpenHelper {

    public static final String TABLE_MEMORY = "memory";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String TabaleName="lock";
    public static final String PasswordColumn="password";
    public static final String TableCal="calender";
    public static final String dateCol="caldate";
    public static final String eventCol="event";




    private static final String DATABASE_NAME = "memories.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS memory ("
                + "image TEXT,"
                + "title TEXT PRIMARY KEY,"
                + "date TEXT,"
                + "description TEXT)";
        db.execSQL(createTableQuery);
        Log.d("DatabaseConnection", "Table created successfully");

        //password table
        String createLockTableQuery = "CREATE TABLE IF NOT EXISTS " + TabaleName + " ("
                + PasswordColumn + " TEXT PRIMARY KEY)";
        db.execSQL(createLockTableQuery);
        Log.d("DatabaseConnection", "Lock Table created successfully");

        //calender table
        String calTableQuery = "CREATE TABLE IF NOT EXISTS calender ("
                + "caldate TEXT PRIMARY KEY,"
                + "event TEXT )";
        db.execSQL(calTableQuery);
        Log.d("DatabaseConnection", "Table created successfully");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMORY);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + TabaleName);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + TableCal);
        onCreate(db);
    }

    public long insertMemory(String image, String title, String date, String description) {
        long result = -1;
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_IMAGE, image);
            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_DATE, date);
            values.put(COLUMN_DESCRIPTION, description);

            result = db.insert(TABLE_MEMORY, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return result;
    }

    public ArrayList<ModelMemories>getAllMemories(){
        ArrayList<ModelMemories>memoriesList = new ArrayList<>();
        String query="select * from memory";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do {
                int columnIndexImage = cursor.getColumnIndex(DatabaseConnection.COLUMN_IMAGE);
                int columnIndexTitle = cursor.getColumnIndex(DatabaseConnection.COLUMN_TITLE);
                int columnIndexDate = cursor.getColumnIndex(DatabaseConnection.COLUMN_DATE);
                int columnIndexDescription = cursor.getColumnIndex(DatabaseConnection.COLUMN_DESCRIPTION);

                if (columnIndexImage != -1 && columnIndexTitle != -1 && columnIndexDate != -1 && columnIndexDescription != -1) {
                    String image = cursor.getString(columnIndexImage);
                    String title = cursor.getString(columnIndexTitle);
                    String date = cursor.getString(columnIndexDate);
                    String description = cursor.getString(columnIndexDescription);

                    ModelMemories modelMemories = new ModelMemories(title, image, date, description);
                    memoriesList.add(modelMemories);

                } else {

                    Log.e("MemoryDetail", "One or more columns not found in the cursor");

                }
            } while (cursor.moveToNext());
        }

        db.close();
        return memoriesList;
    }

    public ArrayList<ModelMemories>searchMemories(String query){
        ArrayList<ModelMemories>memoriesList = new ArrayList<>();
        String querySearch = "SELECT * FROM " + TABLE_MEMORY + " WHERE " + COLUMN_TITLE + " LIKE '%" + query + "%'";
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querySearch, null);


        if(cursor.moveToFirst()){
            do {
                int columnIndexImage = cursor.getColumnIndex(DatabaseConnection.COLUMN_IMAGE);
                int columnIndexTitle = cursor.getColumnIndex(DatabaseConnection.COLUMN_TITLE);
                int columnIndexDate = cursor.getColumnIndex(DatabaseConnection.COLUMN_DATE);
                int columnIndexDescription = cursor.getColumnIndex(DatabaseConnection.COLUMN_DESCRIPTION);

                if (columnIndexImage != -1 && columnIndexTitle != -1 && columnIndexDate != -1 && columnIndexDescription != -1) {
                    String image = cursor.getString(columnIndexImage);
                    String title = cursor.getString(columnIndexTitle);
                    String date = cursor.getString(columnIndexDate);
                    String description = cursor.getString(columnIndexDescription);

                    ModelMemories modelMemories = new ModelMemories(title, image, date, description);
                    memoriesList.add(modelMemories);

                } else {

                    Log.e("MemoryDetail", "One or more columns not found in the cursor");

                }
            } while (cursor.moveToNext());
        }

        db.close();
        return memoriesList;
    }
    public void deleteData(String title){
        SQLiteDatabase db=getWritableDatabase();
        db.delete(TABLE_MEMORY,COLUMN_TITLE+"=?",new String[]{title});
        db.close();
    }
    public int getRecordCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM memory", null);
        int count = cursor.getCount();
        cursor.close();
        db.close(); // Close the database after use
        return count;
    }

    //App lock part

    //insert password

    public long insertPassword(String password){
        long result = -1;
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(PasswordColumn, password);

            result = db.insert(TabaleName, null, values);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return result;
    }

    public int deleteAllPasswords() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TabaleName, null, null);
        db.close();
        return result;
    }

    public String getPassword() {
        String password = "";
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            String[] columns = {PasswordColumn}; // Assuming PasswordColumn is the column name where the password is stored
            cursor = db.query(TabaleName, columns, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int getColumnIndex = cursor.getColumnIndex(DatabaseConnection.PasswordColumn);
                if(getColumnIndex != -1){
                    password = cursor.getString(getColumnIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return password;
    }


    public long insertEvent(String date,String event){
        long result = -1;
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(dateCol, date);
            values.put(eventCol,event);

            result = db.insert(TableCal, null, values);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return result;
    }

    public void deleteEvent(String date){
        SQLiteDatabase db=getWritableDatabase();
        db.delete(TableCal,dateCol+"=?",new String[]{date});
        db.close();
    }
}

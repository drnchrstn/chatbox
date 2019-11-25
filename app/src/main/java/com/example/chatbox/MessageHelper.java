package com.example.chatbox;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MessageHelper extends SQLiteOpenHelper {
    SQLiteDatabase db;

    public static final String DATABASE_NAME = "message_database";

    public static final String MESSAGE_TABLE = "message_table";
    public static final String COL_ID = "Id";
    public static final String COL_ISQUOTE = "IsQuote";
    public static final String COL_QUOTE = "QuoteContent";
    public static final String COL_MESSAGE = "Message";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS";


    public MessageHelper(Context context) {
        super(context, DATABASE_NAME, null, 4);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

      db.execSQL("CREATE TABLE " + MESSAGE_TABLE + "(Id INTEGER PRIMARY KEY AUTOINCREMENT, Message TEXT, quoteContent TEXT)");
        db.execSQL("CREATE TABLE " + LinkDb.TABLE_NAME + "(Id INTEGER PRIMARY KEY AUTOINCREMENT, Link TEXT, Title Text, Description Text, Image TEXT, Url TEXT, SiteName TEXT, Favicon TEXT, Mediatype TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
//        db.execSQL("ALTER TABLE message_table ADD COLUMN quoteContent TEXT");
        //db.execSQL("ALTER TABLE message_table ADD COLUMN isQuote BOOLEAN DEFAULT 0");


       //db.execSQL("CREATE TABLE " + LinkDb.TABLE_NAME + "(Id INTEGER PRIMARY KEY AUTOINCREMENT, Link TEXT, Title Text, Description Text, Image TEXT, Url TEXT, SiteName TEXT, Favicon TEXT, Mediatype TEXT)");
        db.execSQL(DROP_TABLE + MESSAGE_TABLE);
        onCreate(db);
    }
}

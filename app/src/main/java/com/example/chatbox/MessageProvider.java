package com.example.chatbox;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MessageProvider extends ContentProvider {

    MessageHelper messageHelper;
    private static final int ALL_MESSAGE = 1;
    private static final int SINGLE_MESSAGE = 2;
    private static final String AUTHORITY = "com.example.chatbox.contentprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/messages");
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "messages", ALL_MESSAGE);
        uriMatcher.addURI(AUTHORITY, "messages/#", SINGLE_MESSAGE);
    }




    @Override
    public boolean onCreate() {
        messageHelper = new MessageHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = messageHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(MessageDb.TABLE_NAME);
        String id = null;
        switch (uriMatcher.match(uri)){
            case ALL_MESSAGE:
                //Nothing
            break;

            case SINGLE_MESSAGE:
                id = uri.getLastPathSegment();
                queryBuilder.appendWhere(MessageDb.KEY_ROWID + "=" + id);
                break;
             default:
                throw new IllegalArgumentException("Unsupported Uri: " +uri);
        }
//        int intOffset = Integer.parseInt(offset);
//        String limitArg = id + 30;


        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);


        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case ALL_MESSAGE:
                return "vnd.android.cursor.dir/vnd.com.example.android.chatbox.contentprovider.messages";
            case SINGLE_MESSAGE:
                return "vnd.android.cursor.item/vnd.com.example.android.chatbox.contentprovider.messages";
            default:
                throw new IllegalArgumentException("Unsupported URI: " +uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        SQLiteDatabase db = messageHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case ALL_MESSAGE:
                //nothing
            break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " +uri);
        }
        long id = db.insert(MessageDb.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(CONTENT_URI + "/" +id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = messageHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case ALL_MESSAGE:
                //nothing
            break;
            case SINGLE_MESSAGE:
                String id = uri.getPathSegments().get(1);
                selection = MessageDb.KEY_ROWID + "=" +id +
                        (!TextUtils.isEmpty(selection) ?
                                "AND ("+selection+')' : "");
            break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        int deleteCount = db.delete(MessageDb.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return deleteCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = messageHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)){
            case ALL_MESSAGE:
                //do nothing
                break;
            case SINGLE_MESSAGE:
                String id = uri.getPathSegments().get(1);
                selection = MessageDb.KEY_ROWID + "=" +id +
                        (!TextUtils.isEmpty(selection) ?
                                "AND ("+selection+')' : "");
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " +uri);
        }
        int updateCount = db.update(MessageDb.TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }
}

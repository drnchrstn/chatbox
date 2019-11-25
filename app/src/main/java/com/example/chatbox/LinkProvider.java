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

public class LinkProvider extends ContentProvider {

    MessageHelper messageHelper;
    private static final int ALL_LINK = 1;
    private static final int SINGLE_LINK = 2;
    private static final String AUTHORITY = "com.example.chatbox.link.contentprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/links");
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "links", ALL_LINK);
        uriMatcher.addURI(AUTHORITY, "links/#", SINGLE_LINK);
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
        queryBuilder.setTables(LinkDb.TABLE_NAME);
        switch (uriMatcher.match(uri)){
            case ALL_LINK:
                //Nothing
                break;

            case SINGLE_LINK:
                String id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(LinkDb.KEY_ROWID + "=" + id);
                break;
            default:
                throw new IllegalArgumentException("Unsupported Uri: " +uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);


        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        switch (uriMatcher.match(uri)){
            case ALL_LINK:
                return "vnd.android.cursor.dir/vnd.com.example.android.chatbox.contentprovider.links";
            case SINGLE_LINK:
                return "vnd.android.cursor.item/vnd.com.example.android.chatbox.contentprovider.links";
            default:
                throw new IllegalArgumentException("Unsupported URI: " +uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        SQLiteDatabase db = messageHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case ALL_LINK:
                //nothing
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " +uri);
        }
        long id = db.insert(LinkDb.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(CONTENT_URI + "/" +id);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = messageHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case ALL_LINK:
                //nothing
                break;
            case SINGLE_LINK:
                String id = uri.getPathSegments().get(1);
                selection = LinkDb.KEY_ROWID + "=" +id +
                        (!TextUtils.isEmpty(selection) ?
                                "AND ("+selection+')' : "");
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        int deleteCount = db.delete(LinkDb.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return deleteCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}

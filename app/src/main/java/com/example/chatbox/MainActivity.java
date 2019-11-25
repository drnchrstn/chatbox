package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.util.ArrayList;

import io.github.ponnamkarthik.richlinkpreview.RichLinkView;

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>
{
    static View includelayout;
    RichLinkView richLinkView;
    EditText EditMessage;
    TextView TxtMessageContent, TxtMessageId, TxtPosition;
    Button BtnSend;
    static RecyclerView recyclerView;
    ArrayList<Message> messageList;
    MessageHelper messageHelper;
    MessageCursorAdapter mAdapter;
    static LinearLayoutManager mLinearLayoutManager;
    FloatingActionButton ScrollToBottom;
    public final int offset = 30;
    private int page = 0;
    public static int dataLimit = 15;
    public static int finalPos;
    public static boolean sent = false;
    public int activeOffset = 1;
    public int needToScroll = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScrollToBottom = findViewById(R.id.ScrollToBottom);
        includelayout = findViewById(R.id.includelayout);
        TxtMessageContent = includelayout.findViewById(R.id.TxtMessageContent);
        TxtMessageId = includelayout.findViewById(R.id.TxtMessageId);
        TxtPosition = includelayout.findViewById(R.id.TxtPosition);
        includelayout.setVisibility(View.GONE);
        messageHelper = new MessageHelper(MainActivity.this);
        ScrollToBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToBottom();
            }
        });
        LoaderManager.getInstance(MainActivity.this).initLoader(1, null, MainActivity.this);
        recyclerView = findViewById(R.id.recyclerView);
         mLinearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
         mLinearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new MessageCursorAdapter(MainActivity.this);
        recyclerView.setAdapter(mAdapter);
        if (mAdapter == null){
            return;
        }




        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = mLinearLayoutManager.getChildCount();
                int totalItemCount = mLinearLayoutManager.getItemCount();
                int pastVisibleItems = mLinearLayoutManager.findFirstVisibleItemPosition();

                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                        dataLimit +=15;
                        LoaderManager.getInstance(MainActivity.this).restartLoader(1, null, MainActivity.this);
                }

            }
        });


        EditMessage = findViewById(R.id.EditMessage);
        BtnSend = findViewById(R.id.BtnSend);
        BtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myMessage = EditMessage.getText().toString();
                if (myMessage.length() == 0){
                    Toast.makeText(MainActivity.this, "Unable to send message", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(myMessage);
                    sent = true;
                    recyclerView.scrollToPosition(0);
                    includelayout.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                    EditMessage.setText("");
                }

            }
        });



    }






    public void sendMessage(String myMessage){
        Uri newUri;
        ContentValues newValues = new ContentValues();
        if (includelayout.getVisibility() == View.VISIBLE){
            String myId = TxtMessageId.getText().toString();
            String messageContent = TxtMessageContent.getText().toString();
            int messagePos = Integer.parseInt(TxtPosition.getText().toString());

            Message message = new Message(myId, messageContent, messagePos);
            Gson gson = new Gson();
            String json = gson.toJson(message);
            newValues.put(MessageDb.KEY_QUOTE, json);
            newValues.put(MessageDb.KEY_MESSAGE, myMessage);
            newUri = getContentResolver().insert(MessageProvider.CONTENT_URI, newValues);
        }else{


            for (int i = 0; i<1000; i++){
                newValues.put(MessageDb.KEY_MESSAGE, i +myMessage);
                newUri = getContentResolver().insert(MessageProvider.CONTENT_URI, newValues);
            }


        }

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        if (id == 1){
            return messageLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {


        mAdapter.swapCursor(data);
        if (needToScroll != -1){
            recyclerView.scrollToPosition(needToScroll);
            recyclerView.scrollToPosition(needToScroll);
//            RecyclerView.ViewHolder view = recyclerView.findViewHolderForLayoutPosition(needToScroll);

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        if (needToScroll != -1){
            recyclerView.scrollToPosition(needToScroll);
            needToScroll = -1;

        }
    }


    private Loader<Cursor> messageLoader(){
        Uri uri = MessageProvider.CONTENT_URI;
        String[] projection = {
            MessageDb.KEY_ROWID,
            MessageDb.KEY_MESSAGE,
            MessageDb.KEY_QUOTE
        };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = "Id DESC LIMIT " + dataLimit;
        return new CursorLoader(
                MainActivity.this,
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }
    public void scrollToBottom(){
        recyclerView.scrollToPosition(0);
    }

    public void getMessagePosition(int quoteId){
        SQLiteDatabase db = messageHelper.getWritableDatabase();
        String sql = "SELECT " + MessageDb.KEY_ROWID + " FROM " + MessageDb.TABLE_NAME + " ORDER BY " + MessageDb.KEY_ROWID + " DESC";
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()){
//            int position = cursor.getPosition();
            int id = cursor.getInt(cursor.getColumnIndex(MessageDb.KEY_ROWID));
            if(quoteId == id){
                Log.v("", "");
                int position = cursor.getPosition();
                if (position < dataLimit){
                    recyclerView.scrollToPosition(position);
                }else{
                    while (position > dataLimit){
                        dataLimit += 15;

                        if (dataLimit >= position){
                            needToScroll = position;
                            LoaderManager.getInstance(MainActivity.this).restartLoader(1, null, this);

                            break;
                        }
                    }
                }

//                Toast.makeText(MainActivity.this, "" + cursor.getPosition(), Toast.LENGTH_SHORT).show();

//                return position;
            }
        }

    }

//    public void jumptToMessage(int quoteId){
//        SQLiteDatabase db = messageHelper.getWritableDatabase();
//        String sql = "SELECT " + MessageDb.KEY_ROWID + " FROM " + MessageDb.TABLE_NAME;
//        Cursor cursor = db.rawQuery(sql, null);
//        while(cursor.moveToNext()){
//            int position = cursor.getPosition();
//            int id = cursor.getInt(cursor.getColumnIndex(MessageDb.KEY_ROWID));
//            if(quoteId == id){
//
//
//
//
////                Log.v("", "");
////                return position;
//            }
//        }
//
//
//
//    }

}

package com.example.chatbox;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Color;
import android.net.Uri;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;


import static com.example.chatbox.MainActivity.includelayout;


import java.util.regex.Pattern;
import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.RichLinkListener;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;


public class MessageCursorAdapter extends BaseCursorAdapter<MessageCursorAdapter.MyViewHolder>{
    Context context;
    private DiskLruCacheUtil mDiskLruCacheUtil;
    int VersionNumber;
    long CACHE_SIZE;
    MessageHelper messageHelper;
    private int lastCheckedPosition = -1;

    public MessageCursorAdapter(Context context) {
        super(null);
        this.context = context;
        VersionNumber = 1;
        CACHE_SIZE = 500 * 1024;
        mDiskLruCacheUtil =  new DiskLruCacheUtil(context, "journal");
        messageHelper = new MessageHelper(context);


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final Cursor cursor) {
        final String id = cursor.getString(0);
        final String message = cursor.getString(1);
        final String quoteContent = cursor.getString(2);
        holder.TxtMessage.setText(message);
        holder.TxtMessage.setMovementMethod(LinkMovementMethodExt.getInstantce(context));
        holder.myLayout.setSelected(true);
        TextView TxtContent, TxtId;

        holder.myLayout.setSelected(cursor.getPosition() == lastCheckedPosition);


        if (quoteContent != null){


            holder.quotelayout.setVisibility(View.VISIBLE);
            try {
                JSONObject jsonObject = new JSONObject(quoteContent);
                holder.quoteId = Integer.parseInt(jsonObject.getString("Id"));
                final String quoteBody = jsonObject.getString("messageBody");
                holder.currentPos = jsonObject.getInt("position");
                TxtId = holder.quotelayout.findViewById(R.id.TxtMessageId);
                TxtId.setVisibility(View.GONE);
                TxtContent = holder.quotelayout.findViewById(R.id.TxtMessageContent);
                TxtContent.setText(quoteBody);
                holder.quotelayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ((MainActivity) context).getMessagePosition(holder.quoteId);

                    }
                });


            }catch (JSONException ex){
                ex.printStackTrace();
            }
        }
        else{
            holder.quotelayout.setVisibility(View.GONE);
        }


        final String[] menu = new String[]{
                "Reply",
                "Delete"
//                "Save Contact",
//                "Copy"
        };
        holder.BtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                includelayout.setVisibility(View.GONE);
            }
        });
        holder.TxtMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                             switch (i){
                                 case 0:
                                     int pos = holder.getLayoutPosition();
                                     final String myMessage = message;
                                     includelayout.setVisibility(View.VISIBLE);
                                         Message thisMessage = new Message(id, myMessage, pos);
                                     Toast.makeText(context, "" + pos, Toast.LENGTH_SHORT).show();
                                     holder.TxtPosition.setText(thisMessage.getPosition() + "");
                                     holder.TxtMessageId.setText(thisMessage.getId());
                                     holder.TxtMessageContent.setText(thisMessage.getMessageBody());
                                     Toast.makeText(context, "Reply", Toast.LENGTH_SHORT).show();
                                     break;
                                 case 1:
                                     ContentResolver cr = context.getContentResolver();
                                     cr.delete(MessageProvider.CONTENT_URI, MessageDb.KEY_ROWID + " = " +id, null);
                            if (!cr.equals(true)){
                            Toast.makeText(context, "Message was deleted", Toast.LENGTH_SHORT).show();
                                }else{
                            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                        }
                                     break;
                             }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });

        Pattern pattern = Patterns.WEB_URL;
        if (pattern.matcher(message).find()){
            final String customUrl = UrlGetter(message);
            if (customUrl.contains("http://") || message.contains("https")){


                String key = mDiskLruCacheUtil.getStringCache("" + customUrl);
                if (key != null){
                    try {
                            JSONObject jsonObject = new JSONObject(key);
                            String loadurl = jsonObject.getString("url");
                            String imageurl = jsonObject.getString("imageurl");
                            String title = jsonObject.getString("title");
                            String description = jsonObject.getString("description");
                            String sitename = jsonObject.getString("sitename");
                            String mediatype = jsonObject.getString("mediatype");
                            String favicon = jsonObject.getString("favicon");
                            MetaData metaData = new MetaData();
                            metaData.setUrl(loadurl);
                            metaData.setMediatype(mediatype);
                            metaData.setTitle(title);
                            metaData.setDescription(description);
                            metaData.setFavicon(favicon);
                            metaData.setImageurl(imageurl);
                            metaData.setSitename(sitename);

                            holder.richLinkView.setLinkFromMeta(metaData);
                        holder.richLinkView.setVisibility(View.VISIBLE);
                            holder.richLinkView.setDefaultClickListener(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        holder.richLinkView.setClickListener(new RichLinkListener() {
                            @Override
                            public void onClicked(View view, MetaData meta) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(meta.getUrl()));
                                context.startActivity(intent);
                            }
                        });
                }else{
                    holder.richLinkView.setVisibility(View.GONE);
                    holder.richLinkView.setLink(customUrl, new ViewListener() {

                        @Override
                        public void onSuccess(boolean status) {
                            MetaData metaData = holder.richLinkView.getMetaData();
                            String jsonObj = JsonUtil.toJson(metaData);
                            mDiskLruCacheUtil.put("" + customUrl, jsonObj);
                            holder.bubbleLayout.setMinimumWidth(500);
                            holder.richLinkView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }

            }else{
               final String myUrl = "http://" + customUrl;
                String key = mDiskLruCacheUtil.getStringCache("" + myUrl);

                if (key!= null){

                    try {
                        JSONObject jsonObject = new JSONObject(key);
                        String loadurl = jsonObject.getString("url");
                        String imageurl = jsonObject.getString("imageurl");
                        String title = jsonObject.getString("title");
                        String description = jsonObject.getString("description");
                        String sitename = jsonObject.getString("sitename");
                        String mediatype = jsonObject.getString("mediatype");
                        String favicon = jsonObject.getString("favicon");
                        MetaData MymetaData = new MetaData();
                        MymetaData.setUrl(loadurl);
                        MymetaData.setMediatype(mediatype);
                        MymetaData.setTitle(title);
                        MymetaData.setDescription(description);
                        MymetaData.setFavicon(favicon);
                        MymetaData.setImageurl(imageurl);
                        MymetaData.setSitename(sitename);

                        holder.richLinkView.setLinkFromMeta(MymetaData);
                        holder.richLinkView.setVisibility(View.VISIBLE);
                        holder.richLinkView.setDefaultClickListener(false);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    holder.richLinkView.setClickListener(new RichLinkListener() {
                        @Override
                        public void onClicked(View view, MetaData meta) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(meta.getUrl()));
                            context.startActivity(intent);
                        }
                    });

                }else{
                    holder.richLinkView.setVisibility(View.GONE);
                    holder.richLinkView.setLink(myUrl, new ViewListener() {
                        @Override
                        public void onSuccess(boolean status) {
                            MetaData metaData = holder.richLinkView.getMetaData();
                            String jsonObj = JsonUtil.toJson(metaData);
                            mDiskLruCacheUtil.put("" + myUrl, jsonObj);
                            holder.bubbleLayout.setMinimumWidth(500);
                            holder.richLinkView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                }
            }

        }
        else{
            holder.richLinkView.setVisibility(View.GONE);
        }

        if (holder.richLinkView.getVisibility() == View.VISIBLE) {
            holder.bubbleLayout.setMinimumWidth(500);
        }else if (holder.quotelayout.getVisibility() == View.VISIBLE) {
            holder.bubbleLayout.setMinimumWidth(150);
        }else{
            holder.bubbleLayout.setMinimumWidth(10);
        }


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatcard, parent, false);


        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private MetaData data;
//        public int pos;
        public int quoteId;
        public View quotelayout;
        public TextView TxtMessage,TxtMessageContent,TxtMessageId, TxtPosition;
        public RichLinkView richLinkView;
        public LinearLayout myLayout;
        public LinearLayout bubbleLayout;
        public Button BtnClose;
        public int currentPos;

        public MyViewHolder(@NonNull View v) {
            super(v);
            quotelayout = v.findViewById(R.id.quotelayout);
            TxtPosition = includelayout.findViewById(R.id.TxtPosition);
            TxtMessageId = includelayout.findViewById(R.id.TxtMessageId);
            TxtMessageId.setVisibility(View.GONE);
            BtnClose = includelayout.findViewById(R.id.BtnClose);
            BtnClose.setVisibility(View.VISIBLE);
            TxtMessageContent = includelayout.findViewById(R.id.TxtMessageContent);
            TxtMessageId = includelayout.findViewById(R.id.TxtMessageId);
            bubbleLayout = (LinearLayout) v.findViewById(R.id.bubbleLayout);
            myLayout = (LinearLayout) v.findViewById(R.id.myLayout);
            richLinkView = v.findViewById(R.id.richLinkView);
            TxtMessage = v.findViewById(R.id.TxtMessage);

        }
    }



    @Override
    public Cursor getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }






    public String UrlGetter(String website){
        String myUrl = "";
        Matcher matcher = Patterns.WEB_URL.matcher(website);
        while (matcher.find()){
            int matchstart = matcher.start(1);
            int matchend = matcher.end();
            myUrl = website.substring(matchstart, matchend);
        }
        return myUrl;

    }

    public void highlightItem(int position){




    }

}

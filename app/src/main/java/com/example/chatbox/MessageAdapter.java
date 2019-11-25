package com.example.chatbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Message> messageList;


    public MessageAdapter(Context mContext, ArrayList<Message> messageList) {
        this.mContext = mContext;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatcard, parent, false);



        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.TxtMessage.setText(messageList.get(position).getMessageBody());

    }

    @Override
    public int getItemCount() {
        if (messageList == null){
            return 0;
        }else{
            return messageList.size();
        }




    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView TxtMessage;


        public MyViewHolder(@NonNull View v) {
            super(v);

            TxtMessage = v.findViewById(R.id.TxtMessage);
        }
    }
}

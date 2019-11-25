package com.example.chatbox;

public class Message {

    private String Id;
    private String messageBody;
    private int position;

    public Message(String id, String messageBody, int position) {
        Id = id;
        this.messageBody = messageBody;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public String getMessageBody() {
        return messageBody;
    }


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}

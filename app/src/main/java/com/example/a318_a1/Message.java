package com.example.a318_a1;

public class Message {
    private String msgBody;
    private boolean belongToCurrentUser;

    public Message(String msg, boolean belong){
        msgBody = msg;
        belongToCurrentUser = belong;
    }

    public String getMsgBody(){
        return msgBody;
    }

    public boolean isBelongToCurrentUser(){
        return belongToCurrentUser;
    }
}

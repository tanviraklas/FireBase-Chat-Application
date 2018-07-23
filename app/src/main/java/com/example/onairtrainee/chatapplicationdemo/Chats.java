package com.example.onairtrainee.chatapplicationdemo;

/**
 * Created by onAir on 02-Jun-18.
 */

public class Chats
{
    private String user_status;

    private Chats()
    {

    }

    public Chats(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }
}

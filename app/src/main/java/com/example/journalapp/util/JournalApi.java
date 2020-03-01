package com.example.journalapp.util;

import android.app.Application;

public class JournalApi extends Application {
    String username;
    String userId;

    public static JournalApi INSTANCE;

    public static JournalApi getINSTANCE(){
        if (INSTANCE==null)
            INSTANCE=new JournalApi();

        return INSTANCE;
    }

    public JournalApi() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

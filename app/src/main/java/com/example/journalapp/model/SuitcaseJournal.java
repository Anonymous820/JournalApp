package com.example.journalapp.model;

import com.google.firebase.Timestamp;

public class SuitcaseJournal {
    String title;
    String thought;
    Timestamp timeAdded;
    String imageUrl;
    String userName;
    String userId;

    public SuitcaseJournal() {
    }

    public SuitcaseJournal(String title, String thought, Timestamp timeAdded, String imageUrl, String userName, String userId) {
        this.title = title;
        this.thought = thought;
        this.timeAdded = timeAdded;
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public String toString() {
        return "SuitcaseJournal{" +
                "title='" + title + '\'' +
                ", thought='" + thought + '\'' +
                ", timeAdded=" + timeAdded +
                ", imageUrl='" + imageUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}

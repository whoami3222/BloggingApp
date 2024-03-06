package com.example.bloggingapp.model;

import java.io.Serializable;

public class MessageDataModel implements Serializable {
    int id;
    String messageTitle,detail,time,image_uri;

    boolean isSelected=false;

    public MessageDataModel(int id, String messageTitle, String detail, String time, String image_uri) {
        this.id = id;
        this.messageTitle = messageTitle;
        this.detail = detail;
        this.time = time;
        this.image_uri = image_uri;
    }

    public MessageDataModel(String messageTitle, String detail, String time, String image_uri) {
        this.messageTitle = messageTitle;
        this.detail = detail;
        this.time = time;
        this.image_uri = image_uri;
    }

    public MessageDataModel(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

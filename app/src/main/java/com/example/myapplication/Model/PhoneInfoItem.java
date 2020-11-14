package com.example.myapplication.Model;

public class PhoneInfoItem {
    private String infoName,infoValue;

    public String getInfoName() {
        return infoName;
    }

    public void setInfoName(String infoName) {
        this.infoName = infoName;
    }

    public String getInfoValue() {
        return infoValue;
    }

    public void setInfoValue(String infoValue) {
        this.infoValue = infoValue;
    }

    public PhoneInfoItem(String infoName, String infoValue) {
        this.infoName = infoName;
        this.infoValue = infoValue;
    }
}

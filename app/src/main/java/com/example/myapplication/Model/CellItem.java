package com.example.myapplication.Model;

import android.graphics.Color;

import com.example.myapplication.R;

public class CellItem {
    private int status, color;

    public CellItem() {
        this.status=100;
        this.color= 0;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public CellItem(int status) {
        this.status = status;
        if(status==1) color= Color.BLUE;
        if(status==0)  color=Color.YELLOW;
        if(status==-1)  color=Color.RED;
    }
}

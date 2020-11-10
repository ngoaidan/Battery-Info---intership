package com.example.myapplication.Model;

public class UsableTimeItem {
    private float Value;
    private String Name;
    private int Color;

    public UsableTimeItem(float value, String name, int color) {
        Value = value;
        Name = name;
        Color = color;
    }

    public float getValue() {
        return Value;
    }

    public void setValue(float value) {
        Value = value;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }
}

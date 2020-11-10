package com.example.myapplication.Model;

public class BatteryHealthItem {
    private String Name,Status;

    public BatteryHealthItem(String name, String status) {
        Name = name;
        Status = status;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}

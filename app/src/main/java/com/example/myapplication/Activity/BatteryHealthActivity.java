package com.example.myapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.BatteryHealthAdapter;
import com.example.myapplication.Model.BatteryHealthItem;
import com.example.myapplication.R;

import java.util.ArrayList;

import static maes.tech.intentanim.CustomIntent.customType;

public class BatteryHealthActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<BatteryHealthItem> list;
    BatteryHealthAdapter adapter;
    TextView healthMessage,expectingTime;
    ImageView alertIcon;
    private int lastEnergy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_health);
        SetUpStatusBar();
        Hook();
        ActionToolBar();
        lastEnergy=getIntent().getIntExtra("remainingBattery",0);
        setUpRemainingTime();
        list=new ArrayList<>();
        adapter=new BatteryHealthAdapter(list,this);
        SetUpList();
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setUpRemainingTime() {
        SharedPreferences pref = this.getSharedPreferences("MyPref", 0);
        String result=pref.getString("prefTime","");
        if(result.isEmpty()) expectingTime.setVisibility(View.GONE);
        else expectingTime.setText(result);
    }

    private void SetUpStatusBar() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(-1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                customType(BatteryHealthActivity.this,"right-to-left");
            }
        });
    }

    private void Hook() {
        alertIcon=findViewById(R.id.alertImage);
        toolbar=findViewById(R.id.toolbar);
        healthMessage=findViewById(R.id.healthMessage);
        recyclerView=findViewById(R.id.batteryHealthRecyclerView);
        expectingTime=findViewById(R.id.expectingTime);
    }


    private void SetUpList() {
        Intent batteryStatus =registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int batteryHealth=0;
        String health="";
        int batteryState=0;
        int voltag=0,temperature=0,size=0;
        long estimated=0;
        //Battery Health
        batteryHealth=batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH,batteryHealth);
        if(batteryHealth==1) health="UNKNOWN";
        if(batteryHealth==2) health="GOOD";
        if(batteryHealth==3) health="OVERHEAT";
        if(batteryHealth==4) health="DEAD";
        if(batteryHealth==5) health="OVER VOLTAGE";
        if(batteryHealth==6) health="UNSPECIFIED FAILURE";
        healthMessage.setText(health);
        if(health.equals("GOOD")) alertIcon.setImageResource(R.drawable.ic_thumb_up_black_24dp);
        else{
            alertIcon.setImageResource(R.drawable.ic_add_alert_black_24dp);
            healthMessage.setTextColor(Color.RED);
        }

        BatteryManager mBatteryManager = (BatteryManager)getSystemService(Context.BATTERY_SERVICE);

        //Battery Status
        batteryState=batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS,batteryHealth);
        String status="";
        if(batteryState==1) status="Unknown";
        if(batteryState==2) status="Charging";
        if(batteryState==3) status="Discharging";
        if(batteryState==4) status="Not charging";
        if(batteryState==5) status="FULL";
        //Battery Size
        estimated=mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        //battery Estimated
        long remainingPercent=mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        long temp= estimated*100;
        long result=temp/remainingPercent;

       size=(int)result;
       size=(int)getBatteryCapacity(this);

        //Battery Voltage
        voltag=batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE,voltag);
        //Battery Temperature
        temperature=batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,temperature);

        list.add(new BatteryHealthItem("Battery Health",health));
        list.add(new BatteryHealthItem("Estimated",estimated/1000+"mAh"));
        list.add(new BatteryHealthItem("Charge state",status));
        list.add(new BatteryHealthItem("Battery Size",size+"mAh"));
        list.add(new BatteryHealthItem("Voltage",(float)voltag/1000+"V"));
        list.add(new BatteryHealthItem("Temperature",temperature/10-17+""+'\u00B0'+"C"));
    }


    public double getBatteryCapacity(Context context) {
        Object mPowerProfile;
        Object power;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);

            batteryCapacity = (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return batteryCapacity;

    }
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}

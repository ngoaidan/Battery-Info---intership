package com.example.myapplication.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapter.PhoneInfoAdapter;
import com.example.myapplication.Model.BatteryHealthItem;
import com.example.myapplication.Model.PhoneInfoItem;
import com.example.myapplication.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DifferenceFragment extends Fragment {
    TextView phoneModel,useSize,remainSize,screenSize,resolutionInfo,cameraSize,osVersion;
    RecyclerView rvBasic,rvHardWare,rvBattery;
    ArrayList<PhoneInfoItem> list;
    ArrayList<PhoneInfoItem> hardWareList;
    ArrayList<PhoneInfoItem> batteryList;
    String lang="";
    StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
    StorageManager mStorageManager;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        lang=sharedPrefs.getString("lang","");
        View view;
        if(lang.equals("ar"))
        view = inflater.inflate(R.layout.ar_fragment_diff,container,false);
        else  view = inflater.inflate(R.layout.fragment_different_things,container,false);
        useSize=view.findViewById(R.id.usedSize);
        remainSize=view.findViewById(R.id.remainingSize);
        screenSize=view.findViewById(R.id.screenSize);
        resolutionInfo=view.findViewById(R.id.resolution);
        cameraSize=view.findViewById(R.id.cameraInfo);
        rvBasic=view.findViewById(R.id.rvBasic);
        rvHardWare=view.findViewById(R.id.rvHardware);
        phoneModel=view.findViewById(R.id.phoneMode);
        osVersion=view.findViewById(R.id.osVersion);
        rvBattery=view.findViewById(R.id.rvBattery);

        phoneModel.setText(sharedPrefs.getString("deviceName","Unknown"));
        osVersion.setText("Android "+GetAndroidVersion());
        screenSize.setText(sharedPrefs.getString("Dislay","0")+"IN");
        resolutionInfo.setText(sharedPrefs.getString("screenResolution","Unknown"));
        cameraSize.setText(sharedPrefs.getString("cameraResolution","Unknown"));

            showStorageVolumes();



        SetUpBasicInfo();
        SetUpHardWareInfo();
        SetUpBatteryList();
        GetAndroidVersion();

        return view;
    }


    private String GetAndroidVersion() {
        String versionRelease = Build.VERSION.RELEASE;

    return versionRelease;
    }

    private void SetUpBasicInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        long dateInMillis=java.lang.System.currentTimeMillis() - android.os.SystemClock.elapsedRealtime();
        String dateString = formatter.format(new Date(dateInMillis));
        Log.d("last boot", SystemClock.elapsedRealtime()+"");
        list=new ArrayList<>();
        list.add(new PhoneInfoItem(getResources().getString(R.string.last_boot),dateString));
        String runningResult=getRunningTime(SystemClock.elapsedRealtime());
        String [] arr=runningResult.split(", ");
        runningResult="";
        for(int i=0;i<arr.length;i++){
            Log.d("arr",""+arr[i].equals("0 d"));
            if(i==0&&arr[i].equals("0 d")==false) runningResult=runningResult+arr[i]+", ";
            else if(i==1&&!arr[i].equals("0 h")) runningResult=runningResult+arr[i]+", ";
            else if(i==3) runningResult=runningResult+arr[i];
            else if(i==2)runningResult=runningResult+arr[i]+", ";
            Log.d("arr",runningResult);

        }
        list.add(new PhoneInfoItem(getResources().getString(R.string.running_time),runningResult));
        PhoneInfoAdapter adapter=new PhoneInfoAdapter(list,getContext(),lang);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        rvBasic.setLayoutManager(mLayoutManager);
        rvBasic.setAdapter(adapter);
        rvBasic.setNestedScrollingEnabled(false);
    }

    private String getRunningTime(long timeInMilliSeconds){
        long seconds = timeInMilliSeconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
         String time = days + " d, " + hours % 24 + " h, " + minutes % 60 + " m, " + seconds % 6+" s";
         return time;
    }
    private void SetUpHardWareInfo() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        hardWareList=new ArrayList<>();
        String cpuName=sharedPrefs.getString("cpuName","Unknown");
        if(!cpuName.isEmpty()) hardWareList.add(new PhoneInfoItem("CPU",cpuName));

        hardWareList.add(new PhoneInfoItem(getResources().getString(R.string.cores),sharedPrefs.getString("cpuCore","Unknown")));
        String lang=sharedPrefs.getString("lang","");
        if(lang.equals("ar"))
        hardWareList.add(new PhoneInfoItem("RAM","GB "+sharedPrefs.getString("ramSize","Unknown")));
        else   hardWareList.add(new PhoneInfoItem("RAM",sharedPrefs.getString("ramSize","Unknown")+" GB"));
        hardWareList.add(new PhoneInfoItem(getResources().getString(R.string.internal_storage),TotalandFree));
        PhoneInfoAdapter adapter=new PhoneInfoAdapter(hardWareList,getContext(),lang);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        rvHardWare.setLayoutManager(mLayoutManager);
        rvHardWare.setAdapter(adapter);
        rvHardWare.setNestedScrollingEnabled(false);
    }

   private String TotalandFree="";
    //Lấy giá các giá trị của internal/external storage

    private void showStorageVolumes() {
        Log.d("freespace","aasdsad");
        StorageStatsManager storageStatsManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            storageStatsManager = (StorageStatsManager) getContext().getSystemService(Context.STORAGE_STATS_SERVICE);


        StorageManager storageManager = (StorageManager) getContext().getSystemService(Context.STORAGE_SERVICE);
        if (storageManager == null || storageStatsManager == null) {

            return;
        }
        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();

        double result=0;
        long remain=0;
        Log.d("volumeSize",storageVolumes.size()+"");

        for (StorageVolume storageVolume : storageVolumes) {


            try {


                Log.d("AppLog", "storage:" + StorageManager.UUID_DEFAULT + " : " + storageVolume.getDescription(getContext()) + " : " + storageVolume.getState());
                Log.d("AppLog", "getFreeBytes:" + Formatter.formatShortFileSize(getContext(), storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT)));
                Log.d("AppLog", "getTotalBytes:" + Formatter.formatShortFileSize(getContext(), storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)));
                result=(double) (storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)-storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT))*100/storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT);
                Log.d("result",""+result);
                remain=storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT);
                TotalandFree=Formatter.formatShortFileSize(getContext(), storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT))+" free\n"+
                        Formatter.formatShortFileSize(getContext(), storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT))+" total";

            } catch (Exception e) {
                // IGNORED
            }
        }
        DecimalFormat df = new DecimalFormat("#.#"); String formatted = df.format(result);


        useSize.setText(formatted+"%");
        remainSize.setText(Formatter.formatShortFileSize(getContext(), remain));
        }
        else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            File internalStorageFile=getContext().getFilesDir();
            File[] externalStorageFiles= ContextCompat.getExternalFilesDirs(getContext(),null);
            long availableSizeInBytes=internalStorageFile.getFreeSpace();
            long totalSize=internalStorageFile.getTotalSpace();
            Log.d("space",Formatter.formatShortFileSize(getContext(),availableSizeInBytes)+" "+Formatter.formatShortFileSize(getContext(),totalSize));
            TotalandFree=Formatter.formatShortFileSize(getContext(),availableSizeInBytes)+" free\n"
                    +Formatter.formatShortFileSize(getContext(),totalSize)+" total";
            remainSize.setText(Formatter.formatShortFileSize(getContext(),availableSizeInBytes));
            double result= (double) ((totalSize-availableSizeInBytes)*100/totalSize);
            DecimalFormat df = new DecimalFormat("#.#"); String formatted = df.format(result);
            useSize.setText(formatted+"%");
        }
        else {
            File internalStorageFile=getContext().getFilesDir();
            File[] externalStorageFiles= ContextCompat.getExternalFilesDirs(getContext(),null);
            long availableSizeInBytes=internalStorageFile.getFreeSpace();
            long totalSize=internalStorageFile.getTotalSpace();
            Log.d("space",Formatter.formatShortFileSize(getContext(),availableSizeInBytes)+" "+Formatter.formatShortFileSize(getContext(),totalSize));
            TotalandFree=Formatter.formatShortFileSize(getContext(),availableSizeInBytes)+" free\n"
                    +Formatter.formatShortFileSize(getContext(),totalSize)+" total";
            remainSize.setText(Formatter.formatShortFileSize(getContext(),availableSizeInBytes));
            double result= (double) ((totalSize-availableSizeInBytes)*100/totalSize);
            DecimalFormat df = new DecimalFormat("#.#"); String formatted = df.format(result);
            useSize.setText(formatted+"%");

        }
    }

    private void SetUpBatteryList() {
        batteryList=new ArrayList<>();
        Intent batteryStatus =getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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


        BatteryManager mBatteryManager = (BatteryManager)getContext().getSystemService(Context.BATTERY_SERVICE);

        //Battery Status
        batteryState=batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS,batteryHealth);
        String status="";
        if(batteryState==1) status=getResources().getString(R.string.unknown);
        if(batteryState==2) status=getResources().getString(R.string.charging);
        if(batteryState==3) status=getResources().getString(R.string.discharging);
        if(batteryState==4) status=getResources().getString(R.string.not_charging);
        if(batteryState==5) status=getResources().getString(R.string.full);
        //Battery Size

        //battery Estimated

        int batteryLevel=0;
        batteryLevel=batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);


        //Battery Voltage
        voltag=batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE,voltag);
        //Battery Temperature
        temperature=batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,temperature);
        batteryList.add(new PhoneInfoItem(getResources().getString(R.string.battery_status),status));
        batteryList.add(new PhoneInfoItem(getResources().getString(R.string.level),batteryLevel+"%"));
        batteryList.add(new PhoneInfoItem(getResources().getString(R.string.temperature),temperature/10+""+'\u00B0'+"C"));
        batteryList.add(new PhoneInfoItem(getResources().getString(R.string.voltage),(float)voltag/1000+"V"));

        PhoneInfoAdapter adapter=new PhoneInfoAdapter(batteryList,getContext(),lang);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        rvBattery.setLayoutManager(mLayoutManager);
        rvBattery.setAdapter(adapter);
        rvBattery.setNestedScrollingEnabled(false);
    }

    private boolean isTablet(Context c) {
        return (c.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


}




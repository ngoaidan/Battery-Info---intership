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
    StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
    StorageManager mStorageManager;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view= inflater.inflate(R.layout.fragment_different_things,container,false);
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
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
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
        list.add(new PhoneInfoItem("Last boot",dateString));
        list.add(new PhoneInfoItem("Running Time",getRunningTime(SystemClock.elapsedRealtime())));
        PhoneInfoAdapter adapter=new PhoneInfoAdapter(list,getContext());
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

        hardWareList.add(new PhoneInfoItem("Cores",sharedPrefs.getString("cpuCore","Unknown")));
        hardWareList.add(new PhoneInfoItem("RAM",sharedPrefs.getString("ramSize","Unknown")+" GB"));
        hardWareList.add(new PhoneInfoItem("Internal Storage",TotalandFree));
        PhoneInfoAdapter adapter=new PhoneInfoAdapter(hardWareList,getContext());
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
            long availableSizeInBytes=new StatFs(internalStorageFile.getPath()).getAvailableBytes();
            Long totalsize=new StatFs(internalStorageFile.getPath()).getTotalBytes();
            Log.d("freespace",totalsize+"");
            Toast.makeText(getContext(), ""+totalsize, Toast.LENGTH_LONG).show();
            Log.d("freespace",Formatter.formatShortFileSize(getContext(),totalsize));
            double result= (double) ((totalsize-availableSizeInBytes)*100/totalsize);
            DecimalFormat df = new DecimalFormat("#.#"); String formatted = df.format(result);
            remainSize.setText(Formatter.formatShortFileSize(getContext(),availableSizeInBytes));
            useSize.setText(formatted+"%");
            TotalandFree=Formatter.formatShortFileSize(getContext(),availableSizeInBytes)+" free\n"
                    +Formatter.formatShortFileSize(getContext(),totalsize)+" total";

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
        if(batteryState==1) status="Unknown";
        if(batteryState==2) status="Charging";
        if(batteryState==3) status="Discharging";
        if(batteryState==4) status="Not charging";
        if(batteryState==5) status="FULL";
        //Battery Size

        //battery Estimated

        int batteryLevel=0;
        batteryLevel=batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);


        //Battery Voltage
        voltag=batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE,voltag);
        //Battery Temperature
        temperature=batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,temperature);
        batteryList.add(new PhoneInfoItem("Battery status",status));
        batteryList.add(new PhoneInfoItem("Level",batteryLevel+"%"));
        batteryList.add(new PhoneInfoItem("Temperature",temperature/10+""+'\u00B0'+"C"));
        batteryList.add(new PhoneInfoItem("Voltage",(float)voltag/1000+"V"));

        PhoneInfoAdapter adapter=new PhoneInfoAdapter(batteryList,getContext());
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




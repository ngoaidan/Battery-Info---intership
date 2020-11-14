package com.example.myapplication.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapter.PhoneInfoAdapter;
import com.example.myapplication.Model.PhoneInfoItem;
import com.example.myapplication.R;

import java.io.File;
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
    RecyclerView rvBasic,rvHardWare;
    ArrayList<PhoneInfoItem> list;
    ArrayList<PhoneInfoItem> hardWareList;
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
        phoneModel.setText("Android "+getDeviceName());
        osVersion.setText("Android "+GetAndroidVersion());
        screenSize.setText(getDisplaySize((Activity) getContext()));
        resolutionInfo.setText(getScreenResolution(getContext()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showStorageVolumes();
        }
        Log.d("navigate",""+getNavBarHeight(getContext()));
        getBackCameraResolutionInMp();
        SetUpBasicInfo();
        SetUpHardWareInfo();
        GetAndroidVersion();
        return view;
    }

    private String getScreenResolution(Context context)
    {
        DisplayMetrics metrics;
        int width = 0, height = 0;
        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        height = Math.min(metrics.widthPixels, metrics.heightPixels); //height
        width = Math.max(metrics.widthPixels, metrics.heightPixels); //

        return "" + height + "*" + (width+getNavBarHeight(getContext())) + "";
    }
    private String GetAndroidVersion() {
        String versionRelease = Build.VERSION.RELEASE;

    return versionRelease;
    }

    private void SetUpBasicInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
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
        hardWareList=new ArrayList<>();
        hardWareList.add(new PhoneInfoItem("Last boot","2020-11-08"));
        hardWareList.add(new PhoneInfoItem("Running Time","3d, 14h, 56 m, 18s"));
        hardWareList.add(new PhoneInfoItem("Last boot","2020-11-08"));
        hardWareList.add(new PhoneInfoItem("Running Time","3d, 14h, 56 m, 18s"));
        hardWareList.add(new PhoneInfoItem("Last boot","2020-11-08"));
        hardWareList.add(new PhoneInfoItem("Running Time","3d, 14h, 56 m, 18s"));
        PhoneInfoAdapter adapter=new PhoneInfoAdapter(hardWareList,getContext());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        rvHardWare.setLayoutManager(mLayoutManager);
        rvHardWare.setAdapter(adapter);
        rvHardWare.setNestedScrollingEnabled(false);
    }
    private String getDisplaySize(Activity activity) {
        double x = 0, y = 0;
        int mWidthPixels, mHeightPixels;
        try {
            WindowManager windowManager = activity.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            Point realSize = new Point();
            Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
            mWidthPixels = realSize.x;
            mHeightPixels = realSize.y;
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            x = Math.pow(mWidthPixels / dm.xdpi, 2);
            y = Math.pow(mHeightPixels / dm.ydpi, 2);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return String.format(Locale.US, "%.2f", Math.sqrt(x + y));
    }
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model.toUpperCase();
        }
        return ((manufacturer) + " " + model).toUpperCase();
    }
    //Lấy giá các giá trị của internal/external storage
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showStorageVolumes() {
        StorageStatsManager storageStatsManager = (StorageStatsManager) getContext().getSystemService(Context.STORAGE_STATS_SERVICE);

        StorageManager storageManager = (StorageManager) getContext().getSystemService(Context.STORAGE_SERVICE);
        if (storageManager == null || storageStatsManager == null) {

            return;
        }
        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
        double result=0;
        long remain=0;
        for (StorageVolume storageVolume : storageVolumes) {
            final String uuidStr = storageVolume.getUuid();
            try {
            final UUID uuid = uuidStr == null ? StorageManager.UUID_DEFAULT : UUID.fromString(uuidStr);

                Log.d("AppLog", "storage:" + uuid + " : " + storageVolume.getDescription(getContext()) + " : " + storageVolume.getState());
                Log.d("AppLog", "getFreeBytes:" + Formatter.formatShortFileSize(getContext(), storageStatsManager.getFreeBytes(uuid)));
                Log.d("AppLog", "getTotalBytes:" + Formatter.formatShortFileSize(getContext(), storageStatsManager.getTotalBytes(uuid)));
                result=(double)(storageStatsManager.getTotalBytes(uuid)-storageStatsManager.getFreeBytes(uuid))*100/storageStatsManager.getTotalBytes(uuid);
                Log.d("result",""+result);
                remain=storageStatsManager.getFreeBytes(uuid);
            } catch (Exception e) {
                // IGNORED
            }
        }
        DecimalFormat df = new DecimalFormat("#.#"); String formatted = df.format(result);


        useSize.setText(formatted+"%");
        remainSize.setText(Formatter.formatShortFileSize(getContext(), remain));
    }

    //Lấy chiều dài chiều rộng của Camera
    public float getBackCameraResolutionInMp()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
            }
        }
        int noOfCameras = Camera.getNumberOfCameras();
        float maxResolution1 = -1;
        float maxResolution2=-1;
        long pixelCount = -1;
        Log.d("num camera",noOfCameras+"");
        for (int i = 0;i < noOfCameras;i++)
        {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);

            if (i==0)
            {
                Camera camera = Camera.open(i);;
                Camera.Parameters cameraParams = camera.getParameters();
                for (int j = 0;j < cameraParams.getSupportedPictureSizes().size();j++)
                {
                    long pixelCountTemp = cameraParams.getSupportedPictureSizes().get(j).width * cameraParams.getSupportedPictureSizes().get(j).height; // Just changed i to j in this loop
                    if (pixelCountTemp > pixelCount)
                    {
                        pixelCount = pixelCountTemp;
                        maxResolution1 = ((float)pixelCountTemp) / (1024000.0f);

                        Log.d("camera",Math.round(maxResolution1)+"");
                    }
                }
               pixelCount = -1;
                camera.release();
            }else if (i==1)
            {

                Camera camera = Camera.open(i);
                Camera.Parameters cameraParams = camera.getParameters();
                for (int j = 0;j < cameraParams.getSupportedPictureSizes().size();j++)
                {
                    Log.d("camera front",cameraParams.getSupportedPictureSizes().size()+"");
                    long pixelCountTemp = cameraParams.getSupportedPictureSizes().get(j).width * cameraParams.getSupportedPictureSizes().get(j).height; // Just changed i to j in this loop
                    if (pixelCountTemp > pixelCount)
                    {
                        pixelCount = pixelCountTemp;
                        maxResolution2 = ((float)pixelCountTemp) / (1024000.0f);


                    }
                }

                camera.release();
            }

            cameraSize.setText(((int)(maxResolution1)+1)+"MP+"+((int)(maxResolution2)+1)+"MP");
        }

        return 0;
    }
    public int getNavBarHeight(Context c) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if(!hasMenuKey && !hasBackKey) {
            //The device has a navigation bar
            Resources resources = c.getResources();

            int orientation = resources.getConfiguration().orientation;
            int resourceId;
            if (isTablet(c)){
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
            }  else {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
            }

            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }
    private boolean isTablet(Context c) {
        return (c.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


}




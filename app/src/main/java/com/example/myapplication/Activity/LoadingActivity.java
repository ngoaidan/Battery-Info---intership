package com.example.myapplication.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Point;
import android.graphics.Shader;

import android.hardware.Camera;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dx.dxloadingbutton.lib.LoadingButton;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Model.ShellExecuter;
import com.example.myapplication.Model.UsableTimeItem;
import com.example.myapplication.R;
import com.google.gson.Gson;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;




import static maes.tech.intentanim.CustomIntent.customType;

public class LoadingActivity extends AppCompatActivity {
    private boolean goToBatHealhAct=false;
    private ArrayList<UsableTimeItem> list;
    private Button startButton;
    private  boolean granted = false;
    private LottieAnimationView animationView;
    SharedPreferences sharedPrefs ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        list=new ArrayList<>();
        SetUpStatusBar();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Hook();
        startButton.setVisibility(View.GONE);
        animationView = (LottieAnimationView) findViewById(R.id.loading_animation);
        animationView.setAnimation("loading.json");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //kitkat không dùng Usage Stats được
            GetUsagePermission();
        }
        else{ //bỏ qua xin quyền đối với kitkat
            ActivityNavigate();
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              handler.removeCallbacksAndMessages(null);
                //
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[] {Manifest.permission.CAMERA}, 222);
                    } else {
                            startButton.setEnabled(false);
                            getBackCameraResolutionInMp();
                            Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                            startActivity(intent);
                            customType(LoadingActivity.this,"bottom-to-up");
                            finish();
                    }
                }else {
                    startButton.setEnabled(false);

                    getBackCameraResolutionInMp();

                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                    customType(LoadingActivity.this,"bottom-to-up");
                    finish();

                }
            }
        });


    }

    private void Hook() {
        startButton=findViewById(R.id.start_button);
       // startButton=findViewById(R.id.starButton);

    }



    private void getDisplaySize(Activity activity) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
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
        DecimalFormat df = new DecimalFormat("#.#");
        String formatted = df.format(Math.sqrt(x + y));
       formatted= formatted.replace(',','.');
        editor.putString("Dislay",formatted);
        editor.commit();

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
    private void getScreenResolution(Context context)
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        DisplayMetrics metrics;
        int width = 0, height = 0;
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        height = Math.min(metrics.widthPixels, metrics.heightPixels); //height
        width = Math.max(metrics.widthPixels, metrics.heightPixels); //

        editor.putString("screenResolution", "" + height + "*" + (width+getNavBarHeight(this)) + "");
        editor.commit();
    }
    private void GetDeviceName() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            editor.putString("deviceName",model.toUpperCase());
        }
        else editor.putString ("deviceName",((manufacturer) + " " + model).toUpperCase());
        editor.commit();
    }

    private void GetRamSize() {
        ShellExecuter exe = new ShellExecuter();
        String command = "cat /proc/meminfo";
        String outp = exe.Executer(command);
        String[] ramInfo=outp.split(" ");
        float result=Float.parseFloat(ramInfo[8]);
        Log.d("RAM",result+"");
        result=result/1000000;

        SharedPreferences.Editor editor = sharedPrefs.edit();
        DecimalFormat df = new DecimalFormat("#.##");
        String formatted = df.format(result);
        editor.putString("ramSize",formatted);
        editor.commit();
    }
    private void GetBatteryName(){
        ShellExecuter exe = new ShellExecuter();
        String command = "$ df -Th /var";
        String outp = exe.Executer(command);
        Log.d("df",outp);

    }


    //Lấy chiều dài chiều rộng của Camera
    public float getBackCameraResolutionInMp()
    {

        int noOfCameras = Camera.getNumberOfCameras();
        float maxResolution1 = -1;
        float maxResolution2=-1;
        long pixelCount = -1;

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

                    long pixelCountTemp = cameraParams.getSupportedPictureSizes().get(j).width * cameraParams.getSupportedPictureSizes().get(j).height; // Just changed i to j in this loop
                    if (pixelCountTemp > pixelCount)
                    {
                        pixelCount = pixelCountTemp;
                        maxResolution2 = ((float)pixelCountTemp) / (1024000.0f);


                    }
                }

                camera.release();
            }
            SharedPreferences.Editor editor = sharedPrefs.edit();
           editor.putString("cameraResolution",((int)(maxResolution1)+1)+"MP+"+((int)(maxResolution2)+1)+"MP");
           editor.commit();
        }

        return 0;
    }


    private void GetCoreInfo(String path) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            if (inputStream != null) {

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                do {
                    line = bufferedReader.readLine();

                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    if(line.contains("1")) editor.putString("cpuCore","2");
                    else if (line.contains("3")) editor.putString("cpuCore","4");
                    else if (line.contains("7")) editor.putString("cpuCore","8");
                    else editor.putString("cpuCore","1");
                    editor.commit();
                } while (line != null);

            }
        } catch (Exception e) {

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    boolean flag=false;
    int secondValue=0;
    int firstValue=0;
Handler handler=new Handler();
    private void ActivityNavigate() {
       // getBatteryCapacity();
        animationView.playAnimation();
        GetCPUInfo();//lấy tên model của cpu
        GetCoreInfo("/sys/devices/system/cpu/present");//lấy số lượng core
        GetRamSize();//lấy total size của ram
        GetDeviceName();//lấy tên model của điện thoại
        getDisplaySize(LoadingActivity.this);//lấy dislay của diện thoại dơn vị là inch
        getScreenResolution(LoadingActivity.this);
        GetBatteryName();
        final BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            firstValue=bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);

        }


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if(flag==false){ handler.postDelayed(this,6000);flag=true;}else{
                animationView.cancelAnimation();
                animationView.setProgress(120);
                startButton.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        secondValue=bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                        SharedPreferences.Editor editor=sharedPrefs.edit();
                        editor.putLong("sub",firstValue-secondValue);
                        editor.putLong("lastBattery",secondValue);
                        editor.putBoolean("returnFragment",false);
                        Log.d("first",""+secondValue);
                        editor.commit();
                       Log.d("banaba",firstValue+" "+secondValue);
                    }

                    handler.removeCallbacksAndMessages(null);




            }}


        };
        runnable.run();
    }

    private void GetCPUInfo() {



        ShellExecuter exe = new ShellExecuter();
        String command = "cat /proc/cpuinfo";

        String outp = exe.Executer(command);
        String [] spiltString=outp.split(":");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Log.d("cpuinfo",outp);
        String result=spiltString[spiltString.length-1];
        result=result;
        result.replace(" ","");
        result.replace("\n","");
        String t="\n abcdc123";
        t=result.replace("\n", "").replace("\r", "");
        boolean atleastOneAlpha = t.matches(".*[a-zA-Z]+.*");


        if(atleastOneAlpha)
        editor.putString("cpuName",t);
        else editor.putString("cpuName","");

        editor.commit();


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
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
    //Build a new usage list without duplicate PackageName
    private ArrayList<UsableTimeItem> createResult(ArrayList<UsableTimeItem> list){
        ArrayList<UsableTimeItem> result=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            int index=getIndexEqualPackageName(result,list.get(i));
            if(index!=-1){
                float total=  ( result.get(index).getValue()+list.get(i).getValue());
                result.get(index).setValue(total);}
            else result.add(list.get(i));

        }
        return result;
    }

    private int getIndexEqualPackageName(ArrayList<UsableTimeItem> list,UsableTimeItem item) {
        if(list.size()==0) return -1;
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i).getName().equals(item.getName())){
                return i;
            }
        }
        return -1;
    }

    private void GetUsagePermission(){
        ActivityNavigate();
        AppOpsManager appOps = (AppOpsManager)this
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), this.getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (this.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }
        //Navigate to active permission
        if(granted==false){
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivityForResult(intent,1111);
        }
        else{
        }
   }

}

package com.example.myapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.myapplication.MainActivity;
import com.example.myapplication.Model.UsableTimeItem;
import com.example.myapplication.R;
import com.google.gson.Gson;
import com.yanzhenjie.loading.LoadingView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static maes.tech.intentanim.CustomIntent.customType;

public class LoadingActivity extends AppCompatActivity {
    private boolean goToBatHealhAct=false;
    private ArrayList<UsableTimeItem> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        list=new ArrayList<>();
        SetUpStatusBar();
        LoadingView loadingView=findViewById(R.id.loading_view);
        loadingView.setCircleColors(Color.BLACK,Color.BLUE,Color.GREEN);
        GetBatteryStat();
        ListToSharePreference();
        ActivityNavigate();
    }

    private void ListToSharePreference() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(list);

        editor.putString("usageStats", json);
        editor.commit();
    }

    private void ActivityNavigate() {

            BatteryManager mBatteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
            final int estimated = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    intent.putExtra("remainingBattery", estimated);
                    startActivity(intent);
                    customType(LoadingActivity.this,"bottom-to-up");
                    finish();
                }

            }, 5000);

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
    //Get battery Usage time
    private void GetBatteryStat() {
        //Checking permission
        boolean granted = false;
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
        if(granted==false)startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        Log.d("check_permission",""+granted);
        //Get usable time service in a day
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                (System.currentTimeMillis() - 86400000), System.currentTimeMillis());
        Log.d("index",""+queryUsageStats.size());


        //add to list usableTime
        for(int i=0;i<queryUsageStats.size();i++)
        {
            final String packageName = queryUsageStats.get(i).getPackageName();
            PackageManager packageManager= this.getPackageManager();
            try {
                String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
                if(queryUsageStats.get(i).getTotalTimeInForeground()!=0)
                    list.add(new UsableTimeItem(queryUsageStats.get(i).getTotalTimeInForeground(),appName,1));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }


        float totalTime=0;
        list=createResult(list);

        for(int i=0;i<list.size()-1;i++){
            totalTime= (long) (totalTime+list.get(i).getValue());
        }
        for(int i=0;i<list.size()-1;i++){

            float result=list.get(i).getValue()*100/totalTime;
            list.get(i).setValue(result);

        }
        list.remove(list.size()-1);

    }
}

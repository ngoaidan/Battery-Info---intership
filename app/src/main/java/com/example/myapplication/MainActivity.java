package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.myapplication.Fragment.DifferenceFragment;
import com.example.myapplication.Fragment.HomeFragment;
import com.example.myapplication.Fragment.AboutAppFragment;
import com.example.myapplication.Model.ShellExecuter;
import com.example.myapplication.Model.UsableTimeItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navigationBar;
    int lastBatteryEnnergy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastBatteryEnnergy=getIntent().getIntExtra("remainingBattery",0);
        setStatusBar();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        GetBatteryStat();}
        else GetUsageStatForKitKat();
        ListToSharePreference();
        setContentView(R.layout.activity_main);
        navigationBar=findViewById(R.id.bottom_nav);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer, new HomeFragment());
        transaction.addToBackStack(null);
        transaction.commit();
        navigationBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);




    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment=null;
            switch(item.getItemId()){
                case R.id.home:
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("returnFragment",true);
                    item.setChecked(true);
                    editor.commit();
                    fragment=new HomeFragment();
                    break;
                case R.id.differences:
                    fragment=new DifferenceFragment();
                    item.setChecked(true);
                    break;
                case R.id.setting:
                    fragment=new AboutAppFragment();
                    item.setChecked(true);
                    break;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void setStatusBar() {
        requestWindowFeature(1);
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
    private void ListToSharePreference() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this) ;

        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(list);

        editor.putString("usageStats", json);
        editor.commit();
    }
    private ArrayList<UsableTimeItem> list;
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
    private void GetBatteryStat() {
        //Checking permission
    list=new ArrayList<>();
        //Get usable time service in a day
        UsageStatsManager mUsageStatsManager = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUsageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);


        List<UsageStats> queryUsageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                (System.currentTimeMillis() - 86400000), System.currentTimeMillis());

        String appPackageName=getApplication().getPackageName();
        PackageManager pm = this.getPackageManager();

        //add to list usableTime
        for(int i=0;i<queryUsageStats.size();i++)
        {

            try {
                ApplicationInfo ai = pm.getApplicationInfo(queryUsageStats.get(i).getPackageName(), 0);

                if(queryUsageStats.get(i).getTotalTimeInForeground()!=0&&!queryUsageStats.get(i).getPackageName()
                        .equals(appPackageName)) {

                    final String packageName = queryUsageStats.get(i).getPackageName();
                    PackageManager packageManager = this.getPackageManager();
                    try {
                        String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));

                        if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM)
                            list.add(new UsableTimeItem(queryUsageStats.get(i).getTotalTimeInForeground(), appName, 1));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }


        float totalTime=0;
        list=createResult(list);

        for(int i=0;i<list.size();i++){

            totalTime= (long) (totalTime+list.get(i).getValue());
        }
        for(int i=0;i<list.size();i++){

            float result=list.get(i).getValue()*100/totalTime;
            list.get(i).setValue(result);

        }
        }


    }
    private int totalTime=0;
    private  void GetUsageStatForKitKat(){
        list=new ArrayList<>();
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> pids = am.getRunningAppProcesses();
        int processid = 0;
        PackageManager pm = this.getPackageManager();

        String appPackageName=getApplication().getPackageName();
        for (int i = 0; i < pids.size(); i++) {
            ActivityManager.RunningAppProcessInfo info = pids.get(i);

            processid = info.pid;

            ShellExecuter exe = new ShellExecuter();
            String command = "cat /proc/"+processid+"/stat";
            String outp = exe.Executer(command);
            outp.replace(' ','\n');
            String [] result=outp.split(" ");
            Log.d("info",outp);

            PackageManager packageManager= this.getPackageManager();
            try {
                String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(pids.get(i).processName, PackageManager.GET_META_DATA));
                Log.d("Banaba",appName+" " +result[13]+" "+result[14]+"\n");
                totalTime= Integer.parseInt(result[13])+ Integer.parseInt(result[14]);
                if(totalTime!=0) list.add(new UsableTimeItem(totalTime,appName,-1));

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


        }
        float totalTime=0;
        list=createResult(list);

        for(int i=0;i<list.size();i++){

            totalTime= (long) (totalTime+list.get(i).getValue());
        }
        for(int i=0;i<list.size();i++){

            float result=list.get(i).getValue()*100/totalTime;
            list.get(i).setValue(result);

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
}

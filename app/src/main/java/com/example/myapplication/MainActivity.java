package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.myapplication.Fragment.DifferenceFragment;
import com.example.myapplication.Fragment.HomeFragment;
import com.example.myapplication.Fragment.AboutAppFragment;
import com.example.myapplication.Model.UsableTimeItem;
import com.google.gson.Gson;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ChipNavigationBar navigationBar;
    int lastBatteryEnnergy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastBatteryEnnergy=getIntent().getIntExtra("remainingBattery",0);
        setStatusBar();
        GetBatteryStat();
        ListToSharePreference();
        setContentView(R.layout.activity_main);
        navigationBar=findViewById(R.id.bottom_menu);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer, new HomeFragment());
        transaction.addToBackStack(null);
        transaction.commit();
        navigationBar.setItemSelected(R.id.home,true);
        navigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment=null;
                switch(i){
                    case R.id.home:
                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putBoolean("returnFragment",true);
                        editor.commit();
                        fragment=new HomeFragment();
                        break;
                    case R.id.differences:
                        fragment=new DifferenceFragment();
                        break;
                    case R.id.setting:
                        fragment=new AboutAppFragment();
                        break;
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameContainer, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


    }

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
        window.setStatusBarColor(Color.TRANSPARENT);
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
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                (System.currentTimeMillis() - 86400000), System.currentTimeMillis());

        String appPackageName=getApplication().getPackageName();
        PackageManager pm = this.getPackageManager();

        //add to list usableTime
        for(int i=0;i<queryUsageStats.size();i++)
        {
            try {
                ApplicationInfo ai = pm.getApplicationInfo(queryUsageStats.get(i).getPackageName(), 0);


                if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {

                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            final String packageName = queryUsageStats.get(i).getPackageName();
            PackageManager packageManager= this.getPackageManager();
            try {
                String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
                if(queryUsageStats.get(i).getTotalTimeInForeground()!=0&&!queryUsageStats.get(i).getPackageName().equals(appPackageName))
                    list.add(new UsableTimeItem(queryUsageStats.get(i).getTotalTimeInForeground(),appName,1));
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

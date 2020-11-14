package com.example.myapplication.Fragment;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.UserManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activity.BatteryHealthActivity;
import com.example.myapplication.Activity.LoadingActivity;
import com.example.myapplication.Adapter.UsableTimeAdapter;
import com.example.myapplication.Model.ShellExecuter;
import com.example.myapplication.Model.UsableTimeItem;
import com.example.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static maes.tech.intentanim.CustomIntent.customType;


public class HomeFragment extends Fragment {
  CircularProgressBar circularProgressBar;
  RecyclerView recyclerView;
  TextView moreBtn;
  TextView value;
  ArrayList<UsableTimeItem> list;
  private int lastBatteryEnergy;
  TextView remainingTime;
  private  boolean returnFragment=false;

  public HomeFragment(int lastBatteryEnergy) {

    this.lastBatteryEnergy = lastBatteryEnergy;
  }

  public HomeFragment(boolean returnFragment) {

    this.returnFragment = returnFragment;
  }

  @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);
      View view= inflater.inflate(R.layout.fragment_home,container,false);
      circularProgressBar = view.findViewById(R.id.circularProgressBar);
      moreBtn=view.findViewById(R.id.loadMore);
      value=view.findViewById(R.id.progressValue);
      recyclerView=view.findViewById(R.id.recyclerView);
      remainingTime=view.findViewById(R.id.remainingTime);
      //setup list usable item
      AddItemToList();
      SetUpList();
      //Get battery level
      list=new ArrayList<>();
      GetBatteryInfo();
      GetSystemInfo();

      return view;
    }

  private void GetSystemInfo() {
    ShellExecuter exe = new ShellExecuter();
    String command = "ls";

    String outp = exe.Executer(command);

    Log.d("Output", outp);
  }


  private void GetBatteryInfo() {
    Intent batteryStatus = getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    int batteryLevel=0;
    batteryLevel=batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    double batLevel =  batteryLevel/ (double) scale;
    value.setText(""+batteryLevel);
    circularProgressBar.setProgress((float)batteryLevel);
    SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0);
    if(returnFragment==true) {
      String estimateTime=pref.getString("prefTime", "Unknown");
      if(!estimateTime.isEmpty())
      remainingTime.setText(estimateTime);
      else remainingTime.setVisibility(View.GONE);
    }

    else
    {
     BatteryManager mBatteryManager = (BatteryManager)getContext().getSystemService(Context.BATTERY_SERVICE);
     int  estimated=mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
     /*Tính tam suất giữa lượng pin mất mác trong 5s và lượng pin còn lại */
     int sub=lastBatteryEnergy-estimated;
     float resultSecond=(float)estimated*5/sub;
     float resultHours=  (resultSecond/3600);
     int temp= (int) (resultSecond/3600);
     float temp1= resultHours-temp;
     int resultMinute= (int) (temp1*60);
     if(sub>0)
     remainingTime.setText("It is expected to last for "+(int)resultHours+"h "+resultMinute+" min");
     else remainingTime.setVisibility(View.GONE);
     SharedPreferences.Editor editor = pref.edit();
     if(sub>0){
      editor.putString("prefTime","It is expected to last for "+(int)resultHours+"h "+resultMinute+" min");
      editor.commit();
      }
     else{
       editor.putString("prefTime","");
       editor.commit();
     }
    }

  }

  private void AddItemToList() {
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    Gson gson = new Gson();
    String json = sharedPrefs.getString("usageStats", "");
    Type type = new TypeToken<List<UsableTimeItem>>() {}.getType();
    list = gson.fromJson(json, type);
    Log.d("listsize",list.size()+"");
  }

  private void SetUpList() {

    final UsableTimeAdapter adapter = new UsableTimeAdapter(list, getContext());
    recyclerView.setAdapter(adapter);
    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
    value.setText("84%");
    moreBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (linearLayoutManager.findLastCompletelyVisibleItemPosition() < (adapter.getItemCount() - 1)) {
          linearLayoutManager.scrollToPosition(linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1);
        }
      }
    });
    recyclerView.setLayoutManager(linearLayoutManager);
    circularProgressBar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent=new Intent(getContext(), BatteryHealthActivity.class);
        intent.putExtra("goToBatHealth",true);
        startActivity(intent);
        customType(getContext(),"left-to-right");
      }
    });
  }
}

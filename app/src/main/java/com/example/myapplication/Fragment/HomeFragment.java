package com.example.myapplication.Fragment;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.UserManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activity.BatteryHealthActivity;
import com.example.myapplication.Activity.CellMapActivity;
import com.example.myapplication.Activity.LoadingActivity;
import com.example.myapplication.Adapter.UsableTimeAdapter;
import com.example.myapplication.Model.ShellExecuter;
import com.example.myapplication.Model.UsableTimeItem;
import com.example.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import static maes.tech.intentanim.CustomIntent.customType;


public class HomeFragment extends Fragment {
  CircularProgressBar circularProgressBar;
  RecyclerView recyclerView;
  TextView moreBtn,message;
  TextView value;
  ArrayList<UsableTimeItem> list;
  private int lastBatteryEnergy;
  TextView remainingTime;
  private  boolean returnFragment=false;
  String lang="";

  SharedPreferences sharedPrefs;


  @RequiresApi(api = Build.VERSION_CODES.Q)
  @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);
    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    loadLocal();
    lang=sharedPrefs.getString("lang","");
    View view;
    if(lang.equals("ar"))
    view= inflater.inflate(R.layout.ar_home_fragment,container,false);
    else view=inflater.inflate(R.layout.fragment_home,container,false);

      circularProgressBar = view.findViewById(R.id.circularProgressBar);
      moreBtn=view.findViewById(R.id.loadMore);
      value=view.findViewById(R.id.progressValue);
      recyclerView=view.findViewById(R.id.recyclerView);
      remainingTime=view.findViewById(R.id.remainingTime);
      message=view.findViewById(R.id.message);


    //




      //setup list usable item
      AddItemToList();
      SetUpList();
      //Get battery level
      list=new ArrayList<>();

      GetBatteryInfo();

    try {
      Map<String, String> getCPUInfo=getCPUInfo();
    } catch (IOException e) {
      e.printStackTrace();
    }

      return view;
    }

  private void loadLocal(){
    String lang=sharedPrefs.getString("lang","");


    if(lang!=null)
      setLocale(lang);
  }
  private void setLocale(String lang) {
    Locale locale= new Locale(lang);
    Locale.setDefault(locale);
    Configuration configuration=new Configuration();
    configuration.locale=locale ;
    getActivity().getBaseContext().getResources().updateConfiguration(configuration,getActivity().getBaseContext().getResources().getDisplayMetrics());
    SharedPreferences.Editor editor=sharedPrefs.edit();
    editor.putString("lang",lang);
    editor.commit();
  }

  public static Map<String, String> getCPUInfo () throws IOException {

    BufferedReader br = new BufferedReader (new FileReader("/proc/cpuinfo"));

    String str;

    Map<String, String> output = new HashMap<>();

    while ((str = br.readLine ()) != null) {

      String[] data = str.split (":");

      if (data.length > 1) {

        String key = data[0].trim ().replace (" ", "_");
        if (key.equals ("model_name")) key = "cpu_model";

        { output.put (key, data[1].trim ());

        }

      }

    }

    br.close ();

    return output;

  }


  private void GetBatteryInfo() {
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

    returnFragment=sharedPrefs.getBoolean("returnFragment",false);


    Intent batteryStatus = getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    int batteryLevel=0;
    batteryLevel=batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

    value.setText(""+batteryLevel+"%");
    circularProgressBar.setProgress((float)batteryLevel);
    SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0);
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      if (returnFragment == true) {

        String estimateTime = pref.getString("prefTime", "Unknown");
        if (!estimateTime.isEmpty())
          remainingTime.setText(estimateTime);
        else remainingTime.setVisibility(View.GONE);
      } else {

        long estimated = sharedPrefs.getLong("lastBattery",0);



        /*Tính tam suất giữa lượng pin mất mác trong 5s và lượng pin còn lại */
        long sub = sharedPrefs.getLong("sub",0);
        float resultSecond = (float) estimated * 6 / sub;
        float resultHours = (resultSecond / 3600);
        int temp = (int) (resultSecond / 3600);
        float temp1 = resultHours - temp;
        int resultMinute = (int) (temp1 * 60);
        Log.d("sub",sub+"");
        if (sub > 0)
          remainingTime.setText(getResources().getString(R.string.time_remaining)+ " " + (int) resultHours + "h " + resultMinute + " min");
        else remainingTime.setVisibility(View.GONE);
        SharedPreferences.Editor editor = pref.edit();
        if (sub > 0) {
          editor.putString("prefTime",getResources().getString(R.string.time_remaining)+ " " + (int) resultHours + "h " + resultMinute + " min");
          editor.commit();
        } else {
          editor.putString("prefTime", "");
          editor.commit();

        }
      }
    } else {
      batteryLevel=batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);


      value.setText(""+batteryLevel+"%");
      circularProgressBar.setProgress((float)batteryLevel);
      remainingTime.setVisibility(View.GONE);
    }

  }


  private void AddItemToList() {
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    Gson gson = new Gson();
    String json = sharedPrefs.getString("usageStats", "");
    Type type = new TypeToken<List<UsableTimeItem>>() {}.getType();
    list = gson.fromJson(json, type);

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
    if(lang.equals("ar")) linearLayoutManager.setReverseLayout(true);
    circularProgressBar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent=new Intent(getContext(), CellMapActivity.class);
        intent.putExtra("goToBatHealth",true);
        startActivity(intent);
        customType(getContext(),"left-to-right");
      }
    });
  }
}

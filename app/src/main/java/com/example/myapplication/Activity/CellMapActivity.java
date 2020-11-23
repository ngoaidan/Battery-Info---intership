package com.example.myapplication.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.example.myapplication.Adapter.CellAdapter;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Model.CellItem;
import com.example.myapplication.R;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

import static maes.tech.intentanim.CustomIntent.customType;

public class CellMapActivity extends AppCompatActivity {

    RecyclerView cellMap;
    ArrayList<CellItem> list;
    CellAdapter cellAdapter;
    LoadingButton button,skipButton;
    Toolbar toolbar;
    int [] low;
    int [] inative;
    Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_map);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SetUpStatusBar();
        SetUp();
        ActionToolBar();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.startLoading();
                for(int i=0;i<120;i++){
                    list.get(i).setColor(R.drawable.cell_item_background);
                }
               inative= new int[]{13, 23, 33, 43, 53, 63, 74, 91, 84, 102};
                for(int i=0;i<inative.length;i++)
                    list.get(inative[i]).setColor(R.drawable.cell_battery_inactive);
                low= new int[]{2, 4, 5, 7, 9, 11, 15, 16, 20, 28,39,55,69,93,110,};
                for(int i=0;i<inative.length;i++)
                    list.get(low[i]).setColor(R.drawable.cell_low_status);
                RunCheckBattery();

            }
        });
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                Intent intent=new Intent(CellMapActivity.this,BatteryHealthActivity.class);
                startActivity(intent);
                CustomIntent.customType(CellMapActivity.this,"left-to-right");
                CellMapActivity.this.finish();
            }
        });




    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
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
                customType(CellMapActivity.this,"right-to-left");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        CustomIntent.customType(CellMapActivity.this,"right-to-left");
        handler.removeCallbacksAndMessages(null);
    }

    int j=0;
    boolean isblank=false;
    int color=0;
    private void RunCheckBattery() {

        color=list.get(0).getColor();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {


                if(j==120) {
                    Log.d("loop","loop"+j);
                    handler.removeCallbacksAndMessages(null);
                    button.loadingSuccessful();
                    ShowDialog();
                }
                    if(j<list.size()) {
                        list.get(j).setColor(color);
                        if(j+1<list.size()){
                         color =list.get(j+1).getColor();
                        list.get(j+1).setColor(R.drawable.normal_cell_background);}
                        cellAdapter.notifyItemChanged(j+1);
                        cellAdapter.notifyItemChanged(j);

                        j++;
                        handler.postDelayed(this,300);
                    }



            }
        };
        runnable.run();
    }

    private void ShowDialog() {
        android.app.AlertDialog.Builder  mbuilder=new AlertDialog.Builder(CellMapActivity.this,R.style.CustomAlertDialog);
        View view=getLayoutInflater().inflate(R.layout.cell_dialog,null);
        TextView tvHealth,tvLow,tvInactive;
        LoadingButton btnDone;
        tvHealth=view.findViewById(R.id.cell_health);
        tvLow=view.findViewById(R.id.cell_low);
        tvInactive=view.findViewById(R.id.cell_inactive);
        btnDone=view.findViewById(R.id.button_done);
        mbuilder.setView(view);
        final AlertDialog alertDialog=mbuilder.create();
        alertDialog.setCancelable(false);
        tvHealth.setText("Healthy: "+(120-low.length-inative.length)+" cells");
        tvLow.setText("Low: "+low.length+" cells");
        tvInactive.setText("Inactive: "+inative.length+" cells");
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent=new Intent(CellMapActivity.this,BatteryHealthActivity.class);
                startActivity(intent);
                CustomIntent.customType(CellMapActivity.this,"left-to-right");
                CellMapActivity.this.finish();


            }
        });
        alertDialog.show();
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

    private void SetUp() {
        button=findViewById(R.id.button_cell);
        skipButton=findViewById(R.id.button_skip);
        cellMap=findViewById(R.id.cellMap);
        toolbar=findViewById(R.id.Cell_toolbar);
        list=new ArrayList<>();
        for(int i=0;i<120;i++)
            list.add(new CellItem());
        cellAdapter=new CellAdapter(list,CellMapActivity.this);
        cellMap.setAdapter(cellAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 12);
        cellMap.setLayoutManager(layoutManager);

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

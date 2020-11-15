package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.SharedPreferences;
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
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {
    ChipNavigationBar navigationBar;
    int lastBatteryEnnergy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastBatteryEnnergy=getIntent().getIntExtra("remainingBattery",0);
        setStatusBar();
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

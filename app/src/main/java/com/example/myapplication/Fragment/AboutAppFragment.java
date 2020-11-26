package com.example.myapplication.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import java.util.Locale;

public class AboutAppFragment extends Fragment {
    TextView tvUpdate,tvContact,tvTernOfUse,tvPrivacy,tvAttention,tvLangs;
    SharedPreferences sharedPrefs;
    String []langCode;
    int currentLang=0;
    String lang;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        lang =sharedPrefs.getString("lang","");
        loadLocal();
        View view;
        if(lang.equals("ar"))
            view= inflater.inflate(R.layout.ar_fragment_about_app,container,false);
        else view=inflater.inflate(R.layout.fragment_about_app,container,false);
        langCode=new String[]{"en","vi","ar","de","es","fr","it","ja","ko","pt","ru"};
        tvUpdate=view.findViewById(R.id.tvUpdate);
        tvContact=view.findViewById(R.id.tvContact);
        tvTernOfUse=view.findViewById(R.id.tvTermOfUse);
        tvPrivacy=view.findViewById(R.id.tvPrivacy);
        tvAttention=view.findViewById(R.id.tvAttention);
        tvLangs=view.findViewById(R.id.tvLanguage);
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkToCHPlay();
            }
        });
        tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenContact();
            }
        });
        tvTernOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenContact();
            }
        });
        tvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenContact();
            }
        });
        tvAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenContact();
            }
        });
        tvLangs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectLang();
            }
        });

        return view;
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

    private boolean isLanguageInList(String[] list, String locale) {
        if (list == null) {
            return false;
        }
        for(int i=0;i<list.length;i++){
            if(list[i].equals(locale)) return true;
        }
        return false;
    }
    private void refeshLayout(){
        Intent intent = getActivity().getIntent();

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
       getActivity().finish();

        startActivity(intent);
    }

    private void SelectLang() {
        ShowLangsDialog();
    }
    private void ShowLangsDialog(){
        android.app.AlertDialog.Builder  mbuilder;

        currentLang=getCurrentLang();
        mbuilder=new AlertDialog.Builder(getContext(),R.style.CustomLangDialog);
        View view;
        view=getLayoutInflater().inflate(R.layout.langs_dialog,null);
        final RadioGroup radioGroup=view.findViewById(R.id.langRadioGroup);
        mbuilder.setView(view);
        final AlertDialog alertDialog=mbuilder.create();

        ((RadioButton)radioGroup.getChildAt(currentLang)).setChecked(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());

                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    setLocale((String) checkedRadioButton.getHint());
                    refeshLayout();
                    SharedPreferences.Editor editor=sharedPrefs.edit();
                    editor.putBoolean("changeLang",true);
                    editor.commit();
                }
            }
        });
        alertDialog.show();
    }

    private int getCurrentLang() {
        for(int i=0;i<langCode.length;i++)
            if(langCode[i].equals(lang)) return i;
            return 0;
    }

    private void OpenContact() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://banabatech.com/"));
        startActivity(browserIntent);
    }
    private void loadLocal(){
        String lang=sharedPrefs.getString("lang","");

        if(lang.isEmpty()){

            Locale locale=Locale.getDefault();
            //Toast.makeText(this, locale.getDefault().getLanguage(), Toast.LENGTH_SHORT).show();
            if(isLanguageInList(langCode,locale.getDefault().getLanguage()))
                setLocale(locale.getDefault().getLanguage() );
            else   setLocale("en");
            return;
        }
        if(lang!=null)
            setLocale(lang);
    }

    private void LinkToCHPlay() {
        final String appPackageName = getContext().getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.wondergames.warpath.gp")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}

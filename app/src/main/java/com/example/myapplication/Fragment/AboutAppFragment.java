package com.example.myapplication.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

public class AboutAppFragment extends Fragment {
    TextView tvUpdate,tvContact,tvTernOfUse,tvPrivacy,tvAttention;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view= inflater.inflate(R.layout.fragment_about_app,container,false);
        tvUpdate=view.findViewById(R.id.tvUpdate);
        tvContact=view.findViewById(R.id.tvContact);
        tvTernOfUse=view.findViewById(R.id.tvTermOfUse);
        tvPrivacy=view.findViewById(R.id.tvPrivacy);
        tvAttention=view.findViewById(R.id.tvAttention);
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
        return view;
    }

    private void OpenContact() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://banabatech.com/"));
        startActivity(browserIntent);
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

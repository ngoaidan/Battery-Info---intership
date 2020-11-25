package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.PhoneInfoItem;
import com.example.myapplication.R;

import java.util.ArrayList;

public class PhoneInfoAdapter extends RecyclerView.Adapter<PhoneInfoAdapter.ViewHolder> {
    ArrayList<PhoneInfoItem> list;
    Context context;
    String lang;
    public PhoneInfoAdapter(ArrayList<PhoneInfoItem> list, Context context, String lang) {
        this.list = list;
        this.context = context;
        this.lang=lang;
    }

    @NonNull
    @Override
    public PhoneInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(lang.equals("ar"))
        return  new PhoneInfoAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.ar_phone_info_item, parent, false));
        else return  new PhoneInfoAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.phone_info_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneInfoAdapter.ViewHolder holder, int position) {
        holder.name.setText(list.get(position).getInfoName());
        holder.value.setText(list.get(position).getInfoValue());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,value;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.infoName);
            value=itemView.findViewById(R.id.infoValue);
        }
    }
}

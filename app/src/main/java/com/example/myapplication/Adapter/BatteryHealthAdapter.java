package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.BatteryHealthItem;
import com.example.myapplication.R;

import java.util.ArrayList;

public class BatteryHealthAdapter extends RecyclerView.Adapter<BatteryHealthAdapter.ViewHolder> {
    ArrayList<BatteryHealthItem> list;
    Context context;

    public BatteryHealthAdapter(ArrayList<BatteryHealthItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public BatteryHealthAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.battery_health_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BatteryHealthAdapter.ViewHolder holder, int position) {
        holder.nameTextView.setText(list.get(position).getName());
        holder.statusTextView.setText(list.get(position).getStatus());
        if(position==0) holder.iconImage.setImageResource(R.drawable.battery_health);
        if(position==1) holder.iconImage.setImageResource(R.drawable.estimated);
        if(position==2) holder.iconImage.setImageResource(R.drawable.charge);
        if(position==3) holder.iconImage.setImageResource(R.drawable.battery_size);
        if(position==4) holder.iconImage.setImageResource(R.drawable.voltage);
        if(position==5) holder.iconImage.setImageResource(R.drawable.temperature);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView, statusTextView;
        ImageView iconImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView=itemView.findViewById(R.id.healthName);
            statusTextView=itemView.findViewById(R.id.healthStatus);
            iconImage=itemView.findViewById(R.id.healthIcon);
        }
    }
}

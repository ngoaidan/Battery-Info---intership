package com.example.myapplication.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.UsableTimeItem;
import com.example.myapplication.R;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;
import com.skydoves.progressview.ProgressView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UsableTimeAdapter  extends RecyclerView.Adapter<UsableTimeAdapter.ViewHolder> {
    private ArrayList<UsableTimeItem> list;
    private Context context;
    public UsableTimeAdapter(ArrayList<UsableTimeItem> list, Context context) {
        this.list = list;
        this.context=context;

    }

    @NonNull
    @Override
    public UsableTimeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.usable_time_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UsableTimeAdapter.ViewHolder holder, int position) {
        if(list.get(position).getValue()<1)  holder.progressView.setProgress(1);
        else
        holder.progressView.setProgress(list.get(position).getValue());
        Log.d("item",""+list.get(position).getValue());
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        holder.value.setText(df.format(list.get(position).getValue())+"%");
        holder.name.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if(list!=null) return list.size(); else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ProgressView progressView;
        TextView name,value;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            progressView=itemView.findViewById(R.id.progressViewItem);
            name=itemView.findViewById(R.id.name);
            value=itemView.findViewById(R.id.percentValue);
        }
    }
}

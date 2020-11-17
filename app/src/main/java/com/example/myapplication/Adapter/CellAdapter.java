package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.CellItem;
import com.example.myapplication.R;

import java.util.ArrayList;

public class CellAdapter extends RecyclerView.Adapter<CellAdapter.ViewHolder> {
    ArrayList<CellItem> list;
    Context context;

    public CellAdapter(ArrayList<CellItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CellAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(list.get(position).getColor()!=0)
        {
            holder.item.setBackgroundResource(list.get(position).getColor());
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item=itemView.findViewById(R.id.cell_item);
        }
    }
}

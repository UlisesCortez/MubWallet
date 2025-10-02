package com.example.mubwallet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SimpleItemAdapter extends RecyclerView.Adapter<SimpleItemAdapter.VH> {
    private final List<String> items = new ArrayList<>();

    public void setItems(List<String> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View view = LayoutInflater.from(p.getContext())
                .inflate(android.R.layout.simple_list_item_1, p, false);
        return new VH(view);
    }
    @Override public void onBindViewHolder(@NonNull VH h, int i) { h.txt.setText(items.get(i)); }
    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView txt; VH(@NonNull View v){ super(v); txt = v.findViewById(android.R.id.text1); }
    }
}

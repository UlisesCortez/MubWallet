package com.example.mubwallet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SubscriptionsAdapter extends RecyclerView.Adapter<SubscriptionsAdapter.VH> {

    public interface OnItemLongPress {
        void onItemLongPressed(CalendarActivity.SubscriptionItem item);
    }

    private final List<CalendarActivity.SubscriptionItem> data;
    private final OnItemLongPress longPress;

    public SubscriptionsAdapter(List<CalendarActivity.SubscriptionItem> data, OnItemLongPress longPress) {
        this.data = data;
        this.longPress = longPress;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subscription, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        CalendarActivity.SubscriptionItem it = data.get(pos);
        h.tvName.setText(it.name);
        h.tvPlanDate.setText(it.plan + " • " + it.billingDate);
        h.tvCard.setText(it.cardLabel);

        if (it.amount != null) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
            h.tvAmount.setText(nf.format(it.amount));
            h.tvAmount.setVisibility(View.VISIBLE);
        } else {
            h.tvAmount.setVisibility(View.GONE);
        }

        h.itemView.setOnLongClickListener(v -> {
            if (longPress != null) longPress.onItemLongPressed(it);
            return true;
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvPlanDate, tvCard, tvAmount;
        VH(@NonNull View v) {
            super(v);
            tvName    = v.findViewById(R.id.tvName);
            tvPlanDate= v.findViewById(R.id.tvPlanDate);
            tvCard    = v.findViewById(R.id.tvCard);
            tvAmount  = v.findViewById(R.id.tvAmount);
        }
    }
}

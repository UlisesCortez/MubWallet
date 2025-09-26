// app/src/main/java/com/example/mubwallet/CardsAdapter.java
package com.example.mubwallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardViewHolder> {

    private final Context context;
    private final List<Card> cardList;

    public CardsAdapter(Context context, List<Card> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wallet_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cardList.get(position);
        holder.tvBank.setText(card.getBank());
        holder.tvAlias.setText(card.getAlias());
        holder.tvDigits.setText(card.getDigits());
        holder.tvBrand.setText(card.getBrand());
        holder.root.setBackgroundResource(card.getBackgroundRes());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public void addCard(Card c) {
        cardList.add(0, c);          // insertamos arriba
        notifyItemInserted(0);
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tvBank, tvAlias, tvDigits, tvBrand;
        RelativeLayout root;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.cardRoot);
            tvBank = itemView.findViewById(R.id.tvBank);
            tvAlias = itemView.findViewById(R.id.tvAlias);
            tvDigits = itemView.findViewById(R.id.tvDigits);
            tvBrand = itemView.findViewById(R.id.tvBrand);
        }
    }
}

// app/src/main/java/com/example/mubwallet/HomeActivity.java
package com.example.mubwallet;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvCards;
    private CardsAdapter adapter;
    private final List<Card> cardList = new ArrayList<>();

    private final ActivityResultLauncher<Intent> addCardLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String bank  = result.getData().getStringExtra("bank");
                    String alias = result.getData().getStringExtra("alias");
                    String digits = result.getData().getStringExtra("digits");
                    String brand = result.getData().getStringExtra("brand");

                    String last = "•••• " + digits;

                    int bg = pickBackgroundForBank(bank);

                    Card c = new Card(bank, alias, last, brand, bg);
                    adapter.addCard(c);
                    rvCards.scrollToPosition(0);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        rvCards = findViewById(R.id.rvCards);
        rvCards.setLayoutManager(new LinearLayoutManager(this));

        // (opcional) tarjeta de ejemplo
        // cardList.add(new Card("Santander", "Débito", "•••• 5678", "Mastercard", R.drawable.bg_card_santander));

        adapter = new CardsAdapter(this, cardList);
        rvCards.setAdapter(adapter);

        findViewById(R.id.fabAdd).setOnClickListener(v -> {
            Intent i = new Intent(this, AddCardActivity.class);
            addCardLauncher.launch(i);
        });


    }

    private int pickBackgroundForBank(String bank) {
        if (bank == null) return R.drawable.bg_card_santander;
        String b = bank.toLowerCase();
        if (b.contains("santander")) return R.drawable.bg_card_santander;
        if (b.contains("bbva"))      return R.drawable.bg_card_bbva;
        if (b.contains("nu"))        return R.drawable.bg_card_nu;
        // fallback
        return R.drawable.bg_card_santander;
    }
}

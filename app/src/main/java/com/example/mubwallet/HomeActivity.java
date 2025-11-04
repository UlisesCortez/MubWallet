package com.example.mubwallet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mubwallet.Database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvCards;
    private CardsAdapter adapter;
    private final List<Card> cardList = new ArrayList<>();
    private final List<Integer> cardIds = new ArrayList<>();

    private DatabaseHelper dbHelper;

    private final ActivityResultLauncher<Intent> detailLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> loadCardsFromDb());

    private final ActivityResultLauncher<Intent> addCardLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> loadCardsFromDb());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);

        rvCards = findViewById(R.id.rvCards);
        if (rvCards != null) {
            rvCards.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CardsAdapter(this, cardList);
            rvCards.setAdapter(adapter);
            attachItemTapListener();
        }

        // ---- FAB para abrir pantalla de suscripciones (CalendarActivity) ----
        View fabCalendar = findViewById(R.id.fabCalendar);
        if (fabCalendar != null) {
            fabCalendar.setOnClickListener(v -> {
                Intent i = new Intent(this, CalendarActivity.class);
                startActivity(i);
            });
        }

        // ---- FAB para agregar nueva tarjeta ----
        View fabAdd = findViewById(R.id.fabAdd);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                Intent i = new Intent(this, AddCardActivity.class);
                addCardLauncher.launch(i);
            });
        }

        loadCardsFromDb();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCardsFromDb();
    }

    private void loadCardsFromDb() {
        int idUser = 1; // TODO: usar el id real del usuario logueado

        cardList.clear();
        cardIds.clear();

        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor c = db.rawQuery(
                     "SELECT id_Card, num_tarjeta, Bank, Type FROM Cards WHERE id_User = ? ORDER BY id_Card DESC",
                     new String[]{String.valueOf(idUser)})) {

            while (c != null && c.moveToNext()) {
                int idCard = c.getInt(c.getColumnIndexOrThrow("id_Card"));
                String num = c.getString(c.getColumnIndexOrThrow("num_tarjeta"));
                String bank = safe(c.getString(c.getColumnIndexOrThrow("Bank")));
                String type = safe(c.getString(c.getColumnIndexOrThrow("Type")));

                String digits = maskDigits(num);
                String alias = bank.isEmpty() ? ("Card #" + idCard) : bank;
                String brand = type.isEmpty() ? detectBrand(bank) : type;
                int bg = pickBackgroundForBank(bank);

                cardIds.add(idCard);
                cardList.add(new Card(bank, alias, digits, brand, bg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private String safe(String s) { return s == null ? "" : s; }

    private String maskDigits(String num) {
        if (num == null) return "•••• ••••";
        String clean = num.replaceAll("\\s+", "");
        if (clean.length() >= 4) {
            String last4 = clean.substring(clean.length() - 4);
            return "•••• " + last4;
        }
        return clean;
    }

    private String detectBrand(String bank) {
        String b = bank == null ? "" : bank.toLowerCase();
        if (b.contains("nu")) return "Mastercard";
        if (b.contains("bbva")) return "Visa";
        if (b.contains("santander")) return "Visa";
        return "";
    }

    private int pickBackgroundForBank(String bank) {
        String b = bank == null ? "" : bank.toLowerCase();
        if (b.contains("santander")) return R.drawable.bg_card_santander;
        if (b.contains("bbva"))      return R.drawable.bg_card_bbva;
        if (b.contains("nu"))        return R.drawable.bg_card_nu;
        return R.drawable.bg_card_santander;
    }

    private void attachItemTapListener() {
        GestureDetector gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) { return true; }
                });

        rvCards.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                    if (position != RecyclerView.NO_POSITION) {
                        int idCard = cardIds.get(position);
                        Intent i = new Intent(HomeActivity.this, CardDetailActivity.class);
                        i.putExtra("id_card", idCard);
                        detailLauncher.launch(i);
                    }
                    return true;
                }
                return false;
            }

            @Override public void onTouchEvent(RecyclerView rv, MotionEvent e) {}
            @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    }
}

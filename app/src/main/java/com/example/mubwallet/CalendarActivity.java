package com.example.mubwallet;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mubwallet.Database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    private CalendarView calendarView;
    private RecyclerView rvSubs;
    private SubscriptionsAdapter adapter;
    private View emptyState;
    private FloatingActionButton fabAddSub, fabToday;

    private final List<SubscriptionItem> items = new ArrayList<>();

    private final int idUser = 1; // TODO: id real del usuario
    private String selectedDate;   // formato YYYY-MM-DD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        dbHelper = new DatabaseHelper(this);

        calendarView = findViewById(R.id.cvCalendar);
        rvSubs       = findViewById(R.id.rvSubscriptions);
        emptyState   = findViewById(R.id.emptyState);
        fabAddSub    = findViewById(R.id.fabAddSubscription);
        fabToday     = findViewById(R.id.fabChart);

        // Recycler
        rvSubs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubscriptionsAdapter(items, new SubscriptionsAdapter.OnItemLongPress() {
            @Override
            public void onItemLongPressed(SubscriptionItem item) {
                confirmDelete(item.idSubscription);
            }
        });
        rvSubs.setAdapter(adapter);

        // Fecha inicial: hoy
        Calendar cal = Calendar.getInstance();
        selectedDate = toYmd(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        calendarView.setDate(System.currentTimeMillis(), false, true);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = toYmd(year, month, dayOfMonth);
                loadSubscriptionsByDate();
            }
        });

        fabAddSub.setOnClickListener(v -> {
            // Abre tu pantalla para crear suscripción (si ya la tienes)
            startActivity(new Intent(this, AddSubscriptionActivity.class));
        });

        fabToday.setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            calendarView.setDate(now, true, true);
            Calendar c = Calendar.getInstance();
            selectedDate = toYmd(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            loadSubscriptionsByDate();
        });

        FloatingActionButton fabChart = findViewById(R.id.fabChart);
        fabChart.setOnClickListener(v -> {
            // Usamos el mes de la fecha seleccionada (YYYY-MM)
            String yearMonth = (selectedDate != null && selectedDate.length() >= 7)
                    ? selectedDate.substring(0, 7)
                    : new java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.US).format(new java.util.Date());

            Intent i = new Intent(this, GraphActivity.class);
            i.putExtra("yearMonth", yearMonth); // ej: "2025-11"
            startActivity(i);
        });

        loadSubscriptionsByDate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubscriptionsByDate();
    }

    private void loadSubscriptionsByDate() {
        items.clear();

        try (Cursor c = dbHelper.getSubscriptionsByUserAndDate(idUser, selectedDate)) {
            while (c != null && c.moveToNext()) {
                int idSub   = c.getInt(c.getColumnIndexOrThrow("id_Subscription"));
                String name = safe(c.getString(c.getColumnIndexOrThrow("name")));
                String plan = safe(c.getString(c.getColumnIndexOrThrow("plan_type")));
                String date = safe(c.getString(c.getColumnIndexOrThrow("billing_date")));
                Double amt  = c.isNull(c.getColumnIndexOrThrow("amount")) ? null : c.getDouble(c.getColumnIndexOrThrow("amount"));
                String bank = safe(c.getString(c.getColumnIndexOrThrow("Bank")));
                String last4= last4(c.getString(c.getColumnIndexOrThrow("num_tarjeta")));

                items.add(new SubscriptionItem(
                        idSub,
                        name,
                        plan,
                        date,
                        amt,
                        bank + " •••• " + last4
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error cargando suscripciones", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
        emptyState.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        rvSubs.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void confirmDelete(int idSub) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar suscripción")
                .setMessage("¿Deseas eliminar esta suscripción?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    int rows = dbHelper.deleteSubscription(idSub);
                    if (rows > 0) {
                        loadSubscriptionsByDate();
                        Toast.makeText(this, "Eliminada", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String toYmd(int year, int monthZeroBased, int day) {
        // month viene 0-11
        return String.format(Locale.US, "%04d-%02d-%02d", year, monthZeroBased + 1, day);
    }

    private String safe(String s) { return s == null ? "" : s; }

    private String last4(String num) {
        if (TextUtils.isEmpty(num)) return "0000";
        String clean = num.replaceAll("\\s+","");
        return clean.length() >= 4 ? clean.substring(clean.length() - 4) : clean;
    }

    // DTO simple para la lista
    public static class SubscriptionItem {
        public final int idSubscription;
        public final String name;
        public final String plan;
        public final String billingDate;
        public final Double amount;
        public final String cardLabel;

        public SubscriptionItem(int idSubscription, String name, String plan, String billingDate, Double amount, String cardLabel) {
            this.idSubscription = idSubscription;
            this.name = name;
            this.plan = plan;
            this.billingDate = billingDate;
            this.amount = amount;
            this.cardLabel = cardLabel;
        }
    }
}

package com.example.mubwallet;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView rvItems;
    private SimpleItemAdapter adapter;

    private final Map<String, List<String>> data = new HashMap<>();
    private String selectedKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        rvItems = findViewById(R.id.rvItems);
        adapter = new SimpleItemAdapter();

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);

        // init with today
        selectedKey = keyFromMillis(calendarView.getDate());
        render(selectedKey);

        calendarView.setOnDateChangeListener((v, y, m, d) -> {
            selectedKey = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
            render(selectedKey);
        });

        findViewById(R.id.fabAdd).setOnClickListener(v -> showAddDialog());
    }

    private void showAddDialog() {
        EditText input = new EditText(this);
        input.setHint("Agregar el nombre del servicio. Ejemplos: Agua");
        new AlertDialog.Builder(this)
                .setTitle("Agregar Servicio")
                .setView(input)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (d, w) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) return;
                    data.computeIfAbsent(selectedKey, k -> new ArrayList<>()).add(name);
                    render(selectedKey);
                })
                .show();
    }

    private void render(String key) {
        List<String> items = data.getOrDefault(key, Collections.emptyList());
        adapter.setItems(items);
    }

    private String keyFromMillis(long ms){
        Calendar c = Calendar.getInstance(); c.setTimeInMillis(ms);
        return String.format(Locale.US,"%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
    }
}

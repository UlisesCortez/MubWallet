package com.example.mubwallet;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mubwallet.Database.DatabaseHelper;

import java.util.Calendar;

public class AddSubscriptionActivity extends AppCompatActivity {

    private EditText etName, etAmount, etDate;
    private Spinner spPlan;
    private Button btnSave;

    private DatabaseHelper dbHelper;

    // TODO: reemplazar por el id real del usuario y la tarjeta seleccionada
    private final int idUser = 1;
    private final int idCard = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subscription);

        dbHelper  = new DatabaseHelper(this);

        etName   = findViewById(R.id.etName);
        etAmount = findViewById(R.id.etAmount);
        etDate   = findViewById(R.id.etDate);
        spPlan   = findViewById(R.id.spPlan);
        btnSave  = findViewById(R.id.btnSave);

        // Planes
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"Mensual", "Anual", "Semanal"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPlan.setAdapter(adapter);

        // Selector de fecha (YYYY-MM-DD)
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH);
            int d = c.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(this, (view, yy, mm, dd) -> {
                // month es 0-11
                String ymd = String.format("%04d-%02d-%02d", yy, mm + 1, dd);
                etDate.setText(ymd);
            }, y, m, d).show();
        });

        btnSave.setOnClickListener(v -> saveSubscription());
    }

    private void saveSubscription() {
        String name   = etName.getText().toString().trim();
        String amountS= etAmount.getText().toString().trim();
        String date   = etDate.getText().toString().trim();
        String plan   = spPlan.getSelectedItem().toString();

        if (name.isEmpty() || amountS.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountS);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        long newId = dbHelper.insertSubscription(idUser, idCard, name, plan, date, amount);
        if (newId > 0) {
            Toast.makeText(this, "Suscripción guardada", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }
}

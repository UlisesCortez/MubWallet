package com.example.mubwallet;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddCardActivity extends AppCompatActivity {

    private TextInputLayout tilBank, tilAlias, tilBrand, tilCard;
    private TextInputEditText etBank, etAlias, etBrand, etCard;

    private com.example.mubwallet.Database.DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        dbHelper = new com.example.mubwallet.Database.DatabaseHelper(this);

        tilBank = findViewById(R.id.tilBank);
        tilAlias = findViewById(R.id.tilAlias);
        tilBrand = findViewById(R.id.tilBrand);
        // IDs originales de tu XML
        tilCard = findViewById(R.id.firsDigits);

        etBank = findViewById(R.id.etBank);
        etAlias = findViewById(R.id.etAlias);
        etBrand = findViewById(R.id.etBrand);
        // ID original de tu XML
        etCard  = findViewById(R.id.firDigits);

        // El banco se autocompleta; bloquear edición manual
        etBank.setFocusable(false);
        etBank.setClickable(false);
        etBank.setCursorVisible(false);
        etBank.setLongClickable(false);
        etBank.setTextIsSelectable(false);

        etCard.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() >= 4) {
                    String prefix = s.toString().substring(0, 4);
                    String bank = detectBank(prefix);
                    etBank.setText(bank != null ? bank : "");
                    if (bank != null) tilBank.setError(null);

                    String brand = detectBrand(bank);
                    etBrand.setText(brand);
                } else {
                    etBank.setText("");
                    etBrand.setText("");
                }
                tilCard.setError(null);
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        MaterialButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            String bank      = text(etBank);
            String alias     = text(etAlias);
            String brand     = text(etBrand);
            String cardNum   = text(etCard);

            if (TextUtils.isEmpty(cardNum) || cardNum.length() != 16) {
                tilCard.setError("La tarjeta debe tener 16 dígitos");
                return;
            } else tilCard.setError(null);

            if (TextUtils.isEmpty(bank))   { tilBank.setError("Ingresa el banco"); return; } else tilBank.setError(null);
            if (TextUtils.isEmpty(alias))  { tilAlias.setError("Ingresa un alias"); return; } else tilAlias.setError(null);
            if (TextUtils.isEmpty(brand))  { tilBrand.setError("Ingresa la marca"); return; } else tilBrand.setError(null);

            int idUser = 1;        // TODO: reemplazar por el id del usuario logueado
            String exp = "12/30";  // TODO: placeholder hasta agregar campo en UI
            String cvv = "000";    // TODO: placeholder (no guardar CVV en producción)

            long newId = insertCardSQLite(idUser, cardNum, exp, cvv, bank, brand);
            if (newId == -1) {
                Toast.makeText(this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show();
                return;
            }

            String digits = cardNum.substring(cardNum.length() - 4);

            Intent data = new Intent();
            data.putExtra("bank", bank);
            data.putExtra("alias", alias);
            data.putExtra("digits", digits);
            data.putExtra("brand", brand);
            // data.putExtra("card_id", newId); // opcional
            setResult(RESULT_OK, data);
            Toast.makeText(this, "Tarjeta agregada", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private String text(TextInputEditText et) {
        return et != null && et.getText() != null ? et.getText().toString().trim() : "";
    }

    private String detectBank(String firstFour) {
        if (firstFour == null || firstFour.length() < 4) return null;
        switch (firstFour) {
            case "5101": return "Nu";
            case "5579": return "Santander";
            case "4152": return "BBVA";
            default:     return null;
        }
    }

    private String detectBrand(String bank) {
        if (bank == null) return "";
        switch (bank) {
            case "Nu":        return "Mastercard";
            case "BBVA":      return "Visa";
            case "Santander": return "Visa"; // o Mastercard, según prefieras
            default:          return "";
        }
    }

    private long insertCardSQLite(int idUser, String numTarjeta, String expirationDate, String cvv, String bank, String type) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("id_User", idUser);
            cv.put("num_tarjeta", numTarjeta);
            cv.put("expiration_date", expirationDate);
            cv.put("CVV", cvv);
            cv.put("Bank", bank);
            cv.put("Type", type);
            return db.insert("Cards", null, cv);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}

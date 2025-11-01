package com.example.mubwallet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mubwallet.Database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class CardDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int idCard;

    private MaterialCardView cardPreview;
    private MaterialTextView tvAlias, tvDigits, tvBrand;
    private ImageView ivChip; // por si quieres un icono, opcional
    private TextInputEditText etBank, etType;
    private MaterialButton btnSave, btnDelete;

    private String bank = "";
    private String type = "";
    private String num = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        dbHelper = new DatabaseHelper(this);
        idCard = getIntent().getIntExtra("id_card", -1);
        if (idCard <= 0) {
            Toast.makeText(this, "Tarjeta no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cardPreview = findViewById(R.id.cardPreview);
        tvAlias     = findViewById(R.id.tvAlias);
        tvDigits    = findViewById(R.id.tvDigits);
        tvBrand     = findViewById(R.id.tvBrand);
        ivChip      = findViewById(R.id.ivChip);

        etBank      = findViewById(R.id.etBankEdit);
        etType      = findViewById(R.id.etTypeEdit);

        btnSave     = findViewById(R.id.btnSave);
        btnDelete   = findViewById(R.id.btnDelete);

        loadCard();

        btnSave.setOnClickListener(v -> {
            String newBank = text(etBank);
            String newType = text(etType);
            int rows = dbHelper.updateCardBankType(idCard, newBank, newType);
            if (rows > 0) {
                Toast.makeText(this, "Tarjeta actualizada", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            int rows = dbHelper.deleteCard(idCard);
            if (rows > 0) {
                Toast.makeText(this, "Tarjeta eliminada", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCard() {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor c = db.rawQuery(
                     "SELECT num_tarjeta, Bank, Type FROM Cards WHERE id_Card = ? LIMIT 1",
                     new String[]{String.valueOf(idCard)})) {

            if (c != null && c.moveToFirst()) {
                num  = c.getString(c.getColumnIndexOrThrow("num_tarjeta"));
                bank = safe(c.getString(c.getColumnIndexOrThrow("Bank")));
                type = safe(c.getString(c.getColumnIndexOrThrow("Type")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String digits = maskDigits(num);
        String brand  = type.isEmpty() ? detectBrand(bank) : type;
        int bg        = pickBackgroundForBank(bank);

        cardPreview.setBackgroundResource(bg);
        tvAlias.setText(bank.isEmpty() ? ("Card #" + idCard) : bank);
        tvDigits.setText(digits);
        tvBrand.setText(brand);

        etBank.setText(bank);
        etType.setText(brand);
    }

    private String text(TextInputEditText et) {
        return et != null && et.getText() != null ? et.getText().toString().trim() : "";
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
}

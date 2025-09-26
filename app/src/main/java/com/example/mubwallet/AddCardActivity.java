package com.example.mubwallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddCardActivity extends AppCompatActivity {

    private TextInputLayout tilBank, tilAlias, tilDigits, tilBrand;
    private TextInputEditText etBank, etAlias, etDigits, etBrand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        tilBank = findViewById(R.id.tilBank);
        tilAlias = findViewById(R.id.tilAlias);
        tilDigits = findViewById(R.id.tilDigits);
        tilBrand = findViewById(R.id.tilBrand);

        etBank = findViewById(R.id.etBank);
        etAlias = findViewById(R.id.etAlias);
        etDigits = findViewById(R.id.etDigits);
        etBrand = findViewById(R.id.etBrand);

        MaterialButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            String bank = text(etBank);
            String alias = text(etAlias);
            String digits = text(etDigits);
            String brand = text(etBrand);

            if (TextUtils.isEmpty(bank)) { tilBank.setError("Ingresa el banco"); return; } else tilBank.setError(null);
            if (TextUtils.isEmpty(alias)) { tilAlias.setError("Ingresa un alias"); return; } else tilAlias.setError(null);
            if (digits.length() != 4) { tilDigits.setError("Deben ser 4 dígitos"); return; } else tilDigits.setError(null);
            if (TextUtils.isEmpty(brand)) { tilBrand.setError("Ingresa la marca"); return; } else tilBrand.setError(null);

            Intent data = new Intent();
            data.putExtra("bank", bank);
            data.putExtra("alias", alias);
            data.putExtra("digits", digits);
            data.putExtra("brand", brand);
            setResult(RESULT_OK, data);
            Toast.makeText(this, "Tarjeta agregada", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private String text(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}

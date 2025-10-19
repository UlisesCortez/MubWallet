package com.example.mubwallet;

import android.content.Intent;
import android.os.Bundle;
import android.database.Cursor;
import android.text.TextUtils;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mubwallet.Database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.mubwallet.Database.DatabaseHelper;


public class LoginActivity extends AppCompatActivity {
    private TextInputLayout tilUser, tilPass;
    private TextInputEditText etUser, etPass;
    private MaterialButton btnLogin;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilUser = findViewById(R.id.tilUser);
        tilPass = findViewById(R.id.tilPass);
        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);

        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> {
            String user = etUser.getText() != null ? etUser.getText().toString().trim() : "";
            String pass = etPass.getText() != null ? etPass.getText().toString().trim() : "";

            if (TextUtils.isEmpty(user)) {
                tilUser.setError("Ingresa tu usuario");
                return;
            } else {
                tilUser.setError(null);
            }

            if (TextUtils.isEmpty(pass)) {
                tilPass.setError("Ingresa tu contraseña");
                return;
            } else {
                tilPass.setError(null);
            }

            if (validarUsuario(user, pass)) {
                Toast.makeText(this, "Bienvenido, " + user, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, HomeActivity.class);
                i.putExtra("username", user);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean validarUsuario(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "Users",
                new String[]{"id_User"},
                "Name = ? AND password = ?",
                new String[]{username, password},
                null, null, null
        );

        boolean existe = cursor.moveToFirst();
        cursor.close();
        db.close();
        return existe;
    }
}

package com.example.mubwallet.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mub_wallet.db";
    private static final int DATABASE_VERSION = 2; // sube versión para aplicar onUpgrade

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Users (" +
                "id_User INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Name TEXT NOT NULL," +
                "last_Name TEXT NOT NULL," +
                "register_Date TEXT," +
                "modification_Date TEXT," +
                "password TEXT NOT NULL)");

        db.execSQL("CREATE TABLE Cards (" +
                "id_Card INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_User INTEGER NOT NULL," +
                "num_tarjeta TEXT NOT NULL," +
                "expiration_date TEXT NOT NULL," +
                "CVV TEXT NOT NULL," +
                "Bank TEXT," +
                "Type TEXT," +
                "FOREIGN KEY(id_User) REFERENCES Users(id_User) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE Subscriptions (" +
                "id_Subscription INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_User INTEGER NOT NULL," +
                "id_Card INTEGER NOT NULL," +
                "name TEXT," +
                "amount REAL," +
                "plan_type TEXT," +
                "billing_date TEXT," +
                "status TEXT," +
                "FOREIGN KEY(id_User) REFERENCES Users(id_User) ON DELETE CASCADE," +
                "FOREIGN KEY(id_Card) REFERENCES Cards(id_Card) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE Transactions (" +
                "id_Transaction INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_User INTEGER NOT NULL," +
                "id_Card INTEGER," +
                "subscription_id INTEGER," +
                "type TEXT," +
                "amount REAL," +
                "transaction_Date TEXT," +
                "description TEXT," +
                "status TEXT," +
                "FOREIGN KEY(id_User) REFERENCES Users(id_User) ON DELETE CASCADE," +
                "FOREIGN KEY(id_Card) REFERENCES Cards(id_Card) ON DELETE SET NULL," +
                "FOREIGN KEY(subscription_id) REFERENCES Subscriptions(id_Subscription) ON DELETE SET NULL)");

        db.execSQL("INSERT INTO Users (Name, last_Name, password) VALUES ('Brayan', 'root', '1234')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Transactions");
        db.execSQL("DROP TABLE IF EXISTS Subscriptions");
        db.execSQL("DROP TABLE IF EXISTS Cards");
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }

    // ---------- SUBSCRIPTIONS: utilidades ----------

    public Cursor getSubscriptionsByUser(int idUser) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT s.id_Subscription, s.name, s.amount, s.plan_type, s.billing_date, s.status, " +
                "c.id_Card, c.num_tarjeta, c.Bank, c.Type " +
                "FROM Subscriptions s JOIN Cards c ON c.id_Card = s.id_Card " +
                "WHERE s.id_User=? ORDER BY s.id_Subscription DESC";
        return db.rawQuery(sql, new String[]{String.valueOf(idUser)});
    }

    public int deleteSubscription(int idSubscription) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.delete("Subscriptions", "id_Subscription = ?", new String[]{String.valueOf(idSubscription)});
        } finally {
            db.close();
        }
    }

    public int updateSubscription(int idSubscription, int idCard, String name, Double amount,
                                  String planType, String billingDate, String status) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("id_Card", idCard);
            cv.put("name", name);
            if (amount != null) cv.put("amount", amount);
            cv.put("plan_type", planType);
            cv.put("billing_date", billingDate);
            cv.put("status", status);
            return db.update("Subscriptions", cv, "id_Subscription = ?", new String[]{String.valueOf(idSubscription)});
        } finally {
            db.close();
        }
    }

    // ===================== TARJETAS =====================

    public int updateCardBankType(int idCard, String bank, String type) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("Bank", bank);
            cv.put("Type", type);
            return db.update("Cards", cv, "id_Card = ?", new String[]{String.valueOf(idCard)});
        } finally {
            db.close();
        }
    }

    public int deleteCard(int idCard) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.delete("Cards", "id_Card = ?", new String[]{String.valueOf(idCard)});
        } finally {
            db.close();
        }
    }
    public Cursor getSubscriptionsByUserAndDate(int idUser, String ymd) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT s.id_Subscription, s.name, s.amount, s.plan_type, s.billing_date, s.status, " +
                "c.id_Card, c.num_tarjeta, c.Bank, c.Type " +
                "FROM Subscriptions s " +
                "JOIN Cards c ON c.id_Card = s.id_Card " +
                "WHERE s.id_User=? AND s.billing_date = ? " +
                "ORDER BY s.id_Subscription DESC";
        return db.rawQuery(sql, new String[]{String.valueOf(idUser), ymd});
    }
    public long insertSubscription(int idUser, int idCard, String name, String planType, String billingDate, double amount) {
        android.content.ContentValues values = new android.content.ContentValues();
        values.put("id_User", idUser);
        values.put("id_Card", idCard);
        values.put("name", name);
        values.put("amount", amount);
        values.put("plan_type", planType);
        values.put("billing_date", billingDate);
        values.put("status", "Activo");

        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();
        try {
            return db.insert("Subscriptions", null, values);
        } finally {
            db.close();
        }
    }
    public android.database.Cursor getDailyTotalsForMonth(int idUser, String yearMonth) {
        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT substr(billing_date, 9, 2) AS day, " +
                "SUM(COALESCE(amount,0)) AS total " +
                "FROM Subscriptions " +
                "WHERE id_User = ? AND billing_date LIKE ? " +
                "GROUP BY day ORDER BY day";
        return db.rawQuery(sql, new String[]{ String.valueOf(idUser), yearMonth + "%" });
    }
    // Totales del mes por nombre de suscripción (para gráfico de pastel)
    public android.database.Cursor getMonthlyTotalsByName(int idUser, String yearMonth) {
        // yearMonth = "YYYY-MM"
        android.database.sqlite.SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT COALESCE(name, 'Sin nombre') AS name, " +
                "SUM(COALESCE(amount,0)) AS total " +
                "FROM Subscriptions " +
                "WHERE id_User = ? AND billing_date LIKE ? " +
                "GROUP BY name " +
                "ORDER BY total DESC";
        return db.rawQuery(sql, new String[]{ String.valueOf(idUser), yearMonth + "%" });
    }


}

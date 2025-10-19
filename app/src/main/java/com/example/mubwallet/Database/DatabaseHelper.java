package com.example.mubwallet.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mub_wallet.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabla Users
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
                "FOREIGN KEY(id_User) REFERENCES Users(id_User))");

        db.execSQL("CREATE TABLE Subscriptions (" +
                "id_Subscription INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_User INTEGER NOT NULL," +
                "id_Card INTEGER NOT NULL," +
                "name TEXT," +
                "amount REAL," +
                "plan_type TEXT," +
                "billing_date TEXT," +
                "status TEXT," +
                "FOREIGN KEY(id_User) REFERENCES Users(id_User)," +
                "FOREIGN KEY(id_Card) REFERENCES Cards(id_Card))");

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
                "FOREIGN KEY(id_User) REFERENCES Users(id_User)," +
                "FOREIGN KEY(id_Card) REFERENCES Cards(id_Card)," +
                "FOREIGN KEY(subscription_id) REFERENCES Subscriptions(id_Subscription))");

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
}

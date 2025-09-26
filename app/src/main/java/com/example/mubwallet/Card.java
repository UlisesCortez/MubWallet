package com.example.mubwallet;

public class Card {
    private String bank;
    private String alias;
    private String digits;
    private String brand;
    private int backgroundRes;

    public Card(String bank, String alias, String digits, String brand, int backgroundRes) {
        this.bank = bank;
        this.alias = alias;
        this.digits = digits;
        this.brand = brand;
        this.backgroundRes = backgroundRes;
    }

    public String getBank() { return bank; }
    public String getAlias() { return alias; }
    public String getDigits() { return digits; }
    public String getBrand() { return brand; }
    public int getBackgroundRes() { return backgroundRes; }
}

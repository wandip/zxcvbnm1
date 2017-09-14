package com.dipak.calendardemo;

public class Menu {
    private String rice,vegie,special,other;

    public Menu(String rice,  String vegie, String special, String other) {
        this.rice = rice;
        this.vegie = vegie;
        this.special = special;
        this.other = other;
    }

    public String getRice() {
        return rice;
    }

    public void setRice(String rice) {
        this.rice = rice;
    }

    public String getVegie() {
        return vegie;
    }

    public void setVegie(String vegie) {
        this.vegie = vegie;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}

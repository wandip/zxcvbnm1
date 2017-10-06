package com.dipak.calendardemo;

import java.io.Serializable;

public class Menu implements Serializable{
    private String rice,roti,veg1,veg2,veg3,special,special2,other;

    public Menu(String rice, String roti, String veg1, String veg2, String veg3, String special, String special2, String other) {

        this.rice = rice;
        this.roti = roti;
        this.veg1 = veg1;
        this.veg2 = veg2;
        this.veg3 = veg3;
        this.special = special;
        this.special2 = special2;
        this.other = other;
    }

    @Override
    public String toString() {

        String menu = "";
        if (special != null && !special.equals("null") && special.length()>1) {
            menu = special + ", ";
        }
        if (special2 != null && !special2.equals("null") && special2.length()>1) {
            menu = menu + special2 + ", ";
        }
        if (veg1 != null && !veg1.equals("null") && veg1.length()>1) {
            menu = menu + veg1 + ", ";
        }
        if (veg2 != null && !veg2.equals("null") && veg2.length()>1) {
            menu = menu + veg2 + ", ";
        }
        if (veg3 != null && !veg3.equals("null") && veg3.length()>1) {
            menu = menu + veg3 + ", ";
        }
        if (rice != null && !rice.equals("null") && rice.length()>1) {
            menu = menu + rice + ", ";
        }
        if ((roti != null && !roti.equals("null")) && roti.length()>1) {
            menu = menu + roti + ", ";
        }

        if (other != null && !other.equals("null") && other.length()>1)  {
            menu = menu + other;
        }

        menu = menu + ".";

        return  menu;
    }


    public String getRice() {
        return rice;
    }



    public String getRoti() {
        return roti;
    }


    public String getVeg1() {
        return veg1;
    }



    public String getVeg2() {
        return veg2;
    }



    public String getVeg3() {
        return veg3;
    }



    public String getSpecial() {
        return special;
    }



    public String getSpecial2() {
        return special2;
    }



    public String getOther() {
        return other;}


}

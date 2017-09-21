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
        return  ", rice='" + rice + '\'' +
                ", roti='" + roti + '\'' +
                ", veg1='" + veg1 + '\'' +
                ", veg2='" + veg2 + '\'' +
                ", veg3='" + veg3 + '\'' +
                ", special='" + special + '\'' +
                ", special2='" + special2 + '\'' +
                ", other='" + other;
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
        return other;
    }


}

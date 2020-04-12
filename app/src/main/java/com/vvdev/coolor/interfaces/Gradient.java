package com.vvdev.coolor.interfaces;

public class Gradient {

    private String name;
    private String hexaValue;

    public Gradient(String name,String hexaValue) {
        setName(name);
        setHexaValue(hexaValue);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHexaValue() {
        return hexaValue;
    }

    public void setHexaValue(String hexaValue) {
        this.hexaValue = hexaValue;
    }

    public static String getShadesValue(){
        return "#000000";
    }

    public static String getTonesValue(){
        return "#707070";
    }

    public static String getTintsValue(){
        return "#f7f7f7";
    }

    public boolean equals(Gradient gradient) {
        return gradient.getName().equals(this.name)&&gradient.getHexaValue().equals(this.hexaValue);
    }
}

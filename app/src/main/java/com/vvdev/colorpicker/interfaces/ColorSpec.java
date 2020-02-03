package com.vvdev.colorpicker.interfaces;

public class ColorSpec { // https://htmlcolorcodes.com/fr/selecteur-de-couleur/

    private String hexa;
    private int[] hsv = new int[3];
    private int[] rgb = new int[3];

    private int[] complementary = new int[2];
    private int[] triadic = new int[3];
    private int[] tone = new int[6];



    public ColorSpec(int[] rgb) {
        setRGB(rgb);
        setHexa(ColorUtility.getHexFromRGB(rgb));
        setHSV(ColorUtility.getHsvFromRGB(rgb));
        setup();
    }

    public ColorSpec(String hexa) {
        setHexa(hexa);
        setRGB(ColorUtility.getRGBFromHex(hexa));
        setHSV(ColorUtility.getHsvFromRGB(getRGB()));
        setup();
    }

    public ColorSpec(String hexa, int[] rgb, int[] hsv) {
        setHexa(hexa);
        setHSV(hsv);
        setRGB(rgb);
        setup();
    }



    private void setup(){
        setComplementary();
        setTriadic();
        setTone();
    }

    private void setComplementary() {
    }

    private void setTriadic() {
    }


    private void setTone() {
    }



    public String getHexa() {
        return hexa;
    }

    private void setHexa(String hexa) {
        this.hexa = hexa;
    }

    public int[] getHSV() {
        return hsv;
    }

    private void setHSV(int[] HSV) {
        this.hsv = HSV;
    }

    public int[] getRGB() {
        return rgb;
    }

    private void setRGB(int[] RGB) {
        this.rgb = RGB;
    }

    public int[] getComplementary() {
        return complementary;
    }

    public int[] getTriadic() {
        return triadic;
    }

    public int[] getTone() {
        return tone;
    }
}

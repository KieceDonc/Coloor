package com.vvdev.colorpicker.interfaces;

import android.util.Log;

public class ColorSpec { // https://htmlcolorcodes.com/fr/selecteur-de-couleur/

    private String hexa;
    private int[] hsv = new int[3];
    private int[] rgb = new int[3];

    private String complementary ="";
    private String[] triadic = new String[3];
    private String[] tone = new String[6];
    private int[][] rgbShades = new int[6][3];
    private String[] shades = new String[6];



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
        setShades();
    }

    private void setComplementary() { // Complémentaire
        int[] complementaryRGB = new int[3];

        complementaryRGB[0] = 255-getRGB()[0];
        complementaryRGB[1] = 255-getRGB()[1];
        complementaryRGB[2] = 255-getRGB()[2];

        complementary=ColorUtility.getHexFromRGB(complementaryRGB);
    }

    /**
     * triadic[0] = this.hexa
     * triadic[ 1 / 2 ] = generate
     */
    private void setTriadic() { // Triadique
        int triadic1[] = new int[3];
        int triadic2[] = new int[3];

        triadic1[1]=getRGB()[0];
        triadic2[2]=getRGB()[0];
        triadic1[2]=getRGB()[1];
        triadic2[0]=getRGB()[1];
        triadic1[0]=getRGB()[2];
        triadic1[1]=getRGB()[2];

        triadic[0]=getHexa();
        triadic[1]=ColorUtility.getHexFromRGB(triadic1);
        triadic[2]=ColorUtility.getHexFromRGB(triadic2);
    }


    private void setTone() { // Tonalités

    }

    /**
     * shades[0] = this.hexa
     * triadic[ 1 / 2 /3 / 4 / 5 ] = generate
     */

    private void setShades(){ // Nuances
        int redDistanceBlack = (int) (getRGB()[0]/5.5);
        int greenDistanceBlack = (int) (getRGB()[1]/5.5);
        int blueDistanceBlack = (int) (getRGB()[2]/5.5);

        shades[0]=ColorUtility.getHexFromRGB(getRGB());
        rgbShades[0]=getRGB();
        for(int x=1;x<shades.length;x++){ // x=1 cuz shades[0] = this.hexa
            for(int y = 0;y<3;y++){
                switch (y){
                    case 0:{
                        rgbShades[x][y]=getRGB()[y]-redDistanceBlack*x;
                        break;
                    }
                    case 1:{
                        rgbShades[x][y]=getRGB()[y]-greenDistanceBlack*x;
                        break;
                    }
                    case 2:{
                        rgbShades[x][y]=getRGB()[y]-blueDistanceBlack*x;
                        break;
                    }
                    default:{
                        Log.e("ColorSpec","Error, setShades, out of bound y. ColorSpec = "+toString());
                    }
                }
                shades[x]=ColorUtility.getHexFromRGB(rgbShades[x]);
            }
        }
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

    public String getComplementary() {
        return complementary;
    }

    public String[] getTriadic() {
        return triadic;
    }

    public String[] getTone() {
        return tone;
    }

    public String[] getShades(){
        return shades;
    }

    public int[][] getRgbShades(){
        return rgbShades;
    }

    public String toString(){
        return "Hexadecimal = "+hexa+"\n" +
                "RGB = RGB("+getRGB()[0]+", "+getRGB()[1]+", "+getRGB()[2]+")\n" +
                "HSV = HSV("+getHSV()[0]+", "+getHSV()[1]+", "+getHSV()[2]+")\n" +
                "Complementary = "+complementary+"\n" +
                "Triadic = "+getTriadic()[0]+", "+getTriadic()[1]+", "+getTriadic()[2]+"\n" +
                "Tone = TODO\n" +
                "Shades = "+getShades()[0]+", "+getShades()[1]+", "+getShades()[2]+", "+getShades()[3]+", "+getShades()[4]+", "+getShades()[5];


        //TODO make toString() of ColorSpec
    }
}

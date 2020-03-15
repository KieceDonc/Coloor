package com.vvdev.colorpicker.interfaces;

import android.app.Activity;
import android.util.Log;

import com.vvdev.colorpicker.R;

import java.util.ArrayList;
import java.util.Arrays;

public class ColorSpec { // https://htmlcolorcodes.com/fr/selecteur-de-couleur/

    private final static String TAG = ColorSpec.class.getName();


    private String hexa;
    private int[] hsv = new int[3]; // also hsb
    private int[] rgb = new int[3];
    private int[] hsl = new int[3];

    private ArrayList<String> methodName;
    private String[] complementary =new String[2];
    private String[] triadic = new String[3];
    private String[] tints = new String[6];
    private String[] tones = new String[6];
    private String[] shades = new String[6];

    public ColorSpec(ArrayList<String> methodName, int[] rgb) {
        this.methodName = methodName;
        setRGB(rgb);
        setHexa(ColorUtility.getHexFromRGB(rgb));
        setup();
    }

    public ColorSpec(ArrayList<String> methodName,String hexa) {
        this.methodName = methodName;
        setHexa(hexa);
        setRGB(ColorUtility.getRGBFromHex(hexa));
        setup();
    }

    private void setup(){
        setHSV(ColorUtility.getHsvFromRGB(getRGB()));
        /*setHSL(ColorUtility.getHslFromRGB(getRGB()));*/

        setShades();
        setTones();
        setTines();
        setTriadic();
        setComplementary();
    }

    /**
     * shades[0] = this.hexa
     * triadic[ 1 / 2 /3 / 4 / 5 ] = generate
     */

    private void setShades(){ // Nuances
        shades=ColorUtility.gradientApproximatelyGenerator(getHexa(),"#000000",6);
    }

    private void setTones() { // Tonalités
        tones=ColorUtility.gradientApproximatelyGenerator(getHexa(),"707070",6);
    }

    private void setTines() {
        tints=ColorUtility.gradientApproximatelyGenerator(getHexa(),"#f7f7f7",6);
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
        triadic2[1]=getRGB()[2];

        triadic[0]=getHexa();
        triadic[1]=ColorUtility.getHexFromRGB(triadic1);
        triadic[2]=ColorUtility.getHexFromRGB(triadic2);
    }

    private void setComplementary() { // Complémentaire
        int[] complementaryRGB = new int[3];

        complementaryRGB[0] = 255-getRGB()[0];
        complementaryRGB[1] = 255-getRGB()[1];
        complementaryRGB[2] = 255-getRGB()[2];

        complementary[0]=getHexa();
        complementary[1]=ColorUtility.getHexFromRGB(complementaryRGB);
    }


    public String getHexa() {
        return hexa;
    }

    private void setHexa(String hexa) {
        this.hexa = hexa.toUpperCase();
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

    public int[] getHSL() {
        return hsl;
    }

    public void setHSL(int[] hsl) {
        this.hsl = hsl;
    }

    public String[] getComplementary() {
        return complementary;
    }

    public String[] getTriadic() {
        return triadic;
    }

    public String[] getTones() {
        return tones;
    }

    public String[] getTints() {
        return tints;
    }

    public String[] getShades(){
        return shades;
    }

    public ArrayList<String[]> getAllGeneratedColors(){
        return new ArrayList<>(Arrays.asList(getShades(),getTones(),getTints(),getTriadic(),getComplementary()));
    }

    public ArrayList<String> getAllMethodName() {
        return methodName;
    }


    public String toString(){
        StringBuilder toReturn= new StringBuilder("ColorsSpec{\n" + "Hexa =" + hexa +"\n");
        for(int x=0;x<getAllMethodName().size();x++){
            toReturn.append(getAllMethodName().get(x)+" = ");
            for(int y=0;y<getAllGeneratedColors().get(x).length;y++){
                toReturn.append(getAllGeneratedColors().get(x)[y]+", ");
            }
            toReturn.append("\n");
        }
        toReturn.append("}");
        return toReturn.toString();

        //TODO make toString() of ColorSpec
    }

    /**
     * Used to check is the object is correct
     * @param toVerify object to verify
     * @param longCheck if true, check all hexa value length to know if it's correct or not
     * @return
     */
    public static boolean isCorrect(ColorSpec toVerify,boolean longCheck){
        if(toVerify.getHexa().length()<6){
            Log.e(TAG,"isCorrect() detect error.\nHexa color length of object is <6. Value ="+toVerify.getHexa()
            +"\nObject to verify toString()= "+toVerify.toString());
            return false;
        }
        if(toVerify.getRGB().length<3){
            return false;
        }
        if(toVerify.getHSV().length<3){
            return false;
        }
        if(toVerify.getHSL().length<3){
            return false;
        }

        if(toVerify.getAllMethodName().size()!=toVerify.getAllGeneratedColors().size()){ // you might forgot to add new method in getGenerateMethod() or getAllGeneratedColors()
            Log.e(TAG,"isCorrect() detect error.\nYou might forgot to add new method in getGenerateMethod() or getAllGeneratedColors().\nObject to verify to string = "+toVerify.toString());
            return false;
        }
        if(longCheck){
            for(int x=0;x<toVerify.getAllMethodName().size();x++){
                for(int y=0;y<toVerify.getAllGeneratedColors().get(x).length;x++){
                    if(toVerify.getAllGeneratedColors().get(x)[y].length()<6){
                        Log.e(TAG,"isCorrect() detect error."
                                + "\nMethod at position "+x+" and hexa value at "+y+" have a length <6"
                        +"\nWrong string value ="+toVerify.getAllGeneratedColors().get(x)[y]
                        +"\nObject to verify to string ="+toVerify.toString());
                        return false;
                    }
                }
            }
        }
        return true;
    }
}

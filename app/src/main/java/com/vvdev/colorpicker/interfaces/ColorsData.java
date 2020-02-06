package com.vvdev.colorpicker.interfaces;

public class ColorsData {

    private static ColorSpec[] all = new ColorSpec[]{new ColorSpec("#050505"),new ColorSpec("#26c1c8"),new ColorSpec("#a326c8"),new ColorSpec("#c82683"),
            new ColorSpec("#c3c826"),new ColorSpec("#c82626"),new ColorSpec("#26c89c"),new ColorSpec("#5cc826")};

    public static ColorSpec[] getColorsSpec(){
        return all;
    }
}

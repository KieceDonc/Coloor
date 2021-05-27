package com.vvdev.coolor.interfaces;

import java.io.File;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaveColorsToFile{

    private final Activity activity;

    public SaveColorsToFile(Activity activity){
        this.activity = activity;
        mainWork();
    }

    private void mainWork(){
        String[] fileExtensions = {".json",".txt"};
        String fileName = getFileName();
        File file = new File(Environment.getExternalStorageDirectory()+"/save_colors", fileName);
        try {
            file.mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos = null;

        try {
            for (String fileExtension : fileExtensions) {
                fos = new FileOutputStream(file + fileExtension);
                fos.write(getJsonObject().toString(1).getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (fos != null) {
                try {
                    Toast.makeText(activity, "Success : "+file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(activity, "Failed to write!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private JSONArray getJsonObject(){
        try{
            ArrayList<ColorSpec> colors = SavedData.getInstance(activity).getColors();
            JSONArray mainJson = new JSONArray();
            for(int x=0;x<colors.size();x++){
                ColorSpec currentColor = colors.get(x);
                String currentColorName = ColorUtility.nearestColor(currentColor.getHexa())[0];

                JSONObject JSONCurrentColor = new JSONObject();
                    JSONObject JSONCurrentColorCodes = new JSONObject();
                    JSONObject JSONCurrentColorGradients = new JSONObject();

                    JSONCurrentColor.put("name",currentColorName);
                        JSONCurrentColorCodes.put("Hexa",currentColor.getHexa());
                        JSONCurrentColorCodes.put("RGB",currentColor.getRGB()[0]+", "+currentColor.getRGB()[1]+", "+currentColor.getRGB()[2]);
                        JSONCurrentColorCodes.put("HSV",currentColor.getHSV()[0]+", "+currentColor.getHSV()[1]+", "+currentColor.getHSV()[2]);
                        JSONCurrentColorCodes.put("HSL",currentColor.getHSL()[0]+", "+currentColor.getHSL()[1]+", "+currentColor.getHSL()[2]);
                        JSONCurrentColorCodes.put("CMYK",currentColor.getCmyk()[0]+", "+currentColor.getCmyk()[1]+", "+currentColor.getCmyk()[2]+", "+currentColor.getCmyk()[3]);
                        JSONCurrentColorCodes.put("CIELAB",currentColor.getCielab()[0]+", "+currentColor.getCielab()[1]+", "+currentColor.getCielab()[2]);
                    JSONCurrentColor.put("codes",JSONCurrentColorCodes);
                        JSONCurrentColorGradients.put("complementary",currentColor.getComplementary()[0]+", "+currentColor.getComplementary()[1]);
                        JSONCurrentColorGradients.put("triadic",currentColor.getTriadic()[0]+", "+currentColor.getTriadic()[1]+", "+currentColor.getTriadic()[2]);
                        JSONCurrentColorGradients.put("tints",currentColor.getTints()[0]+", "+currentColor.getTints()[1]+", "+currentColor.getTints()[2]+", "+currentColor.getTints()[3]+", "+currentColor.getTints()[4]+", "+currentColor.getTints()[5]);
                        JSONCurrentColorGradients.put("tones",currentColor.getTones()[0]+", "+currentColor.getTones()[1]+", "+currentColor.getTones()[2]+", "+currentColor.getTones()[3]+", "+currentColor.getTones()[4]+", "+currentColor.getTones()[5]);
                        JSONCurrentColorGradients.put("shades",currentColor.getShades()[0]+", "+currentColor.getShades()[1]+", "+currentColor.getShades()[2]+", "+currentColor.getShades()[3]+", "+currentColor.getShades()[4]+", "+currentColor.getShades()[5]);
                        JSONCurrentColorGradients.put("compound",currentColor.getCompound()[0]+", "+currentColor.getShades()[1]+", "+currentColor.getShades()[2]);
                        JSONCurrentColorGradients.put("analogous",currentColor.getAnalogous()[0]+", "+currentColor.getAnalogous()[1]+", "+currentColor.getAnalogous()[2]);
                    JSONCurrentColor.put("gradients",JSONCurrentColorGradients);
                mainJson.put(JSONCurrentColor);
            }
            Log.e("test",mainJson.toString(1));
            return mainJson;
        }catch (JSONException e){
            e.printStackTrace();
        }
        throw new RuntimeException("Can't return null jsonObject");
    }

    private String getFileName(){
        return  new SimpleDateFormat("yyyy-MM-dd___HH_mm_ss_SSS").format(new Date());
    }
}

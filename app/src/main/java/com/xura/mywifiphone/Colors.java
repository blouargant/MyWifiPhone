package com.xura.mywifiphone;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by bertrand on 11/02/16.
 */
public class Colors {
    private ArrayList<Integer> colorList;
    private static Hashtable<Integer, String> materialColorsMap;

    static {
        materialColorsMap = new Hashtable<Integer, String>();
        materialColorsMap.put(0xffD32F2F,"Dark Red");
        materialColorsMap.put(0xffF44336,"Red");
        materialColorsMap.put(0xffC2185B,"Dark Pink");
        materialColorsMap.put(0xffE91E63,"Pink");
        materialColorsMap.put(0xff9C27B0,"Purple");
        materialColorsMap.put(0xff673AB7,"Deep Purple");
        materialColorsMap.put(0xff303F9F,"Dark Indigo");
        materialColorsMap.put(0xff3F51B5,"Indigo");
        materialColorsMap.put(0xff1976D2,"Dark Blue");
        materialColorsMap.put(0xff0288D1,"Dark Light Blue");
        materialColorsMap.put(0xff0097A7,"Dark Cyan");
        materialColorsMap.put(0xff00796B,"Dark Teal");
        materialColorsMap.put(0xff388E3C,"Dark Green");
        materialColorsMap.put(0xff4CAF50,"Green");
        materialColorsMap.put(0xffFFA000,"Dark Amber");
        materialColorsMap.put(0xffF57C00,"Dark Orange");
        materialColorsMap.put(0xffE64A19,"Dark Deep Orange");
        materialColorsMap.put(0xff4CAF50,"Deep Orange");
        materialColorsMap.put(0xffE64A19,"Dark Brown");
        materialColorsMap.put(0xffE64A19,"Dark Blue Grey");
        materialColorsMap.put(0xffFBC02D,"Dark Yellow");
    }

    public Colors() {
        generateRandomColorList();
    }

    public String getColorName(int iColor) {
        return materialColorsMap.get(iColor);
    }

    private void generateRandomColorList() {
        colorList = new ArrayList<>(materialColorsMap.size());
        ArrayList<Integer> tmplist = new ArrayList<>(materialColorsMap.size());
        Enumeration<Integer> keys = materialColorsMap.keys();
        while (keys.hasMoreElements()) {
            tmplist.add(keys.nextElement());
        }
        Random random = new Random();
        while (tmplist.size() != 0) {
            int newindex = random.nextInt(tmplist.size());
            colorList.add(tmplist.get(newindex));
            tmplist.remove(newindex);
        }
        //int aColor = materialColors.get(random.nextInt(materialColors.size()));
    }
    public int getRandomColor() {
        int aColor = colorList.get(0);
        colorList.remove(0);
        if (colorList.size() == 0) {
            generateRandomColorList();
        }
        return aColor;

    }

    public int getMaterialColor(Object key) {
        ArrayList<Integer> tmplist = new ArrayList<>(materialColorsMap.size());
        Enumeration<Integer> keys = materialColorsMap.keys();
        while (keys.hasMoreElements()) {
            tmplist.add(keys.nextElement());
        }
        return tmplist.get(Math.abs(key.hashCode()) % tmplist.size());
    }
}

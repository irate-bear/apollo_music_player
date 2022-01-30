package com.iratebear.apollo.imageutils;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ImageDominantColor {
    public static String getDominantColor(Bitmap image) {
        int height = image.getHeight();
        int width = image.getWidth();

        Map m = new HashMap();
        for(int i=0; i < width ; i+=2)
        {
            for(int j=0; j < height ; j+=2)
            {
                int rgb = image.getPixel(i, j);
                int[] rgbArr = getRGBArr(rgb);
                // Filter out grays....
                if (!isGray(rgbArr)) {
                    Integer counter = (Integer) m.get(rgb);
                    if (counter == null)
                        counter = 0;
                    counter++;
                    m.put(rgb, counter);
                }
            }
        }
        return "#" + getMostCommonColour(m);
    }


    private static String getMostCommonColour(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, (Comparator) (o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue())
                .compareTo(((Map.Entry) (o2)).getValue()));
        Map.Entry me;
        if (list.size() > 0) {
            me = (Map.Entry) list.get(list.size() - 1);
            int[] rgb = getRGBArr((Integer) me.getKey());
            return String.format("%1$2s", Integer.toHexString(rgb[0])).replace(' ',  '0') + String.format("%1$2s", Integer.toHexString(rgb[1])).replace(' ',  '0') + String.format("%1$2s", Integer.toHexString(rgb[2])).replace(' ',  '0') + String.format("%1$2s", Integer.toHexString(rgb[3])).replace(' ',  '0');
        } else
            return Integer.toHexString(126)+Integer.toHexString(126)+Integer.toHexString(126)+Integer.toHexString(126);
    }

    private static int[] getRGBArr(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        return new int[]{alpha,red,green,blue};

    }
    private static boolean isGray(int[] rgbArr) {
        int rgDiff = rgbArr[1] - rgbArr[2];
        int rbDiff = rgbArr[1] - rgbArr[3];
        // Filter out black, white and grays...... (tolerance within 10 pixels)
        int tolerance = 10;
        if (rgDiff > tolerance || rgDiff < -tolerance)
            if (rbDiff > tolerance || rbDiff < -tolerance) {
                return false;
            }
        return true;
    }
}

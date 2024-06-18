package eu.midnightdust.visualoverhaul.util;

public class VOColorUtil {
    public static int convertRgbToArgb(int rgb) {
        int red = 0xFF & (rgb >> 16);
        int green = 0xFF & (rgb >> 8);
        int blue = 0xFF & (rgb);
        int alpha = 200; // Makes water bottles transparent, 255 would be opaque

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}

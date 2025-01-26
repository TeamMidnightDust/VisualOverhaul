package eu.midnightdust.visualoverhaul.util;

import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class VOColorUtil {
    public static int convertRgbToArgb(int rgb, int alpha) {
        int red = 0xFF & (rgb >> 16);
        int green = 0xFF & (rgb >> 8);
        int blue = 0xFF & (rgb);

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    public static int alphaAndBrightness(float alpha, float brightness) {
        return ColorHelper.getArgb(MathHelper.floor(alpha*255), MathHelper.floor(brightness*255), MathHelper.floor(brightness*255), MathHelper.floor(brightness*255));
    }
}

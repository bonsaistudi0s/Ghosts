package dev.xylonity.bonsai.ghosts.util;

import net.minecraft.util.Mth;

/**
 * ARGB parser using bitwise shift operations:
 *
 * https://github.com/Xylonity/Knight-Lib/blob/1.20.1/common/src/main/java/dev/xylonity/knightlib/api/util/KnightLibUtil.java
 */
public class GhostsColor {

    public float alpha;
    public float red;
    public float green;
    public float blue;

    public GhostsColor(float red, float green, float blue, float alpha) {
        this.alpha = alpha;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public static GhostsColor parse(int color) {
        return new GhostsColor(((color >> 16) & 0x00ff) / 255f, ((color >> 8) & 0x0000ff) / 255f, (color & 0x000000ff) / 255f, ((color >> 24) & 0xff) / 255f);
    }

    public int toInt() {
        int alpha = Mth.clamp(Math.round(this.alpha * 255), 0, 255);
        int red = Mth.clamp(Math.round(this.red * 255), 0, 255);
        int green = Mth.clamp(Math.round(this.green * 255), 0, 255);
        int blue = Mth.clamp(Math.round(this.blue * 255), 0, 255);

        return (alpha << 24) + (red << 16) + (green << 8) + blue;
    }

}

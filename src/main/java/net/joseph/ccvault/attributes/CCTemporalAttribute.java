package net.joseph.ccvault.attributes;

import net.minecraft.resources.ResourceLocation;

public class CCTemporalAttribute {
    public String modifier;
    public int count;
    public int time;

    public CCTemporalAttribute(String modifier, int count, int time) {
        this.modifier = modifier;
        this.count = count;
        this.time = time;
    }
}

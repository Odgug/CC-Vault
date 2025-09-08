package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;

public class TieredValueAttribute<T> extends ValueAttribute<T> {
    protected int tier;

    protected TieredValueAttribute(String name, AffixCategorySet categories, T value, int tier) {
        super(name, categories, value);
        this.tier = tier;
    }
}

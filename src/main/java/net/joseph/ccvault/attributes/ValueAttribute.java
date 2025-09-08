package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;

public class ValueAttribute<T> extends CCVaultGearAttribute {
    protected T value;

    public T getValue() {
        return value;
    }

    public ValueAttribute(String name, AffixCategorySet categories, T value) {
        super(name, categories);
        this.value = value;
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        var map = super.toLuaTable();
        map.put("value", this.value);
        return map;
    }

}

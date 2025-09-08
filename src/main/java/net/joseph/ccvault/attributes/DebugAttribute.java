package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;

public class DebugAttribute extends CCVaultGearAttribute {
    Object data;    

    public DebugAttribute(Object data){
        super("debug" , new AffixCategorySet());
        this.data = data;
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map = super.toLuaTable();
        map.put("data", this.data);
        return map;

    }
}

package net.joseph.ccvault.attributes;

import java.util.HashMap;

import com.mojang.datafixers.util.Pair;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.custom.loot.ManaPerLootAttribute;

@SuppressWarnings("unused")
public class CCManaPerLootAttribute extends ValueAttribute<Pair<Integer, Float>>{
    

    public CCManaPerLootAttribute( AffixCategorySet categories,Pair<Integer, Float> value) {
        super("Manabloom", categories, value);
    }

    
    
    public HashMap<String, Object> toLuaTable(Pair<Integer, Float> value) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("mana", value.getFirst());
        map.put("chance", value.getSecond());
        return map;
    }



    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map =  super.toLuaTable();
        map.put("value", this.toLuaTable(value));
        return map;
    }
}

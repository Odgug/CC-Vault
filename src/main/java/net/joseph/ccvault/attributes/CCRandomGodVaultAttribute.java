package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import net.minecraft.resources.ResourceLocation;

public class CCRandomGodVaultAttribute extends ValueAttribute<CCTemporalAttribute> {
    public CCRandomGodVaultAttribute(AffixCategorySet categories, ResourceLocation modifier, int count, int time) {
        super("Temporal Modifier", categories, new CCTemporalAttribute(modifier.toString(), count, time));
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        var map = super.toLuaTable();
        HashMap<String, Object> v = new HashMap<>();
        v.put("effect", this.value.modifier);
        v.put("count", this.value.count);
        v.put("time", this.value.time);
        map.put("value", v);
        return map;
    }
}

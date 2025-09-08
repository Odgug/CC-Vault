package net.joseph.ccvault.attributes;

import java.util.HashMap;

import org.apache.commons.lang3.reflect.FieldUtils;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.custom.effect.EffectGearAttribute;

public class CCEffectAttribute extends RangedValueAttribute<Integer> {
    String effect_name;

    public CCEffectAttribute(String effect_name, AffixCategorySet categories, Integer value, int tier, Integer min,
            Integer max) {
        super("Effect", categories, value, tier, min, max);
        this.effect_name = effect_name;
    }

    public static CCEffectAttribute from(VaultGearModifier<EffectGearAttribute> modifier,
            VaultGearTierConfig.ModifierConfigRange config) {
        var v = (EffectGearAttribute) modifier.getValue();

        Integer min;
        Integer max;
        try {
            min = (Integer) FieldUtils.readField(config.minAvailableConfig(),
                    "amplifier", true);
            max = (Integer) FieldUtils.readField(config.maxAvailableConfig(),
                    "amplifier", true);
            return new CCEffectAttribute(v.getEffect().getDisplayName().getString(),
                    modifier.getCategories(),
                    modifier.getRolledTier() + 1,
                    v.getAmplifier(),
                    min,
                    max);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map = super.toLuaTable();
        map.put("effect", effect_name);
        return map;
    }

}

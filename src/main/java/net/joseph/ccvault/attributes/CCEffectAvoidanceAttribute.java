package net.joseph.ccvault.attributes;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.custom.effect.IEffectAvoidanceChanceAttribute;

public class CCEffectAvoidanceAttribute extends RangedValueAttribute<Float> {
    private List<String> effects;

    public CCEffectAvoidanceAttribute(List<String> effects, AffixCategorySet categories, Float value, int tier,
            Float min,
            Float max) {
        super("Effect Avoidance", categories, value, tier, min, max);
        this.effects = effects;
    }

    public static CCEffectAvoidanceAttribute from(VaultGearModifier<? extends IEffectAvoidanceChanceAttribute> modifier,
            VaultGearTierConfig.ModifierConfigRange config) {
        try {
            Float min = (Float) FieldUtils.readField(config.minAvailableConfig(), "minChance",
                    true);
            Float max = (Float) FieldUtils.readField(config.maxAvailableConfig(), "maxChance",
                    true);
            return new CCEffectAvoidanceAttribute(
                    modifier.getValue().getEffects().stream().map(e -> e.getRegistryName().toString())
                            .collect(Collectors.toList()),
                    modifier.getCategories(),
                    modifier.getValue().getChance(),
                    modifier.getRolledTier(),
                    min,
                    max);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        var map = super.toLuaTable();
        map.put("effects", effects);
        return map;
    }

}

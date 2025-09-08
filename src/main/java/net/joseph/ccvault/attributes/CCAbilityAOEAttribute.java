package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.ability.AbilityAreaOfEffectPercentAttribute;

public class CCAbilityAOEAttribute extends RangedValueAttribute<Float> {
    private String ability;

    private CCAbilityAOEAttribute(AffixCategorySet categories, String ability, int tier, Float value, Float min,
            Float max) {
        super("Ability AOE", categories, value, tier, min, max);
        this.ability = ability;
    }

    public static CCAbilityAOEAttribute from(VaultGearModifier<AbilityAreaOfEffectPercentAttribute> modifier,
            VaultGearTierConfig.ModifierConfigRange config) {
        AbilityAreaOfEffectPercentAttribute value = (AbilityAreaOfEffectPercentAttribute) modifier.getValue();
        var min = (AbilityAreaOfEffectPercentAttribute.Config) config.minAvailableConfig();
        var max = (AbilityAreaOfEffectPercentAttribute.Config) config.maxAvailableConfig();
        return new CCAbilityAOEAttribute(modifier.getCategories(),
                value.getAbilityKey(),
                modifier.getRolledTier() + 1,
                value.getAmount(),
                min.getMin(),
                max.generateMaximumValue());
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        var map = super.toLuaTable();
        map.put("ability", this.ability);
        return map;
    }

}

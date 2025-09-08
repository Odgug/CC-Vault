package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.ability.AbilityCooldownPercentAttribute;

public class CCAbilityCDAttribute extends RangedValueAttribute<Float> {
    private String ability;

    public CCAbilityCDAttribute(AffixCategorySet categories, String ability, int tier, Float value, Float min,
            Float max) {
        super("Ability Cooldown", categories, value, tier, min, max);
        this.ability = ability;
    }

    public static CCAbilityCDAttribute from(VaultGearModifier<AbilityCooldownPercentAttribute> modifier,
            VaultGearTierConfig.ModifierConfigRange config) {
        AbilityCooldownPercentAttribute value = (AbilityCooldownPercentAttribute) modifier.getValue();
        var min = (AbilityCooldownPercentAttribute.Config) config.minAvailableConfig();
        var max = (AbilityCooldownPercentAttribute.Config) config.maxAvailableConfig();
        return new CCAbilityCDAttribute(modifier.getCategories(),
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

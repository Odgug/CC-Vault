package net.joseph.ccvault.attributes;

import java.util.HashMap;

import org.apache.commons.lang3.reflect.FieldUtils;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.ability.AbilityLevelAttribute;

public class CCAbilityLevelAttribute extends RangedValueAttribute<Integer> {
    private String ability;

    public CCAbilityLevelAttribute(String ability, int tier, Integer value, Integer min, Integer max,
            AffixCategorySet categories) {
        super("Ability Level Increase", categories, value, tier, min, max);
        this.ability = ability;
    }

    public static CCAbilityLevelAttribute from(VaultGearModifier<AbilityLevelAttribute> modifier,
            VaultGearTierConfig.ModifierConfigRange config) {

        try {
            Integer min = (Integer) FieldUtils.readField(config.minAvailableConfig(), "levelChange",
                    true);
            Integer max = (Integer) FieldUtils.readField(config.maxAvailableConfig(), "levelChange",
                    true);
            return new CCAbilityLevelAttribute(modifier.getValue().getAbility(),
                    modifier.getRolledTier() + 1,
                    modifier.getValue().getLevelChange(),
                    min,
                    max,
                    modifier.getCategories());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        var map = super.toLuaTable();
        map.put("ability", this.ability);
        return map;
    }

}

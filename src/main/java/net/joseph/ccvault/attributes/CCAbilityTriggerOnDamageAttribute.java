package net.joseph.ccvault.attributes;

import java.util.HashMap;

import com.mojang.datafixers.util.Pair;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.custom.ability.AbilityTriggerOnDamageAttribute;

public class CCAbilityTriggerOnDamageAttribute extends RangedValueAttribute<Pair<Integer, Float>> {
    private String ability;

    private CCAbilityTriggerOnDamageAttribute(AffixCategorySet categories, String ability, Pair<Integer, Float> value,
            int tier, Pair<Integer, Float> min, Pair<Integer, Float> max) {
        super("On Hit Ability Cast", categories, value, tier, min, max);
        this.ability = ability;
    }

    public static CCAbilityTriggerOnDamageAttribute from(VaultGearModifier<AbilityTriggerOnDamageAttribute> modifier,
            VaultGearTierConfig.ModifierConfigRange config) {
        AbilityTriggerOnDamageAttribute value = (AbilityTriggerOnDamageAttribute) modifier.getValue();
        Pair<Integer, Float> current = new Pair<Integer, Float>(value.getLevel(), value.getChance());
        var minCfg = (AbilityTriggerOnDamageAttribute.Config) config.minAvailableConfig();
        var maxCfg = (AbilityTriggerOnDamageAttribute.Config) config.maxAvailableConfig();
        Pair<Integer, Float> min = new Pair<Integer, Float>(minCfg.getLevel().getMin(), minCfg.getChance().getMin());
        Pair<Integer, Float> max = new Pair<Integer, Float>(maxCfg.getLevel().getMax(), maxCfg.getChance().getMax());
        return new CCAbilityTriggerOnDamageAttribute(modifier.getCategories(),
                value.getAbilityId(),
                current,
                modifier.getRolledTier() + 1,
                min,
                max);

    }

    private static HashMap<String, Object> toLuaTable(Pair<Integer, Float> pair) {
        var map = new HashMap<String, Object>();
        map.put("level", pair.getFirst());
        map.put("chance", pair.getSecond());
        return map;
    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map = super.toLuaTable();
        map.put("ability", ability);
        map.put("value", toLuaTable(value));
        HashMap<String, Object> roll = new HashMap<>();
        roll.put("tier", this.tier);
        roll.put("min", toLuaTable(this.min));
        roll.put("max", toLuaTable(this.max));
        map.put("roll", roll);
        return map;
    }
}

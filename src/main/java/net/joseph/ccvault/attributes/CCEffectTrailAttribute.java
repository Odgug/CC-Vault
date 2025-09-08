package net.joseph.ccvault.attributes;

import java.util.HashMap;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.custom.effect.EffectTrialAttribute;

public class CCEffectTrailAttribute extends RangedValueAttribute<Integer> {
    String effect_name;

    private CCEffectTrailAttribute(String effect_name, AffixCategorySet categories, Integer value, int tier, Integer min,
            Integer max) {
        super("Effect Trail", categories, value, tier, min, max);
        this.effect_name = effect_name;
    }

    public static CCEffectTrailAttribute from(VaultGearModifier<EffectTrialAttribute> modifier,
            VaultGearTierConfig.ModifierConfigRange config) {
        var v = modifier.getValue();
        Integer min = ((EffectTrialAttribute.Config) config
                .minAvailableConfig()).getDurationTicks().getMin();
        Integer max = ((EffectTrialAttribute.Config) config
                .maxAvailableConfig()).getDurationTicks().getMax();
        return new CCEffectTrailAttribute(v.getEffectId().toString(), modifier.getCategories(),
                v.getDurationTicks(),
                modifier.getRolledTier() + 1, min, max);

    }

    @Override
    public HashMap<String, Object> toLuaTable() {
        HashMap<String, Object> map = super.toLuaTable();
        map.put("effect", effect_name);
        return map;
    }

}

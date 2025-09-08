package net.joseph.ccvault.attributes;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.custom.effect.EffectCloudAttribute;

public class CCEffectCloudAttribute extends TieredValueAttribute<String> {

    protected CCEffectCloudAttribute(AffixCategorySet categories, String value, int tier) {
        super("Cloud", categories, value, tier);
    }

    public static CCEffectCloudAttribute from(VaultGearModifier<EffectCloudAttribute> modifier,
            VaultGearTierConfig.ModifierConfigRange config) {
        return new CCEffectCloudAttribute(
                modifier.getCategories(),
                modifier.getValue().getPrimaryEffect().getRegistryName().toString(),
                modifier.getRolledTier()+1);
    }
}

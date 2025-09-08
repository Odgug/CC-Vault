package net.joseph.ccvault.attributes;

import java.util.HashMap;

import org.apache.commons.lang3.reflect.FieldUtils;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityGearAttribute;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityGearAttribute.SpecialAbilityTierConfig;
import iskallia.vault.gear.attribute.ability.special.base.template.value.FloatValue;
import iskallia.vault.gear.attribute.ability.special.base.template.value.IntValue;

public class CCSpecialAbilityAttribute<T> extends RangedValueAttribute<T> {
	private String abilityKey;
	private String modificationType;

	private CCSpecialAbilityAttribute(String abilityKey, String modificationKey, int tier, T value, T min, T max,
			AffixCategorySet categories) {
		super("Ability Modification", categories, value, tier, min, max);
		this.abilityKey = abilityKey;
		this.modificationType = modificationKey;
	}

	@SuppressWarnings("rawtypes")
	public static CCSpecialAbilityAttribute<?> from(VaultGearModifier<SpecialAbilityGearAttribute> modifier,
			VaultGearTierConfig.ModifierConfigRange config) {
		String ability = modifier.getValue().getAbilityKey();
		String modification = modifier.getValue().getModification().getKey()
				.toString();
		var v = modifier.getValue().getValue();
		if (v instanceof IntValue) {
			try {
				Integer min = (Integer) FieldUtils.readField(((SpecialAbilityTierConfig) config
						.minAvailableConfig()).getConfig(),
						"min",
						true);
				Integer max = (Integer) FieldUtils.readField(
						((SpecialAbilityTierConfig) config
								.minAvailableConfig()).getConfig(),
						"max",
						true);
				return new CCSpecialAbilityAttribute<Integer>(ability, modification,
						modifier.getRolledTier() + 1,
						((IntValue) v).getValue(),
						min, max, modifier.getCategories());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else if (v instanceof FloatValue) {
			try {
				Float min = (Float) FieldUtils.readField(
						((SpecialAbilityTierConfig) config
								.minAvailableConfig()).getConfig(),
						"min",
						true);
				Float max = (Float) FieldUtils.readField(
						((SpecialAbilityTierConfig) config
								.minAvailableConfig()).getConfig(),
						"max",
						true);
				return new CCSpecialAbilityAttribute<Float>(ability, modification, modifier.getRolledTier() + 1,
						((FloatValue) v).getValue(),
						min, max, modifier.getCategories());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public HashMap<String, Object> toLuaTable() {
		var map = super.toLuaTable();
		map.put("ability", this.abilityKey);
		map.put("modification", this.modificationType);
		return map;
	}

}

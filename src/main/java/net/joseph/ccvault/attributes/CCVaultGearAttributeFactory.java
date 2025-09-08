package net.joseph.ccvault.attributes;

import java.util.HashMap;
import org.apache.commons.lang3.reflect.FieldUtils;
import com.mojang.datafixers.util.Pair;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.config.gear.VaultGearTierConfig.ModifierConfigRange;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.ability.AbilityAreaOfEffectPercentAttribute;
import iskallia.vault.gear.attribute.ability.AbilityCooldownPercentAttribute;
import iskallia.vault.gear.attribute.ability.AbilityLevelAttribute;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityGearAttribute;
import iskallia.vault.gear.attribute.config.IntegerAttributeGenerator;
import iskallia.vault.gear.attribute.custom.RandomGodVaultModifierAttribute;
import iskallia.vault.gear.attribute.custom.ability.AbilityTriggerOnDamageAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectAvoidanceGearAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectAvoidanceListGearAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectCloudAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectGearAttribute;
import iskallia.vault.gear.attribute.custom.effect.EffectTrialAttribute;
import iskallia.vault.gear.attribute.custom.loot.ManaPerLootAttribute;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.gear.VaultCharmItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CCVaultGearAttributeFactory {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static CCVaultGearAttribute parse(ItemStack stack, VaultGearModifier<?> modifier) {
		VaultGearData data = VaultGearData.read(stack);
		String name = modifier.getAttribute().getReader().getModifierName();
		Object value = modifier.getValue();

		// Tier -1 is used to define things that dont roll a tier, like tools, gem
		// sizes, etc
		// why of all places getRolledTier doesnt return a nullable value instead, is
		// out of my non giga brain developer brain
		if (modifier.getRolledTier() == -1) {
			return parseDeterministicModifier(modifier);
		}

		// This is here because vault charm items break that convention for some
		// forsaken reason
		if (stack.getItem() instanceof VaultCharmItem && (modifier.getRolledTier() == 0)) {
			return parseDeterministicModifier(modifier);
		}
		// while we are at it we can also get the tier, since tiers are 0 indexed in
		// code but 1 indexed in the client,
		// we just correct for that so it matches what players can read with their
		// eyeballs
		int tier = modifier.getRolledTier() + 1;

		VaultGearTierConfig.ModifierConfigRange config = getModifierConfigForLevel(stack, modifier,
				data.getItemLevel());
		try {
			if (value instanceof Boolean) {
				return new ValueAttribute<Boolean>(name, modifier.getCategories(), (Boolean) value);
			} else if (value instanceof Integer) {
				return new RangedValueAttribute<Integer>(name,
						modifier.getCategories(),
						(Integer) value,
						tier,
						((IntegerAttributeGenerator.Range) config
								.minAvailableConfig()).min,
						((IntegerAttributeGenerator.Range) config
								.maxAvailableConfig()).max);
			} else if (value instanceof Float) {
				Float min = (Float) FieldUtils.readField(config.minAvailableConfig(), "min",
						true);
				Float max = (Float) FieldUtils.readField(config.maxAvailableConfig(), "max",
						true);
				return new RangedValueAttribute<Float>(name,
						modifier.getCategories(),
						(Float) value,
						tier,
						min,
						max);
			} else if (value instanceof Double) {

				Double min = (Double) FieldUtils.readField(config.minAvailableConfig(), "min",
						true);
				Double max = (Double) FieldUtils.readField(config.maxAvailableConfig(), "max",
						true);
				return new RangedValueAttribute<Double>(name,
						modifier.getCategories(),
						(Double) value,
						tier,
						min,
						max);
			}
		} catch (

		IllegalAccessException e) {
			e.printStackTrace();
		}
		if (value instanceof String) {
			return new ValueAttribute<String>(name, modifier.getCategories(), (String) value);
		} else if (value instanceof ManaPerLootAttribute) {
			return RangedManaPerLootAttribute.from((VaultGearModifier<ManaPerLootAttribute>) modifier, config);
		} else if (value instanceof EffectAvoidanceListGearAttribute) {
			return CCEffectAvoidanceAttribute.from((VaultGearModifier<EffectAvoidanceListGearAttribute>) modifier,
					config);
		} else if (value instanceof EffectAvoidanceGearAttribute) {
			return CCEffectAvoidanceAttribute.from((VaultGearModifier<EffectAvoidanceGearAttribute>) modifier,
					config);
		} else if (value instanceof AbilityLevelAttribute) {
			return CCAbilityLevelAttribute.from((VaultGearModifier<AbilityLevelAttribute>) modifier, config);
		} else if (value instanceof EffectCloudAttribute) {
			return CCEffectCloudAttribute.from((VaultGearModifier<EffectCloudAttribute>) modifier, config);
		} else if (value instanceof SpecialAbilityGearAttribute) {
			return CCSpecialAbilityAttribute.from((VaultGearModifier<SpecialAbilityGearAttribute>) modifier,
					config);
		} else if (value instanceof AbilityTriggerOnDamageAttribute) {
			return CCAbilityTriggerOnDamageAttribute.from(
					(VaultGearModifier<AbilityTriggerOnDamageAttribute>) modifier,
					config);
		} else if (value instanceof AbilityAreaOfEffectPercentAttribute) {
			return CCAbilityAOEAttribute.from((VaultGearModifier<AbilityAreaOfEffectPercentAttribute>) modifier,
					config);
		} else if (value instanceof AbilityCooldownPercentAttribute) {
			return CCAbilityCDAttribute.from((VaultGearModifier<AbilityCooldownPercentAttribute>) modifier, config);
		} else if (value instanceof EffectGearAttribute) {
			return CCEffectAttribute.from((VaultGearModifier<EffectGearAttribute>) modifier, config);
		} else if (value instanceof EffectTrialAttribute) {
			return CCEffectTrailAttribute.from((VaultGearModifier<EffectTrialAttribute>) modifier, config);

		}

		HashMap<String, Object> map = new HashMap<>();
		map.put("modifier", modifier.toString());
		map.put("value", value.toString());
		return new DebugAttribute(map);
	}

	private static VaultGearTierConfig.ModifierConfigRange getModifierConfigForLevel(ItemStack stack,
			VaultGearModifier<?> modifier, int level) {
		VaultGearTierConfig.ModifierConfigRange configRange = (VaultGearTierConfig.ModifierConfigRange) VaultGearTierConfig
				.getConfig(stack).map((tierCfg) -> {
					return tierCfg.getTierConfigRange(modifier, level);
				}).orElse(ModifierConfigRange.empty());
		return configRange;
	}

	public static CCVaultGearAttribute parse(ItemStack stack, VaultGearAttributeInstance<?> instance,
			VaultGearData data) {
		if (instance.getAttribute().equals(ModGearAttributes.CRAFTING_POTENTIAL)) {
			return new ValueAttribute<Integer>("Crafting Potential", new AffixCategorySet(),
					(Integer) instance.getValue());
		} else if (instance.getAttribute().equals(ModGearAttributes.MAX_CRAFTING_POTENTIAL)) {
			return new ValueAttribute<Integer>("Max Crafting Potential", new AffixCategorySet(),
					(Integer) instance.getValue());
		} else if (instance.getAttribute().equals(ModGearAttributes.GEAR_MODEL)) {
			ResourceLocation loc = (ResourceLocation) instance.getValue();
			var model = ModDynamicModels.REGISTRIES.getModelByResourceLocation(loc);
			if (model.isPresent()) {
				return new ValueAttribute<String>("Model", new AffixCategorySet(),
						model.get().getDisplayName());
			}
			return new ValueAttribute<String>("Model", new AffixCategorySet(), null);
		} else if (instance.getAttribute().equals(ModGearAttributes.PREFIXES)) {
			return new ValueAttribute<Integer>("Prefixes", new AffixCategorySet(),
					(Integer) instance.getValue());
		} else if (instance.getAttribute().equals(ModGearAttributes.SUFFIXES)) {
			return new ValueAttribute<Integer>("Suffixes", new AffixCategorySet(),
					(Integer) instance.getValue());
		} else if (instance.getAttribute().equals(ModGearAttributes.IS_LEGENDARY)) {
			return new ValueAttribute<Boolean>("Legendary", new AffixCategorySet(), true);
		} else if (instance.getAttribute().equals(ModGearAttributes.UNIQUE_ITEM_KEY)) {
			return new ValueAttribute<String>("Unique Key", new AffixCategorySet(),
					((ResourceLocation) instance.getValue()).toString());
		} else if (instance.getAttribute().equals(ModGearAttributes.GEAR_NAME)) {
			return new ValueAttribute<String>("Gear Name", new AffixCategorySet(),
					(String) instance.getValue().toString());
		} else if (instance.getAttribute().equals(ModGearAttributes.GEAR_UNIQUE_POOL)) {
			return new ValueAttribute<String>("Unique Pool", new AffixCategorySet(),
					((ResourceLocation) instance.getValue()).toString());
		}
		try {
			// We just try and cast it to see if this instance was cast down from a modifier
			return parse(stack, (VaultGearModifier<?>) instance);

		} catch (ClassCastException e) {
			return new DebugAttribute(instance.toString());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static CCVaultGearAttribute parseDeterministicModifier(VaultGearModifier<?> modifier) {
		var value = modifier.getValue();
		// There are a few modifiers we handle differently, mostly the weird ones like
		// Manabloom
		if (value instanceof ManaPerLootAttribute) {
			// Because this has 2 values
			Pair<Integer, Float> v = new Pair<>(((ManaPerLootAttribute) value).getManaGenerated(),
					((ManaPerLootAttribute) value).getManaGenerationChance());
			return new CCManaPerLootAttribute(modifier.getCategories(), v);
		} else if (value instanceof RandomGodVaultModifierAttribute) {
			RandomGodVaultModifierAttribute temporal = (RandomGodVaultModifierAttribute) value;
			return new CCRandomGodVaultAttribute(modifier.getCategories(), temporal.getModifier(), temporal.getCount(),
					temporal.getTime());
		}
		return new ValueAttribute(modifier.getAttribute().getReader().getModifierName(),
				new AffixCategorySet(),
				value);
	}
}

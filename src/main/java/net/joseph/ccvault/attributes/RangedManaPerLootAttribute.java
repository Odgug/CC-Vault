package net.joseph.ccvault.attributes;

import java.util.HashMap;

import com.mojang.datafixers.util.Pair;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixCategorySet;
import iskallia.vault.gear.attribute.custom.loot.ManaPerLootAttribute;

public class RangedManaPerLootAttribute extends RangedValueAttribute<Pair<Integer, Float>> {

	private RangedManaPerLootAttribute(AffixCategorySet categories, Pair<Integer, Float> value, int tier,
			Pair<Integer, Float> min, Pair<Integer, Float> max) {
		super("Manabloom", categories, value, tier, min, max);
	}

	public static RangedManaPerLootAttribute from(VaultGearModifier<ManaPerLootAttribute> modifier,
			VaultGearTierConfig.ModifierConfigRange config) {
		Pair<Integer, Float> v = new Pair<>(modifier.getValue().getManaGenerated(),
				modifier.getValue().getManaGenerationChance());
		ManaPerLootAttribute.Config min_cfg = ((ManaPerLootAttribute.Config) config
				.minAvailableConfig());
		ManaPerLootAttribute.Config max_cfg = ((ManaPerLootAttribute.Config) config
				.maxAvailableConfig());
		Pair<Integer, Float> min = new Pair<>(min_cfg.getManaGenerated().getMin(),
				min_cfg.getManaGenerationChance().getMin());
		Pair<Integer, Float> max = new Pair<>(max_cfg.getManaGenerated().getMax(),
				min_cfg.getManaGenerationChance().getMax());
		return new RangedManaPerLootAttribute(modifier.getCategories(), v, modifier.getRolledTier() + 1, min, max);
	}

	private HashMap<String, Object> toLuaTable(Pair<Integer, Float> value) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("mana", value.getFirst());
		map.put("chance", value.getSecond());
		return map;
	}

	@Override
	public HashMap<String, Object> toLuaTable() {
		HashMap<String, Object> map = super.toLuaTable();
		map.put("value", toLuaTable(this.value));
		HashMap<String, Object> roll = new HashMap<>();
		roll.put("tier", this.tier);
		roll.put("min", toLuaTable(this.min));
		roll.put("max", toLuaTable(this.max));
		map.put("roll", roll);
		return map;
	}

}

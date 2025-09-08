package net.joseph.ccvault.peripheral.custom;

import static net.joseph.ccvault.peripheral.Methods.assertBetween;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import dan200.computercraft.api.detail.DetailRegistries;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixType;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.InfusedCatalystItem;
import iskallia.vault.item.InscriptionItem;
import iskallia.vault.item.data.InscriptionData;
import iskallia.vault.item.gear.TrinketItem;
import iskallia.vault.item.gear.VaultCharmItem;
import iskallia.vault.item.tool.JewelItem;
import iskallia.vault.item.tool.ToolItem;
import net.joseph.ccvault.attributes.CCVaultGearAttributeFactory;
import net.joseph.ccvault.blockEntity.custom.VaultReaderBlockEntity;
import net.joseph.ccvault.peripheral.TweakedPeripheral;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

public class VaultReaderBlockPeripheral extends TweakedPeripheral<VaultReaderBlockEntity> {
  private final List<IComputerAccess> pcs = new LinkedList<>();
  private IItemHandler inventory;
  private VaultReaderBlockEntity be;

  public VaultReaderBlockPeripheral(VaultReaderBlockEntity blockentity) {
    super("vaultreader", blockentity);
    this.inventory = blockentity.getItemHandler();
    this.be = blockentity;
  }

  private static int moveItem(IItemHandler from, int fromSlot, IItemHandler to, int toSlot, final int limit) {
    // See how much we can get out of this slot
    ItemStack extracted = from.extractItem(fromSlot, limit, true);
    if (extracted.isEmpty())
      return 0;

    // Limit the amount to extract
    int extractCount = Math.min(extracted.getCount(), limit);
    extracted.setCount(extractCount);

    ItemStack remainder = toSlot < 0 ? ItemHandlerHelper.insertItem(to, extracted, false)
        : to.insertItem(toSlot, extracted, false);
    int inserted = remainder.isEmpty() ? extractCount : extractCount - remainder.getCount();
    if (inserted <= 0)
      return 0;

    // Remove the item from the original inventory. Technically this could fail, but
    // there's little we can do
    // about that.
    from.extractItem(fromSlot, inserted, false);
    return inserted;
  }

  private static IItemHandler extractHandler(@Nullable Object object) {
    if (object instanceof BlockEntity blockEntity && blockEntity.isRemoved())
      return null;

    if (object instanceof ICapabilityProvider provider) {
      LazyOptional<IItemHandler> cap = provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
      if (cap.isPresent())
        return cap.orElseThrow(NullPointerException::new);
    }

    if (object instanceof IItemHandler handler)
      return handler;
    if (object instanceof Container container)
      return new InvWrapper(container);
    return null;
  }

  @LuaFunction
  public final int size() {
    return inventory.getSlots();
  }

  @LuaFunction
  public final Map<Integer, Map<String, ?>> list() {
    Map<Integer, Map<String, ?>> result = new HashMap<>();
    int size = inventory.getSlots();
    for (int i = 0; i < size; i++) {
      ItemStack stack = inventory.getStackInSlot(i);
      if (!stack.isEmpty())
        result.put(i + 1, DetailRegistries.ITEM_STACK.getBasicDetails(stack));
    }

    return result;
  }

  @LuaFunction
  public final Map<String, ?> getItemDetail(int slot) throws LuaException {
    assertBetween(slot, 1, inventory.getSlots(), "Slot out of range (%s)");

    ItemStack stack = inventory.getStackInSlot(slot - 1);
    return stack.isEmpty() ? null : DetailRegistries.ITEM_STACK.getDetails(stack);
  }

  @LuaFunction
  public final int getItemLimit(int slot) throws LuaException {
    assertBetween(slot, 1, inventory.getSlots(), "Slot out of range (%s)");
    return inventory.getSlotLimit(slot - 1);
  }

  @LuaFunction
  public final int pushItems(
      IComputerAccess computer,
      String toName, int fromSlot, Optional<Integer> limit, Optional<Integer> toSlot) throws LuaException {
    // Find location to transfer to
    IPeripheral location = computer.getAvailablePeripheral(toName);
    if (location == null)
      throw new LuaException("Target '" + toName + "' does not exist");

    IItemHandler to = extractHandler(location.getTarget());
    if (to == null)
      throw new LuaException("Target '" + toName + "' is not an inventory");

    // Validate slots
    int actualLimit = limit.orElse(Integer.MAX_VALUE);
    assertBetween(fromSlot, 1, inventory.getSlots(), "From slot out of range (%s)");
    if (toSlot.isPresent())
      assertBetween(toSlot.get(), 1, to.getSlots(), "To slot out of range (%s)");

    if (actualLimit <= 0)
      return 0;
    return moveItem(inventory, fromSlot - 1, to, toSlot.orElse(0) - 1, actualLimit);
  }

  @LuaFunction
  public final int pullItems(
      IComputerAccess computer,
      String fromName, int fromSlot, Optional<Integer> limit, Optional<Integer> toSlot) throws LuaException {
    // Find location to transfer to
    IPeripheral location = computer.getAvailablePeripheral(fromName);
    if (location == null)
      throw new LuaException("Source '" + fromName + "' does not exist");

    IItemHandler from = extractHandler(location.getTarget());
    if (from == null)
      throw new LuaException("Source '" + fromName + "' is not an inventory");

    // Validate slots
    int actualLimit = limit.orElse(Integer.MAX_VALUE);
    assertBetween(fromSlot, 1, from.getSlots(), "From slot out of range (%s)");
    if (toSlot.isPresent())
      assertBetween(toSlot.get(), 1, inventory.getSlots(), "To slot out of range (%s)");

    if (actualLimit <= 0)
      return 0;
    return moveItem(from, fromSlot - 1, inventory, toSlot.orElse(0) - 1, actualLimit);
  }

  @LuaFunction
  public final int getItemLevel() {

    return VaultGearData.read(be.getItemStack()).getItemLevel();
  }

  @LuaFunction
  public final String getRarity() {
    return VaultGearData.read(be.getItemStack()).getRarity().toString();
  }

  @LuaFunction
  public final int getRepairSlots() {
    return VaultGearData.read(be.getItemStack()).getRepairSlots();
  }

  @LuaFunction
  public final int getUsedRepairSlots() {
    return VaultGearData.read(be.getItemStack()).getUsedRepairSlots();
  }

  @LuaFunction
  public final List<HashMap<String, Object>> getImplicits() {
    var stack = be.getItemStack();
    VaultGearData data = VaultGearData.read(be.getItemStack());
    return data.getModifiers(AffixType.IMPLICIT).stream()
        .map(modifier -> CCVaultGearAttributeFactory.parse(stack, modifier).toLuaTable()).toList();
  }

  @LuaFunction
  public final List<HashMap<String, Object>> getPrefixes() {
    var stack = be.getItemStack();
    VaultGearData data = VaultGearData.read(be.getItemStack());
    return data.getModifiers(AffixType.PREFIX).stream()
        .map(modifier -> CCVaultGearAttributeFactory.parse(stack, modifier).toLuaTable()).toList();
  }

  @LuaFunction
  public final List<HashMap<String, Object>> getSuffixes() {
    var stack = be.getItemStack();
    VaultGearData data = VaultGearData.read(be.getItemStack());
    return data.getModifiers(AffixType.SUFFIX).stream()
        .map(modifier -> CCVaultGearAttributeFactory.parse(stack, modifier).toLuaTable()).toList();
  }

  @LuaFunction
  public final int getImplicitCount() {
    VaultGearData data = VaultGearData.read(be.getItemStack());
    return data.getModifiers(VaultGearModifier.AffixType.IMPLICIT).size();
  }

  @LuaFunction
  public final int getPrefixCount() {
    VaultGearData data = VaultGearData.read(be.getItemStack());
    return (Integer) data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0);
  }

  @LuaFunction
  public final int getSuffixCount() {
    VaultGearData data = VaultGearData.read(be.getItemStack());
    return (Integer) data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0);
  }

  @LuaFunction
  public HashMap<String, Object> getJewelDetails() throws LuaException {
    ItemStack stack = be.getItemStack();
    if (stack == ItemStack.EMPTY) {
      return null;
    }
    Item item = stack.getItem();
    if (!(item instanceof JewelItem)) {
      throw new LuaException("Item is not a Jewel");
    }
    VaultGearData data = VaultGearData.read(stack);
    HashMap<String, Object> jewel = new HashMap<>();
    jewel.put("name", stack.getDisplayName().getString());
    jewel.put("type", "Jewel");
    jewel.put("level", data.getItemLevel());
    jewel.put("rarity", data.getRarity().getDisplayName().getString());
    List<HashMap<String, Object>> implicits = data.getModifiers(AffixType.IMPLICIT).stream()
        .map(modifier -> CCVaultGearAttributeFactory.parse(stack, modifier).toLuaTable())
        .collect(Collectors.toList());
    jewel.put("implicits", implicits);
    List<HashMap<String, Object>> prefixes = data.getModifiers(AffixType.PREFIX).stream()
        .map(modifier -> CCVaultGearAttributeFactory.parse(stack, modifier).toLuaTable())
        .collect(Collectors.toList());
    jewel.put("prefixes", prefixes);
    List<HashMap<String, Object>> suffixes = data.getModifiers(AffixType.SUFFIX).stream()
        .map(modifier -> CCVaultGearAttributeFactory.parse(stack, modifier).toLuaTable())
        .collect(Collectors.toList());
    jewel.put("suffixes", suffixes);
    return jewel;

  }

  @LuaFunction
  public HashMap<String, Object> getGearDetails() throws LuaException {
    ItemStack stack = be.getItemStack();
    if (stack == ItemStack.EMPTY) {
      return null;
    }
    Item item = stack.getItem();
    if (!(item instanceof VaultGearItem)) {
      throw new LuaException("Item is not Gear");
    }
    VaultGearData data = VaultGearData.read(stack);
    HashMap<String, Object> gear = new HashMap<>();
    gear.put("level", data.getItemLevel());
    gear.put("rarity", data.getRarity().getDisplayName().getString());
    gear.put("name", stack.getDisplayName().getString());
    gear.put("type", "Gear");
    // Return early in case the gear isn't identified as there is no more data to be
    // read
    switch (data.getState()) {
      case UNIDENTIFIED:
      case ROLLING:
        gear.put("identified", false);
        return gear;
      default:
        gear.put("identified", true);

        break;
    }
    switch (VaultGearItem.of(stack).getGearType(stack)) {
      case HELMET:
      case CHESTPLATE:
      case LEGGINGS:
      case BOOTS:
        gear.put("slot", VaultGearItem.of(stack).getEquipmentSlot(stack).toString());
        break;
      case CHARM:
        throw new LuaException("Gear Item is a Charm");
      default:
        break;
    }
    if (data.getRarity() != VaultGearRarity.UNIQUE) {
      gear.put("prefixSlots", data.getFirstValue(ModGearAttributes.PREFIXES).get());
      gear.put("suffixSlots", data.getFirstValue(ModGearAttributes.SUFFIXES).get());
      gear.put("craftingPotential", getCraftingPotential(data));
    }
    gear.put("repairSlots", getRepairslots(data));
    gear.put("durability", getDurability(stack));

    List<HashMap<String, Object>> attributes = new ArrayList<HashMap<String, Object>>();
    data.getAttributes().forEach(instance -> {
      if (instance.getAttribute().equals(ModGearAttributes.CRAFTING_POTENTIAL)) {
        return;
      } else if (instance.getAttribute().equals(ModGearAttributes.MAX_CRAFTING_POTENTIAL)) {
        return;
      } else if (instance.getAttribute().equals(ModGearAttributes.GEAR_MODEL)) {
        return;
      } else if (instance.getAttribute().equals(ModGearAttributes.PREFIXES)) {
        return;
      } else if (instance.getAttribute().equals(ModGearAttributes.SUFFIXES)) {
        return;
      } else if (instance.getAttribute().equals(ModGearAttributes.GEAR_ROLL_TYPE)) {
        return;
      } else {
        attributes.add(CCVaultGearAttributeFactory.parse(stack, instance, data).toLuaTable());
      }
    });
    gear.put("attributes", attributes);
    List<HashMap<String, Object>> implicits = data.getModifiers(AffixType.IMPLICIT).stream()
        .map(modifier -> CCVaultGearAttributeFactory.parse(stack, modifier).toLuaTable())
        .collect(Collectors.toList());
    gear.put("implicits", implicits);
    List<HashMap<String, Object>> prefixes = data.getModifiers(AffixType.PREFIX).stream()
        .map(modifier -> CCVaultGearAttributeFactory.parse(stack, modifier).toLuaTable())
        .collect(Collectors.toList());
    gear.put("prefixes", prefixes);
    List<HashMap<String, Object>> suffixes = data.getModifiers(AffixType.SUFFIX).stream()
        .map(modifier -> CCVaultGearAttributeFactory.parse(stack, modifier).toLuaTable())
        .collect(Collectors.toList());

    gear.put("suffixes", suffixes);
    return gear;

  }

  @LuaFunction
  public HashMap<String, Object> getToolDetails() throws LuaException {
    ItemStack stack = be.getItemStack();
    if (stack == ItemStack.EMPTY) {
      return null;
    }
    Item item = stack.getItem();
    if (!(item instanceof ToolItem)) {
      throw new LuaException("Item is not a Tool");
    }
    VaultGearData data = VaultGearData.read(stack);
    HashMap<String, Object> tool = new HashMap<>();
    tool.put("name", stack.getDisplayName().getString());
    tool.put("type", "Tool");
    tool.put("level", data.getItemLevel());
    tool.put("rarity", data.getRarity().getDisplayName().getString());
    tool.put("repairSlots", getRepairslots(data));
    tool.put("durability", getDurability(stack));
    List<HashMap<String, Object>> prefixes = data.getModifiers(AffixType.PREFIX).stream()
        .map(modifier -> CCVaultGearAttributeFactory.parse(stack, modifier).toLuaTable())
        .collect(Collectors.toList());
    tool.put("prefixes", prefixes);
    List<HashMap<String, Object>> suffixes = data.getModifiers(AffixType.SUFFIX).stream()
        .map(modifier -> CCVaultGearAttributeFactory.parse(stack, modifier).toLuaTable())
        .collect(Collectors.toList());
    tool.put("suffixes", suffixes);
    return tool;

  }

  private HashMap<String, Integer> getDurability(ItemStack stack) {
    HashMap<String, Integer> durability = new HashMap<>();
    int maxDurability = VaultGearItem.of(stack).getMaxDamage(stack);
    durability.put("total", maxDurability);
    int current_durability = maxDurability - VaultGearItem.of(stack).getDamage(stack);
    durability.put("current", current_durability);
    return durability;
  }

  private HashMap<String, Integer> getCraftingPotential(VaultGearData data) {
    HashMap<String, Integer> craft_potential = new HashMap<>();
    int potential = data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL).get();
    int max_potential = data.getFirstValue(ModGearAttributes.MAX_CRAFTING_POTENTIAL).get();
    craft_potential.put("max", max_potential);
    craft_potential.put("current", potential);
    return craft_potential;
  }

  private HashMap<String, Integer> getRepairslots(VaultGearData data) {
    HashMap<String, Integer> repair_slots = new HashMap<>();
    repair_slots.put("total", data.getRepairSlots());
    repair_slots.put("used", data.getUsedRepairSlots());
    return repair_slots;
  }

  @LuaFunction
  public final HashMap<String, Object> getInscriptionDetails() throws LuaException {
    ItemStack stack = be.getItemStack();
    if (stack == ItemStack.EMPTY) {
      return null;
    }
    Item item = stack.getItem();
    if (!(item instanceof InscriptionItem)) {
      throw new LuaException("Item is not an Inscription");
    }
    HashMap<String, Object> map = new HashMap<>();
    InscriptionData data = InscriptionData.from(stack);
    map.put("size", data.getSize());
    map.put("rooms", data.getEntries().stream().map(r -> r.toRoomEntry().getName().getString())
        .collect(Collectors.toList()));
    return map;
  }

  @LuaFunction
  public final HashMap<String, Object> getTrinketDetails() throws LuaException {
    ItemStack stack = be.getItemStack();
    HashMap<String, Object> map = new HashMap<>();
    if (stack == ItemStack.EMPTY) {
      return null;
    }
    Item item = stack.getItem();
    if (!(item instanceof TrinketItem)) {
      throw new LuaException("Item is not a Trinket");
    }
    if (!TrinketItem.isIdentified(stack)) {
      map.put("identified", false);
      return map;
    }
    map.put("identified", true);
    map.put("name", stack.getItem().getName(stack).getString());
    map.put("uses", TrinketItem.getUses(stack));
    map.put("slot", TrinketItem.getSlotIdentifier(stack).get());
    return map;
  }

  @LuaFunction
  public final HashMap<String, Object> getCatalystDetails() throws LuaException {
    ItemStack stack = be.getItemStack();
    if (stack == ItemStack.EMPTY) {
      return null;
    }
    Item item = stack.getItem();
    if (!(item instanceof InfusedCatalystItem)) {
      throw new LuaException("Item is not a Infused Catalyst");
    }
    HashMap<String, Object> map = new HashMap<>();
    map.put("size", InfusedCatalystItem.getSize(stack).get());
    map.put("modifiers",
        InfusedCatalystItem.getModifiers(stack).stream().map(r -> r.toString()).collect(Collectors.toList()));
    return map;
  }

  @LuaFunction
  public final HashMap<String, Object> getCharmDetails() throws LuaException {
    ItemStack stack = be.getItemStack();
    HashMap<String, Object> map = new HashMap<>();

    if (stack == ItemStack.EMPTY) {
      return null;
    }
    Item item = stack.getItem();
    if (!(item instanceof VaultCharmItem)) {
      throw new LuaException("Item is not a Charm");
    }
    VaultGearData data = VaultGearData.read(stack);
    // Return early in case the gear isn't identified as there is no more data to be
    // read
    switch (data.getState()) {
      case UNIDENTIFIED:
      case ROLLING:
        map.put("identified", false);
        return map;
      default:
        map.put("identified", true);
        break;
    }
    map.put("uses", VaultCharmItem.getUses(stack));
    map.put("god", VaultCharmItem.getGod(stack).get().getName());
    map.put("rarity", data.getRarity().getDisplayName().getString());
    List<HashMap<String, Object>> prefixes = data.getModifiers(AffixType.PREFIX).stream()
        .map(modifier -> CCVaultGearAttributeFactory.parse(stack, modifier).toLuaTable())
        .collect(Collectors.toList());
    map.put("prefixes", prefixes);
    return map;
  }

  @LuaFunction
  public final String getItemType() {
    // Returns the type of item in the vault reader slot, returns Unknown if its not
    // a vault item, and nil on an empty slot.
    ItemStack stack = be.getItemStack();
    if (stack == ItemStack.EMPTY) {
      return null;
    }
    Item item = stack.getItem();
    if (item instanceof VaultCharmItem) {
      return "Charm";
    } else if (item instanceof TrinketItem) {
      return "Trinket";
    } else if (item instanceof JewelItem) {
      return "Jewel";
    } else if (item instanceof InscriptionItem) {
      return "Inscription";
    } else if (item instanceof ToolItem) {
      return "Tool";
    } else if (item instanceof VaultGearItem) {
      return "Gear";
    } else if (item instanceof InfusedCatalystItem) {
      return "Catalyst";
    }
    return "Unknown";
  }
}

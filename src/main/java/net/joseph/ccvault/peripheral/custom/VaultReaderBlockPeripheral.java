package net.joseph.ccvault.peripheral.custom;

import com.google.common.collect.Multimap;
import dan200.computercraft.api.detail.DetailRegistries;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.gear.VaultSwordItem;
import net.joseph.ccvault.blockEntity.custom.VaultReaderBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.joseph.ccvault.peripheral.TweakedPeripheral;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.*;

import static net.joseph.ccvault.peripheral.Methods.assertBetween;


public class VaultReaderBlockPeripheral extends TweakedPeripheral<VaultReaderBlockEntity> {
    private final List<IComputerAccess> pcs = new LinkedList<>();
    private IItemHandler inventory;
    private VaultReaderBlockEntity be;
    public VaultReaderBlockPeripheral(VaultReaderBlockEntity blockentity) {
        super("vaultreader", blockentity);
        this.inventory = blockentity.getItemHandler();
        this.be = blockentity;
    }

    private static int moveItem( IItemHandler from, int fromSlot, IItemHandler to, int toSlot, final int limit )
    {
        // See how much we can get out of this slot
        ItemStack extracted = from.extractItem( fromSlot, limit, true );
        if( extracted.isEmpty() ) return 0;

        // Limit the amount to extract
        int extractCount = Math.min( extracted.getCount(), limit );
        extracted.setCount( extractCount );

        ItemStack remainder = toSlot < 0 ? ItemHandlerHelper.insertItem( to, extracted, false ) : to.insertItem( toSlot, extracted, false );
        int inserted = remainder.isEmpty() ? extractCount : extractCount - remainder.getCount();
        if( inserted <= 0 ) return 0;

        // Remove the item from the original inventory. Technically this could fail, but there's little we can do
        // about that.
        from.extractItem( fromSlot, inserted, false );
        return inserted;
    }
    private static IItemHandler extractHandler( @Nullable Object object )
    {
        if( object instanceof BlockEntity blockEntity && blockEntity.isRemoved() ) return null;

        if( object instanceof ICapabilityProvider provider )
        {
            LazyOptional<IItemHandler> cap = provider.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY );
            if( cap.isPresent() ) return cap.orElseThrow( NullPointerException::new );
        }

        if( object instanceof IItemHandler handler ) return handler;
        if( object instanceof Container container ) return new InvWrapper( container );
        return null;
    }
    @LuaFunction
    public final int size() {return inventory.getSlots();}

    @LuaFunction
    public final Map<Integer, Map<String, ?>> list()
    {
        Map<Integer, Map<String, ?>> result = new HashMap<>();
        int size = inventory.getSlots();
        for( int i = 0; i < size; i++ )
        {
            ItemStack stack = inventory.getStackInSlot( i );
            if( !stack.isEmpty() ) result.put( i + 1, DetailRegistries.ITEM_STACK.getBasicDetails( stack ) );
        }

        return result;
    }

    @LuaFunction
    public final Map<String, ?> getItemDetail( int slot ) throws LuaException
    {
        assertBetween( slot, 1, inventory.getSlots(), "Slot out of range (%s)" );

        ItemStack stack = inventory.getStackInSlot( slot - 1 );
        return stack.isEmpty() ? null : DetailRegistries.ITEM_STACK.getDetails( stack );
    }

    @LuaFunction
    public final int getItemLimit( int slot ) throws LuaException
    {
        assertBetween( slot, 1, inventory.getSlots(), "Slot out of range (%s)" );
        return inventory.getSlotLimit( slot - 1 );
    }

    @LuaFunction
    public final int pushItems(
             IComputerAccess computer,
            String toName, int fromSlot, Optional<Integer> limit, Optional<Integer> toSlot
    ) throws LuaException
    {
        // Find location to transfer to
        IPeripheral location = computer.getAvailablePeripheral( toName );
        if( location == null ) throw new LuaException( "Target '" + toName + "' does not exist" );

        IItemHandler to = extractHandler( location.getTarget() );
        if( to == null ) throw new LuaException( "Target '" + toName + "' is not an inventory" );

        // Validate slots
        int actualLimit = limit.orElse( Integer.MAX_VALUE );
        assertBetween( fromSlot, 1, inventory.getSlots(), "From slot out of range (%s)" );
        if( toSlot.isPresent() ) assertBetween( toSlot.get(), 1, to.getSlots(), "To slot out of range (%s)" );

        if( actualLimit <= 0 ) return 0;
        return moveItem( inventory, fromSlot - 1, to, toSlot.orElse( 0 ) - 1, actualLimit );
    }

    @LuaFunction
    public final int pullItems(
            IComputerAccess computer,
            String fromName, int fromSlot, Optional<Integer> limit, Optional<Integer> toSlot
    ) throws LuaException
    {
        // Find location to transfer to
        IPeripheral location = computer.getAvailablePeripheral( fromName );
        if( location == null ) throw new LuaException( "Source '" + fromName + "' does not exist" );

        IItemHandler from = extractHandler( location.getTarget() );
        if( from == null ) throw new LuaException( "Source '" + fromName + "' is not an inventory" );

        // Validate slots
        int actualLimit = limit.orElse( Integer.MAX_VALUE );
        assertBetween( fromSlot, 1, from.getSlots(), "From slot out of range (%s)" );
        if( toSlot.isPresent() ) assertBetween( toSlot.get(), 1, inventory.getSlots(), "To slot out of range (%s)" );

        if( actualLimit <= 0 ) return 0;
        return moveItem( from, fromSlot - 1, inventory, toSlot.orElse( 0 ) - 1, actualLimit );
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
    public final String getImplicit(int index) {
        VaultGearData data =VaultGearData.read(be.getItemStack());
        VaultGearModifier.AffixType type = VaultGearModifier.AffixType.IMPLICIT;
        ItemStack stack = be.getItemStack();
        Boolean displayDetail = true;
        List<VaultGearModifier<?>> affixes = data.getModifiers(type);

        if (index >= affixes.size()) {
            return "null";
        }
        VaultGearModifier affix = affixes.get(index);

        MutableComponent component = (MutableComponent) affix.getDisplay(data,type,stack,displayDetail).get();
        return component.getString();

    }
    @LuaFunction
    public final String getPrefix(int index) {
        VaultGearData data =VaultGearData.read(be.getItemStack());
        VaultGearModifier.AffixType type = VaultGearModifier.AffixType.PREFIX;
        ItemStack stack = be.getItemStack();
        Boolean displayDetail = true;
        List<VaultGearModifier<?>> affixes = data.getModifiers(type);

        if (affixes.size() > index) {
            VaultGearModifier affix = affixes.get(index);

            MutableComponent component = (MutableComponent) affix.getDisplay(data,type,stack,displayDetail).get();
            return component.getString();
        }
        if (this.getPrefixCount() > index) {
            return "empty";
        }
        return "null";

    }
    @LuaFunction
    public final String getSuffix(int index) {
        VaultGearData data =VaultGearData.read(be.getItemStack());
        VaultGearModifier.AffixType type = VaultGearModifier.AffixType.SUFFIX;
        ItemStack stack = be.getItemStack();
        Boolean displayDetail = true;
        List<VaultGearModifier<?>> affixes = data.getModifiers(type);


        if (affixes.size() > index) {
            VaultGearModifier affix = affixes.get(index);

            MutableComponent component = (MutableComponent) affix.getDisplay(data,type,stack,displayDetail).get();
            return component.getString();
        }
        if (this.getSuffixCount() > index) {
            return "empty";
        }
            return "null";
    }

    @LuaFunction
    public final int getImplicitCount() {
        VaultGearData data =VaultGearData.read(be.getItemStack());
        return data.getModifiers(VaultGearModifier.AffixType.IMPLICIT).size();
    }
    @LuaFunction
    public final int getPrefixCount() {
        VaultGearData data =VaultGearData.read(be.getItemStack());
        return (Integer)data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0);
    }

    @LuaFunction
    public final int getSuffixCount() {
        VaultGearData data =VaultGearData.read(be.getItemStack());
        return (Integer)data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0);
    }

    public static boolean isNumber(String num) {
        if (num == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(num);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    @LuaFunction
    public final double getModifierValue(String modifier) {
        boolean flag = false;
        int flagint = 0;
        for (int i = 0; i < modifier.length(); i++) {
            if (isNumber(String.valueOf(modifier.charAt(i)))) {
                flag = true;
                flagint = i;
                i = 100000;
            }
        }
        if (modifier.contains("IV")) {return 4;}
        if (modifier.contains("V")) {return 5;}
        if (modifier.contains("III")) {return 3;}
        if (modifier.contains("II")) {return 2;}

        if (!flag) {
            return 1;
        }
        String tempnum = String.valueOf(modifier.charAt(flagint));
        for (int i = flagint+1; i < modifier.length(); i++) {
            if (isNumber(String.valueOf(modifier.charAt(i))) || String.valueOf(modifier.charAt(i)).equals(".")) {
                tempnum = tempnum + (String.valueOf(modifier.charAt(i)));
            } else {
                i = 100000;
            }
        }
        return Double.parseDouble(tempnum);

    }
    @LuaFunction
    public final double getMaximumRoll(String modifier) {
        if (!modifier.contains("-")) {
            return this.getModifierValue(modifier);
        }
        int baseIndex = modifier.indexOf('-')+1;
        String tempnum = String.valueOf(modifier.charAt(baseIndex));
        for (int i = baseIndex+1; i < modifier.length(); i++) {
            if (isNumber(String.valueOf(modifier.charAt(i))) || String.valueOf(modifier.charAt(i)).equals(".")) {
                tempnum = tempnum + (String.valueOf(modifier.charAt(i)));
            } else {
                i = 100000;
            }
        }
        return Double.parseDouble(tempnum);
    }

    @LuaFunction
    public final double getMinimumRoll(String modifier) {
        if (!modifier.contains("(")) {
            return this.getModifierValue(modifier);
        }
        int baseIndex = modifier.indexOf('(')+1;
        String tempnum = String.valueOf(modifier.charAt(baseIndex));
        for (int i = baseIndex+1; i < modifier.length(); i++) {
            if (isNumber(String.valueOf(modifier.charAt(i))) || String.valueOf(modifier.charAt(i)).equals(".")) {
                tempnum = tempnum + (String.valueOf(modifier.charAt(i)));
            } else {
                i = 100000;
            }
        }
        return Double.parseDouble(tempnum);
    }

    @LuaFunction
    public final String getName(String modifier) {
        String toReturn = "";
        boolean isCloud = (modifier.contains("Cloud"));
        for (int i = 0; i < modifier.length(); i++) {
            if (Character.isAlphabetic(modifier.charAt(i)) && !(isCloud && (modifier.charAt(i)== 'I' || modifier.charAt(i) == 'V'))) {
                toReturn = toReturn + String.valueOf(modifier.charAt(i));
            }
            if (modifier.charAt(i) == '[' || modifier.charAt(i) == '(') {
                i = 10000;
            }
        }
        return toReturn;
    }
    @LuaFunction
    public final String getType(String modifier) {
        char firstchar = modifier.charAt(0);
        if (firstchar == 'e') {
            return "empty";
        }
        if (firstchar == 'n') {
            return "null";
        }
        if (modifier.contains("Crafted")) {
            return "crafted";
        }
        if (Character.isAlphabetic(firstchar) || firstchar == '+') {
            return "regular";
        }
        return "legendary";
    }
}

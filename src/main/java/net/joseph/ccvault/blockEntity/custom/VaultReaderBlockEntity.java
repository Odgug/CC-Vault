package net.joseph.ccvault.blockEntity.custom;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.joseph.ccvault.blockEntity.ModBlockEntities;
import net.joseph.ccvault.blockEntity.PeripheralBlockEntity;
import net.joseph.ccvault.peripheral.custom.VaultReaderBlockPeripheral;
import net.joseph.ccvault.screen.VaultReaderBlockMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class VaultReaderBlockEntity extends BlockEntity implements PeripheralBlockEntity, MenuProvider {
    private VaultReaderBlockPeripheral peripheral;
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public VaultReaderBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.VAULT_READER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    public ItemStack getItemStack() {
        return this.itemHandler.getStackInSlot(1);
    }
    public IItemHandler getItemHandler() {
        return this.itemHandler;
    }
    @Override
    public @Nullable IPeripheral getPeripheral(@NotNull Direction side) {
        if (peripheral == null)
            peripheral = new VaultReaderBlockPeripheral(this);
        return peripheral;
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Vault Reader");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new VaultReaderBlockMenu(pContainerId,pInventory,this);
    }
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }
    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
}

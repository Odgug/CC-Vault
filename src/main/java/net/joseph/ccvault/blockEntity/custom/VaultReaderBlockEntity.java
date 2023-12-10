package net.joseph.ccvault.blockEntity.custom;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.joseph.ccvault.blockEntity.ModBlockEntities;
import net.joseph.ccvault.blockEntity.PeripheralBlockEntity;
import net.joseph.ccvault.peripheral.custom.VaultReaderBlockPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VaultReaderBlockEntity extends BlockEntity implements PeripheralBlockEntity {
    private VaultReaderBlockPeripheral peripheral;

    public VaultReaderBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.VAULT_READER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    @Override
    public @Nullable IPeripheral getPeripheral(@NotNull Direction side) {
        if (peripheral == null)
            peripheral = new VaultReaderBlockPeripheral(this);
        return peripheral;
    }
}

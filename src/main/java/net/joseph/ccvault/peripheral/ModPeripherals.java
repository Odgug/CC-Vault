package net.joseph.ccvault.peripheral;

import net.joseph.ccvault.blockEntity.PeripheralBlockEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class ModPeripherals implements IPeripheralProvider {
    @NotNull
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level world, @NotNull BlockPos pos, @NotNull Direction side) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PeripheralBlockEntity peripheralBlock) {
            IPeripheral peripheral = peripheralBlock.getPeripheral(side);

            if (peripheral != null)
                return LazyOptional.of(() -> peripheral);
        }

        return LazyOptional.empty();
    }
}


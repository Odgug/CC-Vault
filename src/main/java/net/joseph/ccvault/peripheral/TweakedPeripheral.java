package net.joseph.ccvault.peripheral;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * I did not steal the name from Create. I swear!
 * No seriously!
 */
public abstract class TweakedPeripheral<BE extends BlockEntity> implements IPeripheral {
    private final String type;
    private final BE blockEntity;
    private final List<IComputerAccess> computers = new LinkedList<>();

    public TweakedPeripheral(String type, @Nullable BE blockEntity) {
        this.type = type;
        this.blockEntity = blockEntity;
    }

    public void sendEvent(@Nonnull String event, @Nullable Object... arguments) {
        for (IComputerAccess pc : computers)
            pc.queueEvent(event, pc.getAttachmentName(), arguments);
    }

    @Override
    public void attach(@Nonnull IComputerAccess computer) {
        computers.add(computer);
    }

    @Override
    public void detach(@Nonnull IComputerAccess computer) {
        computers.removeIf(p -> (p.getID() == computer.getID()));
    }

    @NotNull
    @Override
    public String getType() {
        return type;
    }

    @Nullable
    @Override
    public BE getTarget() {
        return blockEntity;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this && other.getType().equals(type) && other.getTarget() == this.getTarget();
    }
}
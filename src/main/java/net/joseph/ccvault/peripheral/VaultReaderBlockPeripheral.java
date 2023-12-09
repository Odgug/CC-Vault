package net.joseph.ccvault.peripheral;

import net.joseph.ccvault.blockEntity.VaultReaderBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.core.computer.ComputerSide;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class VaultReaderBlockPeripheral extends TweakedPeripheral<VaultReaderBlockEntity>{
    private final List<IComputerAccess> pcs = new LinkedList<>();
    public VaultReaderBlockPeripheral(VaultReaderBlockEntity blockentity) {
        super("vaultreader", blockentity);
    }



    @LuaFunction
    public final boolean test() {
        return true;
    }
}

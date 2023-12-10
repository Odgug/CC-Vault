package net.joseph.ccvault.peripheral.custom;

import net.joseph.ccvault.blockEntity.custom.VaultReaderBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.joseph.ccvault.peripheral.TweakedPeripheral;

import java.util.LinkedList;
import java.util.List;

public class VaultReaderBlockPeripheral extends TweakedPeripheral<VaultReaderBlockEntity> {
    private final List<IComputerAccess> pcs = new LinkedList<>();
    public VaultReaderBlockPeripheral(VaultReaderBlockEntity blockentity) {
        super("vaultreader", blockentity);
    }



    @LuaFunction
    public final boolean test() {
        return true;
    }
}

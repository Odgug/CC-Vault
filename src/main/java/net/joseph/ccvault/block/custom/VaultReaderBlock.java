package net.joseph.ccvault.block.custom;

import net.joseph.ccvault.blockEntity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.joseph.ccvault.blockEntity.VaultReaderBlockEntity;
import org.jetbrains.annotations.Nullable;

public class VaultReaderBlock extends Block implements EntityBlock{
    public VaultReaderBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new VaultReaderBlockEntity(pPos, pState);
    }

}

package net.joseph.ccvault.blockEntity;

import com.mojang.datafixers.types.Type;
import net.joseph.ccvault.CCVault;
import net.joseph.ccvault.block.custom.VaultReaderBlock;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.joseph.ccvault.block.ModBlocks;

public class ModBlockEntities {
    private static Type<?> getType(String key) {
        return Util.fetchChoiceType(References.BLOCK_ENTITY, key);
    }
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CCVault.MOD_ID);
    public static final RegistryObject<BlockEntityType<VaultReaderBlockEntity>> VAULT_READER_BLOCK_ENTITY = BLOCK_ENTITIES.register("vault_reader_block_entity", () -> BlockEntityType.Builder.of(VaultReaderBlockEntity::new, ModBlocks.VAULT_READER_BLOCK.get()).build(getType("vault_reader_block")));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

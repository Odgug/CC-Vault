package net.joseph.ccvault;

import com.mojang.logging.LogUtils;
import net.joseph.ccvault.block.ModBlocks;
import net.joseph.ccvault.blockEntity.ModBlockEntities;
import net.joseph.ccvault.item.ModItems;
import net.joseph.ccvault.peripheral.ModPeripherals;
import net.joseph.ccvault.screen.ModMenuTypes;
import net.joseph.ccvault.screen.VaultReaderBlockScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import dan200.computercraft.api.*;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CCVault.MOD_ID)


public class CCVault
{
    // Directly reference a slf4j logger
    public static final String MOD_ID = "ccvault";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CCVault()
    {

        // Register the setup method for modloading
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModMenuTypes.register(eventBus);

        // Register ourselves for server and other game events we are interested in
        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);

        ComputerCraftAPI.registerPeripheralProvider(new ModPeripherals());
    }
    private void clientSetup(final FMLClientSetupEvent event) {
        MenuScreens.register(ModMenuTypes.VAULT_READER_BLOCK_MENU.get(), VaultReaderBlockScreen::new);
        }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code

        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}

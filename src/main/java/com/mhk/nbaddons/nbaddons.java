package com.mhk.nbaddons;

import com.mhk.nbaddons.config.ModConfiguration;
import com.mhk.nbaddons.setup.Registration;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("nbaddons")
public class nbaddons {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "nbaddons";
    private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

    public nbaddons() {
        Registration.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ModConfiguration.server_config);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ModConfiguration.client_config);

        ModConfiguration.loadConfig(ModConfiguration.client_config, FMLPaths.CONFIGDIR.get().resolve("nbaddons-client.toml").toString());
        ModConfiguration.loadConfig(ModConfiguration.server_config, FMLPaths.CONFIGDIR.get().resolve("nbaddons-server.toml").toString());

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }

}
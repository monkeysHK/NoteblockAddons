package com.mhk.nbaddons.events;

import com.mhk.nbaddons.config.NoteBlockCommand;
import com.mhk.nbaddons.nbaddons;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = nbaddons.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModServerEvent {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        NoteBlockCommand.register(event.getDispatcher());
    }

}

package com.mhk.nbaddons.config;

import com.mhk.nbaddons.gui.ConfigInterface;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class NoteBlockCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("noteblock")
                .requires(source -> source.hasPermission(0))
                .executes(command -> NoteBlockCommand.enterGui(command.getSource())));
    }

    private static int enterGui(CommandSource source) {
        ConfigInterface.openGUI();
        return 1;
    }
}

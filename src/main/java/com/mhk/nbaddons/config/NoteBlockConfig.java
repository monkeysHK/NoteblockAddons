package com.mhk.nbaddons.config;

import com.mhk.nbaddons.events.ModClientEvents;
import net.minecraftforge.common.ForgeConfigSpec;

public class NoteBlockConfig {

    public static ForgeConfigSpec.BooleanValue allowScroll;
    public static ForgeConfigSpec.BooleanValue coloredKeys;
    public static ForgeConfigSpec.BooleanValue remapReplay;
    public static ForgeConfigSpec.IntValue targetRange;

    public static void init(ForgeConfigSpec.Builder server, ForgeConfigSpec.Builder client) {
        server.comment("Note Block Configuration");

        allowScroll = server.comment("Decide if scrolling while pointing to note block will change the note outside GUI")
                .define("noteblock.allow_scroll", true);

        coloredKeys = server.comment("Decide if the labels on White Keys are coloured based on their octaves")
                .define("noteblock.colored_keys", false);

        remapReplay = server.comment("Decide if replay button is to be swapped with the double note input function")
                .define("noteblock.remap_replay", false);

        targetRange = server.comment("The target range: a default range on the keyboard for note input")
                .defineInRange("noteblock.target_range", 0, 0, 1);
    }

    public static boolean getAllowScroll() {
        return allowScroll.get();
    }

    public static boolean getColoredKeys() {
        return coloredKeys.get();
    }

    public static boolean getRemapReplay() {
        return remapReplay.get();
    }

    public static int getTargetRange() {
        return targetRange.get();
    }

    public static void setScroll() {
        allowScroll.set(!allowScroll.get());
    }

    public static void setColoredKeys() {
        coloredKeys.set(!coloredKeys.get());
    }

    public static void remapReplay() {
        remapReplay.set(!remapReplay.get());
    }

    public static void setTargetRange(int range) {
        targetRange.set(range);
    }
}

package com.mhk.nbaddons.gui;

import net.minecraft.util.text.TextFormatting;

public enum InstrumentGroup {
    RANGE1("F♯1–F♯3"),
    RANGE2("F♯2–F♯4"),
    RANGE3("F♯3–F♯5"),
    RANGE4("F♯4–F♯6"),
    RANGE5("F♯5–F♯7"),
    WITHOUT("—");

    // NoteBlockInterface::WHITEKEYS = {26,1,3,5,6,8,10,11,13,15,17,18,20,22,23,25};
    private final String[] keys = {"F","G","A","B","C","D","E","F","G","A","B","C","D","E","F","G"};
    private final String[] coloredkeys = new String[16];

    private final String range;

    InstrumentGroup(String range) {
        int octave;
        for (int i = 0; i < keys.length; i++) {
            if (i <= 3) {
                // Lowest Octave
                octave = this.ordinal() + 1;
            }
            else if (i <= 10) {
                // Middle Octave
                octave = this.ordinal() + 2;
            }
            else {
                // Highest Octave
                octave = this.ordinal() + 3;
            }
            coloredkeys[i] = getColor(octave) + keys[i] + (this.ordinal() == 5? "": octave);
            keys[i] = TextFormatting.WHITE + keys[i] + (this.ordinal() == 5? "": octave);
        }
        this.range = range;
    }

    private String getColor(int oct) {
        switch (oct) {
            case 1: return TextFormatting.RED.toString();
            case 2: return TextFormatting.GOLD.toString();
            case 3: return TextFormatting.DARK_GREEN.toString();
            case 4: return TextFormatting.BLUE.toString();
            case 5: return TextFormatting.LIGHT_PURPLE.toString();
            case 6: return TextFormatting.YELLOW.toString();
            case 7: return TextFormatting.GRAY.toString();
            case 8: return TextFormatting.DARK_GRAY.toString();
            default: return TextFormatting.WHITE.toString();
        }
    }

    public String getRange() {
        return range;
    }

    public String[] getColoredkeys() {
        return coloredkeys;
    }

    public String[] getKeys() {
        return keys;
    }
}

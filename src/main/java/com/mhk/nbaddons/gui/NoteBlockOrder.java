package com.mhk.nbaddons.gui;

import com.mhk.nbaddons.nbaddons;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public enum NoteBlockOrder {
    BASS(NoteBlockInstrument.BASS, InstrumentGroup.RANGE1, Blocks.DARK_OAK_LOG),
    DIDGERIDOO(NoteBlockInstrument.DIDGERIDOO, InstrumentGroup.RANGE1, Blocks.PUMPKIN),
    GUITAR(NoteBlockInstrument.GUITAR, InstrumentGroup.RANGE2, Blocks.LIGHT_GRAY_WOOL),
    BANJO(NoteBlockInstrument.BANJO, InstrumentGroup.RANGE3, Blocks.HAY_BLOCK),
    BIT(NoteBlockInstrument.BIT, InstrumentGroup.RANGE3, Blocks.EMERALD_BLOCK),
    HARP(NoteBlockInstrument.HARP, InstrumentGroup.RANGE3, Blocks.DIAMOND_BLOCK),
    IRON_XYLOPHONE(NoteBlockInstrument.IRON_XYLOPHONE, InstrumentGroup.RANGE3, Blocks.IRON_BLOCK),
    PLING(NoteBlockInstrument.PLING, InstrumentGroup.RANGE3, Blocks.GLOWSTONE),
    COW_BELL(NoteBlockInstrument.COW_BELL, InstrumentGroup.RANGE4, Blocks.SOUL_SAND),
    FLUTE(NoteBlockInstrument.FLUTE, InstrumentGroup.RANGE4, Blocks.CLAY),
    BELL(NoteBlockInstrument.BELL, InstrumentGroup.RANGE5, Blocks.GOLD_BLOCK),
    CHIME(NoteBlockInstrument.CHIME, InstrumentGroup.RANGE5, Blocks.PACKED_ICE),
    XYLOPHONE(NoteBlockInstrument.XYLOPHONE, InstrumentGroup.RANGE5, Blocks.BONE_BLOCK),
    BASEDRUM(NoteBlockInstrument.BASEDRUM, InstrumentGroup.WITHOUT, Blocks.POLISHED_ANDESITE),
    HAT(NoteBlockInstrument.HAT, InstrumentGroup.WITHOUT, Blocks.LIGHT_BLUE_STAINED_GLASS),
    SNARE(NoteBlockInstrument.SNARE, InstrumentGroup.WITHOUT, Blocks.BROWN_CONCRETE_POWDER);

    public static final int length = NoteBlockOrder.values().length;

    private final String name;
    private final SoundEvent sound;
    private final InstrumentGroup group;
    private final Block blockUnder;
    private final NoteBlockInstrument instru;
    private final StringTextComponent stc;
    private final ResourceLocation res;


    NoteBlockOrder(NoteBlockInstrument instrument, InstrumentGroup group, Block block) {
        this.name = instrument.getString();
        this.sound = instrument.getSound();
        this.group = group;
        this.blockUnder = block;
        this.instru = instrument;
        this.stc = new StringTextComponent(getName());
        this.res = new ResourceLocation(nbaddons.MOD_ID, "/textures/instruments/" +this.name+".png");
    }

    public String getName() {
        return name;
    }

    public SoundEvent getSound() {
        return sound;
    }

    public InstrumentGroup getGroup() {
        return group;
    }

    public Block getBlockUnder() {
        return blockUnder;
    }

    public StringTextComponent getStc() {
        return stc;
    }

    public ResourceLocation getRes() {
        return res;
    }

    public NoteBlockOrder getNext(int i) {
        // i can also be negative to get previous
        int delta = Math.abs(i) % length;
        int ret = (this.ordinal() + (i > 0? 1: -1) * delta + length) % length;
        return NoteBlockOrder.values()[ret];
    }

    public NoteBlockOrder jumpNext(boolean direction) {
        int aim = (this.group.ordinal() + (direction? 1: -1) + InstrumentGroup.values().length) % InstrumentGroup.values().length;
        Optional<NoteBlockOrder> op;
        if (direction) {
            op = Arrays.stream(NoteBlockOrder.values()).filter(x -> x.getGroup().ordinal() == aim).findFirst();
        }
        else {
            List<NoteBlockOrder> ls = Arrays.asList(NoteBlockOrder.values());
            Collections.reverse(Arrays.asList(NoteBlockOrder.values()));
            op = ls.stream().filter(x -> x.getGroup().ordinal() == aim).findFirst();
        }
        return op.orElse(this);
    }

    public NoteBlockInstrument getInstru() {
        return instru;
    }

    public static NoteBlockOrder byInstrument(NoteBlockInstrument instrument) {
        for (NoteBlockOrder noteBlockOrder : NoteBlockOrder.values()) {
            if (noteBlockOrder.instru == instrument) {
                return noteBlockOrder;
            }
        }
        return null;
    }

    public static NoteBlockOrder byState(BlockState bs) {
        return byInstrument(NoteBlockInstrument.byState(bs));
    }

}
package com.mhk.nbaddons.blocks;

import com.mhk.nbaddons.setup.Registration;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.block.NoteBlock.INSTRUMENT;
import static net.minecraft.block.NoteBlock.NOTE;

public class NoteBlockTileEntity extends TileEntity {

    public static final RegistryObject<TileEntityType<NoteBlockTileEntity>> NOTEBLOCK_TE = Registration.NOTEBLOCK_TE;

    private int note;
    private int instrument;

    public NoteBlockTileEntity() {
        super(NOTEBLOCK_TE.get());
    }

    public void readFromBlock(BlockState state) {
        this.note = state.get(NOTE);
        this.instrument = state.get(INSTRUMENT).ordinal();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putInt("note", note);
        compound.putInt("instrument", instrument);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.note = nbt.getInt("note");
        this.instrument = nbt.getInt("instrument");
    }

    @Override
    public CompoundNBT getUpdateTag() {
        readFromBlock(getBlockState());
        return write(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, -1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getNbtCompound();
        // Handle data
        this.read(this.getBlockState(), pkt.getNbtCompound());
    }

    public int getNote() {
        return note;
    }

    public int getInstrument() {
        return instrument;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public void setInstrument(int instrument) {
        this.instrument = instrument;
    }
}

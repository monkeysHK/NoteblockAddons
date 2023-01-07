package com.mhk.nbaddons.blocks;

import com.mhk.nbaddons.setup.Registration;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.block.NoteBlock.INSTRUMENT;
import static net.minecraft.block.NoteBlock.NOTE;

public class NoteBlockTileEntity extends TileEntity {

    public static final RegistryObject<TileEntityType<NoteBlockTileEntity>> NOTEBLOCK_TE = Registration.NOTEBLOCK_TE;

    private int note;
    private int instrument;

    public NoteBlockTileEntity() {
        super(NOTEBLOCK_TE.get());
    }

    public void loadFromBlockState(BlockState state) {
        this.note = state.getValue(NOTE);
        this.instrument = state.getValue(INSTRUMENT).ordinal();
    }

    @Override
    @NotNull
    public CompoundNBT save(@NotNull CompoundNBT compound) {
        super.save(compound);
        compound.putInt("note", note);
        compound.putInt("instrument", instrument);
        return compound;
    }

    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundNBT nbt) {
        super.load(state, nbt);
        this.note = nbt.getInt("note");
        this.instrument = nbt.getInt("instrument");
    }

    @Override
    @NotNull
    public CompoundNBT getUpdateTag() {
        loadFromBlockState(getBlockState());
        return save(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getBlockPos(), -1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getTag();
        // Handle data
        this.load(this.getBlockState(), pkt.getTag());
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

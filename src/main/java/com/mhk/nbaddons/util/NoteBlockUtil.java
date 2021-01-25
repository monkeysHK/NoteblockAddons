package com.mhk.nbaddons.util;


import com.mhk.nbaddons.blocks.NoteBlockTileEntity;
import com.mhk.nbaddons.gui.NoteBlockOrder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import static net.minecraft.block.NoteBlock.INSTRUMENT;
import static net.minecraft.block.NoteBlock.NOTE;

public class NoteBlockUtil {

    public static int transposeNote(int note, int delta) {
        int abs_i = Math.abs(delta);
        if (delta > 0) delta %= 25;
        else delta = - (abs_i % 25);

        int old_ = note;
        int new_ = old_ + delta;

        if (new_ >= 0) new_ %= 25;
        else new_ += 25;

        return new_;
    }

    public static int octaveChange(int note, boolean direction) {
        // direction == true for raise octave, false otherwise
        int new_;

        if (direction) {
            // raise by an octave
            if (note == 24) new_ = 0;
            else if (note > 12) new_ = note - 12;
            else new_ = note + 12;
        }
        else {
            // lower by an octave
            if (note == 0) new_ = 24;
            else if (note < 12) new_ = note + 12;
            else new_ = note - 12;
        }

        return new_;
    }

    public static BlockState tuneNoteBlock(World worldIn, BlockPos nbPos, PlayerEntity player, int note){
        BlockState blockState = worldIn.getBlockState(nbPos);
        BlockState newBlockState;
        if (!worldIn.isRemote()) {
            worldIn.setBlockState(nbPos, blockState.with(NOTE, note));
            newBlockState = blockState.with(NOTE, note);
            worldIn.addBlockEvent(nbPos, newBlockState.getBlock(), 0, 0); // play sound

            player.addStat(Stats.TUNE_NOTEBLOCK);

            handleChange(worldIn, nbPos, blockState, newBlockState);
            return newBlockState;
        }
        return blockState;
    }

    public static BlockState tuneNoteBlock(World worldIn, BlockPos nbPos, PlayerEntity player, NoteBlockOrder nbo){
        BlockState blockState = worldIn.getBlockState(nbPos);
        BlockState newBlockState;
        if (!worldIn.isRemote()) {
            worldIn.setBlockState(nbPos.down(), nbo.getBlockUnder().getDefaultState());
            newBlockState = blockState.with(INSTRUMENT, nbo.getInstru());

            handleChange(worldIn, nbPos, blockState, newBlockState);
            return newBlockState;
        }
        return blockState;
    }

    public static void handleChange(World worldIn, BlockPos pos, BlockState oldState, BlockState newState) {
        TileEntity te;
        te = worldIn.getChunkAt(pos).getTileEntity(pos, Chunk.CreateEntityType.IMMEDIATE);
        if (te != null && te instanceof NoteBlockTileEntity) {
            ((NoteBlockTileEntity) te).readFromBlock(newState);
        }
        worldIn.notifyBlockUpdate(pos, oldState, newState, 2);
    }
}

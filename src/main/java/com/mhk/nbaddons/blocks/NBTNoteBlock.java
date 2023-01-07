package com.mhk.nbaddons.blocks;

import com.mhk.nbaddons.gui.NoteBlockInterface;
import com.mhk.nbaddons.gui.NoteBlockOrder;
import com.mhk.nbaddons.setup.Registration;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;

import static com.mhk.nbaddons.util.NoteBlockUtil.handleChange;
import static com.mhk.nbaddons.util.NoteBlockUtil.tuneNoteBlock;

public class NBTNoteBlock extends NoteBlock {

    public static final RegistryObject<Block> NOTEBLOCK = Registration.NOTEBLOCK;

    public NBTNoteBlock(Properties properties) {
        super(properties);
    }
    NoteBlockInterface gui;

    @Override
    public BlockState getStateForPlacement(@NotNull BlockItemUseContext context) {
        BlockState bs = super.getStateForPlacement(context);
        ItemStack stack = context.getItemInHand();
        CompoundNBT cmp = stack.getTagElement("BlockEntityTag");
        if (!context.getLevel().isClientSide() && (cmp != null) && (bs != null)) {
            return bs.setValue(NOTE, cmp.getInt("note")).setValue(
                    INSTRUMENT, NoteBlockInstrument.values()[cmp.getInt("instrument")]);
        }
        return bs;
    }

    @Override
    public void setPlacedBy(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        CompoundNBT cmp = stack.getTagElement("BlockEntityTag");
        if (!worldIn.isClientSide() && (cmp != null)) {
            NoteBlockInstrument current = NoteBlockInstrument.byState(worldIn.getBlockState(pos.below()));
            NoteBlockInstrument result = NoteBlockInstrument.values()[cmp.getInt("instrument")];
            if (!current.equals(result)) {
                tuneNoteBlock(worldIn, pos, (PlayerEntity) placer, NoteBlockOrder.byInstrument(result));
            }
        }
    }

    @Override
    @NotNull
    public ActionResultType use(@NotNull BlockState state, World worldIn, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit) {
        // Note: each time this is invoked, there are two calls: first one from client side, second one from non-client side.
        // We need to open the GUI on the client world, then supply the non-client world to it so that it can update the world.
        if (!worldIn.isClientSide()) {
            if (this.gui != null) {
                this.gui.attachNonClientWorld(worldIn);
            }
            return ActionResultType.SUCCESS;
        }
        this.gui = NoteBlockInterface.openGUI(pos, player);
        return ActionResultType.CONSUME;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new NoteBlockTileEntity();
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        handleChange((World) world, pos, NOTEBLOCK.get().defaultBlockState(), state);
        return super.getPickBlock(state, target, world, pos, player);
    }
}

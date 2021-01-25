package com.mhk.nbaddons.blocks;

import com.mhk.nbaddons.gui.NoteBlockInterface;
import com.mhk.nbaddons.gui.NoteBlockOrder;
import com.mhk.nbaddons.setup.Registration;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.client.Minecraft;
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

import static com.mhk.nbaddons.util.NoteBlockUtil.handleChange;
import static com.mhk.nbaddons.util.NoteBlockUtil.tuneNoteBlock;

public class NBTNoteBlock extends NoteBlock {

    //public static Block NOTEBLOCK;
    public static final RegistryObject<Block> NOTEBLOCK = Registration.NOTEBLOCK;

    public NBTNoteBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState bs = super.getStateForPlacement(context);
        ItemStack stack = context.getItem();
        CompoundNBT cmp = stack.getChildTag("BlockEntityTag");
        if (!context.getWorld().isRemote() && (cmp != null) && (bs != null)) {
            return bs.with(NOTE, cmp.getInt("note")).with(
                    INSTRUMENT, NoteBlockInstrument.values()[cmp.getInt("instrument")]);
        }
        return bs;
    }



    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        CompoundNBT cmp = stack.getChildTag("BlockEntityTag");
        if (!worldIn.isRemote() && (cmp != null)) {
            NoteBlockInstrument current = NoteBlockInstrument.byState(worldIn.getBlockState(pos.down()));
            NoteBlockInstrument result = NoteBlockInstrument.values()[cmp. getInt("instrument")];
            if (!current.equals(result)) {
                tuneNoteBlock(worldIn, pos, (PlayerEntity) placer, NoteBlockOrder.byInstrument(result));
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            Minecraft.getInstance().displayGuiScreen(new NoteBlockInterface(pos, worldIn, player));
            return ActionResultType.CONSUME;
        }
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
        handleChange((World) world, pos, NOTEBLOCK.get().getDefaultState(), state);
        return super.getPickBlock(state, target, world, pos, player);
    }
}

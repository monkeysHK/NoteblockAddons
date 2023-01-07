package com.mhk.nbaddons.events;

import com.mhk.nbaddons.blocks.NBTNoteBlock;
import com.mhk.nbaddons.blocks.NoteBlockTileEntity;
import com.mhk.nbaddons.config.NoteBlockConfig;
import com.mhk.nbaddons.nbaddons;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import static com.mhk.nbaddons.util.NoteBlockUtil.*;
import static net.minecraft.block.NoteBlock.NOTE;

@Mod.EventBusSubscriber(modid = nbaddons.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModClientEvents {

    private static World THISWORLD;
    private static World CLIENTWORLD;
    private static World NONCLIENTWORLD;
    private static Minecraft MC;

    @SubscribeEvent
    public static void onLoadWorld(WorldEvent event) {
        THISWORLD = (World) event.getWorld();
        if (THISWORLD.isClientSide())
            CLIENTWORLD = THISWORLD;
        else
            NONCLIENTWORLD = THISWORLD;
        MC = Minecraft.getInstance();
    }

    @SubscribeEvent
    public static void mouseScrollHandler(InputEvent.MouseScrollEvent event) {
        World worldUsing = NONCLIENTWORLD == null ? THISWORLD : NONCLIENTWORLD;

        if (!Screen.hasShiftDown() && NoteBlockConfig.allowScroll.get() && MC != null && worldUsing != null && !worldUsing.isClientSide()) {
            if (MC.hitResult != null && MC.player != null) {
                if (MC.hitResult.getType() == RayTraceResult.Type.BLOCK) {
                    BlockRayTraceResult target_ = (BlockRayTraceResult) MC.hitResult;
                    BlockPos blockPos = target_.getBlockPos();
                    BlockState blockState = worldUsing.getBlockState(blockPos);

                    if (blockState.getBlock() instanceof NBTNoteBlock) {
                        int note = blockState.getValue(NOTE);
                        if (Screen.hasControlDown()) {
                            // raise/lower by octave
                            if (event.getScrollDelta() > 0) note = octaveChange(note,true); // scrolling up + ctrl
                            if (event.getScrollDelta() < 0) note = octaveChange(note,false); // scrolling down + ctrl
                        }
                        else {
                            // raise/lower by note
                            if (event.getScrollDelta() > 0) note = transposeNote(note,1); // scrolling up
                            if (event.getScrollDelta() < 0) note = transposeNote(note,-1); // scrolling down
                        }
                        tuneNoteBlock(worldUsing, blockPos, MC.player, note);
                        if (event.isCancelable()) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void keyPressHandler(InputEvent.KeyInputEvent event) {
        if (event.getKey() == GLFW.GLFW_KEY_R && event.getAction() != GLFW.GLFW_RELEASE && !Screen.hasShiftDown() && MC != null) {
            if (MC.hitResult != null && MC.player != null && THISWORLD != null) {
                if (MC.hitResult.getType() == RayTraceResult.Type.BLOCK) {
                    BlockRayTraceResult target_ = (BlockRayTraceResult) MC.hitResult;
                    BlockPos blockPos = target_.getBlockPos();
                    BlockState blockState = THISWORLD.getBlockState(blockPos);
                    if (blockState.getBlock() instanceof NBTNoteBlock) {
                        THISWORLD.blockEvent(blockPos, blockState.getBlock(), 0, 0);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void clickInputHandler (InputEvent.ClickInputEvent event) {
        // CTRL + Middle Click: Pick up with NBT
        if (event.isPickBlock() && Screen.hasControlDown() && MC != null) {
            if (MC.hitResult != null && MC.player != null && THISWORLD != null && MC.gameMode != null) {
                boolean isCreative = MC.player.abilities.instabuild;
                if (isCreative && MC.hitResult.getType() == RayTraceResult.Type.BLOCK) {
                    BlockRayTraceResult target_ = (BlockRayTraceResult) MC.hitResult;
                    BlockPos blockPos = target_.getBlockPos();
                    THISWORLD.getBlockEntity(blockPos);
                    BlockState state = THISWORLD.getBlockState(blockPos);

                    if (state.getBlock() instanceof NBTNoteBlock) {
                        event.setCanceled(true);
                        ItemStack result;
                        NoteBlockTileEntity te = null;
                        BlockPos pos = ((BlockRayTraceResult) MC.hitResult).getBlockPos();

                        if (state.hasTileEntity())
                            te = (NoteBlockTileEntity) THISWORLD.getBlockEntity(pos);
                        if (te == null) {
                            te = (NoteBlockTileEntity) state.getBlock().createTileEntity(state, MC.level);
                        }

                        result = state.getPickBlock(target_, THISWORLD, pos, MC.player);
                        if (te != null) {
                            te.loadFromBlockState(state);
                            Minecraft.getInstance().addCustomNbtData(result, te);
                        }

                        MC.player.inventory.setPickedItem(result);
                        MC.gameMode.handleCreativeModeItemAdd(MC.player.getItemInHand(Hand.MAIN_HAND), 36 + MC.player.inventory.selected);
                    }
                }
            }
        }
    }
}
package com.mhk.nbaddons.events;

import com.mhk.nbaddons.blocks.NBTNoteBlock;
import com.mhk.nbaddons.config.NoteBlockConfig;
import com.mhk.nbaddons.nbaddons;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import static com.mhk.nbaddons.util.NoteBlockUtil.*;
import static net.minecraft.block.NoteBlock.NOTE;

@Mod.EventBusSubscriber(modid = nbaddons.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModClientEvents {

    private static World THISWORLD;
    private static Minecraft MC;

    @SubscribeEvent
    public static void onLoadWorld(WorldEvent event) {
        THISWORLD = (World) event.getWorld();
        MC = Minecraft.getInstance();
    }

    @SubscribeEvent
    public static void mouseScrollHandler(MouseScrollEvent event) {

        if (!Screen.hasShiftDown() && NoteBlockConfig.allowScroll.get() && MC != null) {
            if (MC.objectMouseOver != null && MC.player != null && THISWORLD != null) {
                if (MC.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
                    BlockRayTraceResult target_ = (BlockRayTraceResult) MC.objectMouseOver;
                    BlockPos blockPos = target_.getPos();
                    BlockState blockState = THISWORLD.getBlockState(blockPos);
                    if (blockState.getBlock() instanceof NBTNoteBlock) {
                        int note = blockState.get(NOTE);
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
                        tuneNoteBlock(THISWORLD, blockPos, MC.player, note);
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
            if (MC.objectMouseOver != null && MC.player != null && THISWORLD != null) {
                if (MC.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
                    BlockRayTraceResult target_ = (BlockRayTraceResult) MC.objectMouseOver;
                    BlockPos blockPos = target_.getPos();
                    BlockState blockState = THISWORLD.getBlockState(blockPos);
                    if (blockState.getBlock() instanceof NBTNoteBlock) {
                        THISWORLD.addBlockEvent(blockPos, blockState.getBlock(), 0, 0);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void guiKeyPressHandler(GuiScreenEvent.KeyboardKeyEvent event) {

    }

    @SubscribeEvent
    public static void gameTickHandler(TickEvent.ClientTickEvent event) {

    }

    @SubscribeEvent
    public static void rightClickBlockHandler(PlayerInteractEvent.RightClickBlock event) {

    }
}
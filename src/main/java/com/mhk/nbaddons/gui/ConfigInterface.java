package com.mhk.nbaddons.gui;

import com.mhk.nbaddons.config.NoteBlockConfig;
import com.mhk.nbaddons.events.ModClientEvents;
import com.mhk.nbaddons.nbaddons;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.mhk.nbaddons.gui.SwitchButton.SWITCH_H;
import static com.mhk.nbaddons.gui.SwitchButton.SWITCH_W;

public class ConfigInterface extends Screen {

    // General
    private List<Button> buttonRenderOrder = new ArrayList<>();
    // Measurements
    private final int TEXTURESIZE = 255;
    private final int WHITEKEY_W = 128, WHITEKEY_H = 512;
    private final int BLACKKEY_W = 64, BLACKKEY_H = 360;
    private final int NUMBERSIZE = 9, LETTERSIZE = 7, SMALLTEXTSIZE = 4;
    private final float SWITCHSIZE = 0.5f;
    private final int LARGEPADDING = 128, PADDING = 32;
    private final int SEP_X = 4, SEP_Y = 1;
    private float scale;
    // Columns & Config
    private List<ScreenText> right_column_content = new ArrayList<>();
    private float column_width, left_column_x, right_column_x, right_column_mid;
    // Others
    private final StringTextComponent EMPTY_STC = new StringTextComponent("");
    private final ResourceLocation GUIDE_LOC = new ResourceLocation(nbaddons.MOD_ID, "/textures/other/guide_smoke.png");

    public ConfigInterface() {
        super(new StringTextComponent("Configuration Interface"));
    }

    @Override
    public void init() {
        assert minecraft != null;
        // Scale is calculated the same way as NoteBlockInterface::scale
        scale = (float) this.width / (TEXTURESIZE * 8 + LARGEPADDING * (2 + SEP_X * 2) + PADDING * 5);

        // initialize list for each render stage
        // render order is in different order of addButton order:
        // addButton: surface button added *first* -> top click priority when layered
        // render: surface button added *last* -> shows on top when layered
        buttonRenderOrder.clear();
        right_column_content.clear();

        {
            right_column_x = this.width * (3/4f) + PADDING * scale;
            column_width = this.width * (1/4f) - PADDING * scale * 2f;
            right_column_mid = right_column_x + column_width/2f;
        }
        // Add done button to button list
        {
            buttonRenderOrder.add(addButton(new Button(width/2 - 100, Math.round(height/2f + 5.5f * LARGEPADDING * scale), 200, 20, new StringTextComponent("Done"), button -> closeScreen())));
        }
        // setup columns and add buttons
        {
            float ypos = 2 * LARGEPADDING * scale;
            right_column_content.add(new ScreenText(font, TextFormatting.WHITE.toString(), "Options",
                    column_width, NUMBERSIZE*scale, 0, right_column_mid, ypos, true, false));
            ypos += right_column_content.get(0).getHeight() + PADDING * scale;
            right_column_content.add(new ScreenText(font, TextFormatting.WHITE.toString() + TextFormatting.ITALIC.toString(), "Enable Scrolling Outside GUI",
                    column_width, SMALLTEXTSIZE*scale, 0, right_column_mid, ypos, true, false));
            ypos += right_column_content.get(1).getHeight() + PADDING * scale;
            buttonRenderOrder.add(addButton(new SwitchButton(minecraft, NoteBlockConfig.getAllowScroll(), scale*SWITCHSIZE, right_column_mid - (SWITCH_W*scale*SWITCHSIZE)/2f, ypos,
                    EMPTY_STC, button -> NoteBlockConfig.setScroll())));
            ypos += SWITCH_H*scale*SWITCHSIZE + PADDING * scale;
            right_column_content.add(new ScreenText(font, TextFormatting.WHITE.toString() + TextFormatting.ITALIC.toString(), "Coloured Octaves",
                    column_width, SMALLTEXTSIZE*scale, 0, right_column_mid, ypos, true, false));
            ypos += right_column_content.get(2).getHeight() + PADDING * scale;
            buttonRenderOrder.add(addButton(new SwitchButton(minecraft, NoteBlockConfig.getColoredKeys(), scale*SWITCHSIZE, right_column_mid - (SWITCH_W*scale*SWITCHSIZE)/2f, ypos,
                    EMPTY_STC, button -> NoteBlockConfig.setColoredKeys())));
            ypos += SWITCH_H*scale*SWITCHSIZE + PADDING * scale;
            right_column_content.add(new ScreenText(font, TextFormatting.WHITE.toString() + TextFormatting.ITALIC.toString(), "Swap Double Tap and Replay",
                    column_width, SMALLTEXTSIZE*scale, 0, right_column_mid, ypos, true, false));
            ypos += right_column_content.get(3).getHeight() + PADDING * scale;
            buttonRenderOrder.add(addButton(new SwitchButton(minecraft, NoteBlockConfig.getRemapReplay(), scale*SWITCHSIZE, right_column_mid - (SWITCH_W*scale*SWITCHSIZE)/2f, ypos,
                    EMPTY_STC, button -> NoteBlockConfig.remapReplay())));
        }
        super.init();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        matrixStack.clear();
        this.renderBackground(matrixStack);

        for (Button bt : buttonRenderOrder) {
            bt.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        for (ScreenText text : right_column_content) {
            text.render(matrixStack);
        }

        matrixStack.push();
        {
            matrixStack.translate(this.width/2f, LARGEPADDING * scale, 0);
            matrixStack.scale(11*scale, 11*scale, 11*scale);
            drawCenteredString(matrixStack, font, TextFormatting.GRAY + "Note Block Configurations/Guide", 0, 0, 0);
        }
        matrixStack.pop();
        matrixStack.push();
        {
            if (minecraft != null) {
                minecraft.getTextureManager().bindTexture(GUIDE_LOC);
                matrixStack.translate(LARGEPADDING * scale, 2 * LARGEPADDING * scale, 0);
                matrixStack.scale(10*scale, 10*(1001/1920f)*scale, 10*scale);
                this.blit(matrixStack, 0, 0, 0, 0, 255, 255);
            }
        }
        matrixStack.pop();
    }

    public static void open() {
        Minecraft.getInstance().displayGuiScreen(new ConfigInterface());
    }
}

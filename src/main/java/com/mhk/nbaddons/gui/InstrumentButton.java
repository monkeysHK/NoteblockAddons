package com.mhk.nbaddons.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public class InstrumentButton extends Button {

    private float scale;
    private int true_width; //this.width is the scaled width
    private int true_height; //this.height is the scaled height
    private final Minecraft mc;
    private final ResourceLocation rl;

    public InstrumentButton(Minecraft mc, ResourceLocation rl, float scale, float x, float y, float w, float h, ITextComponent itc, Button.IPressable pressedAction) {

        super(Math.round(x), Math.round(y), Math.round(w*scale), Math.round(h*scale), itc, pressedAction);
        this.scale = scale;
        this.mc = mc;
        this.rl = rl;
        this.true_width = Math.round(w);
        this.true_height = Math.round(h);
    }

    @Override
    public void playDownSound(SoundHandler handler) {
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            mc.getTextureManager().bindTexture(rl);
            matrixStack.push();
            {
                matrixStack.translate(x, y, 0);
                matrixStack.scale(scale, scale, scale);
                this.blit(matrixStack, 0, 0, 0, 0, true_width, true_height);
            }
            matrixStack.pop();
        }
    }
}

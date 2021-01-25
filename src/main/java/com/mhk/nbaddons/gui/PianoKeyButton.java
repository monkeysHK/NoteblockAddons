package com.mhk.nbaddons.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public class PianoKeyButton extends Button {

    private int u = 0, v = 0, color;
    private int true_width, true_height; //this.width is the scaled width //this.height is the scaled height
    private float scale;
    private final Minecraft mc;

    public PianoKeyButton(Minecraft mc, int color, float scale, float x, float y, float w, float h, ITextComponent itc, Button.IPressable pressedAction) {

        super(Math.round(x), Math.round(y), Math.round(w*scale), Math.round(h*scale), itc, pressedAction);
        this.color = color;
        this.scale = scale;
        this.mc = mc;
        this.true_width = Math.round(w);
        this.true_height = Math.round(h);
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void playDownSound(SoundHandler handler) {
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            matrixStack.push();
            {
                matrixStack.translate(x, y, 0);
                matrixStack.scale(scale, scale, scale);
                fill(matrixStack, 0, 0, true_width, true_height, color);
            }
            matrixStack.pop();
        }
    }
}

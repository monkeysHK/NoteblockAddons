package com.mhk.nbaddons.gui;

import com.mhk.nbaddons.nbaddons;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public class SwitchButton extends Button {

    private float scale;
    private final Minecraft mc;
    private final ResourceLocation rl = new ResourceLocation(nbaddons.MOD_ID, "/textures/other/switch.png");

    private boolean isOn;

    public static int SWITCH_W = 255, SWITCH_H = 127;

    public SwitchButton(Minecraft mc, boolean defaultState, float scale, float x, float y, ITextComponent itc, Button.IPressable pressedAction) {

        super(Math.round(x), Math.round(y), Math.round(SWITCH_W * scale), Math.round(SWITCH_H * scale), itc, pressedAction);
        this.scale = scale;
        this.mc = mc;
        this.isOn = defaultState;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        setOn();
        super.onClick(mouseX, mouseY);
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            mc.getTextureManager().bind(rl);
            matrixStack.pushPose();
            {
                matrixStack.translate(x, y, 0);
                matrixStack.scale(scale, scale * (144/168f), scale);
                if (isOn)
                    this.blit(matrixStack, 0, 0, 0, SWITCH_H, SWITCH_W, SWITCH_H);
                else
                    this.blit(matrixStack, 0, 0, 0, 0, SWITCH_W, SWITCH_H);
            }
            matrixStack.popPose();
        }
    }

    public void setOn() {
        isOn = !isOn;
    }

    public boolean isOn() {
        return isOn;
    }
}

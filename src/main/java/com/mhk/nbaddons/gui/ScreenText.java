package com.mhk.nbaddons.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.AbstractGui.drawCenteredString;
import static net.minecraft.client.gui.AbstractGui.drawString;

public class ScreenText {

    private final FontRenderer fontRenderer;
    private final String textFormatHeader, displayedText;
    private final int text;
    private final float x_limit, wordScale;
    private final boolean center_x, center_y;
    private final List<String> stringHolder = new ArrayList<>();
    private float height, x, y;

    public static final float RATIO = 10;

    public ScreenText(FontRenderer fontRenderer, String textFormatHeader, String displayedText, float x_limit, float wordScale, int text, float x, float y, boolean center_x, boolean center_y) {
        this.fontRenderer = fontRenderer;
        this.textFormatHeader = textFormatHeader;
        this.displayedText = displayedText;
        this.x_limit = x_limit;
        this.wordScale = wordScale;
        this.text = text;
        this.x = x;
        this.y = y;
        this.center_x = center_x;
        this.center_y = center_y;

        updateString();
    }

    private void updateString() {
        // textFormatHeader is a string of TextFormatting(s) added to the beginning of each line
        stringHolder.clear();
        String[] wordList = displayedText.split(" ");
        String line = "";
        for (String word : wordList) {
            if (word.equals("")) continue;
            if (fontRenderer.width(line+(line.equals("")? "": " ")+word) * wordScale > x_limit) {
                stringHolder.add(textFormatHeader + line);
                line = word;
            }
            else line += (line.equals("")? "": " ") + word;
        }
        if (!line.equals("")) stringHolder.add(textFormatHeader + line);
        this.height = stringHolder.size() * wordScale * RATIO;
    }

    public void render(MatrixStack matrixStack) {
        // center_x is true when lines are drawn in centre
        // center_y is true when y indicates centre, not the top
        float starting_y;

        if (center_y) starting_y = y - RATIO * (stringHolder.size() / 2f);
        else starting_y = y;

        for (int i = 0; i < stringHolder.size(); i++) {
            matrixStack.pushPose();
            {
                matrixStack.translate(x, starting_y + RATIO * wordScale * i, 0);
                matrixStack.scale(wordScale, wordScale, wordScale);
                if (center_x)
                    drawCenteredString(matrixStack, fontRenderer, stringHolder.get(i), text, 0, 0);
                else
                    drawString(matrixStack, fontRenderer, stringHolder.get(i), text, 0, 0);
            }
            matrixStack.popPose();
        }
    }

    public List<String> getString() {
        return stringHolder;
    }

    public float getHeight() {
        return height;
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
        updateString();
    }
}

package com.mhk.nbaddons.gui;

import com.mhk.nbaddons.config.NoteBlockConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mhk.nbaddons.gui.NoteBlockOrder.byState;
import static com.mhk.nbaddons.util.NoteBlockUtil.*;
import static net.minecraft.block.NoteBlock.NOTE;

public class NoteBlockInterface extends Screen {

    // General
    private BlockPos nbPos;
    private BlockState blockState, blockStateUnder;
    private final List<List<Button>> buttonRenderOrder = new ArrayList<>();
    private final int RENDERSTAGES = 4;
    private final PlayerEntity player;
    // Measurements
    private final int LARGEGAPSIZE = 128, SMALLGAPSIZE = 16;
    private final int SEP_X = 4, SEP_Y = 1;
    private float scale;
    // Instrument
    private final float[][] instru_pos = new float[16][2];
    private final int TEXTURESIZE = 256;
    private NoteBlockOrder lastNbo, currentNbo;
    // Keys
    private final int[] WHITEKEYS = {26,1,3,5,6,8,10,11,13,15,17,18,20,22,23,25}; // values over 24 are dummy values; 25 MUST be after the last white key, no other restrictions.
    private final int[] BLACKKEYS = {0,2,4,7,9,12,14,16,19,21,24};
    private String[] render_white, render_color;
    private final float[][] key_pos = new float[30][2]; // Allow no more than 5 dummy values larger than 24
    private final int WHITEKEY_W = 128, WHITEKEY_H = 512;
    private final int BLACKKEY_W = 64, BLACKKEY_H = 360;
    private KeyType keyType;
    private int lastNote, currentNote;
    // Keyboard Input
    private boolean noteChangeFlag, ignoreFlag = false;
    private int sequenceFlag = 0;
    private int prevKey = -1, prevMod = -1;
    // Others
    private final StringTextComponent EMPTY_STC = new StringTextComponent("");
    private static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    private final ResourceLocation BUTTON_LOC = new ResourceLocation("minecraft", "textures/gui/widgets.png");
    World nonClientWorld;

    public NoteBlockInterface(BlockPos blockPos, PlayerEntity player) {
        super(new StringTextComponent("Note Block Interface"));
        nbPos = blockPos;
        this.player = player;
    }

    @Override
    public void init() {
        assert minecraft != null;
        // Scale is common for all objects on screen. formula: this.width = halfx + (LARGEGAPSIZE * 2) * scale = (TEXTURESIZE * 8 + LARGEGAPSIZE * (2 + SEP_X * 2) + SMALLGAPSIZE * 5) * scale;
        scale = (float) this.width / (TEXTURESIZE * 8 + LARGEGAPSIZE * (2 + SEP_X * 2) + SMALLGAPSIZE * 5);

        // initialize list for each render stage
        // render order is in different order of addButton order:
        // addButton: surface button added *first* -> top click priority when layered
        // render: surface button added *last* -> shows on top when layered
        buttonRenderOrder.clear();
        for (int i = 0; i < RENDERSTAGES; i++) {
            buttonRenderOrder.add(new ArrayList<>());
        }
        // Add instrument buttons
        {
            float x_rowlength = TEXTURESIZE * scale * 8 + LARGEGAPSIZE * scale * 2 + SMALLGAPSIZE * scale * 5;
            float x_startingpt = (this.width - x_rowlength) / 2f;
            float y_separation = (LARGEGAPSIZE * SEP_Y * scale) + (TEXTURESIZE * scale);
            float y_startingpt_r2 = (this.height / 2f) - y_separation;
            float y_startingpt_r1 = y_startingpt_r2 - (TEXTURESIZE + SMALLGAPSIZE) * scale;
            for (NoteBlockOrder nbo : NoteBlockOrder.values()) {
                int n = nbo.ordinal();
                int groupn = nbo.getGroup().ordinal();
                instru_pos[nbo.ordinal()][1] = nbo.getGroup().ordinal() < 3 ? y_startingpt_r1 : y_startingpt_r2;
                instru_pos[nbo.ordinal()][0] = x_startingpt + (n % 8) * TEXTURESIZE * scale + (n % 8 - groupn % 3) * SMALLGAPSIZE * scale + (groupn % 3) * LARGEGAPSIZE * scale;
            }
        }
        // Add white keys
        {
            float x_rowlength = WHITEKEY_W * scale * WHITEKEYS.length + SMALLGAPSIZE * scale * (WHITEKEYS.length - 1);
            float x_startingpt = (this.width - x_rowlength) / 2f;
            float y_startingpt = this.height / 2f;
            int i = 0;
            for (int key : WHITEKEYS) {
                key_pos[key][0] = x_startingpt + Math.round((WHITEKEY_W + SMALLGAPSIZE) * scale * i);
                key_pos[key][1] = y_startingpt;
                i++;
            }
        }
        // Add black keys
        {
            float x_referencept;
            float y_startingpt = this.height / 2f;
            for (int key : BLACKKEYS) {
                x_referencept = key_pos[key+1][0];
                key_pos[key][0] = x_referencept - ((BLACKKEY_W + SMALLGAPSIZE) * scale / 2f);
                key_pos[key][1] = y_startingpt;
            }
        }
        // Add all buttons to button lists (in separate render stage)
        {
            for (NoteBlockOrder nbo : NoteBlockOrder.values()) {
                buttonRenderOrder.get(0).add(addButton(new InstrumentButton(minecraft, nbo.getRes(), scale, instru_pos[nbo.ordinal()][0], instru_pos[nbo.ordinal()][1], TEXTURESIZE, TEXTURESIZE, nbo.getStc(), button -> switchNbo(nbo))));
            }
            for (int key: BLACKKEYS) {
                buttonRenderOrder.get(1).add(addButton(new PianoKeyButton(minecraft, 0xFF000000, scale, key_pos[key][0], key_pos[key][1], BLACKKEY_W, BLACKKEY_H, EMPTY_STC, button -> switchNote(key))));

            }
            for (int key : WHITEKEYS) {
                buttonRenderOrder.get(2).add(addButton(new PianoKeyButton(minecraft, 0xFFFFFFFF, scale, key_pos[key][0], key_pos[key][1], WHITEKEY_W, WHITEKEY_H, EMPTY_STC, button -> switchNote(key))));
            }
            // All other buttons
            buttonRenderOrder.get(3).add(addButton(new Button(width/2 - 100, (int) Math.round(height/2f + (WHITEKEY_H + 1.5*LARGEGAPSIZE)*scale), 200, 20, new StringTextComponent("Done"), button -> NoteBlockInterface.closeGUI())));
        }
        // initialize variables
        if (this.getMinecraft().level != null) {
            blockState = this.getMinecraft().level.getBlockState(nbPos);
            blockStateUnder = this.getMinecraft().level.getBlockState(nbPos.below());
        }
        lastNbo = currentNbo = byState(blockStateUnder);
        updateRenderKeys();
        lastNote = blockState.getValue(NOTE);
        putNote(lastNote);
        super.init();
    }

    private void switchNbo(NoteBlockOrder selectedNbo) {
        if (player.isCreative()) {
            currentNbo = selectedNbo;
            updateBlock();
        }
    }

    private void switchNote(int i) {
        if (ignoreFlag) return;
        if (i > 24) return;
        putNote(i);
        if (!updateBlock() && this.getMinecraft().level != null)
            this.getMinecraft().level.blockEvent(nbPos, blockState.getBlock(), 0, 0); // play sound
        noteChangeFlag = true;
    }

    private void putNote(int i) {
        currentNote = i;
        if (Arrays.stream(WHITEKEYS).anyMatch(x -> x == i)) keyType = KeyType.WHITE;
        if (Arrays.stream(BLACKKEYS).anyMatch(x -> x == i)) keyType = KeyType.BLACK;
    }

    private boolean updateBlock() {
        World worldUsing = this.nonClientWorld != null ? this.nonClientWorld : this.getMinecraft().level;

        if (lastNbo != currentNbo && worldUsing != null && !worldUsing.isClientSide()) {
            lastNbo = currentNbo;
            blockState = tuneNoteBlock(worldUsing, nbPos, player, currentNbo);
            updateRenderKeys();
            return true;
        }

        if (lastNote != currentNote && worldUsing != null && !worldUsing.isClientSide()) {
            lastNote = currentNote;
            blockState = tuneNoteBlock(worldUsing, nbPos, player, currentNote);
            return true;
        }

        return false;
    }

    private void updateRenderKeys() {
        render_white = currentNbo.getGroup().getKeys();
        render_color = currentNbo.getGroup().getColoredkeys();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        assert minecraft != null;

        int old_ = blockState.getValue(NOTE);
        int new_ = old_;

        if (!hasShiftDown()) {
            if (hasControlDown()) {
                // raise/lower by octave
                if (delta > 0) new_ = octaveChange(old_, true); // scrolling up + ctrl
                if (delta < 0) new_ = octaveChange(old_, false); // scrolling down + ctrl
            }
            else {
                // raise/lower by note
                if (delta > 0) new_ = transposeNote(blockState.getValue(NOTE), 1); // scrolling up
                if (delta < 0) new_ = transposeNote(blockState.getValue(NOTE), -1); // scrolling down
            }
            switchNote(new_);
        }

        return true;
    }

    private void replayNote() {
        ignoreFlag = true;
        if (this.getMinecraft().level != null)
            this.getMinecraft().level.blockEvent(nbPos, blockState.getBlock(), 0, 0);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if (keyCode >= GLFW.GLFW_KEY_A && keyCode <= GLFW.GLFW_KEY_H) {
            // note change handler
            if (prevKey == keyCode && prevMod == modifiers && !noteChangeFlag) {
                if (NoteBlockConfig.getRemapReplay()) replayNote();
                else switchNote(octaveChange(blockState.getValue(NOTE), true));
            }
            else {
                int delta = NoteBlockConfig.getTargetRange()*12;
                if (modifiers == GLFW.GLFW_MOD_CONTROL) delta += 1; // control: sharp
                else if (modifiers == GLFW.GLFW_MOD_SHIFT) delta -= 1; // shift: flat
                switch (keyCode) {
                    // a-g: set to lowest of corresponding note
                    case GLFW.GLFW_KEY_G: switchNote(1+delta); break;
                    case GLFW.GLFW_KEY_A: switchNote(3+delta); break;
                    case GLFW.GLFW_KEY_B: case GLFW.GLFW_KEY_H: switchNote(5+delta); break;
                    case GLFW.GLFW_KEY_C: switchNote(6+delta); break;
                    case GLFW.GLFW_KEY_D: switchNote(8+delta); break;
                    case GLFW.GLFW_KEY_E: switchNote(10+delta); break;
                    case GLFW.GLFW_KEY_F: switchNote(11+delta); break;
                }
            }
            noteChangeFlag = false;
        }
        else {
            // other handlers
            switch (keyCode) {
                // spacebar button replays note
                case GLFW.GLFW_KEY_SPACE: replayNote(); break;
                case GLFW.GLFW_KEY_R:
                    if (NoteBlockConfig.getRemapReplay()) switchNote(octaveChange(blockState.getValue(NOTE), true));
                    else replayNote(); break;
                // for instrument switch
                case GLFW.GLFW_KEY_RIGHT: NoteBlockConfig.setTargetRange(1); break; // right arrow
                case GLFW.GLFW_KEY_LEFT: NoteBlockConfig.setTargetRange(0); break; // left arrow
                case GLFW.GLFW_KEY_TAB: // tab: shift-backwards; ctrl: jump;
                    if (modifiers == 1) switchNbo(currentNbo.getNext(-1)); // with mod=shift
                    else if (modifiers == 2) switchNbo(currentNbo.jumpNext(true)); // with mod=ctrl
                    else if (modifiers == 3) switchNbo(currentNbo.jumpNext(false)); // with mod=ctrl+shift
                    else switchNbo(currentNbo.getNext(1)); break; // with mod=none
                // for note switch
                case GLFW.GLFW_KEY_DOWN: // down arrow
                    if (modifiers != 2) switchNote(transposeNote(blockState.getValue(NOTE), -1));
                    else switchNote(octaveChange(blockState.getValue(NOTE), false)); break;
                case GLFW.GLFW_KEY_UP: // up arrow
                    if (modifiers != 2) switchNote(transposeNote(blockState.getValue(NOTE), 1));
                    else switchNote(octaveChange(blockState.getValue(NOTE), true)); break;
                // closeScreen by pressing enter keys
                case GLFW.GLFW_KEY_ENTER: case GLFW.GLFW_KEY_KP_ENTER: NoteBlockInterface.closeGUI(); break;
            }
        }
        // sequence flag handler
        if (sequenceFlag == 1) {
            if (keyCode >= GLFW.GLFW_KEY_KP_1 && keyCode <= GLFW.GLFW_KEY_KP_8) switchNbo(NoteBlockOrder.values()[keyCode-321]); // numpad
            if (keyCode >= GLFW.GLFW_KEY_1 && keyCode <= GLFW.GLFW_KEY_8) switchNbo(NoteBlockOrder.values()[keyCode-49]); // keyboard top row numbers
            sequenceFlag = 0;
        }
        else if (sequenceFlag == 2) {
            if (keyCode >= GLFW.GLFW_KEY_KP_1 && keyCode <= GLFW.GLFW_KEY_KP_8) switchNbo(NoteBlockOrder.values()[keyCode-321 + 8]); // numpad
            if (keyCode >= GLFW.GLFW_KEY_1 && keyCode <= GLFW.GLFW_KEY_8) switchNbo(NoteBlockOrder.values()[keyCode-49 + 8]); // keyboard top row numbers
            sequenceFlag = 0;
        }
        else {
            switch (keyCode) {
                case GLFW.GLFW_KEY_KP_1: case GLFW.GLFW_KEY_1: sequenceFlag = 1; break; // numpad 1 or number 1
                case GLFW.GLFW_KEY_KP_2: case GLFW.GLFW_KEY_2: sequenceFlag = 2; break; // numpad 2 or number 2
            }
        }
        prevKey = keyCode;
        prevMod = modifiers;
        ignoreFlag = false;
        return super.keyPressed(keyCode, scanCode, modifiers);
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

        // render the instruments
        for (Button bt : buttonRenderOrder.get(0)) {
            bt.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        // render the overlay
        for (int i = 0, a, b; i < instru_pos.length; i++) {
            matrixStack.pushPose();
            {
                matrixStack.translate(instru_pos[i][0]+TEXTURESIZE*scale*0.5, instru_pos[i][1]+TEXTURESIZE*scale*0.65, 0);
                matrixStack.scale(9*scale, 9*scale, 9*scale);
                a = i/8 + 1; b = i%8 + 1;
                drawCenteredString(matrixStack, font, (i == currentNbo.ordinal()? TextFormatting.GREEN + (a + "-" + b):
                        (sequenceFlag == a? TextFormatting.GREEN : TextFormatting.WHITE) + (a + "-") + TextFormatting.WHITE + b), 0, 0, 0);
            }
            matrixStack.popPose();
        }
        if (currentNbo != null && minecraft != null) {
            matrixStack.pushPose();
            {
                matrixStack.translate(instru_pos[currentNbo.ordinal()][0], instru_pos[currentNbo.ordinal()][1], 0);
                matrixStack.scale(scale, scale, scale);
                fill(matrixStack, 0, 0, TEXTURESIZE, TEXTURESIZE, 0x8071ea00);
            }
            matrixStack.popPose();
            if (!player.isCreative()) {
                minecraft.getTextureManager().bind(BUTTON_LOC);
                matrixStack.pushPose();
                {
                    TRANSLUCENT_TRANSPARENCY.setupRenderState();
                    RenderSystem.color4f(1f, 1f, 1f, 0.3f);
                    matrixStack.translate(instru_pos[currentNbo.ordinal()][0], instru_pos[currentNbo.ordinal()][1], 0);
                    matrixStack.scale((255/20f)*scale, (255/20f)*scale, (255/20f)*scale);
                    this.blit(matrixStack, 0, 0, 0, 146, 20, 20);
                    TRANSLUCENT_TRANSPARENCY.clearRenderState();
                }
                matrixStack.popPose();
            }
        }
        // render the whitekeys
        for (Button bt : buttonRenderOrder.get(2)) {
            bt.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        // render the overlay
        if (keyType == KeyType.WHITE) {
            matrixStack.pushPose();
            {
                matrixStack.translate(key_pos[currentNote][0], key_pos[currentNote][1], 0);
                matrixStack.scale(scale, scale, scale);
                fill(matrixStack, 0, 0, WHITEKEY_W, WHITEKEY_H, 0x8071ea00);
            }
            matrixStack.popPose();
        }
        // Key Labels
        for (int i = 0; i < WHITEKEYS.length; i++) {
            matrixStack.pushPose();
            {
                matrixStack.translate(key_pos[WHITEKEYS[i]][0] + WHITEKEY_W * scale * 0.5, key_pos[WHITEKEYS[i]][1] + WHITEKEY_H * scale * 0.8, 0);
                matrixStack.scale(7 * scale, 7 * scale, 7 * scale);
                drawCenteredString(matrixStack, font, NoteBlockConfig.getColoredKeys()? render_color[i]: render_white[i], 0, 0, 0);
            }
            matrixStack.popPose();
        }
        // Range Indicator
        matrixStack.pushPose();
        {
            matrixStack.translate(key_pos[1][0], key_pos[1][1] + (WHITEKEY_H + 3 * SMALLGAPSIZE) * scale, 0);
            matrixStack.scale(scale, scale, scale);
            fill(matrixStack, 0, 0, WHITEKEY_W * 7 + SMALLGAPSIZE * 6, 16,
                    (NoteBlockConfig.getTargetRange() == 0)? 0xFF71ea00: 0xFFFFFFFF);
        }
        matrixStack.popPose();
        matrixStack.pushPose();
        {
            matrixStack.translate(key_pos[13][0], key_pos[13][1] + (WHITEKEY_H + 3 * SMALLGAPSIZE) * scale, 0);
            matrixStack.scale(scale, scale, scale);
            fill(matrixStack, 0, 0, WHITEKEY_W * 7 + SMALLGAPSIZE * 6, 16,
                    (NoteBlockConfig.getTargetRange() == 1)? 0xFF71ea00: 0xFFFFFFFF);
        }
        matrixStack.popPose();
        // render the blackkeys
        for (Button bt : buttonRenderOrder.get(1)) {
            bt.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        // render the overlay
        if (keyType == KeyType.BLACK) {
            matrixStack.pushPose();
            {
                matrixStack.translate(key_pos[currentNote][0], key_pos[currentNote][1], 0);
                matrixStack.scale(scale, scale, scale);
                fill(matrixStack, 0, 0, BLACKKEY_W, BLACKKEY_H, 0x8071ea00);
            }
            matrixStack.popPose();
        }
        // render all other buttons
        for (Button bt : buttonRenderOrder.get(3)) {
            bt.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        matrixStack.pushPose();
        {
            matrixStack.translate(this.width/2f, instru_pos[0][1] - LARGEGAPSIZE*scale, 0);
            matrixStack.scale(11*scale, 11*scale, 11*scale);
            drawCenteredString(matrixStack, font, TextFormatting.GRAY + "Note Block Interface", 0, 0, 0);
        }
        matrixStack.popPose();
        matrixStack.pushPose();
        {
            matrixStack.translate(this.width/2f, height/2f + (WHITEKEY_H + LARGEGAPSIZE - SMALLGAPSIZE)*scale, 0);
            matrixStack.scale(5*scale, 5*scale, 5*scale);
            drawCenteredString(matrixStack, font, TextFormatting.GRAY + "Use /noteblock for configuration and usage guide.", 0, 0, 0);
        }
        matrixStack.popPose();
    }

    // Custom Member Functions
    public void attachNonClientWorld(World worldIn) {
        this.nonClientWorld = worldIn;
    }
    public static NoteBlockInterface openGUI(BlockPos pos, PlayerEntity player) {
        NoteBlockInterface self = new NoteBlockInterface(pos, player);
        Minecraft.getInstance().setScreen(self);
        return self;
    }
    public static void closeGUI() {
        Minecraft.getInstance().setScreen(null);
    }

    private enum KeyType {
        WHITE,
        BLACK
    }
}

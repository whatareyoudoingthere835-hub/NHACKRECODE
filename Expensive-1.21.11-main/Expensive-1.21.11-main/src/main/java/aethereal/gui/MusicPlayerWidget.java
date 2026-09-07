package aethereal.gui;
import aethereal.*;
import aethereal.features.modules.Module;
import aethereal.features.modules.*;
import aethereal.features.modules.combat.*;
import aethereal.features.modules.movement.*;
import aethereal.features.modules.player.*;
import aethereal.features.modules.render.*;
import aethereal.features.modules.misc.*;
import aethereal.features.modules.earnings.*;
import aethereal.features.modules.autobuy.*;
import aethereal.features.commands.*;
import aethereal.gui.*;
import aethereal.graphics.*;
import aethereal.system.config.*;
import aethereal.system.events.*;
import aethereal.system.network.*;
import aethereal.system.resources.*;
import aethereal.core.models.*;
import aethereal.core.types.*;
import aethereal.core.accessors.*;
import aethereal.core.annotations.*;
import aethereal.utils.*;
import aethereal.utils.math.*;


import java.util.function.BooleanSupplier;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class MusicPlayerWidget extends Draggable {
    public static final float WIDGET_WIDTH = 210.0f;
    public static final float WIDGET_HEIGHT = 85.0f;
    public static final float COVER_SIZE = 44.0f;
    public static final float COVER_CORNER_RADIUS = 6.0f;

    public final GlTextureObject playIcon;
    public final GlTextureObject pauseIcon;
    public final GlTextureObject previousIcon;
    public final GlTextureObject nextIcon;
    public final GlTextureObject starsTexture;

    public final MsdfFont titleFont;
    public final MsdfFont headerFont;
    public final MsdfFont timeFont;

    public final ToggleAnimator playHoverAnimator;
    public final ToggleAnimator prevHoverAnimator;
    public final ToggleAnimator nextHoverAnimator;

    public float computedWidth;
    public float computedHeight;

    public MusicPlayerWidget(BooleanSupplier visibility) {
        super("Music Player", visibility);
        this.playIcon = new GlTextureObject(new ClasspathResource("/icons/mediaplayer/play.png"));
        this.pauseIcon = new GlTextureObject(new ClasspathResource("/icons/mediaplayer/pause.png"));
        this.previousIcon = new GlTextureObject(new ClasspathResource("/icons/mediaplayer/previous.png"));
        this.nextIcon = new GlTextureObject(new ClasspathResource("/icons/mediaplayer/next.png"));
        this.starsTexture = new GlTextureObject(new ClasspathResource("/textures/stars.png"));

        this.titleFont = Fonts.INTER_BOLD.get();
        this.headerFont = Fonts.INTER_SEMIBOLD.get();
        this.timeFont = Fonts.INTER_BOLD.get();

        this.playHoverAnimator = new ToggleAnimator(150, Easings.LINEAR);
        this.prevHoverAnimator = new ToggleAnimator(150, Easings.LINEAR);
        this.nextHoverAnimator = new ToggleAnimator(150, Easings.LINEAR);

        this.computedWidth = WIDGET_WIDTH;
        this.computedHeight = WIDGET_HEIGHT;

        this.x = 10.0f;
        this.y = 80.0f;
    }

    @Override
    public void layout(DragRenderContext context) {
        if (isVisible()) {
            this.computedWidth = WIDGET_WIDTH;
            this.computedHeight = WIDGET_HEIGHT;
        }
    }

    @Override
    public void render(DragRenderContext context) {
        if (!isVisible()) return;

        MusicService music= MusicService.INSTANCE;
        music.ensureStarted();

        MatrixStack matrixStack= context.matrixStack();
        Matrix4f matrix= matrixStack.peek().getPositionMatrix();
        GraphicsDrawEngine drawEngine= context.drawEngine();
        PaletteColorStack colorStack= drawEngine.colorStack();
        StylePalette palette= context.theme().palette();

        float mX= context.mouseX();
        float mY= context.mouseY();
        float centerX= this.x + WIDGET_WIDTH / 2.0f;

        boolean prevHovered= mX >= centerX - 40.0f && mX <= centerX - 16.0f && mY >= this.y + 55.0f && mY <= this.y + 75.0f;
        boolean playHovered= mX >= centerX - 12.0f && mX <= centerX + 12.0f && mY >= this.y + 55.0f && mY <= this.y + 75.0f;
        boolean nextHovered= mX >= centerX + 16.0f && mX <= centerX + 40.0f && mY >= this.y + 55.0f && mY <= this.y + 75.0f;

        this.prevHoverAnimator.state(prevHovered);
        this.playHoverAnimator.state(playHovered);
        this.nextHoverAnimator.state(nextHovered);

        // 1. Background Blur & Panel
        int blurAttachment= FrameBufferUtils.getColorAttachmentId(Expensive.INSTANCE.windowController().headerBlur().getBlurFramebuffer());
        if (blurAttachment > 0) {
            drawEngine.roundedBlur(matrix, this.x, this.y, WIDGET_WIDTH, WIDGET_HEIGHT, 10.0f, colorStack.white(), blurAttachment);
        }

        int panelTop= colorStack.computeColor(palette.surfaceBackground().tone(900).argb());
        int panelBottom= colorStack.computeColor(palette.surfaceBackground().tone(801).argb());
        int panelOutline= colorStack.computeColor(palette.surfaceOutline().tone(600).argb());
        
        drawEngine.roundedRectangle(matrix, this.x, this.y, WIDGET_WIDTH, WIDGET_HEIGHT, 10.0f, 2.5f, panelOutline, panelTop, panelTop, panelBottom, panelBottom);
        drawEngine.texture(matrix, this.x, this.y, WIDGET_WIDTH, WIDGET_HEIGHT, drawEngine.bindTexture(this.starsTexture.textureWithSTB()), colorStack.white());

        // 2. Cover Thumbnail (Rounded Rectangle 6px)
        float coverX= this.x + 12.0f;
        float coverY= this.y + 12.0f;
        GlTextureObject cover= music.cover();
        if (cover != null) {
            int coverTexId= drawEngine.bindTexture(cover.textureWithSTB());
            drawEngine.roundedTexture(matrix, coverX, coverY, COVER_SIZE, COVER_SIZE, COVER_CORNER_RADIUS, coverTexId, colorStack.white());
        }

        // 3. Track Info (Title on line 1, Artist on line 2)
        float textX= coverX + COVER_SIZE + 10.0f;
        float titleY= coverY + 3.0f;
        float artistY= coverY + 20.0f;

        String titleStr= music.title().toLowerCase();
        String artistStr= music.author().toUpperCase();

        int titleColor= colorStack.white();
        int artistColor= colorStack.computeColor(0xFF888899);

        drawEngine.beginScissor(matrix, textX, this.y, (this.x + WIDGET_WIDTH - 12.0f) - textX, WIDGET_HEIGHT);
        drawEngine.msdfFont(matrix, this.titleFont, titleStr, textX, titleY, 14.0f, 0.0f, titleColor);
        drawEngine.msdfFont(matrix, this.headerFont, artistStr, textX, artistY, 11.0f, 0.0f, artistColor);
        drawEngine.endScissor();

        // 4. Bottom Controls Row (Time left, Previous, Play/Pause, Next, Time right)
        float rowY= this.y + 62.0f;
        float iconY= this.y + 61.0f;
        String posTime= formatTime(music.positionSeconds());
        String durTime= formatTime(music.durationSeconds());

        int timeColor= colorStack.computeColor(0xFFDDDDDD);
        drawEngine.msdfFont(matrix, this.timeFont, posTime, this.x + 12.0f, rowY, 11.0f, 0.0f, timeColor);

        float durWidth= this.timeFont.getWidth(durTime, 11.0f);
        drawEngine.msdfFont(matrix, this.timeFont, durTime, this.x + WIDGET_WIDTH - 12.0f - durWidth, rowY, 11.0f, 0.0f, timeColor);

        // Control Icons (Previous, Play/Pause, Next)
        int prevColor= getIconColor(colorStack, this.prevHoverAnimator.smoothAnimation());
        int playColor= getIconColor(colorStack, this.playHoverAnimator.smoothAnimation());
        int nextColor= getIconColor(colorStack, this.nextHoverAnimator.smoothAnimation());

        if (this.previousIcon != null) {
            int prevTex= drawEngine.bindTexture(this.previousIcon.textureWithSTB());
            drawEngine.texture(matrix, centerX - 30.0f, iconY, 12.0f, 12.0f, 0.0f, 0.0f, 1.0f, 1.0f, prevTex, prevColor, prevColor, prevColor, prevColor);
        }

        GlTextureObject mainIcon= music.isPlaying() ? this.pauseIcon : this.playIcon;
        if (mainIcon != null) {
            int mainTex= drawEngine.bindTexture(mainIcon.textureWithSTB());
            drawEngine.texture(matrix, centerX - 6.0f, iconY, 12.0f, 12.0f, 0.0f, 0.0f, 1.0f, 1.0f, mainTex, playColor, playColor, playColor, playColor);
        }

        if (this.nextIcon != null) {
            int nextTex= drawEngine.bindTexture(this.nextIcon.textureWithSTB());
            drawEngine.texture(matrix, centerX + 18.0f, iconY, 12.0f, 12.0f, 0.0f, 0.0f, 1.0f, 1.0f, nextTex, nextColor, nextColor, nextColor, nextColor);
        }

        // 5. Bottom Progress Bar Line
        float barX= this.x + 12.0f;
        float barY= this.y + 77.0f;
        float barWidth= WIDGET_WIDTH - 24.0f;
        float barHeight= 3.0f;

        int barBg= colorStack.computeColor(0x25FFFFFF);
        int barFill= colorStack.computeColor(palette.accent().argb());

        drawEngine.roundedRectangle(matrix, barX, barY, barWidth, barHeight, 1.25f, barBg);

        float fillWidth= barWidth * music.progress();
        if (fillWidth > 0.5f) {
            drawEngine.roundedRectangle(matrix, barX, barY, fillWidth, barHeight, 1.25f, barFill);
        }
    }

    private int getIconColor(PaletteColorStack colorStack, float hoverAlpha) {
        if (hoverAlpha <= 0.01f) return colorStack.white();
        int alpha= Math.min(255, 180 + Math.round(75.0f * hoverAlpha));
        return colorStack.computeColor((alpha << 24) | 0xFFFFFF);
    }

    @Override
    public boolean click(MouseButtonInput2 context, boolean inArea) {
        if (isVisible() && context.press() && context.isLeftButtonPressed()) {
            float mX= context.mouseX();
            float mY= context.mouseY();
            float centerX= this.x + WIDGET_WIDTH / 2.0f;

            if (mX >= centerX - 40.0f && mX <= centerX - 16.0f && mY >= this.y + 55.0f && mY <= this.y + 75.0f) {
                MusicService.INSTANCE.previousTrack();
                return true;
            }
            if (mX >= centerX - 12.0f && mX <= centerX + 12.0f && mY >= this.y + 55.0f && mY <= this.y + 75.0f) {
                MusicService.INSTANCE.togglePlay();
                return true;
            }
            if (mX >= centerX + 16.0f && mX <= centerX + 40.0f && mY >= this.y + 55.0f && mY <= this.y + 75.0f) {
                MusicService.INSTANCE.nextTrack();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean cursor(MouseMoveInput context, boolean inArea) {
        return false;
    }

    @Override
    public void animate(WeightedEngine engine) {
        this.playHoverAnimator.animate(engine);
        this.prevHoverAnimator.animate(engine);
        this.nextHoverAnimator.animate(engine);
    }

    @Override
    public void update() {
    }

    @Override
    public float width() {
        return this.computedWidth;
    }

    @Override
    public float height() {
        return this.computedHeight;
    }

    private String formatTime(double seconds) {
        if (seconds < 0.0d || Double.isNaN(seconds)) seconds = 0.0d;
        int sec= (int) seconds;
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }
}

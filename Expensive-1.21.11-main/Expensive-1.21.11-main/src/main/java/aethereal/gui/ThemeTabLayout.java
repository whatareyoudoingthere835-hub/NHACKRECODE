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

import net.minecraft.text.Text;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import net.minecraft.util.math.MathHelper;

public class ThemeTabLayout extends AbstractTabLayout {
    static final GlTextureObject importIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/click.png"));
    static final float ax = 30.0f;
    static final float ay = 12.0f;
    static final float az = 97.0f;
    static final float aA = 20.0f;
    static final float aB = 0.0f;

    public ScrollbarWidget scrollbar;
    public final GlTextureObject themesIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/tabs/themes.png"));
    public final GlTextureObject brushIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/brush.png"));
    public final GlTextureObject filterIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/enum.png"));
    public final MsdfFont font = Fonts.INTER_SEMIBOLD.get();
    public final ToggleAnimator panelAnimator = ToggleAnimator.times(2, 100);
    public final WidgetBounds headerBounds = new WidgetBounds(aB, aB, aB, 71.0f);
    public final IconLabelBadge titleBadge = new IconLabelBadge(this.brushIcon, Lang.THEMES_TITLE);
    public final List<ThemeFilterIndicator> filterIndicators = List.of(new ThemeFilterIndicator(ConfigFilter.FAVORITE, Lang.THEMES_INDICATOR_FAVORITE), new ThemeFilterIndicator(ConfigFilter.OFFICIAL, Lang.THEMES_INDICATOR_OFFICIAL), new ThemeFilterIndicator(ConfigFilter.USER, Lang.THEMES_INDICATOR_USER));
    public final ThemeCreatorPanel creatorPanel = new ThemeCreatorPanel();
    public final List<LabeledIconButton> actionButtons = new ArrayList();

    public final MultiSelectDropdown<ConfigFilter> filterDropdown = new MultiSelectDropdown<>(this.font, this.filterIcon, 7.0f, 8.0f, 5.5f, 12, 244.0f);
    public final Set<ConfigFilter> activeFilters = EnumSet.allOf(ConfigFilter.class);
    public boolean filtersDirty = true;
    public int lastFrameCount = -1;

    public ThemeTabLayout() {
        this.filterDropdown.setOptions(List.of(new MultiSelectOption(ConfigFilter.FAVORITE, Lang.THEMES_FILTER_FAVORITE), new MultiSelectOption(ConfigFilter.OFFICIAL, Lang.THEMES_FILTER_OFFICIAL), new MultiSelectOption(ConfigFilter.USER, Lang.THEMES_FILTER_USER)), this.activeFilters);
        this.actionButtons.addAll(List.of(new LabeledIconButton(Lang.THEMES_CREATE, () -> {
            if (this.creatorPanel.isOpened()) {
                openExitConfirmDialog();
            } else {
                this.creatorPanel.open();
            }
        }, this.font, this.themesIcon, 7.0f, 8.0f, 5.5f, 12), new LabeledIconButton(Lang.CONFIG_BUTTON_IMPORT, () -> {
            openImportDialog();
        }, this.font, importIcon, 7.0f, 8.0f, 5.5f, 12)));
        this.filterDropdown.onSelect(set -> {
            this.activeFilters.clear();
            this.activeFilters.addAll(set);
            this.filtersDirty = true;
        });
    }

    public void openExitConfirmDialog() {
        GlTextureObject warningIcon= new GlTextureObject(new ClasspathResource("/icons/menu/new/warning.png"));
        
        ActionDialogData data= new ActionDialogData(
            Translation.clearText("Выход из редактора"),
            Translation.clearText("Тема не создана. Изменения будут потеряны."),
            Translation.clearText("Выйти"),
            warningIcon,
            true,
            0xFF6E74E3,
            Translation.clearText("ВЫХОД"),
            null,
            () -> true,
            320.0f,
            () -> {
                this.creatorPanel.close();
                this.panelAnimator.state(false);
                Expensive.INSTANCE.menuWindow().actionConfirmationDialogContainer().close();
            }
        );
        Expensive.INSTANCE.menuWindow().actionConfirmationDialogContainer().open(data);
    }

    public void openImportDialog() {
        String[] importedId= new String[]{""};
        
        ActionDialogData data= new ActionDialogData(
            Translation.clearText("Импорт темы"),
            Translation.clearText("Cloud ID темы\nВведите Cloud ID существующей темы для импорта"),
            Translation.clearText("Импортировать"),
            importIcon,
            true,
            0xFF6E74E3,
            Translation.clearText("ИМПОРТ"),
            container -> {
                TextFieldSettingElement textField= new TextFieldSettingElement(
                    Translation.clearText(""),
                    null,
                    Translation.clearText("Cloud ID темы"),
                    false, -1, false, () -> ""
                );
                textField.onChange(val -> importedId[0] = val);
                container.addFrameElement(textField);
            },
            () -> true,
            320.0f,
            () -> {
                String id= importedId[0].trim();
                if (!id.isEmpty()) {
                    Theme importedTheme= Theme.of(
                        "import_" + System.currentTimeMillis(),
                        "Импортированная (" + id + ")",
                        Expensive.INSTANCE.userSession().username(),
                        ConfigOrigin.USER,
                        ThemeMode.DARK,
                        ThemeData.defaultDark().palette(),
                        Instant.now(),
                        Instant.now()
                    );
                    ThemeCard2 card= new ThemeCard2(new ThemeCard(importedTheme, Expensive.INSTANCE.userSession().texture()));
                    this.tab.frames().add(card);
                    this.tab.markFramesDirty();

                    if (Expensive.INSTANCE.configManager != null && Expensive.INSTANCE.configManager.themeConfig != null) {
                        Expensive.INSTANCE.configManager.themeConfig.customThemes.add(importedTheme);
                        Expensive.INSTANCE.configManager.saveThemes();
                    }

                    Expensive.INSTANCE.notificationRepository().post(
                        NotificationType.SUCCESS,
                        Text.literal("Тема с Cloud ID \"" + id + "\" успешно импортирована!"),
                        3L, TimeUnit.SECONDS
                    );
                }
                Expensive.INSTANCE.menuWindow().actionConfirmationDialogContainer().close();
            }
        );
        Expensive.INSTANCE.menuWindow().actionConfirmationDialogContainer().open(data);
    }

    @Override
    public void initialize(MenuTabElement class732Var) {
        super.initialize(class732Var);
        ScrollArea class789VarScrollingAreaComponent= class732Var.scrollingAreaComponent();
        Objects.requireNonNull(class789VarScrollingAreaComponent);
        Supplier supplier= class789VarScrollingAreaComponent::scrollY;
        Supplier supplier2= this::getContentHeight;
        Supplier supplier3= () -> {
            return Float.valueOf(((MenuWindow.MENU_HEIGHT - MenuWindow.COLLAPSED_HEADER_HEIGHT) - this.headerBounds.height()) - 16.0f);
        };
        ScrollArea class789VarScrollingAreaComponent2= class732Var.scrollingAreaComponent();
        Objects.requireNonNull(class789VarScrollingAreaComponent2);
        this.scrollbar = new ScrollbarWidget(supplier, supplier2, supplier3, (v1) -> {
            class789VarScrollingAreaComponent.scrollTo(v1);
        }, 10.0f, 3.0f, 24.0f);
    }

    @Override
    public void render(DrawCtx class699Var) throws MatchException {
        StylePalette class764VarPalette= class699Var.theme().palette();
        PaletteColorStack class115VarColorStack= class699Var.drawEngine().colorStack();
        class115VarColorStack.push();
        class115VarColorStack.alphaAnimation(this.tab.currentTabAnimation());
        this.filtersDirty |= this.lastFrameCount != this.tab.frames().size();
        this.tab.scrollingAreaComponent().beginArea(class699Var, this.tab.framesOriginX(), this.tab.framesOriginY() + az, width(), height() - az);
        class115VarColorStack.push();
        class115VarColorStack.alpha(1.0f - Math.max(this.panelAnimator.smoothAnimation() - 1.0f, aB));
        this.scrollbar.render(class699Var);
        for (int size = this.tab.frames().size() - 1; size >= 0; size--) {
            AbstractFrame class757Var= this.tab.frames().get(size);
            if (class757Var.visible() && isFrameVisible(class757Var, ax, originY() + az)) {
                class757Var.render(class699Var);
            }
        }
        this.tab.scrollingAreaComponent().endArea(class699Var, getContentHeight());
        renderOverlays(class699Var);
        applyFilters();
        this.titleBadge.render(class699Var);
        class699Var.text(this.font, Lang.THEMES_AVAILABLE.effective(), 16, this.headerBounds.x() + 10.0f, this.headerBounds.y() + this.titleBadge.height() + 8.0f, class115VarColorStack.computeColor(class764VarPalette.text().tone(300).argb()));
        renderFilterIndicators(class699Var);
        this.actionButtons.forEach(class747Var -> {
            class747Var.render(class699Var);
        });
        this.filterDropdown.render(class699Var);
        class115VarColorStack.pop();
        class115VarColorStack.push();
        class115VarColorStack.alpha(Math.max(this.panelAnimator.smoothAnimation() - 1.0f, aB));
        this.creatorPanel.render(class699Var);
        class115VarColorStack.pop();
        class115VarColorStack.pop();
    }

    @Override
    public void layout(LayoutScaleContext class698Var) {
        this.tab.frames().forEach(class757Var -> {
            class757Var.layout(class698Var);
        });
        this.creatorPanel.layout(class698Var);
        this.creatorPanel.setPosition(originX(), originY());
        this.headerBounds.withPosition(originX() + ay, originY() + 16.0f).withSize(((width() - 16.0f) - 3.0f) - 24.0f, 71.0f);
        this.titleBadge.layout(class698Var);
        this.titleBadge.setPosition(this.headerBounds.x() + 10.0f, this.headerBounds.y());
        layoutFilterDropdown(class698Var);
        layoutScrollbar(class698Var);
        this.actionButtons.forEach(class747Var -> {
            class747Var.layout(class698Var);
        });
        float fWidth= 0.0f;
        for (int size = this.actionButtons.size() - 1; size >= 0; size--) {
            LabeledIconButton class747Var2= this.actionButtons.get(size);
            fWidth += class747Var2.width() + 4.0f;
            class747Var2.setPosition(this.filterDropdown.x() - fWidth, this.filterDropdown.y());
        }
    }

    @Override
    public void animation(WeightedEngine class141Var) {
        this.filterDropdown.animation(class141Var);
        this.filterIndicators.forEach(class802Var -> {
            class802Var.animation().state(this.activeFilters.contains(class802Var.filter()));
            class802Var.animation().animate(class141Var);
        });
        this.panelAnimator.state(this.creatorPanel.isOpened());
        this.panelAnimator.animate(class141Var);
        this.creatorPanel.animation(class141Var);
        this.actionButtons.forEach(class747Var -> {
            class747Var.animation(class141Var);
        });
        this.scrollbar.animation(class141Var);
    }

    @Override
    public boolean handleInput(InputEventContext class688Var, boolean z) {
        boolean z2= z;
        if (this.creatorPanel.isOpened()) {
            ColorPickerWindow colorPickerWindow= Expensive.INSTANCE.windowController().getWindow(ColorPickerWindow.class);
            if (colorPickerWindow != null && colorPickerWindow.opened() && shouldYieldToColorPicker(class688Var, colorPickerWindow)) {
                return false;
            }
            InputEvent class691VarInputEvent= class688Var.inputEvent();
            if (class691VarInputEvent instanceof KeyInput class696Var) {
                if (class696Var.keyCode() == 256 && class696Var.keyAction().press()) {
                    openExitConfirmDialog();
                    return true;
                }
            }
            if (this.creatorPanel.handleInput(class688Var, z2)) {
                return true;
            }
            if (class688Var.inputEvent() instanceof MouseButtonInput) {
                z2 = true;
            }
        }
        boolean zHandleInput= z2 | this.scrollbar.handleInput(class688Var, z);
        boolean zHandleInput2= zHandleInput | this.filterDropdown.handleInput(class688Var, zHandleInput);
        boolean zAnyMatch= zHandleInput2 | this.actionButtons.stream().anyMatch(class747Var -> {
            return class747Var.handleInput(class688Var, zHandleInput2);
        });
        InputEventContext class688VarWithMouseOffset= class688Var.withMouseOffset(aB, this.tab.scrollingAreaComponent().scrollY());
        boolean zInArea= zAnyMatch | class688Var.inArea(this.headerBounds.x(), this.headerBounds.y(), this.headerBounds.width(), this.headerBounds.height());
        for (AbstractFrame class757Var : new ArrayList<>(this.tab.frames())) {
            if (class757Var.visible() && isFrameVisible(class757Var, ax, originY() + az)) {
                zInArea |= class757Var.handleInput(class688VarWithMouseOffset, zInArea);
            }
        }
        return zInArea | this.tab.scrollingAreaComponent().handleInput(class688Var, zInArea);
    }

    private boolean shouldYieldToColorPicker(InputEventContext class688Var, ColorPickerWindow colorPickerWindow) {
        return colorPickerWindow.draggingSV
            || colorPickerWindow.draggingHue
            || colorPickerWindow.draggingAlpha
            || colorPickerWindow.draggingSide
            || class688Var.inArea(colorPickerWindow.x(), colorPickerWindow.y(), colorPickerWindow.width(), colorPickerWindow.height());
    }

    @Override
    public float getContentHeight() {
        float f= 0.0f;
        for (AbstractFrame class757Var : new ArrayList<>(this.tab.frames())) {
            if (class757Var.visible()) {
                float fY= ((class757Var.y() - this.tab.framesOriginY()) - az) + class757Var.height() + class757Var.contentHeight();
                if (fY > f) {
                    f = fY;
                }
            }
        }
        return f + 10.0f;
    }

    @Override
    public void positionFrames() {
        List<AbstractFrame> list= this.tab.frames().stream().filter((v0) -> {
            return v0.visible();
        }).sorted((class757Var, class757Var2) -> {
            if (!(class757Var instanceof ThemeCard2)) {
                return 0;
            }
            ThemeCard2 class839Var= (ThemeCard2) class757Var;
            if (!(class757Var2 instanceof ThemeCard2)) {
                return 0;
            }
            ThemeCard2 class839Var2= (ThemeCard2) class757Var2;
            if (class839Var.favorite() != class839Var2.favorite()) {
                return class839Var.favorite() ? -1 : 1;
            }
            ConfigOrigin class763VarKind= class839Var.themeContext().kind();
            ConfigOrigin class763VarKind2= class839Var2.themeContext().kind();
            if (class763VarKind == ConfigOrigin.OFFICIAL && class763VarKind2 == ConfigOrigin.USER) {
                return -1;
            }
            return (class763VarKind == ConfigOrigin.USER && class763VarKind2 == ConfigOrigin.OFFICIAL) ? 1 : 0;
        }).toList();
        float fFloatValue= ((Float) list.stream().map((v0) -> {
            return v0.width();
        }).max((v0, v1) -> {
            return Float.compare(v0, v1);
        }).orElse(Float.valueOf(aB))).floatValue();
        int iMax= fFloatValue > aB ? Math.max(1, (int) Math.floor((((((width() - ay) - this.scrollbar.width()) - 24.0f) + 3.0f) + aA) / (fFloatValue + aA))) : 1;
        float[] fArr= new float[iMax];
        float[] fArr2= new float[iMax];
        for (int i = 0; i < iMax; i++) {
            fArr2[i] = i * (fFloatValue + aA);
        }
        for (AbstractFrame class757Var3 : list) {
            int iIndexOfMin= indexOfMin(fArr);
            float f= fArr2[iIndexOfMin];
            float f2= fArr[iIndexOfMin];
            float fHeight= class757Var3.height() + class757Var3.contentHeight();
            if (class757Var3 instanceof ThemeCard2) {
                ((ThemeCard2) class757Var3).targetPosition(originX() + ay + f, originY() + az + f2, !this.tab.forceImmediateFramePositioning());
            } else {
                class757Var3.setPosition(originX() + ay + f, originY() + az + f2);
            }
            fArr[iIndexOfMin] = fArr[iIndexOfMin] + fHeight + aB;
        }
    }

    @Override
    public float width() {
        return MenuWindow.MENU_WIDTH;
    }

    public void layoutScrollbar(LayoutScaleContext class698Var) {
        float f= MenuWindow.MENU_HEIGHT - MenuWindow.COLLAPSED_HEADER_HEIGHT;
        this.scrollbar.setPosition(((originX() + width()) - ay) - this.scrollbar.width(), this.headerBounds.y() + this.headerBounds.height());
        this.scrollbar.setSize(this.scrollbar.width(), f);
        this.scrollbar.layout(class698Var);
    }

    public void layoutFilterDropdown(LayoutScaleContext class698Var) {
        this.filterDropdown.setPosition((this.headerBounds.x() + this.headerBounds.width()) - this.filterDropdown.width(), (this.headerBounds.y() + (this.headerBounds.height() / 2.0f)) - (this.filterDropdown.height() / 2.0f));
        this.filterDropdown.layout(class698Var);
    }

    public void renderFilterIndicators(DrawCtx class699Var) throws MatchException {
        int iComputeColor;
        float fCeil= MathHelper.ceil(this.font.getHeight(11.0f));
        float fY= this.headerBounds.y() + this.titleBadge.height() + 8.0f + this.font.getHeight(16.0f) + ay;
        float fX= this.headerBounds.x() + 10.0f + 6.0f;
        PaletteColorStack class115VarColorStack= class699Var.drawEngine().colorStack();
        StylePalette class764VarPalette= class699Var.theme().palette();
        for (ThemeFilterIndicator class802Var : this.filterIndicators) {
            float fSmoothAnimation= class802Var.animation().smoothAnimation();
            float fCeil2= 11.0f + MathHelper.ceil(class699Var.textWidthPhysical(this.font, class802Var.label(), 11)) + 16.0f;
            if (fSmoothAnimation > 0.001f) {
                switch (class802Var.filter().ordinal()) {
                    case 0:
                        iComputeColor = class115VarColorStack.computeColor(class764VarPalette.favorite().argb());
                        break;
                    case 1:
                        iComputeColor = class115VarColorStack.computeColor(class764VarPalette.accentBright().argb());
                        break;
                    case 2:
                        iComputeColor = class115VarColorStack.computeColor(class764VarPalette.text().tone(600).argb());
                        break;
                    default:
                        throw new MatchException((String) null, (Throwable) null);
                }
                int i= iComputeColor;
                class115VarColorStack.push();
                class115VarColorStack.alpha(fSmoothAnimation);
                class699Var.outlineCircle(MathHelper.ceil(fX), MathHelper.ceil(fY + (fCeil / 2.0f)) - 1.0f, 5.0f, 2.5f, class115VarColorStack.computeColor(i), class115VarColorStack.computeColor(i, 0.2f));
                class699Var.text(this.font, class802Var.label(), 11, fX + 5.0f + 6.0f, fY, class115VarColorStack.computeColor(class764VarPalette.text().tone(500).argb()));
                class115VarColorStack.pop();
            }
            fX += fCeil2 * fSmoothAnimation;
        }
    }

    public void applyFilters() {
        if (this.filtersDirty) {
            for (AbstractFrame class757Var : new ArrayList<>(this.tab.frames())) {
                if (class757Var instanceof ThemeCard2) {
                    ThemeCard2 class839Var= (ThemeCard2) class757Var;
                    class839Var.updateFilterVisibility((this.activeFilters.contains(ConfigFilter.FAVORITE) && class839Var.favorite()) || (this.activeFilters.contains(ConfigFilter.OFFICIAL) && class839Var.themeContext().kind() == ConfigOrigin.OFFICIAL) || (this.activeFilters.contains(ConfigFilter.USER) && class839Var.themeContext().kind() == ConfigOrigin.USER));
                }
            }
            this.tab.markFramesDirty();
            this.filtersDirty = false;
            this.lastFrameCount = this.tab.frames().size();
        }
    }
}

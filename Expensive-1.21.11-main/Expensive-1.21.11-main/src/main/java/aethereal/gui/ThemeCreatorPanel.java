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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ThemeCreatorPanel extends WidgetContainer {
    public enum CategoryFilter {
        ALL("Все категории", null),
        TEXT("Текстовые", "text"),
        PRIMARY("Основные", "primary"),
        OUTLINE("Поверхность / Обводка", "outline"),
        SURFACE("Поверхность / Фон", "surface");

        private final String displayName;
        private final String categoryKey;

        CategoryFilter(String displayName, String categoryKey) {
            this.displayName = displayName;
            this.categoryKey = categoryKey;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getCategoryKey() {
            return categoryKey;
        }
    }

    public final GlTextureObject brushIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/brush.png"));
    public final GlTextureObject searchIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/search.png"));
    public final GlTextureObject enumIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/enum.png"));
    public final GlTextureObject checkmarkIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/checkmark.png"));
    public final GlTextureObject pencilIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/pencil.png"));
    public final GlTextureObject wandSparkleIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/wand_sparkle.png"));

    public final MsdfFont titleFont = Fonts.INTER_SEMIBOLD.get();
    public final MsdfFont boldFont = Fonts.INTER_BOLD.get();
    public final MsdfFont regularFont = Fonts.INTER_MEDIUM.get();

    public final WidgetBounds headerBounds = new WidgetBounds(0.0f, 0.0f, 0.0f, 46.0f);
    public final IconLabelBadge titleBadge = new IconLabelBadge(this.brushIcon, Lang.THEMES_TITLE);

    public final ScrollArea scrollArea = new ScrollArea();

    public boolean opened = false;
    public boolean previewMode = false;
    public String searchQuery = "";
    public String themeName = "Новая тема";
    
    boolean editingThemeName = false;
    public boolean editingSearch = false;
    public CategoryFilter selectedCategoryFilter = CategoryFilter.ALL;
    public final DropdownWidget<CategoryFilter> categoryDropdown = new DropdownWidget<>(Fonts.INTER_MEDIUM.get(), this.enumIcon, 6.0f, 8.0f, 5.5f, 12);

    public final Stopwatch cursorBlinkTimer = new Stopwatch();
    public boolean cursorState = true;
    public Theme originalTheme = null;
    public ColorSettingItem activeEditingItem = null;

    public static final int[] PRESET_COLORS = new int[] {
        0xFF4F5ED5, // Soft Indigo / Blue
        0xFFFDC95A, // Yellow / Gold
        0xFF50B47B, // Green
        0xFFED4561, // Crimson / Red
        0xFF4F9BE3, // Light Blue
        0xFFFFFFFF, // White
        0xFFB4B5BA  // Silver / Gray
    };

    public static class ColorSettingItem {
        final String nameKey;
        final String defaultName;
        final String category; // "text", "primary", "outline", "surface"
        int colorArgb;
        final int defaultArgb;
        String hexInputBuffer = "";

        float hue = 0.0f;
        float saturation = 1.0f;
        float brightness = 1.0f;
        float alpha = 1.0f;

        ColorSettingItem(String defaultName, String category, int defaultArgb) {
            this.nameKey = defaultName;
            this.defaultName = defaultName;
            this.category = category;
            this.colorArgb = defaultArgb;
            this.defaultArgb = defaultArgb;
            this.hexInputBuffer = hexString();
            syncHSBFromArgb();
        }

        void syncHSBFromArgb() {
            float[] hsb= java.awt.Color.RGBtoHSB(
                (colorArgb >> 16) & 0xFF,
                (colorArgb >> 8) & 0xFF,
                colorArgb & 0xFF,
                null
            );
            this.hue = hsb[0];
            this.saturation = hsb[1];
            this.brightness = hsb[2];
            int a= (colorArgb >> 24) & 0xFF;
            this.alpha = a / 255.0f;
        }

        public String hexString() {
            int alpha= (colorArgb >> 24) & 0xFF;
            if (alpha >= 255) {
                return String.format("#%06X", (0xFFFFFF & colorArgb));
            }
            return String.format("#%08X", colorArgb);
        }
    }

    public final List<ColorSettingItem> textColors = new ArrayList<>();
    public final List<ColorSettingItem> primaryColors = new ArrayList<>();
    public final List<ColorSettingItem> surfaceColors = new ArrayList<>();

    public ThemeCreatorPanel() {
        // COLUMN 1: Текстовые (10 items)
        textColors.add(new ColorSettingItem("Оттенок - 900", "text", 0xFF313133));
        textColors.add(new ColorSettingItem("Оттенок - 800", "text", 0xFF505155));
        textColors.add(new ColorSettingItem("Оттенок - 700", "text", 0xFF606166));
        textColors.add(new ColorSettingItem("Оттенок - 600", "text", 0xFF76777E));
        textColors.add(new ColorSettingItem("Оттенок - 500", "text", 0xFF868791));
        textColors.add(new ColorSettingItem("Оттенок - 400", "text", 0xFFB4B5BA));
        textColors.add(new ColorSettingItem("Оттенок - 300", "text", 0xFFC5C6C8));
        textColors.add(new ColorSettingItem("Оттенок - 200", "text", 0xFFDADCE2));
        textColors.add(new ColorSettingItem("Оттенок - 100", "text", 0xFFE3E4E7));
        textColors.add(new ColorSettingItem("Оттенок - 50",  "text", 0xFFF0F1F4));

        // COLUMN 2: Основные & Поверхность / Обводка (14 items total)
        primaryColors.add(new ColorSettingItem("Акцент", "primary", 0xFF6E74E3));
        primaryColors.add(new ColorSettingItem("Яркий акцент", "primary", 0xFF8186EA));
        primaryColors.add(new ColorSettingItem("Избранное", "primary", 0xFFFDC95A));
        primaryColors.add(new ColorSettingItem("Фон фрейма", "primary", 0xFF151617));
        primaryColors.add(new ColorSettingItem("Тень", "primary", 0xFF000000));
        primaryColors.add(new ColorSettingItem("Ошибка - 900", "primary", 0xFFED4561));
        primaryColors.add(new ColorSettingItem("Ошибка - 500", "primary", 0xFFEE5871));
        primaryColors.add(new ColorSettingItem("Ошибка - 300", "primary", 0xFFEF6179));
        // Sub-section: Поверхность / Обводка
        primaryColors.add(new ColorSettingItem("Оттенок - 700", "outline", 0xFF17181A));
        primaryColors.add(new ColorSettingItem("Оттенок - 600", "outline", 0xFF1A1B1E));
        primaryColors.add(new ColorSettingItem("Оттенок - 500", "outline", 0xFF202123));
        primaryColors.add(new ColorSettingItem("Оттенок - 400", "outline", 0xFF222325));
        primaryColors.add(new ColorSettingItem("Оттенок - 300", "outline", 0xFF282A2E));
        primaryColors.add(new ColorSettingItem("Оттенок - 50",  "outline", 0xFF6E7279));

        // COLUMN 3: Поверхность / Фон (9 items)
        surfaceColors.add(new ColorSettingItem("Оттенок - 900", "surface", 0xFF0F1011));
        surfaceColors.add(new ColorSettingItem("Оттенок - 801", "surface", 0xFF121315));
        surfaceColors.add(new ColorSettingItem("Оттенок - 800", "surface", 0xFF17181A));
        surfaceColors.add(new ColorSettingItem("Оттенок - 700", "surface", 0xFF18191A));
        surfaceColors.add(new ColorSettingItem("Оттенок - 600", "surface", 0xFF1A1B1D));
        surfaceColors.add(new ColorSettingItem("Оттенок - 500", "surface", 0xFF1E1F22));
        surfaceColors.add(new ColorSettingItem("Оттенок - 400", "surface", 0xFF26272A));
        surfaceColors.add(new ColorSettingItem("Оттенок - 300", "surface", 0xFF2D2E31));
        surfaceColors.add(new ColorSettingItem("Оттенок - 200", "surface", 0xFF565659));

        List<DropdownOption<CategoryFilter>> options= List.of(
            new DropdownOption<>(CategoryFilter.ALL, Translation.clearText("Все категории")),
            new DropdownOption<>(CategoryFilter.TEXT, Translation.clearText("Текстовые")),
            new DropdownOption<>(CategoryFilter.PRIMARY, Translation.clearText("Основные")),
            new DropdownOption<>(CategoryFilter.OUTLINE, Translation.clearText("Поверхность / Обводка")),
            new DropdownOption<>(CategoryFilter.SURFACE, Translation.clearText("Поверхность / Фон"))
        );
        this.categoryDropdown.setOptions(options, CategoryFilter.ALL);
        this.categoryDropdown.onSelect(filter -> {
            this.selectedCategoryFilter = filter;
        });
        addChild(this.categoryDropdown);
    }

    private void updateBlinkTimer() {
        if (cursorBlinkTimer.hasElapsed(500L)) {
            cursorState = !cursorState;
            cursorBlinkTimer.reset();
        }
    }

    @Override
    public void render(DrawCtx class699Var) {
        if (!this.opened) return;

        updateBlinkTimer();
        StylePalette palette= class699Var.theme().palette();
        PaletteColorStack colorStack= class699Var.drawEngine().colorStack();

        // Header & Badge
        this.titleBadge.render(class699Var);
        class699Var.text(this.titleFont, Translation.clearText("Создание цветовой темы").effective(), 16, 
            this.headerBounds.x(), this.headerBounds.y() + this.titleBadge.height() + 8.0f, 
            colorStack.computeColor(palette.text().tone(300).argb()));

        // Top Action Controls on Right Side (level with title)
        float topControlY= y() + 28.0f;
        float rightX= x() + width() - 25.0f;

        // Preview Label & Toggle
        class699Var.text(this.regularFont, "Превью", 12, rightX - 85.0f, topControlY + 5.0f, colorStack.computeColor(palette.text().tone(400).argb()));
        class699Var.fillRoundedRect(rightX - 35.0f, topControlY + 4.0f, 30.0f, 16.0f, 8.0f, colorStack.computeColor(previewMode ? palette.accent().argb() : palette.surfaceBackground().tone(600).argb()));
        class699Var.circle(previewMode ? rightX - 13.0f : rightX - 27.0f, topControlY + 12.0f, 6.0f, colorStack.computeColor(previewMode ? 0xFFFFFFFF : 0xFF666666));

        // Create Button ("❇ Создать")
        float createBtnX= rightX - 185.0f;
        class699Var.fillRoundedRect(createBtnX, topControlY, 88.0f, 26.0f, 6.0f, colorStack.computeColor(palette.accent().argb()));
        class699Var.texture(this.wandSparkleIcon, createBtnX + 10.0f, topControlY + 7.0f, 12.0f, 12.0f, colorStack.computeColor(0xFFFFFFFF));
        class699Var.text(this.boldFont, "Создать", 12, createBtnX + 26.0f, topControlY + 6.0f, colorStack.computeColor(0xFFFFFFFF));

        // Theme Name Input Box ("✏ Новая тема" / "Expensive Violet")
        float nameBoxX= createBtnX - 170.0f;
        int nameOutlineColor= editingThemeName ? palette.accent().argb() : palette.surfaceOutline().tone(600).argb();
        class699Var.fillOutlinedRoundedRect(nameBoxX, topControlY, 160.0f, 26.0f, 6.0f, 1.5f, 
            colorStack.computeColor(nameOutlineColor), 
            colorStack.computeColor(palette.surfaceBackground().tone(800).argb()));
        
        class699Var.texture(this.pencilIcon, nameBoxX + 10.0f, topControlY + 7.0f, 12.0f, 12.0f, 
            colorStack.computeColor(editingThemeName ? palette.accent().argb() : palette.text().tone(500).argb()));
        
        String displayName= this.themeName.isEmpty() ? "Название темы" : this.themeName;
        int displayTextColor= this.themeName.isEmpty() ? palette.text().tone(600).argb() : palette.text().tone(100).argb();
        class699Var.text(this.regularFont, displayName, 12, nameBoxX + 26.0f, topControlY + 6.0f, colorStack.computeColor(displayTextColor));

        if (editingThemeName && cursorState) {
            float cursorX= nameBoxX + 26.0f + (this.themeName.isEmpty() ? 0 : this.regularFont.getWidth(this.themeName, 12));
            class699Var.fillRect(cursorX + 1.0f, topControlY + 6.0f, 1.5f, 13.0f, colorStack.computeColor(palette.accent().argb()));
        }

        // Second Row: Search Bar
        float searchY= y() + 72.0f;
        float searchWidth= width() - 280.0f;
        int searchOutlineColor= editingSearch ? palette.accent().argb() : palette.surfaceOutline().tone(600).argb();
        
        class699Var.fillOutlinedRoundedRect(x() + 20.0f, searchY, searchWidth, 26.0f, 6.0f, 1.5f,
            colorStack.computeColor(searchOutlineColor),
            colorStack.computeColor(palette.surfaceBackground().tone(800).argb()));
        
        class699Var.texture(this.searchIcon, x() + 28.0f, searchY + 7.0f, 12.0f, 12.0f, 
            colorStack.computeColor(editingSearch ? palette.accent().argb() : palette.text().tone(600).argb()));
        
        String searchDisplay= searchQuery.isEmpty() ? "Найти цвет по названию" : searchQuery;
        int searchTextColor= searchQuery.isEmpty() ? palette.text().tone(600).argb() : palette.text().tone(100).argb();
        class699Var.text(this.regularFont, searchDisplay, 12, 
            x() + 46.0f, searchY + 6.0f, colorStack.computeColor(searchTextColor));

        if (editingSearch && cursorState) {
            float searchCursorX= x() + 46.0f + (searchQuery.isEmpty() ? 0 : this.regularFont.getWidth(searchQuery, 12));
            class699Var.fillRect(searchCursorX + 1.0f, searchY + 6.0f, 1.5f, 13.0f, colorStack.computeColor(palette.accent().argb()));
        }

        if (!searchQuery.isEmpty()) {
            class699Var.text(this.boldFont, "✕", 11, x() + 20.0f + searchWidth - 18.0f, searchY + 6.0f, colorStack.computeColor(palette.text().tone(400).argb()));
        }

        // Scrollable Area for 3 Columns of Settings
        float scrollStartY= searchY + 36.0f;
        float scrollAreaHeight= y() + height() - scrollStartY - 10.0f;
        float colWidth= (width() - 80.0f) / 3.0f;

        this.scrollArea.beginArea(class699Var, x() + 10.0f, scrollStartY, width() - 20.0f, scrollAreaHeight);

        // Compute total heights for each column inside scroll view
        float h1= renderColumn(class699Var, "Текстовые", textColors, x() + 20.0f, scrollStartY, colWidth, palette, colorStack);
        float h2= renderColumn2WithSubheader(class699Var, "Основные", "Поверхность / Обводка", primaryColors, x() + 35.0f + colWidth, scrollStartY, colWidth, palette, colorStack);
        float h3= renderColumn(class699Var, "Поверхность / Фон", surfaceColors, x() + 50.0f + colWidth * 2.0f, scrollStartY, colWidth, palette, colorStack);

        float maxContentHeight= Math.max(h1, Math.max(h2, h3)) - scrollStartY + 20.0f;
        this.scrollArea.endArea(class699Var, maxContentHeight);

        // Render official DropdownWidget after scroll area so popup appears on top
        this.categoryDropdown.render(class699Var);

        super.render(class699Var);
    }

    private boolean shouldShowItem(ColorSettingItem item) {
        if (!searchQuery.isEmpty() && !item.defaultName.toLowerCase().contains(searchQuery.toLowerCase())) {
            return false;
        }
        if (selectedCategoryFilter != CategoryFilter.ALL) {
            String filterKey= selectedCategoryFilter.getCategoryKey();
            if (filterKey != null && !filterKey.equals(item.category)) {
                return false;
            }
        }
        return true;
    }

    private float renderColumn(DrawCtx ctx, String headerTitle, List<ColorSettingItem> items, float startX, float startY, float colWidth, StylePalette palette, PaletteColorStack colorStack) {
        ctx.text(this.boldFont, headerTitle, 13, startX, startY, colorStack.computeColor(palette.text().tone(200).argb()));
        
        float currentY= startY + 22.0f;
        for (ColorSettingItem item : items) {
            if (!shouldShowItem(item)) {
                continue;
            }

            renderItemCard(ctx, item, startX, currentY, colWidth, palette, colorStack);
            currentY += 56.0f;
        }
        return currentY;
    }

    private float renderColumn2WithSubheader(DrawCtx ctx, String header1, String header2, List<ColorSettingItem> items, float startX, float startY, float colWidth, StylePalette palette, PaletteColorStack colorStack) {
        ctx.text(this.boldFont, header1, 13, startX, startY, colorStack.computeColor(palette.text().tone(200).argb()));
        
        float currentY= startY + 22.0f;
        boolean subHeaderRendered= false;

        for (ColorSettingItem item : items) {
            if (!shouldShowItem(item)) {
                continue;
            }

            if ("outline".equals(item.category) && !subHeaderRendered) {
                currentY += 10.0f;
                ctx.text(this.boldFont, header2, 13, startX, currentY, colorStack.computeColor(palette.text().tone(200).argb()));
                currentY += 22.0f;
                subHeaderRendered = true;
            }

            renderItemCard(ctx, item, startX, currentY, colWidth, palette, colorStack);
            currentY += 56.0f;
        }
        return currentY;
    }

    private void renderItemCard(DrawCtx ctx, ColorSettingItem item, float startX, float currentY, float colWidth, StylePalette palette, PaletteColorStack colorStack) {
        // Title row: Left name, Right current color dot + Reset icon "↺"
        ctx.text(this.boldFont, item.defaultName, 12, startX, currentY, colorStack.computeColor(palette.text().tone(100).argb()));
        
        // Current color circle indicator
        ctx.circle(startX + colWidth - 32.0f, currentY + 6.0f, 5.0f, colorStack.computeColor(item.colorArgb));
        
        // Reset button icon "↺"
        ctx.text(this.boldFont, "↺", 13, startX + colWidth - 16.0f, currentY + 1.0f, colorStack.computeColor(palette.text().tone(400).argb()));
        
        // Card container for color selector
        float cardY= currentY + 18.0f;
        float cardHeight= 28.0f;
        ctx.fillOutlinedRoundedRect(startX, cardY, colWidth, cardHeight, 6.0f, 1.5f,
            colorStack.computeColor(palette.surfaceOutline().tone(600).argb()),
            colorStack.computeColor(palette.surfaceBackground().tone(800).argb()));

        // Section 1: Brush icon button
        ctx.texture(this.brushIcon, startX + 8.0f, cardY + 7.0f, 14.0f, 14.0f, colorStack.computeColor(palette.accentBright().argb()));
        
        // Divider 1
        ctx.fillRect(startX + 28.0f, cardY + 4.0f, 1.0f, 20.0f, colorStack.computeColor(palette.surfaceOutline().tone(600).argb()));
        
        // Section 2: Preset color dots (7 colors)
        float dotStartX= startX + 36.0f;
        for (int i = 0; i < PRESET_COLORS.length; i++) {
            int presetColor= PRESET_COLORS[i];
            float dotX= dotStartX + i * 16.0f;
            float dotY= cardY + 14.0f;
            
            ctx.circle(dotX, dotY, 5.0f, colorStack.computeColor(presetColor));
            
            // Show checkmark if matches active color
            if ((item.colorArgb & 0xFFFFFF) == (presetColor & 0xFFFFFF)) {
                ctx.texture(this.checkmarkIcon, dotX - 4.0f, dotY - 4.0f, 8.0f, 8.0f, colorStack.computeColor(0xFF000000));
            }
        }

        // Divider 2
        float div2X= startX + colWidth - 65.0f;
        ctx.fillRect(div2X, cardY + 4.0f, 1.0f, 20.0f, colorStack.computeColor(palette.surfaceOutline().tone(600).argb()));

        // Section 3: HEX string text display
        boolean isEditingThisHex= (item == this.activeEditingItem);
        String hexStr= isEditingThisHex ? item.hexInputBuffer : item.hexString();
        int hexTextColor= isEditingThisHex ? palette.accent().argb() : palette.text().tone(200).argb();
        
        if (isEditingThisHex) {
            ctx.fillRoundedRect(div2X + 2.0f, cardY + 3.0f, colWidth - (div2X - startX) - 4.0f, cardHeight - 6.0f, 4.0f,
                colorStack.computeColor(palette.surfaceBackground().tone(700).argb()));
        }
        
        ctx.text(this.boldFont, hexStr, 10, div2X + 6.0f, cardY + 9.0f, colorStack.computeColor(hexTextColor));
        
        if (isEditingThisHex && cursorState) {
            float curX= div2X + 6.0f + this.boldFont.getWidth(hexStr, 10);
            ctx.fillRect(curX + 1.0f, cardY + 8.0f, 1.0f, 11.0f, colorStack.computeColor(palette.accent().argb()));
        }
    }

    @Override
    public boolean handleInput(InputEventContext class688Var, boolean z) {
        if (!this.opened) return false;

        InputEvent class691VarInputEvent= class688Var.inputEvent();
        boolean hasCtrl= net.minecraft.client.util.InputUtil.isKeyPressed(Mc.INSTANCE.getWindow(), net.minecraft.client.util.InputUtil.GLFW_KEY_LEFT_CONTROL) ||
                          net.minecraft.client.util.InputUtil.isKeyPressed(Mc.INSTANCE.getWindow(), net.minecraft.client.util.InputUtil.GLFW_KEY_RIGHT_CONTROL);

        // Category Dropdown input handling takes priority when open
        if (this.categoryDropdown.handleInput(class688Var, z)) {
            return true;
        }

        // Keyboard Typing Handler
        if (this.editingThemeName) {
            if (class691VarInputEvent instanceof CharInput charInput) {
                if (this.themeName.length() < 25) {
                    this.themeName += (char) charInput.codePoint();
                    return true;
                }
            }
            if (class691VarInputEvent instanceof KeyInput keyInput) {
                if (keyInput.keyAction().press() || keyInput.keyAction().repeat()) {
                    if (keyInput.keyCode() == 259 && !this.themeName.isEmpty()) {
                        this.themeName = this.themeName.substring(0, this.themeName.length() - 1);
                        return true;
                    }
                    if (keyInput.keyCode() == 257 || keyInput.keyCode() == 335 || keyInput.keyCode() == 256) {
                        this.editingThemeName = false;
                        return true;
                    }
                    if (keyInput.keyCode() == 86 && hasCtrl) {
                        String clip= Mc.INSTANCE.getMinecraft().keyboard.getClipboard();
                        if (clip != null && !clip.isEmpty()) {
                            this.themeName = (this.themeName + clip);
                            if (this.themeName.length() > 25) this.themeName = this.themeName.substring(0, 25);
                        }
                        return true;
                    }
                }
            }
        }

        if (this.editingSearch) {
            if (class691VarInputEvent instanceof CharInput charInput) {
                this.searchQuery += (char) charInput.codePoint();
                return true;
            }
            if (class691VarInputEvent instanceof KeyInput keyInput) {
                if (keyInput.keyAction().press() || keyInput.keyAction().repeat()) {
                    if (keyInput.keyCode() == 259 && !this.searchQuery.isEmpty()) {
                        this.searchQuery = this.searchQuery.substring(0, this.searchQuery.length() - 1);
                        return true;
                    }
                    if (keyInput.keyCode() == 257 || keyInput.keyCode() == 335 || keyInput.keyCode() == 256) {
                        this.editingSearch = false;
                        return true;
                    }
                    if (keyInput.keyCode() == 86 && hasCtrl) {
                        String clip= Mc.INSTANCE.getMinecraft().keyboard.getClipboard();
                        if (clip != null && !clip.isEmpty()) {
                            this.searchQuery += clip;
                        }
                        return true;
                    }
                }
            }
        }

        if (this.activeEditingItem != null) {
            if (class691VarInputEvent instanceof CharInput charInput) {
                char ch= (char) charInput.codePoint();
                if (ch == '#' || (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F')) {
                    if (this.activeEditingItem.hexInputBuffer.length() < 9) {
                        this.activeEditingItem.hexInputBuffer += ch;
                        tryParseHexAndUpdate(this.activeEditingItem);
                        return true;
                    }
                }
            }
            if (class691VarInputEvent instanceof KeyInput keyInput) {
                if (keyInput.keyAction().press() || keyInput.keyAction().repeat()) {
                    if (keyInput.keyCode() == 259 && !this.activeEditingItem.hexInputBuffer.isEmpty()) {
                        this.activeEditingItem.hexInputBuffer = this.activeEditingItem.hexInputBuffer.substring(0, this.activeEditingItem.hexInputBuffer.length() - 1);
                        tryParseHexAndUpdate(this.activeEditingItem);
                        return true;
                    }
                    if (keyInput.keyCode() == 257 || keyInput.keyCode() == 335 || keyInput.keyCode() == 256) {
                        commitHexEdit(this.activeEditingItem);
                        this.activeEditingItem = null;
                        Expensive.INSTANCE.windowController().closeColorPickers();
                        return true;
                    }
                    if (keyInput.keyCode() == 86 && hasCtrl) {
                        String clip= Mc.INSTANCE.getMinecraft().keyboard.getClipboard();
                        if (clip != null && !clip.isEmpty()) {
                            this.activeEditingItem.hexInputBuffer = clip.startsWith("#") ? clip : "#" + clip;
                            tryParseHexAndUpdate(this.activeEditingItem);
                        }
                        return true;
                    }
                }
            }
        }

        if (this.scrollArea.handleInput(class688Var, z)) {
            return true;
        }

        if (class691VarInputEvent instanceof MouseButtonInput mouseInput && mouseInput.button() == 0 && mouseInput.action().press()) {
            float topControlY= y() + 28.0f;
            float rightX= x() + width() - 25.0f;
            float createBtnX= rightX - 185.0f;
            float nameBoxX= createBtnX - 170.0f;
            float searchY= y() + 72.0f;
            float searchWidth= width() - 280.0f;

            // Click Theme Name Input Box
            if (class688Var.inArea(nameBoxX, topControlY, 160.0f, 26.0f)) {
                this.editingThemeName = true;
                this.editingSearch = false;
                this.activeEditingItem = null;
                return true;
            } else {
                this.editingThemeName = false;
            }

            // Click Search Bar
            if (class688Var.inArea(x() + 20.0f, searchY, searchWidth, 26.0f)) {
                if (!searchQuery.isEmpty() && class688Var.inArea(x() + 20.0f + searchWidth - 25.0f, searchY, 25.0f, 26.0f)) {
                    this.searchQuery = "";
                }
                this.editingSearch = true;
                this.editingThemeName = false;
                this.activeEditingItem = null;
                return true;
            } else {
                this.editingSearch = false;
            }

            // Click Create Button
            if (class688Var.inArea(createBtnX, topControlY, 88.0f, 26.0f)) {
                createTheme();
                return true;
            }

            // Click Preview Toggle
            if (class688Var.inArea(rightX - 35.0f, topControlY + 4.0f, 30.0f, 16.0f)) {
                this.previewMode = !this.previewMode;
                if (this.previewMode) {
                    applyRealtimeTheme();
                } else if (this.originalTheme != null) {
                    Expensive.INSTANCE.theme = this.originalTheme;
                }
                return true;
            }

            // Handle Clicks in Color Settings Columns (considering scroll offset)
            float scrollStartY= searchY + 36.0f;
            float scrollOffset= this.scrollArea.scrollY();
            float colY= scrollStartY - scrollOffset;
            float colWidth= (width() - 80.0f) / 3.0f;

            if (checkColumnClick(class688Var, textColors, x() + 20.0f, colY, colWidth, false)) return true;
            if (checkColumnClick(class688Var, primaryColors, x() + 35.0f + colWidth, colY, colWidth, true)) return true;
            if (checkColumnClick(class688Var, surfaceColors, x() + 50.0f + colWidth * 2.0f, colY, colWidth, false)) return true;
        }

        return super.handleInput(class688Var, z);
    }

    private void tryParseHexAndUpdate(ColorSettingItem item) {
        try {
            String hex= item.hexInputBuffer.replace("#", "").trim();
            if (hex.length() == 6 || hex.length() == 8) {
                int color= (int) Long.parseLong(hex, 16);
                if (hex.length() == 6) {
                    color |= 0xFF000000;
                }
                color = normalizeThemeAlpha(color);
                item.colorArgb = color;
                item.syncHSBFromArgb();
                if (this.previewMode) {
                    applyRealtimeTheme();
                }
            }
        } catch (Exception ignored) {}
    }

    private void commitHexEdit(ColorSettingItem item) {
        tryParseHexAndUpdate(item);
        item.hexInputBuffer = item.hexString();
    }

    private boolean checkColumnClick(InputEventContext ctx, List<ColorSettingItem> items, float startX, float startY, float colWidth, boolean hasSubheader) {
        float currentY= startY + 22.0f;
        boolean subHeaderRendered= false;

        for (ColorSettingItem item : items) {
            if (!shouldShowItem(item)) {
                continue;
            }

            if (hasSubheader && "outline".equals(item.category) && !subHeaderRendered) {
                currentY += 32.0f;
                subHeaderRendered = true;
            }

            float cardY= currentY + 18.0f;
            float cardHeight= 28.0f;
            float div2X= startX + colWidth - 65.0f;

            // Check HEX string box click
            if (ctx.inArea(div2X, cardY, colWidth - (div2X - startX), cardHeight)) {
                InputEvent input= ctx.inputEvent();
                if (input instanceof MouseButtonInput mouseInput && mouseInput.action().press()) {
                    if (mouseInput.button() == 0) { // Left click: Start keyboard editing HEX directly
                        this.activeEditingItem = item;
                        item.hexInputBuffer = item.hexString();
                        this.editingThemeName = false;
                        this.editingSearch = false;
                    } else if (mouseInput.button() == 1) { // Right click: Paste from clipboard directly
                        String clipboard= Mc.INSTANCE.getMinecraft().keyboard.getClipboard();
                        if (clipboard != null && !clipboard.isEmpty()) {
                            try {
                                if (clipboard.startsWith("#")) clipboard = clipboard.substring(1);
                                if (clipboard.length() == 6 || clipboard.length() == 8) {
                                    int color= (int) Long.parseLong(clipboard, 16);
                                    if (clipboard.length() == 6) {
                                        color |= 0xFF000000;
                                    }
                                    color = normalizeThemeAlpha(color);
                                    item.colorArgb = color;
                                    item.hexInputBuffer = item.hexString();
                                    item.syncHSBFromArgb();
                                    if (this.previewMode) applyRealtimeTheme();
                                    Expensive.INSTANCE.notificationRepository().post(
                                        NotificationType.SUCCESS,
                                        Text.literal("Цвет вставлен!"),
                                        3L, TimeUnit.SECONDS
                                    );
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                    return true;
                }
            }

            // Check Reset Icon Click "↺"
            if (ctx.inArea(startX + colWidth - 22.0f, currentY, 20.0f, 16.0f)) {
                if (ctx.inputEvent() instanceof MouseButtonInput mouseInput && mouseInput.button() == 0 && mouseInput.action().press()) {
                    item.colorArgb = item.defaultArgb;
                    item.hexInputBuffer = item.hexString();
                    item.syncHSBFromArgb();
                    if (this.previewMode) applyRealtimeTheme();
                    return true;
                }
            }
            
            // Check card click (to set preset or open color picker)
            if (ctx.inArea(startX, cardY, colWidth, cardHeight)) {
                InputEvent input= ctx.inputEvent();
                if (input instanceof MouseButtonInput mouseInput && mouseInput.button() == 0 && mouseInput.action().press()) {
                    boolean hitDot= false;
                    float dotStartX= startX + 36.0f;
                    for (int i = 0; i < PRESET_COLORS.length; i++) {
                        float dotX= dotStartX + i * 16.0f;
                        if (ctx.inArea(dotX - 7.0f, cardY, 15.0f, cardHeight)) {
                            item.colorArgb = PRESET_COLORS[i];
                            item.hexInputBuffer = item.hexString();
                            item.syncHSBFromArgb();
                            if (this.previewMode) applyRealtimeTheme();
                            hitDot = true;
                            return true;
                        }
                    }
                    if (!hitDot) {
                        openColorPicker(item);
                        return true;
                    }
                }
            }

            currentY += 56.0f;
        }
        return false;
    }

    private void openColorPicker(ColorSettingItem item) {
        ColorPickerWindow existingPicker= (ColorPickerWindow) Expensive.INSTANCE.windowController().getWindow(ColorPickerWindow.class);
        boolean wasSelf= (existingPicker != null && this.activeEditingItem == item);
        Expensive.INSTANCE.windowController().closeColorPickers();
        if (wasSelf) {
            this.activeEditingItem = null;
            return;
        }
        this.activeEditingItem = item;
        item.syncHSBFromArgb();

        ColorPickerWindow newPicker= new ColorPickerWindow(item.hue, item.saturation, item.brightness, item.alpha);
        Expensive.INSTANCE.windowController().newWindow(newPicker);

        newPicker.colorConsumer(num -> {
            int rgb= num.intValue();
            float[] hsb= java.awt.Color.RGBtoHSB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, null);
            item.hue = hsb[0];
            item.saturation = hsb[1];
            item.brightness = hsb[2];
            int alphaInt= normalizedAlphaByte(item.alpha);
            item.colorArgb = (alphaInt << 24) | (rgb & 0xFFFFFF);
            item.hexInputBuffer = item.hexString();
            if (this.previewMode) applyRealtimeTheme();
        });

        newPicker.alphaConsumer(f -> {
            item.alpha = f.floatValue();
            int alphaInt= normalizedAlphaByte(item.alpha);
            int rgb= java.awt.Color.HSBtoRGB(item.hue, item.saturation, item.brightness) & 0xFFFFFF;
            item.colorArgb = (alphaInt << 24) | rgb;
            item.hexInputBuffer = item.hexString();
            if (this.previewMode) applyRealtimeTheme();
        });

        newPicker.openPicker();
    }

    private int normalizedAlphaByte(float alpha) {
        int value= Math.round(Math.max(0.0f, Math.min(1.0f, alpha)) * 255.0f);
        return value <= 0 ? 1 : value;
    }

    private int normalizeThemeAlpha(int color) {
        return ((color >>> 24) == 0) ? (color | 0x01000000) : color;
    }

    public void applyRealtimeTheme() {
        StylePalette palette= buildPaletteFromSettings();
        Instant now= Instant.now();
        Theme activeCustomTheme= Theme.of(
            "custom_preview",
            this.themeName.isEmpty() ? "Новая тема" : this.themeName,
            Expensive.INSTANCE.userSession().username(),
            ConfigOrigin.USER,
            ThemeMode.DARK,
            palette,
            now,
            now
        );
        Expensive.INSTANCE.theme = activeCustomTheme;
    }

    private StylePalette buildPaletteFromSettings() {
        Map<Integer, ColorValue> textMap = new HashMap<>();
        textMap.put(900, new ColorValue(getItemColor(textColors, 0, 0xFF313133)));
        textMap.put(800, new ColorValue(getItemColor(textColors, 1, 0xFF505155)));
        textMap.put(700, new ColorValue(getItemColor(textColors, 2, 0xFF606166)));
        textMap.put(600, new ColorValue(getItemColor(textColors, 3, 0xFF76777E)));
        textMap.put(500, new ColorValue(getItemColor(textColors, 4, 0xFF868791)));
        textMap.put(400, new ColorValue(getItemColor(textColors, 5, 0xFFB4B5BA)));
        textMap.put(300, new ColorValue(getItemColor(textColors, 6, 0xFFC5C6C8)));
        textMap.put(200, new ColorValue(getItemColor(textColors, 7, 0xFFDADCE2)));
        textMap.put(100, new ColorValue(getItemColor(textColors, 8, 0xFFE3E4E7)));
        textMap.put(50,  new ColorValue(getItemColor(textColors, 9, 0xFFF0F1F4)));

        Map<Integer, ColorValue> errorMap = new HashMap<>();
        errorMap.put(900, new ColorValue(getItemColor(primaryColors, 5, 0xFFED4561)));
        errorMap.put(500, new ColorValue(getItemColor(primaryColors, 6, 0xFFEE5871)));
        errorMap.put(300, new ColorValue(getItemColor(primaryColors, 7, 0xFFEF6179)));

        Map<Integer, ColorValue> outlineMap = new HashMap<>();
        outlineMap.put(700, new ColorValue(getItemColor(primaryColors, 8, 0xFF17181A)));
        outlineMap.put(600, new ColorValue(getItemColor(primaryColors, 9, 0xFF1A1B1E)));
        outlineMap.put(500, new ColorValue(getItemColor(primaryColors, 10, 0xFF202123)));
        outlineMap.put(400, new ColorValue(getItemColor(primaryColors, 11, 0xFF222325)));
        outlineMap.put(300, new ColorValue(getItemColor(primaryColors, 12, 0xFF282A2E)));
        outlineMap.put(50,  new ColorValue(getItemColor(primaryColors, 13, 0xFF6E7279)));

        Map<Integer, ColorValue> surfaceMap = new HashMap<>();
        surfaceMap.put(900, new ColorValue(getItemColor(surfaceColors, 0, 0xFF0F1011)));
        surfaceMap.put(801, new ColorValue(getItemColor(surfaceColors, 1, 0xFF121315)));
        surfaceMap.put(800, new ColorValue(getItemColor(surfaceColors, 2, 0xFF17181A)));
        surfaceMap.put(700, new ColorValue(getItemColor(surfaceColors, 3, 0xFF18191A)));
        surfaceMap.put(600, new ColorValue(getItemColor(surfaceColors, 4, 0xFF1A1B1D)));
        surfaceMap.put(500, new ColorValue(getItemColor(surfaceColors, 5, 0xFF1E1F22)));
        surfaceMap.put(400, new ColorValue(getItemColor(surfaceColors, 6, 0xFF26272A)));
        surfaceMap.put(300, new ColorValue(getItemColor(surfaceColors, 7, 0xFF2D2E31)));
        surfaceMap.put(200, new ColorValue(getItemColor(surfaceColors, 8, 0xFF565659)));

        return new StylePalette(
            new ColorValue(getItemColor(primaryColors, 0, 0xFF6E74E3)),
            new ColorValue(getItemColor(primaryColors, 1, 0xFF8186EA)),
            new ColorValue(getItemColor(primaryColors, 2, 0xFFFDC95A)),
            new ColorValue(getItemColor(primaryColors, 3, 0xFF151617)),
            new ColorToneScale(textMap),
            new ColorToneScale(errorMap),
            new ColorToneScale(outlineMap),
            new ColorToneScale(surfaceMap)
        );
    }

    private int getItemColor(List<ColorSettingItem> list, int index, int fallback) {
        if (index >= 0 && index < list.size()) {
            return list.get(index).colorArgb;
        }
        return fallback;
    }

    private void createTheme() {
        String finalName= this.themeName.trim().isEmpty() ? "Новая тема" : this.themeName.trim();
        StylePalette palette= buildPaletteFromSettings();
        Theme newTheme= Theme.of(
            "custom_" + System.currentTimeMillis(),
            finalName,
            Expensive.INSTANCE.userSession().username(),
            ConfigOrigin.USER,
            ThemeMode.DARK,
            palette,
            Instant.now(),
            Instant.now()
        );
        
        ThemeCard2 card= new ThemeCard2(new ThemeCard(newTheme, Expensive.INSTANCE.userSession().texture()));
        Expensive.INSTANCE.tabsController().theme().newFrame(card);
        Expensive.INSTANCE.tabsController().theme().markFramesDirty();
        Expensive.INSTANCE.theme = newTheme;

        if (Expensive.INSTANCE.configManager != null && Expensive.INSTANCE.configManager.themeConfig != null) {
            Expensive.INSTANCE.configManager.themeConfig.customThemes.add(newTheme);
            Expensive.INSTANCE.configManager.themeConfig.selectedThemeId = newTheme.id();
            Expensive.INSTANCE.configManager.saveThemes();
        }
        
        Expensive.INSTANCE.notificationRepository().post(NotificationType.SUCCESS, 
            Text.literal("Тема \"" + finalName + "\" создана!"), 3L, TimeUnit.SECONDS);
        
        close();
    }

    @Override
    public void layout(LayoutScaleContext class698Var) {
        this.headerBounds.withSize(width() - 40.0f, 46.0f).withPosition(x() + 20.0f, y() + 16.0f);
        this.titleBadge.layout(class698Var);
        this.titleBadge.setPosition(this.headerBounds.x(), this.headerBounds.y());

        float searchY= y() + 72.0f;
        float searchWidth= width() - 280.0f;
        float catX= x() + 20.0f + searchWidth + 10.0f;
        this.categoryDropdown.setPosition(catX, searchY);
        this.categoryDropdown.layout(class698Var);

        this.scrollArea.layout(class698Var);
        super.layout(class698Var);
    }

    @Override
    public void animation(WeightedEngine class141Var) {
        this.categoryDropdown.animation(class141Var);
        this.scrollArea.animation(class141Var);
        super.animation(class141Var);
    }

    public void syncFromActiveTheme() {
        Theme currentTheme= Expensive.INSTANCE.theme;
        if (currentTheme == null || currentTheme.palette() == null) return;

        StylePalette p= currentTheme.palette();

        // Populate Column 1 (Text)
        if (p.text() != null) {
            setItemColor(textColors, 0, p.text().tone(900).argb());
            setItemColor(textColors, 1, p.text().tone(800).argb());
            setItemColor(textColors, 2, p.text().tone(700).argb());
            setItemColor(textColors, 3, p.text().tone(600).argb());
            setItemColor(textColors, 4, p.text().tone(500).argb());
            setItemColor(textColors, 5, p.text().tone(400).argb());
            setItemColor(textColors, 6, p.text().tone(300).argb());
            setItemColor(textColors, 7, p.text().tone(200).argb());
            setItemColor(textColors, 8, p.text().tone(100).argb());
            setItemColor(textColors, 9, p.text().tone(50).argb());
        }

        // Populate Column 2 (Primary & Outline)
        if (p.accent() != null) setItemColor(primaryColors, 0, p.accent().argb());
        if (p.accentBright() != null) setItemColor(primaryColors, 1, p.accentBright().argb());
        if (p.favorite() != null) setItemColor(primaryColors, 2, p.favorite().argb());
        if (p.frameBackground() != null) setItemColor(primaryColors, 3, p.frameBackground().argb());
        // Shadow is index 4
        if (p.error() != null) {
            setItemColor(primaryColors, 5, p.error().tone(900).argb());
            setItemColor(primaryColors, 6, p.error().tone(500).argb());
            setItemColor(primaryColors, 7, p.error().tone(300).argb());
        }
        if (p.surfaceOutline() != null) {
            setItemColor(primaryColors, 8, p.surfaceOutline().tone(700).argb());
            setItemColor(primaryColors, 9, p.surfaceOutline().tone(600).argb());
            setItemColor(primaryColors, 10, p.surfaceOutline().tone(500).argb());
            setItemColor(primaryColors, 11, p.surfaceOutline().tone(400).argb());
            setItemColor(primaryColors, 12, p.surfaceOutline().tone(300).argb());
            setItemColor(primaryColors, 13, p.surfaceOutline().tone(50).argb());
        }

        // Populate Column 3 (Surface)
        if (p.surfaceBackground() != null) {
            setItemColor(surfaceColors, 0, p.surfaceBackground().tone(900).argb());
            setItemColor(surfaceColors, 1, p.surfaceBackground().tone(801).argb());
            setItemColor(surfaceColors, 2, p.surfaceBackground().tone(800).argb());
            setItemColor(surfaceColors, 3, p.surfaceBackground().tone(700).argb());
            setItemColor(surfaceColors, 4, p.surfaceBackground().tone(600).argb());
            setItemColor(surfaceColors, 5, p.surfaceBackground().tone(500).argb());
            setItemColor(surfaceColors, 6, p.surfaceBackground().tone(400).argb());
            setItemColor(surfaceColors, 7, p.surfaceBackground().tone(300).argb());
            setItemColor(surfaceColors, 8, p.surfaceBackground().tone(200).argb());
        }
    }

    private void setItemColor(List<ColorSettingItem> list, int index, int colorArgb) {
        if (index >= 0 && index < list.size()) {
            ColorSettingItem item= list.get(index);
            item.colorArgb = colorArgb;
            item.hexInputBuffer = item.hexString();
            item.syncHSBFromArgb();
        }
    }

    public void open() {
        Expensive.INSTANCE.windowController().closeColorPickers();
        this.originalTheme = Expensive.INSTANCE.theme;
        this.opened = true;
        this.editingThemeName = false;
        this.editingSearch = false;
        this.selectedCategoryFilter = CategoryFilter.ALL;
        this.categoryDropdown.select(CategoryFilter.ALL);
        this.categoryDropdown.close();
        this.activeEditingItem = null;
        syncFromActiveTheme();
    }

    public void close() {
        Expensive.INSTANCE.windowController().closeColorPickers();
        this.categoryDropdown.close();
        if (this.previewMode && this.originalTheme != null) {
            Expensive.INSTANCE.theme = this.originalTheme;
        }
        if (this.opened) {
            this.opened = false;
        }
    }

    @Override
    public float width() {
        return MenuWindow.MENU_WIDTH;
    }

    @Override
    public float height() {
        return MenuWindow.MENU_HEIGHT - MenuWindow.COLLAPSED_HEADER_HEIGHT;
    }

    public boolean isOpened() {
        return this.opened;
    }
}

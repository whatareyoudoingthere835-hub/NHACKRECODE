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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;

public class HotkeysWidget extends Draggable {
    public final MsdfFont semiBoldFont;
    public final MsdfFont boldFont;
    public final GlTextureObject starsTexture;
    public final GlTextureObject buttonsIcon;
    public final GlTextureObject frameIcon;
    public final AnimatedFloat opacityAnimation;
    public final List<HotkeyEntry> entries;
    public final Map<String, HotkeyEntry> entryMap;
    public final WidgetBounds headerBounds;
    public final MultiSelectSetting<HotkeyCategory> hiddenCategoriesSetting;
    public float width;
    public float height;

    public HotkeysWidget(BooleanSupplier booleanSupplier) {
        super("Hotkeys", booleanSupplier);
        this.semiBoldFont = Fonts.INTER_SEMIBOLD.get();
        this.boldFont = Fonts.INTER_EXTRA_BOLD.get();
        this.starsTexture = new GlTextureObject(new ClasspathResource("/textures/stars.png"));
        this.buttonsIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/buttons.png"));
        this.frameIcon = new GlTextureObject(new ClasspathResource("/icons/menu/new/frame.png"));
        this.opacityAnimation = new AnimatedFloat(200, Easings.LINEAR);
        this.entries = new ArrayList();
        this.entryMap = new HashMap();
        this.headerBounds = new WidgetBounds(0.0f, 0.0f, 0.0f, 19.0f);
        this.hiddenCategoriesSetting = new MultiSelectSetting(Lang.WIDGET_HOTKEYS_HIDDEN_CATEGORIES).values(HotkeyCategory.class);
        this.width = 161.0f;
        this.height = 31.0f;
        this.x = 10.0f;
        this.y = 49.0f;
        addSettings(this.hiddenCategoriesSetting);
    }

    @Override
    public float width() {
        return this.width;
    }

    @Override
    public float height() {
        return this.height;
    }

    @Override
    public boolean click(MouseButtonInput2 class807Var, boolean z) {
        return false;
    }

    @Override
    public boolean cursor(MouseMoveInput class808Var, boolean z) {
        return z && !class808Var.intercepted();
    }

    @Override
    public void layout(DragRenderContext class809Var) {
        if (isVisible()) {
            float fMax= 150.0f;
            float fMin= 39.0f;
            float fMax2= 0.0f;
            int i= -1;
            for (int i2 = 0; i2 < this.entries.size(); i2++) {
                if (this.entries.get(i2).anim.smoothAnimation() > 0.001f) {
                    i = i2;
                }
            }
            int i3= 0;
            while (i3 < this.entries.size()) {
                HotkeyEntry class640Var= this.entries.get(i3);
                float fMin2= Math.min(class640Var.anim.smoothAnimation(), 1.0f);
                String combination= KeyboardUtil.formatCombination(class640Var.keys);
                float width= this.semiBoldFont.getWidth(class640Var.name, 12.0f);
                float width2= 12.0f + this.boldFont.getWidth(combination, 10.0f);
                float height= class640Var.hasModule() ? 22.0f + this.semiBoldFont.getHeight(12.0f) : this.boldFont.getHeight(10.0f) + 6.0f;
                fMax = Math.max(fMax, 150.0f + ((((width + width2) + 60.0f) - 150.0f) * fMin2));
                fMin += Math.min(height, height * fMin2) + (i3 != i ? 6.0f * fMin2 : 0.0f);
                fMax2 = Math.max(fMax2, fMin2);
                i3++;
            }
            if (fMax2 > 0.0f) {
                fMin += 10.0f * fMax2;
            }
            this.width = fMax;
            this.height = fMin;
            this.headerBounds.withSize(fMax - 20.0f, 19.0f).withPosition(this.x + 10.0f, (this.y + 19.5f) - 9.5f);
        }
    }

    @Override
    public void render(DragRenderContext class809Var) {
        if (!isVisible() || this.opacityAnimation.isZero()) {
            return;
        }
        GraphicsDrawEngine class154VarDrawEngine= class809Var.drawEngine();
        MatrixStack matrixStack= class809Var.matrixStack();
        PaletteColorStack class115VarColorStack= class154VarDrawEngine.colorStack();
        StylePalette class764VarPalette= class809Var.theme().palette();

        float fAnimatedValue= this.opacityAnimation.animatedValue();
        class115VarColorStack.push();
        class115VarColorStack.alpha(fAnimatedValue);
        int blurAttachment= FrameBufferUtils.getColorAttachmentId(Expensive.INSTANCE.windowController().headerBlur().getBlurFramebuffer());
        if (blurAttachment > 0) {
            class154VarDrawEngine.roundedBlur(matrixStack.peek().getPositionMatrix(), this.x, this.y, this.width, this.height, 8.0f, class115VarColorStack.white(), blurAttachment);
        }
        int iComputeColor= class115VarColorStack.computeColor(class764VarPalette.surfaceBackground().tone(801).argb());
        int iComputeColor2= class115VarColorStack.computeColor(class764VarPalette.surfaceBackground().tone(900).argb());
        class154VarDrawEngine.roundedRectangle(matrixStack.peek().getPositionMatrix(), this.x, this.y, this.width, this.height, 8.0f, 2.5f, class115VarColorStack.computeColor(class764VarPalette.surfaceOutline().tone(600).argb()), iComputeColor2, iComputeColor2, iComputeColor, iComputeColor);
        class154VarDrawEngine.texture(matrixStack.peek().getPositionMatrix(), this.x, this.y, this.width, this.height, class154VarDrawEngine.bindTexture(this.starsTexture.textureWithSTB()), class115VarColorStack.white());
        class154VarDrawEngine.msdfFont(matrixStack.peek().getPositionMatrix(), this.semiBoldFont, "Hotkeys", this.headerBounds.x(), (this.headerBounds.y() + 9.5f) - (this.semiBoldFont.getHeight(13.0f) / 2.0f), 13.0f, 0.0f, class115VarColorStack.computeColor(class764VarPalette.text().tone(200).argb()));
        class154VarDrawEngine.roundedRectangle(matrixStack.peek().getPositionMatrix(), this.headerBounds.right() - 19.0f, this.headerBounds.y(), 19.0f, 19.0f, 6.0f, class115VarColorStack.computeColor(class764VarPalette.accentBright().argb(), 0.1f));
        class154VarDrawEngine.textureVerticalCHorizontalC(matrixStack.peek().getPositionMatrix(), this.buttonsIcon, this.headerBounds.right() - 9.5f, this.headerBounds.y() + 9.5f, 11, 11, class115VarColorStack.computeColor(class764VarPalette.accent().argb()));
        float fBottom= this.headerBounds.bottom() + 12.0f;
        for (HotkeyEntry class640Var : this.entries) {
            String combination= KeyboardUtil.formatCombination(class640Var.keys);
            float fMin= Math.min(class640Var.anim.smoothAnimation(), 1.0f);
            float width= this.boldFont.getWidth(combination, 10.0f);
            float height= this.boldFont.getHeight(10.0f) + 6.0f;
            float f= 12.0f + width;
            float f2= this.x + (10.0f * fAnimatedValue * fMin);
            float height2= class640Var.hasModule() ? 22.0f + this.semiBoldFont.getHeight(12.0f) : height;
            float fRight= this.headerBounds.right() - f;
            float f3= class640Var.hasModule() ? (fBottom + (height2 / 2.0f)) - (height / 2.0f) : fBottom;
            float height3= class640Var.hasModule() ? fBottom + 17.0f : (fBottom + (height / 2.0f)) - (this.semiBoldFont.getHeight(12.0f) / 2.0f);
            class115VarColorStack.push();
            class115VarColorStack.alpha(fMin);
            if (class640Var.hasModule() && class640Var.moduleName != null) {
                float height4= this.boldFont.getHeight(8.0f) + 4.0f;
                class154VarDrawEngine.roundedRectangle(matrixStack.peek().getPositionMatrix(), f2, fBottom, 20.0f + this.boldFont.getWidth(class640Var.moduleName.toUpperCase(), 8.0f), height4, 4.0f, class115VarColorStack.computeColor(class764VarPalette.accent().argb(), 0.1f));
                class154VarDrawEngine.textureVerticalC(matrixStack.peek().getPositionMatrix(), this.frameIcon, f2 + 4.0f, fBottom + (height4 / 2.0f), 9, 9, class115VarColorStack.computeColor(class764VarPalette.accent().argb()));
                class154VarDrawEngine.msdfFont(matrixStack.peek().getPositionMatrix(), this.boldFont, class640Var.moduleName.toUpperCase(), f2 + 16.0f, (fBottom + (height4 / 2.0f)) - (this.boldFont.getHeight(8.0f) / 2.0f), 8.0f, 0.05f, class115VarColorStack.computeColor(class764VarPalette.accent().argb()));
            }
            class154VarDrawEngine.msdfFont(matrixStack.peek().getPositionMatrix(), this.semiBoldFont, class640Var.name, f2, height3, 12.0f, 0.05f, class115VarColorStack.computeColor(class764VarPalette.text().tone(400).argb()));
            class154VarDrawEngine.roundedRectangle(matrixStack.peek().getPositionMatrix(), fRight, f3, f, height, 6.0f, class115VarColorStack.computeColor(1974050, 0.5f));
            class154VarDrawEngine.msdfFont(matrixStack.peek().getPositionMatrix(), this.boldFont, combination, (fRight + (f / 2.0f)) - (width / 2.0f), (f3 + (height / 2.0f)) - (this.boldFont.getHeight(10.0f) / 2.0f), 10.0f, 0.05f, class115VarColorStack.computeColor(class764VarPalette.text().tone(500).argb()));
            class115VarColorStack.pop();
            fBottom += (height2 + 6.0f) * fMin;
        }
        class115VarColorStack.pop();
    }

    @Override
    public void animate(WeightedEngine class141Var) {
        if (Mc.INSTANCE.getPlayer() == null) {
            return;
        }
        this.entries.forEach(class640Var -> {
            class640Var.anim.animate(class141Var);
        });
        this.opacityAnimation.animate(class141Var);
    }

    @Override
    public void update() {
        if (isVisible()) {
            Set<String> activeKeys= new HashSet<>();
            boolean anyActive= false;

            for (Module module : Expensive.INSTANCE.moduleRepository().getModules()) {
                HotkeyCategory category= HotkeyCategory.fromTab(module.getModuleTab());
                if (category != null && this.hiddenCategoriesSetting.isSelected(category)) {
                    continue;
                }

                // Module must be active AND module itself must be bound!
                boolean moduleHasBind= module.hasKeyBind() && module.getKey() != -1 && module.getKeyBind() != null && !module.getKeyBind().isEmpty();

                if (module.isState()) {
                    // 1. Display module main keybind if module is bound
                    if (moduleHasBind) {
                        boolean entryActive= updateEntry(activeKeys, "m:" + module.getName(), () -> {
                            return new HotkeyEntry(module.getName(), module.getKeyBind());
                        }, module.getKeyBind(), true);
                        if (entryActive) {
                            anyActive = true;
                        }
                    }

                    // 2. Display setting keybinds ONLY IF module itself is bound!
                    if (moduleHasBind) {
                        for (Setting setting : module.getSettings()) {
                            anyActive |= processSetting(module, setting, activeKeys);
                        }
                    }
                }
            }

            this.entryMap.keySet().retainAll(activeKeys);
            this.entries.clear();
            this.entryMap.values().stream()
                .sorted(Comparator.comparing(entry -> entry.name, String.CASE_INSENSITIVE_ORDER))
                .forEach(this.entries::add);

            float opacityTarget= ((this.entries.isEmpty() || !anyActive) && !(Mc.INSTANCE.getCurrentScreen() instanceof ChatScreen)) ? 0.0f : 1.0f;
            this.opacityAnimation.destination(opacityTarget);
        }
    }

    public boolean processSetting(Module module, Setting setting, Set<String> activeKeys) {
        if (setting == null) {
            return false;
        }
        if (setting.getVisible() != null && !Boolean.TRUE.equals(setting.getVisible().get())) {
            return false;
        }

        boolean anyActive= false;
        String settingName= setting.getName() != null ? setting.getName().effective() : "";

        if (isGenericKeybindName(settingName)) {
            return false;
        }

        if (setting instanceof BooleanSetting boolSetting) {
            List<Integer> keyBind= boolSetting.getKeyBind();
            if (keyBind != null && !keyBind.isEmpty() && boolSetting.getKey() != -1) {
                boolean isActive= boolSetting.isValue();
                if (isActive) {
                    if (updateEntry(activeKeys, "s:" + module.getName() + ":" + settingName, () -> {
                        return new HotkeyEntry(module, settingName, keyBind);
                    }, keyBind, true)) {
                        anyActive = true;
                    }
                }
            }
        } else if (setting instanceof KeybindSetting keySetting) {
            List<Integer> keyBind= keySetting.getKeyBind();
            if (keyBind != null && !keyBind.isEmpty() && keySetting.getKey() != -1) {
                if (updateEntry(activeKeys, "s:" + module.getName() + ":" + settingName, () -> {
                    return new HotkeyEntry(module, settingName, keyBind);
                }, keyBind, true)) {
                    anyActive = true;
                }
            }
        } else if (setting instanceof ExpandableSetting expSetting) {
            List<Integer> keyBind= expSetting.getKeyBind();
            if (keyBind != null && !keyBind.isEmpty() && expSetting.getKey() != -1) {
                boolean isActive= expSetting.isValue();
                if (isActive) {
                    if (updateEntry(activeKeys, "s:" + module.getName() + ":" + settingName, () -> {
                        return new HotkeyEntry(module, settingName, keyBind);
                    }, keyBind, true)) {
                        anyActive = true;
                    }
                }
            }
            if (expSetting.getSubSettings() != null) {
                for (Setting sub : expSetting.getSubSettings()) {
                    anyActive |= processSetting(module, sub, activeKeys);
                }
            }
        }

        return anyActive;
    }

    private boolean isGenericKeybindName(String name) {
        if (name == null || name.isBlank()) return true;
        String lower= name.trim().toLowerCase();
        return lower.equals("клавиша") 
            || lower.equals("кнопка") 
            || lower.equals("key") 
            || lower.equals("keybind") 
            || lower.equals("bind")
            || lower.equals("кнопка активации")
            || lower.equals("клавиша активации")
            || lower.equals("клавиша свапа")
            || lower.equals("кнопка свапа");
    }

    public boolean updateEntry(Set<String> set, String str, Supplier<HotkeyEntry> supplier, List<Integer> list, boolean z) {
        set.add(str);
        HotkeyEntry class640VarComputeIfAbsent= this.entryMap.computeIfAbsent(str, str2 -> {
            return (HotkeyEntry) supplier.get();
        });
        class640VarComputeIfAbsent.keys.clear();
        if (list != null) {
            class640VarComputeIfAbsent.keys.addAll(list);
        }
        class640VarComputeIfAbsent.anim.state(z);
        return z;
    }
}

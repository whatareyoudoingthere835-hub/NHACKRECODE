package aethereal.system.network;
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

import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class TooltipService {
    public final WidgetContainer container;
    public final Map<Object, TooltipWidget> tooltips = new HashMap();

    public TooltipService(WidgetContainer class683Var) {
        this.container = class683Var;
    }

    public void cleanupStaleTooltips() {
        this.tooltips.entrySet().removeIf(entry -> {
            TooltipWidget widget= entry.getValue();
            if (widget != null && !widget.isVisible()) {
                if (this.container != null) {
                    this.container.removeChild(widget);
                }
                return true;
            }
            return false;
        });
    }

    public void hideAll() {
        for (TooltipWidget widget : this.tooltips.values()) {
            if (widget != null) {
                widget.hide();
            }
        }
        cleanupStaleTooltips();
    }

    public void show(Object obj, WidgetBounds class678Var, Translation class254Var, GlTextureObject class073Var) {
        for (Map.Entry<Object, TooltipWidget> entry : this.tooltips.entrySet()) {
            if (entry.getKey() != obj && entry.getValue() != null) {
                entry.getValue().hide();
            }
        }
        cleanupStaleTooltips();
        this.tooltips.computeIfAbsent(obj, obj2 -> {
            TooltipWidget class734Var= new TooltipWidget();
            this.container.addChild(class734Var);
            return class734Var;
        }).show(class254Var, class073Var, class678Var);
    }

    public void hide(Object obj) {
        TooltipWidget class734Var= this.tooltips.get(obj);
        if (class734Var != null) {
            class734Var.hide();
        }
        cleanupStaleTooltips();
    }

    public void updateAnchor(Object obj, WidgetBounds class678Var, DrawCtx class699Var) {
        TooltipWidget class734Var= this.tooltips.get(obj);
        if (class734Var == null || !class734Var.isVisible()) {
            return;
        }
        class734Var.updateAnchor(toScreenRect(class699Var, class678Var.x(), class678Var.y(), class678Var.width(), class678Var.height()));
    }

    public static WidgetBounds toScreenRect(DrawCtx class699Var, float f, float f2, float f3, float f4) {
        Matrix4f positionMatrix= class699Var.matrixStack().peek().getPositionMatrix();
        GraphicsDrawEngine class154VarDrawEngine= class699Var.drawEngine();
        Vector4f vector4fMul= new Vector4f(f, f2, 0.0f, 1.0f).mul(positionMatrix);
        Vector4f vector4fMul2= new Vector4f(f + f3, f2 + f4, 0.0f, 1.0f).mul(positionMatrix);
        return new WidgetBounds(class154VarDrawEngine.transformX(positionMatrix, f), class154VarDrawEngine.transformY(positionMatrix, f2), vector4fMul2.x - vector4fMul.x, vector4fMul2.y - vector4fMul.y);
    }

    public boolean isOwnedBy(Object obj) {
        TooltipWidget class734Var= this.tooltips.get(obj);
        return class734Var != null && class734Var.isVisible();
    }
}

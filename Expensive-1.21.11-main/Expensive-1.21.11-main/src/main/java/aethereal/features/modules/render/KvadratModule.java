package aethereal.features.modules.render;
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

import org.joml.Matrix4f;

public class KvadratModule extends Module {
    public ScaledRenderTarget renderTarget;

    public KvadratModule() {
        super(ModuleTab.RENDER, "Kvadrat");
        this.renderTarget = new ScaledRenderTarget(2);
        register(Render2DEvent.class, class311Var -> {
            if (isState() && class311Var.isPre()) {
                Matrix4f positionMatrix= class311Var.matrixStack().peek().getPositionMatrix();
                GraphicsDrawEngine class154VarDrawEngine= Expensive.INSTANCE.drawEngine();
                PaletteColorStack class115VarColorStack= class154VarDrawEngine.colorStack();
                this.renderTarget.add(() -> {
                    class154VarDrawEngine.begin();
                    class154VarDrawEngine.roundedRectangle(positionMatrix, 150.0f, 150.0f, 100.0f, 100.0f, 15.0f, class115VarColorStack.black());
                    class154VarDrawEngine.end();
                });
                this.renderTarget.renderToFramebuffer();
            }
        });
    }
}

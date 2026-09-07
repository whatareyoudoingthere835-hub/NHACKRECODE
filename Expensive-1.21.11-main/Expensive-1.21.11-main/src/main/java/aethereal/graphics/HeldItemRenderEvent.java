package aethereal.graphics;
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

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public final class HeldItemRenderEvent implements Event {
    public final Hand hand;

    public final MatrixStack matrices;
    public final ItemStack stack;

    public HeldItemRenderEvent(Hand hand, MatrixStack matrixStack, ItemStack itemStack) {
        this.hand = hand;
        this.matrices = matrixStack;
        this.stack = itemStack;
    }

        @Override
    public final String toString() {
        return getClass().getSimpleName() + "[" + "hand=" + this.hand + ", " + "matrices=" + this.matrices + ", " + "stack=" + this.stack + "]";
    }
    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.hand, this.matrices, this.stack);
    }
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HeldItemRenderEvent)) return false;
        HeldItemRenderEvent o= (HeldItemRenderEvent) obj;
        return java.util.Objects.equals(this.hand, o.hand) && java.util.Objects.equals(this.matrices, o.matrices) && java.util.Objects.equals(this.stack, o.stack);
    }
public Hand hand() {
        return this.hand;
    }

    public MatrixStack matrices() {
        return this.matrices;
    }

    public ItemStack stack() {
        return this.stack;
    }
}

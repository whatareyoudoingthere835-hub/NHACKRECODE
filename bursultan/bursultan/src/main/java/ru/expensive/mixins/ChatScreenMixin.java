package ru.expensive.mixins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.expensive.core.Extra;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.common.QuickImports;

import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements QuickImports {

    @Unique
    List<AbstractDraggable> draggables = Extra.getInstance()
            .getDraggableRepository()
            .draggable();

    protected ChatScreenMixin() {
        super(Text.empty());
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ru.expensive.common.util.render.ExpensiveRenderLayers.beginGuiFrame();
        try {
            draggables.stream()
                    .filter(draggable -> draggable.visible() && draggable.isDragging())
                    .reduce((first, second) -> second)
                    .ifPresent(active -> draggables.forEach(draggable -> {
                        if (active == draggable) {
                            draggable.render(context, mouseX, mouseY, delta);
                        }
                    }));
        } finally {
            ru.expensive.common.util.render.ExpensiveRenderLayers.endGuiFrame();
        }
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    private void onMouseClicked(net.minecraft.client.gui.Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        draggables.forEach(draggable -> draggable.mouseClicked(click, doubled));
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        draggables.forEach(draggable -> draggable.mouseReleased(click));
        return super.mouseReleased(click);
    }
}

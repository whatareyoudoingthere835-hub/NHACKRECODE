package ru.expensive.mixin;
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

import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ChatHud.class})
public abstract class ChatHudMixin {

    @Shadow
    @Final
    public List<ChatHudLine.Visible> visibleMessages;

    @Shadow
    @Final
    public List<ChatHudLine> messages;

    @Shadow
    private boolean hasUnreadNewMessages;

    @Shadow
    private int scrolledLines;

    @Shadow
    public abstract void scroll(int i);

    @Shadow
    public abstract boolean isChatFocused();

    @Shadow
    public abstract int getWidth();

    @Redirect(method = {"addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V"}, at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0))
    public int hookGetSize2(List<ChatHudLine.Visible> list) {
        ChatLimitEvent class056Var = new ChatLimitEvent(ChatLimitType.LIMIT);
        Expensive.INSTANCE.eventDispatcher().dispatch(class056Var);
        if (class056Var.isCancelled()) {
            return -1;
        }
        return list.size();
    }

    @Inject(method = {"addVisibleMessage"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;isChatFocused()Z", shift = At.Shift.BEFORE)}, cancellable = true)
    public void hookAddVisibleMessage(ChatHudLine chatHudLine, CallbackInfo callbackInfo, @Local List<OrderedText> list) {
        boolean zMethod_1819 = isChatFocused();
        String strExpensive_ru$getId = ((ChatLineIdAccessor) ChatLineIdAccessor.class.cast(chatHudLine)).expensive_ru$getId();
        int i = 0;
        while (i < list.size()) {
            OrderedText orderedText = list.get(i);
            if (zMethod_1819 && this.scrolledLines > 0) {
                this.hasUnreadNewMessages = true;
                scroll(1);
            }
            ChatHudLine.Visible visible = new ChatHudLine.Visible(chatHudLine.creationTick(), orderedText, chatHudLine.indicator(), i == list.size() - 1);
            ((ChatLineIdAccessor) ChatLineIdAccessor.class.cast(visible)).expensive_ru$setId(strExpensive_ru$getId);
            this.visibleMessages.addFirst(visible);
            i++;
        }
        ChatLimitEvent class056Var = new ChatLimitEvent(ChatLimitType.LIMIT);
        Expensive.INSTANCE.eventDispatcher().dispatch(class056Var);
        if (!class056Var.isCancelled()) {
            while (this.visibleMessages.size() > 100) {
                this.visibleMessages.removeLast();
            }
        }
        callbackInfo.cancel();
    }

    @Inject(method = {"clear"}, at = {@At("HEAD")}, cancellable = true)
    private void onClear(boolean z, CallbackInfo callbackInfo) {
        ChatLimitEvent class056Var = new ChatLimitEvent(ChatLimitType.HISTORY);
        Expensive.INSTANCE.eventDispatcher().dispatch(class056Var);
        if (class056Var.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}

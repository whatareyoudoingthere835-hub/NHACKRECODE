package thunder.hack.events.impl;

import thunder.hack.events.Event;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import thunder.hack.utility.render.PoseStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class EventHeldItemRenderer extends Event {
    private final Hand hand;
    private final ItemStack item;
    private float ep;
    private final PoseStack stack;

    public EventHeldItemRenderer(Hand hand, ItemStack item, float equipProgress, PoseStack stack) {
        this.hand = hand;
        this.item = item;
        this.ep = equipProgress;
        this.stack = stack;
    }

    public Hand getHand() {
        return hand;
    }

    public ItemStack getItem() {
        return item;
    }

    public float getEp() {
        return ep;
    }

    public PoseStack getStack() {
        return stack;
    }
}
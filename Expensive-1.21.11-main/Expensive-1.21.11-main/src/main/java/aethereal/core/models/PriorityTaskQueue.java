package aethereal.core.models;
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

import java.util.PriorityQueue;

public class PriorityTaskQueue<T> {
    public int tickCounter = 0;
    public PriorityQueue<PriorityTaskEntry<T>> activeTasks = new PriorityQueue<>((class194Var, class194Var2) -> {
        return Integer.compare(class194Var2.priority, class194Var.priority);
    });

    public void tick(int i) {
        this.tickCounter += i;
    }

    public void addTask(PriorityTaskEntry<T> class194Var) {
        this.activeTasks.removeIf(class194Var2 -> {
            return class194Var2.module.equals(class194Var.module);
        });
        class194Var.expiresAt += this.tickCounter;
        this.activeTasks.add(class194Var);
    }

    public T fetchActiveTaskValue() {
        while (!this.activeTasks.isEmpty() && this.activeTasks.peek() != null && (this.activeTasks.peek().expiresAt <= this.tickCounter || !this.activeTasks.peek().module.isState())) {
            this.activeTasks.poll();
        }
        if (this.activeTasks.isEmpty() || this.activeTasks.peek() == null) {
            return null;
        }
        return (T) this.activeTasks.peek().module;
    }
}

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

public class PriorityValueQueue<T> {
    public int tickCounter = 0;
    public final PriorityQueue<PriorityValueEntry<T>> queue = new PriorityQueue<>((class413Var, class413Var2) -> {
        return Integer.compare(class413Var2.priority, class413Var.priority);
    });

    public void tick(int i) {
        this.tickCounter += i;
    }

    public void addTask(PriorityValueEntry<T> class413Var) {
        this.queue.removeIf(class413Var2 -> {
            return class413Var2.provider == class413Var.provider;
        });
        ((PriorityValueEntry) class413Var).expiresIn += this.tickCounter;
        this.queue.add(class413Var);
    }

    public T getValue() {
        while (!this.queue.isEmpty()) {
            PriorityValueEntry<T> class413VarPeek= this.queue.peek();
            if (((PriorityValueEntry<T>) class413VarPeek).expiresIn > this.tickCounter) {
                return ((PriorityValueEntry<T>) class413VarPeek).value;
            }
            this.queue.poll();
        }
        return null;
    }
}

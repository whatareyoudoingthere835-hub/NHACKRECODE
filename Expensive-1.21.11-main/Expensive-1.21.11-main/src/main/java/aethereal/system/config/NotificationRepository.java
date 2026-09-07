package aethereal.system.config;
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

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class NotificationRepository {
    public final List<Notification> notifications = new CopyOnWriteArrayList();

    public void post(NotificationType class659Var, Text text, long j, TimeUnit timeUnit) {
        long millis= timeUnit.toMillis(j);
        AnimatedFloat class042Var= new AnimatedFloat(250, Easings.LINEAR);
        class042Var.destination(1.0f);
        this.notifications.add(new Notification(class042Var, class659Var, text, System.currentTimeMillis(), millis, null));
        if (this.notifications.size() > 5) {
            this.notifications.removeFirst();
        }
        this.notifications.sort(Comparator.comparingDouble(class657Var -> {
            return -class657Var.duration();
        }));
    }

    public void post(NotificationType class659Var, Text text, long j) {
        post(class659Var, text, j, TimeUnit.MILLISECONDS);
    }

    public void post(Text text, ItemStack itemStack, long j, TimeUnit timeUnit) {
        long millis= timeUnit.toMillis(j);
        AnimatedFloat class042Var= new AnimatedFloat(250, Easings.LINEAR);
        class042Var.destination(1.0f);
        this.notifications.add(new Notification(class042Var, NotificationType.INFO, text, System.currentTimeMillis(), millis, itemStack.copy()));
        if (this.notifications.size() > 5) {
            this.notifications.removeFirst();
        }
        this.notifications.sort(Comparator.comparingDouble(class657Var -> {
            return -class657Var.duration();
        }));
    }

    public void post(Text text, ItemStack itemStack, long j) {
        post(text, itemStack, j, TimeUnit.MILLISECONDS);
    }

    public void tick() {
        this.notifications.forEach(class657Var -> {
            if (class657Var.isExpired()) {
                class657Var.valueAnimation().destination(0.0f);
            }
        });
        this.notifications.removeIf(class657Var2 -> {
            return class657Var2.isExpired() && class657Var2.valueAnimation().animatedValue() <= 0.0f;
        });
    }

    public void animate(WeightedEngine class141Var) {
        this.notifications.forEach(class657Var -> {
            class657Var.valueAnimation().animate(class141Var);
        });
    }

    public List<Notification> getNotifications() {
        return this.notifications;
    }
}

package aethereal.utils.math;
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

public final class Easings {
    public static final EasingFunction LINEAR = f -> {
        return f;
    };
    public static final EasingFunction EASE_IN_QUAD = f -> {
        return f * f;
    };
    public static final EasingFunction EASE_OUT_QUAD = f -> {
        return 1.0f - ((1.0f - f) * (1.0f - f));
    };
    public static final EasingFunction EASE_IN_OUT_QUAD = f -> {
        return f < 0.5f ? 2.0f * f * f : 1.0f - (FastMathUtils.fastPow(((-2.0f) * f) + 2.0f, 2.0f) / 2.0f);
    };
    public static final EasingFunction EASE_IN_CUBIC = f -> {
        return f * f * f;
    };
    public static final EasingFunction EASE_OUT_CUBIC = f -> {
        return 1.0f - FastMathUtils.fastPow(1.0f - f, 3.0f);
    };
    public static final EasingFunction EASE_IN_OUT_CUBIC = f -> {
        return f < 0.5f ? 4.0f * f * f * f : 1.0f - (FastMathUtils.fastPow(((-2.0f) * f) + 2.0f, 3.0f) / 2.0f);
    };
    public static final EasingFunction EASE_IN_QUART = f -> {
        return f * f * f * f;
    };
    public static final EasingFunction EASE_OUT_QUART = f -> {
        return 1.0f - FastMathUtils.fastPow(1.0f - f, 4.0f);
    };
    public static final EasingFunction EASE_IN_OUT_QUART = f -> {
        return f < 0.5f ? 8.0f * FastMathUtils.fastPow(f, 4.0f) : 1.0f - (FastMathUtils.fastPow(((-2.0f) * f) + 2.0f, 4.0f) / 2.0f);
    };
    public static final EasingFunction EASE_IN_QUINT = f -> {
        return FastMathUtils.fastPow(f, 5.0f);
    };
    public static final EasingFunction EASE_OUT_QUINT = f -> {
        return 1.0f - FastMathUtils.fastPow(1.0f - f, 5.0f);
    };
    public static final EasingFunction EASE_IN_OUT_QUINT = f -> {
        return f < 0.5f ? 16.0f * FastMathUtils.fastPow(f, 5.0f) : 1.0f - (FastMathUtils.fastPow(((-2.0f) * f) + 2.0f, 5.0f) / 2.0f);
    };

    public Easings() {
    }
}

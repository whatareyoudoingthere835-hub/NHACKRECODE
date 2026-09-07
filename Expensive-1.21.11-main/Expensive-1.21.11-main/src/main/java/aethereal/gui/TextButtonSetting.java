package aethereal.gui;
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

import java.util.function.Supplier;

public class TextButtonSetting extends Setting {
    public String text;
    public int min;
    public int max;
    public boolean onlyDigits;
    public boolean password;
    public Runnable runnable;
    public Translation buttonName;

    public TextButtonSetting(Translation class254Var, Translation class254Var2) {
        super(class254Var, class254Var2);
        this.min = 0;
        this.max = Integer.MAX_VALUE;
        this.onlyDigits = false;
        this.password = false;
    }

    public TextButtonSetting(Translation class254Var) {
        this(class254Var, null);
    }

    public TextButtonSetting visible(Supplier<Boolean> supplier) {
        setVisible(supplier);
        return this;
    }

    public String getText() {
        return this.text;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public boolean isOnlyDigits() {
        return this.onlyDigits;
    }

    public boolean isPassword() {
        return this.password;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public Translation getButtonName() {
        return this.buttonName;
    }

    public TextButtonSetting setText(String str) {
        this.text = str;
        return this;
    }

    public TextButtonSetting setMin(int i) {
        this.min = i;
        return this;
    }

    public TextButtonSetting setMax(int i) {
        this.max = i;
        return this;
    }

    public TextButtonSetting setOnlyDigits(boolean z) {
        this.onlyDigits = z;
        return this;
    }

    public TextButtonSetting setPassword(boolean z) {
        this.password = z;
        return this;
    }

    public TextButtonSetting setRunnable(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    public TextButtonSetting setButtonName(Translation class254Var) {
        this.buttonName = class254Var;
        return this;
    }
}

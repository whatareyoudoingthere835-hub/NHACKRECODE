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

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ActionDialogBuilder {
    public Translation titleText;
    public Translation descriptionText;
    public Translation confirmLabelText;
    public GlTextureObject iconTexture;
    public boolean changePlaceholderColor;
    public int placeholderColorValue;
    public Translation placeholderTextValue;
    public Consumer<FrameElementColumn> contentConfigurator;
    public BooleanSupplier enableConfirmSupplier;
    public float dialogWidth = 320.0f;

    public Runnable onConfirmAction;

    public ActionDialogBuilder title(Translation class254Var) {
        this.titleText = class254Var;
        return this;
    }

    public ActionDialogBuilder title(String str) {
        this.titleText = Translation.clearText(str);
        return this;
    }

    public ActionDialogBuilder description(Translation class254Var) {
        this.descriptionText = class254Var;
        return this;
    }

    public ActionDialogBuilder description(String str) {
        this.descriptionText = Translation.clearText(str);
        return this;
    }

    public ActionDialogBuilder confirmLabel(Translation class254Var) {
        this.confirmLabelText = class254Var;
        return this;
    }

    public ActionDialogBuilder confirmLabel(String str) {
        this.confirmLabelText = Translation.clearText(str);
        return this;
    }

    public ActionDialogBuilder icon(GlTextureObject class073Var) {
        this.iconTexture = class073Var;
        return this;
    }

    public ActionDialogBuilder placeholderColor(int i) {
        this.changePlaceholderColor = true;
        this.placeholderColorValue = i;
        return this;
    }

    public ActionDialogBuilder placeholderText(Translation class254Var) {
        this.placeholderTextValue = class254Var;
        return this;
    }

    public ActionDialogBuilder placeholderText(String str) {
        this.placeholderTextValue = Translation.clearText(str);
        return this;
    }

    public ActionDialogBuilder content(Consumer<FrameElementColumn> consumer) {
        this.contentConfigurator = consumer;
        return this;
    }

    public ActionDialogBuilder enableConfirmButton(BooleanSupplier booleanSupplier) {
        this.enableConfirmSupplier = booleanSupplier;
        return this;
    }

    public ActionDialogBuilder width(float f) {
        this.dialogWidth = f;
        return this;
    }

    public ActionDialogBuilder onConfirm(Runnable runnable) {
        this.onConfirmAction = runnable;
        return this;
    }

    public ActionDialogData build() {
        return new ActionDialogData(this.titleText, this.descriptionText, this.confirmLabelText, this.iconTexture, this.changePlaceholderColor, this.placeholderColorValue, this.placeholderTextValue, this.contentConfigurator, this.enableConfirmSupplier, this.dialogWidth, this.onConfirmAction);
    }
}

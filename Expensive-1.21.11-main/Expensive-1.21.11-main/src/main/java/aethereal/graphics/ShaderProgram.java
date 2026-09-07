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

import java.util.function.IntConsumer;
import org.lwjgl.opengl.GL33;

public class ShaderProgram implements Reloadable {
    public static final IntConsumer noOpCallback = i -> {
    };
    public final ResourceSource fragmentSource;
    public final ResourceSource vertexSource;
    public Integer programId;

    public IntConsumer compileCallback = noOpCallback;

    public void bind() {
        GL33.glUseProgram(getProgramId());
    }

    public void unbind() {
        GL33.glUseProgram(0);
    }

    @Override
    public void reload() {
        release();
    }

    public void release() {
        if (this.programId != null) {
            GL33.glDeleteProgram(this.programId.intValue());
            this.programId = null;
        }
    }

    public ShaderUniform uniform(String str) {
        ShaderUniform class228Var= new ShaderUniform(str);
        addCompileCallback(class228Var.programCompileCallback());
        return class228Var;
    }

    public int getProgramId() {
        if (this.programId != null) {
            return this.programId.intValue();
        }
        Integer numValueOf= Integer.valueOf(createProgram());
        this.programId = numValueOf;
        return numValueOf.intValue();
    }

    public int createProgram() {
        int iGlCreateProgram= GL33.glCreateProgram();
        int iMethod003= compileShader(35633, this.vertexSource);
        int iMethod004= compileShader(35632, this.fragmentSource);
        GL33.glAttachShader(iGlCreateProgram, iMethod003);
        GL33.glAttachShader(iGlCreateProgram, iMethod004);
        GL33.glLinkProgram(iGlCreateProgram);
        if (GL33.glGetProgrami(iGlCreateProgram, 35714) == 0) {
            throw new IllegalStateException("Could not link program: " + GL33.glGetProgramInfoLog(iGlCreateProgram));
        }
        GL33.glDeleteShader(iMethod003);
        GL33.glDeleteShader(iMethod004);
        notifyCompiled(iGlCreateProgram);
        return iGlCreateProgram;
    }

    public int compileShader(int i, ResourceSource class178Var) {
        String str;
        switch (i) {
            case 35632:
                str = "FRAGMENT";
                break;
            case 35633:
                str = "VERTEX";
                break;
            default:
                str = "TYPE_" + i;
                break;
        }
        Expensive.LOGGER.info("Compiling {} shader from resource: {} ({})", new Object[]{str, class178Var, class178Var.getClass().getName()});
        int iGlCreateShader= GL33.glCreateShader(i);
        GL33.glShaderSource(iGlCreateShader, class178Var.utf8());
        GL33.glCompileShader(iGlCreateShader);
        if (GL33.glGetShaderi(iGlCreateShader, 35713) == 0) {
            throw new IllegalStateException("Couldn't compile shader: " + GL33.glGetShaderInfoLog(iGlCreateShader));
        }
        return iGlCreateShader;
    }

    public void notifyCompiled(int i) {
        this.compileCallback.accept(i);
    }

    public void addCompileCallback(IntConsumer intConsumer) {
        IntConsumer intConsumer2= this.compileCallback;
        this.compileCallback = i -> {
            intConsumer2.accept(i);
            intConsumer.accept(i);
        };
    }

    public ShaderProgram(ResourceSource class178Var, ResourceSource class178Var2) {
        this.fragmentSource = class178Var;
        this.vertexSource = class178Var2;
    }
}

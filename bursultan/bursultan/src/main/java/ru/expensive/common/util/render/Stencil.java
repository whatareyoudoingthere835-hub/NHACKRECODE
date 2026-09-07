package ru.expensive.common.util.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import ru.expensive.common.QuickImports;

import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;

public class Stencil implements QuickImports {

    public static void push() {
        glStencilMask(0xFF);
        glClear(GL_STENCIL_BUFFER_BIT);
        glEnable(GL_STENCIL_TEST);
        glStencilFunc(GL_ALWAYS, 1, 1);
        glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
        glDisable(GL_DEPTH_TEST);
        glColorMask(false, false, false, false);
    }

    public static void read(int ref) {
        glColorMask(true, true, true, true);
        glStencilFunc(GL_EQUAL, ref, 1);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
    }

    public static void pop() {
        glDisable(GL_STENCIL_TEST);
    }
}
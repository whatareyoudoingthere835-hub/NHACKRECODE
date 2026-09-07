package ru.expensive.common.util.render;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.lwjgl.opengl.GL11;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.other.Pool;

import java.awt.*;
import java.util.Stack;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScissorManager implements QuickImports {
    Pool<Scissor> scissorPool = new Pool<>(Scissor::new);
    Stack<Scissor> scissorStack = new Stack<>();

    public void push(double x, double y, double width, double height) {
        Scissor currentScissor = scissorPool.get().copy();
        if (!scissorStack.isEmpty()) {
            Scissor parent = scissorStack.peek();
            double x1 = Math.max(x, parent.x);
            double y1 = Math.max(y, parent.y);
            double x2 = Math.min(x + width, parent.x + parent.width);
            double y2 = Math.min(y + height, parent.y + parent.height);
            currentScissor.set(x1, y1, Math.max(0, x2 - x1), Math.max(0, y2 - y1));
        } else {
            currentScissor.set(x, y, width, height);
        }
        scissorStack.push(currentScissor);
        setScissor(currentScissor);
    }

    public void pop() {
        if (!scissorStack.isEmpty()) {
            scissorPool.free(scissorStack.pop());
            if (scissorStack.isEmpty()) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            } else {
                setScissor(scissorStack.peek());
            }
        }
    }

    private void setScissor(Scissor scissor) {
        double scaleFactor = mc.getWindow().getScaleFactor();
        int x = (int) Math.round(scissor.x * scaleFactor);
        int y = mc.getWindow().getFramebufferHeight() - (int) Math.round((scissor.y + scissor.height) * scaleFactor);
        int width = (int) Math.round(scissor.width * scaleFactor);
        int height = (int) Math.round(scissor.height * scaleFactor);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(Math.max(0, x), Math.max(0, y), Math.max(0, width), Math.max(0, height));
    }

    private static class Scissor {
        public int x, y;
        public int width, height;

        public void set(double x, double y, double width, double height) {
            this.x = Math.max(0, (int) Math.round(x));
            this.y = Math.max(0, (int) Math.round(y));
            this.width = Math.max(0, (int) Math.round(width));
            this.height = Math.max(0, (int) Math.round(height));
        }

        Scissor copy() {
            Scissor newScissor = new Scissor();
            newScissor.set(this.x, this.y, this.width, this.height);
            return newScissor;
        }
    }
}
package aethereal.utils;
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

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class ImageBufferUtil {
    public static ByteBuffer convertToByteBuffer(BufferedImage bufferedImage) {
        int width= bufferedImage.getWidth();
        int height= bufferedImage.getHeight();
        int[] iArr= new int[width * height];
        bufferedImage.getRGB(0, 0, width, height, iArr, 0, width);
        ByteBuffer byteBufferOrder= ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
        for (int i : iArr) {
            byteBufferOrder.put((byte) ((i >> 16) & StencilBufferUtil.STENCIL_MASK));
            byteBufferOrder.put((byte) ((i >> 8) & StencilBufferUtil.STENCIL_MASK));
            byteBufferOrder.put((byte) (i & StencilBufferUtil.STENCIL_MASK));
            byteBufferOrder.put((byte) ((i >> 24) & StencilBufferUtil.STENCIL_MASK));
        }
        byteBufferOrder.flip();
        return byteBufferOrder;
    }

    public ImageBufferUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

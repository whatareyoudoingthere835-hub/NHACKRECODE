package aethereal.system.resources;
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteArrayResource implements ResourceSource {
    public final byte[] bytes;

    @Override
    public InputStream stream() {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override
    public void writeToByteBuffer(ByteBuffer byteBuffer) {
        byteBuffer.put(this.bytes);
    }

    @Override
    public ByteBuffer asDirectByteBuffer() {
        ByteBuffer byteBufferAllocateDirect= ByteBuffer.allocateDirect(this.bytes.length);
        byteBufferAllocateDirect.put(this.bytes);
        return byteBufferAllocateDirect.flip();
    }

    @Override
    public byte[] bytes() {
        return this.bytes;
    }

    public ByteArrayResource(byte[] bArr) {
        this.bytes = bArr;
    }
}

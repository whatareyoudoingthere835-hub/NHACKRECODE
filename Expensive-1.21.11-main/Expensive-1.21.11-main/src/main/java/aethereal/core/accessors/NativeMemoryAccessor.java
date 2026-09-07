package aethereal.core.accessors;
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

import org.lwjgl.system.MemoryUtil;

public class NativeMemoryAccessor implements NativeMemory {
    @Override
    public void writeByte(long j, int i) {
        MemoryUtil.memPutByte(j, (byte) i);
    }

    @Override
    public void writeShort(long j, int i) {
        MemoryUtil.memPutShort(j, (short) i);
    }

    @Override
    public void writeInt(long j, int i) {
        MemoryUtil.memPutInt(j, i);
    }

    @Override
    public void writeFloat(long j, float f) {
        MemoryUtil.memPutFloat(j, f);
    }

    @Override
    public void writeLong(long j, long j2) {
        MemoryUtil.memPutLong(j, j2);
    }

    @Override
    public int readByte(long j) {
        return MemoryUtil.memGetByte(j) & 255;
    }

    @Override
    public int readShort(long j) {
        return MemoryUtil.memGetShort(j) & 65535;
    }

    @Override
    public int readInt(long j) {
        return MemoryUtil.memGetInt(j);
    }

    @Override
    public long readLong(long j) {
        return MemoryUtil.memGetLong(j);
    }

    @Override
    public void memoryCopy(long j, long j2, long j3) {
        MemoryUtil.memCopy(j, j2, j3);
    }

    @Override
    public long allocate(long j) {
        return MemoryUtil.nmemAllocChecked(j);
    }

    @Override
    public long reallocate(long j, long j2, long j3) {
        long jAllocate= allocate(j3);
        memoryCopy(j, jAllocate, j2);
        free(j);
        return jAllocate;
    }

    @Override
    public void free(long j) {
        MemoryUtil.nmemFree(j);
    }
}

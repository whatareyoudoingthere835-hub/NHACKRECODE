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

public interface MemoryAccessor {
    public void writeByte(long j, int i);

    public void writeShort(long j, int i);

    public void writeInt(long j, int i);

    public void writeFloat(long j, float f);

    public void writeLong(long j, long j2);

    public int readByte(long j);

    public int readShort(long j);

    public int readInt(long j);

    public long readLong(long j);

    public void memoryCopy(long j, long j2, long j3);
}

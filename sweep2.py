import os, re
ROOT="src/main/java"
log=[]
def read(p): return open(p,encoding="utf-8",errors="replace").read()
def write(p,s): open(p,"w",encoding="utf-8").write(s)

# ---- global regex sweep ----
stats={}
def files():
    for dp,dn,fn in os.walk(ROOT):
        for f in fn:
            if f.endswith(".java"): yield os.path.join(dp,f)
def sub(name,pat,rep):
    rx=re.compile(pat); n=0
    for path in files():
        src=read(path)
        out,cnt=rx.subn(rep,src)
        if cnt: write(path,out); n+=cnt
    stats[name]=n

# P1: inventory main
def mains(m):
    return m.group(0)  # placeholder
sub("P1-main", r"getInventory\(\)\.main\b", "getInventory().getMainStacks()")
sub("P1b-main-dot", r"getInventory\(\)\.getMainStacks\(\).(main)", r"getInventory().getMainStacks().\1")
# P3: selectedSlot writes first, then reads
sub("P3a-sel-write", r"(getInventory\(\))\.selectedSlot\s*=\s*([^;]+);", r"\1.setSelectedSlot(\2);")
sub("P3b-sel-read", r"getInventory\(\)\.selectedSlot\b", "getInventory().getSelectedSlot()")

# P5: spawn getId
sub("P5a", r"spawn\.getId\(\)", "spawn.getEntityId()")
sub("P5b", r"(\w+)\.setId\(spawn\.getEntityId\(\)\)", r"((IEntity) \1).th$setId(spawn.getEntityId())")

# P9 selected slot s2c
sub("P9", r"(\w+)\.getSlot\(\);\s*$", lambda m: m.group(0))  # no-op guard

# P10 vehicle move
sub("PV", r"new VehicleMoveC2SPacket\((\w+)\)", r"new VehicleMoveC2SPacket(\1.getX(), \1.getY(), \1.getZ(), \1.getYaw(), \1.getPitch())")

# armor patterns (P2) -- targeted
sub("P2a", r"getInventory\(\)\.armor\.reversed\(\)", "java.util.List.of(getInventory().getStack(39), getInventory().getStack(38), getInventory().getStack(37), getInventory().getStack(36))")
sub("P2b", r"getInventory\(\)\.armor\.get\(([^)]+)\)", r"getInventory().getStack(36 + (\1))")
sub("P2c", r"List<ItemStack> armor = \(\(PlayerEntity\) target\)\.getInventory\.armor;", "X")  # noop protection
sub("P2d", r"(\w+)\.getInventory\(\)\.armor;", r"\1.getInventory().getArmorStacks();")  # if method missing -> next pass list
sub("P2e", r"for \(ItemStack is : mc\.player\.getInventory\(\)\.armor\)", "for (int ai = 0; ai < 4; ai++) if (mc.player.getInventory().getStack(36+ai) instanceof ItemStack is)")
sub("P2f", r"armorItems", "armorItems")  # noop
sub("P2g", r"getInventory\(\)\.armor\b(?!;|\.\w|List)", "getInventory().getArmorStacks()")
print(stats)

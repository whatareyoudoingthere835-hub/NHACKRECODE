#!/usr/bin/env python3
# fix12: round-14 — 15 errors
import os, re, sys

ROOT = "/home/user/NHACKRECODE/src/main/java/thunder/hack"
applied, missed = [], []

def rd(p):
    with open(os.path.join(ROOT, p), encoding="utf-8") as f: return f.read()
def wr(p, s):
    with open(os.path.join(ROOT, p), "w", encoding="utf-8") as f: f.write(s)

def patch(tag, p, old, new, count=None, regex=False):
    s = rd(p)
    if regex:
        hits = len(re.findall(old, s))
        if hits == 0:
            missed.append(tag); return
        s2 = re.sub(old, new, s)
    else:
        hits = s.count(old)
        if hits == 0:
            missed.append(tag); return
        s2 = s.replace(old, new)
    if count is not None and hits != count:
        print(f"!! {tag}: expected {count}, got {hits}"); sys.exit(1)
    wr(p, s2); applied.append(f"{tag} ({hits})")

def find_file(name):
    for dp, _, fn in os.walk(ROOT):
        if name in fn: return os.path.relpath(os.path.join(dp, name), ROOT)
    return None

# 1. PVETools — original TH intentionally comments out the whole class; drop ModuleManager field
P = "core/manager/client/ModuleManager.java"
patch("1a.pve-import", P, "import thunder.hack.features.modules.misc.PVETools;\n", "")
patch("1b.pve-field", P, "    public static PVETools pveTools = new PVETools();\n", "")

# 2. EntityVelocityUpdateS2CPacket getter = getEntityId() (delta build.5)
for f in ("Strafe.java", "TargetStrafe.java"):
    patch(f"2.{f}", find_file(f), "(velocity = e.getPacket()).entityId()", "(velocity = e.getPacket()).getEntityId()")

# 3. Render2DEngine VertexFormats FQN
patch("3.vertexformats", os.path.join("utility", "render", "Render2DEngine.java"),
      "begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);",
      "begin(VertexFormat.DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);")

# 4. AutoCrystal revert (getX exists — AntiCrash proves it)
patch("4.autocrystal", find_file("AutoCrystal.java"),
      "crystal.squaredDistanceTo(explosion.x(), explosion.y(), explosion.z())",
      "crystal.squaredDistanceTo(explosion.getX(), explosion.getY(), explosion.getZ())")

# 5. KillEffect addParticle 9-arg
patch("5.killeffect", find_file("KillEffect.java"),
      "mc.world.addParticle(entity.getX() + j * 0.1, entity.getY() + i * 0.1, entity.getZ() + k * 0.1, 0, 0, 0, ParticleTypes.FALLING_LAVA);",
      "mc.world.addParticle(ParticleTypes.FALLING_LAVA, false, false, entity.getX() + j * 0.1, entity.getY() + i * 0.1, entity.getZ() + k * 0.1, 0, 0, 0);")

# 6. AutoSprint PlayerInput rebuild (no withSprint)
patch("6.autosprint", find_file("AutoSprint.java"),
      """        mc.player.input.playerInput = mc.player.input.playerInput.withSprint(
                mc.player.getHungerManager().getFoodLevel() > 6
                        && !mc.player.horizontalCollision
                        && thunder.hack.utility.player.InputUtility.forward() > 0
                        && (!mc.player.isSneaking() || (ModuleManager.noSlow.isEnabled() && ModuleManager.noSlow.sneak.getValue()))
                        && (!mc.player.isUsingItem() || !stopWhileUsing.getValue())
                        && (!ModuleManager.aura.isEnabled() || Aura.target == null || !pauseWhileAura.getValue())
        );""",
      """        boolean wantSprint = mc.player.getHungerManager().getFoodLevel() > 6
                && !mc.player.horizontalCollision
                && thunder.hack.utility.player.InputUtility.forward() > 0
                && (!mc.player.isSneaking() || (ModuleManager.noSlow.isEnabled() && ModuleManager.noSlow.sneak.getValue()))
                && (!mc.player.isUsingItem() || !stopWhileUsing.getValue())
                && (!ModuleManager.aura.isEnabled() || Aura.target == null || !pauseWhileAura.getValue());
        net.minecraft.util.PlayerInput pi = mc.player.input.playerInput;
        mc.player.input.playerInput = new net.minecraft.util.PlayerInput(
                pi.forward(), pi.backward(), pi.left(), pi.right(), pi.jump(), pi.sneak(), wantSprint);""")

# 7. ToolSaver DataComponents -> DataComponentTypes (renamed in 1.21.11 yarn)
patch("7.toolsaver", find_file("ToolSaver.java"),
      "tool.contains(net.minecraft.component.DataComponents.TOOL)",
      "tool.contains(net.minecraft.component.DataComponentTypes.TOOL)")

# 8. BoatFly VehicleMoveC2SPacket(Vec3d, float, float, boolean)
patch("8.vehiclemove", find_file("BoatFly.java"),
      r"new VehicleMoveC2SPacket\((\w+)\)",
      r"new VehicleMoveC2SPacket(new Vec3d(\1.getX(), \1.getY(), \1.getZ()), \1.getYaw(), \1.getPitch(), \1.isOnGround())",
      regex=True)

print("APPLIED:")
for a in applied: print("  +", a)
if missed:
    print("MISSED:")
    for m in missed: print("  -", m)

#!/usr/bin/env python3
# sweep3: 1.21.11 mega-sweep (round 8) — ~90 error patterns from 706-error build
import re, os, sys

ROOT = "src/main/java/thunder/hack"
files = []
for dp, dn, fn in os.walk(ROOT):
    for f in fn:
        if f.endswith(".java"):
            files.append(os.path.join(dp, f))

def read(p):
    with open(p, encoding="utf-8", errors="replace") as fh:
        return fh.read()

def write(p, s):
    with open(p, "w", encoding="utf-8") as fh:
        fh.write(s)

changed = {}
def save(path, txt):
    if path not in changed and read(path) != txt:
        changed[path] = txt
    changed[path] = txt

def ensure_import(path, imp, txt=None):
    if txt is None: txt = changed.get(path) or read(path)
    if imp in txt:
        return txt
    m = re.search(r"package [\w.]+;\n", txt)
    idx = m.end() if m else 0
    return txt[:idx] + "\n" + imp + "\n" + txt[idx:]

# ---------- global regex rules: (regex, repl, flags) ----------
GLOBAL = [
    # A/B: MatrixStack -> PoseStack shim (class deleted in 1.21.11)
    (r"import net\.minecraft\.client\.util\.math\.MatrixStack;", r"import thunder.hack.utility.render.PoseStack;", 0),
    (r"(?<![\w.])MatrixStack\b(?!;)", r"PoseStack", 0),  # keep import line handled above; skip dotted FQN
    (r"(?<![\w.])MatrixStack\b(?=;)", r"PoseStack", 0),
    (r"import net\.minecraft\.client\.util\.math\.Matrix3x2fStack;", r"import org.joml.Matrix3x2fStack;", 0),
    (r"import net\.minecraft\.util\.math\.Matrix3x2fStack;", r"import org.joml.Matrix3x2fStack;", 0),
    # E: velocity
    (r"\.getDeltaMovement\(\)", r".getVelocity()", 0),
    (r"\b(\w+)\.getHorizontalSpeed\(\)", r"Math.hypot(\1.getVelocity().x, \1.getVelocity().z)", 0),
    # entity.getPos() (Vec3d accessor gone)
    (r"\b(mc\.player|player|target|other|entity|ent|pl)\.getPos\(\)\.getX\(\)", r"\1.getX()", 0),
    (r"\b(mc\.player|player|target|other|entity|ent|pl)\.getPos\(\)\.getY\(\)", r"\1.getY()", 0),
    (r"\b(mc\.player|player|target|other|entity|ent|pl)\.getPos\(\)\.getZ\(\)", r"\1.getZ()", 0),
    (r"\b(mc\.player|player|target|other|entity|ent|pl)\.getPos\(\)\.toCenterPos\(\)", r"new Vec3d(\1.getX(), \1.getY(), \1.getZ()).add(0.5, 0.5, 0.5)", 0),
    (r"new Vec3d\(([^;=()]*?)\)\.toCenterPos\(\)", r"new Vec3d(\1).add(0.5, 0.5, 0.5)", 0),
    # J: input framework
    (r"([\w.]+(?:\([^()]*\))?)\.input\.movementForward \*= [^;]*;", r"thunder.hack.utility.player.InputUtility.setForward(false);", 0),
    (r"([\w.]+(?:\([^()]*\))?)\.input\.movementSideways \*= [^;]*;", r"thunder.hack.utility.player.InputUtility.setStrafe(false);", 0),
    (r"([\w.]+(?:\([^()]*\))?)\.input\.movementForward = [-0][^;]*;", r"thunder.hack.utility.player.InputUtility.setForward(false);", 0),
    (r"([\w.]+(?:\([^()]*\))?)\.input\.movementSideways = [-0][^;]*;", r"thunder.hack.utility.player.InputUtility.setStrafe(false);", 0),
    (r"([\w.]+(?:\([^()]*\))?)\.input\.jumping = (true|false);", r"thunder.hack.utility.player.InputUtility.setJump(\2);", 0),
    (r"([\w.]+(?:\([^()]*\))?)\.input\.sneaking = (true|false);", r"thunder.hack.utility.player.InputUtility.setSneak(\2);", 0),
    (r"([\w.]+(?:\([^()]*\))?)\.input\.movementForward\b", r"thunder.hack.utility.player.InputUtility.forward()", 0),
    (r"([\w.]+(?:\([^()]*\))?)\.input\.movementSideways\b", r"thunder.hack.utility.player.InputUtility.strafe()", 0),
    (r"([\w.]+(?:\([^()]*\))?)\.input\.pressingForward\b", r"(thunder.hack.utility.player.InputUtility.forward() > 0)", 0),
    (r"([\w.]+(?:\([^()]*\))?)\.input\.jumping\b", r"thunder.hack.utility.player.InputUtility.jump()", 0),
    (r"([\w.]+(?:\([^()]*\))?)\.input\.sneaking\b", r"thunder.hack.utility.player.InputUtility.sneak()", 0),
    (r"([\w.]+(?:\([^()]*\))?)\.input\.sneaking \|\| ", r"thunder.hack.utility.player.InputUtility.sneak() || ", 0),
    # K: GameProfile record accessors
    (r"\b(\w*[Pp]rofile)\.getName\(\)", r"\1.name()", 0),
    (r"\b(\w*[Pp]rofile)\.getId\(\)", r"\1.id()", 0),
    (r"\b(\w*[Pp]rofile)\.getProperties\(\)", r"\1.properties()", 0),
    (r"GameProfile::getName", r"GameProfile::name", 0),
    (r"\.getProfile\(\)\.getName\(\)", r".getProfile().name()", 0),
    (r"\.getProfile\(\)\.getId\(\)", r".getProfile().id()", 0),
    (r"\.getGameProfile\(\)\.getName\(\)", r".getGameProfile().name()", 0),
    (r"\.getGameProfile\(\)\.getId\(\)", r".getGameProfile().id()", 0),
    (r"\.getGameProfile\(\)\.getProperties\(\)", r".getGameProfile().properties()", 0),
    # tickDelta
    (r"\.getTickDelta\(\)(?!;?\s*\))", r".getTickDelta(false)", 0),
    (r"\.getTickDelta\(\) *\)", r".getTickDelta(false))", 0),
    # M: enchantments idiom
    (r"getOrThrow\(Enchantments\.(\w+)\.getRegistryRef\(\)\)\.getEntry\(Enchantments\.\1\)\.get\(\)", r"getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.\1)", 0),
    (r"(int (\w+) = )EnchantmentHelper\.getLevel\(", r"\1(int) EnchantmentHelper.getLevel(", 0),
    (r"(= )EnchantmentHelper\.getLevel\(([^;]*)\)(?=\s*[;,)])", r"\1(int) EnchantmentHelper.getLevel(\2)", 0),
    # E/L skins
    (r"\.getSkinTextures\(\)\.texture\(\)", r"§SKIN§", 0),
    # X: client command modes
    (r"\.PRESS_SHIFT_KEY\b", r".START_SNEAKING", 0),
    (r"\.RELEASE_SHIFT_KEY\b", r".STOP_SNEAKING", 0),
    (r"\.PRESS_?SPRINT_KEY\b", r".START_SPRINTING", 0),
    (r"\.RELEASE_?SPRINT_KEY\b", r".STOP_SPRINTING", 0),
    # S attributes
    (r"EntityAttributes\.PLAYER_SUBMERGED_MINING_SPEED", r"EntityAttributes.SUBMERGED_MINING_SPEED", 0),
    (r"EntityAttributes\.PLAYER_SNEAKING_SPEED", r"EntityAttributes.SNEAKING_SPEED", 0),
    (r"EntityAttributes\.PLAYER_BLOCK_INTERACTION_RANGE", r"EntityAttributes.BLOCK_INTERACTION_RANGE", 0),
    (r"EntityAttributes\.PLAYER_ENTITY_INTERACTION_RANGE", r"EntityAttributes.ENTITY_INTERACTION_RANGE", 0),
    (r"EntityAttributes\.PLAYER_EXHAUSTION", r"EntityAttributes.EXHAUSTION", 0),
    # T/U/V/JJ
    (r"\bmc\.cameraEntity\b", r"mc.getCameraEntity()", 0),
    (r"ScreenRect\.EMPTY", r"new ScreenRect(0, 0, 0, 0)", 0),
    (r"\.getId\(\)\.toTranslationKey\(\)", r".id().toString()", 0),
    (r"\bSoundEvent\b(?!\.)", r"SoundEvent", 0),
    # TT sneaking etc setters
    (r"(\w+)\.setSneaking\(([^;]*)\);", r"\1.input.playerInput = \1.input.playerInput.withSneak(\2);", 0),
    (r"(\w+)\.setSprinting\(([^;]*)\);", r"\1.input.playerInput = \1.input.playerInput.withSprint(\2);", 0),
    # R rotation setters (field-based ones broken): getYaw() = V pattern
    (r"(\w+)\.getYaw\(\) = ([^;]+);", r"\1.changeLookDirection((\2) - \1.getYaw(), 0);", 0),
    (r"(\w+)\.getPitch\(\) = ([^;]+);", r"\1.changeLookDirection(0, (\2) - \1.getPitch());", 0),
    (r"\b(\w+)\.setYaw\(([^;]+)\);", r"\1.changeLookDirection((\2) - \1.getYaw(), 0);", 0),
    (r"\b(\w+)\.setPitch\(([^;]+)\);", r"\1.changeLookDirection(0, (\2) - \1.getPitch());", 0),
    # NN armor slots
    (r"\.getArmorStack\((\d)\)", r".getStack(§AS§\1)", 0),  # placeholder, fixed below
    # LL drawItem state form
    (r"\.drawItem\(\s*\w+,\s*([^,()]+),\s*([^,()]+),\s*([^,()]+)\s*\)", r".drawItem(\1, \2, \3)", 0),
    # BB on-ground only packets
    (r"PlayerMoveC2SPacket\.OnGroundOnly\(([^,()]*)\)", r"PlayerMoveC2SPacket.OnGroundOnly(\1, false)", 0),
    # DD boat ctor
    (r"new BoatEntity\((\w+(?:\.\w+\(\))?)\)", r"new BoatEntity(\1, () -> net.minecraft.item.Items.OAK_BOAT)", 0),
    # EE use action
    (r"\.getUseAction\(\s*[\w.]+\s*\)", r".getUseAction()", 0),
    # P sendMessage one-arg
    (r"(\w+(?:\.\w+)*)\.sendMessage\(([^,();]*(?:\([^()]*\))?[^,();]*)\);", r"\1.sendMessage(\2, false);", 0),
    # Q client command source
    (r"new ClientCommandSource\((\w+|null), (\w+)\)", r"new ClientCommandSource(\1, \2, net.minecraft.command.permission.PermissionPredicate.ALL)", 0),
    # Y accessor-cast bridge — done in code below
    # GG render state limbAnimator in LER — manual
]

# getArmorStack mapping (36+n) done manually because of the placeholder
ARMOR_RE = re.compile(r"\.getStack\(§AS§(\d)\)")

def fix_armor(txt):
    def rep(m):
        return ".getStack(%d)" % (36 + int(m.group(1)))
    return ARMOR_RE.sub(rep, txt)

SKIN_LIST = re.compile(r"([\w.()]+(?:get\w+\([^()]*\))?)§SKIN§")
def fix_skin(txt):
    def rep(m):
        recv = m.group(1)
        if re.search(r"(entry|ple|listEntry|data|PlayerListEntry)\b", recv):
            return recv + ".getSkinTextures().body().id()"
        return "thunder.hack.utility.SkinUtility.skin(%s)" % recv
    return SKIN_LIST.sub(rep, txt)

CAST_BRIDGE = re.compile(r"\(\((I[A-Z]\w*)\) (?!Object\))([^;()]*(?:\([^()]*\))?[^;()]*)\)\)")
def fix_casts(txt):
    def rep(m):
        return "((%s) (Object) %s)" % (m.group(1), m.group(2))
    return CAST_BRIDGE.sub(rep, txt)

for path in files:
    txt = read(path)
    orig = txt
    for pat, repl, fl in GLOBAL:
        txt = re.sub(pat, repl, txt, flags=fl)
    txt = fix_armor(txt)
    txt = fix_skin(txt)
    txt = fix_casts(txt)
    # entity velocity packet getId
    if "EntityVelocityUpdateS2CPacket" in txt:
        names = set(re.findall(r"EntityVelocityUpdateS2CPacket (\w+) =", txt))
        for n in names:
            txt = re.sub(r"\b%s\.getId\(\)" % n, "%s.getEntityId()" % n, txt)
        txt = re.sub(r"EntityVelocityUpdateS2CPacket\)\s*(\w+(?:\.getPacket\(\))?)\)\.getId\(\)",
                     r"EntityVelocityUpdateS2CPacket) \1).getEntityId()", txt)
    # WorldTimeUpdateS2CPacket getTime
    if "WorldTimeUpdateS2CPacket" in txt:
        names = set(re.findall(r"WorldTimeUpdateS2CPacket (\w+) =", txt))
        for n in names:
            txt = re.sub(r"\b%s\.getTime\(\)" % n, "%s.time()" % n, txt)
        txt = re.sub(r"WorldTimeUpdateS2CPacket\)\s*(\w+(?:\.getPacket\(\))?)\)\.getTime\(\)",
                     r"WorldTimeUpdateS2CPacket) \1).time()", txt)
    # PlayerPositionLookS2CPacket record access
    if "PlayerPositionLookS2CPacket" in txt:
        names = set(re.findall(r"PlayerPositionLookS2CPacket (\w+) =", txt))
        for n in names:
            txt = re.sub(r"\b%s\.getX\(\)" % n, r"%s.change().position().x" % n, txt)
            txt = re.sub(r"\b%s\.getY\(\)" % n, r"%s.change().position().y" % n, txt)
            txt = re.sub(r"\b%s\.getZ\(\)" % n, r"%s.change().position().z" % n, txt)
            txt = re.sub(r"\b%s\.getYaw\(\)" % n, r"%s.change().yaw()" % n, txt)
            txt = re.sub(r"\b%s\.getPitch\(\)" % n, r"%s.change().pitch()" % n, txt)
    # VehicleMoveC2SPacket ctor
    txt = re.sub(r"new VehicleMoveC2SPacket\(([^,()]+), ([^,()]+), ([^,()]+), ([^,()]+), ([^,()]+), ([^,()]+)\)",
                 r"new VehicleMoveC2SPacket(new Vec3d(\1, \2, \3), \4, \5, \6)", txt)
    # drawGuiTexture 5-arg -> 6-arg with pipeline
    txt = re.sub(r"\.drawGuiTexture\(([^,()]+), ([^,()]+), ([^,()]+), ([^,()]+), ([^,()]+)\)",
                 r".drawGuiTexture(net.minecraft.client.render.RenderPipelines.GUI_TEXTURED, \1, \2, \3, \4, \5)", txt)
    if "§SKIN§" in txt:
        print("UNRESOLVED SKIN in", path); sys.exit(1)
    if txt != orig:
        if "org.joml.Matrix3x2fStack" in txt or "PoseStack" in txt:
            pass
        save(path, txt)

# import hygiene on touched files
for path, txt in list(changed.items()):
    if "PoseStack" in txt and "import thunder.hack.utility.render.PoseStack;" not in txt and "thunder/hack/utility/render/PoseStack.java" not in path:
        # files inside utility/render can rely on same-package
        if "/utility/render/" not in path.replace(os.sep, "/"):
            txt = ensure_import(path, "import thunder.hack.utility.render.PoseStack;", txt)
    if "use of thunder.hack.utility.player.InputUtility" :
        pass
    if "InputUtility." in txt:
        pass
    if "UseAction" in txt and "import net.minecraft.item.UseAction;" in txt:
        txt = txt.replace("import net.minecraft.item.UseAction;", "import net.minecraft.item.consume.UseAction;")
    if "UseAction" in txt and "import" not in txt.split("UseAction")[0][-120:]:
        if "consume.UseAction" not in txt and "import net.minecraft.item.UseAction" not in txt:
            if re.search(r"\bUseAction\b", txt):
                txt = ensure_import(path, "import net.minecraft.item.consume.UseAction;", txt)
    if "RegistryKeys.ENCHANTMENT" in txt and "import net.minecraft.registry.RegistryKeys;" not in txt:
        txt = ensure_import(path, "import net.minecraft.registry.RegistryKeys;", txt)
    if "RenderPipelines.GUI_TEXTURED" in txt and "import net.minecraft.client.render.RenderPipelines;" not in txt:
        txt = ensure_import(path, "import net.minecraft.client.render.RenderPipelines;", txt)
    save(path, txt)

for p, t in changed.items():
    write(p, t)
print("sweep3: %d files rewritten" % len(changed))

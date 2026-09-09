#!/usr/bin/env python3
# Phase-5 semantic sweep #1: global mechanical fixes for 1.21.11 (delta/Expensive-verified)
import re, os, sys

ROOT = "src/main/java"
stats = {}

def files():
    for dp, dn, fn in os.walk(ROOT):
        for f in fn:
            if f.endswith(".java"):
                yield os.path.join(dp, f)

def sub(name, pat, rep, cond=None, flags=0):
    rx = re.compile(pat, flags)
    n = 0
    for path in files():
        src = open(path, encoding="utf-8", errors="replace").read()
        if cond and not cond(src):
            continue
        out, cnt = rx.subn(rep, src)
        if cnt:
            open(path, "w", encoding="utf-8").write(out)
            n += cnt
    stats[name] = n

# S1: duplicated getMatrices assignment lines
sub("S1-dup-matrices",
    r"Matrix3x2fStack (\w+) = context\.getMatrices\(\) = \w+ = context\.getMatrices\(\);",
    r"Matrix3x2fStack \1 = context.getMatrices();")

# S5: VertexFormat class moved to blaze3d (VertexFormat is a top-level java class, not package!)
sub("S5-vf-import",
    r"import net\.minecraft\.client\.render\.VertexFormat;",
    "import com.mojang.blaze3d.vertex.VertexFormat;")
sub("S5b-vf-drawmode-import",
    r"import net\.minecraft\.client\.render\.VertexFormat\.DrawMode;",
    "import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;")

# S5c: files using VertexFormat.DrawMode but having NO import for it -> insert blaze3d import
n5c = 0
for path in files():
    src = open(path, encoding="utf-8", errors="replace").read()
    if re.search(r"VertexFormat\.(DrawMode|builder)", src) and "import com.mojang.blaze3d.vertex.VertexFormat;" not in src and "import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;" not in src:
        lines = src.split("\n")
        imps = [i for i,l in enumerate(lines) if l.startswith("import ")]
        if imps:
            lines.insert(imps[0], "import com.mojang.blaze3d.vertex.VertexFormat;")
        else:
            pkg = max(i for i,l in enumerate(lines) if l.startswith("package "))
            lines.insert(pkg+1, "\nimport com.mojang.blaze3d.vertex.VertexFormat;")
        open(path, "w", encoding="utf-8").write("\n".join(lines))
        n5c += 1
stats["S5c-add-vf-import"] = n5c

# S6: VertexFormats.LINES removed
sub("S6-lines", r"\bVertexFormats\.LINES\b", "VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH")

# S7: BufferRenderer.drawWithGlobalProgram(x.end()) -> engine flush
n7 = 0
for path in files():
    src = open(path, encoding="utf-8", errors="replace").read()
    if "BufferRenderer.drawWithGlobalProgram" not in src:
        continue
    out = re.sub(r"BufferRenderer\.drawWithGlobalProgram\((\w+)\.end\(\)\);",
                 r"Render2DEngine.endBuilding(\1);", src)
    out = re.sub(r"BufferRenderer\.drawWithGlobalProgram\((\w+)\.end\(\)\)",
                 r"Render2DEngine.endBuilding(\1)", out)
    out = out.replace("import net.minecraft.client.render.BufferRenderer;\n", "")
    if "Render2DEngine.endBuilding" in out and "import thunder.hack.utility.render.Render2DEngine;" not in out:
        lines = out.split("\n")
        imps = [i for i,l in enumerate(lines) if l.startswith("import ")]
        if imps:
            lines.insert(imps[0], "import thunder.hack.utility.render.Render2DEngine;")
        out = "\n".join(lines)
    if out != src:
        open(path, "w", encoding="utf-8").write(out)
        n7 += 1
stats["S7-draw"] = n7

# S8: peek().getPositionMatrix() (Matrix3x2fStack has no peek) -> bake via new Matrix4f().set(last())
sub("S8-peek", r"(\w+)\.getMatrices\(\)\.peek\(\)\.getPositionMatrix\(\)",
    r"new Matrix4f().set(\1.getMatrices().last())")

# S9: entity pos. getPos() removed on Entity; getX/getY/getZ stay.
ENT = r"(?:mc\.player|this\.player|playerEntity|player|entity|ent|target|livingEntity|otherEntity|thePlayer|e)"
sub("S9a", ENT + r"\.getPos\(\)\.x", lambda m: re.sub(r"\.getPos\(\)\.x", ".getX()", m.group(0)))
sub("S9b", ENT + r"\.getPos\(\)\.y", lambda m: re.sub(r"\.getPos\(\)\.y", ".getY()", m.group(0)))
sub("S9c", ENT + r"\.getPos\(\)\.z", lambda m: re.sub(r"\.getPos\(\)\.z", ".getZ()", m.group(0)))
def vec3_rep(m):
    r = m.group(1)
    return f"new Vec3d({r}.getX(), {r}.getY(), {r}.getZ())"
sub("S9d", r"\b(" + ENT + r")\.getPos\(\)", vec3_rep)

# Camera
sub("S9e-camera", r"getCamera\(\)\.getPos\(\)", "getCameraPos()")

# prev fields: only for entity receivers followed by non-method char
def prev_rep(ax):
    def f(m):
        r = m.group(1)
        return f"({r}.get{ax.upper()}() - {r}.getDeltaMovement().{ax})"
    return f
for ax in "xyz":
    sub(f"S9f-prev{ax.upper()}", r"\b(" + ENT + r")\.prev" + ax.upper() + r"(?!\w)", prev_rep(ax))
sub("S9g", r"\b(" + ENT + r")\.prevYaw(?!\w)", r"\1.getYaw()")
sub("S9h", r"\b(" + ENT + r")\.prevPitch(?!\w)", r"\1.getPitch()")
sub("S9i", r"\b(" + ENT + r")\.prevHeadYaw(?!\w)", r"\1.getHeadYaw()")
sub("S9j", r"\b(" + ENT + r")\.prevBodyYaw(?!\w)", r"\1.getBodyYaw()")

sub("S9k-hspeed-prev", r"\b(" + ENT + r")\.prevHorizontalSpeed(?!\w)", r"\1.getHorizontalSpeed()")
sub("S9l-hspeed", r"\b(" + ENT + r")\.horizontalSpeed(?!\w)",
    lambda m: f"(Math.hypot({m.group(1)}.getDeltaMovement().x, {m.group(1)}.getDeltaMovement().z))")
sub("S9m-stride", r"\b(" + ENT + r")\.prevStrideDistance(?!\w)", r"0.0F")
sub("S9n-stride", r"\b(" + ENT + r")\.strideDistance(?!\w)", r"0.0F")

# limb animator
sub("S10-limb1", r"limbAnimator\.getPos\(", "limbAnimator.getAnimationProgress(")
sub("S10-limb2", r"limbAnimator\.getSpeed\((?:[^()]|\([^()]*\))*\)", "limbAnimator.getSpeed()")
sub("S10-limb3", r"limbAnimator\.pos(?![\w(])", "limbAnimator.getAnimationProgress()")
sub("S10-limb4", r"limbAnimator\.speed(?![\w(])", "limbAnimator.getSpeed()")

# fall flying
sub("S11-ff1", r"\.isFallFlying\(\)", ".isGliding()")

# profiler no-op (class gone from client)
sub("S13-prof", r"\n\s*mc\.getProfiler\(\)\.(push|pop|swap)\([^;]*\);", "\n", flags=0)

# attributes renames
sub("S15-attrs", r"EntityAttributes\.GENERIC_", "EntityAttributes.")

# registry manager get->getOrThrow
sub("S16-reg", r"getRegistryManager\(\)\.get\(", "getRegistryManager().getOrThrow(")

# S3: 3-arg scale on 3x2 stacks (getMatrices() receiver only, z==1-ish)
sub("S3-scale-gm", r"(\w+)\.getMatrices\(\)\.scale\(([^();]*)\s*,([^();]*?)\s*,\s*(?:1\.0F|1\.0f|1f|1)\s*\);",
    r"\1.getMatrices().scale(\2,\3);")

# S4: 2-arg translate on getMatrices() -> (float) casts
def tr_rep(m):
    recv, args = m.group(1), m.group(2)
    parts = [a.strip() for a in re.split(r",(?![^()]*\))", args)]
    if len(parts) == 2:
        return f"{recv}.getMatrices().translate((float) ({parts[0]}), (float) ({parts[1]}));"
    if len(parts) == 3 and parts[2] in ("0f", "0.0f", "0"):
        return f"{recv}.getMatrices().translate((float) ({parts[0]}), (float) ({parts[1]}));"
    return m.group(0)
sub("S4-translate", r"(\w+)\.getMatrices\(\)\.translate\(([^();]+)\);", tr_rep)

# drawItemInSlot -> drawStackOverlay
sub("S17-slot", r"\.drawItemInSlot\(", ".drawStackOverlay(")

print("\n".join(f"{k}: {v}" for k, v in sorted(stats.items())))

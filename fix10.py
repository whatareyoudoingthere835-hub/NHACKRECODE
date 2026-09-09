#!/usr/bin/env python3
# fix10 (round-12): RotationAxis now returns Quaternionf -> .multiply( not multiplyPositionMatrix;
# RenderTickCounter.getTickDelta -> getTickProgress; PlayerInput pkg net.minecraft.util; Vec3d record getters;
# Session 5-arg; getScoreboard via world; NativeImage.copyPixelsAbgr; joml 1.12 layout m20/m21; potion StatusEffectInstance getters.
import re, os

ROOT = '/home/user/NHACKRECODE/src/main/java/thunder/hack'
def files():
    for d, _, fs in os.walk(ROOT):
        for f in fs:
            if f.endswith('.java'):
                yield os.path.join(d, f)
def rd(p): return open(p, encoding='utf-8').read()
def wr(p, s): open(p, 'w', encoding='utf-8').write(s)
log = []

# A. RotationAxis -> Quaternionf: undo multiplyPositionMatrix for RotationAxis (MatrixStack.multiply(Quaternionfc) is the right call)
cnt = 0
for p in files():
    s = rd(p); o = s
    s = s.replace('.multiplyPositionMatrix(RotationAxis.', '.multiply(RotationAxis.')
    if s != o: cnt += s.count('.multiply(RotationAxis.'); wr(p, s); log.append(f'rotation->multiply {os.path.relpath(p, ROOT)}')
log.append(f'A) multiplyPositionMatrix(RotationAxis->multiply sites: {cnt}')

# B. tickDelta on RenderTickCounter -> getTickProgress
for p in files():
    s = rd(p); o = s
    s = s.replace('tickCounter.getTickDelta(false)', 'tickCounter.getTickProgress(false)')
    s = s.replace('getRenderTickCounter().getTickDelta(false)', 'getRenderTickCounter().getTickProgress(false)')
    if s != o: wr(p, s); log.append(f'tickProgress {os.path.relpath(p, ROOT)}')

# C. PlayerInput package
for p in files():
    s = rd(p); o = s
    s = s.replace('import net.minecraft.client.input.PlayerInput;', 'import net.minecraft.util.PlayerInput;')
    if s != o: wr(p, s); log.append(f'PlayerInput pkg {os.path.relpath(p, ROOT)}')

# D. Double-getInstance corruption
for p in files():
    s = rd(p); o = s
    s = s.replace('MinecraftClient.getInstance().net.minecraft.client.MinecraftClient.getInstance()', 'MinecraftClient.getInstance()')
    if s != o: wr(p, s); log.append(f'double-getInstance fixed {os.path.relpath(p, ROOT)}')

# E. Render3DEngine Camera import
p = os.path.join(ROOT, 'utility/render/Render3DEngine.java')
s = rd(p)
if 'import net.minecraft.client.render.Camera;' not in s:
    s = s.replace('import net.minecraft.client.render.BufferBuilder;', 'import net.minecraft.client.render.BufferBuilder;\nimport net.minecraft.client.render.Camera;', 1)
    log.append('Camera import added to Render3DEngine')
    wr(p, s)

# F. LoginCommand Session 5-arg
for d, ds, fs in os.walk(ROOT):
    if 'LoginCommand.java' in fs:
        p = os.path.join(d, 'LoginCommand.java'); s = rd(p); o = s
        s = s.replace('new Session(name, Uuids.getOfflinePlayerUuid(name), "", "", "")',
                      'new Session(name, Uuids.getOfflinePlayerUuid(name), "", Optional.empty(), Optional.empty())')
        if s != o: wr(p, s); log.append('LoginCommand Session -> 5-arg')

# G. Scoreboard access
p = os.path.join(ROOT, 'features/hud/impl/TargetHud.java'); s = rd(p); o = s
s = s.replace('(ent.getScoreboard()).getObjectiveForSlot', '(ent.getEntityWorld().getScoreboard()).getObjectiveForSlot')
s = s.replace('ReadableScoreboardScore readableScoreboardScore = ent.getScoreboard().getScore(ent, scoreBoard);',
              'ReadableScoreboardScore readableScoreboardScore = ent.getEntityWorld().getScoreboard().getScore(ent, scoreBoard);')
if s != o: wr(p, s); log.append('TargetHud getScoreboard via world')
for d, ds, fs in os.walk(ROOT):
    if 'RctCommand.java' in fs:
        p = os.path.join(d, 'RctCommand.java'); s = rd(p); o = s
        s = s.replace('mc.player.getScoreboard().getObjectives()', 'mc.world.getScoreboard().getObjectives()')
        if s != o: wr(p, s); log.append('RctCommand getScoreboard via world')

# H. Quiver: Iterable of StatusEffectInstance
p = os.path.join(ROOT, 'features/modules/combat/Quiver.java'); s = rd(p); o = s
s = s.replace('var effs = contents.getEffects().toList();', 'var effs = contents.getEffects();')
s = s.replace('ap.effect().value().getTranslationKey()', 'ap.getEffectType().value().getTranslationKey()')
if s != o: wr(p, s); log.append('Quiver Iterable + getEffectType fixed')

# I. Avoid Vec3d getters
for d, ds, fs in os.walk(ROOT):
    if 'Avoid.java' in fs:
        p = os.path.join(d, 'Avoid.java'); s = rd(p); o = s
        s = s.replace('!mc.world.isChunkLoaded(new Vec3d(e.getX(), e.getY(), e.getZ()).getX() >> 4, new Vec3d(e.getX(), e.getY(), e.getZ()).getZ() >> 4) && unloaded.getValue()',
                      '!mc.world.isChunkLoaded((int) Math.floor(e.getX()) >> 4, (int) Math.floor(e.getZ()) >> 4) && unloaded.getValue()')
        s = s.replace('boolean avoidVoid = new Vec3d(e.getX(), e.getY(), e.getZ()).getY() < mc.world.getBottomY() && voidAir.getValue();',
                      'boolean avoidVoid = e.getY() < mc.world.getBottomY() && voidAir.getValue();')
        if s != o: wr(p, s); log.append('Avoid Vec3d record getters fixed')

# J. NoBob strideDistance
p = os.path.join(ROOT, 'features/modules/render/NoBob.java'); s = rd(p); o = s
s = s.replace('float h = mc.player.strideDistance;', 'float h = 1f; // 1.21.11: strideDistance no longer exposed; unit amplitude')
if s != o: wr(p, s); log.append('NoBob strideDistance removed')

# K. prev[XYZ]: entity assignments -> comment out; entity reads -> (getX() - getVelocity().x())
# skip our own classes' this.prevX fields (AbstractCategory etc.)
for p in files():
    s = rd(p); o = s
    def fix_line(m):
        ind, body = m.group(1), m.group(2)
        if 'this.prev' in body or 'double prev' in body or 'float prev' in body: return m.group(0)
        if re.search(r'\.\s*prev[XYZ]\s*(=[^=]|\+=|-=)', body):
            return f'{ind}// 1.21.11: Entity.prevX/Y/Z removed - dropped: {body.strip()}'
        new = re.sub(r'\b(\w+)\.prev([XYZ])\b(?!\s*[=+\-*/]?=)',
                     lambda x: f'({x.group(1)}.get{x.group(2)}() - {x.group(1)}.getVelocity().{x.group(2).lower()})', body)
        return ind + new if new != body else m.group(0)
    s = re.sub(r'^([ \t]*)(.*)$', fix_line, s, flags=re.M)
    if s != o: wr(p, s); log.append(f'prev[XYZ] fixed {os.path.relpath(p, ROOT)}')

# L. NativeImage pixel copy -> copyPixelsAbgr
p = os.path.join(ROOT, 'injection/MixinMinecraftClient.java'); s = rd(p); o = s
s = re.sub(r'                java\.nio\.IntBuffer pixelBuffer = bytebuffer\.asIntBuffer\(\);\n                for \(int py = 0; py < nativeImage\.getWidth\(\); py\+\+\)\n                    for \(int px = 0; px < nativeImage\.getHeight\(\); px\+\+\)\n                        pixelBuffer\.put\(nativeImage\.getPixelColor\(px, py\)\);',
'''                bytebuffer.asIntBuffer().put(nativeImage.copyPixelsAbgr());''', s)
if s != o: wr(p, s); log.append('NativeImage copyPixelsAbgr fixed')

# M. NativeImageBackedTexture 2-arg in ThunderUtility
for d, ds, fs in os.walk(ROOT):
    if 'ThunderUtility.java' in fs:
        p = os.path.join(d, 'ThunderUtility.java'); s = rd(p); o = s
        s = s.replace('new NativeImageBackedTexture(NativeImage.read(new FileInputStream(IMAGES_FOLDER + "/" + name + ".png")))',
                      'new NativeImageBackedTexture(id::toString, NativeImage.read(new FileInputStream(IMAGES_FOLDER + "/" + name + ".png")))')
        if s != o: wr(p, s); log.append('ThunderUtility NativeImageBackedTexture(id::toString, ...) fixed')

# N. Render2DEngine joml 1.12 layout (m00,m01,m10,m11,m20,m21; translation = m20/m21)
p = os.path.join(ROOT, 'utility/render/Render2DEngine.java'); s = rd(p); o = s
s = s.replace('''        float ax = (float) (m.m00 * r1.x() + m.m01 * r1.y() + m.m02);
        float ay = (float) (m.m10 * r1.x() + m.m11 * r1.y() + m.m12);
        float bx = (float) (m.m00 * r1.x1() + m.m01 * r1.y1() + m.m02);
        float by = (float) (m.m10 * r1.x1() + m.m11 * r1.y1() + m.m12);''',
'''        float ax = (float) (m.m00 * r1.x() + m.m10 * r1.y() + m.m20);
        float ay = (float) (m.m01 * r1.x() + m.m11 * r1.y() + m.m21);
        float bx = (float) (m.m00 * r1.x1() + m.m10 * r1.y1() + m.m20);
        float by = (float) (m.m01 * r1.x1() + m.m11 * r1.y1() + m.m21);''')
s = s.replace('''        return new Matrix4f(
                t.m00, t.m01, 0f, t.m02,
                t.m10, t.m11, 0f, t.m12,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f);''',
'''        return new Matrix4f(
                t.m00, t.m10, 0f, t.m20,
                t.m01, t.m11, 0f, t.m21,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f);''')
if s != o: wr(p, s); log.append('Render2DEngine joml 1.12 layout fixed')

# O. MixinAbstractBlock enchant lookup via getOrThrow
p = os.path.join(ROOT, 'injection/MixinAbstractBlock.java'); s = rd(p); o = s
s = s.replace('int effect = (int) EnchantmentHelper.getLevel(mc.world.getRegistryManager().getOrThrow(EFFICIENCY.getRegistryRef()).getEntry(EFFICIENCY).get(), stack);',
              'int effect = EnchantmentHelper.getLevel(mc.world.getRegistryManager().getOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT).getOrThrow(EFFICIENCY), stack);')
if s != o: wr(p, s); log.append('MixinAbstractBlock getOrThrow fixed')

# audit
import subprocess
out = subprocess.run(['grep', '-rn', '-E', r'multiplyPositionMatrix\(RotationAxis|client\.input\.PlayerInput|tickCounter\.getTickDelta|getRenderTickCounter\(\)\.getTickDelta|AccountType|\bstrideDistance\b|getPixelColor|\.getEffects\(\)\.toList|getInstance\(\)\.net\.minecraft', ROOT], capture_output=True, text=True).stdout
print('AUDIT leftovers:')
print(out if out.strip() else 'CLEAN')
out2 = subprocess.run(['grep', '-rn', r'\.prev[XYZ]\b', ROOT, '--include=*.java'], capture_output=True, text=True).stdout
print('prev refs left:'); print(out2)
for l in log: print('*', l)

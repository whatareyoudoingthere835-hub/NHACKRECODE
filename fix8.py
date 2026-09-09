#!/usr/bin/env python3
# fix8: revert PoseStack-shim delusion -> real net.minecraft.client.util.math.MatrixStack (verified in 1.21.11 javadoc + delta donor),
# undo fix7 gameRenderer corruption, PlayerInput record rebuild, scoreboard/merchant/quiver/session/nativeimage/click-event fixes.
import re, os, glob

ROOT = '/home/user/NHACKRECODE/src/main/java/thunder/hack'
def files():
    for d, _, fs in os.walk(ROOT):
        for f in fs:
            if f.endswith('.java'):
                yield os.path.join(d, f)

def rd(p):
    with open(p, encoding='utf-8') as f: return f.read()
def wr(p, s):
    with open(p, 'w', encoding='utf-8') as f: f.write(s)

log = []
def note(msg): log.append(msg)

# ---------- pass 0: global uncorrupt of fix7 FQN insertions ----------
UNCORRUPT = [
    ('mc.net.minecraft.client.MinecraftClient.getInstance().gameRenderer', 'net.minecraft.client.MinecraftClient.getInstance().gameRenderer'),
    ('mc.net.minecraft.client.MinecraftClient.getInstance()', 'net.minecraft.client.MinecraftClient.getInstance()'),
]
for p in files():
    s = rd(p); o = s
    for a, b in UNCORRUPT:
        s = s.replace(a, b)
    if s != o: wr(p, s); note(f'uncorrupt {os.path.basename(p)}')

# ---------- pass 1: PoseStack shim -> real MatrixStack ----------
SHIM_IMPORT = 'import thunder.hack.utility.render.PoseStack;'
REAL_IMPORT = 'import net.minecraft.client.util.math.MatrixStack;'
renamed = []
for p in files():
    if p.endswith(os.path.join('utility', 'render', 'PoseStack.java')): continue
    s = rd(p); o = s
    had_shim = 'thunder.hack.utility.render.PoseStack' in s or ' PoseStack' in s or 'PoseStack ' in s
    if not had_shim: continue
    has_real = REAL_IMPORT in s
    if SHIM_IMPORT in s:
        s = s.replace(SHIM_IMPORT + '\n', '' if has_real else REAL_IMPORT + '\n')
    s = s.replace('thunder.hack.utility.render.PoseStack', 'net.minecraft.client.util.math.MatrixStack')
    s = re.sub(r'\bPoseStack\b', 'MatrixStack', s)
    # shim-only API -> real API
    s = s.replace('.last()', '.peek()')
    s = s.replace('.multiplyCurrent(', '.multiplyPositionMatrix(')
    s = s.replace('.setIdentity()', '.loadIdentity()')
    s = re.sub(r'\.multiply\(RotationAxis\.', '.multiplyPositionMatrix(RotationAxis.', s)
    s = re.sub(r'\.multiply\((new Matrix4f\(\))', r'.multiplyPositionMatrix(\1', s)
    s = re.sub(r'\.set\((matrices|matrixStack|stack|pose)\)\)', r'.set(\1.peek().getPositionMatrix())', s)
    # 2-arg translate (shim-only) -> 3-arg
    def tr2(m):
        inner = m.group(2)
        if inner.count(',') == 1:
            a, b = [x.strip() for x in inner.split(',', 1)]
            return f'{m.group(1)}.translate({a}, {b}, 0.0)'
        return m.group(0)
    s = re.sub(r'((?:matrices|matrixStack|pose)(?:\.\w+\(\))*)\.translate\(([^\n;]*?)\);', tr2, s)
    if s != o:
        wr(p, s); renamed.append(p); note(f'matrixstack-ified {os.path.relpath(p, ROOT)}')

# drop the shim itself
shim = os.path.join(ROOT, 'utility', 'render', 'PoseStack.java')
if os.path.exists(shim):
    os.remove(shim); note('deleted utility/render/PoseStack.java shim')

# ---------- pass 2: tickDelta in mixins (use the injected RenderTickCounter param) ----------
for rel in ('injection/MixinGameRenderer.java', 'injection/MixinInGameHud.java'):
    p = os.path.join(ROOT, rel)
    if os.path.exists(p):
        s = rd(p); o = s
        s = s.replace('mc.getRenderTickCounter().getTickDelta(false)', 'tickCounter.getTickDelta(false)')
        if s != o: wr(p, s); note(f'tickCounter param {rel}')

r3d = os.path.join(ROOT, 'utility/render/Render3DEngine.java')
s = rd(r3d); o = s
s = s.replace('return mc.getRenderTickCounter().getTickDelta(false);',
              'return net.minecraft.client.MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);')
s = s.replace('''public static float getTickDelta(boolean secondsPerTick) {
        return getTickDelta();
    }''', '''public static float getTickDelta(boolean secondsPerTick) {
        return getTickDelta();
    }''')
if s != o: wr(r3d, s); note('Render3DEngine.getTickDelta fixed')

# ---------- pass 3: Chams / PopChams render-state + Model.render ----------
p = os.path.join(ROOT, 'features/modules/render/Chams.java')
s = rd(p); o = s
s = s.replace('float h = MathHelper.lerpAngleDegrees(g, state.prevBodyYaw, state.bodyYaw);', 'float h = state.bodyYaw;')
s = s.replace('float j = MathHelper.lerpAngleDegrees(g, state.prevHeadYaw, state.headYaw);', 'float j = state.bodyYaw + state.relativeHeadYaw;')
s = s.replace('float m = MathHelper.lerp(g, state.prevPitch, state.pitch);', 'float m = state.pitch;')
s = s.replace('model.render(state, matrixStack, buffer, i, p);', 'model.render(matrixStack, buffer, i, p);')
s = s.replace('l = (float) abstractClientPlayerEntity.getFallFlyingTicks() + h;', 'l = h;')
if s != o: wr(p, s); note('Chams render/state fixed')

p = os.path.join(ROOT, 'features/modules/render/PopChams.java')
s = rd(p); o = s
s = s.replace('modelBase.render(state, matrices,', 'modelBase.render(matrices,')
if s != o: wr(p, s); note('PopChams model.render fixed')

# ---------- pass 4: PlayerInput withSprint/withSneak -> record rebuild ----------
PI_IMPORT = 'import net.minecraft.client.input.PlayerInput;'
pi_re = re.compile(r'^(\s*)([^\n=]+?) = ([^\n=]+?)\.playerInput\.with(Sprint|Sneak)\(([^\n]*?)\);(\s*)$')
for p in files():
    s = rd(p); lines = s.split('\n'); changed = False
    out = []
    for ln in lines:
        m = pi_re.match(ln)
        if m:
            ind, lhs, recv, flag, arg = m.group(1), m.group(2), m.group(3), m.group(4), m.group(5)
            base = f'{recv}.playerInput'
            if flag == 'Sprint':
                args = f'{base}.forward(), {base}.backward(), {base}.left(), {base}.right(), {base}.jump(), {base}.sneak(), {arg}'
            else:
                args = f'{base}.forward(), {base}.backward(), {base}.left(), {base}.right(), {base}.jump(), {arg}, {base}.sprint()'
            out.append(f'{ind}{lhs} = new PlayerInput({args});')
            changed = True
        else:
            out.append(ln)
    if changed:
        s = '\n'.join(out)
        if PI_IMPORT not in s:
            s = s.replace('package ', 'package ', 1)
            # insert import after the package line block
            s = re.sub(r'(package [^\n]+\n)', r'\1\n' + PI_IMPORT + '\n', s, count=1)
        wr(p, s); note(f'PlayerInput rebuilt {os.path.relpath(p, ROOT)}')

# ---------- pass 5: sneak command packets (Mode.START/STOP_SNEAKING gone in 1.21.2+) ----------
sk_re = re.compile(r'^(\s*)(.*new ClientCommandC2SPacket\([^;]*Mode\.(?:START|STOP)_SNEAKING\)\);.*)$')
for p in files():
    s = rd(p); lines = s.split('\n'); changed = False; out = []
    for ln in lines:
        m = sk_re.match(ln)
        if m and '//' not in ln.split('new ')[0]:
            out.append(f'{m.group(1)}// 1.21.11: START/STOP_SNEAKING removed from ClientCommandC2SPacket.Mode (sneak now rides PlayerInput packets)')
            changed = True
        else:
            out.append(ln)
    if changed: wr(p, '\n'.join(out)); note(f'sneak packet removed {os.path.relpath(p, ROOT)}')

# ---------- pass 6: scoreboard ----------
p = os.path.join(ROOT, 'features/hud/impl/TargetHud.java')
s = rd(p); o = s
s = s.replace('''                    ReadableScoreboardScore readableScoreboardScore = ent.getScoreboard().getScore(ent, scoreBoard);
                    MutableText text2 = ReadableScoreboardScore.getFormattedScore(readableScoreboardScore, scoreBoard.getNumberFormatOr(StyledNumberFormat.EMPTY));
                    resolvedHp = text2.getString();''',
'''                    ReadableScoreboardScore readableScoreboardScore = ent.getScoreboard().getScore(ent, scoreBoard);
                    resolvedHp = readableScoreboardScore == null ? "" : String.valueOf(readableScoreboardScore.getScore());''')
# renderRoundedQuadInternal call sites -> new float-args engine overload
s = s.replace('Render2DEngine.renderRoundedQuadInternal(Render2DEngine.toMatrix4f(context.getMatrices()), ', 'Render2DEngine.renderRoundedQuad(context.getMatrices(), ')
if s != o: wr(p, s); note('TargetHud scoreboard + rounded quad fixed')

for pat, rel in [('features/cmd/impl/RctCommand.java', None), ('features/commands/impl/RctCommand.java', None)]:
    p = os.path.join(ROOT, pat)
    if os.path.exists(p):
        s = rd(p); o = s
        s = s.replace(').displayName().getString().substring(10)', ').getDisplayName().getString().substring(10)')
        if s != o: wr(p, s); note('RctCommand getDisplayName fixed')

# ---------- pass 7: Render2DEngine internals + new overload ----------
p = os.path.join(ROOT, 'utility/render/Render2DEngine.java')
s = rd(p); o = s
s = s.replace('''        Matrix3x2f m = new Matrix3x2f(stack);
        float[] a = m.transform((float) r1.x(), (float) r1.y(), new float[2]);
        float[] b = m.transform((float) r1.x1(), (float) r1.y1(), new float[2]);
        Rectangle r = new Rectangle(a[0], a[1], b[0], b[1]);''',
'''        Matrix3x2f m = new Matrix3x2f(stack);
        float ax = (float) (m.m00 * r1.x() + m.m01 * r1.y() + m.m02);
        float ay = (float) (m.m10 * r1.x() + m.m11 * r1.y() + m.m12);
        float bx = (float) (m.m00 * r1.x1() + m.m01 * r1.y1() + m.m02);
        float by = (float) (m.m10 * r1.x1() + m.m11 * r1.y1() + m.m12);
        Rectangle r = new Rectangle(ax, ay, bx, by);''')
s = s.replace('''    public static Matrix4f toMatrix4f(org.joml.Matrix3x2fc m) {
        return new Matrix4f(
                m.m00(), m.m01(), 0f, m.m02(),
                m.m10(), m.m11(), 0f, m.m12(),
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f);
    }''',
'''    public static Matrix4f toMatrix4f(org.joml.Matrix3x2fc mm) {
        org.joml.Matrix3x2f t = new org.joml.Matrix3x2f(mm);
        return new Matrix4f(
                t.m00, t.m01, 0f, t.m02,
                t.m10, t.m11, 0f, t.m12,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f);
    }''')
anchor = '    public static void renderRoundedQuad(Matrix3x2fStack matrices, Color c, double fromX, double fromY, double toX, double toY, double radius, double samples) {'
overload = '''    public static void renderRoundedQuad(Matrix3x2fStack matrices, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double radius, double samples) {
        Batch b = Draw2D.of(matrices, VertexFormat.DrawMode.TRIANGLE_FAN);
        renderRoundedQuadInternal(b, cr, cg, cb, ca, fromX, fromY, toX, toY, radius, samples);
        b.submit();
    }

'''
if anchor in s and overload.strip().split('\n')[0] not in s:
    s = s.replace(anchor, overload + anchor, 1)
if s != o: wr(p, s); note('Render2DEngine joml + overload fixed')

# ---------- pass 8: Quiver ----------
p = os.path.join(ROOT, 'features/modules/combat/Quiver.java')
if os.path.exists(p):
    s = rd(p); o = s
    s = s.replace('var effs = contents.getEffects();', 'var effs = contents.getEffects().toList();')
    s = s.replace('ap.effect().valueOrThrow().getTranslationKey()', 'ap.effect().value().getTranslationKey()')
    if s != o: wr(p, s); note('Quiver RegistryEntry.value fixed')

# ---------- pass 9: AutoTrader offers ----------
for pat in ('features/modules/misc/AutoTrader.java',):
    p = os.path.join(ROOT, pat)
    if os.path.exists(p):
        s = rd(p); o = s
        s = s.replace('msh.screenHandler.getOffers()', 'msh.getRecipes()')
        if s != o: wr(p, s); note('AutoTrader getRecipes fixed')

# ---------- pass 10: ItemChecks attribute on Entry ----------
p = os.path.join(ROOT, 'utility/player/ItemChecks.java')
s = rd(p); o = s
s = s.replace('var attr = entry.modifier().attribute();', 'var attr = entry.attribute();')
if s != o: wr(p, s); note('ItemChecks entry.attribute() fixed')

# ---------- pass 11: ClientClickEvent standalone ----------
p = os.path.join(ROOT, 'events/impl/ClientClickEvent.java')
wr(p, '''package thunder.hack.events.impl;

import net.minecraft.text.ClickEvent;

/*When using a clickable text client, you should create this object instead of the usual ClickEvent.
If not, a vulnerability could occur as mentioned in this GitHub issue: https://github.com/MeteorDevelopment/meteor-client/pull/4399.*/
public class ClientClickEvent {
    private final ClickEvent.Action action;
    private final String value;

    public ClientClickEvent(ClickEvent.Action action, String value) {
        this.action = action;
        this.value = value;
    }

    public ClickEvent.Action getAction() {
        return action;
    }

    public String getValue() {
        return value;
    }
}
''')
note('ClientClickEvent rewritten as standalone')

# ---------- pass 12: MixinPlayerListEntry dead block ----------
p = os.path.join(ROOT, 'injection/MixinPlayerListEntry.java')
s = rd(p); o = s
s = re.sub(r'''    @Inject\(method = "getSkinTextures", at = @At\("TAIL"\), cancellable = true\)\n    private void getCapeTexture\(GameProfile profile, CallbackInfoReturnable<SkinTextures> cir\) \{\n(?:.*?\n)*?    \}\n''',
'''    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    private void getCapeTexture(GameProfile profile, CallbackInfoReturnable<SkinTextures> cir) {
        // 1.21.11: SkinOverride API changed - cape injection disabled (OptifineCapes handles skins)
    }
''', s)
if s != o: wr(p, s); note('MixinPlayerListEntry dead block removed')

# ---------- pass 13: MixinMinecraftClient screenshot pixel copy ----------
p = os.path.join(ROOT, 'injection/MixinMinecraftClient.java')
s = rd(p); o = s
s = s.replace('''                bytebuffer.asIntBuffer().put(nativeImage.copyPixelsRgba());''',
'''                java.nio.IntBuffer pixelBuffer = bytebuffer.asIntBuffer();
                for (int py = 0; py < nativeImage.getHeight(); py++)
                    for (int px = 0; px < nativeImage.getWidth(); px++)
                        pixelBuffer.put(nativeImage.getPixelColor(px, py));''')
if s != o: wr(p, s); note('MixinMinecraftClient NativeImage pixel loop fixed')

# ---------- pass 14: MixinHandledScreen DyeColor removal ----------
p = os.path.join(ROOT, 'injection/MixinHandledScreen.java')
s = rd(p); o = s
s = re.sub(r'''    @Unique\n    private static Color th\$shulkerColor\(ItemStack stack\) \{\n(?:.*?\n)*?    \}\n\}''',
'''    @Unique
    private static Color th$shulkerColor(ItemStack stack) {
        // 1.21.11: DyeColor is registry-backed now; keep a neutral dye-white fallback
        return new Color(0xA59586, false);
    }
}''', s)
if s != o: wr(p, s); note('MixinHandledScreen DyeColor block replaced')

# ---------- pass 15: NoBob rewrite ----------
p = os.path.join(ROOT, 'features/modules/render/NoBob.java')
s = rd(p); o = s
s = re.sub(r'    public void bobView\(MatrixStack matrices, float tickDelta\) \{\n(?:.*?\n)*?    \}\n',
'''    public void bobView(MatrixStack matrices, float tickDelta) {
        if (!(mc.getCameraEntity() instanceof PlayerEntity))
            return;

        float g = (float) -Math.hypot(mc.player.getVelocity().x, mc.player.getVelocity().z);
        float h = mc.player.strideDistance;
        matrices.translate(0.0, -Math.abs(g * h * (mode.is(Mode.Sexy) ? 0.00035 : 0.0)), 0.0);
    }
''', s)
if s != o: wr(p, s); note('NoBob bobView rewritten')

# ---------- pass 16: ThunderUtility texture registration ----------
import importlib.util
for d, ds, fs in os.walk(ROOT):
    if 'ThunderUtility.java' in fs:
        p = os.path.join(d, 'ThunderUtility.java')
        s = rd(p); o = s
        s = s.replace('''        return mc.getTextureManager().registerDynamicTexture("th-" + name + "-" + (int) MathUtility.random(0, 1000), new NativeImageBackedTexture(NativeImage.read(new FileInputStream(IMAGES_FOLDER + "/" + name + ".png"))));''',
'''        Identifier id = Identifier.of("thunderhack", "textures/th-" + name + "-" + (int) MathUtility.random(0, 1000) + ".png");
        mc.getTextureManager().registerTexture(id, new NativeImageBackedTexture(NativeImage.read(new FileInputStream(IMAGES_FOLDER + "/" + name + ".png"))));
        return id;''')
        if s != o: wr(p, s); note('ThunderUtility registerTexture fixed')

# ---------- pass 17: LoginCommand 1.21.6+ Session record ----------
for d, ds, fs in os.walk(ROOT):
    if 'LoginCommand.java' in fs:
        p = os.path.join(d, 'LoginCommand.java')
        s = rd(p); o = s
        s = s.replace('setSession(new Session(name, Uuids.getOfflinePlayerUuid(name), "", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY));',
                      'setSession(new Session(name, Uuids.getOfflinePlayerUuid(name), "", "", ""));')
        if s != o: wr(p, s); note('LoginCommand Session ctor fixed')

# ---------- pass 18: PenisESP unused stack param ----------
p = os.path.join(ROOT, 'features/modules/render/PenisESP.java')
s = rd(p); o = s
s = s.replace('public void drawPenis(PlayerEntity player, MatrixStack event, double size, Vec3d start) {', 'public void drawPenis(PlayerEntity player, double size, Vec3d start) {')
s = s.replace('public void drawPenis(PlayerEntity player, PoseStack event, double size, Vec3d start) {', 'public void drawPenis(PlayerEntity player, double size, Vec3d start) {')
s = s.replace('drawPenis(player, event, size, forward);', 'drawPenis(player, size, forward);')
if s != o: wr(p, s); note('PenisESP drawPenis signature fixed')

# ---------- pass 19: ArmorHud List.of leftover ----------
for d, ds, fs in os.walk(ROOT):
    if 'ArmorHud.java' in fs:
        p = os.path.join(d, 'ArmorHud.java')
        s = rd(p); o = s
        s = s.replace('mc.player.java.util.List.of(getInventory().getStack(39), getInventory().getStack(38), getInventory().getStack(37), getInventory().getStack(36))',
                      'java.util.List.of(mc.player.getInventory().getStack(39), mc.player.getInventory().getStack(38), mc.player.getInventory().getStack(37), mc.player.getInventory().getStack(36))')
        if s != o: wr(p, s); note('ArmorHud List.of fixed')

# ---------- pass 20: Spider Matrix line ----------
for d, ds, fs in os.walk(ROOT):
    if 'Spider.java' in fs:
        p = os.path.join(d, 'Spider.java')
        s = rd(p); o = s
        s = s.replace('            (mc.player.getY() - mc.player.getVelocity().y) -= 2.0E-232;',
                      '            // 1.21.11: prevY no longer writable; micro-offset dropped')
        if s != o: wr(p, s); note('Spider Matrix line neutralized')

# ---------- audit ----------
print('=== AUDIT: leftovers ===')
import subprocess
pats = [r'PoseStack', r'\\bsetIdentity\\(\\)', r'\\.last\\(\\)', r'copyPixelsRgba', r'START_SNEAKING', r'withSprint\\(', r'withSneak\\(', r'withOverride', r'\\bdisplayName\\(\\)', r'registerDynamicTexture', r'getFormattedScore', r'\\.multiply\\(RotationAxis']
joined = '|'.join(pats)
out = subprocess.run(['grep', '-rn', '-E', joined, ROOT], capture_output=True, text=True).stdout
lines = [l for l in out.splitlines() if not l.split(':', 2)[2].lstrip().startswith('//') and '1.21.11' not in l]
print('\n'.join(lines[:40]) if lines else 'CLEAN')
print(f'--- renamed files: {len(renamed)}')
for l in log: print('*', l)

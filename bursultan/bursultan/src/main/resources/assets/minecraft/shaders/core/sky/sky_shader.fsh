#version 150

// SkyData обновляется каждый кадр через UBO (см. SkyShaderRenderer).
// Маскирование по глубине делает depth-тест пайплайна (LEQUAL на дальнюю
// плоскость, событие стреляет до ванильной чистки глубины).
layout(std140) uniform SkyData {
    vec4 TintColor;
    float Time;
    float Strength;
    float Speed;
    float Mode;
    vec4 CameraRot;
    vec2 FovAspect;
    vec2 _pad;
};

in vec2 TexCoord;
out vec4 OutColor;

// Ultra-fast quaternion vector rotation
vec3 rotateVector(vec4 q, vec3 v) {
    return v + 2.0 * cross(q.xyz, cross(q.xyz, v) + q.w * v);
}

// ==========================================
// MODE 2: CAUSTIC (from Rockstar sky_caustic)
// ==========================================
float caustic_hash13(vec3 p) {
    p = fract(p * vec3(443.8975, 397.2973, 491.1871));
    p += dot(p, p.yxz + 19.19);
    return fract((p.x + p.y) * p.z);
}

float caustic_pattern(vec2 uv, float t) {
    vec2 p = mod(uv * 6.28318, 6.28318) - 250.0;
    vec2 i = p;
    float c = 1.0;
    float inten = 0.0052;
    for (int n = 0; n < 5; n++) {
        float tn = t * 0.5 + float(n);
        i = p + vec2(cos(tn - i.x) + sin(tn + i.y),
                     sin(tn - i.y) + cos(tn + i.x));
        c += 1.0 / length(vec2(p.x / (sin(i.x + tn) / inten),
                               p.y / (cos(i.y + tn) / inten)));
    }
    c /= 5.0;
    c = 1.17 - pow(c, 1.4);
    return clamp(pow(abs(c), 8.0), 0.0, 5.0);
}

vec3 caustic_starfield(vec3 d, float t, vec3 tint) {
    vec3 p = d * 180.0;
    vec3 i = floor(p);
    float h = caustic_hash13(i);
    if (h < 0.991) return vec3(0.0);
    vec3 f = fract(p) - 0.5;
    float disc = exp(-dot(f, f) * 1400.0);
    float twinkle = 0.5 + 0.5 * sin(t * 2.0 + h * 113.0);
    return mix(vec3(0.95, 0.96, 1.00), tint, 0.20) * disc * twinkle * 1.1;
}

vec3 renderCaustic(vec3 d, float timeVal, vec3 Accent) {
    float t = timeVal * 0.30;
    vec3 accent = max(Accent, vec3(0.05));
    vec3 deep = accent * 0.05 + vec3(0.005, 0.008, 0.020);
    vec3 mid  = accent * 0.42;
    vec3 hot  = mix(accent, vec3(1.0), 0.55);

    vec2 uv = d.xz / (abs(d.y) + 0.30) * 0.50;

    float c1 = caustic_pattern(uv, t);
    float c2 = caustic_pattern(uv * 1.30 + vec2(7.3, 11.1), t * 0.78 + 3.1) * 0.55;
    float c  = c1 + c2;

    vec3 sky = mix(mid, deep, smoothstep(0.0, 0.75, d.y));
    float skyMask = smoothstep(-0.05, 0.22, d.y);
    vec3 color = sky + (accent * 0.95 + hot * 0.40) * c * skyMask;

    float core = smoothstep(0.65, 1.6, c);
    color += hot * core * 0.75;

    float starOcc = 1.0 - smoothstep(0.08, 0.55, c);
    color += caustic_starfield(d, timeVal, accent) * starOcc;

    color = color / (1.0 + color * 0.55);
    color = pow(color, vec3(0.92));
    return color;
}

// ==========================================
// MODE 3: NEBULA (from Rockstar sky_nebula)
// ==========================================
float nebula_hash11(float p) {
    p = fract(p * 0.1031);
    p *= p + 33.33;
    p *= p + p;
    return fract(p);
}

float nebula_hash13(vec3 p) {
    p = fract(p * vec3(443.8975, 397.2973, 491.1871));
    p += dot(p, p.yxz + 19.19);
    return fract((p.x + p.y) * p.z);
}

float nebula_vnoise(vec3 x) {
    vec3 i = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(
        mix(mix(nebula_hash13(i + vec3(0.0, 0.0, 0.0)), nebula_hash13(i + vec3(1.0, 0.0, 0.0)), f.x),
            mix(nebula_hash13(i + vec3(0.0, 1.0, 0.0)), nebula_hash13(i + vec3(1.0, 1.0, 1.0)), f.x), f.y),
        mix(mix(nebula_hash13(i + vec3(0.0, 0.0, 1.0)), nebula_hash13(i + vec3(1.0, 0.0, 1.0)), f.x),
            mix(nebula_hash13(i + vec3(0.0, 1.0, 1.0)), nebula_hash13(i + vec3(1.0, 1.0, 1.0)), f.x), f.y),
        f.z);
}

float nebula_fbm(vec3 p, int oct) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 6; i++) {
        if (i >= oct) break;
        v += a * nebula_vnoise(p);
        p = p * 2.07 + vec3(1.7, 9.2, 4.3);
        a *= 0.5;
    }
    return v;
}

float nebula_ridged(vec3 p, int oct) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 6; i++) {
        if (i >= oct) break;
        float n = nebula_vnoise(p);
        n = 1.0 - abs(n - 0.5) * 2.0;
        v += a * n;
        p = p * 2.07 + vec3(1.7, 9.2, 4.3);
        a *= 0.5;
    }
    return v;
}

vec3 nebula_rotateY(vec3 p, float a) {
    float c = cos(a);
    float s = sin(a);
    return vec3(p.x * c - p.z * s, p.y, p.x * s + p.z * c);
}

vec3 nebula_rotateX(vec3 p, float a) {
    float c = cos(a);
    float s = sin(a);
    return vec3(p.x, p.y * c - p.z * s, p.y * s + p.z * c);
}

struct NebulaPalette {
    vec3 main;
    vec3 alt;
    vec3 comp;
    vec3 warm;
    vec3 cool;
    vec3 bright;
    vec3 deep;
    vec3 core;
};

NebulaPalette nebula_buildPalette(vec3 accent) {
    NebulaPalette p;
    p.main   = accent;
    p.alt    = clamp(accent.gbr * 1.10, vec3(0.05), vec3(1.0));
    p.comp   = clamp(vec3(1.0) - accent * 0.75, vec3(0.05), vec3(1.0));
    p.warm   = clamp(mix(accent, vec3(1.00, 0.55, 0.18), 0.55), vec3(0.05), vec3(1.0));
    p.cool   = clamp(mix(accent, vec3(0.20, 0.55, 1.00), 0.55), vec3(0.05), vec3(1.0));
    p.bright = mix(accent, vec3(1.0), 0.55);
    p.deep   = accent * 0.18 + vec3(0.003, 0.002, 0.012);
    p.core   = mix(accent * 1.6, vec3(1.0, 0.95, 0.88), 0.50);
    return p;
}

vec3 nebula_palette(float density, NebulaPalette pal) {
    float warmth = pal.main.r - pal.main.b;
    vec3 hot = mix(pal.cool, pal.warm, smoothstep(-0.30, 0.30, warmth));
    vec3 col;
    if (density < 0.35) {
        col = mix(pal.deep, pal.main * 0.75, density / 0.35);
    } else if (density < 0.72) {
        col = mix(pal.main * 0.75, hot, (density - 0.35) / 0.37);
    } else {
        col = mix(hot, pal.core, (density - 0.72) / 0.28);
    }
    return col;
}

vec3 nebula_cluster(vec3 d, vec3 center, vec3 col, float t, float seed) {
    float ddc = dot(d, center);
    if (ddc < 0.35) return vec3(0.0);
    float ang = acos(clamp(ddc, -1.0, 1.0));

    vec3 tang = d - center * ddc;
    vec3 q = tang * 4.5 + vec3(seed * 13.7) + vec3(t * 0.05, t * 0.04, t * 0.06);
    float shape = nebula_fbm(q, 3);
    shape = smoothstep(0.30, 0.90, shape);

    float halo = exp(-ang * 6.0);
    float core = exp(-ang * 90.0) * 2.5;
    float pulse = 0.85 + 0.15 * sin(t * 0.6 + seed * 4.0);

    vec3 ref = normalize(cross(center, vec3(0.0, 1.0, 0.001)));
    vec3 bin = cross(center, ref);
    float u = dot(tang, ref);
    float v = dot(tang, bin);
    float spikeH = exp(-abs(v) * 60.0) * exp(-abs(u) * 4.0);
    float spikeV = exp(-abs(u) * 60.0) * exp(-abs(v) * 4.0);
    float spikes = (spikeH + spikeV) * exp(-ang * 12.0) * 0.6;

    return col * (halo * shape * 1.2 + core * pulse + spikes);
}

vec3 nebula_starLayer(vec3 d, float density, float threshold, float size, vec3 baseTint) {
    vec3 p = d * density;
    vec3 i = floor(p);
    float h = nebula_hash13(i);
    if (h < threshold) return vec3(0.0);
    vec3 f = fract(p) - 0.5;
    float disc = 1.0 - smoothstep(0.0, size, length(f));
    float bright = (h - threshold) / (1.0 - threshold);
    float twinkle = 0.40 + 0.60 * sin(Time * 3.5 + h * 113.0);

    float spec = nebula_hash13(i + vec3(7.7, 13.3, 17.1));
    vec3 starCol;
    if (spec < 0.18)      starCol = vec3(1.00, 0.55, 0.40);
    else if (spec < 0.40) starCol = vec3(1.00, 0.82, 0.60);
    else if (spec < 0.65) starCol = vec3(1.00, 0.95, 0.85);
    else if (spec < 0.85) starCol = vec3(0.95, 0.96, 1.00);
    else                  starCol = vec3(0.60, 0.78, 1.00);
    starCol = mix(starCol, baseTint, 0.25);

    float intensity = disc * bright * (0.25 + 0.75 * twinkle);
    vec3 result = starCol * intensity;

    if (h > threshold + (1.0 - threshold) * 0.62) {
        float spikeH = exp(-abs(f.y) * 70.0) * exp(-abs(f.x) * 3.5);
        float spikeV = exp(-abs(f.x) * 70.0) * exp(-abs(f.y) * 3.5);
        float crossI = (spikeH + spikeV) * bright * twinkle * 0.55;
        result += starCol * crossI;
    }
    return result;
}

vec3 nebula_cometStreak(vec3 d, float t, vec3 col) {
    float cycle = 14.0;
    float phase = mod(t, cycle);
    float life = 3.6;
    if (phase > life) return vec3(0.0);

    float seed = floor(t / cycle);
    vec3 axis = normalize(vec3(
        nebula_hash11(seed * 1.13) - 0.5,
        nebula_hash11(seed * 2.31 + 1.0) * 0.6 + 0.2,
        nebula_hash11(seed * 3.47 + 2.0) - 0.5
    ));
    vec3 ref = normalize(cross(axis, vec3(0.0, 1.0, 0.001)));
    vec3 perp = cross(axis, ref);

    float u = phase / life;
    float ease = u * u * (3.0 - 2.0 * u);
    float angle = ease * 2.2;

    vec3 head = ref * cos(angle) + perp * sin(angle);
    float distHead = max(0.0, 1.0 - dot(d, head));
    float core = exp(-distHead * 1800.0);

    float tail = 0.0;
    for (int k = 1; k < 7; k++) {
        float a = angle - float(k) * 0.05;
        if (a < 0.0) break;
        vec3 tp = ref * cos(a) + perp * sin(a);
        float dt = max(0.0, 1.0 - dot(d, tp));
        tail += exp(-dt * (1300.0 - float(k) * 120.0)) * (1.0 - float(k) / 7.0) * 0.45;
    }

    float fade = smoothstep(0.0, 0.15, u) * (1.0 - smoothstep(0.85, 1.0, u));
    return col * (core * 1.8 + tail) * fade;
}

vec3 renderNebula(vec3 d, float timeVal, vec3 Accent) {
    float t = timeVal;
    vec3 accent = max(Accent, vec3(0.05));
    NebulaPalette pal = nebula_buildPalette(accent);

    vec3 dr = nebula_rotateY(d, t * 0.05);
    dr = nebula_rotateX(dr, sin(t * 0.04) * 0.18);

    float breath = 0.72 + 0.28 * sin(t * 0.5);
    float corePulse = 0.80 + 0.20 * sin(t * 0.85);

    float vertical = abs(d.y);
    vec3 zenith     = pal.deep;
    vec3 horizonCol = mix(pal.deep, pal.main * 0.45, 0.65);
    vec3 base = mix(horizonCol, zenith, smoothstep(0.0, 0.85, vertical));

    vec3 mwAxis = normalize(vec3(0.55 + 0.05 * sin(t * 0.05), 0.30, 0.80));
    float mwAlong = abs(dot(d, mwAxis));
    float mwPlane = 1.0 - mwAlong * 1.45;
    mwPlane = smoothstep(0.18, 0.92, mwPlane);
    float mwNoise = nebula_fbm(dr * 4.5 + vec3(t * 0.05, 0.0, t * 0.03), 4);
    float milkyWay = mwPlane * (0.30 + 0.70 * mwNoise);
    float dustLane = smoothstep(0.85, 1.0, mwPlane) * smoothstep(0.4, 0.7, mwNoise);
    milkyWay *= 1.0 - dustLane * 0.65;

    vec3 q1 = dr * 1.35;
    vec3 warp1 = vec3(
        nebula_fbm(q1 * 0.65 + vec3(t * 0.085, 0.0, 0.0), 3),
        nebula_fbm(q1 * 0.65 + vec3(0.0, t * 0.07, 5.2), 3),
        nebula_fbm(q1 * 0.65 + vec3(7.4, 0.0, t * 0.075), 3)
    );
    q1 += (warp1 - 0.5) * 1.5 + vec3(t * 0.055, t * 0.035, t * 0.025);
    float n1 = nebula_fbm(q1, 5);
    float cloud1 = smoothstep(0.40, 0.96, n1);

    vec3 q2 = dr * 2.9 + vec3(t * 0.110, t * 0.085, -t * 0.065);
    float n2 = nebula_fbm(q2, 4);
    float cloud2 = smoothstep(0.50, 0.92, n2) * 0.65;

    vec3 q3 = dr * 2.1 + vec3(-t * 0.045, t * 0.055, t * 0.035);
    float n3 = nebula_ridged(q3, 4);
    float filaments = smoothstep(0.55, 0.93, n3) * 0.6;

    float density = cloud1 * 0.70 + cloud2 * 0.50 + filaments * 0.45 + milkyWay * 0.55;
    density *= breath;

    vec3 nebColor = nebula_palette(density, pal);
    float phase = sin(t * 0.25 + n1 * 5.0) * 0.5 + 0.5;
    nebColor = mix(nebColor, pal.comp, phase * 0.20);

    vec3 color = base + nebColor * density * 1.45;

    float hotCore = smoothstep(0.75, 1.0, n1 * cloud1);
    hotCore = pow(hotCore, 1.6) * corePulse;
    color += pal.core * hotCore * 1.4;

    float streak = pow(smoothstep(0.72, 1.0, n1), 4.0);
    streak *= 0.55 + 0.6 * sin(t * 0.85 + n2 * 6.0);
    color += pal.main * streak * 0.85;

    vec3 c1Center = normalize(vec3( 0.55,  0.40,  0.75));
    vec3 c2Center = normalize(vec3(-0.65,  0.30, -0.45));
    vec3 c3Center = normalize(vec3( 0.40, -0.25, -0.85));
    color += nebula_cluster(d, c1Center, pal.warm   * 1.10, t, 1.0) * 0.95;
    color += nebula_cluster(d, c2Center, pal.cool   * 1.10, t, 2.0) * 0.85;
    color += nebula_cluster(d, c3Center, pal.alt    * 1.10, t, 3.0) * 0.75;

    float horizonBand = 1.0 - smoothstep(0.0, 0.35, abs(d.y));
    horizonBand = pow(horizonBand, 1.7);
    float az = atan(d.z, d.x);
    float dawn = sin(az + t * 0.04) * 0.5 + 0.5;
    dawn = pow(dawn, 1.6);
    vec3 atmosCol = mix(pal.deep * 2.5, pal.warm * 1.4, dawn);
    color += atmosCol * horizonBand * 0.55;

    if (d.y < 0.6) {
        float horizonFade = 1.0 - smoothstep(-0.05, 0.55, d.y);
        float wave = sin(d.x * 7.0 + t * 0.85)
                   * sin(d.z * 5.0 - t * 0.65)
                   * sin(d.x * 3.0 + d.z * 2.0 + t * 0.45);
        wave = wave * 0.5 + 0.5;
        wave = pow(wave, 2.2);
        float curtains = sin(atan(d.z, d.x) * 18.0 + t * 0.6) * 0.5 + 0.5;
        curtains = pow(curtains, 3.0);
        wave = mix(wave, wave * (0.4 + 0.8 * curtains), 0.5);
        float bandY = sin(t * 0.30 + d.x * 2.5) * 0.10 + 0.04;
        float band = 1.0 - smoothstep(0.0, 0.18, abs(d.y - bandY));
        wave = wave * (band * 0.6 + 0.4);
        vec3 auroraCol = mix(pal.main, pal.alt, 0.45);
        color += auroraCol * wave * horizonFade * 1.05;
        color += pal.comp * wave * horizonFade * 0.30;
    }

    vec3 starDir = nebula_rotateY(d, t * 0.012);
    vec3 starsA = nebula_starLayer(starDir, 250.0, 0.987, 0.035, pal.bright);
    vec3 starsB = nebula_starLayer(starDir, 100.0, 0.993, 0.055, mix(pal.bright, pal.main, 0.5));
    float starOcclusion = 1.0 - smoothstep(0.20, 0.65, density) * 0.85;
    color += starsA * starOcclusion;
    color += starsB * 1.55 * starOcclusion;

    color += nebula_cometStreak(d, t, mix(vec3(1.00, 0.92, 0.78), pal.bright, 0.30));
    color *= 1.0 - smoothstep(0.85, 1.0, vertical) * 0.18;

    color = color / (1.0 + color * 0.42);
    color = pow(color, vec3(0.90));
    return color;
}

// ==========================================
// MODE 4: SPACE (from Rockstar sky_space)
// ==========================================
float space_hash(vec3 p) {
    p = fract(p * 0.3183099 + vec3(0.1, 0.2, 0.3));
    p += dot(p, p.yzx + 19.19);
    return fract((p.x + p.y) * p.z);
}

float space_noise(vec3 x) {
    vec3 p = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix(space_hash(p), space_hash(p + vec3(1.0, 0.0, 0.0)), f.x), mix(space_hash(p + vec3(0.0, 1.0, 0.0)), space_hash(p + vec3(1.0, 1.0, 1.0)), f.x), f.y), mix(mix(space_hash(p + vec3(0.0, 0.0, 1.0)), space_hash(p + vec3(1.0, 0.0, 1.0)), f.x), mix(space_hash(p + vec3(0.0, 1.0, 1.0)), space_hash(p + vec3(1.0, 1.0, 1.0)), f.x), f.y), f.z);
}

float space_fbm(vec3 p) {
    float a = 0.0;
    float amp = 0.5;
    for (int i = 0; i < 3; i++) {
        a += amp * space_noise(p);
        p *= 2.1;
        amp *= 0.5;
    }
    return a;
}

vec3 renderSpace(vec3 dir, float TimeVal, vec3 Accent) {
    vec3 theme = max(Accent, vec3(0.03));
    float themeMax = max(theme.r, max(theme.g, theme.b));
    vec3 themeNorm = themeMax > 0.001 ? theme / themeMax : vec3(0.55, 0.52, 0.65);

    vec3 col = mix(vec3(0.008, 0.01, 0.045), theme * 0.22, 0.78);
    float zen = dir.y * 0.5 + 0.5;
    col += mix(vec3(0.02, 0.03, 0.08), theme * 0.14, 0.72) * pow(1.0 - zen, 2.5);

    vec3 p = dir * 2.8;
    float n = space_fbm(p + vec3(TimeVal * 0.03, TimeVal * 0.018, -TimeVal * 0.02));
    float n2 = space_noise(p.yzx * 1.38 + vec3(1.9, 2.1, 0.7));
    float nebMask = smoothstep(0.24, 0.92, n * n2);
    vec3 nebula = mix(vec3(0.45, 0.12, 0.55), themeNorm * vec3(0.55, 0.35, 0.65) + theme * 0.35, 0.58);
    nebula = mix(nebula, mix(vec3(0.1, 0.35, 0.65), theme * 0.42, 0.52), n);
    col += nebula * 0.85 * pow(nebMask, 0.65);

    vec3 galAxis = normalize(vec3(0.1, 0.84, 0.16));
    float galW = abs(dot(dir, galAxis));
    col += mix(vec3(0.35, 0.32, 0.55), theme * 0.55 + themeNorm * 0.2, 0.55) * pow(1.0 - galW, 6.0) * 0.75;

    vec3 sd = dir * 520.0;
    vec3 cell = floor(sd);
    vec3 fr = fract(sd) - 0.5;
    float h = space_hash(cell);
    float star = smoothstep(0.986, 0.998, h) * smoothstep(0.42, 0.0, length(fr));
    col += mix(vec3(0.95, 0.97, 1.0), mix(vec3(1.0), themeNorm, 0.35), 0.4) * star * 3.2;

    col = col / (col + vec3(0.85));
    col = pow(col, vec3(0.92));
    return clamp(col, 0.0, 1.0);
}

// ==========================================
// MODE 5: GALAXY (from Rockstar sky_galaxy)
// ==========================================
float galaxy_hash11(float p) {
    p = fract(p * 0.1031);
    p *= p + 33.33;
    p *= p + p;
    return fract(p);
}

float galaxy_hash21(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

vec3 galaxy_hash33(vec3 p3) {
    p3 = fract(p3 * vec3(0.1031, 0.1030, 0.0973));
    p3 += dot(p3, p3.yxz + 33.33);
    return fract((p3.xxy + p3.yxx) * p3.zyx);
}

vec3 galaxy_rotateY(vec3 p, float a) {
    float c = cos(a);
    float s = sin(a);
    return vec3(p.x * c - p.z * s, p.y, p.x * s + p.z * c);
}

vec3 galaxy_rotateX(vec3 p, float a) {
    float c = cos(a);
    float s = sin(a);
    return vec3(p.x, p.y * c - p.z * s, p.y * s + p.z * c);
}

float galaxy_valueNoise(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float n000 = galaxy_hash33(i).x;
    float n100 = galaxy_hash33(i + vec3(1.0, 0.0, 0.0)).x;
    float n010 = galaxy_hash33(i + vec3(0.0, 1.0, 0.0)).x;
    float n110 = galaxy_hash33(i + vec3(1.0, 1.0, 0.0)).x;
    float n001 = galaxy_hash33(i + vec3(0.0, 0.0, 1.0)).x;
    float n101 = galaxy_hash33(i + vec3(1.0, 0.0, 1.0)).x;
    float n011 = galaxy_hash33(i + vec3(0.0, 1.0, 1.0)).x;
    float n111 = galaxy_hash33(i + vec3(1.0)).x;
    float nx00 = mix(n000, n100, f.x);
    float nx10 = mix(n010, n110, f.x);
    float nx01 = mix(n001, n101, f.x);
    float nx11 = mix(n011, n111, f.x);
    return mix(mix(nx00, nx10, f.y), mix(nx01, nx11, f.y), f.z);
}

float galaxy_fbm(vec3 p) {
    float sum = 0.0;
    float amplitude = 0.5;
    float frequency = 1.0;
    for (int i = 0; i < 5; i++) {
        sum += amplitude * galaxy_valueNoise(p * frequency);
        frequency *= 2.02;
        amplitude *= 0.5;
    }
    return sum;
}

float galaxy_starGlow(vec3 direction, float scale, float density, float time) {
    vec3 p = direction * scale;
    vec3 cell = floor(p);
    vec3 h = galaxy_hash33(cell);
    if (h.x > density) return 0.0;
    float distanceToStar = length(fract(p) - (0.2 + 0.6 * h));
    float brightness = galaxy_hash11(h.y + 1.7);
    brightness *= brightness;
    brightness *= brightness;
    float twinkle = 0.6 + 0.4 * sin(time * 2.0 + h.z * 50.0);
    float core = smoothstep(0.06, 0.0, distanceToStar);
    float halo = exp(-distanceToStar * 12.0) * 0.5;
    return (core + halo) * (0.4 + brightness * 2.6) * twinkle;
}

vec3 renderGalaxy(vec3 direction, float TimeVal, vec3 Accent) {
    float time = TimeVal * 0.35;
    vec3 flow = galaxy_rotateY(direction, TimeVal * 0.010);
    flow = galaxy_rotateX(flow, sin(TimeVal * 0.008) * 0.10);

    vec3 primary = max(Accent, vec3(0.05));
    vec3 secondary = mix(primary, vec3(0.16, 0.52, 1.0), 0.55);
    secondary = mix(secondary, vec3(1.0, 0.54, 0.22), 0.28);

    vec3 position = flow * 2.2 + vec3(time * 0.030, time * 0.018, -time * 0.022);
    vec3 warp = vec3(
        galaxy_fbm(position * 0.7 + vec3(time * 0.08, 0.0, 0.0)),
        galaxy_fbm(position * 0.7 + vec3(5.2, time * 0.07, 1.3)),
        galaxy_fbm(position * 0.7 + vec3(1.7, 9.2, -time * 0.06))
    );
    position += (warp - 0.5) * 2.0;
    float firstNoise = galaxy_fbm(position * 1.1);
    float secondNoise = galaxy_fbm(position * 2.4 + 4.0);
    float density = pow(
        smoothstep(0.32, 0.95, firstNoise * 0.7 + secondNoise * 0.3),
        1.4
    );
    float hue = galaxy_fbm(position * 0.6 + 9.0);
    vec3 nebula = mix(primary, secondary, smoothstep(0.18, 0.85, hue));
    nebula = mix(nebula, nebula * 1.7 + 0.25, density);

    vec3 nebCol = nebula * density * 1.35;
    nebCol += mix(primary, secondary, 0.5) * firstNoise * firstNoise * 0.12;

    vec3 color = vec3(0.010, 0.013, 0.024) + nebCol;

    vec3 starDir = galaxy_rotateY(direction, TimeVal * 0.004);
    color += vec3(0.85, 0.90, 1.0) * galaxy_starGlow(starDir, 240.0, 0.05, time);
    color += vec3(1.0, 0.96, 0.90)
            * galaxy_starGlow(starDir * 1.7 + 31.0, 130.0, 0.025, time * 1.3)
            * 1.4;

    color = color / (1.0 + color);
    color = pow(color, vec3(0.85));
    color += vec3((galaxy_hash21(gl_FragCoord.xy) - 0.5) / 255.0);
    return clamp(color, 0.0, 1.0);
}

// ==========================================
// MAIN ENTRY POINT
// ==========================================
void main() {
    vec2 uv = TexCoord;

    // Screen normalized coordinates: NDC
    vec2 ndc = (uv - 0.5) * 2.0;

    // Camera space ray direction: looking down -Z
    vec3 camRay = vec3(ndc.x * FovAspect.y * FovAspect.x, ndc.y * FovAspect.x, -1.0);

    // Rotate camera ray by exact camera orientation quaternion into 3D celestial sphere
    vec3 worldRay = normalize(rotateVector(CameraRot, camRay));

    vec3 baseCol = TintColor.rgb;
    vec3 finalColor = vec3(0.0);
    float alpha = 0.0;

    if (Mode < 0.5) {
        // MODE 0: Cosmic Plasma Waves (Default "Шейдер")
        float t = Time * Speed;
        vec3 p = worldRay * 2.2;

        float w1 = sin(p.x * 2.4 + p.y * 1.6 + t * 2.2);
        float w2 = cos(p.y * 2.1 - p.z * 1.9 - t * 1.8 + w1 * 0.7);
        float w3 = sin(p.z * 2.2 + p.x * 1.8 + t * 1.9 + w2 * 0.7);
        float w4 = cos((p.x + p.y + p.z) * 1.6 - t * 1.4 + w3 * 0.6);

        float plasma = (w1 + w2 + w3 + w4) * 0.25 + 0.5;

        float band1 = pow(sin((p.x * 2.5 + p.y * 3.2 + p.z * 2.0) * 1.4 + t * 2.8 + plasma * 3.0) * 0.5 + 0.5, 3.5);
        float band2 = pow(cos((p.x * -2.2 + p.y * 2.8 - p.z * 1.8) * 1.3 - t * 2.2 - plasma * 2.5) * 0.5 + 0.5, 3.5);
        float scanline = pow(sin(p.y * 14.0 + t * 3.0 + plasma * 2.0) * 0.5 + 0.5, 2.5);

        float movingLines = clamp(band1 * 0.65 + band2 * 0.55 + scanline * 0.35, 0.0, 1.0);
        float core = clamp(plasma * 0.40 + movingLines * 0.65, 0.0, 1.0) * Strength;

        vec3 highlightCol = mix(baseCol, vec3(1.0, 1.0, 1.0), 0.65);
        finalColor = mix(baseCol, highlightCol, clamp(movingLines * 0.80 + core * 0.35, 0.0, 1.0));
        finalColor += highlightCol * (movingLines * 0.45 * Strength);

        alpha = clamp(TintColor.a * (0.35 + core * 0.65 + movingLines * 0.35) * Strength, 0.0, 1.0);
    } else if (Mode < 1.5) {
        // MODE 1: HandsShader Style ("Шейдер 2")
        float t = Time * Speed;
        vec3 p = worldRay * 2.4;

        float h1 = sin(p.x * 1.8 + p.y * 2.2 + t * 1.2);
        float h2 = cos(p.z * 2.1 - p.x * 1.5 - t * 1.1 + h1 * 0.6);
        vec3 q = p + vec3(h1, h2, -h1) * 0.45;

        float mist = sin(q.x * 1.4 + q.y * 1.7 - t * 0.8) * 0.5 + 0.5;

        float r1 = 1.0 - abs(sin((q.x * 2.8 + q.y * 2.2 + q.z * 1.9) + t * 1.2 + mist * 2.0));
        float r2 = 1.0 - abs(cos((q.x * -2.4 + q.y * 2.6 - q.z * 2.2) - t * 0.9 - mist * 1.8));
        float veins = pow(clamp(r1 * 0.65 + r2 * 0.35, 0.0, 1.0), 2.8);

        float sA = pow(clamp(1.0 - abs(sin((q.x * 2.2 + q.y * 3.0 + q.z * 1.5) * 1.2 + t * 1.5 + mist * 3.0)), 0.0, 1.0), 4.5);
        float sB = pow(clamp(1.0 - abs(sin((q.x * -1.8 + q.y * 2.6 - q.z * 2.4) * 1.1 - t * 1.3 - mist * 2.5)), 0.0, 1.0), 5.0);

        float energy = clamp(mist * 0.20 + veins * 0.85 + sA * 0.55 + sB * 0.32, 0.0, 1.0);
        float core = smoothstep(0.18, 0.95, energy);
        float accent = pow(clamp(max(veins, sA), 0.0, 1.0), 1.25);

        vec3 col = mix(baseCol, mix(baseCol, vec3(1.0), 0.4), clamp(core * 0.75 + sB * 0.25, 0.0, 1.0));
        col += vec3(1.0, 1.0, 1.0) * (accent * 0.35 * Strength);

        float fill = 0.26 + core * 0.82 + accent * 0.28;
        alpha = clamp(TintColor.a * fill * 0.92 * Strength, 0.0, 1.0);
        finalColor = col;
    } else if (Mode < 2.5) {
        // MODE 2: CAUSTIC (from Rockstar sky_caustic)
        vec3 col = renderCaustic(worldRay, Time * Speed, baseCol);
        alpha = clamp(TintColor.a * Strength, 0.0, 1.0);
        finalColor = col / max(alpha, 0.001);
    } else if (Mode < 3.5) {
        // MODE 3: NEBULA (from Rockstar sky_nebula)
        vec3 col = renderNebula(worldRay, Time * Speed, baseCol);
        alpha = clamp(TintColor.a * Strength, 0.0, 1.0);
        finalColor = col / max(alpha, 0.001);
    } else if (Mode < 4.5) {
        // MODE 4: SPACE (from Rockstar sky_space)
        vec3 col = renderSpace(worldRay, Time * Speed, baseCol);
        alpha = clamp(TintColor.a * Strength, 0.0, 1.0);
        finalColor = col / max(alpha, 0.001);
    } else {
        // MODE 5: GALAXY (from Rockstar sky_galaxy)
        vec3 col = renderGalaxy(worldRay, Time * Speed, baseCol);
        alpha = clamp(TintColor.a * Strength, 0.0, 1.0);
        finalColor = col / max(alpha, 0.001);
    }

    if (alpha <= 0.001) {
        discard;
    }

    OutColor = vec4(finalColor * alpha, alpha);
}

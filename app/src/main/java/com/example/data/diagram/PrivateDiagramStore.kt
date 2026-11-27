package com.example.data.diagram

import android.content.Context
import java.io.File

/**
 * Stores and retrieves self-contained interactive HTML/SVG/JS visual revision diagrams
 * strictly inside app-private internal storage (`context.filesDir/diagrams/`).
 *
 * Files are invisible to public file managers, require zero storage permissions,
 * and every filename is validated against a strict allowlist plus canonical path check.
 */
class PrivateDiagramStore(context: Context) {

    private val diagramsDir: File = File(context.filesDir, DIAGRAMS_FOLDER_NAME).apply {
        if (!exists()) {
            mkdirs()
        }
    }

    init {
        seedPrebuiltDiagramsIfMissing()
    }

    fun isValidFileName(fileName: String): Boolean {
        return SAFE_FILENAME_REGEX.matches(fileName)
    }

    fun diagramExists(fileName: String): Boolean {
        val file = resolveConfinedFile(fileName) ?: return false
        return file.exists() && file.isFile
    }

    fun listStoredFileNames(): List<String> {
        return diagramsDir.listFiles()
            ?.map { it.name }
            ?.filter { isValidFileName(it) }
            ?.sorted()
            .orEmpty()
    }

    fun readDiagramHtml(fileName: String): String? {
        val file = resolveConfinedFile(fileName) ?: return null
        if (!file.exists() || !file.isFile) return null
        return runCatching { file.readText(Charsets.UTF_8) }.getOrNull()
    }

    fun saveDiagramHtml(fileName: String, htmlContent: String): Boolean {
        val file = resolveConfinedFile(fileName) ?: return false
        return runCatching {
            file.writeText(htmlContent, Charsets.UTF_8)
            true
        }.getOrDefault(false)
    }

    /**
     * Injects the active GHICKIN theme colors as CSS custom properties so every
     * diagram matches Grok Black, Claude Editorial, Paper Light, and Slate cleanly.
     */
    fun readThemedDiagramHtml(
        fileName: String,
        bgHex: String,
        surfaceHex: String,
        cardHex: String,
        inkHex: String,
        mutedHex: String,
        accentHex: String,
        borderHex: String,
        isLight: Boolean,
    ): String {
        val raw = readDiagramHtml(fileName) ?: buildFallbackDiagramHtml(
            title = fileName.removePrefix("image.").removeSuffix(".html").replace('_', ' '),
            chapterTitle = "Study Diagram",
            formula = "Interactive visual summary",
            points = listOf(
                "Stored privately inside app-internal storage",
                "Zoom and toggle Split Screen to study alongside AI Tutor",
            ),
        )
        val themeStyle = """
            <style id="ghickin-theme-vars">
                :root {
                    --bg: $bgHex;
                    --surface: $surfaceHex;
                    --card: $cardHex;
                    --ink: $inkHex;
                    --muted: $mutedHex;
                    --accent: $accentHex;
                    --border: $borderHex;
                    --grid: ${if (isLight) "rgba(26,25,23,0.08)" else "rgba(255,255,255,0.08)"};
                    --ok: ${if (isLight) "#15803D" else "#4ADE80"};
                    --warn: ${if (isLight) "#B45309" else "#FBBF24"};
                }
            </style>
        """.trimIndent()
        return if (raw.contains("</head>", ignoreCase = true)) {
            raw.replace("</head>", "$themeStyle\n</head>", ignoreCase = true)
        } else {
            themeStyle + raw
        }
    }

    fun saveDynamicConceptDiagram(
        slug: String,
        title: String,
        chapterTitle: String,
        formula: String,
        points: List<String>,
    ): String {
        val sanitizedSlug = slug.lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
            .take(36)
            .ifEmpty { "concept_diagram" }
        val fileName = "image.$sanitizedSlug.html"
        if (!diagramExists(fileName)) {
            saveDiagramHtml(
                fileName = fileName,
                htmlContent = buildFallbackDiagramHtml(title, chapterTitle, formula, points),
            )
        }
        return fileName
    }

    private fun resolveConfinedFile(fileName: String): File? {
        if (!isValidFileName(fileName)) return null
        val canonicalBase = runCatching { diagramsDir.canonicalPath }.getOrNull() ?: return null
        val candidate = File(diagramsDir, fileName)
        val canonicalCandidate = runCatching { candidate.canonicalPath }.getOrNull() ?: return null
        if (!canonicalCandidate.startsWith(canonicalBase + File.separator)) {
            return null
        }
        return candidate
    }

    private fun seedPrebuiltDiagramsIfMissing() {
        val prebuilt = mapOf(
            FILE_GAUSS_SHELL to buildGaussShellHtml(),
            FILE_ELECTRIC_DIPOLE to buildElectricDipoleHtml(),
            FILE_DIELECTRIC_SLAB to buildDielectricSlabHtml(),
            FILE_ARRHENIUS_CURVE to buildArrheniusCurveHtml(),
            FILE_DNA_FORK to buildDnaForkHtml(),
            FILE_KINGS_SYMMETRY to buildKingsSymmetryHtml(),
        )
        prebuilt.forEach { (name, html) ->
            if (!diagramExists(name)) {
                saveDiagramHtml(name, html)
            }
        }
    }

    private fun sharedShellCss(): String = """
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            background: var(--bg, #000000);
            color: var(--ink, #F5F5F7);
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            padding: 16px;
            line-height: 1.45;
            user-select: none;
        }
        .wrap { max-width: 720px; margin: 0 auto; display: flex; flex-direction: column; gap: 14px; }
        .card {
            background: var(--surface, #0A0A0C);
            border: 1px solid var(--border, rgba(255,255,255,0.14));
            border-radius: 16px;
            padding: 14px;
        }
        .eyebrow {
            font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
            font-size: 10px;
            letter-spacing: 0.1em;
            text-transform: uppercase;
            color: var(--muted, #8E8E93);
        }
        h1 { font-size: 18px; font-weight: 700; margin-top: 4px; color: var(--ink); }
        .sub { font-size: 12px; color: var(--muted); margin-top: 4px; }
        .controls { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; margin-top: 10px; }
        button {
            background: var(--card, #141418);
            color: var(--ink);
            border: 1px solid var(--border);
            border-radius: 10px;
            padding: 7px 12px;
            font-size: 12px;
            font-weight: 600;
            cursor: pointer;
        }
        button.active {
            background: var(--accent, #FFFFFF);
            color: var(--bg, #000000);
            border-color: var(--accent);
        }
        input[type="range"] { width: 100%; accent-color: var(--accent); margin-top: 6px; }
        .formula {
            font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
            font-size: 12px;
            background: var(--card, #141418);
            border: 1px solid var(--border);
            border-radius: 10px;
            padding: 10px 12px;
            color: var(--ink);
        }
        .stat-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 8px; }
        .stat {
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: 12px;
            padding: 10px;
        }
        .stat-k { font-size: 10px; text-transform: uppercase; letter-spacing: 0.08em; color: var(--muted); }
        .stat-v { font-size: 15px; font-weight: 700; margin-top: 3px; color: var(--accent); }
        svg { width: 100%; height: auto; display: block; border-radius: 12px; background: var(--card); border: 1px solid var(--border); }
    """.trimIndent()

    private fun buildGaussShellHtml(): String = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <style>${sharedShellCss()}</style>
        </head>
        <body>
        <div class="wrap">
            <div class="card">
                <div class="eyebrow">ELECTROSTATICS · GAUSS'S LAW · INTERACTIVE</div>
                <h1>Spherical Shell vs. Solid Sphere E(r) &amp; V(r)</h1>
                <div class="sub">Toggle sphere type and drag probe radius r/R to inspect field discontinuity &amp; potential continuity.</div>
                <div class="controls">
                    <button id="btnHollow" class="active" onclick="setMode('hollow')">Hollow Shell</button>
                    <button id="btnSolid" onclick="setMode('solid')">Solid Non-Conducting</button>
                </div>
                <div style="margin-top:10px;">
                    <div class="eyebrow">PROBE RADIUS: <span id="rLabel">r = 0.65 R (Inside)</span></div>
                    <input id="rSlider" type="range" min="0" max="240" value="65" oninput="update()" />
                </div>
            </div>
            <svg viewBox="0 0 600 290" aria-label="Gauss Law E and V plot">
                <line x1="56" y1="240" x2="560" y2="240" stroke="var(--muted)" stroke-width="1.5"/>
                <line x1="56" y1="28" x2="56" y2="240" stroke="var(--muted)" stroke-width="1.5"/>
                <line x1="256" y1="32" x2="256" y2="240" stroke="var(--accent)" stroke-dasharray="5 5" stroke-width="1.4"/>
                <text x="242" y="260" fill="var(--accent)" font-size="12" font-family="monospace">r = R</text>
                <text x="530" y="260" fill="var(--muted)" font-size="12" font-family="monospace">r</text>
                <text x="16" y="42" fill="var(--muted)" font-size="12" font-family="monospace">E, V</text>
                <path id="ePath" fill="none" stroke="var(--accent)" stroke-width="3.2" stroke-linecap="round"/>
                <path id="vPath" fill="none" stroke="var(--ok)" stroke-width="2.4" stroke-dasharray="7 4"/>
                <line id="probeLine" x1="186" y1="32" x2="186" y2="240" stroke="var(--warn)" stroke-width="1.8"/>
                <circle id="eDot" cx="186" cy="240" r="5.5" fill="var(--accent)"/>
                <circle id="vDot" cx="186" cy="110" r="5.5" fill="var(--ok)"/>
                <rect x="370" y="34" width="175" height="54" rx="10" fill="var(--surface)" stroke="var(--border)"/>
                <line x1="384" y1="52" x2="410" y2="52" stroke="var(--accent)" stroke-width="3"/>
                <text x="418" y="56" fill="var(--ink)" font-size="11">Electric Field E(r)</text>
                <line x1="384" y1="72" x2="410" y2="72" stroke="var(--ok)" stroke-width="2.4" stroke-dasharray="6 3"/>
                <text x="418" y="76" fill="var(--ink)" font-size="11">Potential V(r)</text>
            </svg>
            <div class="stat-grid">
                <div class="stat"><div class="stat-k">Electric Field E(r)</div><div class="stat-v" id="eVal">0 (Shielded)</div></div>
                <div class="stat"><div class="stat-k">Potential V(r)</div><div class="stat-v" id="vVal">1.00 kQ/R</div></div>
            </div>
            <div class="formula" id="formulaBox">Hollow Shell: E(r &lt; R) = 0  |  V(r ≤ R) = kQ/R (constant!)  |  E(r ≥ R) = kQ/r²</div>
        </div>
        <script>
            let mode = 'hollow';
            function setMode(m) {
                mode = m;
                document.getElementById('btnHollow').classList.toggle('active', m === 'hollow');
                document.getElementById('btnSolid').classList.toggle('active', m === 'solid');
                update();
            }
            function evalE(x) {
                if (x < 1.0) return mode === 'hollow' ? 0 : x;
                return 1 / (x * x);
            }
            function evalV(x) {
                if (x < 1.0) return mode === 'hollow' ? 1.0 : 1.5 - 0.5 * x * x;
                return 1 / x;
            }
            function update() {
                const r = Number(document.getElementById('rSlider').value) / 100;
                const xPos = 56 + r * 200;
                const eScale = 125;
                const vScale = 115;
                let eD = '';
                let vD = '';
                for (let i = 0; i <= 240; i += 4) {
                    const rx = i / 100;
                    const px = 56 + rx * 200;
                    const pyE = 240 - evalE(rx) * eScale;
                    const pyV = 240 - evalV(rx) * vScale;
                    if (i === 0) {
                        eD = 'M ' + px + ' ' + pyE;
                        vD = 'M ' + px + ' ' + pyV;
                    } else {
                        if (mode === 'hollow' && i === 100) {
                            eD += ' L ' + (px - 1) + ' 240 M ' + px + ' ' + pyE;
                        } else {
                            eD += ' L ' + px + ' ' + pyE;
                        }
                        vD += ' L ' + px + ' ' + pyV;
                    }
                }
                document.getElementById('ePath').setAttribute('d', eD);
                document.getElementById('vPath').setAttribute('d', vD);
                const curE = evalE(r);
                const curV = evalV(r);
                document.getElementById('probeLine').setAttribute('x1', xPos);
                document.getElementById('probeLine').setAttribute('x2', xPos);
                document.getElementById('eDot').setAttribute('cx', xPos);
                document.getElementById('eDot').setAttribute('cy', 240 - curE * eScale);
                document.getElementById('vDot').setAttribute('cx', xPos);
                document.getElementById('vDot').setAttribute('cy', 240 - curV * vScale);
                const region = r < 0.99 ? 'Inside' : (r <= 1.03 ? 'At Surface' : 'Outside');
                document.getElementById('rLabel').textContent = 'r = ' + r.toFixed(2) + ' R (' + region + ')';
                document.getElementById('eVal').textContent = curE === 0 ? '0 (Zero Inside)' : curE.toFixed(2) + ' kQ/R²';
                document.getElementById('vVal').textContent = curV.toFixed(2) + ' kQ/R';
                document.getElementById('formulaBox').textContent = mode === 'hollow'
                    ? 'Hollow Shell: E(r < R) = 0  |  V(r ≤ R) = kQ/R (constant)  |  E(r ≥ R) = kQ/r²'
                    : 'Solid Sphere: E(r < R) = kQr/R³  |  V(center) = 1.5 kQ/R  |  V(r < R) = (kQ/2R³)(3R² − r²)';
            }
            update();
        </script>
        </body>
        </html>
    """.trimIndent()

    private fun buildElectricDipoleHtml(): String = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <style>${sharedShellCss()}</style>
        </head>
        <body>
        <div class="wrap">
            <div class="card">
                <div class="eyebrow">ELECTROSTATICS · ELECTRIC DIPOLE</div>
                <h1>Polar Dipole Field &amp; Torque Explorer</h1>
                <div class="sub">Rotate angle θ from axial (0°) to equatorial (90°) to see |E(θ)| and direction α.</div>
                <div style="margin-top:10px;">
                    <div class="eyebrow">POLAR ANGLE θ = <span id="thetaLbl">0° (Axial)</span></div>
                    <input id="thetaSlider" type="range" min="0" max="90" value="0" oninput="render()" />
                </div>
            </div>
            <svg viewBox="0 0 600 280" aria-label="Electric Dipole Field Vector">
                <circle cx="300" cy="150" r="95" fill="none" stroke="var(--grid)" stroke-width="1.4" stroke-dasharray="4 4"/>
                <line x1="80" y1="150" x2="520" y2="150" stroke="var(--muted)" stroke-width="1.2" stroke-dasharray="4 4"/>
                <line x1="252" y1="150" x2="348" y2="150" stroke="var(--accent)" stroke-width="3.5"/>
                <circle cx="252" cy="150" r="14" fill="var(--card)" stroke="var(--warn)" stroke-width="2.5"/>
                <text x="245" y="154" fill="var(--warn)" font-size="13" font-weight="bold">−q</text>
                <circle cx="348" cy="150" r="14" fill="var(--card)" stroke="var(--ok)" stroke-width="2.5"/>
                <text x="340" y="154" fill="var(--ok)" font-size="13" font-weight="bold">+q</text>
                <text x="292" y="175" fill="var(--accent)" font-size="12" font-family="monospace">p →</text>
                <line id="rVec" x1="300" y1="150" x2="395" y2="150" stroke="var(--muted)" stroke-width="1.8"/>
                <circle id="pDot" cx="395" cy="150" r="6" fill="var(--accent)"/>
                <line id="eVec" x1="395" y1="150" x2="475" y2="150" stroke="var(--accent)" stroke-width="3.5"/>
                <text id="eLbl" x="455" y="136" fill="var(--accent)" font-size="12" font-family="monospace">E_net</text>
            </svg>
            <div class="stat-grid">
                <div class="stat"><div class="stat-k">Field Magnitude</div><div class="stat-v" id="magVal">2.00 kp/r³</div></div>
                <div class="stat"><div class="stat-k">Angle α with r-vector</div><div class="stat-v" id="alphaVal">0.0°</div></div>
            </div>
            <div class="formula">E(r, θ) = (kp / r³) · √(1 + 3cos²θ)   |   tan(α) = ½ tan(θ)   |   W_ext(0°→180°) = 2pE</div>
        </div>
        <script>
            function render() {
                const deg = Number(document.getElementById('thetaSlider').value);
                const rad = deg * Math.PI / 180;
                const px = 300 + 95 * Math.cos(rad);
                const py = 150 - 95 * Math.sin(rad);
                const mag = Math.sqrt(1 + 3 * Math.cos(rad) * Math.cos(rad));
                const alphaRad = deg === 90 ? Math.PI / 2 : Math.atan(0.5 * Math.tan(rad));
                const totalAng = rad + alphaRad;
                const len = 34 * mag;
                const ex = px + len * Math.cos(totalAng);
                const ey = py - len * Math.sin(totalAng);
                document.getElementById('rVec').setAttribute('x2', px);
                document.getElementById('rVec').setAttribute('y2', py);
                document.getElementById('pDot').setAttribute('cx', px);
                document.getElementById('pDot').setAttribute('cy', py);
                document.getElementById('eVec').setAttribute('x1', px);
                document.getElementById('eVec').setAttribute('y1', py);
                document.getElementById('eVec').setAttribute('x2', ex);
                document.getElementById('eVec').setAttribute('y2', ey);
                document.getElementById('eLbl').setAttribute('x', ex - 10);
                document.getElementById('eLbl').setAttribute('y', ey - 10);
                const posName = deg === 0 ? ' (Axial)' : (deg === 90 ? ' (Equatorial)' : '');
                document.getElementById('thetaLbl').textContent = deg + '°' + posName;
                document.getElementById('magVal').textContent = mag.toFixed(2) + ' kp/r³';
                document.getElementById('alphaVal').textContent = (alphaRad * 180 / Math.PI).toFixed(1) + '°';
            }
            render();
        </script>
        </body>
        </html>
    """.trimIndent()

    private fun buildDielectricSlabHtml(): String = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <style>${sharedShellCss()}</style>
        </head>
        <body>
        <div class="wrap">
            <div class="card">
                <div class="eyebrow">CAPACITORS · DIELECTRIC SLAB INSERTION</div>
                <h1>Battery Connected vs. Isolated Capacitor</h1>
                <div class="sub">Adjust dielectric constant K to compare Q, V, E, and stored Energy U.</div>
                <div class="controls">
                    <button id="bConn" class="active" onclick="setMode('conn')">Battery Connected (V const)</button>
                    <button id="bIso" onclick="setMode('iso')">Isolated Plates (Q const)</button>
                </div>
                <div style="margin-top:10px;">
                    <div class="eyebrow">DIELECTRIC CONSTANT K = <span id="kLbl">3.0</span></div>
                    <input id="kSlider" type="range" min="10" max="60" value="30" oninput="render()" />
                </div>
            </div>
            <svg viewBox="0 0 600 220" aria-label="Dielectric Capacitor Diagram">
                <rect x="110" y="34" width="380" height="16" rx="4" fill="var(--accent)"/>
                <rect x="110" y="170" width="380" height="16" rx="4" fill="var(--muted)"/>
                <rect id="slab" x="150" y="56" width="300" height="108" rx="10" fill="var(--surface)" stroke="var(--ok)" stroke-width="2.2"/>
                <text x="235" y="114" fill="var(--ok)" font-size="14" font-family="monospace" id="slabTxt">Dielectric K = 3.0</text>
                <text x="122" y="46" fill="var(--bg)" font-size="11" font-weight="bold">+Q  +  +  +  +  +  +  +  +</text>
                <text x="122" y="182" fill="var(--bg)" font-size="11" font-weight="bold">−Q  −  −  −  −  −  −  −  −</text>
            </svg>
            <div class="stat-grid">
                <div class="stat"><div class="stat-k">Charge Q / Q₀</div><div class="stat-v" id="qOut">3.00 ×</div></div>
                <div class="stat"><div class="stat-k">Voltage V / V₀</div><div class="stat-v" id="vOut">1.00 ×</div></div>
                <div class="stat"><div class="stat-k">Field E / E₀</div><div class="stat-v" id="eOut">1.00 ×</div></div>
                <div class="stat"><div class="stat-k">Energy U / U₀</div><div class="stat-v" id="uOut">3.00 ×</div></div>
            </div>
            <div class="formula" id="fBox">Battery Connected: V fixed ⇒ C → KC, Q → KQ, U = ½CV² → KU (Energy rises)</div>
        </div>
        <script>
            let mode = 'conn';
            function setMode(m) {
                mode = m;
                document.getElementById('bConn').classList.toggle('active', m === 'conn');
                document.getElementById('bIso').classList.toggle('active', m === 'iso');
                render();
            }
            function render() {
                const K = Number(document.getElementById('kSlider').value) / 10;
                document.getElementById('kLbl').textContent = K.toFixed(1);
                document.getElementById('slabTxt').textContent = 'Dielectric Slab  K = ' + K.toFixed(1);
                if (mode === 'conn') {
                    document.getElementById('qOut').textContent = K.toFixed(2) + ' × (↑)';
                    document.getElementById('vOut').textContent = '1.00 × (Fixed)';
                    document.getElementById('eOut').textContent = '1.00 × (Fixed)';
                    document.getElementById('uOut').textContent = K.toFixed(2) + ' × (↑)';
                    document.getElementById('fBox').textContent = 'Battery Connected: V constant ⇒ C → KC, Q → KQ, U = ½(KC)V² = K·U₀';
                } else {
                    document.getElementById('qOut').textContent = '1.00 × (Fixed)';
                    document.getElementById('vOut').textContent = (1 / K).toFixed(2) + ' × (↓)';
                    document.getElementById('eOut').textContent = (1 / K).toFixed(2) + ' × (↓)';
                    document.getElementById('uOut').textContent = (1 / K).toFixed(2) + ' × (↓)';
                    document.getElementById('fBox').textContent = 'Isolated Capacitor: Q constant ⇒ V → V₀/K, E → E₀/K, U = Q²/(2KC) = U₀/K';
                }
            }
            render();
        </script>
        </body>
        </html>
    """.trimIndent()

    private fun buildArrheniusCurveHtml(): String = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <style>${sharedShellCss()}</style>
        </head>
        <body>
        <div class="wrap">
            <div class="card">
                <div class="eyebrow">CHEMICAL KINETICS · ARRHENIUS BARRIER</div>
                <h1>Catalyst Activation Energy &amp; Enthalpy Invariance</h1>
                <div class="sub">Slide catalyst drop ΔEa to observe forward &amp; reverse barrier reduction while ΔH stays identical.</div>
                <div style="margin-top:10px;">
                    <div class="eyebrow">CATALYST REDUCTION ΔEa = <span id="eaLbl">28 kJ/mol</span></div>
                    <input id="eaSlider" type="range" min="0" max="55" value="28" oninput="render()" />
                </div>
            </div>
            <svg viewBox="0 0 600 270" aria-label="Arrhenius Catalyst Energy Profile">
                <line x1="54" y1="230" x2="560" y2="230" stroke="var(--muted)" stroke-width="1.5"/>
                <line x1="54" y1="28" x2="54" y2="230" stroke="var(--muted)" stroke-width="1.5"/>
                <path d="M 70 165 Q 190 165 260 56 Q 330 56 420 198 L 535 198" fill="none" stroke="var(--muted)" stroke-width="2.5" stroke-dasharray="6 4"/>
                <path id="catCurve" d="M 70 165 Q 190 165 260 105 Q 330 105 420 198 L 535 198" fill="none" stroke="var(--accent)" stroke-width="3.2"/>
                <line x1="70" y1="165" x2="535" y2="165" stroke="var(--grid)" stroke-dasharray="3 3"/>
                <line x1="420" y1="165" x2="420" y2="198" stroke="var(--ok)" stroke-width="2.4"/>
                <text x="430" y="186" fill="var(--ok)" font-size="11" font-family="monospace">ΔH = −35 kJ (Fixed)</text>
                <text x="72" y="156" fill="var(--ink)" font-size="11">Reactants</text>
                <text x="466" y="216" fill="var(--ink)" font-size="11">Products</text>
                <text x="208" y="45" fill="var(--muted)" font-size="11">Uncatalyzed Ea = 90 kJ</text>
                <text id="catPeakLbl" x="210" y="96" fill="var(--accent)" font-size="11" font-weight="bold">Catalyzed Ea' = 62 kJ</text>
            </svg>
            <div class="stat-grid">
                <div class="stat"><div class="stat-k">Catalyzed Ea (Fwd)</div><div class="stat-v" id="eaFwd">62 kJ/mol</div></div>
                <div class="stat"><div class="stat-k">Equilibrium K_eq</div><div class="stat-v">Unchanged</div></div>
            </div>
            <div class="formula">log₁₀(k₂ / k₁) = [Ea / (2.303 · R)] · (1/T₁ − 1/T₂)   |   Slope(log₁₀ k vs 1/T) = −Ea / (2.303·R)</div>
        </div>
        <script>
            function render() {
                const drop = Number(document.getElementById('eaSlider').value);
                const peakY = 56 + drop * 1.65;
                const eaNew = 90 - drop;
                document.getElementById('catCurve').setAttribute('d', 'M 70 165 Q 190 165 260 ' + peakY + ' Q 330 ' + peakY + ' 420 198 L 535 198');
                document.getElementById('catPeakLbl').setAttribute('y', peakY - 10);
                document.getElementById('catPeakLbl').textContent = "Catalyzed Ea' = " + eaNew + " kJ";
                document.getElementById('eaLbl').textContent = drop + ' kJ/mol';
                document.getElementById('eaFwd').textContent = eaNew + ' kJ/mol';
            }
            render();
        </script>
        </body>
        </html>
    """.trimIndent()

    private fun buildDnaForkHtml(): String = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <style>${sharedShellCss()}</style>
        </head>
        <body>
        <div class="wrap">
            <div class="card">
                <div class="eyebrow">MOLECULAR INHERITANCE · REPLICATION FORK</div>
                <h1>Leading vs. Lagging Strand Enzymatic Polarity</h1>
                <div class="sub">Tap any enzyme below to inspect its exact role and NCERT polarity rule.</div>
                <div class="controls">
                    <button id="eHel" class="active" onclick="pick('hel')">Helicase</button>
                    <button id="ePol" onclick="pick('pol')">DNA Pol III</button>
                    <button id="ePri" onclick="pick('pri')">RNA Primase</button>
                    <button id="eLig" onclick="pick('lig')">DNA Ligase</button>
                </div>
            </div>
            <svg viewBox="0 0 600 250" aria-label="DNA Replication Fork">
                <path d="M 60 125 L 250 125 L 540 55" fill="none" stroke="var(--muted)" stroke-width="4"/>
                <path d="M 60 140 L 250 140 L 540 210" fill="none" stroke="var(--muted)" stroke-width="4"/>
                <polygon points="240,115 274,132 240,149" fill="var(--accent)"/>
                <text x="180" y="106" fill="var(--accent)" font-size="11" font-family="monospace">Helicase →</text>
                <!-- Leading strand continuous 5' -> 3' -->
                <line x1="520" y1="74" x2="292" y2="124" stroke="var(--ok)" stroke-width="4"/>
                <text x="350" y="88" fill="var(--ok)" font-size="12" font-weight="bold">Leading Strand (5' → 3' Continuous)</text>
                <!-- Lagging strand Okazaki fragments -->
                <line x1="296" y1="150" x2="368" y2="168" stroke="var(--warn)" stroke-width="4"/>
                <line x1="386" y1="173" x2="452" y2="190" stroke="var(--warn)" stroke-width="4"/>
                <line x1="470" y1="194" x2="532" y2="210" stroke="var(--warn)" stroke-width="4"/>
                <text x="310" y="228" fill="var(--warn)" font-size="12" font-weight="bold">Lagging Strand (Okazaki Fragments · DNA Ligase)</text>
                <text x="546" y="60" fill="var(--muted)" font-size="11" font-family="monospace">3'</text>
                <text x="546" y="216" fill="var(--muted)" font-size="11" font-family="monospace">5'</text>
            </svg>
            <div class="formula" id="enzInfo">Helicase unwinds the double helix at the replication fork by breaking hydrogen bonds between complementary bases.</div>
        </div>
        <script>
            const info = {
                hel: "Helicase unwinds the DNA double helix at the replication fork by breaking hydrogen bonds; Topoisomerase relieves supercoiling tension ahead of it.",
                pol: "DNA Polymerase III polymerizes strictly in the 5' → 3' direction using dNTPs (dual role: substrate + energy) and proofreads in 3' → 5'.",
                pri: "RNA Primase lays down short RNA primers with a free 3'-OH group required for DNA Pol III to initiate each Okazaki fragment.",
                lig: "DNA Ligase seals phosphodiester nicks between adjacent Okazaki fragments on the lagging strand after RNA primers are replaced."
            };
            function pick(k) {
                ['hel','pol','pri','lig'].forEach(id => {
                    const el = document.getElementById('e' + id.charAt(0).toUpperCase() + id.slice(1));
                    el.classList.toggle('active', id === k);
                });
                document.getElementById('enzInfo').textContent = info[k];
            }
        </script>
        </body>
        </html>
    """.trimIndent()

    private fun buildKingsSymmetryHtml(): String = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <style>${sharedShellCss()}</style>
        </head>
        <body>
        <div class="wrap">
            <div class="card">
                <div class="eyebrow">DEFINITE INTEGRATION · KING'S PROPERTY</div>
                <h1>Visual Reflection Proof: f(x) + f(a + b − x)</h1>
                <div class="sub">Toggle reflection overlay to see why pairing ∫ f(x)dx with ∫ f(a+b−x)dx collapses to a constant rectangle!</div>
                <div class="controls">
                    <button id="btnOrig" class="active" onclick="setView('orig')">Original f(x)</button>
                    <button id="btnPair" onclick="setView('pair')">Overlay f(a+b−x)</button>
                    <button id="btnSum" onclick="setView('sum')">Sum = Constant Rectangle</button>
                </div>
            </div>
            <svg viewBox="0 0 600 255" aria-label="Kings Property Symmetry">
                <line x1="60" y1="215" x2="540" y2="215" stroke="var(--muted)" stroke-width="1.5"/>
                <line x1="60" y1="35" x2="60" y2="215" stroke="var(--muted)" stroke-width="1.5"/>
                <line x1="300" y1="40" x2="300" y2="215" stroke="var(--grid)" stroke-dasharray="5 5"/>
                <path id="origArea" d="M 80 215 L 80 205 C 190 200 280 120 520 55 L 520 215 Z" fill="var(--accent)" fill-opacity="0.22" stroke="var(--accent)" stroke-width="2.6"/>
                <path id="reflArea" d="M 80 215 L 80 55 C 320 120 410 200 520 205 L 520 215 Z" fill="var(--ok)" fill-opacity="0.0" stroke="var(--ok)" stroke-width="0"/>
                <rect id="sumRect" x="80" y="55" width="440" height="160" fill="var(--warn)" fill-opacity="0.0" stroke="var(--warn)" stroke-width="0"/>
                <text x="76" y="236" fill="var(--muted)" font-size="12" font-family="monospace">a = 0</text>
                <text x="280" y="236" fill="var(--muted)" font-size="12" font-family="monospace">(a+b)/2</text>
                <text x="498" y="236" fill="var(--muted)" font-size="12" font-family="monospace">b = π/2</text>
            </svg>
            <div class="formula" id="kNote">I = ∫[0..π/2] sinⁿx / (sinⁿx + cosⁿx) dx  ⇒  2I = ∫[0..π/2] 1 dx = π/2  ⇒  I = π/4</div>
        </div>
        <script>
            function setView(v) {
                document.getElementById('btnOrig').classList.toggle('active', v === 'orig');
                document.getElementById('btnPair').classList.toggle('active', v === 'pair');
                document.getElementById('btnSum').classList.toggle('active', v === 'sum');
                document.getElementById('reflArea').setAttribute('fill-opacity', v === 'orig' ? '0.0' : '0.20');
                document.getElementById('reflArea').setAttribute('stroke-width', v === 'orig' ? '0' : '2.4');
                document.getElementById('sumRect').setAttribute('fill-opacity', v === 'sum' ? '0.16' : '0.0');
                document.getElementById('sumRect').setAttribute('stroke-width', v === 'sum' ? '2.5' : '0');
            }
        </script>
        </body>
        </html>
    """.trimIndent()

    private fun buildFallbackDiagramHtml(
        title: String,
        chapterTitle: String,
        formula: String,
        points: List<String>,
    ): String {
        val safeTitle = escapeHtml(title)
        val safeChapter = escapeHtml(chapterTitle)
        val safeFormula = escapeHtml(formula)
        val itemsHtml = points.joinToString("") { pt ->
            "<li style='margin:6px 0;color:var(--ink);font-size:13px;'>${escapeHtml(pt)}</li>"
        }
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <style>${sharedShellCss()}</style>
            </head>
            <body>
            <div class="wrap">
                <div class="card">
                    <div class="eyebrow">$safeChapter · SAVED DIAGRAM CODE</div>
                    <h1>$safeTitle</h1>
                    <div class="sub">Generated and cached in private app storage for instant reuse across chats.</div>
                </div>
                <svg viewBox="0 0 600 180" aria-label="$safeTitle">
                    <rect x="28" y="34" width="150" height="110" rx="14" fill="var(--surface)" stroke="var(--accent)" stroke-width="2"/>
                    <text x="48" y="84" fill="var(--accent)" font-size="13" font-weight="bold">Core Concept</text>
                    <text x="48" y="106" fill="var(--muted)" font-size="11">Input / Boundary</text>
                    <line x1="178" y1="89" x2="224" y2="89" stroke="var(--accent)" stroke-width="2.5"/>
                    <rect x="224" y="34" width="152" height="110" rx="14" fill="var(--surface)" stroke="var(--ok)" stroke-width="2"/>
                    <text x="244" y="84" fill="var(--ok)" font-size="13" font-weight="bold">Mechanism</text>
                    <text x="244" y="106" fill="var(--muted)" font-size="11">Key Relation</text>
                    <line x1="376" y1="89" x2="422" y2="89" stroke="var(--ok)" stroke-width="2.5"/>
                    <rect x="422" y="34" width="150" height="110" rx="14" fill="var(--surface)" stroke="var(--warn)" stroke-width="2"/>
                    <text x="442" y="84" fill="var(--warn)" font-size="13" font-weight="bold">Exam Takeaway</text>
                    <text x="442" y="106" fill="var(--muted)" font-size="11">Trap Check</text>
                </svg>
                <div class="formula">$safeFormula</div>
                <div class="card">
                    <div class="eyebrow">KEY VISUAL CHECKPOINTS</div>
                    <ul style="padding-left:18px;margin-top:8px;">$itemsHtml</ul>
                </div>
            </div>
            </body>
            </html>
        """.trimIndent()
    }

    private fun escapeHtml(text: String): String = text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")

    companion object {
        private const val DIAGRAMS_FOLDER_NAME = "diagrams"
        private val SAFE_FILENAME_REGEX = Regex("^image\\.[a-z0-9_]{2,48}\\.html$")

        const val FILE_GAUSS_SHELL = "image.gauss_spherical_shell.html"
        const val FILE_ELECTRIC_DIPOLE = "image.electric_dipole_field.html"
        const val FILE_DIELECTRIC_SLAB = "image.dielectric_slab_field.html"
        const val FILE_ARRHENIUS_CURVE = "image.arrhenius_activation_curve.html"
        const val FILE_DNA_FORK = "image.dna_replication_fork.html"
        const val FILE_KINGS_SYMMETRY = "image.kings_property_symmetry.html"
    }
}

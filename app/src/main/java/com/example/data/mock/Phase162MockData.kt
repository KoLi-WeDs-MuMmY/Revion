package com.example.data.mock

import com.example.data.diagram.PrivateDiagramStore
import com.example.model.ArenaQuestion
import com.example.model.ChapterDiagramRef
import com.example.model.DiagramAttachment
import com.example.model.QuizAttemptSummary
import com.example.model.TestArenaMode
import com.example.model.TutorChatMessage
import com.example.model.TutorChatSession
import com.example.model.VaultChapterNode
import com.example.model.WeakTopicDiagnostic

object Phase162MockData {

    /**
     * Normalizes remapped chapter IDs (e.g. `batch_neet_2026_ch_jee_gauss` -> `ch_jee_gauss`)
     * so chapter-scoped diagram RAG and quiz banks work identically across JEE, NEET, Boards,
     * and custom batches.
     */
    fun canonicalChapterKey(chapterId: String): String {
        val knownKeys = listOf(
            "ch_jee_gauss",
            "ch_jee_cap",
            "ch_jee_kinetics",
            "ch_jee_int",
            "ch_neet_dna",
        )
        return knownKeys.firstOrNull { chapterId.endsWith(it) } ?: chapterId
    }

    fun initialDiagramCatalog(): List<ChapterDiagramRef> = listOf(
        ChapterDiagramRef(
            fileName = PrivateDiagramStore.FILE_GAUSS_SHELL,
            title = "Spherical Shell vs. Solid Sphere E(r) & V(r)",
            chapterKey = "ch_jee_gauss",
            chapterTitle = "Electrostatics & Gauss's Law",
            summary = "Interactive E(r) discontinuity and V(r) continuity explorer for hollow and solid spheres.",
            keywords = listOf("gauss", "shell", "sphere", "potential", "field", "cavity", "flux", "hollow", "solid", "graph", "diagram", "visual"),
        ),
        ChapterDiagramRef(
            fileName = PrivateDiagramStore.FILE_ELECTRIC_DIPOLE,
            title = "Electric Dipole Polar Field & Torque",
            chapterKey = "ch_jee_gauss",
            chapterTitle = "Electrostatics & Gauss's Law",
            summary = "Interactive θ sweep comparing axial (2kp/r³) and equatorial (kp/r³) fields with α = atan(½ tan θ).",
            keywords = listOf("dipole", "axial", "equatorial", "torque", "polar", "angle", "work"),
        ),
        ChapterDiagramRef(
            fileName = PrivateDiagramStore.FILE_DIELECTRIC_SLAB,
            title = "Dielectric Slab: Battery Connected vs. Isolated",
            chapterKey = "ch_jee_cap",
            chapterTitle = "Capacitors & Dielectrics",
            summary = "Interactive K-factor slider contrasting constant-V vs. constant-Q charge, field, and energy shifts.",
            keywords = listOf("dielectric", "slab", "capacitor", "battery", "isolated", "energy", "capacitance", "diagram", "visual"),
        ),
        ChapterDiagramRef(
            fileName = PrivateDiagramStore.FILE_ARRHENIUS_CURVE,
            title = "Arrhenius Catalyst Activation Energy Profile",
            chapterKey = "ch_jee_kinetics",
            chapterTitle = "Chemical Kinetics",
            summary = "Interactive catalyst ΔEa slider demonstrating barrier reduction while net enthalpy ΔH remains fixed.",
            keywords = listOf("arrhenius", "catalyst", "activation", "energy", "kinetics", "slope", "barrier", "plot", "diagram", "visual"),
        ),
        ChapterDiagramRef(
            fileName = PrivateDiagramStore.FILE_DNA_FORK,
            title = "DNA Replication Fork & Enzyme Polarity",
            chapterKey = "ch_neet_dna",
            chapterTitle = "Molecular Basis of Inheritance",
            summary = "Interactive enzyme inspector for Helicase, DNA Pol III, RNA Primase, and DNA Ligase on leading/lagging strands.",
            keywords = listOf("dna", "fork", "replication", "okazaki", "leading", "lagging", "helicase", "ligase", "polymerase", "diagram", "visual"),
        ),
        ChapterDiagramRef(
            fileName = PrivateDiagramStore.FILE_KINGS_SYMMETRY,
            title = "King's Property Visual Area Reflection",
            chapterKey = "ch_jee_int",
            chapterTitle = "Definite Integration",
            summary = "Interactive reflection proof showing f(x) + f(a + b − x) forming a constant rectangle.",
            keywords = listOf("king", "symmetry", "integral", "definite", "reflection", "leibniz", "walli", "area", "diagram", "visual"),
        ),
    )

    fun diagramsForLinkedChapters(
        linkedChapterIds: List<String>,
        catalog: List<ChapterDiagramRef>,
    ): List<ChapterDiagramRef> {
        if (linkedChapterIds.isEmpty()) return emptyList()
        val canonicalKeys = linkedChapterIds.map { canonicalChapterKey(it) }.toSet()
        return catalog.filter { ref ->
            ref.chapterKey in canonicalKeys || ref.chapterKey in linkedChapterIds
        }
    }

    /**
     * Appended silently to the user's prompt so the AI sees ONLY the filenames inside
     * the linked chapter(s) RAG partition and appends a trailing `image.<slug>.html`
     * strictly when a visual diagram adds value.
     */
    fun buildInvisibleDiagramDirective(chapterDiagramFileNames: List<String>): String {
        val listBlock = if (chapterDiagramFileNames.isEmpty()) {
            "(none yet — you may generate and name a new image.<slug>.html if a visual truly helps)"
        } else {
            chapterDiagramFileNames.joinToString(", ")
        }
        return "[SYSTEM_INVISIBLE_RAG] Available chapter diagram files: $listBlock. " +
            "If one matches your explanation, append ONLY its exact filename (image.<name>.html) on the final line. " +
            "If a pure text/formula explanation is sufficient, omit the filename completely."
    }

    private val TRAILING_IMAGE_REGEX = Regex("(?m)^\\s*(image\\.[a-z0-9_]{2,48}\\.html)\\s*$")

    /**
     * Extracts and removes any trailing `image.<slug>.html` directive from the raw AI output
     * so the user sees crisp prose followed by the interactive inline Diagram Artifact card.
     */
    fun parseTrailingImageDirective(rawResponse: String): Pair<String, String?> {
        val matches = TRAILING_IMAGE_REGEX.findAll(rawResponse).toList()
        val fileName = matches.lastOrNull()?.groupValues?.getOrNull(1)
        val cleaned = if (fileName != null) {
            rawResponse.replace(TRAILING_IMAGE_REGEX, "").trimEnd()
        } else {
            rawResponse.trim()
        }
        return cleaned to fileName
    }

    data class SynthesizedTutorTurn(
        val visibleText: String,
        val formulaBlock: String?,
        val citations: List<String>,
        val matchedDiagram: DiagramAttachment?,
        val newlyCatalogedRef: ChapterDiagramRef?,
        val invisiblePromptUsed: String,
    )

    fun synthesizeChapterRagResponse(
        userPrompt: String,
        linkedChapters: List<VaultChapterNode>,
        catalog: List<ChapterDiagramRef>,
        diagramStore: PrivateDiagramStore?,
    ): SynthesizedTutorTurn {
        val scopedDiagrams = diagramsForLinkedChapters(linkedChapters.map { it.id }, catalog)
        val invisiblePrompt = buildInvisibleDiagramDirective(scopedDiagrams.map { it.fileName })
        val lower = userPrompt.lowercase()

        val pureFormulaOrShortText = (lower.contains("definition only") ||
            lower.contains("no diagram") ||
            lower.contains("just formula") ||
            lower.contains("short answer") ||
            lower.contains("unit of") ||
            lower.contains("si unit"))

        val explicitVisualAsk = lower.contains("diagram") ||
            lower.contains("visual") ||
            lower.contains("plot") ||
            lower.contains("graph") ||
            lower.contains("show") ||
            lower.contains("draw") ||
            lower.contains("illustrate") ||
            lower.contains("curve") ||
            lower.contains("fork")

        val matchedRef = if (!pureFormulaOrShortText) {
            scopedDiagrams.maxByOrNull { ref ->
                ref.keywords.count { kw -> lower.contains(kw) }
            }?.takeIf { ref ->
                explicitVisualAsk || ref.keywords.any { kw -> lower.contains(kw) }
            }
        } else {
            null
        }

        val primaryChapter = linkedChapters.firstOrNull()
        val chapterNamesLabel = linkedChapters.joinToString(" + ") { it.title }.ifEmpty { "Selected Chapter" }
        val citations = linkedChapters.take(3).map { "${it.chapterCode} · ${it.title}" }

        // Build raw AI response (ending with trailing `image.<slug>.html` when appropriate)
        var newlyCatalogedRef: ChapterDiagramRef? = null
        val (rawResponse, formula) = when {
            matchedRef?.fileName == PrivateDiagramStore.FILE_GAUSS_SHELL -> {
                """
                Inside a uniformly charged hollow conducting shell, any concentric Gaussian sphere of radius r < R encloses zero net charge (q_enc = 0), so E(r) = 0 everywhere inside.

                Because E = −dV/dr = 0 throughout the cavity, the electric potential cannot drop as you move inward—it stays locked at the surface value V = kQ/R all the way to the center. In contrast, a uniformly charged solid non-conducting sphere has E(r) increasing linearly as kQr/R³ and V peaking at 1.5 kQ/R at the center.

                Explore the interactive shell-vs-solid comparison below to inspect the exact boundary jump at r = R.
                ${matchedRef.fileName}
                """.trimIndent() to "Hollow: E(r < R) = 0, V = kQ/R  |  Solid: V(0) = 1.5 kQ/R"
            }

            matchedRef?.fileName == PrivateDiagramStore.FILE_ELECTRIC_DIPOLE -> {
                """
                For a short electric dipole of moment p = q(2a), the axial field points along p with magnitude 2kp/r³, whereas the equatorial field points antiparallel to p with magnitude kp/r³—exactly half as strong.

                At a general polar angle θ, the resultant field makes an angle α with the radial vector r given by tan(α) = ½ tan(θ). Sweep θ in the interactive vector explorer below to verify both limiting positions.
                ${matchedRef.fileName}
                """.trimIndent() to "E(r, θ) = (kp/r³)√(1 + 3cos²θ)  |  tan(α) = ½ tan(θ)"
            }

            matchedRef?.fileName == PrivateDiagramStore.FILE_DIELECTRIC_SLAB -> {
                """
                Always check which variable stays invariant before computing dielectric scaling:
                • Battery Connected: Potential difference V is clamped by the source, so inserting slab K boosts capacitance to KC, pulls extra charge Q → KQ, and increases stored energy U → KU.
                • Isolated Plates: Total charge Q is trapped, so V drops to V/K, field drops to E/K, and stored energy decreases to U/K because the fringing field pulls the slab inward.

                Use the interactive K-slider below to compare both boundary conditions live.
                ${matchedRef.fileName}
                """.trimIndent() to "V const ⇒ U' = K·U₀  |  Q const ⇒ U' = U₀ / K"
            }

            matchedRef?.fileName == PrivateDiagramStore.FILE_ARRHENIUS_CURVE -> {
                """
                A catalyst introduces an alternative transition pathway that lowers both the forward and reverse activation barriers by the exact same amount ΔEa.

                Because the reactant and product energy plateaus do not shift, net reaction enthalpy ΔH and equilibrium constant K_eq remain completely unchanged—only the rate of reaching equilibrium accelerates. Remember to include 2.303·R in the base-10 Arrhenius slope.
                ${matchedRef.fileName}
                """.trimIndent() to "log₁₀(k₂/k₁) = [Ea / (2.303·R)] · (1/T₁ − 1/T₂)"
            }

            matchedRef?.fileName == PrivateDiagramStore.FILE_DNA_FORK -> {
                """
                DNA Polymerase III can only add deoxyribonucleotides to a free 3'-OH end, strictly synthesizing in the 5' → 3' direction.

                At an unwinding replication fork, the 3' → 5' template strand is copied continuously as the Leading Strand, while the antiparallel 5' → 3' template must be built discontinuously away from the fork in short Okazaki fragments primed by RNA Primase and sealed by DNA Ligase. Tap the enzymes below to trace each step.
                ${matchedRef.fileName}
                """.trimIndent() to "Synthesis: 5' → 3'  |  Proofreading: 3' → 5'  |  dNTP = Substrate + Energy"
            }

            matchedRef?.fileName == PrivateDiagramStore.FILE_KINGS_SYMMETRY -> {
                """
                King's Property states that ∫[a..b] f(x) dx equals ∫[a..b] f(a + b − x) dx. Visually, replacing x by (a + b − x) mirrors the curve horizontally across the midpoint (a + b)/2 without changing the area underneath.

                When you add the original integral I and its reflected twin I, symmetric trigonometric denominators cancel cleanly into a constant integrand of height 1, making 2I = (b − a). Toggle the reflection overlay below to see the area collapse.
                ${matchedRef.fileName}
                """.trimIndent() to "I = ∫[a..b] f(x)dx = ∫[a..b] f(a + b − x)dx"
            }

            // Dynamic diagram creation & caching for user-created custom chapters when asked visually
            explicitVisualAsk && primaryChapter != null && diagramStore != null -> {
                val slug = primaryChapter.title
                val savedFileName = diagramStore.saveDynamicConceptDiagram(
                    slug = slug,
                    title = "${primaryChapter.title} Visual Map",
                    chapterTitle = primaryChapter.title,
                    formula = primaryChapter.lectures.firstOrNull()?.revisionFormula
                        ?: "Core relation for ${primaryChapter.title}",
                    points = listOf(
                        "Linked context: $chapterNamesLabel",
                        primaryChapter.lectures.firstOrNull()?.revisionHeadline
                            ?: "Identify invariant boundary quantities before substituting values.",
                        "Saved permanently to private internal storage ($slug) for future turns.",
                    ),
                )
                newlyCatalogedRef = ChapterDiagramRef(
                    fileName = savedFileName,
                    title = "${primaryChapter.title} Visual Map",
                    chapterKey = canonicalChapterKey(primaryChapter.id),
                    chapterTitle = primaryChapter.title,
                    summary = "AI-generated visual diagram cached privately for reuse.",
                    keywords = primaryChapter.title.lowercase().split(" ").filter { it.length > 3 },
                    isPreseeded = false,
                )
                """
                I built and saved a reusable visual concept map for ${primaryChapter.title} directly in private app storage so future questions in this chapter can open it immediately.

                Break the problem into boundary invariants first, map the governing relation, and verify sign/limit edge cases before plugging in numbers.
                $savedFileName
                """.trimIndent() to (primaryChapter.lectures.firstOrNull()?.revisionFormula)
            }

            else -> {
                val lectureHint = primaryChapter?.lectures?.firstOrNull()
                val headline = lectureHint?.revisionHeadline
                    ?: "Anchor your derivation in the fundamental conservation or boundary condition for $chapterNamesLabel."
                val keyPoint = lectureHint?.revisionKeyPoints?.firstOrNull()
                    ?: "Double-check units, sign conventions, and limiting cases (0 or ∞) to eliminate distractor options."
                """
                Scoped to $chapterNamesLabel:

                1. Core Principle: $headline
                2. Exam Trap Check: $keyPoint
                3. Quick Verification: No diagram was required for this concise derivation—ask me to "show diagram" or "plot graph" anytime if you'd like an interactive visual from this chapter's private RAG store.
                """.trimIndent() to lectureHint?.revisionFormula
            }
        }

        val (visibleText, parsedFileName) = parseTrailingImageDirective(rawResponse)
        val resolvedRef = parsedFileName?.let { fname ->
            (catalog + listOfNotNull(newlyCatalogedRef)).firstOrNull { it.fileName == fname }
        }
        val attachment = parsedFileName?.let { fname ->
            DiagramAttachment(
                fileName = fname,
                title = resolvedRef?.title ?: fname.removePrefix("image.").removeSuffix(".html").replace('_', ' '),
                chapterTitle = resolvedRef?.chapterTitle ?: chapterNamesLabel,
                summary = resolvedRef?.summary ?: "Interactive HTML/SVG diagram from private storage.",
                generatedOnTheFly = newlyCatalogedRef != null,
            )
        }

        return SynthesizedTutorTurn(
            visibleText = visibleText,
            formulaBlock = formula,
            citations = citations,
            matchedDiagram = attachment,
            newlyCatalogedRef = newlyCatalogedRef,
            invisiblePromptUsed = invisiblePrompt,
        )
    }

    fun initialTutorSessions(): List<TutorChatSession> = listOf(
        TutorChatSession(
            id = "chat_gauss_shell_01",
            title = "Hollow Shell Potential Continuity",
            batchId = "batch_jee_2026",
            linkedChapterIds = listOf("ch_jee_gauss"),
            updatedAtLabel = "Yesterday",
            updatedAtMillis = 1_732_600_000_000L,
            messages = listOf(
                TutorChatMessage(
                    id = "msg_g1",
                    isUser = true,
                    visibleText = "Why is electric potential constant inside a hollow charged sphere even though E = 0? Show the diagram.",
                    timestampLabel = "Yesterday · 19:20",
                ),
                TutorChatMessage(
                    id = "msg_g2",
                    isUser = false,
                    visibleText = "Because E = −dV/dr = 0 everywhere inside a uniformly charged hollow conducting shell (r < R), no work is done moving a test charge inside the cavity. Therefore V stays flat at the surface value kQ/R instead of dropping to zero.",
                    formulaBlock = "r < R ⇒ E = 0  |  r ≤ R ⇒ V = kQ/R = constant",
                    citations = listOf("CH 01 · Electrostatics & Gauss's Law"),
                    diagramAttachment = DiagramAttachment(
                        fileName = PrivateDiagramStore.FILE_GAUSS_SHELL,
                        title = "Spherical Shell vs. Solid Sphere E(r) & V(r)",
                        chapterTitle = "Electrostatics & Gauss's Law",
                        summary = "Interactive E(r) discontinuity and V(r) continuity explorer for hollow and solid spheres.",
                    ),
                    invisiblePromptUsed = buildInvisibleDiagramDirective(
                        listOf(
                            PrivateDiagramStore.FILE_GAUSS_SHELL,
                            PrivateDiagramStore.FILE_ELECTRIC_DIPOLE,
                        ),
                    ),
                    timestampLabel = "Yesterday · 19:20",
                ),
            ),
        ),
        TutorChatSession(
            id = "chat_kinetics_arrhenius_02",
            title = "Arrhenius Catalyst & 2.303 Factor",
            batchId = "batch_jee_2026",
            linkedChapterIds = listOf("ch_jee_kinetics"),
            updatedAtLabel = "2 days ago",
            updatedAtMillis = 1_732_520_000_000L,
            messages = listOf(
                TutorChatMessage(
                    id = "msg_k1",
                    isUser = true,
                    visibleText = "Does a positive catalyst shift ΔH or K_eq? Show me the activation energy curve.",
                    timestampLabel = "2 days ago",
                ),
                TutorChatMessage(
                    id = "msg_k2",
                    isUser = false,
                    visibleText = "A catalyst lowers both forward and reverse activation barriers equally by ΔEa. Neither reactant nor product enthalpy levels change, so ΔH and K_eq stay untouched.",
                    formulaBlock = "Slope(log₁₀ k vs 1/T) = −Ea / (2.303 · R)",
                    citations = listOf("CH 04 · Chemical Kinetics"),
                    diagramAttachment = DiagramAttachment(
                        fileName = PrivateDiagramStore.FILE_ARRHENIUS_CURVE,
                        title = "Arrhenius Catalyst Activation Energy Profile",
                        chapterTitle = "Chemical Kinetics",
                        summary = "Interactive catalyst ΔEa slider demonstrating barrier reduction while net enthalpy ΔH remains fixed.",
                    ),
                    invisiblePromptUsed = buildInvisibleDiagramDirective(
                        listOf(PrivateDiagramStore.FILE_ARRHENIUS_CURVE),
                    ),
                    timestampLabel = "2 days ago",
                ),
            ),
        ),
        TutorChatSession(
            id = "chat_dna_fork_03",
            title = "Okazaki Fragments & Pol III Polarity",
            batchId = "batch_neet_2026",
            linkedChapterIds = listOf("ch_neet_dna"),
            updatedAtLabel = "3 days ago",
            updatedAtMillis = 1_732_430_000_000L,
            messages = listOf(
                TutorChatMessage(
                    id = "msg_d1",
                    isUser = true,
                    visibleText = "Compare leading vs lagging strand enzymes with the replication fork visual.",
                    timestampLabel = "3 days ago",
                ),
                TutorChatMessage(
                    id = "msg_d2",
                    isUser = false,
                    visibleText = "DNA Polymerase III synthesizes exclusively 5' → 3'. The 3' → 5' template runs continuously toward the fork, while the 5' → 3' template is built in short Okazaki fragments joined by DNA Ligase.",
                    formulaBlock = "Polymerization: 5' → 3'  |  Proofreading: 3' → 5'",
                    citations = listOf("CH 06 · Molecular Basis of Inheritance"),
                    diagramAttachment = DiagramAttachment(
                        fileName = PrivateDiagramStore.FILE_DNA_FORK,
                        title = "DNA Replication Fork & Enzyme Polarity",
                        chapterTitle = "Molecular Basis of Inheritance",
                        summary = "Interactive enzyme inspector for Helicase, DNA Pol III, RNA Primase, and DNA Ligase.",
                    ),
                    invisiblePromptUsed = buildInvisibleDiagramDirective(
                        listOf(PrivateDiagramStore.FILE_DNA_FORK),
                    ),
                    timestampLabel = "3 days ago",
                ),
            ),
        ),
    )

    // --- PW-Style Quizzes Arena Question Bank (+4 / -1 Exam Mode & Normal Mode) ---

    private val seededQuestionsByChapterKey: Map<String, List<ArenaQuestion>> = mapOf(
        "ch_jee_gauss" to listOf(
            ArenaQuestion(
                id = "q_gauss_1",
                chapterKey = "ch_jee_gauss",
                subjectName = "Physics",
                chapterTitle = "Electrostatics & Gauss's Law",
                topicName = "Hollow Spherical Shell Potential",
                prompt = "A thin hollow conducting spherical shell of radius R carries total charge +Q. What are the electric field E and potential V at a point r = R/2 inside the shell?",
                options = listOf(
                    "E = 0 and V = 0",
                    "E = 0 and V = kQ / R",
                    "E = kQ / (2R²) and V = 2kQ / R",
                    "E = 0 and V = 2kQ / R",
                ),
                correctIndex = 1,
                stepByStepExplanation = listOf(
                    "Step 1: Construct a concentric Gaussian sphere of radius r = R/2 inside the hollow shell.",
                    "Step 2: Enclosed charge q_enc = 0 ⇒ By Gauss's Law, E(r < R) = 0 identically.",
                    "Step 3: Since E = −dV/dr = 0 inside, potential V does not change from surface to center: V(R/2) = V(R) = kQ / R.",
                ),
                formulaAnchor = "r < R ⇒ E = 0, V = kQ/R",
                difficulty = "Core Trap",
            ),
            ArenaQuestion(
                id = "q_gauss_2",
                chapterKey = "ch_jee_gauss",
                subjectName = "Physics",
                chapterTitle = "Electrostatics & Gauss's Law",
                topicName = "Cavity Superposition",
                prompt = "A solid non-conducting sphere of uniform volume charge density ρ has a spherical cavity whose center is displaced by vector d from the sphere's center. The electric field inside the cavity is:",
                options = listOf(
                    "Zero everywhere inside the cavity",
                    "Radial and proportional to distance from cavity center",
                    "Uniform everywhere with E = (ρ · d) / (3ε₀)",
                    "Uniform everywhere with E = (ρ · d) / ε₀",
                ),
                correctIndex = 2,
                stepByStepExplanation = listOf(
                    "Step 1: Superpose a full sphere of density +ρ (centered at O) with a smaller sphere of density −ρ (centered at O').",
                    "Step 2: At any cavity point P, E_net = (ρ / 3ε₀) r₁ − (ρ / 3ε₀) r₂ = (ρ / 3ε₀)(r₁ − r₂).",
                    "Step 3: By vector triangle O→O'→P, (r₁ − r₂) = d (constant vector), so E_cavity = (ρ · d) / (3ε₀) is uniform!",
                ),
                formulaAnchor = "E_cavity = (ρ · d) / (3ε₀)",
                difficulty = "JEE Adv",
            ),
            ArenaQuestion(
                id = "q_gauss_3",
                chapterKey = "ch_jee_gauss",
                subjectName = "Physics",
                chapterTitle = "Electrostatics & Gauss's Law",
                topicName = "Electric Dipole Work & Torque",
                prompt = "How much work must an external agent perform to slowly rotate an electric dipole p in a uniform electric field E from stable equilibrium (θ = 0°) to unstable equilibrium (θ = 180°)?",
                options = listOf("0", "pE", "2pE", "−2pE"),
                correctIndex = 2,
                stepByStepExplanation = listOf(
                    "Step 1: Potential energy of a dipole in uniform field E is U(θ) = −pE cos(θ).",
                    "Step 2: At stable equilibrium θ₁ = 0°, U₁ = −pE. At unstable equilibrium θ₂ = 180°, U₂ = +pE.",
                    "Step 3: For slow rotation (ΔK = 0), W_ext = U₂ − U₁ = (+pE) − (−pE) = +2pE.",
                ),
                formulaAnchor = "W_ext = pE(cos θ₁ − cos θ₂) = 2pE",
                difficulty = "Medium",
            ),
            ArenaQuestion(
                id = "q_gauss_4",
                chapterKey = "ch_jee_gauss",
                subjectName = "Physics",
                chapterTitle = "Electrostatics & Gauss's Law",
                topicName = "Gaussian Flux Invariance",
                prompt = "A point charge +q sits inside a closed irregular Gaussian surface, while another charge +5q is placed just outside. If the external +5q charge is moved closer to the surface, what happens to the net outward flux Φ and local field E on the surface?",
                options = listOf(
                    "Both Φ and local E stay unchanged",
                    "Φ stays q/ε₀, while local E on the surface changes",
                    "Both Φ and local E increase",
                    "Φ increases while local E stays unchanged",
                ),
                correctIndex = 1,
                stepByStepExplanation = listOf(
                    "Step 1: Net electric flux Φ = ∮ E·dA depends strictly on enclosed charge q_enc / ε₀ = +q / ε₀.",
                    "Step 2: Local field E at every surface patch is the vector sum of ALL charges (both inside and outside).",
                    "Step 3: Moving the external +5q changes local E, but its positive and negative flux contributions cancel identically.",
                ),
                formulaAnchor = "Φ_net = q_enc / ε₀ (independent of external charges)",
                difficulty = "Conceptual",
            ),
        ),
        "ch_jee_cap" to listOf(
            ArenaQuestion(
                id = "q_cap_1",
                chapterKey = "ch_jee_cap",
                subjectName = "Physics",
                chapterTitle = "Capacitors & Dielectrics",
                topicName = "Isolated Dielectric Slab",
                prompt = "An isolated charged parallel-plate capacitor (battery removed) has stored energy U₀. A dielectric slab of constant K = 4 is slowly inserted to completely fill the gap. The new stored energy is:",
                options = listOf("4 U₀", "U₀", "U₀ / 4", "U₀ / 16"),
                correctIndex = 2,
                stepByStepExplanation = listOf(
                    "Step 1: Because the battery is disconnected, plate charge Q remains strictly invariant.",
                    "Step 2: Capacitance increases to C' = K·C = 4C.",
                    "Step 3: Using U = Q² / (2C'), U' = Q² / (2·4C) = U₀ / 4. The missing energy did positive mechanical work pulling the slab in.",
                ),
                formulaAnchor = "Isolated (Q const): U' = U₀ / K",
                difficulty = "Core Trap",
            ),
            ArenaQuestion(
                id = "q_cap_2",
                chapterKey = "ch_jee_cap",
                subjectName = "Physics",
                chapterTitle = "Capacitors & Dielectrics",
                topicName = "Battery-Connected Slab",
                prompt = "A parallel-plate capacitor stays connected to a constant-voltage battery V₀ while a dielectric slab (K = 3) fills the space between the plates. Which quantity stays unchanged?",
                options = listOf(
                    "Charge Q on the plates",
                    "Stored electrostatic energy U",
                    "Electric field magnitude E between plates",
                    "Capacitance C",
                ),
                correctIndex = 2,
                stepByStepExplanation = listOf(
                    "Step 1: The battery clamps the potential difference V = V₀ across plate separation d.",
                    "Step 2: Uniform field E = V₀ / d depends only on V₀ and d, so E stays completely unchanged!",
                    "Step 3: Meanwhile C → 3C, Q → 3Q, and U = ½CV² → 3U.",
                ),
                formulaAnchor = "Battery Connected (V const): E = V₀ / d unchanged",
                difficulty = "Medium",
            ),
            ArenaQuestion(
                id = "q_cap_3",
                chapterKey = "ch_jee_cap",
                subjectName = "Physics",
                chapterTitle = "Capacitors & Dielectrics",
                topicName = "Partial Slab Equivalent Thickness",
                prompt = "A slab of thickness t = d/2 and dielectric constant K = 2 is inserted between plates separated by d. What is the ratio C' / C₀?",
                options = listOf("4 / 3", "3 / 2", "2", "3 / 4"),
                correctIndex = 0,
                stepByStepExplanation = listOf(
                    "Step 1: Effective air gap with partial slab is d_eff = d − t + t/K.",
                    "Step 2: Substitute t = d/2 and K = 2 ⇒ d_eff = d − d/2 + d/4 = 3d/4.",
                    "Step 3: Therefore C' / C₀ = d / d_eff = 4 / 3.",
                ),
                formulaAnchor = "C' = ε₀A / (d − t + t/K)",
                difficulty = "Medium",
            ),
        ),
        "ch_jee_kinetics" to listOf(
            ArenaQuestion(
                id = "q_kin_1",
                chapterKey = "ch_jee_kinetics",
                subjectName = "Chemistry",
                chapterTitle = "Chemical Kinetics",
                topicName = "Arrhenius Base-10 Slope",
                prompt = "For a reaction obeying the Arrhenius equation, a plot of log₁₀(k) versus (1 / T) yields a straight line with slope equal to:",
                options = listOf("−Ea / R", "−Ea / (2.303 · R)", "+Ea / (2.303 · R)", "−2.303 · Ea / R"),
                correctIndex = 1,
                stepByStepExplanation = listOf(
                    "Step 1: Start from k = A · e^(−Ea/RT) and take natural log: ln(k) = ln(A) − Ea/(RT).",
                    "Step 2: Divide both sides by 2.303 to convert to base-10 logarithm: log₁₀(k) = log₁₀(A) − [Ea / (2.303·R)]·(1/T).",
                    "Step 3: Comparing with y = c + mx gives slope m = −Ea / (2.303 · R).",
                ),
                formulaAnchor = "Slope(log₁₀ k vs 1/T) = −Ea / (2.303·R)",
                difficulty = "Core Trap",
            ),
            ArenaQuestion(
                id = "q_kin_2",
                chapterKey = "ch_jee_kinetics",
                subjectName = "Chemistry",
                chapterTitle = "Chemical Kinetics",
                topicName = "Catalyst & Equilibrium",
                prompt = "Adding a positive catalyst lowers the forward activation energy of a reversible exothermic reaction by 25 kJ/mol. What happens to the reverse activation energy and K_eq?",
                options = listOf(
                    "Reverse Ea drops by 25 kJ/mol; K_eq increases",
                    "Reverse Ea drops by 25 kJ/mol; K_eq stays unchanged",
                    "Reverse Ea is unchanged; K_eq stays unchanged",
                    "Reverse Ea increases by 25 kJ/mol; K_eq decreases",
                ),
                correctIndex = 1,
                stepByStepExplanation = listOf(
                    "Step 1: A catalyst lowers the transition-state energy peak without moving reactant or product levels.",
                    "Step 2: Hence both forward Ea and reverse Ea decrease by the exact same 25 kJ/mol.",
                    "Step 3: Both k_f and k_b accelerate by the same factor, keeping K_eq = k_f / k_b strictly invariant.",
                ),
                formulaAnchor = "ΔEa(fwd) = ΔEa(rev) ⇒ K_eq invariant",
                difficulty = "Conceptual",
            ),
            ArenaQuestion(
                id = "q_kin_3",
                chapterKey = "ch_jee_kinetics",
                subjectName = "Chemistry",
                chapterTitle = "Chemical Kinetics",
                topicName = "Parallel Reaction Effective Ea",
                prompt = "Reactant A undergoes two parallel first-order paths with rate constants k₁, k₂ and activation energies Ea₁, Ea₂. The effective activation energy Ea_eff is:",
                options = listOf(
                    "(Ea₁ + Ea₂) / 2",
                    "(k₁·Ea₁ + k₂·Ea₂) / (k₁ + k₂)",
                    "Ea₁ + Ea₂",
                    "(k₁·Ea₂ + k₂·Ea₁) / (k₁ + k₂)",
                ),
                correctIndex = 1,
                stepByStepExplanation = listOf(
                    "Step 1: Overall rate constant for parallel pathways is k_eff = k₁ + k₂.",
                    "Step 2: Differentiate ln(k_eff) with respect to T: (1/k_eff)(dk_eff/dT) = (k₁·Ea₁ + k₂·Ea₂) / [(k₁ + k₂)·RT²].",
                    "Step 3: Matching with Ea_eff / RT² yields the rate-weighted average (k₁·Ea₁ + k₂·Ea₂) / (k₁ + k₂).",
                ),
                formulaAnchor = "Ea_eff = (k₁Ea₁ + k₂Ea₂) / (k₁ + k₂)",
                difficulty = "JEE Adv",
            ),
        ),
        "ch_neet_dna" to listOf(
            ArenaQuestion(
                id = "q_dna_1",
                chapterKey = "ch_neet_dna",
                subjectName = "Biology",
                chapterTitle = "Molecular Basis of Inheritance",
                topicName = "Polymerase Polarity & Dual dNTP Role",
                prompt = "During DNA replication in prokaryotes, deoxyribonucleoside triphosphates (dNTPs) perform which dual role?",
                options = listOf(
                    "Unwinding helix and sealing Okazaki fragments",
                    "Acting as substrates and supplying energy via terminal phosphate cleavage",
                    "Synthesizing RNA primers and proofreading 5' → 3'",
                    "Stabilizing single strands and relieving supercoiling",
                ),
                correctIndex = 1,
                stepByStepExplanation = listOf(
                    "Step 1: NCERT explicitly highlights the dual function of dNTPs at the replication fork.",
                    "Step 2: First, they serve as monomer substrates matching the template strand.",
                    "Step 3: Second, cleaving their two high-energy terminal phosphates supplies energy for polymerization.",
                ),
                formulaAnchor = "dNTPs = Substrate + Polymerization Energy",
                difficulty = "NCERT Direct",
            ),
            ArenaQuestion(
                id = "q_dna_2",
                chapterKey = "ch_neet_dna",
                subjectName = "Biology",
                chapterTitle = "Molecular Basis of Inheritance",
                topicName = "Meselson-Stahl Density Ratios",
                prompt = "Heavy ¹⁵N/¹⁵N E. coli DNA is transferred to light ¹⁴N medium and allowed to divide for 3 generations (60 minutes). What percentage of DNA molecules are hybrid (¹⁵N/¹⁴N)?",
                options = listOf("50%", "25%", "12.5%", "0%"),
                correctIndex = 1,
                stepByStepExplanation = listOf(
                    "Step 1: After n generations of semi-conservative replication, total DNA duplexes = 2ⁿ = 2³ = 8.",
                    "Step 2: Exactly 2 duplexes ALWAYS retain one original ¹⁵N parental strand (hybrid ¹⁵N/¹⁴N).",
                    "Step 3: Hybrid percentage = (2 / 8) × 100% = 25%.",
                ),
                formulaAnchor = "Hybrid % at Gen n = (2 / 2ⁿ) × 100%",
                difficulty = "Core Trap",
            ),
            ArenaQuestion(
                id = "q_dna_3",
                chapterKey = "ch_neet_dna",
                subjectName = "Biology",
                chapterTitle = "Molecular Basis of Inheritance",
                topicName = "Lagging Strand Ligation",
                prompt = "Which enzyme joins adjacent Okazaki fragments on the discontinuously synthesized lagging strand?",
                options = listOf("DNA Helicase", "RNA Primase", "DNA Ligase", "Topoisomerase II"),
                correctIndex = 2,
                stepByStepExplanation = listOf(
                    "Step 1: Leading strand runs continuously 5' → 3' on the 3' → 5' template.",
                    "Step 2: Lagging strand forms discontinuous Okazaki fragments that leave sugar-phosphate nicks.",
                    "Step 3: DNA Ligase catalyzes phosphodiester bond formation to seal those nicks.",
                ),
                formulaAnchor = "DNA Ligase seals Okazaki phosphodiester nicks",
                difficulty = "NCERT Direct",
            ),
        ),
        "ch_jee_int" to listOf(
            ArenaQuestion(
                id = "q_int_1",
                chapterKey = "ch_jee_int",
                subjectName = "Maths",
                chapterTitle = "Definite Integration",
                topicName = "King's Property Collapse",
                prompt = "Evaluate the definite integral I = ∫[0..π/2] [sin²⁰²⁶(x) / (sin²⁰²⁶(x) + cos²⁰²⁶(x))] dx:",
                options = listOf("π / 2", "π / 4", "1", "π / 8"),
                correctIndex = 1,
                stepByStepExplanation = listOf(
                    "Step 1: Apply King's Property x → (π/2 − x): sin(π/2 − x) = cos(x) and cos(π/2 − x) = sin(x).",
                    "Step 2: Add original I and transformed I: 2I = ∫[0..π/2] [(sin²⁰²⁶x + cos²⁰²⁶x) / (sin²⁰²⁶x + cos²⁰²⁶x)] dx = ∫[0..π/2] 1 dx.",
                    "Step 3: 2I = π/2 ⇒ I = π/4.",
                ),
                formulaAnchor = "2I = ∫[a..b] 1 dx ⇒ I = (b − a) / 2",
                difficulty = "Core Trap",
            ),
            ArenaQuestion(
                id = "q_int_2",
                chapterKey = "ch_jee_int",
                subjectName = "Maths",
                chapterTitle = "Definite Integration",
                topicName = "Walli's Reduction Multiplier",
                prompt = "When applying Walli's formula to ∫[0..π/2] sinᵐ(x)·cosⁿ(x) dx, when MUST you multiply the fraction by the extra factor (π/2)?",
                options = listOf(
                    "Whenever m + n is odd",
                    "Whenever at least one of m or n is even",
                    "Strictly when BOTH m and n are even integers",
                    "Always for every positive integer m and n",
                ),
                correctIndex = 2,
                stepByStepExplanation = listOf(
                    "Step 1: Walli's reduction repeatedly pulls out ratios down to either 1 or 2.",
                    "Step 2: Only when both m and n are even does the terminal integral reduce to ∫[0..π/2] 1 dx = π/2.",
                    "Step 3: If either exponent is odd, the multiplier is simply 1.",
                ),
                formulaAnchor = "m, n both even ⇒ × (π/2)  |  Any odd ⇒ × 1",
                difficulty = "Medium",
            ),
            ArenaQuestion(
                id = "q_int_3",
                chapterKey = "ch_jee_int",
                subjectName = "Maths",
                chapterTitle = "Definite Integration",
                topicName = "Newton-Leibniz Variable Limits",
                prompt = "Compute d/dx [ ∫[x..x²] cos(t²) dt ] using the Newton-Leibniz rule:",
                options = listOf(
                    "cos(x⁴) − cos(x²)",
                    "2x · cos(x⁴) − cos(x²)",
                    "2x · cos(x²) − cos(x)",
                    "−2x · sin(x⁴) + sin(x²)",
                ),
                correctIndex = 1,
                stepByStepExplanation = listOf(
                    "Step 1: Recall Newton-Leibniz: d/dx ∫[h(x)..g(x)] f(t) dt = f(g(x))·g'(x) − f(h(x))·h'(x).",
                    "Step 2: Here g(x) = x² (g' = 2x), h(x) = x (h' = 1), and f(t) = cos(t²).",
                    "Step 3: Plugging in yields cos((x²)²)·(2x) − cos(x²)·(1) = 2x·cos(x⁴) − cos(x²).",
                ),
                formulaAnchor = "f(g(x))·g'(x) − f(h(x))·h'(x)",
                difficulty = "Medium",
            ),
        ),
    )

    fun questionsForChapter(chapter: VaultChapterNode): List<ArenaQuestion> {
        val canonical = canonicalChapterKey(chapter.id)
        val seeded = seededQuestionsByChapterKey[canonical]
        if (seeded != null) {
            return seeded.map { q ->
                q.copy(
                    chapterTitle = chapter.title,
                    subjectName = chapter.subjectName,
                )
            }
        }
        // Dynamic generator for any custom user-created chapter so Quizzes works everywhere!
        val firstLecture = chapter.lectures.firstOrNull()
        val formula = firstLecture?.revisionFormula ?: "Core governing relation of ${chapter.title}"
        val headline = firstLecture?.revisionHeadline ?: "Verify invariant conditions and limiting behavior first."
        return listOf(
            ArenaQuestion(
                id = "dyn_${chapter.id}_1",
                chapterKey = chapter.id,
                subjectName = chapter.subjectName,
                chapterTitle = chapter.title,
                topicName = "${chapter.title} · Core Principle",
                prompt = "When solving high-weightage problems in ${chapter.title}, which verification step eliminates the majority of conceptual sign and boundary errors?",
                options = listOf(
                    "Memorizing numeric constants without checking units",
                    "Identifying invariant boundary quantities and checking limiting cases",
                    "Skipping free-body or state diagrams to save time",
                    "Assuming linear proportionality in every regime",
                ),
                correctIndex = 1,
                stepByStepExplanation = listOf(
                    "Step 1: Anchor the setup using the chapter's invariant rule: $headline",
                    "Step 2: Verify dimensional consistency and boundary limits (0 or ∞) before selecting an option.",
                ),
                formulaAnchor = formula,
                difficulty = "Core Trap",
            ),
            ArenaQuestion(
                id = "dyn_${chapter.id}_2",
                chapterKey = chapter.id,
                subjectName = chapter.subjectName,
                chapterTitle = chapter.title,
                topicName = "${chapter.title} · Formula Anchor",
                prompt = "In ${chapter.subjectName} — ${chapter.title}, which strategy best locks in long-term retention under Exam Mode (+4 / −1)?",
                options = listOf(
                    "Blindly guessing on every uncertain option",
                    "Eliminating at least two distractors using physical/mathematical limits before committing",
                    "Ignoring marked-for-review questions until time runs out",
                    "Only reading solutions passively without active recall",
                ),
                correctIndex = 1,
                stepByStepExplanation = listOf(
                    "Step 1: With +4 / −1 negative marking, random guessing hurts expected score whenever fewer than 2 distractors are eliminated.",
                    "Step 2: Use Mark for Review on multi-step derivations and revisit them once direct recalls are banked.",
                ),
                formulaAnchor = "Expected Value > 0 when P(correct) > 20%",
                difficulty = "Strategy",
            ),
            ArenaQuestion(
                id = "dyn_${chapter.id}_3",
                chapterKey = chapter.id,
                subjectName = chapter.subjectName,
                chapterTitle = chapter.title,
                topicName = "${chapter.title} · Revision Checkpoint",
                prompt = "Which active-recall sequence aligns with your ${chapter.chapterCode} (${chapter.title}) syllabus notes?",
                options = listOf(
                    "Derive key relation → test boundary edge case → review mistakes in AI Tutor",
                    "Read highlighted notes once without attempting questions",
                    "Skip revision until the final week before exams",
                    "Avoid checking wrong options after submitting a quiz",
                ),
                correctIndex = 0,
                stepByStepExplanation = listOf(
                    "Step 1: Active derivation + immediate mistake autopsy converts short-term familiarity into exam-day speed.",
                    "Step 2: Link ${chapter.title} in AI Tutor to drill any subtopic flagged as weak.",
                ),
                formulaAnchor = formula,
                difficulty = "Medium",
            ),
        )
    }

    fun initialRecentQuizAttempts(): List<QuizAttemptSummary> = listOf(
        QuizAttemptSummary(
            id = "att_seed_1",
            batchId = "batch_jee_2026",
            subjectId = "pw_jee_phys",
            chapterId = "ch_jee_gauss",
            subjectName = "Physics",
            chapterTitle = "Electrostatics & Gauss's Law",
            mode = TestArenaMode.EXAM,
            correctCount = 3,
            wrongCount = 1,
            unattemptedCount = 0,
            netScore = 11,
            maxScore = 16,
            accuracyPercent = 75,
            weakTopics = listOf(
                WeakTopicDiagnostic(
                    chapterId = "ch_jee_gauss",
                    chapterTitle = "Electrostatics & Gauss's Law",
                    topicName = "Cavity Superposition",
                    missedCount = 1,
                    totalCount = 1,
                ),
            ),
            timestampLabel = "Yesterday · +4/−1",
        ),
        QuizAttemptSummary(
            id = "att_seed_2",
            batchId = "batch_jee_2026",
            subjectId = "pw_jee_chem",
            chapterId = "ch_jee_kinetics",
            subjectName = "Chemistry",
            chapterTitle = "Chemical Kinetics",
            mode = TestArenaMode.NORMAL,
            correctCount = 3,
            wrongCount = 0,
            unattemptedCount = 0,
            netScore = 3,
            maxScore = 3,
            accuracyPercent = 100,
            weakTopics = emptyList(),
            timestampLabel = "2 days ago · Normal",
        ),
    )
}

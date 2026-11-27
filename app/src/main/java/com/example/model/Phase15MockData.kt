package com.example.model

import com.example.ui.theme.AccentMood

object Phase15MockData {

    fun createChapters(): List<RagChapterContext> = listOf(
        RagChapterContext(
            id = "rag_phys_gauss",
            subjectName = "Physics",
            accentMood = AccentMood.CYAN,
            chapterTitle = "Electrostatics — Gauss's Law & Conductors",
            pdfFileName = "Ch02_Gauss_Law_MasterDeck.pdf",
            pageCount = 42,
            sectionTags = listOf("§2.1 Electric Flux", "§2.3 Spherical Shells", "§2.5 Cavity Superposition"),
            memoryTurnsCount = 14,
            quickPrompts = listOf(
                "Why is V constant inside a hollow shell when E = 0?",
                "Derive cavity field using superposition",
                "Summarize top 3 Gauss's Law exam traps",
            ),
            summaryNote = "Whole-chapter vector index loaded · Cross-linked with yesterday's Physics Quiz errors.",
        ),
        RagChapterContext(
            id = "rag_chem_kinetics",
            subjectName = "Chemistry",
            accentMood = AccentMood.VIOLET,
            chapterTitle = "Chemical Kinetics — Arrhenius & Order Laws",
            pdfFileName = "Ch04_Chemical_Kinetics_Full.pdf",
            pageCount = 36,
            sectionTags = listOf("§4.2 Half-Life Scaling", "§4.4 Arrhenius Plots", "§4.6 Parallel Paths"),
            memoryTurnsCount = 9,
            quickPrompts = listOf(
                "Compare ln(k) vs log10(k) Arrhenius slope",
                "How does a catalyst shift forward & reverse Ea?",
                "Quick trick for 1st vs 2nd order half-life",
            ),
            summaryNote = "Whole-chapter vector index loaded · Tracks your 2.303 conversion slip from Mistake Bank.",
        ),
        RagChapterContext(
            id = "rag_bio_dna",
            subjectName = "Biology",
            accentMood = AccentMood.EMERALD,
            chapterTitle = "Molecular Inheritance — DNA Replication Fork",
            pdfFileName = "Ch06_Molecular_Basis_NCERT_Annotated.pdf",
            pageCount = 48,
            sectionTags = listOf("§6.2 Meselson-Stahl", "§6.4 Replication Enzymes", "§6.5 Okazaki Ligation"),
            memoryTurnsCount = 11,
            quickPrompts = listOf(
                "Walk through lagging strand enzyme handoff",
                "Explain Pol III 3'->5' proofreading polarity",
                "Predict Generation 3 hybrid band percentage",
            ),
            summaryNote = "Whole-chapter vector index loaded · High-yield diagram references indexed.",
        ),
        RagChapterContext(
            id = "rag_math_int",
            subjectName = "Maths",
            accentMood = AccentMood.AMBER,
            chapterTitle = "Definite Integration — Symmetry & Leibniz",
            pdfFileName = "Ch07_Definite_Integrals_Clinic.pdf",
            pageCount = 39,
            sectionTags = listOf("§7.1 King's Property", "§7.3 Newton-Leibniz", "§7.5 Walli's Reduction"),
            memoryTurnsCount = 7,
            quickPrompts = listOf(
                "When does King's Property collapse an integral?",
                "Check chain rule in Newton-Leibniz bounds",
                "When do we multiply by pi/2 in Walli's formula?",
            ),
            summaryNote = "Whole-chapter vector index loaded · Linked to Weekend Hard Parts clinic.",
        ),
    )

    fun createInitialMessages(): Map<String, List<TutorMessage>> = mapOf(
        "rag_phys_gauss" to listOf(
            TutorMessage(
                id = "m_phys_1",
                isUser = true,
                text = "In my quiz I marked V = 0 inside a hollow charged conducting sphere because E = 0. Where does the chapter explain this?",
                timestampLabel = "09:12",
            ),
            TutorMessage(
                id = "m_phys_2",
                isUser = false,
                text = "Your chapter PDF specifically flags this in Section 2.3. Because E = −dV/dr = 0 everywhere for r < R, zero electric field means zero spatial change in potential—not zero potential. Walking inward from the surface r = R requires zero work, so every interior point stays locked at the surface potential kQ/R.",
                timestampLabel = "09:12",
                citations = listOf(
                    TutorCitation("p. 14", "§2.3 Spherical Shells"),
                    TutorCitation("p. 16", "Fig 2.8 Potential Curve"),
                ),
                formulaBlock = "r < R ⇒ E(r) = −dV/dr = 0   ⇒   V(r) = V(R) = kQ / R",
            ),
        ),
        "rag_chem_kinetics" to listOf(
            TutorMessage(
                id = "m_chem_1",
                isUser = true,
                text = "Why did I lose marks using slope = −Ea/R on a log10(k) vs 1/T plot?",
                timestampLabel = "08:40",
            ),
            TutorMessage(
                id = "m_chem_2",
                isUser = false,
                text = "Converting natural log ln(k) to base-10 introduces the constant ln(10) ≈ 2.303. Check the boxed conversion table on page 21: for log10(k) vs 1/T, always multiply the magnitude of the slope by both 2.303 and R = 8.314 J·mol⁻¹·K⁻¹.",
                timestampLabel = "08:40",
                citations = listOf(
                    TutorCitation("p. 21", "§4.4 Arrhenius Plots"),
                ),
                formulaBlock = "log₁₀(k) = log₁₀(A) − [Ea / (2.303 · R)] · (1/T)",
            ),
        ),
        "rag_bio_dna" to listOf(
            TutorMessage(
                id = "m_bio_1",
                isUser = true,
                text = "Give me the cleanest sequence for lagging-strand synthesis from unwinding to sealing.",
                timestampLabel = "Yesterday",
            ),
            TutorMessage(
                id = "m_bio_2",
                isUser = false,
                text = "Section 6.4 breaks the fork into five strictly ordered handoffs: Helicase separates the duplex → RNA Primase lays a free 3'-OH primer → DNA Pol III extends Okazaki fragments 5'→3' → DNA Pol I removes RNA primers and fills DNA nucleotides → DNA Ligase seals the final phosphodiester nick.",
                timestampLabel = "Yesterday",
                citations = listOf(
                    TutorCitation("p. 27", "§6.4 Replication Enzymes"),
                    TutorCitation("p. 29", "§6.5 Okazaki Ligation"),
                ),
                formulaBlock = "Helicase → Primase → Pol III (5'→3') → Pol I → Ligase",
            ),
        ),
        "rag_math_int" to listOf(
            TutorMessage(
                id = "m_math_1",
                isUser = true,
                text = "How do I avoid dropping the upper-limit derivative in Newton-Leibniz questions?",
                timestampLabel = "Yesterday",
            ),
            TutorMessage(
                id = "m_math_2",
                isUser = false,
                text = "Page 19 recommends writing the template f(upper)·(upper)' − f(lower)·(lower)' BEFORE substituting the integrand. That keeps variable bounds like x² or sin(x) from silently losing their chain-rule factors.",
                timestampLabel = "Yesterday",
                citations = listOf(
                    TutorCitation("p. 19", "§7.3 Newton-Leibniz"),
                ),
                formulaBlock = "d/dx ∫[h(x)..g(x)] f(t)dt = f(g(x))·g'(x) − f(h(x))·h'(x)",
            ),
        ),
    )

    fun createInitialPlannerBlocks(): List<PlannerBlock> = listOf(
        PlannerBlock(
            id = "blk_1",
            title = "Physics — Gauss's Law Deep Study",
            subtitle = "Spherical cavity superposition & conductor boundary cases",
            category = PlannerBlockCategory.STUDY,
            subjectName = "Physics",
            accentMood = AccentMood.CYAN,
            startMinutes = 390, // 06:30
            durationMinutes = 90,
            isCompleted = true,
            isCriticalPriority = false,
            approvalStatus = PlannerApprovalStatus.AI_SCHEDULED,
        ),
        PlannerBlock(
            id = "blk_2",
            title = "Morning Fuel & Hydration Break",
            subtitle = "Protected recovery & screen-off reset",
            category = PlannerBlockCategory.BREAK_MEAL,
            subjectName = "Wellness",
            accentMood = AccentMood.EMERALD,
            startMinutes = 480, // 08:00
            durationMinutes = 45,
            isCompleted = true,
            isCriticalPriority = false,
            approvalStatus = PlannerApprovalStatus.AI_SCHEDULED,
        ),
        PlannerBlock(
            id = "blk_3",
            title = "Yesterday's Synthesis — Daily x-1 Revision",
            subtitle = "Electrostatics + DNA Fork diagrams (Protected x-1 window)",
            category = PlannerBlockCategory.REVISION,
            subjectName = "Compound Mix",
            accentMood = AccentMood.CYAN,
            startMinutes = 540, // 09:00
            durationMinutes = 60,
            isCompleted = true,
            isCriticalPriority = true,
            approvalStatus = PlannerApprovalStatus.AI_SCHEDULED,
        ),
        PlannerBlock(
            id = "blk_4",
            title = "Chemistry — Arrhenius Kinetics Sprint",
            subtitle = "Base-10 log slope drills & parallel pathway activation energy",
            category = PlannerBlockCategory.STUDY,
            subjectName = "Chemistry",
            accentMood = AccentMood.VIOLET,
            startMinutes = 630, // 10:30
            durationMinutes = 90,
            isCompleted = false,
            isCriticalPriority = false,
            approvalStatus = PlannerApprovalStatus.AI_SCHEDULED,
        ),
        PlannerBlock(
            id = "blk_5",
            title = "Lunch & Afternoon Decompression",
            subtitle = "Balanced meal + 15m eyes-closed cognitive reset",
            category = PlannerBlockCategory.BREAK_MEAL,
            subjectName = "Wellness",
            accentMood = AccentMood.EMERALD,
            startMinutes = 780, // 13:00
            durationMinutes = 60,
            isCompleted = false,
            isCriticalPriority = false,
            approvalStatus = PlannerApprovalStatus.AI_SCHEDULED,
        ),
        PlannerBlock(
            id = "blk_6",
            title = "Timed Exam-Mode Subject Quiz",
            subtitle = "Physics & Chemistry mixed recall + weak-topic scan",
            category = PlannerBlockCategory.QUIZ,
            subjectName = "Physics",
            accentMood = AccentMood.AMBER,
            startMinutes = 930, // 15:30
            durationMinutes = 45,
            isCompleted = false,
            isCriticalPriority = true,
            approvalStatus = PlannerApprovalStatus.AI_SCHEDULED,
        ),
        PlannerBlock(
            id = "blk_7",
            title = "Spaced Flashcards (x-1 & x-3) + Mistake Bank",
            subtitle = "Clear remaining recall queue & promote Weekend Hard traps",
            category = PlannerBlockCategory.FLASHCARDS,
            subjectName = "Biology",
            accentMood = AccentMood.EMERALD,
            startMinutes = 1140, // 19:00
            durationMinutes = 60,
            isCompleted = false,
            isCriticalPriority = true,
            approvalStatus = PlannerApprovalStatus.AI_SCHEDULED,
        ),
        PlannerBlock(
            id = "blk_8",
            title = "Maths — Definite Integration Problem Set",
            subtitle = "King's Property & Newton-Leibniz limit differentiation",
            category = PlannerBlockCategory.STUDY,
            subjectName = "Maths",
            accentMood = AccentMood.AMBER,
            startMinutes = 1245, // 20:45
            durationMinutes = 75,
            isCompleted = false,
            isCriticalPriority = false,
            approvalStatus = PlannerApprovalStatus.AI_SCHEDULED,
        ),
    )

    fun generateMockCitedReply(
        chapter: RagChapterContext,
        userQuestion: String,
        timestampLabel: String,
    ): TutorMessage {
        val q = userQuestion.lowercase()
        return when (chapter.id) {
            "rag_phys_gauss" -> when {
                "cavity" in q || "superposition" in q -> TutorMessage(
                    id = "reply_${System.nanoTime()}",
                    isUser = false,
                    text = "Treat the hollow cavity as the superposition of a full solid sphere (+ρ) and a smaller sphere of opposite density (−ρ) centered at the cavity offset d. Every radial position term r cancels cleanly, proving the electric field inside the cavity is uniform and completely independent of cavity radius.",
                    timestampLabel = timestampLabel,
                    citations = listOf(
                        TutorCitation("p. 28", "§2.5 Cavity Superposition"),
                        TutorCitation("p. 30", "Worked Ex. 2.11"),
                    ),
                    formulaBlock = "E_cavity = (ρ · r₁)/3ε₀ − (ρ · r₂)/3ε₀ = (ρ · d) / (3ε₀)",
                )

                "trap" in q || "summarize" in q -> TutorMessage(
                    id = "reply_${System.nanoTime()}",
                    isUser = false,
                    text = "Top 3 pitfalls highlighted in your attached Gauss's Law PDF: (1) External charges never alter net flux Φ, yet they DO alter pointwise local E on the surface. (2) Inside a hollow shell, E = 0 implies constant V = kQ/R, not V = 0. (3) Surface charge density σ redistributes non-uniformly if an interior charge is placed off-center.",
                    timestampLabel = timestampLabel,
                    citations = listOf(
                        TutorCitation("p. 14", "§2.3 Spherical Shells"),
                        TutorCitation("p. 38", "Chapter Trap Digest"),
                    ),
                    formulaBlock = "Φ_net = Q_enclosed / ε₀   |   E_local = E_in + E_ext",
                )

                else -> TutorMessage(
                    id = "reply_${System.nanoTime()}",
                    isUser = false,
                    text = "Grounded in ${chapter.pdfFileName}: apply Gaussian symmetry only when E has constant magnitude and fixed angle across every patch of your chosen closed surface. Remember your prior chapter note—verify whether the sphere is conducting (surface charge only) or insulating (uniform volume density ρ) before integrating.",
                    timestampLabel = timestampLabel,
                    citations = listOf(
                        TutorCitation("p. 11", chapter.sectionTags.first()),
                        TutorCitation("p. 14", chapter.sectionTags[1]),
                    ),
                    formulaBlock = "∮ E · dA = Q_enclosed / ε₀",
                )
            }

            "rag_chem_kinetics" -> when {
                "catalyst" in q -> TutorMessage(
                    id = "reply_${System.nanoTime()}",
                    isUser = false,
                    text = "A positive catalyst opens an alternate pathway with a lower transition-state barrier. Because the transition peak drops by the exact same ΔEa from both reactant and product sides, forward and reverse activation energies fall equally—accelerating equilibrium attainment while leaving K_eq untouched.",
                    timestampLabel = timestampLabel,
                    citations = listOf(
                        TutorCitation("p. 24", "§4.4 Arrhenius Plots"),
                        TutorCitation("p. 29", "§4.6 Parallel Paths"),
                    ),
                    formulaBlock = "ΔEa(forward) = ΔEa(reverse)   ⇒   K_eq = k_f / k_b invariant",
                )

                else -> TutorMessage(
                    id = "reply_${System.nanoTime()}",
                    isUser = false,
                    text = "From ${chapter.pdfFileName}: check both the reaction order scaling law t₁/₂ ∝ [A]₀^(1−n) and the logarithm base on Arrhenius graphs. In base-10 log₁₀(k) plots, never drop the 2.303 factor when extracting Ea in Joules.",
                    timestampLabel = timestampLabel,
                    citations = listOf(
                        TutorCitation("p. 13", "§4.2 Half-Life Scaling"),
                        TutorCitation("p. 21", "§4.4 Arrhenius Plots"),
                    ),
                    formulaBlock = "t₁/₂ ∝ [A]₀^(1−n)   |   m[log₁₀ k] = −Ea / (2.303·R)",
                )
            }

            "rag_bio_dna" -> TutorMessage(
                id = "reply_${System.nanoTime()}",
                isUser = false,
                text = "Cited from ${chapter.pdfFileName}: DNA Polymerase III strictly polymerizes 5'→3' onto a free 3'-OH primer, while its proofreading exonuclease steps backward 3'→5' to excise mismatched nucleotides. For Meselson-Stahl generations (n ≥ 1), exactly 2 duplexes remain hybrid, giving (2 / 2ⁿ) × 100% intermediate band.",
                timestampLabel = timestampLabel,
                citations = listOf(
                    TutorCitation("p. 18", "§6.2 Meselson-Stahl"),
                    TutorCitation("p. 27", "§6.4 Replication Enzymes"),
                ),
                formulaBlock = "Synthesis: 5'→3'  |  Proofreading: 3'→5'  |  Hybrid % = (2 / 2ⁿ)·100",
            )

            else -> TutorMessage(
                id = "reply_${System.nanoTime()}",
                isUser = false,
                text = "Grounded in ${chapter.pdfFileName}: first test King's Property x → (a + b − x) whenever denominators stay symmetric under limit pairing. For Walli's reduction over [0, π/2], multiply by π/2 strictly when BOTH powers m and n are even.",
                timestampLabel = timestampLabel,
                citations = listOf(
                    TutorCitation("p. 9", "§7.1 King's Property"),
                    TutorCitation("p. 31", "§7.5 Walli's Reduction"),
                ),
                formulaBlock = "I = ∫[a..b] f(a + b − x) dx   |   Walli's (m,n even) ⇒ × (π/2)",
            )
        }
    }
}

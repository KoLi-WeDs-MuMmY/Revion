package com.example.model

import com.example.ui.theme.AccentMood

object Phase13MockData {

    const val SUBJECT_PHYSICS_ID = "rev_physics"
    const val SUBJECT_CHEMISTRY_ID = "rev_chemistry"
    const val SUBJECT_BIOLOGY_ID = "rev_biology"
    const val SUBJECT_COMPOUND_ID = "rev_compound"

    fun createRevisionSheets(): List<RevisionSubjectSheet> = listOf(
        RevisionSubjectSheet(
            id = SUBJECT_PHYSICS_ID,
            subjectName = "Physics",
            accentMood = AccentMood.CYAN,
            chapterTitle = "Electrostatics — Gauss's Law & Spherical Shells",
            sourceLogLabel = "Logged Yesterday · 19:40 (52 min)",
            estimatedMinutes = 7,
            diagramKind = DiagramKind.GAUSS_SPHERE,
            diagramTitle = "Conducting Spherical Shell & Gaussian Surface",
            diagramCaption = "Net outward electric flux depends strictly on enclosed charge; inside a uniform hollow conducting shell, E = 0 identically.",
            formulaCallouts = listOf(
                FormulaCallout(
                    title = "Integral Form of Gauss's Law",
                    expression = "∮ E · dA = Q_enclosed / ε₀",
                    note = "Valid for any closed surface regardless of external asymmetric charges.",
                ),
                FormulaCallout(
                    title = "Field Outside Uniform Shell (r ≥ R)",
                    expression = "E(r) = (1 / 4πε₀) · (Q / r²)",
                    note = "Behaves identically to a point charge Q concentrated at the center; drops sharply to 0 for r < R.",
                ),
            ),
            conceptCards = listOf(
                RevisionConceptCard(
                    id = "pc_1",
                    tag = "SYMMETRY RULE",
                    title = "Gaussian Surface Choice & Constant |E|",
                    summary = "Gauss's law is always true, yet it isolates E algebraically only when symmetry makes E uniform and parallel (or perpendicular) over each face of the surface.",
                    trapAlert = "Exam Trap: External charges change the local field E at points on the surface even while contributing zero net flux.",
                ),
                RevisionConceptCard(
                    id = "pc_2",
                    tag = "BOUNDARY JUMP",
                    title = "Discontinuity Across Charged Conductors",
                    summary = "Crossing a conducting shell surface shifts the normal electric field discontinuously by σ / ε₀ while the electric potential V remains continuous at r = R.",
                    trapAlert = "Exam Trap: Students often zero out V inside a shell — remember V inside is constant at kQ/R, NOT zero.",
                ),
            ),
        ),
        RevisionSubjectSheet(
            id = SUBJECT_CHEMISTRY_ID,
            subjectName = "Chemistry",
            accentMood = AccentMood.VIOLET,
            chapterTitle = "Chemical Kinetics — Rate Order & Arrhenius Barrier",
            sourceLogLabel = "Logged Yesterday · 14:20 (36 min)",
            estimatedMinutes = 6,
            diagramKind = DiagramKind.ARRHENIUS_CURVE,
            diagramTitle = "Arrhenius Activation Energy & Catalyst Path",
            diagramCaption = "A positive catalyst opens an alternate pathway with lower Ea, accelerating both forward and reverse rates equally without altering ΔH.",
            formulaCallouts = listOf(
                FormulaCallout(
                    title = "Arrhenius Equation & Linear Slope",
                    expression = "ln(k) = ln(A) − (Ea / R) · (1 / T)",
                    note = "Plotting ln(k) vs. 1/T yields a straight line with slope = −Ea / R; log₁₀(k) divides slope by 2.303.",
                ),
                FormulaCallout(
                    title = "First-Order Half-Life Invariance",
                    expression = "t₁/₂ = ln(2) / k ≈ 0.693 / k",
                    note = "Completely independent of initial reactant concentration [A]₀.",
                ),
            ),
            conceptCards = listOf(
                RevisionConceptCard(
                    id = "cc_1",
                    tag = "SLOPE CONVERSION",
                    title = "Natural Log vs. Base-10 Factor Trap",
                    summary = "Rate constant ratios across two temperatures T₁ and T₂ map activation energy directly, but mixing R in J·mol⁻¹·K⁻¹ with Ea in kJ·mol⁻¹ creates 1000× errors.",
                    trapAlert = "Exam Trap: Always verify whether the graph y-axis is ln(k) or log₁₀(k) before multiplying by 2.303.",
                ),
                RevisionConceptCard(
                    id = "cc_2",
                    tag = "CATALYSIS",
                    title = "Equilibrium Position vs. Rate Velocity",
                    summary = "Catalysts lower the transition-state peak height equally from both reactant and product valleys, shortening time-to-equilibrium while keeping K_eq unchanged.",
                    trapAlert = "Exam Trap: Catalysts never make a thermodynamically non-spontaneous reaction (ΔG > 0) spontaneous.",
                ),
            ),
        ),
        RevisionSubjectSheet(
            id = SUBJECT_BIOLOGY_ID,
            subjectName = "Biology",
            accentMood = AccentMood.EMERALD,
            chapterTitle = "Molecular Inheritance — DNA Replication Fork",
            sourceLogLabel = "Logged Yesterday · 17:15 (44 min)",
            estimatedMinutes = 5,
            diagramKind = DiagramKind.DNA_REPLICATION,
            diagramTitle = "Semi-Conservative DNA Replication Fork",
            diagramCaption = "DNA Polymerase III synthesizes exclusively in the 5'→3' direction, forcing continuous leading growth and discontinuous Okazaki lagging fragments.",
            formulaCallouts = listOf(
                FormulaCallout(
                    title = "Template Polarity & Polymerization Rule",
                    expression = "Template 3' → 5'  ⇒  New Strand 5' → 3'",
                    note = "Incoming dNTPs attach strictly to the free 3'-OH group via phosphodiester bond formation.",
                ),
                FormulaCallout(
                    title = "Meselson-Stahl Hybrid Ratio (Gen 2)",
                    expression = "Gen 1: 100% ¹⁵N/¹⁴N  |  Gen 2: 50% Hybrid : 50% Light",
                    note = "Directly rules out conservative replication in E. coli CsCl gradient experiments.",
                ),
            ),
            conceptCards = listOf(
                RevisionConceptCard(
                    id = "bc_1",
                    tag = "ENZYME SEQUENCE",
                    title = "Helicase, Primase, Pol III & Ligase Handoff",
                    summary = "Helicase unzips H-bonds at Ori, RNA Primase lays a short primer with a 3'-OH start, Pol III extends rapidly, Pol I replaces RNA primers, and DNA Ligase seals final nicks.",
                    trapAlert = "Exam Trap: DNA Polymerase cannot initiate de novo synthesis — it strictly requires an existing RNA primer.",
                ),
                RevisionConceptCard(
                    id = "bc_2",
                    tag = "PROOFREADING",
                    title = "3'→5' Exonuclease Error Correction",
                    summary = "While polymerization runs 5'→3', backtracking error excision runs in reverse (3'→5'), cutting replication mismatch frequency below 1 in 10⁹ base pairs.",
                    trapAlert = "Exam Trap: Don't confuse polymerization direction (5'→3') with proofreading exonuclease direction (3'→5').",
                ),
            ),
        ),
    )

    fun createCompoundMixSheet(): RevisionSubjectSheet = RevisionSubjectSheet(
        id = SUBJECT_COMPOUND_ID,
        subjectName = "Compound Mix",
        accentMood = AccentMood.AMBER,
        chapterTitle = "Cross-Subject High-Yield Synthesis (Yesterday's Log)",
        sourceLogLabel = "Blended from Physics + Chemistry + Biology",
        estimatedMinutes = 11,
        diagramKind = DiagramKind.COMPOUND_MAP,
        diagramTitle = "Yesterday's Cross-Subject Retention Loop",
        diagramCaption = "Unifies the three highest-risk conceptual bottlenecks logged yesterday into one rapid visual scan before testing.",
        formulaCallouts = listOf(
            FormulaCallout(
                title = "Physics · Shell Interior Check",
                expression = "r < R ⇒ E = 0  |  V = kQ / R (Constant)",
                note = "Zero electric field implies constant potential, not zero potential.",
            ),
            FormulaCallout(
                title = "Chemistry · Arrhenius Barrier",
                expression = "Slope[log₁₀(k) vs 1/T] = −Ea / (2.303 · R)",
                note = "Watch the 2.303 base-10 conversion and kJ ↔ J unit scaling.",
            ),
            FormulaCallout(
                title = "Biology · Fork Directionality",
                expression = "Polymerization: 5' → 3'  |  Proofreading: 3' → 5'",
                note = "Lagging strand forms Okazaki fragments joined by DNA Ligase.",
            ),
        ),
        conceptCards = listOf(
            RevisionConceptCard(
                id = "cm_1",
                tag = "PHYSICS · FLUX",
                title = "External Charge Zero-Net-Flux Rule",
                summary = "Any charge placed outside a closed Gaussian envelope sends equal field lines entering and exiting, yielding zero net flux contribution.",
                trapAlert = "High-Yield Check: Local E on the surface is still affected by external charges.",
            ),
            RevisionConceptCard(
                id = "cm_2",
                tag = "CHEMISTRY · KINETICS",
                title = "Catalyst Invariance of ΔH and K_eq",
                summary = "Catalysts reduce forward and backward activation energies by the exact same ΔEa margin, leaving equilibrium constants unchanged.",
                trapAlert = "High-Yield Check: First-order t₁/₂ never depends on starting concentration.",
            ),
            RevisionConceptCard(
                id = "cm_3",
                tag = "BIOLOGY · REPLICATION",
                title = "Primer Dependency & Okazaki Ligation",
                summary = "Both leading and lagging strands require RNA primers from Primase; lagging fragments require repeated priming and DNA Ligase sealing.",
                trapAlert = "High-Yield Check: Template polarity read by Pol III is always 3'→5'.",
            ),
        ),
    )

    fun createQuizQuestions(): List<QuizQuestion> = listOf(
        // Physics (4)
        QuizQuestion(
            id = "q_phys_1",
            subjectId = SUBJECT_PHYSICS_ID,
            subjectName = "Physics",
            accentMood = AccentMood.CYAN,
            subtopicTag = "Gauss's Law — Spherical Shell",
            prompt = "A thin hollow conducting sphere of radius R carries total charge +Q. What are the electric field E and potential V at distance r = R/2 from its center?",
            options = listOf(
                "E = 0 and V = 0",
                "E = 0 and V = kQ / R",
                "E = kQ / R² and V = kQ / R",
                "E = 4kQ / R² and V = 2kQ / R",
            ),
            correctOptionIndex = 1,
            explanation = "No charge is enclosed inside r < R, so E = 0 everywhere in the cavity. Because E = −dV/dr = 0, potential remains constant at its surface value V = kQ / R.",
        ),
        QuizQuestion(
            id = "q_phys_2",
            subjectId = SUBJECT_PHYSICS_ID,
            subjectName = "Physics",
            accentMood = AccentMood.CYAN,
            subtopicTag = "Gauss's Law — External Charges",
            prompt = "A point charge +q sits outside a closed arbitrary Gaussian surface containing charge −2q inside. How does bringing +q closer affect the surface?",
            options = listOf(
                "Both net flux and local electric field E stay unchanged",
                "Net flux increases while local electric field E stays constant",
                "Net flux stays −2q/ε₀ while local electric field E changes",
                "Net flux drops to −q/ε₀ because total system charge is −q",
            ),
            correctOptionIndex = 2,
            explanation = "Gauss's law guarantees net flux depends strictly on enclosed charge (−2q/ε₀), whereas the local field E at any surface point is the vector sum of all interior and exterior charges.",
        ),
        QuizQuestion(
            id = "q_phys_3",
            subjectId = SUBJECT_PHYSICS_ID,
            subjectName = "Physics",
            accentMood = AccentMood.CYAN,
            subtopicTag = "Gauss's Law — Spherical Shell",
            prompt = "If the radius of a Gaussian sphere enclosing a single point charge +Q is doubled from r to 2r, the total outward electric flux through the sphere:",
            options = listOf(
                "Becomes one-fourth of the original flux",
                "Doubles because surface area expands",
                "Remains strictly unchanged at Q / ε₀",
                "Drops by half due to inverse-square decay",
            ),
            correctOptionIndex = 2,
            explanation = "Area scales by 4× while E scales by 1/4×, so their product Φ = E·A stays invariant at Q / ε₀ for any enclosing radius.",
        ),
        QuizQuestion(
            id = "q_phys_4",
            subjectId = SUBJECT_PHYSICS_ID,
            subjectName = "Physics",
            accentMood = AccentMood.CYAN,
            subtopicTag = "Electrostatics — Boundary Discontinuity",
            prompt = "Across an infinite conducting surface carrying surface charge density σ, the normal component of electric field experiences a jump of magnitude:",
            options = listOf(
                "σ / (2ε₀)",
                "σ / ε₀",
                "2σ / ε₀",
                "Zero, because electric field is always continuous",
            ),
            correctOptionIndex = 1,
            explanation = "Applying a thin Gaussian pillbox across the conductor-vacuum boundary (with E = 0 inside the conductor) yields E_out = σ / ε₀.",
        ),

        // Chemistry (4)
        QuizQuestion(
            id = "q_chem_1",
            subjectId = SUBJECT_CHEMISTRY_ID,
            subjectName = "Chemistry",
            accentMood = AccentMood.VIOLET,
            subtopicTag = "Arrhenius — Activation Energy Slope",
            prompt = "In a plot of log₁₀(k) against 1/T for an elementary reaction, the straight line has a slope of −2000 K. Which expression gives the activation energy Ea?",
            options = listOf(
                "2000 × R",
                "2000 × 2.303 × R",
                "2000 / (2.303 × R)",
                "−2000 × R / 2.303",
            ),
            correctOptionIndex = 1,
            explanation = "Since log₁₀(k) = log₁₀(A) − (Ea / 2.303R)(1/T), equating −Ea/(2.303R) = −2000 K yields Ea = 2000 × 2.303 × R.",
        ),
        QuizQuestion(
            id = "q_chem_2",
            subjectId = SUBJECT_CHEMISTRY_ID,
            subjectName = "Chemistry",
            accentMood = AccentMood.VIOLET,
            subtopicTag = "Kinetics — Catalyst Pathway",
            prompt = "Adding a positive catalyst to a reversible exothermic reaction alters which of the following quantities?",
            options = listOf(
                "Equilibrium constant K_eq and enthalpy ΔH",
                "Forward activation energy only",
                "Both forward and reverse activation energies equally",
                "Gibbs free energy change ΔG of the products",
            ),
            correctOptionIndex = 2,
            explanation = "A catalyst lowers the transition-state barrier equally from both sides, speeding forward and reverse rates identically without shifting ΔH, ΔG, or K_eq.",
        ),
        QuizQuestion(
            id = "q_chem_3",
            subjectId = SUBJECT_CHEMISTRY_ID,
            subjectName = "Chemistry",
            accentMood = AccentMood.VIOLET,
            subtopicTag = "Kinetics — First-Order Half-Life",
            prompt = "A first-order decomposition has a half-life of 20 minutes at 0.8 M initial concentration. If the starting concentration is halved to 0.4 M, the new half-life is:",
            options = listOf(
                "10 minutes",
                "20 minutes",
                "40 minutes",
                "28.3 minutes",
            ),
            correctOptionIndex = 1,
            explanation = "For any first-order reaction, t₁/₂ = 0.693 / k is strictly independent of initial concentration.",
        ),
        QuizQuestion(
            id = "q_chem_4",
            subjectId = SUBJECT_CHEMISTRY_ID,
            subjectName = "Chemistry",
            accentMood = AccentMood.VIOLET,
            subtopicTag = "Arrhenius — Activation Energy Slope",
            prompt = "If the activation energy Ea of a reaction approaches zero, how does its rate constant k behave as temperature T rises?",
            options = listOf(
                "k drops rapidly toward zero",
                "k becomes practically independent of T and approaches A",
                "k increases exponentially without bound",
                "k becomes equal to R × T",
            ),
            correctOptionIndex = 1,
            explanation = "In k = A·e^(−Ea/RT), setting Ea → 0 makes e⁰ = 1, so k ≈ A regardless of temperature.",
        ),

        // Biology (4)
        QuizQuestion(
            id = "q_bio_1",
            subjectId = SUBJECT_BIOLOGY_ID,
            subjectName = "Biology",
            accentMood = AccentMood.EMERALD,
            subtopicTag = "DNA Replication — Lagging Strand Polarity",
            prompt = "Why is the lagging strand synthesized discontinuously as Okazaki fragments during DNA replication?",
            options = listOf(
                "DNA Helicase only unwinds one parent strand at a time",
                "DNA Polymerase III can add nucleotides strictly in the 5'→3' direction",
                "DNA Ligase blocks continuous synthesis on the 5'→3' template",
                "RNA Primase cannot bind to the leading template strand",
            ),
            correctOptionIndex = 1,
            explanation = "Because DNA strands are antiparallel and Pol III exclusively adds dNTPs to a free 3'-OH (5'→3' synthesis), the strand oriented away from fork opening must be built in short back-stitched fragments.",
        ),
        QuizQuestion(
            id = "q_bio_2",
            subjectId = SUBJECT_BIOLOGY_ID,
            subjectName = "Biology",
            accentMood = AccentMood.EMERALD,
            subtopicTag = "DNA Replication — Enzyme Sequence",
            prompt = "Which enzyme catalyzes the final phosphodiester bond sealing the nick between adjacent Okazaki fragments after RNA primers are replaced?",
            options = listOf(
                "DNA Helicase",
                "RNA Primase",
                "DNA Ligase",
                "Topoisomerase II",
            ),
            correctOptionIndex = 2,
            explanation = "After Pol I removes RNA primers and fills the gap with DNA nucleotides, DNA Ligase seals the remaining sugar-phosphate backbone nick.",
        ),
        QuizQuestion(
            id = "q_bio_3",
            subjectId = SUBJECT_BIOLOGY_ID,
            subjectName = "Biology",
            accentMood = AccentMood.EMERALD,
            subtopicTag = "DNA Replication — Proofreading Direction",
            prompt = "During prokaryotic DNA replication, what are the polymerization and proofreading exonuclease directions of DNA Polymerase III respectively?",
            options = listOf(
                "5'→3' polymerization and 3'→5' proofreading",
                "3'→5' polymerization and 5'→3' proofreading",
                "Both proceed strictly in the 5'→3' direction",
                "Both proceed strictly in the 3'→5' direction",
            ),
            correctOptionIndex = 0,
            explanation = "Pol III extends new DNA 5'→3' and backtracks 3'→5' with its exonuclease activity to excise mispaired bases.",
        ),
        QuizQuestion(
            id = "q_bio_4",
            subjectId = SUBJECT_BIOLOGY_ID,
            subjectName = "Biology",
            accentMood = AccentMood.EMERALD,
            subtopicTag = "Molecular Inheritance — Meselson-Stahl",
            prompt = "In the Meselson-Stahl experiment, after two generations of ¹⁵N-labeled E. coli growing in ¹⁴N medium, what DNA density bands appear?",
            options = listOf(
                "100% intermediate hybrid density",
                "50% heavy (¹⁵N/¹⁵N) and 50% light (¹⁴N/¹⁴N)",
                "50% hybrid (¹⁵N/¹⁴N) and 50% light (¹⁴N/¹⁴N)",
                "25% hybrid and 75% light",
            ),
            correctOptionIndex = 2,
            explanation = "Generation 1 yields 100% hybrid duplexes; Generation 2 splits each hybrid strand with a new light ¹⁴N strand, producing exactly 50% hybrid and 50% light.",
        ),
    )

    fun questionIdsForSubject(allQuestions: List<QuizQuestion>, subjectId: String): List<String> {
        return if (subjectId == SUBJECT_COMPOUND_ID) {
            listOf(
                SUBJECT_PHYSICS_ID,
                SUBJECT_CHEMISTRY_ID,
                SUBJECT_BIOLOGY_ID,
            ).flatMap { subId ->
                allQuestions.filter { it.subjectId == subId }.take(2).map { it.id }
            }
        } else {
            allQuestions.filter { it.subjectId == subjectId }.map { it.id }
        }
    }
}

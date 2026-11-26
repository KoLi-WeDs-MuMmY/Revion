package com.example.model

import androidx.compose.runtime.Immutable
import com.example.ui.theme.AccentMood

enum class FlashcardHubMode {
    FLASHCARD_DECK,
    MISTAKE_BANK,
}

enum class SpacedIntervalBucket(
    val title: String,
    val shortBadge: String,
    val cadenceDescription: String,
) {
    TODAY_X1("Today (x-1)", "x-1", "24h fresh consolidation"),
    RECALL_X3("3-Day Recall (x-3)", "x-3", "72h retention checkpoint"),
    WEEKEND_HARD("Weekend Hard Parts", "WKND", "High-friction trap drills"),
    COMPOUND_MIX("Compound Mix", "MIX", "Interleaved multi-interval deck"),
}

enum class FlashcardRating(
    val label: String,
    val nextInterval: String,
) {
    AGAIN("Again", "<1m"),
    HARD("Hard", "x-1"),
    GOOD("Good", "x-3"),
    EASY("Easy", "7d"),
}

@Immutable
data class SpacedFlashcard(
    val id: String,
    val bucket: SpacedIntervalBucket,
    val subjectName: String,
    val accentMood: AccentMood,
    val chapterTag: String,
    val intervalLabel: String,
    val frontPrompt: String,
    val backAnswer: String,
    val formulaOrHint: String,
    val trapNote: String,
)

enum class MistakeCategory(
    val label: String,
    val badgeCode: String,
) {
    CONCEPT_GAP("Concept Gap", "CONCEPT"),
    CALCULATION_ERROR("Calculation Error", "CALC"),
    SILLY_SLIP("Silly / Reading Slip", "SLIP"),
}

@Immutable
data class MistakeRecord(
    val id: String,
    val category: MistakeCategory,
    val subjectName: String,
    val accentMood: AccentMood,
    val chapterTitle: String,
    val sourceLabel: String,
    val mistakeCount: Int,
    val questionSummary: String,
    val wrongPick: String,
    val wrongReason: String,
    val correctConcept: String,
    val formulaAnchor: String,
    val addedToWeekendDeck: Boolean = false,
)

@Immutable
data class FlashcardsMistakesUiState(
    val activeMode: FlashcardHubMode = FlashcardHubMode.FLASHCARD_DECK,
    // Spaced Flashcards state
    val allCards: List<SpacedFlashcard>,
    val activeBucket: SpacedIntervalBucket = SpacedIntervalBucket.TODAY_X1,
    val selectedCardSubject: String? = null,
    val completedCardIds: Set<String> = emptySet(),
    val requeuedCardIds: List<String> = emptyList(),
    val isCardFlipped: Boolean = false,
    val goodOrEasyCount: Int = 0,
    val hardOrAgainCount: Int = 0,
    val deckNonce: Int = 0,
    // Mistake Bank state
    val mistakes: List<MistakeRecord>,
    val selectedMistakeCategory: MistakeCategory? = null,
    val selectedMistakeSubject: String? = null,
    val selectedMistakeId: String? = null,
) {
    fun cardsInBucket(bucket: SpacedIntervalBucket, subjectFilter: String? = selectedCardSubject): List<SpacedFlashcard> {
        return allCards.filter { card ->
            val matchBucket = bucket == SpacedIntervalBucket.COMPOUND_MIX || card.bucket == bucket
            val matchSubject = subjectFilter == null || card.subjectName == subjectFilter
            matchBucket && matchSubject
        }
    }

    val remainingDeckQueue: List<SpacedFlashcard>
        get() {
            val base = cardsInBucket(activeBucket, selectedCardSubject)
            val uncompleted = base.filter { it.id !in completedCardIds }
            // Keep cards marked "Again" at the back of the uncompleted queue
            val normal = uncompleted.filter { it.id !in requeuedCardIds }
            val requeued = requeuedCardIds.mapNotNull { id -> uncompleted.firstOrNull { it.id == id } }
            return normal + requeued
        }

    val currentCard: SpacedFlashcard?
        get() = remainingDeckQueue.firstOrNull()

    val nextPeekCard: SpacedFlashcard?
        get() = remainingDeckQueue.getOrNull(1)

    val totalRatedCount: Int
        get() = goodOrEasyCount + hardOrAgainCount

    val sessionRecallPercent: Int
        get() = if (totalRatedCount == 0) {
            88
        } else {
            ((goodOrEasyCount.toFloat() / totalRatedCount.toFloat()) * 100f).toInt()
        }

    val filteredMistakes: List<MistakeRecord>
        get() = mistakes.filter { item ->
            val matchCat = selectedMistakeCategory == null || item.category == selectedMistakeCategory
            val matchSubj = selectedMistakeSubject == null || item.subjectName == selectedMistakeSubject
            matchCat && matchSubj
        }

    val selectedMistake: MistakeRecord?
        get() = filteredMistakes.firstOrNull { it.id == selectedMistakeId }
            ?: filteredMistakes.firstOrNull()
}

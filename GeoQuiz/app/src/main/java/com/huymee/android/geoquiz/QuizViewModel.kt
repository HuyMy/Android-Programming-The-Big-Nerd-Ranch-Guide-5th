package com.huymee.android.geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"
const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val ANSWERED_COUNT_KEY = "ANSWERED_COUNT_KEY"
const val CORRECT_COUNT_KEY = "CORRECT_COUNT_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    private var currentIndex
        get() = savedStateHandle[CURRENT_INDEX_KEY] ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    var answeredCount
        get() = savedStateHandle[ANSWERED_COUNT_KEY] ?: 0
        set(value) = savedStateHandle.set(ANSWERED_COUNT_KEY, value)

    var correctCount
        get() = savedStateHandle[CORRECT_COUNT_KEY] ?: 0
        set(value) = savedStateHandle.set(CORRECT_COUNT_KEY, value)

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    var isCurrentQuestionAnswered: Boolean
        get() = questionBank[currentIndex].isAnswered
        set(value) {
            questionBank[currentIndex].isAnswered = value
        }

    val isCompleted: Boolean
        get() = answeredCount == questionBank.size

    val score: Int
        get() = correctCount * 100 / answeredCount

    fun changeQuestion(diff: Int) {
        currentIndex = (currentIndex + questionBank.size + diff) % questionBank.size
    }

}
package com.huymee.android.geoquiz

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuizViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var quizViewModel: QuizViewModel

    @Before
    fun setUp() {
        savedStateHandle = SavedStateHandle()
        quizViewModel = QuizViewModel(savedStateHandle)
    }

    @Test
    fun providesExpectedQuestionText() {
        assertEquals(R.string.question_australia, quizViewModel.currentQuestionText)
    }

    @Test
    fun wrapsAroundQuestionBank() {
        savedStateHandle = SavedStateHandle(mapOf(CURRENT_INDEX_KEY to 5))
        quizViewModel = QuizViewModel(savedStateHandle)
        assertEquals(R.string.question_asia, quizViewModel.currentQuestionText)
        quizViewModel.changeQuestion(1)
        assertEquals(R.string.question_australia, quizViewModel.currentQuestionText)
    }

    @Test
    fun testFirstQuestionAnswer() {
        assertTrue(quizViewModel.currentQuestionAnswer)
    }

    @Test
    fun testWhenComplete() {
        quizViewModel.correctCount = 6
        quizViewModel.answeredCount = 6
        assertTrue(quizViewModel.isCompleted)
        assertEquals(100, quizViewModel.score)
    }

    @Test
    fun testIsCurrentAnswer() {
        assertFalse(quizViewModel.isCurrentQuestionAnswered)
        quizViewModel.isCurrentQuestionAnswered = true
        assertTrue(quizViewModel.isCurrentQuestionAnswered)
    }

}
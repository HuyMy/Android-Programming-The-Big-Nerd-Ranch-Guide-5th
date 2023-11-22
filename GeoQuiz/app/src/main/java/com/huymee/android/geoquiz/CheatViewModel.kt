package com.huymee.android.geoquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

const val ANSWER_IS_SHOWN_KEY = "ANSWER_IS_SHOWN_KEY"

class CheatViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    var answerIsTrue = false

    var answerIsShown
        get() = savedStateHandle[ANSWER_IS_SHOWN_KEY] ?: false
        set(value) = savedStateHandle.set(ANSWER_IS_SHOWN_KEY, value)

}
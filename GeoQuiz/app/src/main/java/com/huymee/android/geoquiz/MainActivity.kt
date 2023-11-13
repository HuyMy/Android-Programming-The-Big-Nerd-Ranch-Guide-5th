package com.huymee.android.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.huymee.android.geoquiz.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.questionTextView.setOnClickListener {
            changeQuestion(1)
        }

        binding.trueButton.setOnClickListener {
            checkAnswer(true)
        }

        binding.falseButton.setOnClickListener {
            checkAnswer(false)
        }

        binding.prevButton.setOnClickListener {
            changeQuestion(-1)
        }

        binding.nextButton.setOnClickListener {
            changeQuestion(1)
        }

        updateQuestion()
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
        val isQuestionAnswered = quizViewModel.isCurrentQuestionAnswered
        binding.falseButton.isEnabled = !isQuestionAnswered
        binding.trueButton.isEnabled = !isQuestionAnswered
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = if (correctAnswer == userAnswer) {
            quizViewModel.correctCount++
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        quizViewModel.isCurrentQuestionAnswered = true
        binding.falseButton.isEnabled = false
        binding.trueButton.isEnabled = false
        quizViewModel.answeredCount++
        if (quizViewModel.isCompleted) {
            val resultText = getString(R.string.graded_notice, quizViewModel.score)
            Toast.makeText(this, resultText, Toast.LENGTH_LONG).show()
        }
    }

    private fun changeQuestion(diff: Int) {
        quizViewModel.changeQuestion(diff)
        updateQuestion()
    }
}

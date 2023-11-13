package com.huymee.android.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.huymee.android.geoquiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    private var currentIndex = 0
    private var answeredCount = 0
    private var correctCount = 0

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
        val questionTextResId = questionBank[currentIndex].textResId
        binding.questionTextView.setText(questionTextResId)
        val isQuestionAnswered = questionBank[currentIndex].isAnswered
        binding.falseButton.isEnabled = !isQuestionAnswered
        binding.trueButton.isEnabled = !isQuestionAnswered
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = questionBank[currentIndex].answer

        val messageResId = if (correctAnswer == userAnswer) {
            ++correctCount
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        questionBank[currentIndex].isAnswered = true
        binding.falseButton.isEnabled = false
        binding.trueButton.isEnabled = false
        ++answeredCount
        if (answeredCount == questionBank.size) {
            val resultText = getString(R.string.graded_notice, correctCount * 100 / answeredCount)
            Toast.makeText(this, resultText, Toast.LENGTH_LONG).show()
        }
    }

    private fun changeQuestion(diff: Int) {
        currentIndex = (currentIndex + questionBank.size + diff) % questionBank.size
        updateQuestion()
    }
}

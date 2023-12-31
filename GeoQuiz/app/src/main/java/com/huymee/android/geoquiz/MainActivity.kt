package com.huymee.android.geoquiz

import android.app.Activity
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import com.huymee.android.geoquiz.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (!quizViewModel.isCurrentQuestionCheated) {
                quizViewModel.isCurrentQuestionCheated =
                    result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            }

            if (!quizViewModel.isCurrentQuestionAnswered && quizViewModel.isCurrentQuestionCheated) {
                quizViewModel.cheatRemain--
                updateCheatButtonAndText()
                showRespond(R.string.judgment_toast)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            questionTextView.setOnClickListener { changeQuestion(1) }
            trueButton.setOnClickListener { checkAnswer(true) }
            falseButton.setOnClickListener { checkAnswer(false) }
            prevButton.setOnClickListener { changeQuestion(-1) }
            nextButton.setOnClickListener { changeQuestion(1) }
            cheatButton.setOnClickListener {
                val answerIsTrue = quizViewModel.currentQuestionAnswer
                val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
                cheatLauncher.launch(intent)
            }
            androidVersionTextView.text =
                getString(R.string.android_version_text, Build.VERSION.SDK_INT)
        }
        updateCheatButtonAndText()
        updateQuestion()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurCheatButton()
        }
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        val buttonEnabled =
            !quizViewModel.isCurrentQuestionAnswered && !quizViewModel.isCurrentQuestionCheated

        binding.apply {
            questionTextView.setText(questionTextResId)
            falseButton.isEnabled = buttonEnabled
            trueButton.isEnabled = buttonEnabled
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = if (userAnswer == correctAnswer) {
            quizViewModel.correctCount++
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

        showRespond(messageResId)
    }

    private fun markQuestionAnswered() {
        binding.falseButton.isEnabled = false
        binding.trueButton.isEnabled = false
        quizViewModel.isCurrentQuestionAnswered = true
        quizViewModel.answeredCount++
    }

    private fun changeQuestion(diff: Int) {
        quizViewModel.changeQuestion(diff)
        updateQuestion()
    }

    private fun showRespond(@StringRes messageResId: Int) {
        markQuestionAnswered()
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        if (quizViewModel.isCompleted) {
            val resultText = getString(R.string.graded_notice, quizViewModel.score)
            Toast.makeText(this, resultText, Toast.LENGTH_LONG).show()
        }
    }

    private fun updateCheatButtonAndText() {
        if (quizViewModel.isMaxCheatReached()) {
            binding.cheatButton.isEnabled = false
        }
        binding.cheatRemainCountText.text = resources.getQuantityString(
            R.plurals.cheat_remaining,
            quizViewModel.cheatRemain,
            quizViewModel.cheatRemain
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun blurCheatButton() {
        val effect = RenderEffect.createBlurEffect(
            10.0f,
            10.0f,
            Shader.TileMode.CLAMP
        )
        binding.cheatButton.setRenderEffect(effect)
    }
}

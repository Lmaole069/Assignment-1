package com.example.gymnasticapp2

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "GymnasticsApp"
        private const val KEY_SCORE = "score"
        private const val KEY_COMPLETED = "completed"
        private const val KEY_DEDUCTED = "deducted"
    }

    private val totalElements = 10
    private val minScore = 0
    private val maxScore = 20

    private var score = 0
    private var completed = 0
    private var deducted = false

    private lateinit var scoreText: TextView
    private lateinit var elementText: TextView
    private lateinit var statusText: TextView
    private lateinit var performButton: Button
    private lateinit var deductionButton: Button
    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreText = findViewById(R.id.scoreText)
        elementText = findViewById(R.id.elementText)
        statusText = findViewById(R.id.statusText)
        performButton = findViewById(R.id.performButton)
        deductionButton = findViewById(R.id.deductionButton)
        resetButton = findViewById(R.id.resetButton)

        // Restore state after rotation
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(KEY_SCORE, 0)
            completed = savedInstanceState.getInt(KEY_COMPLETED, 0)
            deducted = savedInstanceState.getBoolean(KEY_DEDUCTED, false)
            Log.d(TAG, "State restored: score=$score, completed=$completed, deducted=$deducted")
        } else {
            Log.d(TAG, "Fresh start")
        }

        performButton.setOnClickListener { onPerform() }
        deductionButton.setOnClickListener { onDeduction() }
        resetButton.setOnClickListener { onReset() }

        updateUi()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SCORE, score)
        outState.putInt(KEY_COMPLETED, completed)
        outState.putBoolean(KEY_DEDUCTED, deducted)
        Log.d(TAG, "State saved: score=$score, completed=$completed, deducted=$deducted")
    }

    private fun onPerform() {
        Log.d(TAG, "Perform clicked")
        if (deducted || completed >= totalElements) {
            Log.w(TAG, "Perform blocked (deducted=$deducted, completed=$completed)")
            return
        }
        val element = completed + 1
        val points = pointsFor(element)
        val old = score
        score = (score + points).coerceIn(minScore, maxScore)
        completed = element
        Log.i(TAG, "Element $element performed: +$points ($old -> $score)")
        if (completed == totalElements) {
            Log.i(TAG, "Routine complete. Final score=$score")
        }
        updateUi()
    }

    private fun onDeduction() {
        Log.d(TAG, "Deduction clicked")
        if (completed < 1 || deducted || completed >= totalElements) {
            Log.w(TAG, "Deduction blocked (deducted=$deducted, completed=$completed)")
            return
        }
        val old = score
        score = (score - 2).coerceIn(minScore, maxScore)
        deducted = true
        Log.i(TAG, "Deduction taken: $old -> $score")
        updateUi()
    }

    private fun onReset() {
        Log.d(TAG, "Reset clicked (score was $score)")
        score = 0
        completed = 0
        deducted = false
        updateUi()
    }
    private fun pointsFor(element: Int): Int = when (element) {
        in 1..3 -> 1
        in 4..7 -> 2
        else -> 3
    }
    @SuppressLint("StringFormatInvalid")
    private fun updateUi() {
        val currentElement = minOf(completed + 1, totalElements)
        scoreText.text = getString(R.string.score_format, score)
        elementText.text = getString(R.string.element_format, currentElement, totalElements)

        val colorRes = when (currentElement) {
            in 1..3 -> R.color.zone_basic
            in 4..7 -> R.color.zone_intermediate
            else -> R.color.zone_advanced
        }
        scoreText.setTextColor(getColor(colorRes))

        val routineComplete = completed >= totalElements
        statusText.text = when {
            routineComplete -> getString(R.string.routine_complete)
            deducted -> getString(R.string.deduction_taken)
            else -> ""
        }

        performButton.isEnabled = !deducted && !routineComplete
        deductionButton.isEnabled = completed >= 1 && !deducted && !routineComplete
        resetButton.isEnabled = true

        Log.d(TAG, "UI updated: score=$score, element=$currentElement, completed=$completed, deducted=$deducted")
    }
}
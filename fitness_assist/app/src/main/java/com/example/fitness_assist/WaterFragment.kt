package com.example.fitness_assist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class WaterFragment : Fragment() {
    private var waterCount = 0
    private var dailyGoal = 8

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_water, container, false)

        // Загружаем сохранённые значения через утилиту
        waterCount = UserPrefsUtils.getInt(requireContext(), "water_count", 0)
        dailyGoal = UserPrefsUtils.getInt(requireContext(), "water_goal", 8)

        val waterTextView = view.findViewById<TextView>(R.id.tv_water_count)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val addBtn = view.findViewById<Button>(R.id.btn_add_water)
        val resetBtn = view.findViewById<Button>(R.id.btn_reset_water)

        updateUI(waterTextView, progressBar)

        addBtn.setOnClickListener {
            if (waterCount < dailyGoal) {
                waterCount++
                // Сохраняем через утилиту
                UserPrefsUtils.putInt(requireContext(), "water_count", waterCount)
                updateUI(waterTextView, progressBar)

                if (waterCount == dailyGoal) {
                    Toast.makeText(
                        context,
                        "🎉 Поздравляем! Вы достигли дневной нормы!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    "Вы уже достигли дневной нормы!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        resetBtn.setOnClickListener {
            waterCount = 0
            UserPrefsUtils.putInt(requireContext(), "water_count", waterCount)
            updateUI(waterTextView, progressBar)
            Toast.makeText(
                context,
                "Счётчик сброшен",
                Toast.LENGTH_SHORT
            ).show()
        }

        return view
    }

    private fun updateUI(textView: TextView, progressBar: ProgressBar) {
        textView.text = "$waterCount/$dailyGoal стаканов"
        val progress = if (dailyGoal > 0) {
            (waterCount.toFloat() / dailyGoal * 100).toInt()
        } else {
            0
        }

        progressBar.progress = progress

        // Меняем цвет прогресс-бара в зависимости от заполненности
        if (progress >= 100) {
            progressBar.progressTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#4CAF50")
            )
        } else if (progress >= 50) {
            progressBar.progressTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#2196F3")
            )
        } else {
            progressBar.progressTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#FF9800")
            )
        }
    }
}
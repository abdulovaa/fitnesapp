package com.example.fitness_assist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class NutritionFragment : Fragment() {

    private var dailyCalorieGoal = 2000
    private var consumedCalories = 0

    // Примеры продуктов для быстрого добавления
    private val foodItems = listOf(
        "Яблоко - 52 ккал",
        "Банан - 96 ккал",
        "Бутерброд - 250 ккал",
        "Куриная грудка - 165 ккал",
        "Рис (100г) - 130 ккал",
        "Овсянка (100г) - 68 ккал",
        "Йогурт - 150 ккал",
        "Яйцо - 78 ккал",
        "Картофель (100г) - 77 ккал",
        "Суп - 120 ккал"
    )

    private val addedFoods = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nutrition, container, false)

        // Загружаем сохранённые данные через утилиту
        consumedCalories = UserPrefsUtils.getInt(requireContext(), "consumed_calories", 0)
        dailyCalorieGoal = UserPrefsUtils.getInt(requireContext(), "calorie_goal", 2000)

        // Загружаем список добавленных продуктов
        val foodsJson = UserPrefsUtils.getString(requireContext(), "added_foods", "")
        addedFoods.clear()
        if (foodsJson.isNotEmpty()) {
            addedFoods.addAll(foodsJson.split("||"))
        }

        // Находим все элементы
        val calorieTextView = view.findViewById<TextView>(R.id.tv_calorie_count)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar_calories)
        val foodListView = view.findViewById<ListView>(R.id.lv_food_items)
        val addedFoodsListView = view.findViewById<ListView>(R.id.lv_added_foods)
        val addCustomBtn = view.findViewById<Button>(R.id.btn_add_custom)
        val resetBtn = view.findViewById<Button>(R.id.btn_reset_nutrition)
        val goalEditText = view.findViewById<EditText>(R.id.et_calorie_goal)
        val setGoalBtn = view.findViewById<Button>(R.id.btn_set_goal)

        // Обновляем UI
        updateUI(calorieTextView, progressBar, addedFoodsListView)

        // Настройка списка продуктов
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, foodItems)
        foodListView.adapter = adapter

        // Обработка клика по продукту
        foodListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedFood = foodItems[position]
            val calories = extractCalories(selectedFood)
            consumedCalories += calories
            addedFoods.add(selectedFood)
            saveNutritionData()
            updateUI(calorieTextView, progressBar, addedFoodsListView)
            Toast.makeText(context, "Добавлено: ${selectedFood.split(" - ")[0]}", Toast.LENGTH_SHORT).show()
        }

        // Кнопка добавления кастомной еды
        addCustomBtn.setOnClickListener {
            val foodName = "Мой продукт"
            val foodCalories = 200
            consumedCalories += foodCalories
            addedFoods.add("$foodName - $foodCalories ккал")
            saveNutritionData()
            updateUI(calorieTextView, progressBar, addedFoodsListView)
            Toast.makeText(context, "Добавлен: $foodName", Toast.LENGTH_SHORT).show()
        }

        // Кнопка сброса
        resetBtn.setOnClickListener {
            consumedCalories = 0
            addedFoods.clear()
            saveNutritionData()
            updateUI(calorieTextView, progressBar, addedFoodsListView)
            Toast.makeText(context, "Данные питания сброшены", Toast.LENGTH_SHORT).show()
        }

        // Установка новой цели
        setGoalBtn.setOnClickListener {
            val goalText = goalEditText.text.toString()
            if (goalText.isNotEmpty()) {
                val newGoal = goalText.toIntOrNull()
                if (newGoal != null && newGoal in 500..5000) {
                    dailyCalorieGoal = newGoal
                    saveNutritionData()
                    updateUI(calorieTextView, progressBar, addedFoodsListView)
                    goalEditText.text.clear()
                    Toast.makeText(context, "Цель обновлена: $newGoal ккал", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Введите число от 500 до 5000", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    private fun extractCalories(foodString: String): Int {
        return try {
            foodString.split(" - ")[1].split(" ")[0].toInt()
        } catch (e: Exception) {
            100
        }
    }

    private fun updateUI(calorieTextView: TextView, progressBar: ProgressBar, addedFoodsListView: ListView) {
        calorieTextView.text = "$consumedCalories/$dailyCalorieGoal ккал"

        val progress = if (dailyCalorieGoal > 0) {
            (consumedCalories.toFloat() / dailyCalorieGoal * 100).toInt()
        } else {
            0
        }

        progressBar.progress = progress

        // Обновляем список добавленных продуктов
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, addedFoods)
        addedFoodsListView.adapter = adapter
    }

    private fun saveNutritionData() {
        // Сохраняем данные через утилиту
        UserPrefsUtils.putInt(requireContext(), "consumed_calories", consumedCalories)
        UserPrefsUtils.putInt(requireContext(), "calorie_goal", dailyCalorieGoal)

        // Сохраняем список добавленных продуктов
        val foodsString = addedFoods.joinToString("||")
        UserPrefsUtils.putString(requireContext(), "added_foods", foodsString)
    }
}
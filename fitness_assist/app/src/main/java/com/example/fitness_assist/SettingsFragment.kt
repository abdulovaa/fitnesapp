package com.example.fitness_assist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Инициализация элементов
        val waterGoalEditText = view.findViewById<EditText>(R.id.et_water_goal)
        val currentGoalTextView = view.findViewById<TextView>(R.id.tv_current_goal)
        val currentUserTextView = view.findViewById<TextView>(R.id.tv_current_user)
        val saveBtn = view.findViewById<Button>(R.id.btn_save_settings)
        val resetAllBtn = view.findViewById<Button>(R.id.btn_reset_all)
        val logoutBtn = view.findViewById<Button>(R.id.btn_logout)

        // Загружаем текущие настройки
        loadCurrentSettings(currentGoalTextView, currentUserTextView, waterGoalEditText)

        // Сохранение настроек
        saveBtn.setOnClickListener {
            saveSettings(waterGoalEditText, currentGoalTextView)
        }

        // Сброс всех данных
        resetAllBtn.setOnClickListener {
            resetAllData(currentGoalTextView, currentUserTextView, waterGoalEditText)
        }

        // Кнопка выхода - ВАЖНО: правильный переход
        logoutBtn.setOnClickListener {
            logoutUser()
        }

        return view
    }

    private fun loadCurrentSettings(goalTextView: TextView, userTextView: TextView, goalEditText: EditText) {
        // Загружаем цель текущего пользователя
        val currentGoal = UserPrefsUtils.getInt(requireContext(), "water_goal", 8)
        goalTextView.text = "Текущая цель: $currentGoal стаканов"
        goalEditText.setText(currentGoal.toString())

        // Показываем текущего пользователя
        val userEmail = UserPrefsUtils.getCurrentUserEmail(requireContext())
        val isGuest = UserPrefsUtils.isGuest(requireContext())

        if (isGuest) {
            userTextView.text = "Пользователь: Гость"
        } else {
            userTextView.text = "Пользователь: $userEmail"
        }
    }

    private fun saveSettings(goalEditText: EditText, goalTextView: TextView) {
        val goalText = goalEditText.text.toString()

        if (goalText.isNotEmpty()) {
            val goal = goalText.toIntOrNull()

            if (goal != null && goal in 1..20) {
                // Сохраняем цель для текущего пользователя
                UserPrefsUtils.putInt(requireContext(), "water_goal", goal)

                goalTextView.text = "Текущая цель: $goal стаканов"
                Toast.makeText(context, "Настройки сохранены", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Введите число от 1 до 20", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Введите цель", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetAllData(goalTextView: TextView, userTextView: TextView, goalEditText: EditText) {
        // Сбрасываем данные текущего пользователя
        UserPrefsUtils.remove(requireContext(), "water_count")
        UserPrefsUtils.remove(requireContext(), "water_goal")
        UserPrefsUtils.remove(requireContext(), "consumed_calories")
        UserPrefsUtils.remove(requireContext(), "calorie_goal")
        UserPrefsUtils.remove(requireContext(), "added_foods")

        // Устанавливаем значения по умолчанию
        UserPrefsUtils.putInt(requireContext(), "water_goal", 8)
        UserPrefsUtils.putInt(requireContext(), "calorie_goal", 2000)

        loadCurrentSettings(goalTextView, userTextView, goalEditText)
        Toast.makeText(context, "Все данные сброшены", Toast.LENGTH_SHORT).show()
    }

    private fun logoutUser() {
        // 1. Сохраняем статус выхода
        val prefs = UserPrefsUtils.getSharedPreferences(requireContext())
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .putBoolean("is_guest", false)
            .apply()

        Toast.makeText(context, "Вы вышли из системы", Toast.LENGTH_SHORT).show()

        // 2. Используем метод из MainActivity
        (requireActivity() as MainActivity).restartApp()
    }

    private fun navigateToLoginScreen() {
        try {
            // Пробуем использовать действие из nav_graph
            findNavController().navigate(R.id.action_settings_to_login)
        } catch (e: Exception) {
            // Если действие не найдено, пробуем просто перейти
            try {
                findNavController().navigate(R.id.loginFragment)
            } catch (e2: Exception) {
                // Если не работает навигация, используем альтернативный метод
                useAlternativeNavigation()
            }
        }
    }

    private fun useAlternativeNavigation() {
        // Метод 1: Очистка стека вручную
        try {
            findNavController().popBackStack(R.id.loginFragment, false)
            if (findNavController().currentDestination?.id != R.id.loginFragment) {
                findNavController().navigate(R.id.loginFragment)
            }
        } catch (e: Exception) {
            // Метод 2: Перезапуск с флагом
            restartActivityWithLogin()
        }
    }

    private fun restartActivityWithLogin() {
        // Полностью перезапускаем активность
        val intent = requireActivity().intent
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)

        requireActivity().finish()
        startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}
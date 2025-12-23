package com.example.fitness_assist

import android.content.Context
import android.content.SharedPreferences

object UserPrefsUtils {

    private const val PREFS_NAME = "com.example.fitness_assist_prefs"

    // Получить SharedPreferences
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Получить ключ с префиксом пользователя
    private fun getKeyWithUser(context: Context, key: String): String {
        val prefs = getPrefs(context)
        val userEmail = prefs.getString("user_email", "")
        val isGuest = prefs.getBoolean("is_guest", true)

        return if (isGuest || userEmail.isNullOrEmpty()) {
            "${key}_guest"
        } else {
            "${key}_${userEmail.hashCode()}"
        }
    }

    // Получить Int значение для текущего пользователя
    fun getInt(context: Context, key: String, defaultValue: Int): Int {
        val userKey = getKeyWithUser(context, key)
        return getPrefs(context).getInt(userKey, defaultValue)
    }

    // Сохранить Int значение для текущего пользователя
    fun putInt(context: Context, key: String, value: Int) {
        val userKey = getKeyWithUser(context, key)
        getPrefs(context).edit().putInt(userKey, value).apply()
    }

    // Получить String значение для текущего пользователя
    fun getString(context: Context, key: String, defaultValue: String): String {
        val userKey = getKeyWithUser(context, key)
        return getPrefs(context).getString(userKey, defaultValue) ?: defaultValue
    }

    // Сохранить String значение для текущего пользователя
    fun putString(context: Context, key: String, value: String) {
        val userKey = getKeyWithUser(context, key)
        getPrefs(context).edit().putString(userKey, value).apply()
    }

    // Удалить значение для текущего пользователя
    fun remove(context: Context, key: String) {
        val userKey = getKeyWithUser(context, key)
        getPrefs(context).edit().remove(userKey).apply()
    }

    // Получить email текущего пользователя
    fun getCurrentUserEmail(context: Context): String {
        return getPrefs(context).getString("user_email", "Гость") ?: "Гость"
    }

    // Проверить является ли пользователь гостем
    fun isGuest(context: Context): Boolean {
        return getPrefs(context).getBoolean("is_guest", true)
    }

    // Получить SharedPreferences напрямую (для общих настроек)
    fun getSharedPreferences(context: Context): SharedPreferences {
        return getPrefs(context)
    }
}
package com.example.fitness_assist

import android.content.Context
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

class LoginFragment : Fragment() {

    private lateinit var prefsName: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        prefsName = "${requireContext().packageName}_prefs"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val emailEditText = view.findViewById<EditText>(R.id.et_email)
        val passwordEditText = view.findViewById<EditText>(R.id.et_password)
        val loginButton = view.findViewById<Button>(R.id.btn_login)
        val registerButton = view.findViewById<Button>(R.id.btn_register)
        val skipTextView = view.findViewById<TextView>(R.id.tv_skip)

        // Проверяем, если уже авторизован
        if (isUserLoggedIn()) {
            navigateToHome()
        }

        // Вход
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                if (loginUser(email, password)) {
                    Toast.makeText(context, "Вход успешен!", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                } else {
                    Toast.makeText(context, "Неверный email или пароль", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Регистрация
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                if (registerUser(email, password)) {
                    Toast.makeText(context, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                } else {
                    Toast.makeText(context, "Пользователь уже существует", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Пропустить
        skipTextView.setOnClickListener {
            saveGuestUser()
            Toast.makeText(context, "Вход как гость", Toast.LENGTH_SHORT).show()
            navigateToHome()
        }

        return view
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Введите корректный email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(context, "Пароль должен быть не менее 6 символов", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun loginUser(email: String, password: String): Boolean {
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val savedEmail = prefs.getString("user_email", "")
        val savedPassword = prefs.getString("user_password", "")

        return email == savedEmail && password == savedPassword
    }

    private fun registerUser(email: String, password: String): Boolean {
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val savedEmail = prefs.getString("user_email", "")

        // Проверяем, не зарегистрирован ли уже пользователь
        if (savedEmail == email) {
            return false
        }

        // Сохраняем нового пользователя
        prefs.edit()
            .putString("user_email", email)
            .putString("user_password", password)
            .putBoolean("is_logged_in", true)
            .putBoolean("is_guest", false)
            .apply()

        return true
    }

    private fun isUserLoggedIn(): Boolean {
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        return prefs.getBoolean("is_logged_in", false)
    }

    private fun saveGuestUser() {
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putBoolean("is_guest", true)
            .apply()
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_login_to_home)
    }
}
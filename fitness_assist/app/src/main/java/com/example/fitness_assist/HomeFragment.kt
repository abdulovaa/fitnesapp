package com.example.fitness_assist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.findViewById<Button>(R.id.btn_workout).setOnClickListener {
            replaceFragment(WorkoutFragment())
        }

        view.findViewById<Button>(R.id.btn_water).setOnClickListener {
            replaceFragment(WaterFragment())
        }

        view.findViewById<Button>(R.id.btn_nutrition).setOnClickListener {
            replaceFragment(NutritionFragment())
        }

        view.findViewById<Button>(R.id.btn_settings).setOnClickListener {
            replaceFragment(SettingsFragment())
        }

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.addToBackStack(null) // Добавляем в стек для кнопки "Назад"
        transaction.commit()
    }
}
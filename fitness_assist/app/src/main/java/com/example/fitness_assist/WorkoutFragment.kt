package com.example.fitness_assist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*

class WorkoutFragment : Fragment() {
    private var workoutTime = 0
    private var timerRunning = false
    private lateinit var timerTextView: TextView
    private var timerJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout, container, false)


        timerTextView = view.findViewById(R.id.tv_timer)
        val startBtn = view.findViewById<Button>(R.id.btn_start_workout)
        val stopBtn = view.findViewById<Button>(R.id.btn_stop_workout)

        startBtn.setOnClickListener { startTimer() }
        stopBtn.setOnClickListener { stopTimer() }

        return view
    }

    private fun startTimer() {
        if (timerRunning) return

        timerRunning = true
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (timerRunning) {
                delay(1000)
                workoutTime++
                updateTimer()
            }
        }
    }

    private fun stopTimer() {
        timerRunning = false
        timerJob?.cancel()
    }

    private fun updateTimer() {
        val minutes = workoutTime / 60
        val seconds = workoutTime % 60
        timerTextView.text = String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("workout_time", workoutTime)
        outState.putBoolean("timer_running", timerRunning)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            workoutTime = it.getInt("workout_time", 0)
            timerRunning = it.getBoolean("timer_running", false)
            updateTimer()
            if (timerRunning) {
                startTimer()
            }
        }
    }
}
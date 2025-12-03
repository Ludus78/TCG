package com.example.mtgtcgtoolkit

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.mtgtcgtoolkit.data.GamePreferences
import com.example.mtgtcgtoolkit.model.GameState
import com.example.mtgtcgtoolkit.model.GameType
import com.example.mtgtcgtoolkit.ui.GameScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var prefs: GamePreferences

    private var autoSaveJob: Job? = null
    private var currentState: GameState = GameState.newFourPlayer(GameType.MTG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        prefs = GamePreferences(this)

        // Charger une éventuelle partie existante
        prefs.loadGameState()?.let {
            currentState = it
        }

        val firstLaunch = prefs.isFirstLaunch()

        setContent {
            var state = currentState
            var showTutorial = firstLaunch

            GameScreen(
                state = state,
                onStateChange = {
                    state = it
                    currentState = it
                },
                showTutorial = showTutorial,
                onDismissTutorial = {
                    showTutorial = false
                    prefs.setNotFirstLaunch()
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        startAutoSave()
    }

    override fun onPause() {
        super.onPause()
        stopAutoSave()
        prefs.saveGameState(currentState)
    }

    private fun startAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = lifecycleScope.launch {
            while (isActive) {
                delay(5_000L)
                prefs.saveGameState(currentState)
            }
        }
    }

    private fun stopAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = null
    }
}




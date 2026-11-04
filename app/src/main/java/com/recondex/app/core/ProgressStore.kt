package com.recondex.app.core

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDate

/**
 * Guarda racha / XP / aceptación del disclaimer legal en SharedPreferences.
 * Es deliberadamente simple (sin Room/DataStore) para mantener el build liviano.
 */
class ProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("recondex_progress", Context.MODE_PRIVATE)

    var xp by mutableIntStateOf(prefs.getInt(KEY_XP, 0))
        private set
    var streak by mutableIntStateOf(prefs.getInt(KEY_STREAK, 0))
        private set
    var disclaimerAccepted by mutableStateOf(prefs.getBoolean(KEY_DISCLAIMER, false))
        private set

    val completedTools = mutableSetOf<String>().apply {
        addAll(prefs.getStringSet(KEY_COMPLETED, emptySet()) ?: emptySet())
    }

    fun acceptDisclaimer() {
        disclaimerAccepted = true
        prefs.edit().putBoolean(KEY_DISCLAIMER, true).apply()
    }

    fun awardXp(toolId: String, amount: Int) {
        val today = LocalDate.now().toString()
        val lastDay = prefs.getString(KEY_LAST_DAY, null)
        if (lastDay != today) {
            streak += 1
            prefs.edit().putString(KEY_LAST_DAY, today).putInt(KEY_STREAK, streak).apply()
        }
        if (completedTools.add(toolId)) {
            xp += amount
            prefs.edit().putInt(KEY_XP, xp).putStringSet(KEY_COMPLETED, completedTools).apply()
        }
    }

    companion object {
        private const val KEY_XP = "xp"
        private const val KEY_STREAK = "streak"
        private const val KEY_DISCLAIMER = "disclaimer_ok"
        private const val KEY_LAST_DAY = "last_day"
        private const val KEY_COMPLETED = "completed_tools"
    }
}

package com.recondex.app.core

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/**
 * Guarda qué herramientas ya se explorearon y si se aceptó el aviso legal.
 * Es deliberadamente simple (SharedPreferences) para mantener el build liviano.
 * RecoNDex es una herramienta, no una app de lecciones: no hay XP ni rachas.
 */
class ProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("recondex_progress", Context.MODE_PRIVATE)

    var disclaimerAccepted by mutableStateOf(prefs.getBoolean(KEY_DISCLAIMER, false))
        private set

    val completedTools = mutableSetOf<String>().apply {
        addAll(prefs.getStringSet(KEY_COMPLETED, emptySet()) ?: emptySet())
    }

    fun acceptDisclaimer() {
        disclaimerAccepted = true
        prefs.edit().putBoolean(KEY_DISCLAIMER, true).apply()
    }

    fun markExplored(toolId: String) {
        if (completedTools.add(toolId)) {
            prefs.edit().putStringSet(KEY_COMPLETED, completedTools).apply()
        }
    }

    companion object {
        private const val KEY_DISCLAIMER = "disclaimer_ok"
        private const val KEY_COMPLETED = "completed_tools"
    }
}

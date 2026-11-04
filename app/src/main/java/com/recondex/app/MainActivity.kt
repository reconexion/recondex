package com.recondex.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.recondex.app.core.ProgressStore
import com.recondex.app.ui.RecoNDexApp
import com.recondex.app.ui.theme.RecoNDexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val progress = ProgressStore(applicationContext)
        setContent {
            RecoNDexTheme {
                RecoNDexApp(progress = progress)
            }
        }
    }
}

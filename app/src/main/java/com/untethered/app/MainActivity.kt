package com.untethered.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.untethered.app.presentation.theme.UntetheredTheme
import com.untethered.app.presentation.ui.terminal.TerminalScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UntetheredTheme {
                TerminalScreen()
            }
        }
    }
}
package com.untethered.app.presentation.terminal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.untethered.app.presentation.theme.TerminalGray
import com.untethered.app.presentation.theme.TerminalGreen
import com.untethered.app.presentation.theme.TerminalRed
import com.untethered.app.presentation.theme.TerminalYellow

@Composable
fun TerminalWelcome(
    shizukuAvailable: Boolean,
    permissionGranted: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = """
 _   _       _       _   _                    _
| | | |_ __ | |_ ___| |_| |__   ___ _ __ ___| |
| | | | '_ \| __/ _ \ __| '_ \ / _ \ '__/ _ \ |
| |_| | | | | ||  __/ |_| | | |  __/ | |  __/ |
 \___/|_| |_|\__\___|\__|_| |_|\___|_|  \___|_|
            """.trimIndent(),
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            color = TerminalGreen,
            lineHeight = 13.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        WelcomeLine(label = "Version", value = "1.0.0")
        WelcomeLine(label = "Shell",   value = "sh via Shizuku")
        WelcomeLine(label = "Package", value = "com.untethered.app")
        WelcomeLine(
            label = "Shizuku",
            value = when {
                !shizukuAvailable  -> "not running ✗"
                !permissionGranted -> "permission needed ✗"
                else               -> "ready ✓"
            },
            valueColor = when {
                !shizukuAvailable  -> TerminalRed
                !permissionGranted -> TerminalYellow
                else               -> TerminalGreen
            }
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Type a command below and press Enter.",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = TerminalGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Swipe from the left edge to open history.",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = TerminalGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Long-press a history item to save as snippet.",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = TerminalGray
        )
    }
}

@Composable
private fun WelcomeLine(
    label: String,
    value: String,
    valueColor: Color = TerminalGreen
) {
    Text(
        text = "%-10s%s".format("$label:", value),
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        color = valueColor,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
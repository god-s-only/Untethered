package com.yourname.termidroid.presentation.terminal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.untethered.app.presentation.theme.TerminalYellow

@Composable
fun ShizukuBanner(
    shizukuAvailable: Boolean,
    permissionGranted: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val message = when {
        !shizukuAvailable  -> "Shizuku is not running. Start Shizuku to execute commands."
        !permissionGranted -> "Permission required. Tap Grant to allow shell access."
        else -> return
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(TerminalYellow.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = TerminalYellow
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = TerminalYellow
            )
        }

        if (!permissionGranted && shizukuAvailable) {
            TextButton(onClick = onRequestPermission) {
                Text(
                    text = "Grant",
                    color = TerminalYellow
                )
            }
        }
    }
}
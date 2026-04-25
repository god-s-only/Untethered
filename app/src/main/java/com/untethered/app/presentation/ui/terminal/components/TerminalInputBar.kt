package com.yourname.termidroid.presentation.terminal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.untethered.app.presentation.theme.TerminalBorder
import com.untethered.app.presentation.theme.TerminalInputStyle
import com.untethered.app.presentation.theme.TerminalRed
import com.untethered.app.presentation.theme.TerminalSurface


@Composable
fun TerminalInputBar(
    input: String,
    isRunning: Boolean,
    onInputChanged: (String) -> Unit,
    onSend: () -> Unit,
    onHistoryUp: () -> Unit,
    onHistoryDown: () -> Unit,
    onCtrlC: () -> Unit,
    onSaveSnippet: () -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalDivider(color = TerminalBorder, thickness = 1.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(TerminalSurface)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isRunning) {
            AssistChip(
                onClick = onCtrlC,
                label = { Text("^C", style = MaterialTheme.typography.labelSmall) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = TerminalRed.copy(alpha = 0.15f),
                    labelColor = TerminalRed
                ),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = true,
                    borderColor = TerminalRed.copy(alpha = 0.4f)
                ),
                modifier = Modifier.height(32.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        TextField(
            value = input,
            onValueChange = onInputChanged,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = if (isRunning) "stdin..." else "Enter command...",
                    style = TerminalInputStyle,
                    color = LocalContentColor.current.copy(alpha = 0.4f)
                )
            },
            textStyle = TerminalInputStyle,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSend() })
        )

        IconButton(
            onClick = onHistoryUp,
            enabled = !isRunning
        ) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Previous command",
                tint = if (!isRunning) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                }
            )
        }

        IconButton(
            onClick = onHistoryDown,
            enabled = !isRunning
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = "Next command",
                tint = if (!isRunning) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                }
            )
        }

        IconButton(
            onClick = onSaveSnippet,
            enabled = input.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.Default.BookmarkAdd,
                contentDescription = "Save as snippet",
                tint = if (input.isNotBlank()) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                }
            )
        }

        IconButton(onClick = if (isRunning) onCtrlC else onSend) {
            Icon(
                imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.Send,
                contentDescription = if (isRunning) "Stop process" else "Send command",
                tint = if (isRunning) TerminalRed else MaterialTheme.colorScheme.primary
            )
        }
    }
}
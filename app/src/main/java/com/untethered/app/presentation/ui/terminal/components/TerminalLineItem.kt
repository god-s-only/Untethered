package com.yourname.termidroid.presentation.terminal.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.untethered.app.presentation.theme.TerminalPrompt
import com.untethered.app.presentation.theme.TerminalRed
import com.untethered.app.presentation.theme.TerminalTextStyle
import com.untethered.app.presentation.theme.TerminalWhite
import com.untethered.app.presentation.ui.terminal.TerminalLineUi


@Composable
fun TerminalLineItem(
    line: TerminalLineUi,
    modifier: Modifier = Modifier
) {
    SelectionContainer {
        Text(
            text = when {
                line.isInput -> buildAnnotatedString {
                    // Input echo: show "$ command" with green prompt
                    withStyle(SpanStyle(color = TerminalPrompt)) {
                        append(line.annotated)
                    }
                }
                line.isError -> buildAnnotatedString {
                    withStyle(SpanStyle(color = TerminalRed)) {
                        append(line.annotated)
                    }
                }
                else -> line.annotated
            },
            style = TerminalTextStyle,
            color = if (!line.isInput && !line.isError) {
                TerminalWhite
            } else {
                Color.Unspecified
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 1.dp)
        )
    }
}
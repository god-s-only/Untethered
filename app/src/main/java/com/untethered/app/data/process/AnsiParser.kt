package com.untethered.app.data.process

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

object AnsiParser {

    private val ANSI_REGEX = Regex("\u001B\\[([0-9;]*)([A-Za-z])")

    private data class AnsiStyle(
        val bold: Boolean = false,
        val italic: Boolean = false,
        val underline: Boolean = false,
        val foreground: Color? = null,
        val background: Color? = null
    ) {
        fun toSpanStyle() = SpanStyle(
            color = foreground ?: Color.Unspecified,
            background = background ?: Color.Unspecified,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
            textDecoration = if (underline) TextDecoration.Underline else TextDecoration.None
        )
    }

    fun parse(raw: String): AnnotatedString {
        return buildAnnotatedString {
            var currentStyle = AnsiStyle()
            var lastIndex = 0

            ANSI_REGEX.findAll(raw).forEach { match ->
                val plainText = raw.substring(lastIndex, match.range.first)
                if (plainText.isNotEmpty()) {
                    pushStyle(currentStyle.toSpanStyle())
                    append(plainText)
                    pop()
                }
                if (match.groupValues[2] == "m") {
                    currentStyle = applySgrParams(
                        params = match.groupValues[1],
                        current = currentStyle
                    )
                }
                lastIndex = match.range.last + 1
            }

            val remaining = raw.substring(lastIndex)
            if (remaining.isNotEmpty()) {
                pushStyle(currentStyle.toSpanStyle())
                append(remaining)
                pop()
            }
        }
    }

    private fun applySgrParams(params: String, current: AnsiStyle): AnsiStyle {
        if (params.isEmpty() || params == "0") return AnsiStyle()

        var style = current
        val codes = params.split(";").mapNotNull { it.toIntOrNull() }

        codes.forEach { code ->
            style = when (code) {
                0          -> AnsiStyle()
                1          -> style.copy(bold = true)
                3          -> style.copy(italic = true)
                4          -> style.copy(underline = true)
                22         -> style.copy(bold = false)
                23         -> style.copy(italic = false)
                24         -> style.copy(underline = false)
                39         -> style.copy(foreground = null)
                49         -> style.copy(background = null)

                30         -> style.copy(foreground = AnsiColors.BLACK)
                31         -> style.copy(foreground = AnsiColors.RED)
                32         -> style.copy(foreground = AnsiColors.GREEN)
                33         -> style.copy(foreground = AnsiColors.YELLOW)
                34         -> style.copy(foreground = AnsiColors.BLUE)
                35         -> style.copy(foreground = AnsiColors.MAGENTA)
                36         -> style.copy(foreground = AnsiColors.CYAN)
                37         -> style.copy(foreground = AnsiColors.WHITE)

                90         -> style.copy(foreground = AnsiColors.BRIGHT_BLACK)
                91         -> style.copy(foreground = AnsiColors.BRIGHT_RED)
                92         -> style.copy(foreground = AnsiColors.BRIGHT_GREEN)
                93         -> style.copy(foreground = AnsiColors.BRIGHT_YELLOW)
                94         -> style.copy(foreground = AnsiColors.BRIGHT_BLUE)
                95         -> style.copy(foreground = AnsiColors.BRIGHT_MAGENTA)
                96         -> style.copy(foreground = AnsiColors.BRIGHT_CYAN)
                97         -> style.copy(foreground = AnsiColors.BRIGHT_WHITE)

                40         -> style.copy(background = AnsiColors.BLACK)
                41         -> style.copy(background = AnsiColors.RED)
                42         -> style.copy(background = AnsiColors.GREEN)
                43         -> style.copy(background = AnsiColors.YELLOW)
                44         -> style.copy(background = AnsiColors.BLUE)
                45         -> style.copy(background = AnsiColors.MAGENTA)
                46         -> style.copy(background = AnsiColors.CYAN)
                47         -> style.copy(background = AnsiColors.WHITE)

                else       -> style
            }
        }
        return style
    }

    private object AnsiColors {
        val BLACK          = Color(0xFF000000)
        val RED            = Color(0xFFCC0000)
        val GREEN          = Color(0xFF4CAF50)
        val YELLOW         = Color(0xFFCDCD00)
        val BLUE           = Color(0xFF1976D2)
        val MAGENTA        = Color(0xFFCD00CD)
        val CYAN           = Color(0xFF00CDCD)
        val WHITE          = Color(0xFFE5E5E5)

        val BRIGHT_BLACK   = Color(0xFF7F7F7F)
        val BRIGHT_RED     = Color(0xFFFF0000)
        val BRIGHT_GREEN   = Color(0xFF00FF00)
        val BRIGHT_YELLOW  = Color(0xFFFFFF00)
        val BRIGHT_BLUE    = Color(0xFF4FC3F7)
        val BRIGHT_MAGENTA = Color(0xFFFF00FF)
        val BRIGHT_CYAN    = Color(0xFF00FFFF)
        val BRIGHT_WHITE   = Color(0xFFFFFFFF)
    }
}
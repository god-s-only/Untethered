package com.untethered.app.presentation.ui.drawer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.untethered.app.domain.model.CommandHistory
import com.untethered.app.domain.model.CommandSnippet
import com.untethered.app.presentation.theme.TerminalBorder
import com.untethered.app.presentation.theme.TerminalGray
import com.untethered.app.presentation.theme.TerminalGreen

@Composable
fun TerminalDrawer(
    uiState: DrawerUiState,
    onCommandSelected: (String) -> Unit,
    onDeleteHistory: (Long) -> Unit,
    onClearHistory: () -> Unit,
    onDeleteSnippet: (Long) -> Unit,
    onSaveSnippet: (CommandHistory) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item(key = "header_history") {
                DrawerSectionHeader(
                    title = "History",
                    icon = Icons.Default.History,
                    actionIcon = Icons.Default.DeleteSweep,
                    actionDescription = "Clear history",
                    onAction = onClearHistory
                )
            }

            if (uiState.history.isEmpty()) {
                item(key = "empty_history") {
                    DrawerEmptyHint("No commands yet")
                }
            } else {
                items(
                    items = uiState.history,
                    key = { "history_${it.id}" }
                ) { item ->
                    SwipeToDismissHistoryItem(
                        item = item,
                        onSelect = { onCommandSelected(item.command) },
                        onDismiss = { onDeleteHistory(item.id) },
                        onSave = { onSaveSnippet(it) }
                    )
                }
            }

            item(key = "divider") {
                HorizontalDivider(
                    color = TerminalBorder,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item(key = "header_snippets") {
                DrawerSectionHeader(
                    title = "Snippets",
                    icon = Icons.Default.Bookmark,
                    actionIcon = null,
                    actionDescription = null,
                    onAction = {}
                )
            }

            if (uiState.snippets.isEmpty()) {
                item(key = "empty_snippets") {
                    DrawerEmptyHint("No saved snippets.\nLong-press a history item to save.")
                }
            } else {
                items(
                    items = uiState.snippets,
                    key = { "snippet_${it.id}" }
                ) { item ->
                    SwipeToDismissSnippetItem(
                        item = item,
                        onSelect = { onCommandSelected(item.command) },
                        onDismiss = { onDeleteSnippet(item.id) }
                    )
                }
            }

            item(key = "footer_spacer") {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DrawerSectionHeader(
    title: String,
    icon: ImageVector,
    actionIcon: ImageVector?,
    actionDescription: String?,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TerminalGreen
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = TerminalGreen,
            modifier = Modifier.weight(1f)
        )
        if (actionIcon != null) {
            IconButton(onClick = onAction) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = actionDescription,
                    tint = TerminalGray
                )
            }
        }
    }
}

@Composable
private fun DrawerEmptyHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = TerminalGray,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SwipeToDismissHistoryItem(
    item: CommandHistory,
    onSelect: () -> Unit,
    onDismiss: () -> Unit,
    onSave: (CommandHistory) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { SwipeDismissBackground() },
        enableDismissFromStartToEnd = false
    ) {
        DrawerCommandItem(
            command = item.command,
            subtitle = null,
            onClick = onSelect,
            onLongClick = { onSave(item) }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SwipeToDismissSnippetItem(
    item: CommandSnippet,
    onSelect: () -> Unit,
    onDismiss: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { SwipeDismissBackground() },
        enableDismissFromStartToEnd = false,
    ) {
        DrawerCommandItem(
            command = item.command,
            subtitle = item.label.ifBlank { null },
            onClick = onSelect,
            onLongClick = {}
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DrawerCommandItem(
    command: String,
    subtitle: String?,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = command,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TerminalGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SwipeDismissBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.DeleteSweep,
            contentDescription = "Delete",
            tint = MaterialTheme.colorScheme.error
        )
    }
}
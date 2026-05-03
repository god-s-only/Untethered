package com.untethered.app.presentation.ui.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.untethered.app.presentation.ui.drawer.DrawerViewModel
import com.untethered.app.presentation.ui.drawer.TerminalDrawer
import com.untethered.app.presentation.ui.terminal.components.SaveSnippetDialog
import com.untethered.app.presentation.ui.terminal.components.ShizukuBanner
import com.untethered.app.presentation.ui.terminal.components.TerminalInputBar
import com.untethered.app.presentation.ui.terminal.components.TerminalLineItem
import com.untethered.app.presentation.ui.terminal.components.TerminalWelcome
import com.untethered.app.presentation.theme.TerminalBackground
import com.untethered.app.presentation.theme.TerminalGreen
import com.untethered.app.presentation.theme.TerminalSurface
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    terminalViewModel: TerminalViewModel = hiltViewModel(),
    drawerViewModel: DrawerViewModel = hiltViewModel()
) {
    val terminalState by terminalViewModel.uiState.collectAsState()
    val drawerState by drawerViewModel.uiState.collectAsState()

    var showSnippetDialog by remember { mutableStateOf(false) }

    val drawerNavState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(terminalState.lines.size) {
        if (terminalState.lines.isNotEmpty()) {
            listState.animateScrollToItem(terminalState.lines.lastIndex)
        }
    }

    LaunchedEffect(drawerNavState.currentValue) {
        terminalViewModel.refreshShizukuState()
    }

    if (showSnippetDialog) {
        SaveSnippetDialog(
            command = terminalState.inputText,
            onConfirm = { label ->
                drawerViewModel.onSaveSnippet(terminalState.inputText, label)
                showSnippetDialog = false
            },
            onDismiss = { showSnippetDialog = false }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerNavState,
        drawerContent = {
            TerminalDrawer(
                uiState = drawerState,
                onCommandSelected = { command ->
                    terminalViewModel.onPasteCommand(command)
                    scope.launch { drawerNavState.close() }
                },
                onDeleteHistory = drawerViewModel::onDeleteHistoryItem,
                onClearHistory = drawerViewModel::onClearHistory,
                onDeleteSnippet = drawerViewModel::onDeleteSnippet,
                onSaveSnippet = { drawerViewModel.onSaveSnippet(command = it.command) }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Untethered",
                            color = TerminalGreen
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerNavState.open() } }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open drawer",
                                tint = TerminalGreen
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = terminalViewModel::onClearSession) {
                            Icon(
                                imageVector = Icons.Default.ClearAll,
                                contentDescription = "Clear session",
                                tint = TerminalGreen
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = TerminalSurface
                    )
                )
            },
            containerColor = TerminalBackground
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(TerminalBackground)
                    .padding(paddingValues)
            ) {
                ShizukuBanner(
                    shizukuAvailable = terminalState.shizukuAvailable,
                    permissionGranted = terminalState.shizukuPermissionGranted,
                    onRequestPermission = terminalViewModel::onRequestShizukuPermission
                )

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(TerminalBackground)
                ) {
                    if (terminalState.lines.isEmpty()) {
                        item(key = "welcome") {
                            TerminalWelcome(
                                shizukuAvailable = terminalState.shizukuAvailable,
                                permissionGranted = terminalState.shizukuPermissionGranted
                            )
                        }
                    }

                    items(
                        items = terminalState.lines,
                        key = { line -> line.id }
                    ) { line ->
                        TerminalLineItem(line = line)
                    }
                }

                TerminalInputBar(
                    input = terminalState.inputText,
                    isRunning = terminalState.isRunning,
                    onInputChanged = terminalViewModel::onInputChanged,
                    onSend = terminalViewModel::onSendCommand,
                    onHistoryUp = terminalViewModel::onHistoryUp,
                    onHistoryDown = terminalViewModel::onHistoryDown,
                    onCtrlC = terminalViewModel::onCtrlC,
                    onSaveSnippet = { showSnippetDialog = true },
                )
            }
        }
    }
}
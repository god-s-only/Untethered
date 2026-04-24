package com.yourname.termidroid.presentation.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Terminal
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.untethered.app.presentation.theme.TerminalBackground
import com.untethered.app.presentation.theme.TerminalGreen
import com.untethered.app.presentation.theme.TerminalSurface
import com.untethered.app.presentation.ui.drawer.DrawerViewModel
import com.untethered.app.presentation.ui.terminal.TerminalViewModel
import com.yourname.termidroid.presentation.drawer.TerminalDrawer
import com.yourname.termidroid.presentation.terminal.components.ShizukuBanner
import com.yourname.termidroid.presentation.terminal.components.TerminalInputBar
import com.yourname.termidroid.presentation.terminal.components.TerminalLineItem

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    terminalViewModel: TerminalViewModel = hiltViewModel(),
    drawerViewModel: DrawerViewModel = hiltViewModel()
) {
    val terminalState by terminalViewModel.uiState.collectAsState()
    val drawerState by drawerViewModel.uiState.collectAsState()

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
                onDeleteSnippet = drawerViewModel::onDeleteSnippet
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Terminal",
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
                        .background(TerminalBackground),
                    contentPadding = paddingValues
                ) {
                    items(
                        items = terminalState.lines,
                        key = { line -> System.identityHashCode(line) }
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
                    onCtrlC = terminalViewModel::onCtrlC,
                    onSaveSnippet = {
                        drawerViewModel.onSaveSnippet(terminalState.inputText)
                    },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding()
                )
            }
        }
    }
}
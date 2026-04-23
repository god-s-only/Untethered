package com.untethered.app.data.process

import com.untethered.app.domain.model.CommandResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShizukuShellExecutor @Inject constructor() {

    private var currentProcess: Process? = null
    private var stdinWriter: BufferedWriter? = null

    fun execute(command: String): Flow<CommandResult> = callbackFlow {
        killCurrent()

        trySend(CommandResult.Running)

        val process = Shizuku.newProcess(
            arrayOf("sh", "-c", command),
            null,
            null
        )

        currentProcess = process
        stdinWriter = BufferedWriter(OutputStreamWriter(process.outputStream))

        val stdoutJob = launch(Dispatchers.IO) {
            process.inputStream.bufferedReader().forEachLine { line ->
                trySend(CommandResult.Output(line = line, isError = false))
            }
        }

        val stderrJob = launch(Dispatchers.IO) {
            process.errorStream.bufferedReader().forEachLine { line ->
                trySend(CommandResult.Output(line = line, isError = true))
            }
        }

        launch(Dispatchers.IO) {
            stdoutJob.join()
            stderrJob.join()
            val exitCode = process.waitFor()
            trySend(CommandResult.Exit(exitCode))
            currentProcess = null
            stdinWriter = null
            close()
        }

        kotlinx.coroutines.awaitCancellation()
    }.also {
        currentProcess?.destroy()
        currentProcess = null
        stdinWriter = null
    }

    suspend fun writeToStdin(input: String) {
        stdinWriter?.apply {
            write(input)
            newLine()
            flush()
        }
    }

    fun killCurrent() {
        currentProcess?.destroy()
        currentProcess = null
        stdinWriter = null
    }
}
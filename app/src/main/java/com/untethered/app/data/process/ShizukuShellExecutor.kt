package com.untethered.app.data.process

import com.untethered.app.domain.model.CommandResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import rikka.shizuku.ShizukuRemoteProcess
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShizukuShellExecutor @Inject constructor() {

    private var currentProcess: ShizukuRemoteProcess? = null
    private var stdinWriter: BufferedWriter? = null

    fun execute(command: String): Flow<CommandResult> = callbackFlow {
        killCurrent()
        trySend(CommandResult.Running)

        val process = try {
            createProcess(arrayOf("sh", "-c", command))
        } catch (e: Exception) {
            trySend(CommandResult.Output(line = "Failed to start: ${e.message}", isError = true))
            trySend(CommandResult.Exit(code = -1))
            close()
            return@callbackFlow
        }

        currentProcess = process
        stdinWriter = BufferedWriter(OutputStreamWriter(process.outputStream))

        try {
            coroutineScope {
                val stdoutJob = launch(Dispatchers.IO) {
                    try {
                        process.inputStream.bufferedReader().forEachLine { line ->
                            trySend(CommandResult.Output(line = line, isError = false))
                        }
                    } catch (_: Exception) {}
                }

                val stderrJob = launch(Dispatchers.IO) {
                    try {
                        process.errorStream.bufferedReader().forEachLine { line ->
                            trySend(CommandResult.Output(line = line, isError = true))
                        }
                    } catch (_: Exception) {}
                }

                launch(Dispatchers.IO) {
                    stdoutJob.join()
                    stderrJob.join()
                    val exitCode = try { process.waitFor() } catch (_: Exception) { -1 }
                    trySend(CommandResult.Exit(code = exitCode))
                    close()
                }
            }
        } finally {
            process.destroy()
            currentProcess = null
            stdinWriter = null
        }
    }

    private fun createProcess(command: Array<String>): ShizukuRemoteProcess {
        val clazz = Class.forName("rikka.shizuku.Shizuku")
        val method = clazz.getDeclaredMethod(
            "newProcess",
            Array<String>::class.java,
            Array<String>::class.java,
            String::class.java
        )
        method.isAccessible = true
        return method.invoke(null, command, null, null) as ShizukuRemoteProcess
    }

    suspend fun writeToStdin(input: String) {
        stdinWriter?.apply {
            write(input)
            newLine()
            flush()
        }
    }

    fun killCurrent() {
        try {
            currentProcess?.destroy()
        } catch (_: Exception) {
        } finally {
            currentProcess = null
            stdinWriter = null
        }
    }
}
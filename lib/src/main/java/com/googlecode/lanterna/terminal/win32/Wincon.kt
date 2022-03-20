package com.googlecode.lanterna.terminal.win32

import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.platform.win32.WinNT
import com.googlecode.lanterna.terminal.win32.WinDef.INPUT_RECORD
import com.sun.jna.Native
import com.sun.jna.ptr.IntByReference
import com.sun.jna.platform.win32.WinDef.LPVOID
import com.sun.jna.win32.W32APIOptions

interface Wincon : StdCallLibrary, com.sun.jna.platform.win32.Wincon {
    fun GetConsoleScreenBufferInfo(
        hConsoleOutput: WinNT.HANDLE,
        lpConsoleScreenBufferInfo: WinDef.CONSOLE_SCREEN_BUFFER_INFO
    ): Boolean

    fun ReadConsoleInput(
        hConsoleInput: WinNT.HANDLE,
        lpBuffer: Array<INPUT_RECORD>,
        nLength: Int,
        lpNumberOfEventsRead: IntByReference
    ): Boolean

    fun WriteConsole(
        hConsoleOutput: WinNT.HANDLE,
        lpBuffer: String,
        nNumberOfCharsToWrite: Int,
        lpNumberOfCharsWritten: IntByReference,
        lpReserved: LPVOID
    ): Boolean

    companion object {
        val INSTANCE = Native.load("kernel32", Wincon::class.java, W32APIOptions.UNICODE_OPTIONS)
        const val ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004
        const val DISABLE_NEWLINE_AUTO_RETURN = 0x0008
        const val ENABLE_VIRTUAL_TERMINAL_INPUT = 0x0200
    }
}
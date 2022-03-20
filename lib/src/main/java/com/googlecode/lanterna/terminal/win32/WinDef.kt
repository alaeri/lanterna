package com.googlecode.lanterna.terminal.win32

import com.sun.jna.Structure.FieldOrder
import com.googlecode.lanterna.terminal.win32.WinDef.KEY_EVENT_RECORD
import com.googlecode.lanterna.terminal.win32.WinDef.MOUSE_EVENT_RECORD
import com.googlecode.lanterna.terminal.win32.WinDef.WINDOW_BUFFER_SIZE_RECORD
import com.googlecode.lanterna.terminal.win32.WinDef.INPUT_RECORD
import com.sun.jna.Structure
import com.sun.jna.Union
import com.sun.jna.platform.win32.WinDef

interface WinDef : WinDef {
    /**
     * COORD structure
     */
    @FieldOrder("X", "Y")
    class COORD : Structure() {
        @JvmField
		var X: Short = 0
        @JvmField
		var Y: Short = 0
        override fun toString(): String {
            return String.format("COORD(%s,%s)", X, Y)
        }
    }

    /**
     * SMALL_RECT structure
     */
    @FieldOrder("Left", "Top", "Right", "Bottom")
    class SMALL_RECT : Structure() {
        @JvmField
		var Left: Short = 0
        @JvmField
		var Top: Short = 0
        @JvmField
		var Right: Short = 0
        @JvmField
		var Bottom: Short = 0
        override fun toString(): String {
            return String.format("SMALL_RECT(%s,%s)(%s,%s)", Left, Top, Right, Bottom)
        }
    }

    /**
     * CONSOLE_SCREEN_BUFFER_INFO structure
     */
    @FieldOrder("dwSize", "dwCursorPosition", "wAttributes", "srWindow", "dwMaximumWindowSize")
    class CONSOLE_SCREEN_BUFFER_INFO : Structure() {
        var dwSize: COORD? = null
        @JvmField
		var dwCursorPosition: COORD? = null
        var wAttributes: Short = 0
        @JvmField
		var srWindow: SMALL_RECT? = null
        var dwMaximumWindowSize: COORD? = null
        override fun toString(): String {
            return String.format(
                "CONSOLE_SCREEN_BUFFER_INFO(%s,%s,%s,%s,%s)",
                dwSize,
                dwCursorPosition,
                wAttributes,
                srWindow,
                dwMaximumWindowSize
            )
        }
    }

    @FieldOrder("EventType", "Event")
    class INPUT_RECORD : Structure() {
        @JvmField
		var EventType: Short = 0
        @JvmField
		var Event: EventA? = null

        class EventA : Union() {
            @JvmField
			var KeyEvent: KEY_EVENT_RECORD? = null
            @JvmField
			var MouseEvent: MOUSE_EVENT_RECORD? = null
            @JvmField
			var WindowBufferSizeEvent: WINDOW_BUFFER_SIZE_RECORD? = null
        }

        override fun read() {
            super.read()
            when (EventType) {
                KEY_EVENT -> Event!!.setType("KeyEvent")
                MOUSE_EVENT -> Event!!.setType("MouseEvent")
                WINDOW_BUFFER_SIZE_EVENT -> Event!!.setType("WindowBufferSizeEvent")
            }
            Event!!.read()
        }

        override fun toString(): String {
            return String.format("INPUT_RECORD(%s)", EventType)
        }

        companion object {
            const val KEY_EVENT: Short = 0x01
            const val MOUSE_EVENT: Short = 0x02
            const val WINDOW_BUFFER_SIZE_EVENT: Short = 0x04
        }
    }

    @FieldOrder(
        "bKeyDown",
        "wRepeatCount",
        "wVirtualKeyCode",
        "wVirtualScanCode",
        "uChar",
        "dwControlKeyState"
    )
    class KEY_EVENT_RECORD : Structure() {
        @JvmField
		var bKeyDown = false
        var wRepeatCount: Short = 0
        var wVirtualKeyCode: Short = 0
        var wVirtualScanCode: Short = 0
        @JvmField
		var uChar = 0.toChar()
        var dwControlKeyState = 0
        override fun toString(): String {
            return String.format(
                "KEY_EVENT_RECORD(%s,%s,%s,%s,%s,%s)",
                bKeyDown,
                wRepeatCount,
                wVirtualKeyCode,
                wVirtualKeyCode,
                wVirtualScanCode,
                uChar,
                dwControlKeyState
            )
        }
    }

    @FieldOrder("dwMousePosition", "dwButtonState", "dwControlKeyState", "dwEventFlags")
    class MOUSE_EVENT_RECORD : Structure() {
        var dwMousePosition: COORD? = null
        var dwButtonState = 0
        var dwControlKeyState = 0
        var dwEventFlags = 0
        override fun toString(): String {
            return String.format(
                "MOUSE_EVENT_RECORD(%s,%s,%s,%s)",
                dwMousePosition,
                dwButtonState,
                dwControlKeyState,
                dwEventFlags
            )
        }
    }

    @FieldOrder("dwSize")
    class WINDOW_BUFFER_SIZE_RECORD : Structure() {
        @JvmField
		var dwSize: COORD? = null
        override fun toString(): String {
            return String.format("WINDOW_BUFFER_SIZE_RECORD(%s)", dwSize)
        }
    }
}
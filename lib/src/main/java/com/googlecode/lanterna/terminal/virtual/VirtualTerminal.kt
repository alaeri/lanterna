/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.terminal.virtual

import com.googlecode.lanterna.terminal.IOSafeTerminal
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.terminal.virtual.VirtualTerminalListener
import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.terminal.virtual.VirtualTerminal.BufferWalker
import com.googlecode.lanterna.terminal.virtual.VirtualTerminal.BufferLine
import com.googlecode.lanterna.terminal.TerminalResizeListener

/**
 * A virtual terminal is a kind of terminal emulator implemented inside of Lanterna that exposes the Terminal interface
 * and maintains its state completely internally. The [VirtualTerminal] interface extends this interface and
 * allows you to query and modify its internals in a way you can not do with a regular terminal. The AWT and Swing
 * terminal implementations in Lanterna uses the [DefaultVirtualTerminal] class internally for keeping its state
 * and doing most of the terminal operations.
 */
interface VirtualTerminal : IOSafeTerminal {
    /**
     * Changes the "visible size" of the virtual terminal. This is the area at the bottom of the text buffer that is
     * considered the workable area since the cursor is restricted to this space. If you call this method with a size
     * that is different from the current size of the virtual terminal, the resize event will be fired on all listeners.
     * @param newSize New size of the virtual terminal
     */
    fun setTerminalSize(newSize: TerminalSize)

    /**
     * Adds a listener to receive notifications when certain events happens on the virtual terminal. Notice that this is
     * not the same as the list of [com.googlecode.lanterna.terminal.TerminalResizeListener], but as the
     * [VirtualTerminalListener] also allows you to listen on size changes, it can be used for the same purpose.
     * @param listener Listener to receive events from this virtual terminal
     */
    fun addVirtualTerminalListener(listener: VirtualTerminalListener)

    /**
     * Removes a listener from this virtual terminal so it will no longer receive events. Notice that this is not the
     * same as the list of [com.googlecode.lanterna.terminal.TerminalResizeListener].
     * @param listener Listener to remove from this virtual terminal
     */
    fun removeVirtualTerminalListener(listener: VirtualTerminalListener)

    /**
     * Sets the number of rows to allow in the non-private buffer above the viewport. The total size of the text buffer
     * will be `backlogSize + terminalSize.getRows()`. If set to 0, there is no scrollback. Please note that
     * private mode is unaffected by this and will always have no backlog.
     * @param backlogSize Number of rows of backlog
     */
    fun setBacklogSize(backlogSize: Int)

    /**
     * Checks if the terminal cursor is visible or not
     * @return `true` if the terminal cursor is visible, `false` otherwise
     */
    val isCursorVisible: Boolean

    /**
     * Adds a [KeyStroke] to the input queue of this virtual terminal. This even will be read the next time either
     * [.pollInput] or [.readInput] is called, assuming there are no other events before it in the queue.
     * @param keyStroke [KeyStroke] to add to the input queue of this virtual terminal
     */
    fun addInput(keyStroke: KeyStroke)

    /**
     * Returns the position of the terminal cursor where the row index is counted from the top of the text buffer,
     * including all backlog. This means, if there is 500 lines of backlog but the cursor position is set to 0x0, this
     * method will return 0x500. If you want to get the cursor's position in the viewport, please use
     * [.getCursorPosition] instead.
     * @return Cursor position as an offset from the top-left position of the text buffer including any backlog
     */
    val cursorBufferPosition: TerminalPosition

    /**
     * Returns a character from this virtual terminal, relative to the top-left position of the text buffer including
     * any backlog. If you want to get a character from the bottom viewport, please use
     * [.getCharacter] instead.
     *
     * @param position Position to get the character from
     * @return Text character at the specific position in the text buffer
     */
    fun getBufferCharacter(position: TerminalPosition): TextCharacter?

    /**
     * Returns a character from this virtual terminal, relative to the top-left position of the text buffer including
     * any backlog. If you want to get a character from the bottom viewport, please use
     * [.getCharacter] instead.
     *
     * @param column Column to get the character from
     * @param row Row, counting from the first line in the backlog, to get the character from
     * @return Text character at the specific position in the text buffer
     */
    fun getBufferCharacter(column: Int, row: Int): TextCharacter?

    /**
     * Returns a character from the viewport at the specified coordinates. This method cannot access the backlog, if you
     * want to fetch a character potentially from the backlog, please use [.getBufferCharacter]
     * instead.
     * @param position Position of the character to return
     * @return Text character at the specific position in the viewport
     */
    fun getCharacter(position: TerminalPosition): TextCharacter?

    /**
     * Returns a character from the viewport at the specified coordinates. This method cannot access the backlog, if you
     * want to fetch a character potentially from the backlog, please use [.getBufferCharacter]
     * instead.
     * @param column Column in the viewport to get the character from
     * @param row Row in the viewport to get the character form
     * @return Text character at the specific position in the viewport
     */
    fun getCharacter(column: Int, row: Int): TextCharacter?

    /**
     * Returns the number of lines in the entire text buffer, including any backlog
     * @return Number of lines in the buffer
     */
    val bufferLineCount: Int

    /**
     * Iterates over a range of lines in the text buffer
     * @param startRow Index of the first row of the iteration, counting 0 as the first row in the backlog
     * @param endRow Index of the last row of the iteration (inclusive), counting 0 as the first row in the backlog
     * @param bufferWalker Callback to invoke on each row in the iteration
     */
    fun forEachLine(startRow: Int, endRow: Int, bufferWalker: BufferWalker)

    /**
     * Interface used by [BufferWalker] to repressent a line in the text buffer when iterating over a range of
     * lines
     */
    interface BufferLine {
        /**
         * Returns a text character from this line in the specific column
         * @param column Column to return the text character from
         * @return Text character in the column of this line
         */
        fun getCharacterAt(column: Int): TextCharacter?
    }

    /**
     * Callback interface that is used by [.forEachLine] as a way to iterate over a range
     * of lines in the text buffer
     */
    interface BufferWalker {
        /**
         * Invoked separately on each line inside the specified range when calling
         * [.forEachLine]
         * @param rowNumber The row number of this invocation, where 0 means the first line of the backlog
         * @param bufferLine Object the repressents the line and its content on this row
         */
        fun onLine(rowNumber: Int, bufferLine: BufferLine)
    }
}
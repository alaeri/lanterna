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
package com.googlecode.lanterna.gui2

import com.googlecode.lanterna.graphics.Theme
import kotlin.Throws
import java.io.IOException
import com.googlecode.lanterna.gui2.TextGUIThread
import com.googlecode.lanterna.gui2.Interactable
import com.googlecode.lanterna.gui2.TextGUI
import com.googlecode.lanterna.gui2.TextGUIGraphics
import com.googlecode.lanterna.graphics.ThemedTextGraphics
import com.googlecode.lanterna.graphics.TextGraphics
import java.lang.IllegalArgumentException
import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.graphics.ThemeStyle
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.SGR
import java.util.EnumSet
import com.googlecode.lanterna.screen.TabBehaviour
import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.graphics.TextImage
import com.googlecode.lanterna.graphics.StyleSet
import java.lang.IllegalStateException
import java.lang.Runnable
import java.lang.InterruptedException
import java.lang.RuntimeException
import com.googlecode.lanterna.gui2.BasePane
import com.googlecode.lanterna.gui2.WindowBasedTextGUI
import com.googlecode.lanterna.TerminalRectangle
import com.googlecode.lanterna.gui2.WindowPostRenderer
import com.googlecode.lanterna.gui2.WindowManager
import com.googlecode.lanterna.gui2.BasePaneListener
import com.googlecode.lanterna.gui2.WindowDecorationRenderer

/**
 * Class that represents the thread this is expected to run the event/input/update loop for the `TextGUI`. There
 * are mainly two implementations of this interface, one for having lanterna automatically spawn a new thread for doing
 * all the processing and leaving the creator thread free to do other things, and one that assumes the creator thread
 * will hand over control to lanterna for as long as the GUI is running.
 * @see SameTextGUIThread
 *
 * @see SeparateTextGUIThread
 *
 * @author Martin
 */
interface TextGUIThread {
    /**
     * Invokes custom code on the GUI thread. Even if the current thread **is** the GUI thread, the code will be
     * executed at a later time when the event processing is done.
     *
     * @param runnable Code to run asynchronously
     * @throws java.lang.IllegalStateException If the GUI thread is not running
     */
    @Throws(IllegalStateException::class)
    fun invokeLater(runnable: Runnable?)

    /**
     * Main method to call when you are managing the event/input/update loop yourself. This method will run one round
     * through the GUI's event/input queue and update the visuals if required. If the operation did nothing (returning
     * `false`) you could sleep for a millisecond and then try again. If you use `SameTextGUIThread` you
     * must either call this method directly to make the GUI update or use one of the methods on
     * `WindowBasedTextGUI` that blocks until a particular window has closed.
     * @return `true` if there was anything to process or the GUI was updated, otherwise `false`
     * @throws IOException If there was an I/O error when processing and updating the GUI
     */
    @Throws(IOException::class)
    fun processEventsAndUpdate(): Boolean

    /**
     * Schedules custom code to be executed on the GUI thread and waits until the code has been executed before
     * returning. If this is run on the GUI thread, it will immediately run the `Runnable` and then return.
     *
     * @param runnable Code to be run and waited for completion before this method returns
     * @throws IllegalStateException If the GUI thread is not running
     * @throws InterruptedException If the caller thread was interrupted while waiting for the task to be executed
     */
    @Throws(IllegalStateException::class, InterruptedException::class)
    fun invokeAndWait(runnable: Runnable?)

    /**
     * Updates the exception handler used by this TextGUIThread. The exception handler will be invoked when an exception
     * occurs in the main event loop. You can then decide how to log this exception and if you want to terminate the
     * thread or not.
     * @param exceptionHandler Handler to inspect exceptions
     */
    fun setExceptionHandler(exceptionHandler: ExceptionHandler?)

    /**
     * Returns the Java thread which is processing GUI events and updating the screen
     * @return Thread which is processing events and updating the screen
     */
    val thread: Thread?

    /**
     * This interface defines an exception handler, that is used for looking at exceptions that occurs during the main
     * event loop of the TextGUIThread. You can for example use this for logging, but also decide if you want the
     * exception to kill the thread.
     */
    interface ExceptionHandler {
        /**
         * Will be called when an IOException has occurred in the main event thread
         * @param e IOException that occurred
         * @return If you return `true`, the event thread will be terminated
         */
        fun onIOException(e: IOException?): Boolean

        /**
         * Will be called when a RuntimeException has occurred in the main event thread
         * @param e RuntimeException that occurred
         * @return If you return `true`, the event thread will be terminated
         */
        fun onRuntimeException(e: RuntimeException?): Boolean
    }
}
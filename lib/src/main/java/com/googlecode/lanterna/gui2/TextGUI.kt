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
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.screen.Screen
import java.io.IOException

/**
 * This is the base interface for advanced text GUIs supported in Lanterna. You may want to use this in combination with
 * a TextGUIThread, that can be created/retrieved by using `getGUIThread()`.
 * @author Martin
 */
interface TextGUI {
    /**
     * Returns the theme currently assigned to this [TextGUI]
     * @return Currently active [Theme]
     */
    /**
     * Sets the global theme to be used by this TextGUI. This value will be set on every TextGUIGraphics object created
     * for drawing the GUI, but individual components can override this if they want. If you don't call this method
     * you should assume that a default theme is assigned by the library.
     * @param theme Theme to use as the default theme for this TextGUI
     */
    var theme: Theme?

    /**
     * Drains the input queue and passes the key strokes to the GUI system for processing. For window-based system, it
     * will send each key stroke to the active window for processing. If the input read gives an EOF, it will throw
     * EOFException and this is normally the signal to shut down the GUI (any command coming in before the EOF will be
     * processed as usual before this).
     * @return `true` if at least one key stroke was read and processed, `false` if there was nothing on the
     * input queue (only for non-blocking IO)
     * @throws java.io.IOException In case there was an underlying I/O error
     * @throws java.io.EOFException In the input stream received an EOF marker
     */
    @Throws(IOException::class)
    fun processInput(): Boolean

    /**
     * Returns the [Screen] for this [WindowBasedTextGUI]
     * @return the [Screen] used by this [WindowBasedTextGUI]
     */
    val screen: Screen?

    /**
     * Updates the screen, to make any changes visible to the user.
     * @throws java.io.IOException In case there was an underlying I/O error
     */
    @Throws(IOException::class)
    fun updateScreen()

    /**
     * This method can be used to determine if any component has requested a redraw. If this method returns
     * `true`, you may want to call `updateScreen()`.
     * @return `true` if this TextGUI has a change and is waiting for someone to call `updateScreen()`
     */
    val isPendingUpdate: Boolean

    /**
     * This method controls whether or not the virtual screen should be used. This is what enabled you to make your UI
     * larger than what fits the terminal, as it will expand the virtual area and put in scrollbars. If set to
     * `false`, the virtual screen will be bypassed and any content outside of the screen will be cropped. This
     * property is `true` by default.
     * @param virtualScreenEnabled If `true`, then virtual screen will be used, otherwise it is bypassed
     */
    fun setVirtualScreenEnabled(virtualScreenEnabled: Boolean)

    /**
     * The first time this method is called, it will create a new TextGUIThread object that you can use to automatically
     * manage this TextGUI instead of manually calling `processInput()` and `updateScreen()`. After the
     * initial call, it will return the same object as it was originally returning.
     * @return A `TextGUIThread` implementation that can be used to asynchronously manage the GUI
     */
    val gUIThread: TextGUIThread?

    /**
     * Returns the interactable component currently in focus
     * @return Component that is currently in input focus
     */
    val focusedInteractable: Interactable?

    /**
     * Adds a listener to this TextGUI to fire events on.
     * @param listener Listener to add
     */
    fun addListener(listener: Listener)

    /**
     * Removes a listener from this TextGUI so that it will no longer receive events
     * @param listener Listener to remove
     */
    fun removeListener(listener: Listener)

    /**
     * Listener interface for TextGUI, firing on events related to the overall GUI
     */
    interface Listener {
        /**
         * Fired either when no component was in focus during a keystroke or if the focused component and all its parent
         * containers chose not to handle the event. This event listener should also return `true` if the event
         * was processed in any way that requires the TextGUI to update itself, otherwise `false`.
         * @param textGUI TextGUI that had the event
         * @param keyStroke Keystroke that was unhandled
         * @return If the outcome of this KeyStroke processed by the implementer requires the TextGUI to re-draw, return
         * `true` here, otherwise `false`
         */
        fun onUnhandledKeyStroke(textGUI: TextGUI, keyStroke: KeyStroke): Boolean
    }
}
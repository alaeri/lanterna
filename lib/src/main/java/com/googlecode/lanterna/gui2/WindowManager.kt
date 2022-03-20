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
 * Window manager is a class that is plugged in to a `WindowBasedTextGUI` to manage the position and placement
 * of windows. The window manager doesn't contain the list of windows so it normally does not need to maintain much
 * state but it is passed all required objects as the window model changes.
 * @see DefaultWindowManager
 *
 * @author Martin
 */
interface WindowManager {
    /**
     * Will be polled by the the [WindowBasedTextGUI] to see if the window manager believes an update is required.
     * For example, it could be that there is no input, no events and none of the components are invalid, but the window
     * manager decides for some other reason that the GUI needs to be updated, in that case you should return
     * `true` here. Please note that returning `false` will not prevent updates from happening, it's just
     * stating that the window manager isn't aware of some internal state change that would require an update.
     * @return `true` if the window manager believes the GUI needs to be update, `false` otherwise
     */
    val isInvalid: Boolean

    /**
     * Returns the `WindowDecorationRenderer` for a particular window
     * @param window Window to get the decoration renderer for
     * @return `WindowDecorationRenderer` for the window
     */
    fun getWindowDecorationRenderer(window: Window): WindowDecorationRenderer?

    /**
     * Called whenever a window is added to the `WindowBasedTextGUI`. This gives the window manager an opportunity
     * to setup internal state, if required, or decide on an initial position of the window
     * @param textGUI GUI that the window was added too
     * @param window Window that was added
     * @param allWindows All windows, including the new window, in the GUI
     */
    fun onAdded(textGUI: WindowBasedTextGUI, window: Window, allWindows: List<Window>)

    /**
     * Called whenever a window is removed from a `WindowBasedTextGUI`. This gives the window manager an
     * opportunity to clear internal state if needed.
     * @param textGUI GUI that the window was removed from
     * @param window Window that was removed
     * @param allWindows All windows, excluding the removed window, in the GUI
     */
    fun onRemoved(textGUI: WindowBasedTextGUI, window: Window, allWindows: List<Window>)

    /**
     * Called by the GUI system before iterating through all windows during the drawing process. The window manager
     * should ensure the position and decorated size of all windows at this point by using
     * `Window.setPosition(..)` and `Window.setDecoratedSize(..)`. Be sure to inspect the window hints
     * assigned to the window, in case you want to try to honour them. Use the
     * [.getWindowDecorationRenderer] method to get the currently assigned window decoration rendering
     * class which can tell you the decorated size of a window given it's content size.
     *
     * @param textGUI Text GUI that is about to draw the windows
     * @param allWindows All windows that are going to be drawn, in the order they will be drawn
     * @param screenSize Size of the terminal that is available to draw on
     */
    fun prepareWindows(
        textGUI: WindowBasedTextGUI,
        allWindows: List<Window>,
        screenSize: TerminalSize
    )
}
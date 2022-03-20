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
 * Extended [BasePaneListener] for [Window] that exposes additional events that are specific to windows
 */
interface WindowListener : BasePaneListener<Window> {
    /**
     * Called whenever the window's size has changed, no matter if it was done by the window manager or the user
     * @param window Window that was resized
     * @param oldSize Previous size of the window
     * @param newSize New size of the window
     */
    fun onResized(window: Window, oldSize: TerminalSize?, newSize: TerminalSize)

    /**
     * Called whenever the window's position has changed, no matter if it was done by the window manager or the user
     * @param window Window that was repositioned
     * @param oldPosition Previous position of the window
     * @param newPosition New position of the window
     */
    fun onMoved(window: Window, oldPosition: TerminalPosition?, newPosition: TerminalPosition)
}
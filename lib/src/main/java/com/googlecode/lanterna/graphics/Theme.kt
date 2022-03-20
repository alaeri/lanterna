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
package com.googlecode.lanterna.graphics

import kotlin.Throws
import java.io.IOException
import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.graphics.StyleSet
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.SGR
import java.util.EnumSet
import java.util.Arrays
import com.googlecode.lanterna.graphics.TextGraphics
import java.lang.IllegalArgumentException
import com.googlecode.lanterna.screen.TabBehaviour
import com.googlecode.lanterna.graphics.TextImage
import com.googlecode.lanterna.graphics.ThemeStyle
import com.googlecode.lanterna.graphics.ThemedTextGraphics
import com.googlecode.lanterna.gui2.ComponentRenderer
import com.googlecode.lanterna.graphics.ThemeDefinition
import com.googlecode.lanterna.gui2.WindowPostRenderer
import com.googlecode.lanterna.gui2.WindowDecorationRenderer

/**
 * The main theme interface, from which you can retrieve theme definitions
 * @author Martin
 */
interface Theme {
    /**
     * Returns what this theme considers to be the default definition
     * @return The default theme definition
     */
    val defaultDefinition: ThemeDefinition

    /**
     * Returns the theme definition associated with this class. The implementation of Theme should ensure that this
     * call never returns `null`, it should always give back a valid value (falling back to the default is nothing
     * else can be used).
     * @param clazz Class to get the theme definition for
     * @return The ThemeDefinition for the class passed in
     */
    fun getDefinition(clazz: Class<*>): ThemeDefinition

    /**
     * Returns a post-renderer to invoke after drawing each window, unless the GUI system or individual windows has
     * their own renderers set. If `null`, no post-renderer will be done (unless the GUI system or the windows
     * has a post-renderer).
     * @return A [com.googlecode.lanterna.gui2.WindowPostRenderer] to invoke after drawing each window unless
     * overridden, or `null` if none
     */
    val windowPostRenderer: WindowPostRenderer

    /**
     * Returns the [WindowDecorationRenderer] to use for windows drawn in this theme. If `null` then
     * lanterna will fall back to use [com.googlecode.lanterna.gui2.DefaultWindowDecorationRenderer].
     *
     * @return The decoration renderer to use for this theme, or `null` to use system default
     */
    val windowDecorationRenderer: WindowDecorationRenderer
}
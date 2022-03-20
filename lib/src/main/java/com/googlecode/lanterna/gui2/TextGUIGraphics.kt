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
 * TextGraphics implementation used by TextGUI when doing any drawing operation.
 * @author Martin
 */
interface TextGUIGraphics : ThemedTextGraphics {
    /**
     * Returns the `TextGUI` this `TextGUIGraphics` belongs to
     * @return `TextGUI` this `TextGUIGraphics` belongs to
     */
    val textGUI: TextGUI?
    @Throws(IllegalArgumentException::class)
    override fun newTextGraphics(
        topLeftCorner: TerminalPosition,
        size: TerminalSize
    ): TextGUIGraphics

    override fun applyThemeStyle(themeStyle: ThemeStyle?): TextGUIGraphics
    override fun setBackgroundColor(backgroundColor: TextColor?): TextGUIGraphics
    override fun setForegroundColor(foregroundColor: TextColor?): TextGUIGraphics
    override fun enableModifiers(vararg modifiers: SGR): TextGUIGraphics
    override fun disableModifiers(vararg modifiers: SGR): TextGUIGraphics
    override fun setModifiers(modifiers: EnumSet<SGR>): TextGUIGraphics
    override fun clearModifiers():TextGUIGraphics
    override fun setTabBehaviour(tabBehaviour: TabBehaviour):TextGUIGraphics
    override fun fill(c: Char):TextGUIGraphics
    override fun fillRectangle(
        topLeft: TerminalPosition,
        size: TerminalSize,
        character: Char
    ):TextGUIGraphics

    override fun fillRectangle(
        topLeft: TerminalPosition,
        size: TerminalSize,
        character: TextCharacter
    ):TextGUIGraphics

    override fun drawRectangle(
        topLeft: TerminalPosition,
        size: TerminalSize,
        character: Char
    ):TextGUIGraphics

    override fun drawRectangle(
        topLeft: TerminalPosition,
        size: TerminalSize,
        character: TextCharacter
    ):TextGUIGraphics

    override fun fillTriangle(
        p1: TerminalPosition,
        p2: TerminalPosition,
        p3: TerminalPosition,
        character: Char
    ):TextGUIGraphics

    override fun fillTriangle(
        p1: TerminalPosition,
        p2: TerminalPosition,
        p3: TerminalPosition,
        character: TextCharacter
    ):TextGUIGraphics

    override fun drawTriangle(
        p1: TerminalPosition,
        p2: TerminalPosition,
        p3: TerminalPosition,
        character: Char
    ):TextGUIGraphics

    override fun drawTriangle(
        p1: TerminalPosition,
        p2: TerminalPosition,
        p3: TerminalPosition,
        character: TextCharacter
    ):TextGUIGraphics

    override fun drawLine(
        fromPoint: TerminalPosition,
        toPoint: TerminalPosition,
        character: Char
    ):TextGUIGraphics

    override fun drawLine(
        fromPoint: TerminalPosition,
        toPoint: TerminalPosition,
        character: TextCharacter
    ):TextGUIGraphics

    override fun drawLine(
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int,
        character: Char
    ):TextGUIGraphics

    override fun drawLine(
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int,
        character: TextCharacter
    ):TextGUIGraphics

    override fun drawImage(topLeft: TerminalPosition, image: TextImage):TextGUIGraphics
    override fun drawImage(
        topLeft: TerminalPosition,
        image: TextImage,
        sourceImageTopLeft: TerminalPosition,
        sourceImageSize: TerminalSize
    ):TextGUIGraphics

    override fun setCharacter(position: TerminalPosition, character: Char): TextGUIGraphics
    override fun setCharacter(
        position: TerminalPosition,
        character: TextCharacter
    ):TextGUIGraphics

    override fun setCharacter(column: Int, row: Int, character: Char):TextGUIGraphics
    override fun setCharacter(column: Int, row: Int, character: TextCharacter):TextGUIGraphics
    override fun putString(column: Int, row: Int, string: String):TextGUIGraphics
    override fun putString(position: TerminalPosition, string: String):TextGUIGraphics
    override fun putString(
        column: Int,
        row: Int,
        string: String,
        extraModifier: SGR,
        vararg optionalExtraModifiers: SGR
    ):TextGUIGraphics

    override fun putString(
        position: TerminalPosition,
        string: String,
        extraModifier: SGR,
        vararg optionalExtraModifiers: SGR
    ):TextGUIGraphics

    override fun putString(
        column: Int,
        row: Int,
        string: String,
        extraModifiers: Collection<SGR>
    ):TextGUIGraphics

    override fun putCSIStyledString(column: Int, row: Int, string: String):TextGUIGraphics
    override fun putCSIStyledString(position: TerminalPosition, string: String):TextGUIGraphics
    override fun setStyleFrom(source: StyleSet<*>):TextGUIGraphics
}
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
package com.googlecode.lanterna.terminal.swing

import com.googlecode.lanterna.terminal.swing.TerminalScrollController

/**
 * This interface can be used to control the backlog scrolling of a SwingTerminal. It's used as a callback by the
 * `SwingTerminal` when it needs to fetch the scroll position and also used whenever the backlog changes to that
 * some view class, like a scrollbar for example, can update its view accordingly.
 * @author Martin
 */
interface TerminalScrollController {
    /**
     * Called by the SwingTerminal when the terminal has changed or more lines are entered into the terminal
     * @param totalSize Total number of lines in the backlog currently
     * @param screenSize Number of lines covered by the terminal window at its current size
     */
    fun updateModel(totalSize: Int, screenSize: Int)

    /**
     * Called by the SwingTerminal to know the 'offset' into the backlog. Returning 0 here will always draw the latest
     * lines; if you return 5, it will draw from five lines into the backlog and skip the 5 most recent lines.
     * @return According to this scroll controller, how far back into the backlog are we?
     */
    val scrollingOffset: Int

    /**
     * Implementation of [TerminalScrollController] that does nothing
     */
    class Null : TerminalScrollController {
        override fun updateModel(totalSize: Int, screenSize: Int) {}
        override val scrollingOffset: Int = 0
    }
}
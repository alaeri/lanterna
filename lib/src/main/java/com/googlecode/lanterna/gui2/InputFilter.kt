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

import com.googlecode.lanterna.input.KeyStroke

/**
 * This interface can be used to programmatically intercept input from the user and decide if the input should be passed
 * on to the interactable. It's also possible to fire custom actions for certain keystrokes.
 */
interface InputFilter {
    /**
     * Called when the component is about to receive input from the user and decides if the input should be passed on to
     * the component or not
     * @param interactable Interactable that the input is directed to
     * @param keyStroke User input
     * @return `true` if the input should be passed on to the interactable, `false` otherwise
     */
    fun onInput(interactable: Interactable, keyStroke: KeyStroke): Boolean
}
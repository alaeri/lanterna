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

import com.googlecode.lanterna.SGR
import com.googlecode.lanterna.TextColor
import java.util.*

/**
 * ThemeStyle is the lowest entry in the theme hierarchy, containing the actual colors and SGRs to use. When drawing a
 * component, you would pick out a [ThemeDefinition] that applies to the whole component and then choose to
 * activate individual [ThemeStyle]s when drawing the different parts of the component.
 * @author Martin
 */
interface ThemeStyle {
    /**
     * Returns the foreground color associated with this style
     * @return foreground color associated with this style
     */
    val foreground: TextColor?

    /**
     * Returns the background color associated with this style
     * @return background color associated with this style
     */
    val background: TextColor?

    /**
     * Returns the set of SGR flags associated with this style. This `EnumSet` is either unmodifiable or a copy so
     * altering it will not change the theme in any way.
     * @return SGR flags associated with this style
     */
    val sGRs: EnumSet<SGR?>?
}
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
package com.googlecode.lanterna

import java.awt.Color
import com.googlecode.lanterna.TextColor
import java.lang.IllegalArgumentException
import com.googlecode.lanterna.TextColor.RGB
import java.io.Serializable
import java.util.Locale
import java.util.regex.Pattern

/**
 * This is an abstract base class for terminal color definitions. Since there are different ways of specifying terminal
 * colors, all with a different range of adoptions, this makes it possible to program an API against an implementation-
 * agnostic color definition. Please remember when using colors that not all terminals and terminal emulators supports
 * them. The 24-bit color mode is very unsupported, for example, and even the default Linux terminal doesn't support
 * the 256-color indexed mode.
 *
 * @author Martin
 */
interface TextColor : Serializable {
    /**
     * Returns the byte sequence in between CSI and character 'm' that is used to enable this color as the foreground
     * color on an ANSI-compatible terminal.
     * @return Byte array out data to output in between of CSI and 'm'
     */
    val foregroundSGRSequence: ByteArray?

    /**
     * Returns the byte sequence in between CSI and character 'm' that is used to enable this color as the background
     * color on an ANSI-compatible terminal.
     * @return Byte array out data to output in between of CSI and 'm'
     */
    val backgroundSGRSequence: ByteArray?

    /**
     * @return Red intensity of this color, from 0 to 255
     */
    val red: Int

    /**
     * @return Green intensity of this color, from 0 to 255
     */
    val green: Int

    /**
     * @return Blue intensity of this color, from 0 to 255
     */
    val blue: Int

    /**
     * Converts this color to an AWT color object, assuming a standard VGA palette.
     * @return TextColor as an AWT Color
     */
    @Deprecated(
        """This adds a runtime dependency to the java.desktop module which isn't declared in the module
      descriptor of lanterna. If you want to call this method, make sure to add it to your module."""
    )
    fun toColor(): Color

    /**
     * This class represent classic ANSI colors that are likely to be very compatible with most terminal
     * implementations. It is limited to 8 colors (plus the 'default' color) but as a norm, using bold mode (SGR code)
     * will slightly alter the color, giving it a bit brighter tone, so in total this will give you 16 (+1) colors.
     *
     *
     * For more information, see http://en.wikipedia.org/wiki/File:Ansi.png
     */
    enum class ANSI(
        index: Int,
        val isBright: Boolean,
        override val red: Int,
        override val green: Int,
        override val blue: Int
    ) : TextColor {
        BLACK(0, 0, 0, 0), RED(1, 170, 0, 0), GREEN(2, 0, 170, 0), YELLOW(3, 170, 85, 0), BLUE(
            4,
            0,
            0,
            170
        ),
        MAGENTA(5, 170, 0, 170), CYAN(6, 0, 170, 170), WHITE(7, 170, 170, 170), DEFAULT(
            9,
            0,
            0,
            0
        ),
        BLACK_BRIGHT(0, true, 85, 85, 85), RED_BRIGHT(1, true, 255, 85, 85), GREEN_BRIGHT(
            2,
            true,
            85,
            255,
            85
        ),
        YELLOW_BRIGHT(3, true, 255, 255, 85), BLUE_BRIGHT(4, true, 85, 85, 255), MAGENTA_BRIGHT(
            5,
            true,
            255,
            85,
            255
        ),
        CYAN_BRIGHT(6, true, 85, 255, 255), WHITE_BRIGHT(7, true, 255, 255, 255);

        private val foregroundSGR: ByteArray
        private val backgroundSGR: ByteArray

        constructor(index: Int, red: Int, green: Int, blue: Int) : this(
            index,
            false,
            red,
            green,
            blue
        ) {
        }

        override val foregroundSGRSequence: ByteArray?
            get() =  foregroundSGR.clone()
        override val backgroundSGRSequence: ByteArray?
            get() =  backgroundSGR.clone()

        override fun toColor(): Color {
            return Color(red, green, blue)
        }

        init {
            foregroundSGR = String.format("%d%d", if (isBright) 9 else 3, index).toByteArray()
            backgroundSGR = String.format("%d%d", if (isBright) 10 else 4, index).toByteArray()
        }
    }

    /**
     * This class represents a color expressed in the indexed XTerm 256 color extension, where each color is defined in a
     * lookup-table. All in all, there are 256 codes, but in order to know which one to know you either need to have the
     * table at hand, or you can use the two static helper methods which can help you convert from three 8-bit
     * RGB values to the closest approximate indexed color number. If you are interested, the 256 index values are
     * actually divided like this:<br></br>
     * 0 .. 15 - System colors, same as ANSI, but the actual rendered color depends on the terminal emulators color scheme<br></br>
     * 16 .. 231 - Forms a 6x6x6 RGB color cube<br></br>
     * 232 .. 255 - A gray scale ramp (without black and white endpoints)<br></br>
     *
     *
     * Support for indexed colors is somewhat widely adopted, not as much as the ANSI colors (TextColor.ANSI) but more
     * than the RGB (TextColor.RGB).
     *
     *
     * For more details on this, please see [
 * this](https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors) commit message to Konsole.
     */
    class Indexed(colorIndex: Int) : TextColor {
        private val colorIndex: Int

        override val foregroundSGRSequence: ByteArray?
            get() = "38;5;$colorIndex".toByteArray()
        override val backgroundSGRSequence: ByteArray?
            get() = "48;5;$colorIndex".toByteArray()

        override val red: Int
            get() = COLOR_TABLE[colorIndex][0].toInt() and 0x000000ff

        override val green: Int
            get() = COLOR_TABLE[colorIndex][1].toInt() and 0x000000ff

        override val blue: Int
            get() = COLOR_TABLE[colorIndex][2].toInt() and 0x000000ff

        override fun toColor(): Color {
            return Color(red, green, blue)
        }

        override fun toString(): String {
            return "{IndexedColor:$colorIndex}"
        }

        override fun hashCode(): Int {
            var hash = 3
            hash = 43 * hash + colorIndex
            return hash
        }

        override fun equals(obj: Any?): Boolean {
            if (obj == null) {
                return false
            }
            if (javaClass != obj.javaClass) {
                return false
            }
            val other = obj as Indexed
            return colorIndex == other.colorIndex
        }

        companion object {
            private val COLOR_TABLE = arrayOf(
                byteArrayOf(0.toByte(), 0.toByte(), 0.toByte()),
                byteArrayOf(170.toByte(), 0.toByte(), 0.toByte()),
                byteArrayOf(0.toByte(), 170.toByte(), 0.toByte()),
                byteArrayOf(170.toByte(), 85.toByte(), 0.toByte()),
                byteArrayOf(0.toByte(), 0.toByte(), 170.toByte()),
                byteArrayOf(170.toByte(), 0.toByte(), 170.toByte()),
                byteArrayOf(0.toByte(), 170.toByte(), 170.toByte()),
                byteArrayOf(170.toByte(), 170.toByte(), 170.toByte()),
                byteArrayOf(85.toByte(), 85.toByte(), 85.toByte()),
                byteArrayOf(255.toByte(), 85.toByte(), 85.toByte()),
                byteArrayOf(85.toByte(), 255.toByte(), 85.toByte()),
                byteArrayOf(255.toByte(), 255.toByte(), 85.toByte()),
                byteArrayOf(85.toByte(), 85.toByte(), 255.toByte()),
                byteArrayOf(255.toByte(), 85.toByte(), 255.toByte()),
                byteArrayOf(85.toByte(), 255.toByte(), 255.toByte()),
                byteArrayOf(255.toByte(), 255.toByte(), 255.toByte()),
                byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
                byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x5f.toByte()),
                byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x87.toByte()),
                byteArrayOf(0x00.toByte(), 0x00.toByte(), 0xaf.toByte()),
                byteArrayOf(0x00.toByte(), 0x00.toByte(), 0xd7.toByte()),
                byteArrayOf(0x00.toByte(), 0x00.toByte(), 0xff.toByte()),
                byteArrayOf(0x00.toByte(), 0x5f.toByte(), 0x00.toByte()),
                byteArrayOf(0x00.toByte(), 0x5f.toByte(), 0x5f.toByte()),
                byteArrayOf(0x00.toByte(), 0x5f.toByte(), 0x87.toByte()),
                byteArrayOf(0x00.toByte(), 0x5f.toByte(), 0xaf.toByte()),
                byteArrayOf(0x00.toByte(), 0x5f.toByte(), 0xd7.toByte()),
                byteArrayOf(0x00.toByte(), 0x5f.toByte(), 0xff.toByte()),
                byteArrayOf(0x00.toByte(), 0x87.toByte(), 0x00.toByte()),
                byteArrayOf(0x00.toByte(), 0x87.toByte(), 0x5f.toByte()),
                byteArrayOf(0x00.toByte(), 0x87.toByte(), 0x87.toByte()),
                byteArrayOf(0x00.toByte(), 0x87.toByte(), 0xaf.toByte()),
                byteArrayOf(0x00.toByte(), 0x87.toByte(), 0xd7.toByte()),
                byteArrayOf(0x00.toByte(), 0x87.toByte(), 0xff.toByte()),
                byteArrayOf(0x00.toByte(), 0xaf.toByte(), 0x00.toByte()),
                byteArrayOf(0x00.toByte(), 0xaf.toByte(), 0x5f.toByte()),
                byteArrayOf(0x00.toByte(), 0xaf.toByte(), 0x87.toByte()),
                byteArrayOf(0x00.toByte(), 0xaf.toByte(), 0xaf.toByte()),
                byteArrayOf(0x00.toByte(), 0xaf.toByte(), 0xd7.toByte()),
                byteArrayOf(0x00.toByte(), 0xaf.toByte(), 0xff.toByte()),
                byteArrayOf(0x00.toByte(), 0xd7.toByte(), 0x00.toByte()),
                byteArrayOf(0x00.toByte(), 0xd7.toByte(), 0x5f.toByte()),
                byteArrayOf(0x00.toByte(), 0xd7.toByte(), 0x87.toByte()),
                byteArrayOf(0x00.toByte(), 0xd7.toByte(), 0xaf.toByte()),
                byteArrayOf(0x00.toByte(), 0xd7.toByte(), 0xd7.toByte()),
                byteArrayOf(0x00.toByte(), 0xd7.toByte(), 0xff.toByte()),
                byteArrayOf(0x00.toByte(), 0xff.toByte(), 0x00.toByte()),
                byteArrayOf(0x00.toByte(), 0xff.toByte(), 0x5f.toByte()),
                byteArrayOf(0x00.toByte(), 0xff.toByte(), 0x87.toByte()),
                byteArrayOf(0x00.toByte(), 0xff.toByte(), 0xaf.toByte()),
                byteArrayOf(0x00.toByte(), 0xff.toByte(), 0xd7.toByte()),
                byteArrayOf(0x00.toByte(), 0xff.toByte(), 0xff.toByte()),
                byteArrayOf(0x5f.toByte(), 0x00.toByte(), 0x00.toByte()),
                byteArrayOf(0x5f.toByte(), 0x00.toByte(), 0x5f.toByte()),
                byteArrayOf(0x5f.toByte(), 0x00.toByte(), 0x87.toByte()),
                byteArrayOf(0x5f.toByte(), 0x00.toByte(), 0xaf.toByte()),
                byteArrayOf(0x5f.toByte(), 0x00.toByte(), 0xd7.toByte()),
                byteArrayOf(0x5f.toByte(), 0x00.toByte(), 0xff.toByte()),
                byteArrayOf(0x5f.toByte(), 0x5f.toByte(), 0x00.toByte()),
                byteArrayOf(0x5f.toByte(), 0x5f.toByte(), 0x5f.toByte()),
                byteArrayOf(0x5f.toByte(), 0x5f.toByte(), 0x87.toByte()),
                byteArrayOf(0x5f.toByte(), 0x5f.toByte(), 0xaf.toByte()),
                byteArrayOf(0x5f.toByte(), 0x5f.toByte(), 0xd7.toByte()),
                byteArrayOf(0x5f.toByte(), 0x5f.toByte(), 0xff.toByte()),
                byteArrayOf(0x5f.toByte(), 0x87.toByte(), 0x00.toByte()),
                byteArrayOf(0x5f.toByte(), 0x87.toByte(), 0x5f.toByte()),
                byteArrayOf(0x5f.toByte(), 0x87.toByte(), 0x87.toByte()),
                byteArrayOf(0x5f.toByte(), 0x87.toByte(), 0xaf.toByte()),
                byteArrayOf(0x5f.toByte(), 0x87.toByte(), 0xd7.toByte()),
                byteArrayOf(0x5f.toByte(), 0x87.toByte(), 0xff.toByte()),
                byteArrayOf(0x5f.toByte(), 0xaf.toByte(), 0x00.toByte()),
                byteArrayOf(0x5f.toByte(), 0xaf.toByte(), 0x5f.toByte()),
                byteArrayOf(0x5f.toByte(), 0xaf.toByte(), 0x87.toByte()),
                byteArrayOf(0x5f.toByte(), 0xaf.toByte(), 0xaf.toByte()),
                byteArrayOf(0x5f.toByte(), 0xaf.toByte(), 0xd7.toByte()),
                byteArrayOf(0x5f.toByte(), 0xaf.toByte(), 0xff.toByte()),
                byteArrayOf(0x5f.toByte(), 0xd7.toByte(), 0x00.toByte()),
                byteArrayOf(0x5f.toByte(), 0xd7.toByte(), 0x5f.toByte()),
                byteArrayOf(0x5f.toByte(), 0xd7.toByte(), 0x87.toByte()),
                byteArrayOf(0x5f.toByte(), 0xd7.toByte(), 0xaf.toByte()),
                byteArrayOf(0x5f.toByte(), 0xd7.toByte(), 0xd7.toByte()),
                byteArrayOf(0x5f.toByte(), 0xd7.toByte(), 0xff.toByte()),
                byteArrayOf(0x5f.toByte(), 0xff.toByte(), 0x00.toByte()),
                byteArrayOf(0x5f.toByte(), 0xff.toByte(), 0x5f.toByte()),
                byteArrayOf(0x5f.toByte(), 0xff.toByte(), 0x87.toByte()),
                byteArrayOf(0x5f.toByte(), 0xff.toByte(), 0xaf.toByte()),
                byteArrayOf(0x5f.toByte(), 0xff.toByte(), 0xd7.toByte()),
                byteArrayOf(0x5f.toByte(), 0xff.toByte(), 0xff.toByte()),
                byteArrayOf(0x87.toByte(), 0x00.toByte(), 0x00.toByte()),
                byteArrayOf(0x87.toByte(), 0x00.toByte(), 0x5f.toByte()),
                byteArrayOf(0x87.toByte(), 0x00.toByte(), 0x87.toByte()),
                byteArrayOf(0x87.toByte(), 0x00.toByte(), 0xaf.toByte()),
                byteArrayOf(0x87.toByte(), 0x00.toByte(), 0xd7.toByte()),
                byteArrayOf(0x87.toByte(), 0x00.toByte(), 0xff.toByte()),
                byteArrayOf(0x87.toByte(), 0x5f.toByte(), 0x00.toByte()),
                byteArrayOf(0x87.toByte(), 0x5f.toByte(), 0x5f.toByte()),
                byteArrayOf(0x87.toByte(), 0x5f.toByte(), 0x87.toByte()),
                byteArrayOf(0x87.toByte(), 0x5f.toByte(), 0xaf.toByte()),
                byteArrayOf(0x87.toByte(), 0x5f.toByte(), 0xd7.toByte()),
                byteArrayOf(0x87.toByte(), 0x5f.toByte(), 0xff.toByte()),
                byteArrayOf(0x87.toByte(), 0x87.toByte(), 0x00.toByte()),
                byteArrayOf(0x87.toByte(), 0x87.toByte(), 0x5f.toByte()),
                byteArrayOf(0x87.toByte(), 0x87.toByte(), 0x87.toByte()),
                byteArrayOf(0x87.toByte(), 0x87.toByte(), 0xaf.toByte()),
                byteArrayOf(0x87.toByte(), 0x87.toByte(), 0xd7.toByte()),
                byteArrayOf(0x87.toByte(), 0x87.toByte(), 0xff.toByte()),
                byteArrayOf(0x87.toByte(), 0xaf.toByte(), 0x00.toByte()),
                byteArrayOf(0x87.toByte(), 0xaf.toByte(), 0x5f.toByte()),
                byteArrayOf(0x87.toByte(), 0xaf.toByte(), 0x87.toByte()),
                byteArrayOf(0x87.toByte(), 0xaf.toByte(), 0xaf.toByte()),
                byteArrayOf(0x87.toByte(), 0xaf.toByte(), 0xd7.toByte()),
                byteArrayOf(0x87.toByte(), 0xaf.toByte(), 0xff.toByte()),
                byteArrayOf(0x87.toByte(), 0xd7.toByte(), 0x00.toByte()),
                byteArrayOf(0x87.toByte(), 0xd7.toByte(), 0x5f.toByte()),
                byteArrayOf(0x87.toByte(), 0xd7.toByte(), 0x87.toByte()),
                byteArrayOf(0x87.toByte(), 0xd7.toByte(), 0xaf.toByte()),
                byteArrayOf(0x87.toByte(), 0xd7.toByte(), 0xd7.toByte()),
                byteArrayOf(0x87.toByte(), 0xd7.toByte(), 0xff.toByte()),
                byteArrayOf(0x87.toByte(), 0xff.toByte(), 0x00.toByte()),
                byteArrayOf(0x87.toByte(), 0xff.toByte(), 0x5f.toByte()),
                byteArrayOf(0x87.toByte(), 0xff.toByte(), 0x87.toByte()),
                byteArrayOf(0x87.toByte(), 0xff.toByte(), 0xaf.toByte()),
                byteArrayOf(0x87.toByte(), 0xff.toByte(), 0xd7.toByte()),
                byteArrayOf(0x87.toByte(), 0xff.toByte(), 0xff.toByte()),
                byteArrayOf(0xaf.toByte(), 0x00.toByte(), 0x00.toByte()),
                byteArrayOf(0xaf.toByte(), 0x00.toByte(), 0x5f.toByte()),
                byteArrayOf(0xaf.toByte(), 0x00.toByte(), 0x87.toByte()),
                byteArrayOf(0xaf.toByte(), 0x00.toByte(), 0xaf.toByte()),
                byteArrayOf(0xaf.toByte(), 0x00.toByte(), 0xd7.toByte()),
                byteArrayOf(0xaf.toByte(), 0x00.toByte(), 0xff.toByte()),
                byteArrayOf(0xaf.toByte(), 0x5f.toByte(), 0x00.toByte()),
                byteArrayOf(0xaf.toByte(), 0x5f.toByte(), 0x5f.toByte()),
                byteArrayOf(0xaf.toByte(), 0x5f.toByte(), 0x87.toByte()),
                byteArrayOf(0xaf.toByte(), 0x5f.toByte(), 0xaf.toByte()),
                byteArrayOf(0xaf.toByte(), 0x5f.toByte(), 0xd7.toByte()),
                byteArrayOf(0xaf.toByte(), 0x5f.toByte(), 0xff.toByte()),
                byteArrayOf(0xaf.toByte(), 0x87.toByte(), 0x00.toByte()),
                byteArrayOf(0xaf.toByte(), 0x87.toByte(), 0x5f.toByte()),
                byteArrayOf(0xaf.toByte(), 0x87.toByte(), 0x87.toByte()),
                byteArrayOf(0xaf.toByte(), 0x87.toByte(), 0xaf.toByte()),
                byteArrayOf(0xaf.toByte(), 0x87.toByte(), 0xd7.toByte()),
                byteArrayOf(0xaf.toByte(), 0x87.toByte(), 0xff.toByte()),
                byteArrayOf(0xaf.toByte(), 0xaf.toByte(), 0x00.toByte()),
                byteArrayOf(0xaf.toByte(), 0xaf.toByte(), 0x5f.toByte()),
                byteArrayOf(0xaf.toByte(), 0xaf.toByte(), 0x87.toByte()),
                byteArrayOf(0xaf.toByte(), 0xaf.toByte(), 0xaf.toByte()),
                byteArrayOf(0xaf.toByte(), 0xaf.toByte(), 0xd7.toByte()),
                byteArrayOf(0xaf.toByte(), 0xaf.toByte(), 0xff.toByte()),
                byteArrayOf(0xaf.toByte(), 0xd7.toByte(), 0x00.toByte()),
                byteArrayOf(0xaf.toByte(), 0xd7.toByte(), 0x5f.toByte()),
                byteArrayOf(0xaf.toByte(), 0xd7.toByte(), 0x87.toByte()),
                byteArrayOf(0xaf.toByte(), 0xd7.toByte(), 0xaf.toByte()),
                byteArrayOf(0xaf.toByte(), 0xd7.toByte(), 0xd7.toByte()),
                byteArrayOf(0xaf.toByte(), 0xd7.toByte(), 0xff.toByte()),
                byteArrayOf(0xaf.toByte(), 0xff.toByte(), 0x00.toByte()),
                byteArrayOf(0xaf.toByte(), 0xff.toByte(), 0x5f.toByte()),
                byteArrayOf(0xaf.toByte(), 0xff.toByte(), 0x87.toByte()),
                byteArrayOf(0xaf.toByte(), 0xff.toByte(), 0xaf.toByte()),
                byteArrayOf(0xaf.toByte(), 0xff.toByte(), 0xd7.toByte()),
                byteArrayOf(0xaf.toByte(), 0xff.toByte(), 0xff.toByte()),
                byteArrayOf(0xd7.toByte(), 0x00.toByte(), 0x00.toByte()),
                byteArrayOf(0xd7.toByte(), 0x00.toByte(), 0x5f.toByte()),
                byteArrayOf(0xd7.toByte(), 0x00.toByte(), 0x87.toByte()),
                byteArrayOf(0xd7.toByte(), 0x00.toByte(), 0xaf.toByte()),
                byteArrayOf(0xd7.toByte(), 0x00.toByte(), 0xd7.toByte()),
                byteArrayOf(0xd7.toByte(), 0x00.toByte(), 0xff.toByte()),
                byteArrayOf(0xd7.toByte(), 0x5f.toByte(), 0x00.toByte()),
                byteArrayOf(0xd7.toByte(), 0x5f.toByte(), 0x5f.toByte()),
                byteArrayOf(0xd7.toByte(), 0x5f.toByte(), 0x87.toByte()),
                byteArrayOf(0xd7.toByte(), 0x5f.toByte(), 0xaf.toByte()),
                byteArrayOf(0xd7.toByte(), 0x5f.toByte(), 0xd7.toByte()),
                byteArrayOf(0xd7.toByte(), 0x5f.toByte(), 0xff.toByte()),
                byteArrayOf(0xd7.toByte(), 0x87.toByte(), 0x00.toByte()),
                byteArrayOf(0xd7.toByte(), 0x87.toByte(), 0x5f.toByte()),
                byteArrayOf(0xd7.toByte(), 0x87.toByte(), 0x87.toByte()),
                byteArrayOf(0xd7.toByte(), 0x87.toByte(), 0xaf.toByte()),
                byteArrayOf(0xd7.toByte(), 0x87.toByte(), 0xd7.toByte()),
                byteArrayOf(0xd7.toByte(), 0x87.toByte(), 0xff.toByte()),
                byteArrayOf(0xd7.toByte(), 0xaf.toByte(), 0x00.toByte()),
                byteArrayOf(0xd7.toByte(), 0xaf.toByte(), 0x5f.toByte()),
                byteArrayOf(0xd7.toByte(), 0xaf.toByte(), 0x87.toByte()),
                byteArrayOf(0xd7.toByte(), 0xaf.toByte(), 0xaf.toByte()),
                byteArrayOf(0xd7.toByte(), 0xaf.toByte(), 0xd7.toByte()),
                byteArrayOf(0xd7.toByte(), 0xaf.toByte(), 0xff.toByte()),
                byteArrayOf(0xd7.toByte(), 0xd7.toByte(), 0x00.toByte()),
                byteArrayOf(0xd7.toByte(), 0xd7.toByte(), 0x5f.toByte()),
                byteArrayOf(0xd7.toByte(), 0xd7.toByte(), 0x87.toByte()),
                byteArrayOf(0xd7.toByte(), 0xd7.toByte(), 0xaf.toByte()),
                byteArrayOf(0xd7.toByte(), 0xd7.toByte(), 0xd7.toByte()),
                byteArrayOf(0xd7.toByte(), 0xd7.toByte(), 0xff.toByte()),
                byteArrayOf(0xd7.toByte(), 0xff.toByte(), 0x00.toByte()),
                byteArrayOf(0xd7.toByte(), 0xff.toByte(), 0x5f.toByte()),
                byteArrayOf(0xd7.toByte(), 0xff.toByte(), 0x87.toByte()),
                byteArrayOf(0xd7.toByte(), 0xff.toByte(), 0xaf.toByte()),
                byteArrayOf(0xd7.toByte(), 0xff.toByte(), 0xd7.toByte()),
                byteArrayOf(0xd7.toByte(), 0xff.toByte(), 0xff.toByte()),
                byteArrayOf(0xff.toByte(), 0x00.toByte(), 0x00.toByte()),
                byteArrayOf(0xff.toByte(), 0x00.toByte(), 0x5f.toByte()),
                byteArrayOf(0xff.toByte(), 0x00.toByte(), 0x87.toByte()),
                byteArrayOf(0xff.toByte(), 0x00.toByte(), 0xaf.toByte()),
                byteArrayOf(0xff.toByte(), 0x00.toByte(), 0xd7.toByte()),
                byteArrayOf(0xff.toByte(), 0x00.toByte(), 0xff.toByte()),
                byteArrayOf(0xff.toByte(), 0x5f.toByte(), 0x00.toByte()),
                byteArrayOf(0xff.toByte(), 0x5f.toByte(), 0x5f.toByte()),
                byteArrayOf(0xff.toByte(), 0x5f.toByte(), 0x87.toByte()),
                byteArrayOf(0xff.toByte(), 0x5f.toByte(), 0xaf.toByte()),
                byteArrayOf(0xff.toByte(), 0x5f.toByte(), 0xd7.toByte()),
                byteArrayOf(0xff.toByte(), 0x5f.toByte(), 0xff.toByte()),
                byteArrayOf(0xff.toByte(), 0x87.toByte(), 0x00.toByte()),
                byteArrayOf(0xff.toByte(), 0x87.toByte(), 0x5f.toByte()),
                byteArrayOf(0xff.toByte(), 0x87.toByte(), 0x87.toByte()),
                byteArrayOf(0xff.toByte(), 0x87.toByte(), 0xaf.toByte()),
                byteArrayOf(0xff.toByte(), 0x87.toByte(), 0xd7.toByte()),
                byteArrayOf(0xff.toByte(), 0x87.toByte(), 0xff.toByte()),
                byteArrayOf(0xff.toByte(), 0xaf.toByte(), 0x00.toByte()),
                byteArrayOf(0xff.toByte(), 0xaf.toByte(), 0x5f.toByte()),
                byteArrayOf(0xff.toByte(), 0xaf.toByte(), 0x87.toByte()),
                byteArrayOf(0xff.toByte(), 0xaf.toByte(), 0xaf.toByte()),
                byteArrayOf(0xff.toByte(), 0xaf.toByte(), 0xd7.toByte()),
                byteArrayOf(0xff.toByte(), 0xaf.toByte(), 0xff.toByte()),
                byteArrayOf(0xff.toByte(), 0xd7.toByte(), 0x00.toByte()),
                byteArrayOf(0xff.toByte(), 0xd7.toByte(), 0x5f.toByte()),
                byteArrayOf(0xff.toByte(), 0xd7.toByte(), 0x87.toByte()),
                byteArrayOf(0xff.toByte(), 0xd7.toByte(), 0xaf.toByte()),
                byteArrayOf(0xff.toByte(), 0xd7.toByte(), 0xd7.toByte()),
                byteArrayOf(0xff.toByte(), 0xd7.toByte(), 0xff.toByte()),
                byteArrayOf(0xff.toByte(), 0xff.toByte(), 0x00.toByte()),
                byteArrayOf(0xff.toByte(), 0xff.toByte(), 0x5f.toByte()),
                byteArrayOf(0xff.toByte(), 0xff.toByte(), 0x87.toByte()),
                byteArrayOf(0xff.toByte(), 0xff.toByte(), 0xaf.toByte()),
                byteArrayOf(0xff.toByte(), 0xff.toByte(), 0xd7.toByte()),
                byteArrayOf(0xff.toByte(), 0xff.toByte(), 0xff.toByte()),
                byteArrayOf(0x08.toByte(), 0x08.toByte(), 0x08.toByte()),
                byteArrayOf(0x12.toByte(), 0x12.toByte(), 0x12.toByte()),
                byteArrayOf(0x1c.toByte(), 0x1c.toByte(), 0x1c.toByte()),
                byteArrayOf(0x26.toByte(), 0x26.toByte(), 0x26.toByte()),
                byteArrayOf(0x30.toByte(), 0x30.toByte(), 0x30.toByte()),
                byteArrayOf(0x3a.toByte(), 0x3a.toByte(), 0x3a.toByte()),
                byteArrayOf(0x44.toByte(), 0x44.toByte(), 0x44.toByte()),
                byteArrayOf(0x4e.toByte(), 0x4e.toByte(), 0x4e.toByte()),
                byteArrayOf(0x58.toByte(), 0x58.toByte(), 0x58.toByte()),
                byteArrayOf(0x62.toByte(), 0x62.toByte(), 0x62.toByte()),
                byteArrayOf(0x6c.toByte(), 0x6c.toByte(), 0x6c.toByte()),
                byteArrayOf(0x76.toByte(), 0x76.toByte(), 0x76.toByte()),
                byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte()),
                byteArrayOf(0x8a.toByte(), 0x8a.toByte(), 0x8a.toByte()),
                byteArrayOf(0x94.toByte(), 0x94.toByte(), 0x94.toByte()),
                byteArrayOf(0x9e.toByte(), 0x9e.toByte(), 0x9e.toByte()),
                byteArrayOf(0xa8.toByte(), 0xa8.toByte(), 0xa8.toByte()),
                byteArrayOf(0xb2.toByte(), 0xb2.toByte(), 0xb2.toByte()),
                byteArrayOf(0xbc.toByte(), 0xbc.toByte(), 0xbc.toByte()),
                byteArrayOf(0xc6.toByte(), 0xc6.toByte(), 0xc6.toByte()),
                byteArrayOf(0xd0.toByte(), 0xd0.toByte(), 0xd0.toByte()),
                byteArrayOf(0xda.toByte(), 0xda.toByte(), 0xda.toByte()),
                byteArrayOf(0xe4.toByte(), 0xe4.toByte(), 0xe4.toByte()),
                byteArrayOf(0xee.toByte(), 0xee.toByte(), 0xee.toByte())
            )

            /**
             * Picks out a color approximated from the supplied RGB components
             * @param red Red intensity, from 0 to 255
             * @param green Red intensity, from 0 to 255
             * @param blue Red intensity, from 0 to 255
             * @return Nearest color from the 6x6x6 RGB color cube or from the 24 entries grey-scale ramp (whichever is closest)
             */
            @JvmStatic
            fun fromRGB(red: Int, green: Int, blue: Int): Indexed {
                require(!(red < 0 || red > 255)) { "fromRGB: red is outside of valid range (0-255)" }
                require(!(green < 0 || green > 255)) { "fromRGB: green is outside of valid range (0-255)" }
                require(!(blue < 0 || blue > 255)) { "fromRGB: blue is outside of valid range (0-255)" }
                val rescaledRed = (red.toDouble() / 255.0 * 5.0).toInt()
                val rescaledGreen = (green.toDouble() / 255.0 * 5.0).toInt()
                val rescaledBlue = (blue.toDouble() / 255.0 * 5.0).toInt()
                val index = rescaledBlue + 6 * rescaledGreen + 36 * rescaledRed + 16
                val fromColorCube = Indexed(index)
                val fromGreyRamp = fromGreyRamp((red + green + blue) / 3)

                //Now figure out which one is closest
                val coloredDistance =
                    (red - fromColorCube.red) * (red - fromColorCube.red) +
                            (green - fromColorCube.green) * (green - fromColorCube.green) +
                            (blue - fromColorCube.blue) * (blue - fromColorCube.blue)
                val greyDistance = (red - fromGreyRamp.red) * (red - fromGreyRamp.red) +
                        (green - fromGreyRamp.green) * (green - fromGreyRamp.green) +
                        (blue - fromGreyRamp.blue) * (blue - fromGreyRamp.blue)
                return if (coloredDistance < greyDistance) {
                    fromColorCube
                } else {
                    fromGreyRamp
                }
            }

            /**
             * Picks out a color from the grey-scale ramp area of the color index.
             * @param intensity Intensity, 0 - 255
             * @return Indexed color from the grey-scale ramp which is the best match for the supplied intensity
             */
            private fun fromGreyRamp(intensity: Int): Indexed {
                val rescaled = (intensity.toDouble() / 255.0 * 23.0).toInt() + 232
                return Indexed(rescaled)
            }
        }

        /**
         * Creates a new TextColor using the XTerm 256 color indexed mode, with the specified index value. You must
         * choose a value between 0 and 255.
         * @param colorIndex Index value to use for this color.
         */
        init {
            require(!(colorIndex > 255 || colorIndex < 0)) {
                "Cannot create a Color.Indexed with a color index of " + colorIndex +
                        ", must be in the range of 0-255"
            }
            this.colorIndex = colorIndex
        }
    }

    /**
     * This class can be used to specify a color in 24-bit color space (RGB with 8-bit resolution per color). Please be
     * aware that only a few terminal support 24-bit color control codes, please avoid using this class unless you know
     * all users will have compatible terminals. For details, please see
     * [
 * this](https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors) commit log. Behavior on terminals that don't support these codes is undefined.
     */
    class RGB(r: Int, g: Int, b: Int) : TextColor {
        override val red: Int
        override val green: Int
        override val blue: Int

        override val foregroundSGRSequence: ByteArray?
            get() = "38;2;$red;$green;$blue".toByteArray()
        override val backgroundSGRSequence: ByteArray?
            get() = "48;2;$red;$green;$blue".toByteArray()

        override fun toColor(): Color {
            return Color(red, green, blue)
        }

        override fun toString(): String {
            return "{RGB:$red,$green,$blue}"
        }

        override fun hashCode(): Int {
            var hash = 7
            hash = 29 * hash + red
            hash = 29 * hash + green
            hash = 29 * hash + blue
            return hash
        }

        override fun equals(obj: Any?): Boolean {
            if (obj == null) {
                return false
            }
            if (javaClass != obj.javaClass) {
                return false
            }
            val other = obj as RGB
            return red == other.red && green == other.green && blue == other.blue
        }

        /**
         * This class can be used to specify a color in 24-bit color space (RGB with 8-bit resolution per color). Please be
         * aware that only a few terminal support 24-bit color control codes, please avoid using this class unless you know
         * all users will have compatible terminals. For details, please see
         * [
 * this](https://github.com/robertknight/konsole/blob/master/user-doc/README.moreColors) commit log. Behavior on terminals that don't support these codes is undefined.
         *
         * @param r Red intensity, from 0 to 255
         * @param g Green intensity, from 0 to 255
         * @param b Blue intensity, from 0 to 255
         */
        init {
            require(!(r < 0 || r > 255)) { "RGB: r is outside of valid range (0-255)" }
            require(!(g < 0 || g > 255)) { "RGB: g is outside of valid range (0-255)" }
            require(!(b < 0 || b > 255)) { "RGB: b is outside of valid range (0-255)" }
            red = r
            green = g
            blue = b
        }
    }

    /**
     * Utility class to instantiate colors from other types and definitions
     */
    object Factory {
        private val INDEXED_COLOR = Pattern.compile("#[0-9]{1,3}")
        private val RGB_COLOR = Pattern.compile("#[0-9a-fA-F]{6}")

        /**
         * Parses a string into a color. The string can have one of three formats:
         *
         *  * *blue* - Constant value from the [ANSI] enum
         *  * *#17* - Hash character followed by one to three numbers; picks the color with that index from
         * the 256 color palette
         *  * *#1a1a1a* - Hash character followed by three hex-decimal tuples; creates an RGB color entry by
         * parsing the tuples as Red, Green and Blue
         *
         * @param value The string value to parse
         * @return A [TextColor] that is either an [ANSI], an [Indexed] or an [RGB] depending on
         * the format of the string, or `null` if `value` is `null`.
         */
        @JvmStatic
        fun fromString(value: String?): TextColor? {
            var value = value ?: return null
            value = value.trim { it <= ' ' }
            if (RGB_COLOR.matcher(value).matches()) {
                val r = value.substring(1, 3).toInt(16)
                val g = value.substring(3, 5).toInt(16)
                val b = value.substring(5, 7).toInt(16)
                return RGB(r, g, b)
            } else if (INDEXED_COLOR.matcher(value).matches()) {
                val index = value.substring(1).toInt()
                return Indexed(index)
            }
            return try {
                ANSI.valueOf(value.uppercase(Locale.getDefault()))
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Unknown color definition \"$value\"", e)
            }
        }
    }
}
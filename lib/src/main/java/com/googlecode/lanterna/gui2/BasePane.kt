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

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.graphics.Theme
import com.googlecode.lanterna.gui2.menu.MenuBar
import com.googlecode.lanterna.input.KeyStroke

/**
 * BasePane is the base container in a Text GUI. A text gui may have several base panes, although they are
 * always independent. One common example of this is a multi-window system where each window is a base pane. Think of
 * the base pane as a root container, the ultimate parent of all components added to a GUI. When you use
 * `MultiWindowTextGUI`, the background space behind the windows is a `BasePane` too, just like each of the
 * windows. They are all drawn separately and composited together. Every `BasePane` has a single component that
 * is drawn over the entire area the `BasePane` is occupying, it's very likely you want to set this component to
 * be a container of some sort, probably a `Panel`, that can host multiple child components.
 *
 * @see Panel
 *
 * @author Martin
 */
interface BasePane : Composite {
    /**
     * Returns the TextGUI this BasePane belongs to or `null` if none. One example of when this method returns
     * `null` is when calling it on a Window that hasn't been displayed yet.
     * @return The TextGUI this BasePane belongs to
     */
    fun getTextGUI(): TextGUI?

    /**
     * Called by the GUI system (or something imitating the GUI system) to draw the root container. The TextGUIGraphics
     * object should be used to perform the drawing operations.
     * @param graphics TextGraphics object to draw with
     */
    fun draw(graphics: TextGUIGraphics)

    /**
     * Checks if this root container (i.e. any of its child components) has signaled that what it's currently displaying
     * is out of date and needs re-drawing.
     * @return `true` if the container's content is invalid and needs redrawing, `false` otherwise
     */
    fun isInvalid(): Boolean

    /**
     * Invalidates the whole root container (including all of its child components) which will cause them all to be
     * recalculated (for containers) and redrawn.
     */
    fun invalidate()

    /**
     * Called by the GUI system to delegate a keyboard input event. The root container will decide what to do with this
     * input, usually sending it to one of its sub-components, but if it isn't able to find any handler for this input
     * it should return `false` so that the GUI system can take further decisions on what to do with it.
     * @param key Keyboard input
     * @return `true` If the root container could handle the input, false otherwise
     */
    fun handleInput(key: KeyStroke): Boolean

    /**
     * Returns the component that is the content of the BasePane. This is probably the root of a hierarchy of nested
     * Panels but it could also be a single component.
     * @return Component which is the content of this BasePane
     */
    override fun getComponent(): Component?

    /**
     * Sets the top-level component inside this BasePane. If you want it to contain only one component, you can set it
     * directly, but for more complicated GUIs you probably want to create a hierarchy of panels and set the first one
     * here.
     * @param component Component which this BasePane is using as it's content
     */
    override fun setComponent(component: Component?)

    /**
     * Returns the component in the root container that currently has input focus. There can only be one component at a
     * time being in focus.
     * @return Interactable component that is currently in receiving input focus
     */
    fun getFocusedInteractable(): Interactable?

    /**
     * Sets the component currently in focus within this root container, or sets no component in focus if `null`
     * is passed in.
     * @param interactable Interactable to focus, or `null` to clear focus
     */
    fun setFocusedInteractable(interactable: Interactable?)

    /**
     * Returns the position of where to put the terminal cursor according to this root container. This is typically
     * derived from which component has focus, or `null` if no component has focus or if the root container doesn't
     * want the cursor to be visible. Note that the coordinates are in local coordinate space, relative to the top-left
     * corner of the root container. You can use your TextGUI implementation to translate these to global coordinates.
     * @return Local position of where to place the cursor, or `null` if the cursor shouldn't be visible
     */
    fun getCursorPosition(): TerminalPosition?

    /**
     * Returns a position in a root container's local coordinate space to global coordinates
     * @param localPosition The local position to translate
     * @return The local position translated to global coordinates
     */
    fun toGlobal(localPosition: TerminalPosition): TerminalPosition

    /**
     * Returns a position expressed in global coordinates, i.e. row and column offset from the top-left corner of the
     * terminal into a position relative to the top-left corner of the base pane. Calling
     * `fromGlobal(toGlobal(..))` should return the exact same position.
     * @param position Position expressed in global coordinates to translate to local coordinates of this BasePane
     * @return The global coordinates expressed as local coordinates
     */
    fun fromGlobal(position: TerminalPosition): TerminalPosition

    /**
     * If set to true, up/down array keys will not translate to next/previous if there are no more components
     * above/below.
     * @param strictFocusChange Will not allow relaxed navigation if set to `true`
     */
    fun setStrictFocusChange(strictFocusChange: Boolean)

    /**
     * If set to false, using the keyboard arrows keys will have the same effect as using the tab and reverse tab.
     * Lanterna will map arrow down and arrow right to tab, going to the next component, and array up and array left to
     * reverse tab, going to the previous component. If set to true, Lanterna will search for the next component
     * starting at the cursor position in the general direction of the arrow. By default this is enabled.
     *
     *
     * In Lanterna 2, direction based movements were not available.
     * @param enableDirectionBasedMovements Should direction based focus movements be enabled?
     */
    fun setEnableDirectionBasedMovements(enableDirectionBasedMovements: Boolean)

    /**
     * Returns the text GUI [Theme] associated with this base pane/window. This is either coming from the
     * [TextGUI] this object is associated with, the theme set as the override through [.setTheme]
     * or `null` if this base pane/window isn't added to any [TextGUI] and doesn't have any override.
     * @return The [Theme] this base pane/window is expected to use when drawing the contents
     */
    fun getTheme(): Theme?

    /**
     * Sets the override [Theme] to use for this base pane/window, rather than the default [Theme]
     * associated with the [TextGUI] it is attached to. If called with `null`, it will clear the override
     * and use the default value instead.
     * @param theme [Theme] to assign to this base pane/window, or `null` to reset
     */
    fun setTheme(theme: Theme?)

    /**
     * Sets the active [MenuBar] for this base pane/window. The menu will be rendered at the top (inside the
     * window decorations if set on a window), if set. If called with `null`, any previously set menu bar is
     * removed.
     * @param menubar The [MenuBar] to assign to this pane/window
     */
    fun setMenuBar(menubar: MenuBar?)

    /**
     * Returns the [MenuBar] assigned to this base pane/window, if any, otherwise returns {code null}.
     * @return The active menu bar or `null`
     */
    fun getMenuBar(): MenuBar?
}
/*
 *    This file is part of the GWTUML project
 *    and was written by Mounier Florian <mounier-dot-florian.at.gmail'dot'com> 
 *    for Objet Direct <http://wwww.objetdirect.com>
 *    
 *    Copyright © 2009 Objet Direct
 *    Contact: gwtuml@googlegroups.com
 *    
 *    GWTUML is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    GWTUML is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with GWTUML. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.objetdirect.gwt.umlapi.client.webinterface;

/**
 * @author Florian Mounier (mounier-dot-florian.at.gmail'dot'com)
 *
 */
public class Session {
    private static UMLCanvas activeCanvas;
    /**
     * Getter for the active canvas
     *
     * @return The active {@link UMLCanvas}
     */
    public static UMLCanvas getActiveCanvas() {
        return activeCanvas;
    }
    /**
     * Setter for the active canvas (the one which will receive the hot keys)
     *  
     * @param canvas The active {@link UMLCanvas}
     */
    public static void setActiveCanvas(final UMLCanvas canvas) {
	activeCanvas = canvas;
    }
}
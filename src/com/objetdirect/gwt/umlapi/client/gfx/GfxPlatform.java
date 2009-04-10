package com.objetdirect.gwt.umlapi.client.gfx;

import com.google.gwt.user.client.ui.Widget;
import com.objetdirect.gwt.umlapi.client.engine.Point;

/**
 * This interface normalize all the methods used on and by a graphical object for any gfx implementation
 * 
 * @author Florian Mounier (mounier-dot-florian.at.gmail'dot'com)
 *
 */
public interface GfxPlatform {
    /**
     * Default canvas height
     */
    final static int DEFAULT_CANVAS_HEIGHT = 600;
    /**
     * Default canvas width
     */
    final static int DEFAULT_CANVAS_WIDTH = 800;

    /**
     * Adds a {@link GfxObjectListener} to the canvas. 
     * 
     * 
     * @param canvas The canvas to add a listener to
     * @param gfxObjectListener The {@link GfxObjectListener} to add to the canvas 
     */
    void addObjectListenerToCanvas(Widget canvas,
	    GfxObjectListener gfxObjectListener);

    /**
     * Adds a {@link GfxObject} to the canvas at the specified 
     * position.
     * 
     * @param canvas The canvas on which the {@link GfxObject} will be added
     * @param gfxObject the {@link GfxObject} to add.
     * @param location The location {@link Point} of the added gfxObject
     */
    void addToCanvas(Widget canvas, GfxObject gfxObject, Point location);

    /**
     * Add a {@link GfxObject} to a virtual group
     * 
     * @param gfxObjectGroup The group that will contain the {@link GfxObject}
     * @param gfxObject The {@link GfxObject} to add to the gfxObjectGroup
     */
    void addToVirtualGroup(GfxObject gfxObjectGroup, GfxObject gfxObject);


    /**
     * Build a line from {@link Point} p1 to {@link Point} p2
     * 
     * @param p1 The line first end {@link Point}
     * @param p2 The line other end {@link Point}
     *      * 
     * @return The new line in a {@link GfxObject}
     */
    GfxObject buildLine(Point p1, Point p2);

    /**
     * Start the building of a path.
     * 
     * @return The initialized path in a {@link GfxObject}
     * 
     * @see GfxPlatform#moveTo(GfxObject, Point)
     * @see GfxPlatform#lineTo(GfxObject, Point)
     */
    GfxObject buildPath();

    /**
     * Build a rectangle with the size : width x height
     * 
     * @param width The rectangle width
     * @param height The rectangle height
     * 
     * @return The new rectangle in a {@link GfxObject}
     */
    GfxObject buildRect(int width, int height);

    /**
     * Build a Text containing text
     * 
     * @param text The text String used to make the Text {@link GfxObject}
     * 
     * @return The new text in a {@link GfxObject}
     */
    GfxObject buildText(String text);

    /**
     * Build a virtual group
     *  
     * @return The new virtual group in a {@link GfxObject}
     */
    GfxObject buildVirtualGroup();

    /**
     * Erase all members in a virtual group
     *  
     * @param gfxObjectGroup The {@link GfxObject} containing the virtual group
     */
    void clearVirtualGroup(GfxObject gfxObjectGroup);

    /**
     * Getter for the {@link GfxObject} virtual group if any
     * 
     * @param gfxObject The {@link GfxObject}to get the virtual group from
     * @return The virtual group of this {@link GfxObject} if any, null otherwise
     */
    GfxObject getGroup(GfxObject gfxObject);

    /**
     * Getter for the height of a Text
     * 
     * @param gfxObject The {@link GfxObject} containing a Text to measure the height
     * @return The height of the Text
     */
    int getTextHeightFor(GfxObject gfxObject);

    /**
     * Getter for the width of a Text
     * 
     * @param gfxObject The {@link GfxObject} containing a Text to measure the width
     * @return The width of the Text
     */
    int getTextWidthFor(GfxObject gfxObject);

    /**
     * Getter for the {@link GfxObject} location in the canvas
     * 
     * @param gfxObject The {@link GfxObject} to retrieve the location 
     * @return The location {@link Point} of the gfxObject
     */
    Point getLocationFor(GfxObject gfxObject);

    /**
     * Line a path from the current location to the {@link Point} location  
     * 
     * @param gfxObject The {@link GfxObject} containing the path
     * @param location The {@link Point} location to line to  
     */
    void lineTo(GfxObject gfxObject, Point location);

    /**
     * Make a graphic canvas with default size and return the widget
     *  
     * @return The created graphic canvas widget
     */
    Widget makeCanvas();

    /**
     * Make a graphic canvas with default size and return the widget
     * 
     * @param width The canvas width
     * @param height The canvas height
     * @param backgroundColor The canvas background {@link GfxColor}
     * @return The created graphic canvas widget
     */
    Widget makeCanvas(int width, int height, GfxColor backgroundColor);

    /**
     * Move a path from the current location to the {@link Point} location without lining  
     * 
     * @param gfxObject The {@link GfxObject} containing the path
     * @param location The {@link Point} location to move to  
     */
    void moveTo(GfxObject gfxObject, Point location);

    /**
     * Move the {@link GfxObject} to the background 
     * 
     * @param gfxObject The {@link GfxObject} to move to the background 
     */
    void moveToBack(GfxObject gfxObject);

    /**
     * Move the {@link GfxObject} to the foreground 
     * 
     * @param gfxObject The {@link GfxObject} to move to the foreground 
     */
    void moveToFront(GfxObject gfxObject);

    /**
     * Remove the {@link GfxObject} from the canvas
     * 
     * @param canvas The graphic canvas widget the {@link GfxObject} is on
     * @param gfxObject The {@link GfxObject} to remove from the canvas
     */
    void removeFromCanvas(Widget canvas, GfxObject gfxObject);

    /**
     * Removes a {@link GfxObject} from its virtual group
     * 
     * @param gfxObjectGroup the virtual group containing the gfxObject
     * @param gfxObject the {@link GfxObject} to remove
     * @param isSilent If true the canvas is not redrawn
     */
    void removeFromVirtualGroup(GfxObject gfxObjectGroup, GfxObject gfxObject,
	    boolean isSilent);

    /**
     * Setter for the filling color of a graphical object
     * 
     * @param gfxObject The {@link GfxObject} to set the filling color 
     * @param color The filling {@link GfxColor} 
     */
    void setFillColor(GfxObject gfxObject, GfxColor color);

    /**
     * Setter for the font of a graphical object text
     * 
     * @param gfxObject The {@link GfxObject} containing the Text to set the font 
     * @param gfxFont The text {@link GfxFont} 
     */
    void setFont(GfxObject gfxObject, GfxFont gfxFont);

    /**
     * Setter for the opacity of a graphical object
     * 
     * @param gfxObject The {@link GfxObject} containing the Text to set the font 
     * @param opacity The new opacity of the graphical object
     * @param isForBack If set to true set the background opacity, otherwise it is the stroke opacity
     * 
     */
    void setOpacity(GfxObject gfxObject, int opacity, boolean isForBack);

    /**
     * Setter for the size of the canvas
     * 
     * @param canvas The canvas to set the size
     * @param width The new canvas width
     * @param height The new canvas height
     */
    void setSize(Widget canvas, int width, int height);

    /**
     * Setter of the stroke color and width for a graphical object
     * 
     * @param gfxObject The {@link GfxObject} to set the stroke color and width
     * @param color The new stroke {@link GfxColor} 
     * @param width The new stroke width
     */
    void setStroke(GfxObject gfxObject, GfxColor color, int width);

    /**
     * Setter of the stroke style for a graphical object
     * 
     * @param gfxObject The {@link GfxObject} to set the stroke style
     * @param style The new {@link GfxStyle} of the graphical object
     */
    void setStrokeStyle(GfxObject gfxObject, GfxStyle style);

    /**
     * Translate a graphical object to a new destination by an offset
     * 
     * @param gfxObject The {@link GfxObject} to be translated
     * @param offset The {@link Point} containing the two offset dimensions
     */
    void translate(GfxObject gfxObject, Point offset);
}

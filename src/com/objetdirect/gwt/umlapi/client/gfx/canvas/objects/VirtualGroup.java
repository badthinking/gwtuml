package com.objetdirect.gwt.umlapi.client.gfx.canvas.objects;

import java.util.HashSet;
import java.util.Set;

import com.allen_sauer.gwt.log.client.Log;
import com.objetdirect.gwt.umlapi.client.gfx.canvas.CanvasBridge;

/**
 * @author Florian Mounier (mounier-dot-florian.at.gmail'dot'com)
 * 
 */
public class VirtualGroup extends IncubatorGfxObject {
    private final Set<IncubatorGfxObject> incubatorGfxObjectSet = new HashSet<IncubatorGfxObject>();

    /**
     * @param incubatorGfxObject
     */
    public void add(final IncubatorGfxObject incubatorGfxObject) {
	incubatorGfxObjectSet.add(incubatorGfxObject);
	incubatorGfxObject.setParentGroup(this);
	if (isVisible) {
	    incubatorGfxObject.addOnCanvasAt(canvas, 0, 0);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.objetdirect.gwt.umlapi.client.gfx.canvas.objects.IncubatorGfxObject
     * #addOnCanvasAt(com.objetdirect.gwt.umlapi.client.gfx.canvas.CanvasBridge,
     * int, int)
     */
    @Override
    public void addOnCanvasAt(final CanvasBridge canvasBridge, final int dx,
	    final int dy) {
	super.addOnCanvasAt(canvasBridge, dx, dy);
	for (final IncubatorGfxObject incubatorGfxObject : incubatorGfxObjectSet) {
	    incubatorGfxObject.addOnCanvasAt(canvasBridge, dx, dy);
	}
    }

    public void clear() {
	incubatorGfxObjectSet.clear();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.objetdirect.gwt.umlapi.client.gfx.canvas.objects.IncubatorGfxObject
     * #draw()
     */
    @Override
    public void draw() {
	if (!isVisible) {
	    Log.trace(this + " is not visible");
	    return;
	}
	if (canvas == null) {
	    Log.fatal("canvas is null for " + this);
	}

	Log.trace("Starting drawing " + this);
	for (final IncubatorGfxObject incubatorGfxObject : incubatorGfxObjectSet) {
	    incubatorGfxObject.draw();
	}
	Log.trace("Ending drawing " + this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.objetdirect.gwt.umlapi.client.gfx.canvas.objects.IncubatorGfxObject
     * #getHeight()
     */
    @Override
    public int getHeight() {
	// TODO Auto-generated method stub
	return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.objetdirect.gwt.umlapi.client.gfx.canvas.objects.IncubatorGfxObject
     * #getWidth()
     */
    @Override
    public int getWidth() {
	// TODO Auto-generated method stub
	return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.objetdirect.gwt.umlapi.client.gfx.canvas.objects.IncubatorGfxObject
     * #isPointed(int, int)
     */
    @Override
    public boolean isPointed(final int xp, final int yp) {
	return false;
    }

    /**
     * @param incubatorGfxObject
     */
    public void remove(final IncubatorGfxObject incubatorGfxObject) {
	incubatorGfxObjectSet.remove(incubatorGfxObject);
    }
}

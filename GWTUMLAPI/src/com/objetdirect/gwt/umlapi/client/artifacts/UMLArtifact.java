/*
 * This file is part of the GWTUML project and was written by Mounier Florian <mounier-dot-florian.at.gmail'dot'com> for Objet Direct
 * <http://wwww.objetdirect.com>
 * 
 * Copyright © 2009 Objet Direct Contact: gwtuml@googlegroups.com
 * 
 * GWTUML is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * GWTUML is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with GWTUML. If not, see <http://www.gnu.org/licenses/>.
 */
package com.objetdirect.gwt.umlapi.client.artifacts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.allen_sauer.gwt.log.client.Log;
import com.objetdirect.gwt.umlapi.client.contextMenu.MenuBarAndTitle;
import com.objetdirect.gwt.umlapi.client.engine.Direction;
import com.objetdirect.gwt.umlapi.client.engine.Point;
import com.objetdirect.gwt.umlapi.client.engine.Scheduler;
import com.objetdirect.gwt.umlapi.client.engine.ShapeGeometry;
import com.objetdirect.gwt.umlapi.client.exceptions.GWTUMLAPIException;
import com.objetdirect.gwt.umlapi.client.gfx.GfxManager;
import com.objetdirect.gwt.umlapi.client.gfx.GfxObject;
import com.objetdirect.gwt.umlapi.client.helpers.GWTUMLDrawerHelper;
import com.objetdirect.gwt.umlapi.client.helpers.QualityLevel;
import com.objetdirect.gwt.umlapi.client.umlCanvas.UMLCanvas;

/**
 * This abstract class represent any uml artifact that can be displayed An artifact is something between the graphical
 * object and the uml component. <br>
 * It has an uml component and it build the graphical object
 * 
 * @author Florian Mounier (mounier-dot-florian.at.gmail'dot'com)
 */
public abstract class UMLArtifact implements Serializable, CommonConstants {
	protected transient GfxObject gfxObject;

	/**
	 * /!\ Don't forget to increment the serialVersionUID if you change any of the fields above /!\
	 */
	private static final long serialVersionUID = 1L;

	protected boolean isSelected = false;
	private int id;

	protected UMLCanvas canvas;

	private LinkedList<LinkArtifact> upDependencies;
	private LinkedList<LinkArtifact> downDependencies;
	private LinkedList<LinkArtifact> leftDependencies;
	private LinkedList<LinkArtifact> rightDependencies;
	private LinkedList<LinkArtifact> allDependencies;

	private HashMap<LinkArtifact, UMLArtifact> dependentUMLArtifacts;
	private boolean isBuilt;
	private Point location;

	/** Default constructor ONLY for GWT-RPC serialization. */
	@Deprecated
	protected UMLArtifact() {
	}

	/**
	 * Constructor of UMLArtifact <br>
	 * 
	 * @param canvas
	 *            Where the gfxObject are displayed
	 * @param id
	 *            The artifacts's id
	 */
	public UMLArtifact(UMLCanvas canvas, int id) {
		this(canvas);
		this.id = id;
	}

	public UMLArtifact(UMLCanvas canvas) {
		this.canvas = canvas;
		isSelected = false;

		upDependencies = new LinkedList<LinkArtifact>();
		downDependencies = new LinkedList<LinkArtifact>();
		leftDependencies = new LinkedList<LinkArtifact>();
		rightDependencies = new LinkedList<LinkArtifact>();
		allDependencies = new LinkedList<LinkArtifact>();

		dependentUMLArtifacts = new HashMap<LinkArtifact, UMLArtifact>();

		isBuilt = false;
		location = Point.getOrigin();
	}

	/**
	 * This method destroys this artifact's graphical object and all dependencies graphical objects. <br>
	 * Useful to remove a class and all its links
	 */
	public void destroyGfxObjectWhithDependencies() {
		Log.trace("UMLArtifact::destroyGfxObjectWhithDependencies()");
		GfxManager.getPlatform().clearVirtualGroup(gfxObject);
		for (final Entry<LinkArtifact, UMLArtifact> dependentUMLArtifact : this.getDependentUMLArtifacts().entrySet()) {
			GfxManager.getPlatform().clearVirtualGroup(dependentUMLArtifact.getKey().getGfxObject());
		}
	}

	/**
	 * Request an edition on this artifact.
	 * 
	 * @param editedGfxObject
	 *            is the graphical object on which edition should occur
	 */
	public abstract void edit(GfxObject editedGfxObject);

	/**
	 * Getter for the center point of the graphical object
	 * 
	 * @return The Point which corresponds to the center of this artifact
	 */
	public Point getCenter() {
		return new Point(this.getLocation().getX() + this.getWidth() / 2, this.getLocation().getY() + this.getHeight() / 2);
	}

	/**
	 * Getter for the dependent UMLArtifacts
	 * 
	 * @return An {@link HashMap} of {@link LinkArtifact} and UMLArtifact which represents the links of this artifact
	 *         with the linked artifacts
	 */
	public HashMap<LinkArtifact, UMLArtifact> getDependentUMLArtifacts() {
		return dependentUMLArtifacts;
	}

	/**
	 * Getter for the graphical object of this artifact
	 * 
	 * @return The graphical object of this artifact. <br>
	 *         If it has already built this function just returns it, otherwise it builds it.
	 * 
	 */
	public GfxObject getGfxObject() {
		if (gfxObject == null) {
			throw new GWTUMLAPIException("Must Initialize before getting gfxObjects");
		}
		if (!isBuilt) {
			final long t = System.currentTimeMillis();
			this.buildGfxObjectWithAnimation();
			Log.trace("([" + (System.currentTimeMillis() - t) + "ms]) to build " + this);
			isBuilt = true;
		}
		return gfxObject;
	}

	/**
	 * Getter for the Height
	 * 
	 * @return This artifact total height
	 */
	public abstract int getHeight();

	/**
	 * Getter for the artifact id
	 * 
	 * @return the unique id of this artifact
	 */
	public int getId() {
		return id;
	}

	/**
	 * Getter for the location
	 * 
	 * @return A copy of the Point that represents where this artifact currently is
	 */
	public Point getLocation() {
		return location.clonePoint();
	}

	/**
	 * Getter for an "opaque" integer table. <br>
	 * This opaque represents all the points that describes a shape. <br>
	 * This is used by the shape based engine.
	 * 
	 * @return The opaque of this artifact which is an integer table
	 * 
	 * @see ShapeGeometry
	 */
	public abstract int[] getOpaque();

	/**
	 * Getter for the outline of an artifact. <br>
	 * The outline is what is been drawn during drag and drop
	 * 
	 * @return The graphical object of this outline
	 */
	public abstract GfxObject getOutline();

	/**
	 * Getter for the dependent objects points. <br>
	 * This is used to draw outline links during drag and drop.
	 * 
	 * @return An ArrayList of all this points
	 */
	public ArrayList<Point> getOutlineForDependencies() {

		final ArrayList<Point> points = new ArrayList<Point>();
		if (QualityLevel.IsAlmost(QualityLevel.HIGH)) {
			for (final Entry<LinkArtifact, UMLArtifact> dependentUMLArtifact : this.getDependentUMLArtifacts().entrySet()) {
				if (dependentUMLArtifact.getValue() != null) {
					points.add(dependentUMLArtifact.getValue().getCenter());
				}
			}
		}
		return points;
	}

	/**
	 * Getter for the context menu. <br>
	 * Each artifact has his own context menu which is built during right click
	 * 
	 * @return The context sub menu and the title of the sub menu
	 * @see MenuBarAndTitle
	 */
	public abstract MenuBarAndTitle getRightMenu();

	/**
	 * Getter for the Width
	 * 
	 * @return This artifact total width
	 */
	public abstract int getWidth();

	/**
	 * This is the method that initializes the graphical object. It <b>MUST</b> be called before doing anything else
	 * with the graphical object.
	 * 
	 * @return The initialized graphical object.
	 */
	public GfxObject initializeGfxObject() {
		gfxObject = GfxManager.getPlatform().buildVirtualGroup();
		isBuilt = false;
		return gfxObject;
	}

	/**
	 * This method can be used to determine if this artifact has a type inherited from {@link LinkArtifact}
	 * 
	 * @return <ul>
	 *         <li><b>True</b> if it is a link</li>
	 *         <li><b>False</b> otherwise</li>
	 *         </ul>
	 */
	public abstract boolean isALink();

	/**
	 * This method can be used to determine if this artifact can do drag and drop
	 * 
	 * @return <ul>
	 *         <li><b>True</b> if it can</li>
	 *         <li><b>False</b> otherwise</li>
	 *         </ul>
	 */
	public abstract boolean isDraggable();

	/**
	 * This method moves an artifact to a new location It changes the current location <b>AND</b> translate the
	 * graphical object
	 * 
	 * @param newLocation
	 *            The new location of the artifact
	 */
	public void moveTo(final Point newLocation) {
		if (!this.isALink()) {
			Log.trace("UMLArtifact::moveto newLocation = " + newLocation);
			Log.trace("\t getLocation = " + getLocation());
			Log.trace("\t vector of translation apply to the gfx object = " + Point.substract(newLocation, this.getLocation()));
			this.getGfxObject().translate(Point.substract(newLocation, this.getLocation()));
			setLocation(newLocation);
		} else {
			Log.error("Can't move a line ! (moveTo called on " + this + ")");
		}

	}

	/**
	 * This method does a rebuild of the graphical object of this artifact. <br>
	 * It is useful to reflect changes made on an artifact / uml component
	 * 
	 */
	public void rebuildGfxObject() {
		final long t = System.currentTimeMillis();
		GfxManager.getPlatform().clearVirtualGroup(gfxObject);
		this.buildGfxObjectWithAnimation();
		if (isSelected) {
			this.select();
		}

		Log.trace("([" + (System.currentTimeMillis() - t) + "ms]) to build " + this);
		for (final Entry<LinkArtifact, UMLArtifact> dependentUMLArtifact : this.getDependentUMLArtifacts().entrySet()) {
			Log.trace("Rebuilding : " + dependentUMLArtifact);
			new Scheduler.Task("RebuildingDependencyFor" + this) {
				@Override
				public void process() {
					final long t2 = System.currentTimeMillis();
					dependentUMLArtifact.getKey().rebuildGfxObject();
					Log.trace("([" + (System.currentTimeMillis() - t2) + "ms]) to arrow " + this);
				}
			};
		}
		Log.trace("UMLArtifact::rebuildGfxObject ([" + (System.currentTimeMillis() - t) + "ms]) to rebuild " + this + " with dependency");
		Log.trace("UMLArtifact::rebuildGfxObject artifact built at the location " + location);
	}

	/**
	 * Remove a dependency for this artifact
	 * 
	 * @param dependentUMLArtifact
	 *            The link which this artifact is no more dependent
	 */
	public void removeDependency(final LinkArtifact dependentUMLArtifact) {
		Log.trace(this + "removing depency with" + dependentUMLArtifact);
		dependentUMLArtifacts.remove(dependentUMLArtifact);
		upDependencies.remove(dependentUMLArtifact);
		downDependencies.remove(dependentUMLArtifact);
		leftDependencies.remove(dependentUMLArtifact);
		rightDependencies.remove(dependentUMLArtifact);
		allDependencies.remove(dependentUMLArtifact);
	}

	/**
	 * This method does the graphic changes to reflect that an artifact has been selected.
	 * 
	 * @param moveToFront
	 *            Specifies if the graphical object must be brought to foreground (this parameter is ignored if this
	 *            graphical object is a link)
	 * 
	 */
	public void select(final boolean moveToFront) {
		this.select();
		if (moveToFront && !this.isALink()) {
			this.toFront();
		}
	}

	/**
	 * This method does the graphic changes to reflect that an artifact has been selected
	 */
	protected void select() {
		isSelected = true;
		for (final Entry<LinkArtifact, UMLArtifact> dependentUMLArtifact : dependentUMLArtifacts.entrySet()) {
			if (!dependentUMLArtifact.getValue().equals(this) && dependentUMLArtifact.getValue().isSelected) {
				canvas.selectArtifact(dependentUMLArtifact.getKey());
			}
		}
	}

	/**
	 * Setter for the canvas Assign an artifact to his canvas
	 * 
	 * @param canvas
	 *            The canvas this artifact belongs
	 * 
	 */
	public void setCanvas(final UMLCanvas canvas) {
		this.canvas = canvas;
	}

	/**
	 * This method sets the current artifact location, it should not be called after the artifact is added on canvas
	 * except for very specifically uses: <br />
	 * It doesn't translate the graphical object unlike {@link UMLArtifact#moveTo}.
	 * 
	 * @param location
	 *            The artifact location
	 */
	public void setLocation(final Point location) {
		Log.trace("UMLArtifact::SetLocation( " + location);
		this.location = location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return GWTUMLDrawerHelper.getShortName(this);
	}

	/**
	 * This method transform the parameters of the object to a string
	 * 
	 * @return The String containing the parameters
	 */
	public abstract String toURL();

	/**
	 * This method do the graphic changes to reflect that an artifact has been unselected
	 */
	public void unselect() {
		isSelected = false;
	}

	public void addDependency(final LinkArtifact dependentUMLArtifact, final UMLArtifact linkedUMLArtifact) {
		dependentUMLArtifacts.put(dependentUMLArtifact, linkedUMLArtifact);
	}

	void buildGfxObjectWithAnimation() {
		this.buildGfxObject();
		if (QualityLevel.IsAlmost(QualityLevel.VERY_HIGH)) {
			for (int i = 25; i < 256; i += 25) {
				final int j = i;
				new Scheduler.Task("OpacityArtifactAnimation") {
					@Override
					public void process() {
						gfxObject.setOpacity(j, false);
					}
				};
			}
		}
	}

	public boolean hasThisAllDirectionsDependecy(final LinkArtifact linkArtifact) {
		return allDependencies.contains(linkArtifact);
	}

	void toBack() {
		gfxObject.moveToBack();
	}

	void toFront() {
		gfxObject.moveToFront();
	}

	public void addAllDirectionsDependecy(final LinkArtifact linkArtifact) {
		allDependencies.add(linkArtifact);
	}

	protected void addDirectionDependecy(final Direction direction, final LinkArtifact linkArtifact) {
		this.getDirectionList(direction).add(linkArtifact);
	}

	protected abstract void buildGfxObject();

	protected int getAllDirectionsDependenciesCount() {
		return allDependencies.size();
	}

	public int getAllDirectionsDependencyIndexOf(final LinkArtifact linkArtifact) {
		return allDependencies.indexOf(linkArtifact);
	}

	protected int getDependenciesCount(final Direction direction) {
		return this.getDirectionList(direction).size();
	}

	protected int getDependencyIndexOf(final LinkArtifact linkArtifact, final Direction direction) {
		return this.getDirectionList(direction).indexOf(linkArtifact);
	}

	protected void rebuildDirectionDependencies(final Direction direction) {
		final LinkedList<LinkArtifact> directionDependenciesList = new LinkedList<LinkArtifact>(this.getDirectionList(direction));
		if (direction == Direction.UNKNOWN) {
			return;
		}
		for (final LinkArtifact directionLink : directionDependenciesList) {
			directionLink.doesntHaveToBeComputed(true);
			directionLink.rebuildGfxObject();
			directionLink.doesntHaveToBeComputed(false);
		}
	}

	protected void removeAllDirectionsDependecy(final LinkArtifact linkArtifact) {
		allDependencies.remove(linkArtifact);
	}

	protected void removeDirectionDependecy(final Direction direction, final LinkArtifact linkArtifact) {
		this.getDirectionList(direction).remove(linkArtifact);
	}

	protected void removeDirectionDependecy(final LinkArtifact linkArtifact) {
		allDependencies.remove(linkArtifact);
	}

	protected void sortDirectionDependecy(final Direction direction) {
		Collections.sort(this.getDirectionList(direction), new Comparator<LinkArtifact>() {

			@Override
			public int compare(final LinkArtifact link1, final LinkArtifact link2) {
				final Point artifact1Location = dependentUMLArtifacts.get(link1).getLocation();
				final Point artifact2Location = dependentUMLArtifacts.get(link2).getLocation();

				final Integer int1 = direction.getXDirection() != 0 ? artifact1Location.getY() : artifact1Location.getX();
				final Integer int2 = direction.getXDirection() != 0 ? artifact2Location.getY() : artifact2Location.getX();

				return int1.compareTo(int2);
			}
		});
	}

	private LinkedList<LinkArtifact> getDirectionList(final Direction direction) {
		switch (direction) {
			case UP:
				return upDependencies;
			case DOWN:
				return downDependencies;
			case LEFT:
				return leftDependencies;
			case RIGHT:
				return rightDependencies;
		}
		return allDependencies;
	}

	public abstract void setUpAfterDeserialization();
}

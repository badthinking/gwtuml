/*
 * This file is part of the Gwt-Generator project and was written by Rapha�l Brugier <raphael dot brugier at gmail dot com > for Objet Direct
 * <http://wwww.objetdirect.com>
 * 
 * Copyright � 2010 Objet Direct
 * 
 * Gwt-Generator is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * Gwt-Generator is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Gwt-Generator. If not, see <http://www.gnu.org/licenses/>.
 */
package com.objetdirect.gwt.umlapi.client.umlCanvas;

import static com.objetdirect.gwt.umlapi.client.contextMenu.ContextMenu.createObjectDiagramContextMenu;
import static com.objetdirect.gwt.umlapi.client.helpers.CursorIconManager.PointerStyle.MOVE;
import static com.objetdirect.gwt.umlapi.client.umlCanvas.UMLCanvas.DragAndDropState.NONE;
import static com.objetdirect.gwt.umlapi.client.umlCanvas.UMLCanvas.DragAndDropState.TAKING;
import static com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.LinkKind.INSTANTIATION;
import static com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.LinkKind.OBJECT_RELATION;
import static com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.LinkKind.OBJECT_RELATION_WITH_CLASSNAME;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.objetdirect.gwt.umlapi.client.artifacts.LinkArtifact;
import com.objetdirect.gwt.umlapi.client.artifacts.UMLArtifact;
import com.objetdirect.gwt.umlapi.client.artifacts.object.ClassSimplifiedArtifact;
import com.objetdirect.gwt.umlapi.client.artifacts.object.InstantiationRelationLinkArtifact;
import com.objetdirect.gwt.umlapi.client.artifacts.object.ObjectArtifact;
import com.objetdirect.gwt.umlapi.client.artifacts.object.ObjectRelationLinkArtifact;
import com.objetdirect.gwt.umlapi.client.contextMenu.ContextMenu;
import com.objetdirect.gwt.umlapi.client.contextMenu.MenuBarAndTitle;
import com.objetdirect.gwt.umlapi.client.engine.Point;
import com.objetdirect.gwt.umlapi.client.gfx.GfxObject;
import com.objetdirect.gwt.umlapi.client.helpers.ObjectRelationsCalculator;
import com.objetdirect.gwt.umlapi.client.umlcomponents.UMLClass;
import com.objetdirect.gwt.umlapi.client.umlcomponents.UMLObject;
import com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.InstantiationRelation;
import com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.ObjectRelation;
import com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.UMLRelation;

/**
 * UMLCanvas concrete class for an object diagram.
 * 
 * @author Rapha�l Brugier <raphael dot brugier at gmail dot com>
 */
public class UMLCanvasObjectDiagram extends UMLCanvas implements ObjectDiagram {

	/**
	 * /!\ Don't forget to increment the serialVersionUID if you change any of the fields above /!\
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Classes defined in the instantiated class diagram.
	 */
	private List<UMLClass> domainClasses;

	/**
	 * Classes that are defined directly in the object diagram. This classes are a simplifed version of the UMLClass.
	 */
	private List<UMLClass> objectDiagramClasses;

	/**
	 * Relations between the classes from the class diagram instantiated.
	 */
	private List<UMLRelation> classRelations;

	private UMLClass classToAttach;
	private ObjectArtifact objectToAttach;

	/**
	 * Default constructor only for gwt-rpc serialization
	 */
	@SuppressWarnings("unused")
	private UMLCanvasObjectDiagram() {
	}

	protected UMLCanvasObjectDiagram(@SuppressWarnings("unused") boolean dummy) {
		super(true);

		objectDiagramClasses = new ArrayList<UMLClass>();
	}

	@Override
	public void addNewClass() {
		this.addNewClass(wrapper.getCurrentMousePosition());
	}
	@Override
	public void addNewObject() {
		this.addNewObject(wrapper.getCurrentMousePosition());
	}

	private void addNewClass(final Point location) {
		if (dragAndDropState != NONE) {
			return;
		}
		final ClassSimplifiedArtifact newClass = new ClassSimplifiedArtifact(this, idCount, "ClassName");
		objectDiagramClasses.add(newClass.toUMLComponent());

		this.add(newClass);
		newClass.moveTo(Point.substract(location, getCanvasOffset()));
		for (final UMLArtifact selectedArtifact : selectedArtifacts.keySet()) {
			selectedArtifact.unselect();
		}
		selectedArtifacts.clear();
		this.doSelection(newClass.getGfxObject(), false, false);
		selectedArtifacts.put(newClass, new ArrayList<Point>());
		dragOffset = location;
		wrapper.setCursorIcon(MOVE);
		dragAndDropState = TAKING;
		mouseIsPressed = true;

		wrapper.setHelpText("Adding a new class", location.clonePoint());
	}

	/**
	 * Add a new object with default values to this canvas at the specified location
	 * 
	 * @param location
	 *            The initial object location
	 */
	private void addNewObject(final Point location) {
		if (dragAndDropState != NONE) {
			return;
		}

		UMLClass clazz = new UMLClass("Object");
		if (domainClasses.size() != 0) {
			clazz = domainClasses.get(0);
		} else if (objectDiagramClasses.size() != 0) {
			clazz = objectDiagramClasses.get(0);
		}

		final ObjectArtifact newObject = new ObjectArtifact(this, idCount, clazz);

		this.add(newObject);
		newObject.moveTo(Point.substract(location, getCanvasOffset()));
		for (final UMLArtifact selectedArtifact : selectedArtifacts.keySet()) {
			selectedArtifact.unselect();
		}
		selectedArtifacts.clear();
		this.doSelection(newObject.getGfxObject(), false, false);
		selectedArtifacts.put(newObject, new ArrayList<Point>());
		dragOffset = location;
		wrapper.setCursorIcon(MOVE);
		dragAndDropState = TAKING;
		mouseIsPressed = true;

		wrapper.setHelpText("Adding a new object", location.clonePoint());
	}


	@Override
	protected LinkArtifact makeLinkBetween(UMLArtifact uMLArtifact, UMLArtifact uMLArtifactNew) {
		try {
			if (activeLinkingIsBetweenTwoObjects() && uMLArtifactNew instanceof ObjectArtifact && uMLArtifact instanceof ObjectArtifact) {
				return makeObjectRelationLink((ObjectArtifact) uMLArtifact, (ObjectArtifact) uMLArtifactNew);
			} else if ((activeLinking == INSTANTIATION)) {
				ClassSimplifiedArtifact classArtifact = null;
				ObjectArtifact objectArtifact = null;

				// Dirty dirty dirty :(
				if (uMLArtifactNew instanceof ClassSimplifiedArtifact && uMLArtifact instanceof ObjectArtifact) {
					classArtifact = (ClassSimplifiedArtifact) uMLArtifactNew;
					objectArtifact = (ObjectArtifact) uMLArtifact;
				} else if (uMLArtifact instanceof ClassSimplifiedArtifact && uMLArtifactNew instanceof ObjectArtifact) {
					classArtifact = (ClassSimplifiedArtifact) uMLArtifact;
					objectArtifact = (ObjectArtifact) uMLArtifactNew;
				} else {
					return null;
				}
				return new InstantiationRelationLinkArtifact(this, idCount, classArtifact, objectArtifact);
			}
		} catch (IllegalArgumentException e) {
			return null;
		}
		return null;
	}

	/**
	 * @return
	 */
	private boolean activeLinkingIsBetweenTwoObjects() {
		return (activeLinking == OBJECT_RELATION || activeLinking == OBJECT_RELATION_WITH_CLASSNAME);
	}

	/**
	 * Helper method to create an object relation between two objects.
	 * If classes relations have been specified, this method will constraint the creation of the association between two object
	 * to be the association between the two classes instantiated.
	 * 
	 * @param uMLArtifact the object owning the relation.
	 * @param uMLArtifactNew the object targeted by the relation.
	 * @return The relation created or null if the relation is illegal.
	 * @throws IllegalArgumentException If the relation is illegal.
	 */
	private LinkArtifact makeObjectRelationLink(ObjectArtifact left, ObjectArtifact right) throws IllegalArgumentException {
		// If no classes relations have been setup, we allow all the objects relations.
		if (classRelations == null) {
			return new ObjectRelationLinkArtifact(this, idCount, left, right);
		}

		for (UMLRelation relation : classRelations) {

			String leftTargetName = relation.getLeftTarget().getName();
			String leftUmlComponentName = left.toUMLComponent().getClassName();
			String rightTargetName = relation.getRightTarget().getName();
			String rightUmlComponentName = right.toUMLComponent().getClassName();

			if (leftTargetName.equals(leftUmlComponentName) && rightTargetName.equals(rightUmlComponentName)) {
				ObjectRelationLinkArtifact objectRelation = new ObjectRelationLinkArtifact(this, idCount, left, right);
				objectRelation.setRightRole(relation.getRightRole());
				return objectRelation;
			}
		}

		return null;
	}

	/**
	 * Create a special relation menu when the select gfxobject is an uml object.
	 * This menu allows to create a relation between the selected object and an other based on
	 * the existing object relations.
	 * 
	 * @param gfxObject
	 * @return
	 */
	private MenuBarAndTitle createRelationMenu(GfxObject gfxObject) {
		MenuBarAndTitle menu = new MenuBarAndTitle();
		menu.setName("Create relation with : ");

		UMLArtifact umlArtifact = getUMLArtifact(gfxObject);
		if (umlArtifact instanceof ObjectArtifact) {
			final ObjectArtifact objectArtifact = (ObjectArtifact) umlArtifact;

			ObjectRelationsCalculator objectRelationsCalculator = new ObjectRelationsCalculator(this, objectArtifact.toUMLComponent());
			List<UMLClass> possibleClasses = objectRelationsCalculator.getPossibleClasses();
			for (final UMLClass umlClass : possibleClasses) {
				menu.addItem(umlClass.getName(), new Command() {

					@Override
					public void execute() {
						objectToAttach = objectArtifact;
						classToAttach = umlClass;
						toLinkMode(OBJECT_RELATION_WITH_CLASSNAME);
					}
				});
			}
		}

		return menu;
	}

	/* (non-Javadoc)
	 * @see com.objetdirect.gwt.umlapi.client.umlCanvas.UMLCanvas#makeObjectAndRelation()
	 */
	@Override
	protected void makeObjectAndRelation() {
		Point location = wrapper.getCurrentMousePosition();
		final ObjectArtifact newObject = new ObjectArtifact(this, idCount, classToAttach);
		this.add(newObject);
		newObject.moveTo(Point.substract(location, getCanvasOffset()));

		LinkArtifact newLink = makeLinkBetween(objectToAttach, newObject);
		this.add(newLink);
		linkingModeOff();
		deselectAllArtifacts();
	}

	@Override
	public void remove(final UMLArtifact umlArtifact) {
		if (umlArtifact instanceof ClassSimplifiedArtifact) {
			ClassSimplifiedArtifact classArtifact = (ClassSimplifiedArtifact) umlArtifact;
			objectDiagramClasses.remove(classArtifact.toUMLComponent());
		}
		super.remove(umlArtifact);
	}

	@Override
	public void dropContextualMenu(GfxObject gfxObject, Point location) {
		doSelection(gfxObject, false, false);

		MenuBarAndTitle rightMenu;
		MenuBarAndTitle relationMenu = null;
		if (getUMLArtifact(gfxObject) == null) {
			rightMenu = null;
		} else {
			rightMenu = getUMLArtifact(gfxObject).getRightMenu();
			relationMenu = createRelationMenu(gfxObject);
		}

		ContextMenu contextMenu = createObjectDiagramContextMenu(location, this, rightMenu, relationMenu);

		contextMenu.show();
	}


	@Override
	public List<UMLObject> getObjects() {
		ArrayList<UMLObject> umlObjects = new ArrayList<UMLObject>();

		for (final UMLArtifact umlArtifact : getArtifactById().values()) {
			if (umlArtifact instanceof ObjectArtifact) {
				ObjectArtifact objectArtifact = (ObjectArtifact) umlArtifact;
				umlObjects.add(objectArtifact.toUMLComponent());
			}
		}
		return umlObjects;
	}

	@Override
	public List<InstantiationRelation> getInstantiationRelations() {
		List<InstantiationRelation> instantiations = new ArrayList<InstantiationRelation>();

		for (final UMLArtifact umlArtifact : getArtifactById().values()) {
			if (umlArtifact instanceof InstantiationRelationLinkArtifact) {
				InstantiationRelationLinkArtifact instantiationArtifact = (InstantiationRelationLinkArtifact) umlArtifact;
				instantiations.add(instantiationArtifact.toUMLComponent());
			}
		}

		return instantiations;
	}

	@Override
	public List<ObjectRelation> getObjectRelations() {
		List<ObjectRelation> relations = new ArrayList<ObjectRelation>();

		for (final UMLArtifact umlArtifact : getArtifactById().values()) {
			if (umlArtifact instanceof ObjectRelationLinkArtifact) {
				ObjectRelationLinkArtifact relationArtifact = (ObjectRelationLinkArtifact) umlArtifact;
				relations.add(relationArtifact.toUMLComponent());
			}
		}

		return relations;
	}

	@Override
	public List<UMLClass> getClasses() {
		// If there is at least one class defined in the domain classes or in the object diagram
		// return the concatenation of the two lists.
		if (domainClasses.size() != 0 || objectDiagramClasses.size() != 0) {
			List<UMLClass> classes = new ArrayList<UMLClass>(domainClasses);
			classes.addAll(objectDiagramClasses);
			return classes;
		}

		// Else return a list with a default UmlClass.
		List<UMLClass> classes = new ArrayList<UMLClass>();
		classes.add(new UMLClass("Object"));
		return classes;
	}

	@Override
	public List<UMLRelation> getClassRelations() {
		return classRelations;
	}

	@Override
	public void setClasses(List<UMLClass> classes) {
		domainClasses = classes;
	}

	@Override
	public void setClassRelations(List<UMLRelation> classRelations) {
		this.classRelations = classRelations;
	}
}

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
package com.objetdirect.gwt.umlapi.client.artifacts.clazz;

import java.util.List;

import com.objetdirect.gwt.umlapi.client.artifacts.NodeArtifact;
import com.objetdirect.gwt.umlapi.client.artifacts.NodePartArtifact;
import com.objetdirect.gwt.umlapi.client.contextMenu.MenuBarAndTitle;
import com.objetdirect.gwt.umlapi.client.umlCanvas.UMLCanvas;
import com.objetdirect.gwt.umlapi.client.umlcomponents.UMLClass;
import com.objetdirect.gwt.umlapi.client.umlcomponents.UMLClassAttribute;
import com.objetdirect.gwt.umlapi.client.umlcomponents.UMLClassMethod;

/**
 * This class is an artifact used to represent a class. <br>
 * A class is divided in three {@link NodePartArtifact} :
 * <ul>
 * <li>{@link ClassPartNameArtifact} For the name and stereotype part</li>
 * <li>{@link ClassPartAttributesArtifact} For the attribute list part</li>
 * <li>{@link ClassPartMethodsArtifact} For the method list part</li>
 * </ul>
 * 
 * @author Florian Mounier (mounier-dot-florian.at.gmail'dot'com)
 * @Contributor Raphael Brugier (raphael-dot-brugier.at.gmail'dot'com)
 */
public class ClassArtifact extends NodeArtifact {

	/**
	 * /!\ Don't forget to increment the serialVersionUID if you change any of the fields above /!\
	 */
	private static final long serialVersionUID = 1L;

	ClassPartAttributesArtifact classAttributes;
	private ClassPartMethodsArtifact classMethods;
	ClassPartNameArtifact className;

	private UMLClass ownedClass;

	/** Default constructor ONLY for GWT-RPC serialization. */
	@Deprecated
	@SuppressWarnings("unused")
	private ClassArtifact() {
	}

	/**
	 * ClassArtifact constructor, initializes the {@link NodeArtifact} with a name and without stereotype
	 * 
	 * @param canvas
	 *            Where the gfxObjects are displayed
	 * @param id
	 *            The artifacts's id
	 * @param className
	 *            The name of the class, sent to {@link ClassPartNameArtifact} constructor
	 */
	public ClassArtifact(final UMLCanvas canvas, int id, final String className) {
		this(canvas, id, className, "");
	}

	/**
	 * ClassArtifact constructor, initializes all {@link NodePartArtifact}
	 * 
	 * @param canvas
	 *            Where the gfxObjects are displayed
	 * @param id
	 *            The artifacts's id
	 * @param className
	 *            The name of the class, sent to {@link ClassPartNameArtifact} constructor
	 * @param stereotype
	 *            The stereotype of the class, sent to {@link ClassPartNameArtifact} constructor
	 */
	public ClassArtifact(final UMLCanvas canvas, int id, final String className, final String stereotype) {
		super(canvas, id);
		ownedClass = new UMLClass(className);
		this.className = new ClassPartNameArtifact(canvas, ownedClass, stereotype);
		classAttributes = new ClassPartAttributesArtifact(canvas, ownedClass.getAttributes());
		classMethods = new ClassPartMethodsArtifact(canvas, ownedClass.getMethods());
		nodeParts.add(this.className);
		nodeParts.add(classAttributes);
		nodeParts.add(classMethods);
		this.className.setNodeArtifact(this);
		classAttributes.setNodeArtifact(this);
		classMethods.setNodeArtifact(this);
	}

	/**
	 * Add an {@link UMLClassAttribute} to this class
	 * 
	 * @param attribute
	 *            The attribute, sent to {@link ClassPartAttributesArtifact}
	 */
	public void addAttribute(final UMLClassAttribute attribute) {
		classAttributes.addAttribute(attribute);
	}

	/**
	 * Add a {@link UMLClassMethod} to this class
	 * 
	 * @param method
	 *            The method, sent to {@link ClassPartMethodsArtifact}
	 */

	public void addMethod(final UMLClassMethod method) {
		classMethods.add(method);
	}

	/**
	 * Getter for the attributes
	 * 
	 * @return the list of attributes of this class
	 */
	public List<UMLClassAttribute> getAttributes() {
		return ownedClass.getAttributes();
	}

	/**
	 * Getter for the methods
	 * 
	 * @return the list of methods of this class
	 */
	public List<UMLClassMethod> getMethods() {
		return classMethods.getList();
	}

	/**
	 * Getter for the name
	 * 
	 * @return the name of this class
	 */
	@Override
	public String getName() {
		return className.getClassName();
	}

	@Override
	public MenuBarAndTitle getRightMenu() {
		final MenuBarAndTitle rightMenu = new MenuBarAndTitle();
		final MenuBarAndTitle classNameRightMenu = className.getRightMenu();
		final MenuBarAndTitle classAttributesRightMenu = classAttributes.getRightMenu();
		final MenuBarAndTitle classMethodsRightMenu = classMethods.getRightMenu();

		rightMenu.setName("Class" + className.getClassName());

		rightMenu.addItem(classNameRightMenu.getName(), classNameRightMenu.getSubMenu());
		rightMenu.addItem(classAttributesRightMenu.getName(), classAttributesRightMenu.getSubMenu());
		rightMenu.addItem(classMethodsRightMenu.getName(), classMethodsRightMenu.getSubMenu());

		return rightMenu;
	}

	public UMLClass toUMLComponent() {
		return ownedClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.objetdirect.gwt.umlapi.client.artifacts.UMLArtifact#toURL()
	 */
	@Override
	public String toURL() {
		return "Class$" + this.getLocation() + "!" + className.toURL() + "!" + classAttributes.toURL() + "!" + classMethods.toURL();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.objetdirect.gwt.umlapi.client.artifacts.UMLArtifact#setUpAfterDeserialization()
	 */
	@Override
	public void setUpAfterDeserialization() {
		classAttributes.setUpAfterDeserialization();
		classMethods.setUpAfterDeserialization();
		className.setUpAfterDeserialization();
	}
}

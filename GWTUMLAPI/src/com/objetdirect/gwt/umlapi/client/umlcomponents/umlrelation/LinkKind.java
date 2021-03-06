package com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation;

import static com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.LinkAdornment.INVERTED_SOLID_ARROW;
import static com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.LinkAdornment.INVERTED_SOLID_CIRCLE;
import static com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.LinkAdornment.INVERTED_SOLID_DIAMOND;
import static com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.LinkAdornment.NONE;
import static com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.LinkAdornment.SOLID_ARROW;
import static com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.LinkAdornment.SOLID_DIAMOND;
import static com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.LinkAdornment.WIRE_ARROW;
import static com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.LinkAdornment.WIRE_CROSS;
import static com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.LinkStyle.DASHED;
import static com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.LinkStyle.DASHED_DOTTED;
import static com.objetdirect.gwt.umlapi.client.umlcomponents.umlrelation.LinkStyle.SOLID;

import com.objetdirect.gwt.umlapi.client.umlcomponents.UMLClass;

/**
 * This enumeration lists all the relations type between two {@link UMLClass}es
 * 
 * @author Florian Mounier (mounier-dot-florian.at.gmail'dot'com)
 * 
 */
public enum LinkKind {
	//
	// CLASS TO CLASS RELATIONS
	//
	/**
	 * Aggregation relation
	 */
	AGGREGATION_RELATION("Aggregation", SOLID_DIAMOND, WIRE_ARROW, "1", "*", SOLID),
	/**
	 * Association relation
	 */
	ASSOCIATION_RELATION("Association", NONE, WIRE_ARROW, "1", "*", SOLID),
	/**
	 * Composition relation
	 */
	COMPOSITION_RELATION("Composition", INVERTED_SOLID_DIAMOND, WIRE_ARROW, "", "1", SOLID),
	/**
	 * Dependency relation
	 */
	DEPENDENCY_RELATION("Dependency", WIRE_CROSS, WIRE_ARROW, "", "", DASHED),
	/**
	 * Generalization relation
	 */
	GENERALIZATION_RELATION("Generalization", NONE, SOLID_ARROW , "", "", SOLID),

	//
	// CLASS TO ANY CLASS-TO-CLASS RELATION
	//
	/**
	 * Class relation
	 */
	CLASSRELATION("Class Relation", NONE, NONE, "", "", SOLID),

	//
	// OBJECT TO CLASS
	//
	/**
	 * Class Object instantiation
	 */
	INSTANTIATION("Instantiation", NONE, WIRE_ARROW, "", "", DASHED_DOTTED),

	//
	// OBJECT TO OBJECT
	//
	/**
	 * Relation between to object
	 */
	OBJECT_RELATION("Relation", NONE, WIRE_ARROW, "", "", SOLID),
	/**
	 * Relation between to object
	 */
	OBJECT_RELATION_WITH_CLASSNAME("Relation", NONE, WIRE_ARROW, "", "", SOLID),

	//
	// LIFE LINE TO LIFE LINE
	//
	/**
	 * Asynchronous message
	 */
	ASYNCHRONOUS_MESSAGE("Asynchronous", WIRE_ARROW, NONE, "", "", SOLID),
	/**
	 * Synchronous message
	 */
	SYNCHRONOUS_MESSAGE("Synchronous", INVERTED_SOLID_ARROW, NONE, "", "", SOLID),
	/**
	 * Reply message
	 */
	REPLY_MESSAGE("Reply", NONE, NONE, "", "", DASHED),
	/**
	 * Object Creation message
	 */
	OBJECT_CREATION_MESSAGE("Object Creation", WIRE_ARROW, NONE, "", "", DASHED),
	/**
	 * Lost message
	 */
	LOST_MESSAGE("Lost", INVERTED_SOLID_CIRCLE, NONE, "", "", SOLID),
	/**
	 * Found message
	 */
	FOUND_MESSAGE("Found", WIRE_ARROW, INVERTED_SOLID_CIRCLE, "", "", SOLID),

	//
	// NOTE TO ANY UMLCOMPONENT
	//
	/**
	 * Note relation
	 */
	NOTE("Note link", NONE, NONE, "", "", SOLID);

	/**
	 * Static getter of a {@link LinkKind} by its name
	 * 
	 * @param relationKindName
	 *            The name of the {@link LinkKind} to retrieve
	 * @return The {@link LinkKind} that has relationKindName for name or null if not found
	 */
	public static LinkKind getRelationKindFromName(final String relationKindName) {
		for (final LinkKind relationKind : LinkKind.values()) {
			if (relationKind.getName().equals(relationKindName)) {
				return relationKind;
			}
		}
		return null;
	}

	private String name;
	private LinkAdornment defaultLeftAdornment;
	private LinkAdornment defaultRightAdornment;
	private String defaultLeftCardinality;
	private String defaultRightCardinality;
	private LinkStyle defaultLinkStyle;

	/**
	 * Default constructor ONLY for gwt-rpc serialization
	 */
	private LinkKind() {
	}

	private LinkKind(final String name, final LinkAdornment defaultLeftAdornment, final LinkAdornment defaultRightAdornment,
			final String defaultLeftCardinality, final String defaultRightCardinality, final LinkStyle defaultLinkStyle) {

		this.name = name;
		this.defaultLeftAdornment = defaultLeftAdornment;
		this.defaultRightAdornment = defaultRightAdornment;
		this.defaultLeftCardinality = defaultLeftCardinality;
		this.defaultRightCardinality = defaultRightCardinality;
		this.defaultLinkStyle = defaultLinkStyle;
	}

	/**
	 * Getter for the defaultLeftAdornment
	 * 
	 * @return the defaultLeftAdornment
	 */
	public LinkAdornment getDefaultLeftAdornment() {
		return defaultLeftAdornment;
	}

	/**
	 * Getter for the defaultLeftCardinality
	 * 
	 * @return the defaultLeftCardinality
	 */
	public String getDefaultLeftCardinality() {
		return defaultLeftCardinality;
	}

	/**
	 * Getter for the defaultLinkStyle
	 * 
	 * @return the defaultLinkStyle
	 */
	public LinkStyle getDefaultLinkStyle() {
		return defaultLinkStyle;
	}

	/**
	 * Getter for the defaultRightAdornment
	 * 
	 * @return the defaultRightAdornment
	 */
	public LinkAdornment getDefaultRightAdornment() {
		return defaultRightAdornment;
	}

	/**
	 * Getter for the defaultRightCardinality
	 * 
	 * @return the defaultRightCardinality
	 */
	public String getDefaultRightCardinality() {
		return defaultRightCardinality;
	}

	/**
	 * Getter for the name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public boolean isClassToClassRelation() {
		if (
				this.equals(ASSOCIATION_RELATION) ||
				this.equals(AGGREGATION_RELATION) ||
				this.equals(COMPOSITION_RELATION) ||
				this.equals(DEPENDENCY_RELATION) ||
				this.equals(GENERALIZATION_RELATION)) {

			return true;
		}
		return false;
	}
}
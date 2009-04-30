/**
 * 
 */
package com.objetdirect.gwt.umlapi.client.editors;

import com.google.gwt.user.client.Window;
import com.objetdirect.gwt.umlapi.client.UMLDrawerException;
import com.objetdirect.gwt.umlapi.client.analyser.LexicalAnalyzer;
import com.objetdirect.gwt.umlapi.client.analyser.LexicalAnalyzer.LexicalFlag;
import com.objetdirect.gwt.umlapi.client.artifacts.ClassPartArtifact;
import com.objetdirect.gwt.umlapi.client.artifacts.ClassPartAttributesArtifact;
import com.objetdirect.gwt.umlapi.client.umlcomponents.Attribute;
import com.objetdirect.gwt.umlapi.client.umlcomponents.Visibility;
import com.objetdirect.gwt.umlapi.client.webinterface.UMLCanvas;

/**
 * @author Florian Mounier (mounier-dot-florian.at.gmail'dot'com)
 */
public class AttributePartEditor extends FieldEditor {

    Attribute attributeToChange;

    /**
     * @param canvas
     * @param artifact
     * @param attributeToChange
     */
    public AttributePartEditor(final UMLCanvas canvas,
	    final ClassPartAttributesArtifact artifact,
	    final Attribute attributeToChange) {
	super(canvas, artifact);
	this.attributeToChange = attributeToChange;
    }

    @Override
    protected void next() {
	((ClassPartArtifact) this.artifact).edit();
    }

    @Override
    protected boolean updateUMLArtifact(final String newContent) {
	if (newContent.equals("")) {
	    ((ClassPartAttributesArtifact) this.artifact).remove(this.attributeToChange);
	    ((ClassPartAttributesArtifact) this.artifact).getClassArtifact()
		    .rebuildGfxObject();
	    return false;
	}

	final LexicalAnalyzer lex = new LexicalAnalyzer(newContent);
	try {
	    String type = null;
	    String name = null;
	    Visibility visibility = Visibility.PACKAGE;
	    LexicalAnalyzer.Token tk = lex.getToken();
	    if (tk != null && tk.getType() != LexicalFlag.VISIBILITY) {
		visibility = Visibility.PACKAGE;
	    } else if (tk != null) {
		visibility = Visibility.getVisibilityFromToken(tk.getContent()
			.charAt(0));
		tk = lex.getToken();
	    }
	    if (tk == null || tk.getType() != LexicalFlag.IDENTIFIER) {
		throw new UMLDrawerException(
			"invalid format : must match 'identifier:type'");
	    }
	    name = tk.getContent();
	    tk = lex.getToken();
	    if (tk != null) {
		if (tk.getType() != LexicalFlag.SIGN
			|| !tk.getContent().equals(":")) {
		    throw new UMLDrawerException(
			    "invalid format : must match 'identifier:type'");
		}
		tk = lex.getToken();
		if (tk == null || tk.getType() != LexicalFlag.IDENTIFIER) {
		    throw new UMLDrawerException(
			    "invalid format : must match 'identifier:type'");
		}
		type = tk.getContent();
	    }
	    this.attributeToChange.setVisibility(visibility);
	    this.attributeToChange.setName(name);
	    this.attributeToChange.setType(type);
	    ((ClassPartAttributesArtifact) this.artifact).getClassArtifact()
		    .rebuildGfxObject();
	} catch (final UMLDrawerException e) {
	    Window.alert(e.getMessage());
	}
	return true;
    }
}

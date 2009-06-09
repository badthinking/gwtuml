/*
 *    This file is part of the GWTUML project
 *    and was written by Mounier Florian <mounier-dot-florian.at.gmail'dot'com> 
 *    for Objet Direct <http://wwww.objetdirect.com>
 *    
 *    Copyright © 2009 Objet Direct
 *    Contact: gwtuml@googlegroups.com
 *    
 *    GWTUML is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    GWTUML is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with GWTUML. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.objetdirect.gwt.umldrawer.client.webinterface;

import java.util.HashMap;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.objetdirect.gwt.umlapi.client.artifacts.UMLArtifact;



/**
 * @author Florian Mounier (mounier-dot-florian.at.gmail'dot'com)
 *
 */
public class HistoryManager implements ValueChangeHandler<String> {
    private static String lastHistoryAnchor = "";
    private static HashMap<String, String> lastHistoryParametersList = new HashMap<String, String>();
    private static SimplePanel applicationPanel = new SimplePanel();
    private static String urlDiagram = "";



    /**
     * Initialize the history management and therefore the application 
     * 
     * @param appRootPanel The panel on which we can put the pages
     */
    public void initApplication(DockPanel appRootPanel) {
	History.addValueChangeHandler(this);
	appRootPanel.add(applicationPanel, DockPanel.CENTER);
	applicationPanel.setSize("100%", "100%");
	parseHistoryToken(History.getToken());
	processHistory();
	Window.addCloseHandler(new CloseHandler<Window>() {
		
	    @Override
	    public void onClose(CloseEvent<Window> event) {
		if(lastHistoryAnchor.equals("Drawer")) {
		HistoryManager.upgradeDiagramURL(Session.getActiveCanvas().toUrl());
		}
	    }
	});
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)
     */
    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
	String historyToken = event.getValue();	
	parseHistoryToken(historyToken);
	processHistory();
    }


    private void processHistory() {
	applicationPanel.clear();
	//DOM.setStyleAttribute(Log.getDivLogger().getWidget().getElement(), "display", "none");

	OptionsManager.setAllFromURL(lastHistoryParametersList);
	UMLArtifact.getArtifactList().clear();
	if(lastHistoryAnchor.equals("Drawer")) {
	    DrawerPanel drawerPanel = new DrawerPanel();
	    if(urlDiagram.equals("")) {
		drawerPanel.addDefaultNode();
	    } else {
		Session.getActiveCanvas().fromURL(urlDiagram);
	    }	    
	    applicationPanel.add(drawerPanel);
	} else if(lastHistoryAnchor.equals("Demo")) {
	    DrawerPanel drawerPanel = new DrawerPanel();
	    new Demo(drawerPanel.getUMLCanvas());
	    applicationPanel.add(drawerPanel);
	} else if(lastHistoryAnchor.equals("AnimatedDemo")) {
	    DrawerPanel drawerPanel = new DrawerPanel();
	    new AnimatedDemo(drawerPanel.getUMLCanvas());
	    applicationPanel.add(drawerPanel);
	} else { 
	    History.newItem("Start", false);
	    urlDiagram = "";
	    applicationPanel.add(new StartPanel(false));
	}
    }

    private void parseHistoryToken(String historyToken) {
	String[] parts = historyToken.split("\\?");
	lastHistoryAnchor = parts[0];
	lastHistoryParametersList.clear();
	if(parts.length > 1) {
	    String[] params = parts[1].split("&");
	    for (int i = 0; i < params.length; i++) {
		String argument = params[i];
		String[] paramVar = argument.split("=", 2);
		if(paramVar.length > 0 && paramVar[0].length() > 0) {
		    if(!paramVar[0].equals("diagram64")) {
			lastHistoryParametersList.put(paramVar[0], paramVar.length > 1 ? paramVar[1] : "");
		    } else {
			urlDiagram  = paramVar.length > 1 ? paramVar[1] : "";
		    }
		}
	    }
	}
    }

    static void upgradeDiagramURL(String url) {	
	String historyToken = lastHistoryAnchor + "?" + OptionsManager.toURL(); 
	if(!historyToken.endsWith("&")) {
	    historyToken += "&";
	}
	historyToken += "diagram64=" + url;
	History.newItem(historyToken, false);
    }
}
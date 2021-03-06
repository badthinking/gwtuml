/*
 * This file is part of the Gwt-Uml project and was written by Rapha�l Brugier <raphael dot brugier at gmail dot com > for Objet Direct
 * <http://wwww.objetdirect.com>
 * 
 * Copyright � 2010 Objet Direct
 * 
 * Gwt-Uml is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * Gwt-Uml is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Gwt-Generator. If not, see <http://www.gnu.org/licenses/>.
 */
package com.objetdirect.gwt.umlapi.client.umlCanvas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.objetdirect.gwt.umlapi.client.Drawer;
import com.objetdirect.gwt.umlapi.client.engine.Direction;
import com.objetdirect.gwt.umlapi.client.engine.Point;
import com.objetdirect.gwt.umlapi.client.engine.Scheduler;
import com.objetdirect.gwt.umlapi.client.gfx.GfxManager;
import com.objetdirect.gwt.umlapi.client.gfx.GfxObject;
import com.objetdirect.gwt.umlapi.client.helpers.OptionsManager;
import com.objetdirect.gwt.umlapi.client.helpers.ThemeManager;
import com.objetdirect.gwt.umlapi.client.helpers.CursorIconManager.PointerStyle;

/**
 * Wrap the Canvas with the sides panel. This wrapper also permits to the canvas to display a Field Editor and a
 * HelpText on the panel at the given position. This class manage the mouse position on the canvas.
 * 
 * @author Raphael Brugier (raphael-dot-brugier.at.gmail'dot'com)
 */
public class DecoratorCanvas extends AbsolutePanel {
	private final int directionPanelSizes = OptionsManager.get("DirectionPanelSizes");
	private final double opacityValue = (double) OptionsManager.get("DirectionPanelOpacity") / 100;
	private final double opacityMax = (double) OptionsManager.get("DirectionPanelMaxOpacity") / 100;
	private final String backgroundColor = ThemeManager.getTheme().getDirectionPanelColor().toString();

	private final UMLCanvas umlCanvas;
	private final Drawer drawer;

	private int height;
	private int width;

	private HashMap<Direction, FocusPanel> sidePanels;
	private GfxObject arrowsVirtualGroup;

	private final Label helpTextLabel;

	private HandlerRegistration mouseHandlerRegistration;

	/**
	 * The current mouse position on the Canvas. A (0,0) position means the mouse is in the left top corner of the
	 * canvas.
	 */
	private final Point currentMousePosition;

	/**
	 * Construct a panel that wraps the canvas. This panel is displayed in the given drawer
	 * 
	 * @param drawer
	 * @param umlCanvas
	 */
	public DecoratorCanvas(Drawer drawer, UMLCanvas umlCanvas) {
		this.umlCanvas = umlCanvas;
		this.drawer = drawer;
		umlCanvas.setDecoratorPanel(this);

		this.add(umlCanvas.getDrawingCanvas(), 0, 0);
		makeSidePanels();

		helpTextLabel = new Label("");
		helpTextLabel.setStylePrimaryName("contextual-help");
		this.add(helpTextLabel, 0, 0);

		currentMousePosition = new Point(0, 0);
	}

	/**
	 * Attach a mouse move handler to the windows that will refresh the current mouse position Point on the canvas. This
	 * position is the real position on the canvas calculated from its container's size and position .
	 * */
	private void attachCurrentMousePositionHandler() {

		mouseHandlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (event.getTypeInt() == Event.ONMOUSEMOVE) {
					int xCoord = event.getNativeEvent().getClientX();
					int yCoord = event.getNativeEvent().getClientY();
					Point mousePositionOnCanvas = calculateMousePosition(xCoord, yCoord);
					currentMousePosition.setX(mousePositionOnCanvas.getX());
					currentMousePosition.setY(mousePositionOnCanvas.getY());
					Log.trace("MouseControler::MouseMoveEvent update mousePosition on : " + currentMousePosition);
				}
			}
		});
	}

	/**
	 * Calculate the mouse position on the canvas from the absolute mouse position. The absolute mouse position is the
	 * position of the mouse from the top left corner of the window. The calculated position means that the position
	 * (0,0) is the position for the mouse on the top left corner of the canvas.
	 * 
	 * @param left
	 * @param top
	 * @return the calculated Point.
	 */
	private Point calculateMousePosition(int left, int top) {
		int canvasLeftPosition = umlCanvas.getDrawingCanvas().getAbsoluteLeft();
		int canvasTopPosition = umlCanvas.getDrawingCanvas().getAbsoluteTop();

		int calculatedLeft = left + RootPanel.getBodyElement().getScrollLeft() - canvasLeftPosition;
		int calculatedTop = top + RootPanel.getBodyElement().getScrollTop() - canvasTopPosition;

		return new Point(calculatedLeft, calculatedTop);
	}

	private void detachCurrentMousePositionHandler() {
		if (mouseHandlerRegistration != null) {
			mouseHandlerRegistration.removeHandler();
		}
	}

	/**
	 * Make the set of sidePanels
	 */
	private void makeSidePanels() {
		sidePanels = new HashMap<Direction, FocusPanel>();
		for (Direction direction : Direction.sideValues()) {
			FocusPanel sidePanel = new FocusPanel();
			sidePanels.put(direction, sidePanel);

			Point pixelSize = getSidePanelSize(direction);
			sidePanel.setPixelSize(pixelSize.getX(), pixelSize.getY());

			sidePanel.getElement().getStyle().setBackgroundColor(ThemeManager.getTheme().getDirectionPanelColor().toString());
			sidePanel.getElement().getStyle().setOpacity(opacityValue);
			sidePanel.getElement().setAttribute("oncontextmenu", "return false");

			addSidePanelHandlers(direction, sidePanel);

			this.add(sidePanel);
		}
		setSidePanelsPositionAndSize();
	}

	private void addSidePanelHandlers(final Direction direction, final FocusPanel sidePanel) {
		sidePanel.getElement().getStyle().setBackgroundColor(backgroundColor);
		sidePanel.getElement().getStyle().setOpacity(opacityValue);

		sidePanel.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(final MouseOverEvent event) {
				for (double d = opacityValue; d <= opacityMax; d += 0.05) {
					final double opacity = Math.ceil(d * 100) / 100;

					new Scheduler.Task("Opacifying") {
						@Override
						public void process() {
							sidePanel.getElement().getStyle().setOpacity(opacity);
						}
					};
				}
				new Scheduler.Task("MovingAllArtifacts") {
					@Override
					public void process() {
						Scheduler.cancel("MovingAllArtifactsRecursive");
						umlCanvas.moveAll(direction.withSpeed(Direction.getDependingOnQualityLevelSpeed()), true);
					}
				};
			}
		});

		sidePanel.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(final MouseOutEvent event) {
				Scheduler.cancel("Opacifying");
				Scheduler.cancel("MovingAllArtifacts");
				Scheduler.cancel("MovingAllArtifactsRecursive");

				sidePanel.getElement().getStyle().setOpacity(opacityValue);

			}
		});

		sidePanel.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(final MouseDownEvent event) {
				sidePanel.getElement().getStyle().setBackgroundColor(ThemeManager.getTheme().getDirectionPanelPressedColor().toString());
				Scheduler.cancel("MovingAllArtifactsRecursive");
			}
		});

		sidePanel.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(final MouseUpEvent event) {
				sidePanel.getElement().getStyle().setBackgroundColor(ThemeManager.getTheme().getDirectionPanelColor().toString());
				umlCanvas.moveAll(direction.withSpeed(Math.min(DecoratorCanvas.this.getOffsetHeight(), DecoratorCanvas.this.getOffsetWidth())), false);
			}
		});
	}

	/**
	 * For each side panel calculate its size and position and set it on the absolute Panel (this).
	 */
	private void setSidePanelsPositionAndSize() {
		for (Entry<Direction, FocusPanel> entry : sidePanels.entrySet()) {
			Direction direction = entry.getKey();
			FocusPanel sidePanel = entry.getValue();

			Point pixelSize = getSidePanelSize(direction);
			sidePanel.setPixelSize(pixelSize.getX(), pixelSize.getY());

			Point sidePanelPosition = getSidePanelPosition(direction);
			this.setWidgetPosition(sidePanel, sidePanelPosition.getX(), sidePanelPosition.getY());
		}
	}

	/**
	 * Get a side panel position from a direction. Ie : left up panel is positioned on the point 0,0 ; etc
	 * 
	 * @param direction
	 * @return
	 */
	private Point getSidePanelPosition(Direction direction) {
		switch (direction) {
			case UP_LEFT:
				return Point.getOrigin();
			case UP:
				return new Point(directionPanelSizes, 0);
			case UP_RIGHT:
				return new Point(width - directionPanelSizes, 0);
			case RIGHT:
				return new Point(width - directionPanelSizes, directionPanelSizes);
			case DOWN_RIGHT:
				return new Point(width - directionPanelSizes, height - directionPanelSizes);
			case DOWN:
				return new Point(directionPanelSizes, height - directionPanelSizes);
			case DOWN_LEFT:
				return new Point(0, height - directionPanelSizes);
			case LEFT:
				return new Point(0, directionPanelSizes);
			default:
				throw new UnsupportedOperationException("Unknow direction for a side panel");
		}
	}

	/**
	 * Get a size for a Panel from its direction Ie : left up panel is of default size, Up panel require a width of the
	 * with of the panel - the left up and right panel's width
	 * 
	 * @param direction
	 * @return
	 */
	private Point getSidePanelSize(Direction direction) {
		switch (direction) {
			case UP_LEFT:
				return new Point(directionPanelSizes, directionPanelSizes);
			case UP:
				return new Point(width - 2 * directionPanelSizes, directionPanelSizes);
			case UP_RIGHT:
				return new Point(directionPanelSizes, directionPanelSizes);
			case RIGHT:
				return new Point(directionPanelSizes, height - 2 * directionPanelSizes);
			case DOWN_RIGHT:
				return new Point(directionPanelSizes, directionPanelSizes);
			case DOWN:
				return new Point(width - 2 * directionPanelSizes, directionPanelSizes);
			case DOWN_LEFT:
				return new Point(directionPanelSizes, directionPanelSizes);
			case LEFT:
				return new Point(directionPanelSizes, height - 2 * directionPanelSizes);
			default:
				throw new UnsupportedOperationException("Unknow direction for a side panel direction = " + direction);
		}
	}

	/**
	 * Draw the directions arrow on the canvas
	 */
	private void drawArrows() {
		if (arrowsVirtualGroup != null) {
			GfxManager.getPlatform().clearVirtualGroup(arrowsVirtualGroup);
		}

		final int arrowSize = 6;
		arrowsVirtualGroup = GfxManager.getPlatform().buildVirtualGroup();
		arrowsVirtualGroup.addToCanvas(umlCanvas.getDrawingCanvas(), Point.getOrigin());
		final ArrayList<GfxObject> arrowList = new ArrayList<GfxObject>();
		for (float f = 0; f < 360; f += 45) {
			final GfxObject arrow = GfxManager.getPlatform().buildPath();
			arrowList.add(arrow);
			GfxManager.getPlatform().moveTo(arrow, Point.getOrigin());
			GfxManager.getPlatform().lineTo(arrow, new Point(arrowSize, 0));
			GfxManager.getPlatform().lineTo(arrow, new Point(2 * arrowSize, arrowSize));
			GfxManager.getPlatform().lineTo(arrow, new Point(arrowSize, 2 * arrowSize));
			GfxManager.getPlatform().lineTo(arrow, new Point(0, 2 * arrowSize));
			GfxManager.getPlatform().lineTo(arrow, new Point(arrowSize, arrowSize));
			GfxManager.getPlatform().lineTo(arrow, Point.getOrigin());
			arrow.setFillColor(ThemeManager.getTheme().getDefaultForegroundColor());
			arrow.setStroke(ThemeManager.getTheme().getDefaultForegroundColor(), 1);
			arrow.rotate(f, new Point(arrowSize, arrowSize));
			arrow.addToVirtualGroup(arrowsVirtualGroup);
		}

		arrowList.get(0).translate(new Point(width - 2 * arrowSize - 2, height / 2 - arrowSize - 2)); // right
		arrowList.get(1).translate(new Point(width - 2 * arrowSize - 2, height - 2 * arrowSize - 2)); // bottom right
		arrowList.get(2).translate(new Point(width / 2 - arrowSize - 2, height - 2 * arrowSize - 2)); // bottom
		arrowList.get(3).translate(new Point(2, height - 2 * arrowSize - 2)); // bottom left
		arrowList.get(4).translate(new Point(2, height / 2 - arrowSize - 2)); // left
		arrowList.get(5).translate(new Point(2, 2)); // up left
		arrowList.get(6).translate(new Point(width / 2 - arrowSize - 2, 2)); // up
		arrowList.get(7).translate(new Point(width - 2 * arrowSize - 2, 2)); // up right
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		attachCurrentMousePositionHandler();
		umlCanvas.onLoad();
	}

	@Override
	protected void onUnload() {
		super.onUnload();
		detachCurrentMousePositionHandler();
	}

	/**
	 * Change the size of the decorator panel. This will cause to resize the arrows and the encapsulated drawingCanvas.
	 * 
	 * @param width
	 *            the new width
	 * @param height
	 *            the new height
	 */
	public void reSize(int width, int height) {
		this.width = width;
		this.height = height;
		this.setPixelSize(width, height);
		setSidePanelsPositionAndSize();
		GfxManager.getPlatform().setSize(umlCanvas.getDrawingCanvas(), width, height);
		drawArrows();
	}

	/**
	 * Set a help Text on the canvas at a given position
	 * 
	 * @param text
	 * @param location
	 */
	public void setHelpText(String text, Point location) {
		helpTextLabel.setText(text);
		moveHelpText(location);
	}

	public void moveHelpText(Point location) {
		int left = location.getX() + 5;
		int top = location.getY() - helpTextLabel.getOffsetHeight() - 5;

		this.setWidgetPosition(helpTextLabel, left, top);
	}

	/**
	 * @return a copy of the currentMousePosition
	 */
	public Point getCurrentMousePosition() {
		return currentMousePosition.clonePoint();
	}

	/**
	 * @param hotKeysEnabled
	 *            the hotKeysEnabled to set
	 */
	public void setHotKeysEnabled(boolean hotKeysEnabled) {
		drawer.setHotKeysEnabled(hotKeysEnabled);
	}

	/**
	 * Set the cursor icon on the canvas
	 * 
	 * @param style
	 *            the icon to set
	 */
	public void setCursorIcon(PointerStyle style) {
		drawer.setCursorIcon(style);
	}
}

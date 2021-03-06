package se.umu.cs.ldbn.client.ui;

import se.umu.cs.ldbn.client.Main;
import se.umu.cs.ldbn.client.utils.Common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget, which consist of a header and a content widget. The header widget
 * is provided the content widget must be passed as a argument to the
 * constructor. This widget is very useful for applications which take a lot of
 * vertical space, because it can be resized manually with the mouse, or it can
 * be  collapsed so only the header can be visible.
 */
public final class DisclosureWidget extends Composite implements ClickHandler,
		MouseOverHandler, MouseOutHandler {

	/**
	 * Timer for the blind up/down effect.
	 */
	private class FXTimer extends Timer {
		public void run() {
			if (isOpen) {
				if (!decrementHeight(pxStep)) {
					isFxFinished = true;
					fxT.cancel();
					resizer.setVisible(false);
					scroller.setVisible(false);
					isOpen = false;
					collapseButton.setVisibleRect(useShadows ? 15 : 0, 0, 15,
							15);
					headerControlsPanel.setVisible(false);
				}
			} else {
				if (!incHeight(pxStep)) {
					isFxFinished = true;
					fxT.cancel();
					resizer.setVisible(true);
					headerControlsPanel.setVisible(true);
					isOpen = true;
					collapseButton.setVisibleRect(useShadows ? 15 : 0, 15, 15,
							15);
				}
			}
		}
	}
	/**
	 * Panel used for resizing. It uses directly the DOM classes for capturing
	 * mouse events because, the widget has to be aware of drag events even
	 * when the mouse leaves the widget area, therefore no mouse listener can
	 * be applied.
	 */
	private class ResizePanel extends HTML {

		public ResizePanel() {
			super("<center><img src='img/dw-resize.png'></center>");
			sinkEvents(Event.MOUSEEVENTS);
		}

		public void onBrowserEvent(Event event) {
			super.onBrowserEvent(event);

			switch (DOM.eventGetType(event)) {

			case Event.ONMOUSEDOWN: {
				Element target = DOM.eventGetTarget(event);
				if (DOM.isOrHasChild(this.getElement(), target)) {
					isResizing = true;
					lastY = DOM.eventGetClientY(event);
					DOM.setCapture(getElement());
					DOM.eventPreventDefault(event);
					hasBeenResized = true;
				}
				break;
			}

			case Event.ONMOUSEUP: {
				DOM.releaseCapture(getElement());
				isResizing = false;
				break;
			}

			case Event.ONMOUSEMOVE: {
				if (isResizing) {
					assert DOM.getCaptureElement() != null;
                    int y = DOM.eventGetClientY(event);
                    decrementHeight(-y+lastY);
                    lastY = y;
                    DOM.eventPreventDefault(event);


				}
				break;
			}
			}
		}
	}
	/** An image for the collapse button in the left top corner. */
	private Image collapseButton;
	/** When a mouse moves over the image a shadow is used. */
	private boolean useShadows;
	/** The content of the widget, which is wrapped in a scroll panel. */
	private Widget content;
	/** A ScrollPanel that wraps around the content panel. */
	private ScrollPanel scroller;

	/**set of controls that may be set in the header. */
	private Widget[] headerContorls;
	/** A panel for the additional controls, if there are any. */
	private HorizontalPanel headerControlsPanel;
	/* Variables used for resizing the widget manually **/
	/** A panel which holds the button for resizing the widget. */
	private ResizePanel resizer;
	/** Indicates if the widget is being resized manually at the moment. */
	private boolean isResizing;
	/** last position of the mouse during resizing.  */
	private int lastY;
	/**
	 * Has the widget been resized manually.If it hasn't been, then
	 * when the <code>scroller</code> is expanded next time,
	 * <code>setHeight</code> will be set to \"\"
	 */
	private boolean hasBeenResized;
	/* Variables used for the blinding effect **/
	/**
	 * Indicates if the effect is still in progress. If false than no other
	 * effects can be engaged
	 **/
	private boolean isFxFinished;
	/**
	 * Is the widget open. It can only be true after a blind up effect.
	 */
	private boolean isOpen;
	/**
	 * Last height after a blind up effect.
	 */
	private int lastHeight;

	/**
	 * How many pixels per step should be added/subtract to/from the widget
	 * content panel, when a blind up/down effect has been engeged.
	 */
	private int pxStep;

	/** Timer for the blind effect. */
	private FXTimer fxT;

	/**
	 * Creates a DisclosureWidget.
	 *
	 * @param name a name for the widget that will appear in the widget header.
	 * @param content content widget, which is then wrapped inside a ScrollPanel
	 * so resizing could be possible.
	 */
	public DisclosureWidget(String name, Widget content) {
		this(name, content, null);
	}

	/**
	 * Creates a DisclosureWidget with additional controls in the header.
	 *
	 * @param name a name for the widget that will appear in the widget header.
	 * @param content content widget, which is then wrapped inside a ScrollPanel
	 * so resizing could be possible.
	 * @param headerControls additional controls for the widget, which will
	 * appear in the upper right corner.
	 */
	public DisclosureWidget(String name, Widget content, Widget[] headerControls) {
		isResizing = false;
		lastY = 0;
		isFxFinished = true;
		isOpen = true;
		lastHeight = 1;
		pxStep = 1;
		this.content = content;
		this.content.addStyleName("dw-content");
		this.headerContorls = headerControls;
		headerControlsPanel = new HorizontalPanel();
		if(headerControls == null) {
			if (content instanceof HasAdditionalControlls) {
				this.headerContorls = ((HasAdditionalControlls) content).getAdditionalControlls();
			}
		}

		if (this.headerContorls != null) {
			for (Widget headerContorl : this.headerContorls) {
				headerContorl.addStyleName("dw-heder-controls");
				;
				headerControlsPanel.add(headerContorl);
			}
		}
		hasBeenResized = false;
		fxT = new FXTimer();
		/*main pane*l*/
		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setWidth("100%");
		/*Border panel*/
		VerticalPanel borderPanel = new VerticalPanel();
		borderPanel.setStyleName("dw");
		/*Expand button*/
		collapseButton = new Image("img/dw-collapse-but.png", 0, 15, 15, 15);
		collapseButton.addClickHandler(this);
		collapseButton.addMouseOverHandler(this);
		collapseButton.addMouseOutHandler(this);
		Common.setCursorPointer(collapseButton);
		/*header*/
		Grid header = new Grid(1, 3);
		CellFormatter cf = header.getCellFormatter();
		cf.setWidth(0, 0, "20px");
		//IE Bug - must set BG to transparent
		DOM.setStyleAttribute(cf.getElement(0, 0), "background", "transparent");
		cf.setAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT,
				HasVerticalAlignment.ALIGN_MIDDLE);
		header.setWidget(0, 0, collapseButton);
		Label nameLabel = new Label(name);
		header.setWidget(0, 1, nameLabel);
		if (this.headerContorls != null)
			header.setWidget(0, 2, headerControlsPanel);
		header.setStyleName("dw-header");

		borderPanel.add(header);

		scroller = new ScrollPanel(content);
		scroller.setStyleName("dw-content");
		scroller.setWidth(""+(Main.WIDTH_PX - 2)+"px");
		borderPanel.add(scroller);
		resizer = new ResizePanel();
		resizer.setStyleName("dw-resizer");
		mainPanel.add(borderPanel);
		mainPanel.add(resizer);
		initWidget(mainPanel);
	}

	/**
	 * Returns the content widget, without the ScrollerPanel.
	 *
	 * @return the content widget, without the ScrollerPanel.
	 */
	public Widget getContent() {
		return content;
	}

	@Override
	public void onClick(ClickEvent event) {
		fxBlind();
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		useShadows = true;
		if (isOpen) {
			collapseButton.setVisibleRect(15, 15, 15, 15);
		} else {
			collapseButton.setVisibleRect(15, 0, 15, 15);
		}

	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		useShadows = false;
		if (isOpen) {
			collapseButton.setVisibleRect(0, 15, 15, 15);
		} else {
			collapseButton.setVisibleRect(0, 0, 15, 15);
		}

	}

	/** Expands the widget to the default height */
	public void resetHeightToDefault() {
		hasBeenResized = false;
		scroller.setVisible(true);
		resizer.setVisible(true);
		headerControlsPanel.setVisible(true);
		isOpen = true;
		scroller.setHeight("");
		collapseButton.setVisibleRect(0, 15, 15, 15);
	}

	/**
	 * Replace the content widget.
	 *
	 * @param content a new Content widget.
	 */
	public void setContent(Widget content) {
		if (this.content != null)
			this.scroller.remove(this.content);
		this.content = content;
		this.scroller.add(content);
	}

	/**
	 * Decrement the height of this.scroller.
	 *
	 * @param dY how many pixels to decrement.
	 * @return return false if no the scroller height cannot be decremented any
	 * more
	 */
	private boolean decrementHeight(int dY) {
		int y = scroller.getOffsetHeight();
		y -= dY;
		if (y < 1) {
			scroller.setHeight("1px"); //must not have size 0
			return false;
		}
		scroller.setHeight("" + y + "px");
		return true;
	}

	/**
	 * A blind up and down effect. (40 FPS for 0.25 sec.)
	 */
	private void fxBlind() {
		if (!isFxFinished) {
			return;
		}

		isFxFinished = false;
		if (isOpen) {
			lastHeight = scroller.getOffsetHeight();
		} else {
			scroller.setVisible(true);
		}
		pxStep = lastHeight / 10;
		if (pxStep < 1) {
			pxStep = 1;
		}
		fxT.scheduleRepeating(25);
	}

	/**
	 * Increment the height of the this.scroller.
	 *
	 * @param dY how many pixels to increment.
	 * @return return false if the scroller height cannot be incremented any
	 * more, thus <code>this.lastHeight</code> value has been reached.
	 */
	private boolean incHeight(int dY) {
		int y = scroller.getOffsetHeight();
		y += dY;
		if (y <= lastHeight) {
			scroller.setHeight("" + y + "px"); //must not have size 0
			return true;
		}
		if(hasBeenResized) {
			scroller.setHeight("" + lastHeight + "px");
		} else {
			scroller.setHeight("");
		}

		return false;
	}
}

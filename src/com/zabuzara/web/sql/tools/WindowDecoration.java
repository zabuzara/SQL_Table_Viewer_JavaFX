package com.zabuzara.web.sql.tools;

import java.util.Arrays;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
/**
 * WindowDecoration, for undecorated stage
 * @author zabuzara
 */
public class WindowDecoration extends ToolBar {
	/**
	 * Static variables
	 */
	static private enum Side { TOP, LEFT, BOTTOM, RIGHT, TOP_LEFT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_RIGHT, NULL};
	static private Side clickedSide = Side.NULL;
	static private int clickCounter = 0;
	static private long clickMillis = 0;
	/**
	 * Final private instance variables 
	 */
	private final Scene scene;
	private final String PACKAGE_PATH = WindowDecoration.class.getPackage().getName().replace('.', '/');
	private final String[] ICON_PATHES = {getClass().getResource("minimizeIcon.png").toExternalForm(), getClass().getResource("maximizeIcon.png").toExternalForm(), getClass().getResource("closeIcon.png").toExternalForm()};
	private final String[] COLORED_ICON_PATHES = {getClass().getResource("minimizeIconColored.png").toExternalForm(), getClass().getResource("maximizeIconColored.png").toExternalForm(), getClass().getResource("closeIconColored.png").toExternalForm()};
	private final String windowTitle;
	private final String windowIconPath;
	/**
	 * None final private instance variables 
	 */
	private enum ButtonShape {Circle, Rectangle};
	private Label windowTitleLabel;
	private double buttonSize;
	private double initialX = 0.0;
	private double initialY = 0.0;
	/**
	 * Inilize instance variables
	 * @param scene given scene from start method of application
	 * @param windowTitle window title
	 * @param windowIconPath window icon path
	 * @param windowTopBarHeight window top bar height
	 */
	public WindowDecoration (@SuppressWarnings("exports") final Scene scene, final String windowTitle, final String windowIconPath, final double windowTopBarHeight) {
		super();
		System.out.println(Arrays.toString(ICON_PATHES));
		this.scene = scene;
		this.windowTitle = windowTitle;
		this.windowIconPath = windowIconPath;
		this.windowTitleLabel = new Label(this.windowTitle);
		this.setPrefHeight(windowTopBarHeight);
		this.setMinHeight(windowTopBarHeight);
		this.setMaxHeight(windowTopBarHeight);
		this.setStyle("-fx-background-color:#2e3436;");
		this.buttonSize = (windowTopBarHeight / 10) * 2;
		this.getItems().add(this.newWindowTopBar());
		scene.widthProperty().addListener(event -> this.resizeHandler());
		scene.heightProperty().addListener(event -> this.resizeHandler());
		this.registerEvents();
		this.resizeHandler();
	}
	/**
	 * Resize Handler for responsive width and height
	 */
	private void resizeHandler () {
		this.setPrefWidth(scene.getWidth());
		this.windowTitleLabel.setPrefWidth(scene.getWidth() - (this.getPrefHeight() * 4) - 20);
		this.windowTitleLabel.setStyle("-fx-pref-height: "+(this.getPrefHeight())+";-fx-alignment: center;-fx-text-fill: white;-fx-font-size: "+((this.getPrefHeight()/10)*4)+";-fx-font-wight: bold;");
	}

	/**
	 * Creates window icon for the decoration
	 * @return window icon
	 */
	private HBox newWindowIcon () {
		final HBox iconFrame = new HBox();
		iconFrame.setPrefWidth(this.getPrefHeight() * 2);
		final ImageView windowIcon = new ImageView(new Image(this.windowIconPath));
		windowIcon.setFitWidth((this.getPrefHeight() / 4) * 3);
		windowIcon.setFitHeight((this.getPrefHeight() / 4) * 3);
		iconFrame.setStyle("-fx-alignment: center-left;");
		iconFrame.getChildren().add(windowIcon);
		return iconFrame;
	}
	
	/**
	 * Creates new window toolbar for the decoration
	 * @return window toolbar
	 */
	private HBox newWindowTopBar () {
		final HBox windowTopBar = new HBox();
		windowTopBar.setOnMousePressed(event -> {
			initialX = ((Stage)scene.getWindow()).getX() - event.getScreenX();
			initialY = ((Stage)scene.getWindow()).getY() - event.getScreenY();
		});
		windowTopBar.setOnMouseDragged(event -> {
			((Stage)scene.getWindow()).setX((event.getScreenX()+initialX));
			((Stage)scene.getWindow()).setY((event.getScreenY()+initialY));
		});
		windowTopBar.getChildren().add(this.newWindowIcon());
		windowTopBar.getChildren().add(this.windowTitleLabel);
		windowTopBar.getChildren().add(this.newWindowButtonBox());
		return windowTopBar;
	}
	/**
	 * Creates new window button box
	 * @return 
	 */
	private HBox newWindowButtonBox () {
		final HBox windowButtonBox = new HBox();
		windowButtonBox.setStyle("-fx-pref-width:"+(this.getPrefHeight() * 2)+";-fx-alignment: center-right;");
		final Button[] windowButtons = {this.newMinimizeButton(ButtonShape.Rectangle), this.newMaximizeButton(ButtonShape.Rectangle), this.newCloseButton(ButtonShape.Rectangle)};
		for (int index=0; index<windowButtons.length; ++index) {
			windowButtons[index].setPrefHeight(this.buttonSize);
			windowButtons[index].setPrefWidth(this.buttonSize);
			windowButtons[index].setStyle("-fx-border-color: transparent;-fx-border-radius: 100%;-fx-background-color: transparent;");
			this.registerButtonEvents(windowButtons[index], index);
		}
		windowButtonBox.getChildren().add(windowButtons[0]);
		windowButtonBox.getChildren().add(windowButtons[1]);
		windowButtonBox.getChildren().add(windowButtons[2]);
		return windowButtonBox;
	}
	/**
	 * Registers button events
	 * @param button given button
	 * @param buttonIndex index of given button
	 */
	private void registerButtonEvents (final Button button, final int buttonIndex) {
		final String notHoveredButtonStyle = "-fx-cursor: default;-fx-border-color: transparent;-fx-border-radius: 100%;-fx-background-color: transparent;";
		final String hoveredButtonStyle = "-fx-cursor: open-hand;-fx-background-color: #555753;";
		button.setOnMouseEntered(event -> {
			final ImageView buttonGraphic = new ImageView(new Image(COLORED_ICON_PATHES[buttonIndex]));
			buttonGraphic.setFitWidth(this.buttonSize);
			buttonGraphic.setFitHeight(this.buttonSize);
			button.setStyle(hoveredButtonStyle);
			button.setGraphic(buttonGraphic);
		});
		button.setOnMouseExited(event -> {
			final ImageView buttonGraphic = new ImageView(new Image(ICON_PATHES[buttonIndex]));
			buttonGraphic.setFitWidth(this.buttonSize);
			buttonGraphic.setFitHeight(this.buttonSize);
			button.setStyle(notHoveredButtonStyle);
			button.setGraphic(buttonGraphic);
		});
	}
	/**
	 * Creates new shape face for buttons
	 * @return shape of button
	 */
	private Shape newButtonShape (ButtonShape buttonShape) {
		return buttonShape == ButtonShape.Rectangle ? new Rectangle(15, 15) : new Circle(15);
	}
	/**
	 * Creates new minimize button for new window decoration
	 * @return minimize button
	 */
	private Button newMinimizeButton (ButtonShape buttonShape) {
		final ImageView minimizeIcon = new ImageView(new Image(ICON_PATHES[0]));
		minimizeIcon.setFitWidth(this.buttonSize);
		minimizeIcon.setFitHeight(this.buttonSize);
		final Button minimizeButton = new Button("",minimizeIcon);
		minimizeButton.setShape(this.newButtonShape(buttonShape));
		minimizeButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent actionEvent) {
	        	((Stage)scene.getWindow()).setIconified(true);
         	}
		}); 
		return minimizeButton;
	}
	/**
	 * Creates new maximize button for new window decoration
	 * @return maximize button
	 */
	private Button newMaximizeButton (ButtonShape buttonShape) {
		final ImageView maximizeIcon = new ImageView(new Image(ICON_PATHES[1]));
		maximizeIcon.setFitWidth(this.buttonSize);
		maximizeIcon.setFitHeight(this.buttonSize);
		final Button maximizeButton = new Button("",maximizeIcon);
		maximizeButton.setShape(this.newButtonShape(buttonShape));
		maximizeButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent actionEvent) {
	        	((Stage)scene.getWindow()).setFullScreen(!((Stage)scene.getWindow()).isFullScreen());
         	}
		});
		return maximizeButton;
	}
	/**
	 * Creates new close button for new window decoration
	 * @return close button
	 */
	private Button newCloseButton (ButtonShape buttonShape) {
		final ImageView closeIcon = new ImageView(new Image(ICON_PATHES[2]));
		closeIcon.setFitWidth(this.buttonSize);
		closeIcon.setFitHeight(this.buttonSize);
		final Button closeButton = new Button("",closeIcon);
		closeButton.setShape(this.newButtonShape(buttonShape));
		closeButton.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent actionEvent) {
	             Platform.exit();
         	}
		});
		return closeButton;
	}
	/**
	 * Register resizing events in SQLRootController
	 */
	public void registerEvents () {
		final ToolBar that = this;
		this.setOnMousePressed(event -> {
			if (clickCounter < 2 && (System.currentTimeMillis()-clickMillis) <= 350) {
				((Stage)scene.getWindow()).setFullScreen(!((Stage)scene.getWindow()).isFullScreen());
			} else {
				clickCounter = 0;
				clickMillis = 0;
			}
			clickMillis = System.currentTimeMillis();
			clickCounter++;
		});
		this.setOnMouseDragged(event -> {
			if (clickedSide == Side.NULL)
				this.setCursor(Cursor.MOVE);
		});
		this.setOnMouseReleased(event -> {
			if (clickedSide == Side.NULL)
				this.setCursor(Cursor.DEFAULT);
		});
		scene.addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
			@Override
		    public void handle(MouseEvent event) {
				final double precision = 5.0;
		    	if (event.getSceneX() < precision && event.getSceneY() < precision) {
		    		scene.setCursor(Cursor.NW_RESIZE);
		    		clickedSide = Side.TOP_LEFT;
		    	} else if (event.getSceneX() < precision && event.getSceneY() > scene.getHeight() - precision) {
		    		scene.setCursor(Cursor.SW_RESIZE);
		    		clickedSide = Side.BOTTOM_LEFT;
		    	} else if (event.getSceneX() > scene.getWidth() - precision && event.getSceneY() > scene.getHeight() - precision) {
		    		scene.setCursor(Cursor.SE_RESIZE);
		    		clickedSide = Side.BOTTOM_RIGHT;
		    	} else if (event.getSceneX() > scene.getWidth() - precision && event.getSceneY() < precision) {
		    		scene.setCursor(Cursor.NE_RESIZE);
		    		clickedSide = Side.TOP_RIGHT;
		    	} else if (event.getSceneX() < precision) {
		    		scene.setCursor(Cursor.W_RESIZE);
		    		clickedSide = Side.LEFT;
		    	} else if (event.getSceneY() < precision) {
		    		scene.setCursor(Cursor.N_RESIZE);
		    		clickedSide = Side.TOP;
		    	} else if (event.getSceneX() > scene.getWidth()-precision) {
		    		scene.setCursor(Cursor.E_RESIZE);
		    		clickedSide = Side.RIGHT;
		    	} else if (event.getSceneY() > scene.getHeight()-precision) {
		    		scene.setCursor(Cursor.S_RESIZE);
		    		clickedSide = Side.BOTTOM;
		    	} else {
		    		scene.setCursor(Cursor.DEFAULT);
		    		clickedSide = Side.NULL;
		    	}
		    }
		});
    	scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
			@Override
		    public void handle(MouseEvent event) {
				final double oldWindowX = ((Stage)scene.getWindow()).getX();
				final double oldWindowY = ((Stage)scene.getWindow()).getY();
				final double oldWindowWidth = ((Stage)scene.getWindow()).getWidth();
				final double oldWindowHeight = ((Stage)scene.getWindow()).getHeight();
				final double newWindowX = event.getScreenX();
				final double newWindowY = event.getScreenY();
				final double newWindowWidth = oldWindowWidth-(newWindowX-oldWindowX);
				final double newWindowHeight = oldWindowHeight-(newWindowY-oldWindowY);
				if ((newWindowX > oldWindowX && clickedSide == Side.LEFT) || (newWindowX < oldWindowX && clickedSide == Side.LEFT)) {
					if (newWindowWidth > 380) {
						((Stage)scene.getWindow()).setWidth(newWindowWidth);
						((Stage)scene.getWindow()).setX(newWindowX);
					}
				} else if ((newWindowX > (oldWindowX+oldWindowWidth) && clickedSide == Side.RIGHT) || (newWindowX < (oldWindowX+oldWindowWidth) && clickedSide == Side.RIGHT)) {
					if (newWindowX-oldWindowX > 380) {
						((Stage)scene.getWindow()).setWidth(newWindowX-oldWindowX);
					}
				} else if ((newWindowY < oldWindowY && clickedSide == Side.TOP) || (newWindowY > oldWindowY && clickedSide == Side.TOP)) {
					if (oldWindowHeight > that.getHeight() && newWindowHeight > that.getHeight()) {
						((Stage)scene.getWindow()).setY(newWindowY);
						((Stage)scene.getWindow()).setHeight(newWindowHeight);
					}
				} else if ((newWindowY < (oldWindowY+oldWindowHeight) && clickedSide == Side.BOTTOM) || (newWindowY > (oldWindowY+oldWindowHeight) && clickedSide == Side.BOTTOM)) {
					if (newWindowY-oldWindowY > that.getHeight()) {
						((Stage)scene.getWindow()).setHeight(newWindowY-oldWindowY);
					}
				} else if (((newWindowY < oldWindowY || newWindowX < oldWindowX) && clickedSide == Side.TOP_LEFT) || ((newWindowY > oldWindowY || newWindowX > oldWindowX) && clickedSide == Side.TOP_LEFT)) {
					if (newWindowWidth > 380) {
						((Stage)scene.getWindow()).setX(newWindowX);
						((Stage)scene.getWindow()).setWidth(newWindowWidth);
					}
					if (oldWindowHeight > that.getHeight() && newWindowHeight > that.getHeight()) {
						((Stage)scene.getWindow()).setY(newWindowY);
						((Stage)scene.getWindow()).setHeight(newWindowHeight);
					}
				} else if (((newWindowY > (oldWindowY+oldWindowHeight) || newWindowX < oldWindowX) && clickedSide == Side.BOTTOM_LEFT) || ((newWindowY < (oldWindowY+oldWindowHeight) || newWindowX > oldWindowX) && clickedSide == Side.BOTTOM_LEFT)) {
					if (newWindowWidth > 380) {
						((Stage)scene.getWindow()).setX(newWindowX);
						((Stage)scene.getWindow()).setWidth(newWindowWidth);
					}
					if (newWindowY-oldWindowY > that.getHeight()) {
						((Stage)scene.getWindow()).setHeight(newWindowY-oldWindowY);
					}
				} else if (((newWindowY < oldWindowY || newWindowX > (oldWindowX+oldWindowWidth)) && clickedSide == Side.TOP_RIGHT) || ((newWindowY > oldWindowY || newWindowX < (oldWindowX+oldWindowWidth)) && clickedSide == Side.TOP_RIGHT)) {
					if (newWindowX-oldWindowX > 380) {
						((Stage)scene.getWindow()).setWidth(newWindowX-oldWindowX);
					}
					if (newWindowHeight > that.getHeight()) {
						((Stage)scene.getWindow()).setY(newWindowY);
						((Stage)scene.getWindow()).setHeight(newWindowHeight);
					}
				} else if (((newWindowY < (oldWindowY+oldWindowHeight) || newWindowX < (oldWindowX+oldWindowWidth)) && clickedSide == Side.BOTTOM_RIGHT) || ((newWindowY > (oldWindowY+oldWindowHeight) || newWindowX > (oldWindowX+oldWindowWidth)) && clickedSide == Side.BOTTOM_RIGHT)) {
					if (newWindowX-oldWindowX > 380) {
						((Stage)scene.getWindow()).setWidth(newWindowX-oldWindowX);
					}
					if (oldWindowHeight > that.getHeight() && newWindowY-oldWindowY > that.getHeight()) {
						((Stage)scene.getWindow()).setHeight(newWindowY-oldWindowY);
					}
				}
			}
		});
	}
}

package com.zabuzara.web.sql.view;

import com.zabuzara.web.sql.controller.AuthenticationController;
import com.zabuzara.web.sql.controller.DatabasesController;
import com.zabuzara.web.sql.controller.SQLRootPaneController;
import com.zabuzara.web.sql.controller.TablesController;
import com.zabuzara.web.sql.tools.WindowDecoration;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SQLViewApp extends Application{
	static private final String PACKAGE_PATH = SQLViewApp.class.getPackage().getName().replace('.', '/');
	static private String ICON_PATH = PACKAGE_PATH + "/icon.png";
	static private String CSS_PATH = PACKAGE_PATH + "/style.css";
	static private final String WINDOW_TITLE = "Local-Database-Manager";
	static private final double WINDOW_WIDTH;
	static private final double WINDOW_HEIGHT;
	static private final double WINDOW_X;
	static private final double WINDOW_Y;
	static private final boolean START_IN_FULL_SCREEN_MODE = false;
	static {
		/**
		 * Get Screen size 
		 */
		ObservableList<Screen> SCREEN_SIZES = Screen.getScreens();
	    Rectangle2D SCREEN_SIZE = SCREEN_SIZES.get(0).getBounds();
	    /**
	     * Set Application widnow properties
	     * width & height, x & y
	     */
		WINDOW_WIDTH = SCREEN_SIZE.getWidth() / 2;
		WINDOW_HEIGHT = SCREEN_SIZE.getHeight() / 2;
		WINDOW_X = WINDOW_WIDTH - (WINDOW_WIDTH / 2);
		WINDOW_Y = WINDOW_HEIGHT - (WINDOW_HEIGHT / 2);
	}
	
	public SQLViewApp () {}
	
	
	@Override
	public void start (final Stage window) throws Exception {
		/**
		 * Initialize GUI components 
		 */
		final BorderPane rootPane = this.newRootPane();
		final Scene sceneGraph = new Scene(rootPane, WINDOW_WIDTH, WINDOW_HEIGHT);

		ICON_PATH = getClass().getResource("icon.png").toExternalForm();
		CSS_PATH = getClass().getResource("style.css").toExternalForm();

		sceneGraph.getStylesheets().add(CSS_PATH);
		final SQLRootPaneController rootController = new SQLRootPaneController((TabPane)rootPane.getCenter());
		/**
		 * Configure the Stage
		 */
		final ToolBar windowTopBar = new WindowDecoration(sceneGraph, WINDOW_TITLE, ICON_PATH, 50); 
		rootPane.setTop(windowTopBar);
		window.getIcons().add(new Image(ICON_PATH));
		window.initStyle(StageStyle.TRANSPARENT);
		window.setTitle(WINDOW_TITLE);
		window.setScene(sceneGraph); 
		window.setX(WINDOW_X);
		window.setY(WINDOW_Y);
		window.setFullScreen(START_IN_FULL_SCREEN_MODE);
		window.setFullScreenExitHint("You cant't ESCAPE unless you press Q");
		window.setFullScreenExitKeyCombination(KeyCombination.valueOf("q"));
		window.addEventHandler(KeyEvent.KEY_PRESSED,  (event) -> {
		    KeyCode keyCode = event.getCode();
		    switch(keyCode) {
		    	case F11:
		    		window.setFullScreen(!window.isFullScreen());
		    		break;
	    		default:break;
		    }
		});
		window.show();
		window.setOnCloseRequest(event -> rootController.closeConnection());
	}

	@Override
	public void stop () {}
	
	 
	private BorderPane newRootPane () {
		final BorderPane rootPane = new BorderPane();

		final Tab loginTab = AuthenticationController.newAuthenticationTab("Login");		
		final Tab databasesTab = DatabasesController.newDatabasesTab("Databases");
		final Tab tablesTab = TablesController.newTableTab("Tables");
		
		final TabPane centerPane = new TabPane(loginTab, databasesTab, tablesTab);
		centerPane.setId("rootPane");
		rootPane.getStyleClass().add("rootPane");
		rootPane.setCenter(centerPane);
		return rootPane;
	}
	
	/**
	 * Application entry point
	 * @param args
	 */
	static public void main (final String[] args) {
		launch(args);
	}
}

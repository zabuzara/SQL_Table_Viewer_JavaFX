package com.zabuzara.web.sql.controller;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.zabuzara.web.sql.tools.ParentController;
import com.zabuzara.web.sql.tools.UncheckedSQLException;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SQLRootPaneController extends ParentController<TabPane> {	
	static private Connection connection;
	/**
	 * initialize instance variables
	 * @param node (rootpane)
	 */
	public SQLRootPaneController (final TabPane node) {
		super(node);
		// initialize AuthenticationController
		final VBox authenticationPane = (VBox) this.getNode().getTabs().get(0).getContent();
		final AuthenticationController authenticationController = new AuthenticationController(this, authenticationPane);
		this.getChildren().put("authentication", authenticationController); 
		// initialize DatabasesController
		final BorderPane databasesPane = (BorderPane) this.getNode().getTabs().get(1).getContent();
		final DatabasesController preferencesController = new DatabasesController(this, databasesPane);
		this.getChildren().put("databases", preferencesController);		
		// initialize TablesController
		final TabPane tablesPane = (TabPane) this.getNode().getTabs().get(2).getContent();
		final TablesController tablesController = new TablesController(this, tablesPane); 
		this.getChildren().put("tables", tablesController);
	}
	
	/**
	 * After successfully authenticated
	 * will be active Tabs
	 */
	private void authenticated () {
		final Label loginTabLabel = (Label) ((Tab) this.getNode().getTabs().get(0)).getGraphic();
		loginTabLabel.setText("Preferences");
		this.getNode().getTabs().get(1).setDisable(false);
		this.getNode().getTabs().get(1).getStyleClass().remove("notActive");
		this.getNode().getSelectionModel().select(1);
	}
	
	/**
	 * Select database for loading tables in TablesController
	 * @param databaseName
	 */
	protected void selectDatabase (final String databaseName) {
		if (!databaseName.isEmpty()) {
			((TablesController)this.getChildren().get("tables")).setDatabaseName(databaseName);
			final String tablesTabLabelText = "Tables of "+databaseName;
			((Label) this.getNode().getTabs().get(2).getGraphic()).setText(tablesTabLabelText);
			this.getNode().getTabs().get(2).setDisable(false);
			this.getNode().getSelectionModel().select(2);
		}
	}	
	
	/**
	 * Returns database connection
	 * @return database connection
	 */
	protected Connection getConnection() {
		return connection;
	}
	
	/**
	 * Creates and returns a new data source for the sql databases. Most flexible
	 * way to bootstrap database communications, but most seldom used in Internet
	 * examples as it is used in server apps (Java EE)!
	 * @return the data source created
	 */
	protected DataSource newSqlConfig (final String username, final String password) {
		final MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setURL("jdbc:mysql://localhost:3306/");
		dataSource.setUser(username);
		dataSource.setPassword(password);
		try {
			connection = dataSource.getConnection();
			((DatabasesController) this.getChildren().get("databases")).loadDatabases();
			this.authenticated();
		} catch (final SQLException e) {
			final VBox authenticationPane = (VBox) this.getNode().getTabs().get(0).getContent();
			final Label infoLabel = (Label) authenticationPane.getChildren().get(0);
			infoLabel.setText("Invalid username or password, please check your login information and try again.");
		}
		return dataSource;
	}
	
	/**
	 * Closes the database connection
	 */
	public void closeConnection () {
		try {
			connection.close();
		} catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
}

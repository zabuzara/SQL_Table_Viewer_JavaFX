package com.zabuzara.web.sql.controller;

import java.sql.*;
import java.util.*;
import com.zabuzara.web.sql.model.DatabasesTableRow;
import com.zabuzara.web.sql.tools.*;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.DefaultStringConverter;

public class DatabasesController extends ChildController<TabPane,BorderPane>{
	
	@SuppressWarnings("unchecked")
	public DatabasesController (final ParentController<TabPane> parent, final BorderPane node) {
		super(parent, node);
		final Tab databasesTab = (Tab) parent.getNode().getTabs().get(1);
		final TableView<DatabasesTableRow> databaseTableView = (TableView<DatabasesTableRow>) ((ScrollPane)node.getCenter()).getContent();
		databaseTableView.setPrefWidth(databasesTab.getTabPane().getWidth());
		
		// register events
		databasesTab.getTabPane().widthProperty().addListener(event -> databaseTableView.setPrefWidth(databasesTab.getTabPane().getWidth()));
		databasesTab.getTabPane().heightProperty().addListener(event -> databaseTableView.setPrefHeight(databasesTab.getTabPane().getHeight()-70));
		
		
		final Tab tablesTab = ((Tab) parent.getNode().getTabs().get(2));
		final Label tableTabLabel = ((Label) tablesTab.getGraphic());
		
		databasesTab.getGraphic().setOnMousePressed(event -> {
			tableTabLabel.setText("Tables");
			if (!databasesTab.isDisable())
				tablesTab.setDisable(true);
		});
		
		// TableView listener
		databaseTableView.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Object> () {
			@Override
			public void onChanged (final Change<?> change) {
				final String clickedDatabaseName = databaseTableView.getItems().get((Integer) change.getList().get(0)).databaseProperty().getValue();
				((TabPane) parent.getNode()).getTabs().remove(2, ((TabPane) parent.getNode()).getTabs().size()-1);
				((SQLRootPaneController) parent).selectDatabase(clickedDatabaseName);
			}
		});
	}
	
	
	public void loadDatabases () {
		@SuppressWarnings("unchecked")
		final TableView<DatabasesTableRow> databaseTableView = (TableView<DatabasesTableRow>) ((ScrollPane) this.getNode().getCenter()).getContent();
		final List<Map<String,Object>> databases = new ArrayList<Map<String,Object>>();
		try {
			final PreparedStatement statement = ((SQLRootPaneController) this.getParent()).getConnection().prepareStatement("SHOW DATABASES"); 
			if (statement.execute()) {
				final ResultSet result = statement.getResultSet();
				final List<Map<String,Object>> database = RelationalDatabases.toRowMaps(result, true);
				databases.clear();
				databases.addAll(database);
			}
		} catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
		
		for (final Map<String,Object> database : databases) {
			final DatabasesTableRow databaseTableRow = new DatabasesTableRow();
			databaseTableRow.setDatabase((String) database.get("Database"));
			databaseTableView.getItems().add(databaseTableRow);
		}
	}
	
	@SuppressWarnings("unchecked")
	static public Tab newDatabasesTab (final String title) {
		final Label databasesTabLabel = new Label(title);
		final Tab databasesTab = new Tab();
		final Label authorInfo = new Label("Author: Omid Malekzadeh \u00A9Zabuzara 2022");
	
		databasesTab.setId("databasesTab");
		databasesTab.setGraphic(databasesTabLabel);
		databasesTab.getGraphic().getStyleClass().add("databasesTabLabel");

		final BorderPane databasesOuterPane = new BorderPane();
		
		final TableView<DatabasesTableRow> databaseTableView = new TableView<>();
		databaseTableView.getStyleClass().add("databases");
		databaseTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		databaseTableView.setEditable(false);

		final TableColumn<DatabasesTableRow,String> databaseColumn = new TableColumn<>("database-name");
		databaseColumn.setCellValueFactory(new PropertyValueFactory<>("database"));
		databaseColumn.setCellFactory(column -> new TextFieldTableCell<>(new DefaultStringConverter()));
		databaseColumn.setMinWidth(300);
		databaseColumn.getStyleClass().add("databaseColumn");
		
		databaseTableView.getColumns().addAll(databaseColumn);
		
		final ScrollPane databasesInnerPane = new ScrollPane(databaseTableView);
		
		databasesOuterPane.getStyleClass().setAll("databasesOuterPane");
		databasesInnerPane.getStyleClass().setAll("databasesInnerPane");
		authorInfo.getStyleClass().setAll("databaseAuthorInfo");
		
		databasesOuterPane.setCenter(databasesInnerPane);
		databasesOuterPane.setBottom(authorInfo);
		databasesOuterPane.setPrefWidth(Integer.MAX_VALUE);
		
		databasesTab.setContent(databasesOuterPane);
		databasesTab.setClosable(false);
		databasesTab.setDisable(true);
		return databasesTab;
	}
}

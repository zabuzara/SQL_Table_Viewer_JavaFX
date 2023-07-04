package com.zabuzara.web.sql.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.zabuzara.web.sql.tools.ChildController;
import com.zabuzara.web.sql.tools.ParentController;
import com.zabuzara.web.sql.tools.RelationalDatabases;
import com.zabuzara.web.sql.tools.UncheckedSQLException;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.DefaultStringConverter;

public class TablesController extends ChildController<TabPane,TabPane> {
	private String databaseName =  null;
	private List<Tab> loadedTables = null;
	public TablesController (final ParentController<TabPane> parent,final TabPane node) {
		super(parent, node);
	}

	public void setDatabaseName (final String databaseName) {
		this.databaseName = databaseName;
		if (!this.databaseName.isEmpty()) {
			this.loadTables();
		}
	}
	
	public String getDatabaseName () {
		return this.databaseName;
	}
	
	public TabPane getTablesTab() {
		final TabPane loadedTabPane = this.getNode();
		loadedTabPane.getTabs().addAll(loadedTables);	
		return loadedTabPane;
	}
	
	private void loadTables () {
		try {
			final PreparedStatement statement = ((SQLRootPaneController) this.getParent()).getConnection().prepareStatement("SHOW TABLES FROM "+this.databaseName); 
			if (statement.execute()) {
				final ResultSet result = statement.getResultSet();
				final List<Map<String,Object>> tablesMap = RelationalDatabases.toRowMaps(result, false);
				final List<Object> tablesList = new ArrayList<>();
				tablesList.clear();
				this.getNode().getTabs().clear();
				for (Map<String,Object> tableMap : tablesMap) {
					tablesList.addAll(tableMap.values());
				}
//				loadedTables = new ArrayList<Tab>();
				for (Object value : tablesList) {
//					loadedTables.add(this.newSubTableTab(value));
					this.getNode().getTabs().add(this.newSubTableTab(value));
				}			
//				this.getNode().getTabs().addAll(loadedTables);
			}
		} catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Tab newSubTableTab (final Object tableName) {
		final BorderPane tableOuterPane = new BorderPane();
		tableOuterPane.getStyleClass().add("tableOuterPane");
		final Label authorInfo = new Label("Author: Omid Malekzadeh \u00A9Zabuzara 2022");
		authorInfo.getStyleClass().setAll("tableAuthorInfo");
		final Label tabLabel = new Label((String) tableName);
		final Tab tableTab = new Tab();
		tableTab.setClosable(false);
		tableTab.setId("tableTab");
		tableTab.setGraphic(tabLabel);
		tableTab.getGraphic().getStyleClass().add("tableTabLabel");
		final TableView<Map<Integer,String>> table = new TableView<>();
		table.getStyleClass().add("table");
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		table.setEditable(true);
		try {
			final PreparedStatement statement = ((SQLRootPaneController) this.getParent()).getConnection().prepareStatement("SELECT * FROM "+this.getDatabaseName()+"."+tabLabel.getText()); 
			if (statement.execute()) {
				final ResultSet result = statement.getResultSet();
				final TableColumn<Map<Integer,String>,String>[] tableColumns =  new TableColumn[statement.getMetaData().getColumnCount()];
				for (int index=1;  index<=statement.getMetaData().getColumnCount(); ++index) {
			        final String name = statement.getMetaData().getColumnName(index);
					final TableColumn<Map<Integer,String>,String> tableColumn  = new TableColumn<>(name);
			        tableColumn.setCellValueFactory(new MapValueFactory(index));
			        tableColumn.setCellFactory(column -> new TextFieldTableCell<>(new DefaultStringConverter()));
			        tableColumns[index-1] = tableColumn;
				}
		        table.getColumns().addAll(tableColumns);

//				final ObservableList<Map<Integer,String>> tableData = FXCollections.observableArrayList();
		    	while(result.next()) {
			        final Map<Integer,String> dataRow = new HashMap<>();
					 for (int columnIndex = 1; columnIndex <= statement.getMetaData().getColumnCount(); columnIndex++) {
			            final String value = result.getString(columnIndex);
			            dataRow.put(columnIndex, value);
			        }
//			        tableData.add(dataRow);
			    	table.getItems().add(dataRow);	
			    }
			}
		} catch (final SQLException e) {
			Logger.getGlobal().log(Level.INFO, e.getMessage());
		} 	
		tableOuterPane.setCenter(table);
		tableOuterPane.setBottom(authorInfo);
		tableTab.setContent(tableOuterPane);
		return tableTab;
	}
	
	static public Tab newTableTab (final String title) {
		final Label tablesTabLabel = new Label(title);
		final TabPane tablesTabPane = new TabPane();
		tablesTabPane.setId("tablesTabPane");
		tablesTabLabel.getStyleClass().add("tablesTabLabel");
		final Tab tablesTab = new Tab();
		tablesTab.setId("tablesTabsTab");
		tablesTab.setGraphic(tablesTabLabel);
		tablesTab.setClosable(false);
		tablesTab.setDisable(true);
		tablesTab.setContent(tablesTabPane);
		return tablesTab;
	}
}

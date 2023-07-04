package com.zabuzara.web.sql.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DatabasesTableRow {
	private final StringProperty database;

	/**
	 * Initializes a new instance.
	 */
	public DatabasesTableRow () {
		this.database = new SimpleStringProperty(this, "database");
	}


	public StringProperty databaseProperty () {
		return this.database;
	}
	

	public void setDatabase (final String value) {
		this.database.set(value);
	}

}

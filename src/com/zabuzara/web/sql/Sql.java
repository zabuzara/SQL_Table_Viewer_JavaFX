package com.zabuzara.web.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
/**
 * Sql class
 * @author zabuzara
 *
 */
public class Sql {
	private Connection connection = null;
	private boolean isConnected = false;
	private boolean isClosed = true;
	private ResultSet resultSet = null;
	public String usedDatabase = "";
	
	/**
	 * Default Constructor
	 */
	Sql () {}
	
	/**
	 * Constructor
	 * @param username
	 * @param password
	 * @param url
	 */
	Sql (final String username, final String password, final String url) {
		if ((username == null || username.trim().isEmpty()) ||
			(password == null || password.trim().isEmpty()) ||
			(url == null || url.trim().isEmpty())) {
			throw new IllegalArgumentException("invalid parameter!");
		}
		
		Properties properties = new Properties();
		properties.put("user", username);
		properties.put("password", password);
		try {
			this.connection = DriverManager.getConnection(url,properties);
			if(this.connection.isValid(1)) {
				this.isConnected = true;
			}else{
				this.isConnected = false;
			}
		} catch ( SQLException SQLE ) {
			System.err.println(SQLE.getMessage());
			this.isConnected = false;
		}
		this.isClosed = !this.isConnected;
	}
	
	/**
	 * checks if connection is stablished
	 * @return boolean 
	 */
	public boolean isConnected () {
		return this.isConnected;
	}
	
	/**
	 * checks if connection is closed
	 * @return boolean
	 */
	public boolean isClosed () {
		return this.isClosed;
	}
	
	/**
	 * closes existing connection
	 * @return boolean
	 */
	public boolean closeConnection () {
		try {
			this.connection.close();
			this.isClosed = this.connection.isClosed();
			this.connection = null;
			this.isConnected = false;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return this.isClosed;
	}
	
	/**
	 * sends the given query-statement to mysql
	 * @param sql
	 */
	public void statement (final String sql) {
		this.resultSet = null;
		try {
			PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
			if (preparedStatement.execute())
				this.resultSet = preparedStatement.getResultSet();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * builds Map from ResultSet
	 * @return Map<String,List<String>> 
	 */
	public Map<Integer,List<Object>> getResultMap () {
		Map<Integer,List<Object>> resultMap = new HashMap<Integer, List<Object>>();
		try {
			if (this.resultSet == null) throw new SQLException("result empty");
			
			ResultSetMetaData resultSetMetaData = this.resultSet.getMetaData();
			Integer[] columnsLengthArray = new Integer[resultSetMetaData.getColumnCount()];
			Map<Integer, Integer> columnsLengthMap = new HashMap<Integer,Integer>();
			resultMap.put(0, new ArrayList<Object>(Arrays.asList(columnsLengthArray)));
			resultMap.put(1, new ArrayList<Object>());
			
			for (int columnIndex=1; columnIndex<=resultSetMetaData.getColumnCount(); columnIndex++) {
				resultMap.get(1).add(resultSetMetaData.getColumnName(columnIndex)+"("+resultSetMetaData.getColumnTypeName(columnIndex).toLowerCase()+")");
				columnsLengthArray[columnIndex-1] = resultSetMetaData.getColumnName(columnIndex).length();
				columnsLengthMap.put(columnIndex, resultSetMetaData.getColumnName(columnIndex).length()+resultSetMetaData.getColumnTypeName(columnIndex).length()+2);
			}
			
			BiPredicate<Integer, Integer> biPredicate = (Integer a, Integer b) -> a < b;
			while (this.resultSet.next()) {
				int putIndex = this.resultSet.getRow()+1;
				resultMap.put(putIndex, new ArrayList<Object>());
				for (int columnIndex=1; columnIndex<=resultSetMetaData.getColumnCount(); columnIndex++) {
					resultMap.get(putIndex).add(this.resultSet.getString(columnIndex));
					if (this.resultSet.getString(columnIndex) != null) {						
						if (biPredicate.test(columnsLengthMap.get(columnIndex),this.resultSet.getString(columnIndex).length())) {
							columnsLengthMap.replace(columnIndex,this.resultSet.getString(columnIndex).length());
						}
					} else {
						if (biPredicate.test(columnsLengthMap.get(columnIndex), 4)) {
							columnsLengthMap.replace(columnIndex,4);
						}
					}
				}
			}
			resultMap.replace(0, columnsLengthMap.values().stream().collect(Collectors.toList()));
		} catch (SQLException e) {
			
		}
		return resultMap;
	}
}

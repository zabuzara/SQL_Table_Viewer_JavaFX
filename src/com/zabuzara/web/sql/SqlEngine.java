package com.zabuzara.web.sql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.zabuzara.web.sql.tools.MyColor;

public class SqlEngine {
	static private final String PROMPT = "\nsql> ";
	
	static public void main (final String[] args) throws IOException {
		final BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("SQL-Statement-App");
		final String HOST = "localhost";
		final String URL = "jdbc:mysql://"+HOST+"/";
		final String USER = "zabuzara";
		final String PASSWORD = "omid40022OMID40022";
		
		Sql sql = new Sql(USER, PASSWORD, URL);

		if (sql.isConnected()) {
			System.out.println("Connected to: " + HOST);
			System.out.print(PROMPT);
			for (String line = consoleReader.readLine(); line != null; line = consoleReader.readLine()) {
				if (line.trim().equalsIgnoreCase("quit")) {
					System.out.println("Goodbye !");
					System.exit(0);
					break;
				}
				line = line.trim();

				Double requestTime = Double.valueOf(System.currentTimeMillis());
				sql.statement(line);
				
				SqlEngine.displayResult(sql.getResultMap(),(System.currentTimeMillis() - requestTime));
				
				System.out.print(PROMPT);
			}
		}
	}
	
	/**
	 * 
	 * @param command
	 * @param parameters
	 */
	static public void displayResult (Map<Integer,List<Object>> resultMap, Double answerTime) {
		if (resultMap.size() > 2) {
			List<Integer> columnsLength = new ArrayList<Integer>();
			List<String> columnType = new ArrayList<String>();
			StringBuilder lineSeparator = new StringBuilder("\n");
			StringBuilder tableContent = new StringBuilder();
			int rowsCount = 0;
			for (Map.Entry<Integer, List<Object>> row :resultMap.entrySet()) {
				int columnCounter = 0;
				StringBuilder rowContent = new StringBuilder();
				for(Object column: row.getValue()) {
					if (row.getKey()==0) {
						columnsLength.add((Integer)column+2);
						if (columnCounter==0) {
							if (System.console() != null && System.getenv().get("TERM") != null) {
								lineSeparator.append(MyColor.BLACK_BRIGHT).append("+").append(MyColor.RESET).append(String.format("%" + (-((Integer)column+2)) + "s", "").replace(" ", MyColor.BLACK_BRIGHT+"-"+MyColor.RESET));
							} else {
								lineSeparator.append("+").append(String.format("%" + (-((Integer)column+2)) + "s", "").replace(' ', '-'));
							}
						} else {
							if (System.console() != null && System.getenv().get("TERM") != null) {
								lineSeparator.append(String.format("%" + (-((Integer)column+2)) + "s", "").replace(" ", MyColor.BLACK_BRIGHT+"-"+MyColor.RESET));
							} else {
								lineSeparator.append(String.format("%" + (-((Integer)column+2)) + "s", "").replace(' ','-'));
							}
						}
						if (columnsLength.size()-1==columnCounter) {
							if (System.console() != null && System.getenv().get("TERM") != null) {
								lineSeparator.append(MyColor.BLACK_BRIGHT).append("+");
							} else {
								lineSeparator.append("+");
							}
						}
						columnCounter++;
					} else {
						if (row.getKey() == 1) {
							columnType.add(column.toString().substring(column.toString().lastIndexOf("(") + 1, column.toString().lastIndexOf(")")));
						}
							
						if (row.getKey() > 1 && (columnType.get(columnCounter).toString().contains("float") || columnType.get(columnCounter).toString().contains("double"))) {
							if (columnCounter == 0) {
								rowContent.append("| " + String.format("%" + (columnsLength.get(columnCounter) - 1) + "s", column + " ").replace(' ', ' ') + "|");
							} else {
								rowContent.append(" " + String.format("%" + (columnsLength.get(columnCounter) - 1) + "s", column + " ").replace(' ', ' ') + "|");
							}
						} else {
							
							if(columnCounter == 0) {
								rowContent.append("| " + String.format("%" + (-columnsLength.get(columnCounter) + 1) + "s", column + " ").replace(' ', ' ') + "|");
							}else {
								rowContent.append(" " + String.format("%" + (-columnsLength.get(columnCounter) + 1) + "s", column + " ").replace(' ', ' ') + "|");
							}
						}
						columnCounter++;
					}
				}
				if(row.getKey() >= 0) {
					tableContent.append(rowContent);
					tableContent.append(lineSeparator).append("\n");
				}
				rowsCount++;
			}
			if (System.console() != null && System.getenv().get("TERM") != null) {
				tableContent.append(MyColor.BLUE).append("+ ").append(rowsCount == 0 ? rowsCount + " row in set (": (rowsCount - 2) + (rowsCount - 2 == 1 ?" row in set (":" rows in set (")).append(answerTime/1000).append("ms)").append(MyColor.RESET);
			} else {
				tableContent.append("+ ").append(rowsCount == 0 ? rowsCount+" row in set (": (rowsCount - 2) + (rowsCount - 2 == 1 ?" row in set (":" rows in set (")).append(answerTime/1000).append("ms)");
			}
			System.out.print(tableContent.append("\n"));	
		}
	}
}

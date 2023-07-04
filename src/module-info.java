module com.ome.flight.model {
	requires java.logging;
	requires java.net.http;
	requires javafx.graphics;
	requires java.sql;
	requires mysql.connector.java;
	requires java.management;
	requires javafx.controls;
	requires java.desktop;

    opens com.zabuzara.web.sql.view;
	exports com.zabuzara.web.sql.tools;
}
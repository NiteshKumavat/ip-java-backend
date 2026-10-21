package com.candidate.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/candidate_management";

    public static Connection getConnection() throws SQLException {
        String url = DEFAULT_URL;
        String user = "root";
        String password = "root";

        if (user.isBlank() || password.isEmpty()) {
            System.out.println(user + password);
            throw new SQLException(
                    "Set CANDIDATE_DB_USER and CANDIDATE_DB_PASSWORD "
                    + "before connecting to the database.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            throw new SQLException(
                    "MySQL JDBC Driver is not available in the application.",
                    e);
        }

        return DriverManager.getConnection(url, user, password);
    }

    private static String setting(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
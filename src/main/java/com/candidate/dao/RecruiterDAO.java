
package com.candidate.dao;

import com.candidate.util.DBConnection;

import java.nio.CharBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecruiterDAO {

    public static class Recruiter {
        private final int id;
        private final String name;
        private final String email;

        public Recruiter(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }

    public Recruiter authenticate(String email, char[] password)
            throws SQLException {

        if (email == null || password == null) {
            return null;
        }

        String sql = """
            SELECT id, name, email, password
            FROM recruiter
            WHERE email = ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                String storedPassword = rs.getString("password");

                if (storedPassword == null
                        || !storedPassword.contentEquals(
                                CharBuffer.wrap(password))) {
                    return null;
                }

                return new Recruiter(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email")
                );
            }
        }
    }
}

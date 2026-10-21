
package com.candidate.dao;

import com.candidate.model.Candidate;
import com.candidate.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CandidateDAO {

    public List<Candidate> getAllCandidates()
            throws SQLException {
        return searchCandidates(null, null);
    }

    public List<Candidate> searchCandidates(String search, String status)
            throws SQLException {
        List<Candidate> candidates = new ArrayList<>();
        String sql = """
            SELECT c.id, c.name, c.email, c.phone, c.skills,
                   c.experience_years, c.status, c.job_id, j.title AS job_title
            FROM candidates c
            LEFT JOIN jobs j ON j.id = c.job_id
            WHERE (? IS NULL OR c.name LIKE ? OR c.email LIKE ?
                   OR c.skills LIKE ?)
              AND (? IS NULL OR c.status = ?)
            ORDER BY c.id DESC
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
        ) {
            String searchTerm = search == null || search.isBlank()
                    ? null : "%" + search.trim() + "%";
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            ps.setString(3, searchTerm);
            ps.setString(4, searchTerm);
            ps.setString(5, status);
            ps.setString(6, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    candidates.add(mapCandidate(rs));
                }
            }
        }
        return candidates;
    }

    public void addCandidate(Candidate candidate) throws SQLException {
        String sql = candidate.getJobId() == null
                ? """
                    INSERT INTO candidates
                        (name, email, phone, skills, experience_years,
                         status, job_id)
                    VALUES (?, ?, ?, ?, ?, 'New', NULL)
                    """
                : """
                    INSERT INTO candidates
                        (name, email, phone, skills, experience_years,
                         status, job_id)
                    SELECT ?, ?, ?, ?, ?, 'New', id
                    FROM jobs
                    WHERE id = ? AND status = 'OPEN'
                    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getEmail());
            ps.setString(3, candidate.getPhone());
            ps.setString(4, candidate.getSkills());
            ps.setBigDecimal(5, candidate.getExperienceYears());
            if (candidate.getJobId() != null) {
                ps.setInt(6, candidate.getJobId());
            }
            if (ps.executeUpdate() != 1 && candidate.getJobId() != null) {
                throw new IllegalArgumentException(
                        "The selected job is no longer open.");
            }
        }
    }

    public boolean updateCandidate(int id, String status, Integer jobId)
            throws SQLException {
        String sql = """
            UPDATE candidates
            SET status = ?, job_id = ?
            WHERE id = ?
              AND (? IS NULL OR EXISTS (
                  SELECT 1 FROM jobs WHERE id = ? AND status = 'OPEN'
              ) OR job_id = ?)
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            if (jobId == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
                ps.setNull(4, java.sql.Types.INTEGER);
                ps.setNull(5, java.sql.Types.INTEGER);
                ps.setNull(6, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, jobId);
                ps.setInt(4, jobId);
                ps.setInt(5, jobId);
                ps.setInt(6, jobId);
            }
            ps.setInt(3, id);
            return ps.executeUpdate() == 1;
        }
    }

    private Candidate mapCandidate(ResultSet rs) throws SQLException {
        return new Candidate(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("skills"),
            rs.getBigDecimal("experience_years"),
            rs.getString("status"),
            rs.getObject("job_id", Integer.class),
            rs.getString("job_title")
        );
    }
}

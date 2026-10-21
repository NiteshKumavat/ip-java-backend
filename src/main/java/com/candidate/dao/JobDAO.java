package com.candidate.dao;

import com.candidate.model.Job;
import com.candidate.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {

    public List<Job> getAllJobs() throws SQLException {
        List<Job> jobs = new ArrayList<>();
        String sql = """
            SELECT id, title, department, location, employment_type,
                   description, salary_min, salary_max, status, created_at
            FROM jobs
            ORDER BY created_at DESC, id DESC
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                jobs.add(mapJob(rs));
            }
        }
        return jobs;
    }

    public List<Job> getOpenJobs() throws SQLException {
        List<Job> jobs = new ArrayList<>();
        String sql = """
            SELECT id, title, department, location, employment_type,
                   description, salary_min, salary_max, status, created_at
            FROM jobs
            WHERE status = 'OPEN'
            ORDER BY title
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                jobs.add(mapJob(rs));
            }
        }
        return jobs;
    }

    public void createJob(String title, String department, String location,
                          String employmentType, String description,
                          BigDecimal salaryMin, BigDecimal salaryMax,
                          int recruiterId) throws SQLException {
        String sql = """
            INSERT INTO jobs (title, department, location, employment_type,
                              description, salary_min, salary_max,
                              status, recruiter_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, 'OPEN', ?)
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, department);
            ps.setString(3, location);
            ps.setString(4, employmentType);
            ps.setString(5, description);
            setNullableDecimal(ps, 6, salaryMin);
            setNullableDecimal(ps, 7, salaryMax);
            ps.setInt(8, recruiterId);
            ps.executeUpdate();
        }
    }

    public boolean closeJob(int id, int recruiterId) throws SQLException {
        String sql = """
            UPDATE jobs
            SET status = 'CLOSED'
            WHERE id = ? AND recruiter_id = ? AND status = 'OPEN'
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, recruiterId);
            return ps.executeUpdate() == 1;
        }
    }

    private Job mapJob(ResultSet rs) throws SQLException {
        return new Job(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("department"),
            rs.getString("location"),
            rs.getString("employment_type"),
            rs.getString("description"),
            rs.getBigDecimal("salary_min"),
            rs.getBigDecimal("salary_max"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        );
    }

    private void setNullableDecimal(PreparedStatement ps, int index,
                                    BigDecimal value) throws SQLException {
        if (value == null) {
            ps.setNull(index, java.sql.Types.DECIMAL);
        } else {
            ps.setBigDecimal(index, value);
        }
    }
}

package com.candidate.servlet;

import com.candidate.dao.JobDAO;
import com.candidate.model.Job;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@WebServlet("/jobs")
public class JobsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Set<String> EMPLOYMENT_TYPES = Set.of(
        "Full-time", "Part-time", "Contract", "Internship"
    );
    private final JobDAO jobDAO = new JobDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        request.setAttribute("successMessage",
                successMessage(request.getParameter("saved")));
        showJobs(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("create".equals(action)) {
                createJob(request);
                response.sendRedirect(request.getContextPath()
                        + "/jobs?saved=created");
            } else if ("close".equals(action)) {
                int id = positiveId(request.getParameter("jobId"));
                HttpSession session = request.getSession(false);
                Object recruiterId = session == null ? null
                        : session.getAttribute("recruiterId");
                if (!(recruiterId instanceof Integer)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                if (!jobDAO.closeJob(id, (Integer) recruiterId)) {
                    request.setAttribute("errorMessage",
                            "That open job could not be closed.");
                    showJobs(request, response);
                    return;
                }
                response.sendRedirect(request.getContextPath()
                        + "/jobs?saved=closed");
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Unknown job action.");
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            showJobs(request, response);
        } catch (SQLException e) {
            getServletContext().log("Failed to update jobs", e);
            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("errorMessage",
                    "Unable to save the job. Check the database configuration "
                    + "and try again.");
            showJobs(request, response);
        }
    }

    private void createJob(HttpServletRequest request) throws SQLException {
        String title = required(request, "title", "Job title", 120);
        String department = required(request, "department", "Department", 100);
        String location = required(request, "location", "Location", 120);
        String employmentType = request.getParameter("employmentType");
        String description = required(request, "description",
                "Description", 4000);
        if (employmentType == null
                || !EMPLOYMENT_TYPES.contains(employmentType)) {
            throw new IllegalArgumentException(
                    "Choose a valid employment type.");
        }

        BigDecimal salaryMin = optionalMoney(request.getParameter("salaryMin"));
        BigDecimal salaryMax = optionalMoney(request.getParameter("salaryMax"));
        if (salaryMin != null && salaryMax != null
                && salaryMin.compareTo(salaryMax) > 0) {
            throw new IllegalArgumentException(
                    "The minimum salary cannot exceed the maximum salary.");
        }

        HttpSession session = request.getSession(false);
        Object recruiterId = session == null ? null
                : session.getAttribute("recruiterId");
        if (!(recruiterId instanceof Integer)) {
            throw new IllegalArgumentException(
                    "Your recruiter session has expired. Sign in again.");
        }
        jobDAO.createJob(title, department, location, employmentType,
                description, salaryMin, salaryMax, (Integer) recruiterId);
    }

    private String required(HttpServletRequest request, String parameter,
                            String label, int maxLength) {
        String value = request.getParameter(parameter);
        if (value == null || value.isBlank() || value.trim().length() > maxLength) {
            throw new IllegalArgumentException(label
                    + " is required and must be at most " + maxLength
                    + " characters.");
        }
        return value.trim();
    }

    private BigDecimal optionalMoney(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            BigDecimal amount = new BigDecimal(value.trim());
            if (amount.signum() < 0 || amount.scale() > 2
                    || amount.compareTo(new BigDecimal("9999999999.99")) > 0) {
                throw new IllegalArgumentException(
                        "Salary must be a non-negative amount with up to "
                        + "two decimal places.");
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Enter a valid salary amount.");
        }
    }

    private int positiveId(String value) {
        try {
            int id = Integer.parseInt(value);
            if (id > 0) {
                return id;
            }
        } catch (NumberFormatException ignored) {
            // The common validation message below covers malformed IDs.
        }
        throw new IllegalArgumentException("Select a valid job.");
    }

    private void showJobs(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Job> jobs = jobDAO.getAllJobs();
            request.setAttribute("jobs", jobs);
            request.getRequestDispatcher("/WEB-INF/views/jobs.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            getServletContext().log("Failed to retrieve jobs", e);
            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("errorMessage",
                    "Unable to load jobs. Apply database/schema.sql and check "
                    + "the database configuration.");
            request.getRequestDispatcher("/WEB-INF/views/jobs.jsp")
                    .forward(request, response);
        }
    }

    private String successMessage(String saved) {
        if ("created".equals(saved)) return "Job created successfully.";
        if ("closed".equals(saved)) return "Job closed successfully.";
        return null;
    }
}

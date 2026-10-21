package com.candidate.servlet;

import com.candidate.dao.CandidateDAO;
import com.candidate.dao.JobDAO;
import com.candidate.model.Candidate;
import com.candidate.model.Job;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@WebServlet("/candidates")
public class CandidateServlet extends HttpServlet {

    private static final Set<String> STATUSES = Set.of(
        "New", "Screening", "Interview", "Offer", "Hired", "Rejected"
    );
    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final JobDAO jobDAO = new JobDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        String status = request.getParameter("status");
        if (status != null && !status.isBlank() && !STATUSES.contains(status)) {
            status = null;
        }
        request.setAttribute("search", safeSearch(request.getParameter("q")));
        request.setAttribute("selectedStatus", status == null ? "" : status);
        request.setAttribute("successMessage",
                successMessage(request.getParameter("saved")));
        showCandidates(request, response, status);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("create".equals(action)) {
                createCandidate(request);
                response.sendRedirect(request.getContextPath()
                        + "/candidates?saved=created");
            } else if ("update".equals(action)) {
                int id = positiveId(request.getParameter("candidateId"));
                String status = request.getParameter("candidateStatus");
                if (status == null || !STATUSES.contains(status)) {
                    throw new IllegalArgumentException(
                            "Choose a valid candidate stage.");
                }
                Integer jobId = optionalPositiveId(
                        request.getParameter("jobId"));
                if (!candidateDAO.updateCandidate(id, status, jobId)) {
                    throw new IllegalArgumentException(
                            "Candidate or selected open job was not found.");
                }
                response.sendRedirect(request.getContextPath()
                        + "/candidates?saved=updated");
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Unknown candidate action.");
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            showCandidates(request, response, validStatus(
                    request.getParameter("status")));
        } catch (SQLException e) {
            getServletContext().log("Failed to update candidates", e);
            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("errorMessage",
                    "Unable to save the candidate. Check the database "
                    + "configuration and try again.");
            showCandidates(request, response, validStatus(
                    request.getParameter("status")));
        }
    }

    private void createCandidate(HttpServletRequest request)
            throws SQLException {
        String name = required(request, "name", "Name", 120);
        String email = required(request, "email", "Email", 254);
        if (!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        String phone = optionalText(request.getParameter("phone"), "Phone", 30);
        String skills = optionalText(request.getParameter("skills"), "Skills",
                2000);
        BigDecimal experience = optionalExperience(
                request.getParameter("experienceYears"));
        Integer jobId = optionalPositiveId(request.getParameter("jobId"));

        candidateDAO.addCandidate(new Candidate(0, name, email, phone, skills,
                experience, "New", jobId));
    }

    private String required(HttpServletRequest request, String parameter,
                            String label, int maxLength) {
        String value = request.getParameter(parameter);
        if (value == null || value.isBlank()
                || value.trim().length() > maxLength) {
            throw new IllegalArgumentException(label
                    + " is required and must be at most " + maxLength
                    + " characters.");
        }
        return value.trim();
    }

    private String optionalText(String value, String label, int maxLength) {
        if (value == null || value.isBlank()) return null;
        if (value.trim().length() > maxLength) {
            throw new IllegalArgumentException(label + " must be at most "
                    + maxLength + " characters.");
        }
        return value.trim();
    }

    private BigDecimal optionalExperience(String value) {
        if (value == null || value.isBlank()) return BigDecimal.ZERO;
        try {
            BigDecimal years = new BigDecimal(value.trim());
            if (years.signum() < 0 || years.compareTo(new BigDecimal("99.9")) > 0
                    || years.scale() > 1) {
                throw new IllegalArgumentException(
                        "Experience must be from 0 to 99.9 years, "
                        + "with at most one decimal place.");
            }
            return years;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Enter a valid number of experience years.");
        }
    }

    private Integer optionalPositiveId(String value) {
        if (value == null || value.isBlank()) return null;
        return positiveId(value);
    }

    private int positiveId(String value) {
        try {
            int id = Integer.parseInt(value);
            if (id > 0) return id;
        } catch (NumberFormatException ignored) {
            // The common validation message below covers malformed IDs.
        }
        throw new IllegalArgumentException("Select a valid record.");
    }

    private String safeSearch(String search) {
        if (search == null) return "";
        return search.length() > 100 ? search.substring(0, 100) : search;
    }

    private String validStatus(String status) {
        return status != null && STATUSES.contains(status) ? status : null;
    }

    private void showCandidates(HttpServletRequest request,
                                HttpServletResponse response,
                                String status)
            throws ServletException, IOException {
        try {
            String search = safeSearch(request.getParameter("q"));
            List<Candidate> candidates =
                    candidateDAO.searchCandidates(search, status);
            List<Job> openJobs = jobDAO.getOpenJobs();
            request.setAttribute("candidates", candidates);
            request.setAttribute("openJobs", openJobs);
            request.setAttribute("search", search);
            request.setAttribute("selectedStatus",
                    status == null ? "" : status);
            request.getRequestDispatcher("/candidates.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            getServletContext().log("Failed to retrieve candidates", e);
            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("errorMessage",
                    "Unable to load candidates. Apply database/schema.sql and "
                    + "check the database configuration.");
            request.getRequestDispatcher("/candidates.jsp")
                    .forward(request, response);
        }
    }

    private String successMessage(String saved) {
        if ("created".equals(saved)) return "Candidate added successfully.";
        if ("updated".equals(saved)) return "Candidate application updated.";
        return null;
    }
}

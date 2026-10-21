
package com.candidate.servlet;

import com.candidate.dao.RecruiterDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final RecruiterDAO recruiterDAO = new RecruiterDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null &&
                session.getAttribute("recruiterId") != null) {
            response.sendRedirect(
                request.getContextPath() + "/dashboard");
            return;
        }

        request.getRequestDispatcher("/login.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String rawPassword = request.getParameter("password");

        if (email == null || rawPassword == null ||
                email.isBlank() || rawPassword.isEmpty() ||
                email.length() > 150 || rawPassword.length() > 1024) {
            showError(request, response);
            return;
        }

        char[] password = rawPassword.toCharArray();

        try {
            RecruiterDAO.Recruiter recruiter =
                    recruiterDAO.authenticate(email, password);

            if (recruiter == null) {
                showError(request, response);
                return;
            }

            // Avoid retaining password copies longer than necessary.
            HttpSession session = request.getSession(true);

            // Prevent session fixation after login.
            request.changeSessionId();

            session.setAttribute("recruiterId", recruiter.getId());
            session.setAttribute("recruiterName", recruiter.getName());
            session.setAttribute("recruiterEmail", recruiter.getEmail());
            session.setMaxInactiveInterval(30 * 60);

            response.sendRedirect(
                request.getContextPath() + "/dashboard");

        } catch (SQLException e) {
            getServletContext().log("Recruiter login failed", e);
            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Unable to process login right now.");
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void showError(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("error",
                "Invalid email or password.");

        request.getRequestDispatcher("/login.jsp")
               .forward(request, response);
    }
}

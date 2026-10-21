<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.candidate.model.Job" %>
<%!
    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }

    private String salaryRange(Job job) {
        if (job.getSalaryMin() == null && job.getSalaryMax() == null) {
            return "Not specified";
        }
        String min = job.getSalaryMin() == null ? "" : job.getSalaryMin().toPlainString();
        String max = job.getSalaryMax() == null ? "" : job.getSalaryMax().toPlainString();
        if (min.isEmpty()) return "Up to " + max;
        if (max.isEmpty()) return "From " + min;
        return min + " – " + max;
    }
%>
<%
    String recruiterName = escapeHtml((String) session.getAttribute("recruiterName"));
    if (recruiterName == null || recruiterName.isEmpty()) recruiterName = "Recruiter";

    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
    int totalJobs = jobs == null ? 0 : jobs.size();
    int openJobs = 0;
    int closedJobs = 0;
    if (jobs != null) {
        for (Job job : jobs) {
            if ("OPEN".equalsIgnoreCase(job.getStatus())) openJobs++;
            else closedJobs++;
        }
    }
    String context = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Jobs | TalentDesk</title>
    <link rel="stylesheet" href="<%= context %>/css/style.css">
</head>
<body class="dashboard-page">
<aside class="sidebar">
	    <div class="brand"><span>T</span> TalentDesk</div>
	    <p class="nav-heading">WORKSPACE</p>
	    <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">▦ Dashboard</a>
	    <a class="nav-link" href="${pageContext.request.contextPath}/candidates">♙ Candidates</a>
	    <a class="nav-link active" href="${pageContext.request.contextPath}/jobs">▤ Jobs</a>
	    <div class="sidebar-bottom">
	        <div class="profile-avatar">R</div>
	        <div class="profile-text">
	            <strong><%= recruiterName %></strong>
	            <small>Recruiter</small>
	        </div>
	        <form action="${pageContext.request.contextPath}/logout" method="post">
	            <button class="logout-button" type="submit" title="Sign out">↪</button>
	        </form>
	    </div>
	</aside>
<div class="app-shell">
 

    <main class="main-content">
        <header class="topbar">
            <div>
                <p class="eyebrow">RECRUITMENT WORKSPACE</p>
                <h1>Jobs <span class="heading-dot">.</span></h1>
                <p class="page-subtitle">Create roles, track openings, and keep your hiring moving.</p>
            </div>
            <a class="primary-button topbar-action" href="#create-job">＋ <span>Post a job</span></a>
        </header>

        <% if (errorMessage != null) { %>
            <div class="alert" role="alert"><%= escapeHtml(errorMessage) %></div>
        <% } %>
        <% if (successMessage != null) { %>
            <div class="success-message" role="status"><%= escapeHtml(successMessage) %></div>
        <% } %>

        <section class="stats-grid" aria-label="Job statistics">
            <article class="stat-card">
                <div class="stat-top"><span>Total roles</span><span class="stat-icon blue">▤</span></div>
                <strong class="stat-value"><%= totalJobs %></strong>
                <span class="stat-note">Roles in your workspace</span>
            </article>
            <article class="stat-card">
                <div class="stat-top"><span>Open roles</span><span class="stat-icon green">↗</span></div>
                <strong class="stat-value"><%= openJobs %></strong>
                <span class="stat-note">Currently accepting applicants</span>
            </article>
            <article class="stat-card">
                <div class="stat-top"><span>Closed roles</span><span class="stat-icon amber">✓</span></div>
                <strong class="stat-value"><%= closedJobs %></strong>
                <span class="stat-note">No longer open for applications</span>
            </article>
        </section>

        <section class="content-panel create-panel" id="create-job">
            <div class="panel-heading">
                <div>
                    <p class="eyebrow">NEW OPPORTUNITY</p>
                    <h2>Post a job</h2>
                    <p class="section-description">Add the details candidates need to understand the role.</p>
                </div>
                <span class="panel-step">01 <span>/</span> DETAILS</span>
            </div>

            <form class="form-grid" action="<%= context %>/jobs" method="post">
                <input type="hidden" name="action" value="create">

                <div class="field">
                    <label for="title">Job title <span class="required">*</span></label>
                    <input id="title" name="title" maxlength="120" placeholder="e.g. Java Developer" required>
                </div>
                <div class="field">
                    <label for="department">Department <span class="required">*</span></label>
                    <input id="department" name="department" maxlength="100" placeholder="e.g. Engineering" required>
                </div>
                <div class="field">
                    <label for="location">Location <span class="required">*</span></label>
                    <input id="location" name="location" maxlength="120" placeholder="e.g. Mumbai / Remote" required>
                </div>
                <div class="field">
                    <label for="employmentType">Employment type <span class="required">*</span></label>
                    <select id="employmentType" name="employmentType" required>
                        <option value="">Choose employment type</option>
                        <option value="Full-time">Full-time</option>
                        <option value="Part-time">Part-time</option>
                        <option value="Contract">Contract</option>
                        <option value="Internship">Internship</option>
                    </select>
                </div>
                <div class="field">
                    <label for="salaryMin">Minimum salary</label>
                    <div class="input-with-prefix"><span>₹</span><input id="salaryMin" name="salaryMin" type="number" min="0" step="0.01" placeholder="e.g. 300000"></div>
                    <small class="field-hint">Use the same currency and pay period for both amounts.</small>
                </div>
                <div class="field">
                    <label for="salaryMax">Maximum salary</label>
                    <div class="input-with-prefix"><span>₹</span><input id="salaryMax" name="salaryMax" type="number" min="0" step="0.01" placeholder="e.g. 600000"></div>
                </div>
                <div class="field field-wide">
                    <label for="description">Job description <span class="required">*</span></label>
                    <textarea id="description" name="description" rows="5" maxlength="4000" placeholder="Describe the responsibilities, required skills, and what success looks like in this role..." required></textarea>
                    <small class="field-hint">Be clear about responsibilities and skills. Maximum 4,000 characters.</small>
                </div>
                <div class="form-actions field-wide">
                    <span class="form-footnote"><span class="required">*</span> Required fields</span>
                    <button class="primary-button" type="submit">Create job <span aria-hidden="true">→</span></button>
                </div>
            </form>
        </section>

        <section class="content-panel jobs-panel">
            <div class="panel-heading jobs-heading">
                <div>
                    <p class="eyebrow">YOUR OPENINGS</p>
                    <h2>All jobs <span class="inline-count"><%= totalJobs %></span></h2>
                    <p class="section-description">Manage the roles associated with your recruiter account.</p>
                </div>
                <span class="list-indicator"><span></span> Live workspace list</span>
            </div>

            <% if (jobs == null || jobs.isEmpty()) { %>
                <div class="empty-state">
                    <div class="empty-icon">▤</div>
                    <h3>No jobs yet</h3>
                    <p>Create your first opening using the form above. Your jobs will appear here.</p>
                    <a class="secondary-button" href="#create-job">Create your first job</a>
                </div>
            <% } else { %>
                <div class="table-wrapper">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>ROLE / DEPARTMENT</th>
                            <th>LOCATION</th>
                            <th>EMPLOYMENT</th>
                            <th>SALARY RANGE</th>
                            <th>STATUS</th>
                            <th><span class="sr-only">Actions</span></th>
                        </tr>
                        </thead>
                        <tbody>
                        <% for (Job job : jobs) { %>
                            <tr>
                                <td class="role-cell">
                                    <strong><%= escapeHtml(job.getTitle()) %></strong>
                                    <span><%= escapeHtml(job.getDepartment()) %></span>
                                    <% if (job.getDescription() != null && !job.getDescription().isBlank()) { %>
                                        <small class="table-detail"><%= escapeHtml(job.getDescription()) %></small>
                                    <% } %>
                                </td>
                                <td><span class="location-cell">⌖ <%= escapeHtml(job.getLocation()) %></span></td>
                                <td><%= escapeHtml(job.getEmploymentType()) %></td>
                                <td class="salary-cell"><%= escapeHtml(salaryRange(job)) %></td>
                                <td>
                                    <% if ("OPEN".equalsIgnoreCase(job.getStatus())) { %>
                                        <span class="stage-pill status-open"><span></span> Open</span>
                                    <% } else { %>
                                        <span class="stage-pill status-closed"><span></span> Closed</span>
                                    <% } %>
                                </td>
                                <td class="action-cell">
                                    <% if ("OPEN".equalsIgnoreCase(job.getStatus())) { %>
                                        <form action="<%= context %>/jobs" method="post" onsubmit="return confirm('Close this job? Candidates may no longer be able to apply.');">
                                            <input type="hidden" name="action" value="close">
                                            <input type="hidden" name="jobId" value="<%= job.getId() %>">
                                            <button class="secondary-button close-button" type="submit">Close role</button>
                                        </form>
                                    <% } else { %>
                                        <span class="muted">—</span>
                                    <% } %>
                                </td>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>
        </section>

        <footer class="page-footer">TalentDesk <span>·</span> Recruitment made clearer.</footer>
    </main>
</div>
</body>
</html>


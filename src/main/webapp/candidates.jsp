<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.candidate.model.Candidate" %>
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
%>
<%
    List<Candidate> candidates = (List<Candidate>) request.getAttribute("candidates");
    List<Job> openJobs = (List<Job>) request.getAttribute("openJobs");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
    String search = (String) request.getAttribute("search");
    String selectedStatus = (String) request.getAttribute("selectedStatus");
    
    String rawRecruiterName = (String) session.getAttribute("recruiterName");
    String recruiterName = escapeHtml(rawRecruiterName != null ? rawRecruiterName : "Recruiter");

    if (search == null) search = "";
    if (selectedStatus == null) selectedStatus = "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Candidates | TalentDesk</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="dashboard-page">
<aside class="sidebar">
    <div class="brand"><span>T</span> TalentDesk</div>
    <p class="nav-heading">WORKSPACE</p>
    <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">▦ Dashboard</a>
    <a class="nav-link active" href="${pageContext.request.contextPath}/candidates">♙ Candidates</a>
    <a class="nav-link" href="${pageContext.request.contextPath}/jobs">▤ Jobs</a>
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
<main class="main-content">
    <header class="topbar">
        <div>
            <p class="eyebrow">WORKSPACE</p>
            <h1>Candidates</h1>
        </div>
    </header>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <p class="alert" role="alert"><%= escapeHtml(errorMessage) %></p>
    <% } %>
    
    <% if (successMessage != null && !successMessage.isEmpty()) { %>
        <p class="success-message" role="status"><%= escapeHtml(successMessage) %></p>
    <% } %>

    <section class="content-panel">
        <h2>Add a candidate</h2>
        <p>Capture a candidate profile and optionally connect it to an open role.</p>
        <form class="form-grid" action="${pageContext.request.contextPath}/candidates" method="post">
            <input type="hidden" name="action" value="create">
            <div class="field">
                <label for="name">Full name</label>
                <input id="name" name="name" maxlength="120" required>
            </div>
            <div class="field">
                <label for="email">Email</label>
                <input id="email" name="email" type="email" maxlength="254" required>
            </div>
            <div class="field">
                <label for="phone">Phone</label>
                <input id="phone" name="phone" type="tel" maxlength="30">
            </div>
            <div class="field">
                <label for="experienceYears">Experience (years)</label>
                <input id="experienceYears" name="experienceYears" type="number" min="0" max="99.9" step="0.1" value="0">
            </div>
            <div class="field">
                <label for="newJobId">Open role</label>
                <select id="newJobId" name="jobId">
                    <option value="">Unassigned</option>
                    <% if (openJobs != null) {
                        for (Job job : openJobs) { %>
                            <option value="<%= job.getId() %>">
                                <%= escapeHtml(job.getTitle()) %> — <%= escapeHtml(job.getLocation()) %>
                            </option>
                    <%  }
                    } %>
                </select>
            </div>
            <div class="field field-wide">
                <label for="skills">Skills</label>
                <textarea id="skills" name="skills" rows="3" maxlength="2000" placeholder="e.g. Java, SQL, Spring"></textarea>
            </div>
            <div class="field field-wide">
                <button class="primary-button form-button" type="submit">
                    Add candidate
                </button>
            </div>
        </form>
    </section>

    <section class="content-panel section-gap">
        <div class="section-heading">
            <div>
                <h2>Candidate pipeline</h2>
                <p>Search profiles, change pipeline stages, and assign open roles.</p>
            </div>
        </div>
        <form class="filter-form" action="${pageContext.request.contextPath}/candidates" method="get">
            <div class="field filter-search">
                <label for="q">Search candidates</label>
                <input id="q" name="q" value="<%= escapeHtml(search) %>" maxlength="100" placeholder="Name, email or skill">
            </div>
            <div class="field filter-status">
                <label for="status">Stage</label>
                <select id="status" name="status">
                    <option value="">All stages</option>
                    <option value="New" <%= "New".equals(selectedStatus) ? "selected" : "" %>>New</option>
                    <option value="Screening" <%= "Screening".equals(selectedStatus) ? "selected" : "" %>>Screening</option>
                    <option value="Interview" <%= "Interview".equals(selectedStatus) ? "selected" : "" %>>Interview</option>
                    <option value="Offer" <%= "Offer".equals(selectedStatus) ? "selected" : "" %>>Offer</option>
                    <option value="Hired" <%= "Hired".equals(selectedStatus) ? "selected" : "" %>>Hired</option>
                    <option value="Rejected" <%= "Rejected".equals(selectedStatus) ? "selected" : "" %>>Rejected</option>
                </select>
            </div>
            <button class="secondary-button filter-button" type="submit">Filter</button>
            <a class="text-link clear-filter" href="${pageContext.request.contextPath}/candidates">Clear</a>
        </form>

        <% if (candidates == null || candidates.isEmpty()) { %>
            <p class="empty-state">No candidates match this search.</p>
        <% } else { %>
            <div class="table-wrapper">
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>Candidate</th>
                        <th>Contact</th>
                        <th>Skills</th>
                        <th>Experience</th>
                        <th>Current role</th>
                        <th>Pipeline</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% for (Candidate candidate : candidates) { %>
                        <tr>
                            <td>
                                <strong><%= escapeHtml(candidate.getName()) %></strong>
                                <small class="table-detail">#<%= candidate.getId() %></small>
                            </td>
                            <td>
                                <%= escapeHtml(candidate.getEmail()) %>
                                <small class="table-detail">
                                    <%= escapeHtml(candidate.getPhone()) %>
                                </small>
                            </td>
                            <td><%= escapeHtml(candidate.getSkills()) %></td>
                            <td><%= candidate.getExperienceYears() == null ? "0.0" : candidate.getExperienceYears() %> years</td>
                            <td><%= candidate.getJobTitle() == null ? "Unassigned" : escapeHtml(candidate.getJobTitle()) %></td>
                            <td>
                                <%
                                    boolean assignedJobOpen = false;
                                    if (candidate.getJobId() != null && openJobs != null) {
                                        for (Job openJob : openJobs) {
                                            if (openJob.getId() == candidate.getJobId()) {
                                                assignedJobOpen = true;
                                                break;
                                            }
                                        }
                                    }
                                %>
                                <form class="inline-update" action="${pageContext.request.contextPath}/candidates" method="post">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="candidateId" value="<%= candidate.getId() %>">
                                    <select name="candidateStatus" aria-label="Candidate stage">
                                        <option value="New" <%= "New".equals(candidate.getStatus()) ? "selected" : "" %>>New</option>
                                        <option value="Screening" <%= "Screening".equals(candidate.getStatus()) ? "selected" : "" %>>Screening</option>
                                        <option value="Interview" <%= "Interview".equals(candidate.getStatus()) ? "selected" : "" %>>Interview</option>
                                        <option value="Offer" <%= "Offer".equals(candidate.getStatus()) ? "selected" : "" %>>Offer</option>
                                        <option value="Hired" <%= "Hired".equals(candidate.getStatus()) ? "selected" : "" %>>Hired</option>
                                        <option value="Rejected" <%= "Rejected".equals(candidate.getStatus()) ? "selected" : "" %>>Rejected</option>
                                    </select>
                                    <select name="jobId" aria-label="Assigned open role">
                                        <option value="">Unassigned</option>
                                        <% if (candidate.getJobId() != null && !assignedJobOpen) { %>
                                            <option value="<%= candidate.getJobId() %>" selected>
                                                <%= escapeHtml(candidate.getJobTitle()) %> (closed)
                                            </option>
                                        <% } %>
                                        <% if (openJobs != null) {
                                            for (Job job : openJobs) { %>
                                                <option value="<%= job.getId() %>"
                                                    <%= candidate.getJobId() != null && candidate.getJobId() == job.getId() ? "selected" : "" %>>
                                                    <%= escapeHtml(job.getTitle()) %>
                                                </option>
                                        <%  }
                                        } %>
                                    </select>
                                    <button class="secondary-button" type="submit">Save</button>
                                </form>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>
    </section>
</main>
</body>
</html>
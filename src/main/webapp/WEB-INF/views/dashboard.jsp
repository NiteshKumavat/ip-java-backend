
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

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
    response.setHeader("Cache-Control", "no-store");
    String safeName = escapeHtml(
        String.valueOf(session.getAttribute("recruiterName"))
    );
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard | TalentDesk</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="dashboard-page">

<aside class="sidebar">
    <div class="brand"><span>T</span> TalentDesk</div>
    <p class="nav-heading">WORKSPACE</p>

    <a class="nav-link active"
       href="${pageContext.request.contextPath}/dashboard">▦ Dashboard</a>
    <a class="nav-link"
       href="${pageContext.request.contextPath}/candidates">♙ Candidates</a>
    <a class="nav-link"
       href="${pageContext.request.contextPath}/jobs">▤ Jobs</a>

    <div class="sidebar-bottom">
        <div class="profile-avatar">R</div>
        <div class="profile-text">
            <strong><%= safeName %></strong>
            <small>Recruiter</small>
        </div>

        <form action="${pageContext.request.contextPath}/logout"
              method="post">
            <button class="logout-button" type="submit"
                    title="Sign out">↪</button>
        </form>
    </div>
</aside>

<main class="main-content">
    <header class="topbar">
        <div>
            <p class="eyebrow">OVERVIEW</p>
            <h1>Dashboard</h1>
        </div>
        <span class="status-pill">● Signed in</span>
    </header>

    <section class="welcome-panel">
        <p class="eyebrow">RECRUITER WORKSPACE</p>
        <h2>Welcome back, <%= safeName %>.</h2>
        <p>Review candidate records and access your recruiter workspace.</p>
    </section>

    <section class="stats-grid">
        <article class="stat-card">
            <span class="stat-label">Candidate management</span>
            <h2>Review candidates</h2>
            <p>Browse candidate profiles and their application details.</p>
            <a class="text-link"
               href="${pageContext.request.contextPath}/candidates">
                Open candidates →
            </a>
        </article>
        <article class="stat-card">
            <span class="stat-label">Job management</span>
            <h2>Post roles</h2>
            <p>Create open positions and manage their status.</p>
            <a class="text-link"
               href="${pageContext.request.contextPath}/jobs">
                Manage jobs →
            </a>
        </article>
    </section>

    <section class="content-panel">
        <h2>Getting started</h2>
        <p>Manage your candidate pipeline, connect candidates to open jobs,
           and track each application through hiring.</p>
        <a class="text-link"
           href="${pageContext.request.contextPath}/candidates">
            Open candidate management →
        </a>
    </section>
</main>

</body>
</html>

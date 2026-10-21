
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
    response.setHeader("Pragma", "no-cache");

    if (session.getAttribute("recruiterId") != null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Recruiter Login | TalentDesk</title>
    <link rel="stylesheet"
          href="./css/style.css">
</head>
<body class="login-page">

<main class="login-card">
    <div class="brand-icon">T</div>
    <h1>Welcome back</h1>
    <p class="muted">Sign in to your recruiter workspace.</p>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert" role="alert">
            <%= escapeHtml(String.valueOf(request.getAttribute("error"))) %>
        </div>
    <% } %>

    <form action="${pageContext.request.contextPath}/login"
          method="post">
        <label for="email">Work email</label>
        <input type="email" id="email" name="email"
               maxlength="150" autocomplete="username"
               placeholder="you@company.com" required>

        <label for="password">Password</label>
        <input type="password" id="password" name="password"
               maxlength="1024" autocomplete="current-password"
               placeholder="Enter your password" required>

        <button type="submit" class="primary-button">Sign in</button>
    </form>

    <p class="login-footer">Recruiter access only</p>
</main>

</body>
</html>

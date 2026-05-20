<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>403 — Unauthorized | Active Learning, Inc.</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>

    <header class="top-bar">
        <span class="brand">Active Learning, Inc.</span>
        <span class="page-label">Access Denied</span>
    </header>

    <div class="error-page-wrapper">
        <div class="error-page-card">
            <div class="error-code">403</div>
            <div class="error-title">Access Denied</div>
            <p class="error-desc">
                You do not have permission to access this page.<br>
                This area is restricted to Admin users only.
                If you believe this is a mistake, please contact your system administrator.
            </p>
            <%
                HttpSession s = request.getSession(false);
                String role = (s != null) ? (String) s.getAttribute("role") : null;
                String dashLink = "Admin".equalsIgnoreCase(role)
                        ? request.getContextPath() + "/admin/dashboard.jsp"
                        : (role != null
                            ? request.getContextPath() + "/guest/dashboard.jsp"
                            : request.getContextPath() + "/login.jsp");
                String dashLabel = (role != null) ? "Go to Dashboard" : "Go to Login";
            %>
            <a href="<%= dashLink %>" class="btn-primary" style="width:auto;padding:11px 32px;display:inline-block;text-decoration:none;">
                <%= dashLabel %>
            </a>
        </div>
    </div>

</body>
</html>

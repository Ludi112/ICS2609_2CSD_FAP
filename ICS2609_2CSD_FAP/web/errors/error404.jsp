<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 — Page Not Found | Active Learning, Inc.</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>

    <header class="top-bar">
        <span class="brand">Active Learning, Inc.</span>
        <span class="page-label">Page Not Found</span>
    </header>

    <div class="error-page-wrapper">
        <div class="error-page-card">
            <div class="error-code">404</div>
            <div class="error-title">Page Not Found</div>
            <p class="error-desc">
                The page you are looking for does not exist or may have been moved.<br>
                Please check the URL or navigate back to the application.
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

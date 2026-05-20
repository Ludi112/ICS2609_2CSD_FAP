<%@ page contentType="text/html;charset=UTF-8" language="java"
         isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 — Server Error | Active Learning, Inc.</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>

    <header class="top-bar">
        <span class="brand">Active Learning, Inc.</span>
        <span class="page-label">Server Error</span>
    </header>

    <div class="error-page-wrapper">
        <div class="error-page-card">
            <div class="error-code">500</div>
            <div class="error-title">Internal Server Error</div>
            <p class="error-desc">
                Something went wrong on our end. The system encountered an unexpected error.<br>
                Please try again. If the problem persists, contact your system administrator.
            </p>
            <%
                // Log the exception info if available (isErrorPage="true" exposes `exception`)
                if (exception != null) {
                    // Write to server log — never expose stack trace to the user
                    application.log("error500.jsp caught: " + exception.getMessage(), exception);
                }
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

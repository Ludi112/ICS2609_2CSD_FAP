<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    javax.servlet.http.HttpSession sess = request.getSession(false);
    if (sess == null || sess.getAttribute("username") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if ("Admin".equalsIgnoreCase((String) sess.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        return;
    }
    String username = (String) sess.getAttribute("username");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Secondary Data | Active Learning, Inc.</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>
    <header class="top-bar">
        <span class="brand">Active Learning, Inc.</span>
        <span class="page-label">Secondary Data</span>
    </header>
    <%@ include file="/includes/navbar_guest.jsp" %>
    <div class="page-content">
        <h2 class="page-title">Secondary Data</h2>
        <div class="card">
            <p style="color:var(--text-muted);font-size:0.92rem;">
                This page displays selected data from the PostgreSQL audit log database.
                Full log access is restricted to administrators.
            </p>
            <br>
            <div class="info-message">No records available for your account at this time.</div>
        </div>
    </div>
</body>
</html>
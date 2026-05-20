<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    // AuthFilter already guards this page; this is a safety double-check.
    HttpSession sess = request.getSession(false);
    if (sess == null || sess.getAttribute("username") == null
            || !"Admin".equalsIgnoreCase((String) sess.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String adminUsername = (String) sess.getAttribute("username");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard | Active Learning, Inc.</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>

    <header class="top-bar">
        <span class="brand">Active Learning, Inc.</span>
        <span class="page-label">Wireframe: Admin Dashboard</span>
    </header>

    <%@ include file="/includes/navbar_admin.jsp" %>

    <div class="page-content">

        <%-- Success / error flash messages --%>
        <%
            String flashMsg = (String) session.getAttribute("flashMessage");
            String flashType = (String) session.getAttribute("flashType");
            if (flashMsg != null) {
                session.removeAttribute("flashMessage");
                session.removeAttribute("flashType");
        %>
            <div class="<%= "error".equals(flashType) ? "error-message" : "success-message" %>">
                <%= flashMsg %>
            </div>
        <% } %>

        <!-- Summary Cards (Figure 3 wireframe) -->
        <div class="summary-grid">
            <div class="summary-card">
                <h3>Total Users</h3>
                <%-- TODO (Database Architect): Replace with actual count from Derby --%>
                <div class="summary-value">50+</div>
                <div class="summary-sub">Sample summary information</div>
            </div>

            <div class="summary-card">
                <h3>Reports Today</h3>
                <%-- TODO (PDF Reports): Replace with actual count from PostgreSQL --%>
                <div class="summary-value">0</div>
                <div class="summary-sub">Sample summary information</div>
            </div>

            <div class="summary-card">
                <h3>Latest Login</h3>
                <%-- TODO (Core Business): Replace with actual last login time from PostgreSQL --%>
                <div class="summary-value" style="font-size:1.4rem;">
                    <%= new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date()) %>
                </div>
                <div class="summary-sub">Sample summary information</div>
            </div>
        </div>

        <!-- Quick Actions (Figure 3 wireframe) -->
        <div class="card">
            <h3 style="margin-bottom:16px;color:var(--primary);">Quick Actions</h3>
            <div class="quick-actions">
                <a href="<%= request.getContextPath() %>/admin/users.jsp">Manage Users</a>
                <a href="<%= request.getContextPath() %>/admin/reports.jsp">Generate Reports</a>
                <a href="<%= request.getContextPath() %>/admin/mysql.jsp">Open 2nd DBMS</a>
                <a href="<%= request.getContextPath() %>/admin/postgresql.jsp">Open 3rd DBMS</a>
            </div>

            <div style="margin-top:20px;padding-top:16px;border-top:1px solid var(--border);">
                <strong>System Summary</strong>
                <ul style="margin-top:10px;padding-left:18px;color:var(--text-muted);font-size:0.9rem;line-height:1.9;">
                    <li>Admin and Guest users can log in.</li>
                    <li>Admin can generate reports and view all records.</li>
                    <li>The system integrates at least three RDBMS.</li>
                </ul>
            </div>
        </div>

    </div><!-- /.page-content -->
</body>
</html>

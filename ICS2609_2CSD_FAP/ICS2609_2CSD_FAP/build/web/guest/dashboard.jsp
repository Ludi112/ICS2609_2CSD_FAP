<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    // AuthFilter already guards this page; this is a safety double-check.
    HttpSession sess = request.getSession(false);
    if (sess == null || sess.getAttribute("username") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    // Block Admin from Guest pages (they should be on /admin/*)
    String role = (String) sess.getAttribute("role");
    if ("Admin".equalsIgnoreCase(role)) {
        response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        return;
    }
    String guestUsername = (String) sess.getAttribute("username");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Guest Dashboard | Active Learning, Inc.</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>

    <header class="top-bar">
        <span class="brand">Active Learning, Inc.</span>
        <span class="page-label">Wireframe: Guest Dashboard</span>
    </header>

    <%@ include file="/includes/navbar_guest.jsp" %>

    <div class="page-content">

        <div class="card" style="margin-bottom:24px;">
            <strong>Guest Dashboard</strong>
            <p style="margin-top:8px;color:var(--text-muted);font-size:0.92rem;">
                Guest users have limited access. Admin-only reports are restricted.
            </p>
        </div>

        <!-- Three feature cards (Figure 4 wireframe) -->
        <div class="summary-grid">

            <div class="card" style="margin-bottom:0;">
                <h3 style="color:var(--primary);margin-bottom:8px;">My Profile</h3>
                <p style="color:var(--text-muted);font-size:0.88rem;margin-bottom:16px;">
                    View guest account details.
                </p>
                <a href="<%= request.getContextPath() %>/guest/profile.jsp"
                   class="btn-secondary" style="display:inline-block;padding:9px 20px;font-size:0.88rem;text-decoration:none;border-radius:6px;">
                    Open
                </a>
            </div>

            <div class="card" style="margin-bottom:0;">
                <h3 style="color:var(--primary);margin-bottom:8px;">Allowed Records</h3>
                <p style="color:var(--text-muted);font-size:0.88rem;margin-bottom:16px;">
                    View guest-accessible records.
                </p>
                <%-- TODO (Core Business / Dan): Connect this to enrolled courses in MySQL --%>
                <a href="<%= request.getContextPath() %>/guest/courses.jsp"
                   class="btn-secondary" style="display:inline-block;padding:9px 20px;font-size:0.88rem;text-decoration:none;border-radius:6px;">
                    Open
                </a>
            </div>

            <div class="card" style="margin-bottom:0;">
                <h3 style="color:var(--primary);margin-bottom:8px;">Secondary Data</h3>
                <p style="color:var(--text-muted);font-size:0.88rem;margin-bottom:16px;">
                    Access selected data from another DBMS.
                </p>
                <%-- TODO (Core Business / Dan): Connect this to PostgreSQL logs if applicable --%>
                <a href="<%= request.getContextPath() %>/guest/logs.jsp"
                   class="btn-secondary" style="display:inline-block;padding:9px 20px;font-size:0.88rem;text-decoration:none;border-radius:6px;">
                    Open
                </a>
            </div>

        </div>
    </div><!-- /.page-content -->
</body>
</html>

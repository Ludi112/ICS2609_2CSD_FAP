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
    <title>Allowed Records | Active Learning, Inc.</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>
    <header class="top-bar">
        <span class="brand">Active Learning, Inc.</span>
        <span class="page-label">Allowed Records</span>
    </header>
    <%@ include file="/includes/navbar_guest.jsp" %>
    <div class="page-content">
        <h2 class="page-title">Allowed Records</h2>
        <div class="card">
            <p style="color:var(--text-muted);font-size:0.92rem;margin-bottom:20px;">
                Courses and enrollments you have access to are listed below.
            </p>
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Course Name</th>
                            <th>Status</th>
                            <th>Enrolled Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td colspan="4" style="text-align:center;color:var(--text-muted);padding:24px;">
                                No enrolled courses found.
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>
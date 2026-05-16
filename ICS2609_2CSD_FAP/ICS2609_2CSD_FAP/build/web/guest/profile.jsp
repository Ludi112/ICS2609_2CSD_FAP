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
    String role     = (String) sess.getAttribute("role");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile | Active Learning, Inc.</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>
    <header class="top-bar">
        <span class="brand">Active Learning, Inc.</span>
        <span class="page-label">My Profile</span>
    </header>
    <%@ include file="/includes/navbar_guest.jsp" %>
    <div class="page-content">
        <h2 class="page-title">My Profile</h2>
        <div class="card">
            <table class="data-table" style="max-width:500px;">
                <tbody>
                    <tr>
                        <td><strong>Username</strong></td>
                        <td><%= username %></td>
                    </tr>
                    <tr>
                        <td><strong>Role</strong></td>
                        <td><%= role %></td>
                    </tr>
                    <tr>
                        <td><strong>Access Level</strong></td>
                        <td>Guest — Read Only</td>
                    </tr>
                </tbody>
            </table>
            <div style="margin-top:24px;">
                <a href="<%= request.getContextPath() %>/LogoutServlet" class="btn-danger" style="padding:10px 24px;border-radius:7px;font-size:0.9rem;">
                    Logout
                </a>
            </div>
        </div>
    </div>
</body>
</html>
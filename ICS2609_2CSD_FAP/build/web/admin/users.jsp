<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*, Model.DBConnectionManager" %>
<%
    javax.servlet.http.HttpSession sess = request.getSession(false);
    if (sess == null || sess.getAttribute("username") == null
            || !"Admin".equalsIgnoreCase((String) sess.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String username = (String) sess.getAttribute("username");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Users | Active Learning, Inc.</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>
    <header class="top-bar">
        <span class="brand">Active Learning, Inc.</span>
        <span class="page-label">Manage Users</span>
    </header>
    <%@ include file="/includes/navbar_admin.jsp" %>
    <div class="page-content">
        <h2 class="page-title">Manage Users</h2>
        <div class="card">
            <%
                Connection derbyConn = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                String dbError = null;
                try {
                    derbyConn = DBConnectionManager.getDerbyConnection(application);
                    ps = derbyConn.prepareStatement(
                        "SELECT Username, Role, Created_Date FROM Users ORDER BY Role ASC, Username ASC");
                    rs = ps.executeQuery();
                    boolean hasUsers = false;
            %>
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Username</th>
                            <th>Role</th>
                            <th>Created Date</th>
                        </tr>
                    </thead>
                    <tbody>
                    <%
                        int rowNum = 1;
                        while (rs.next()) {
                            hasUsers = true;
                    %>
                        <tr>
                            <td><%= rowNum++ %></td>
                            <td><%= rs.getString("Username") %></td>
                            <td><%= rs.getString("Role") %></td>
                            <td><%= rs.getTimestamp("Created_Date") %></td>
                        </tr>
                    <%
                        }
                        if (!hasUsers) {
                    %>
                        <tr>
                            <td colspan="4" style="text-align:center;color:var(--text-muted);padding:24px;">
                                No users found.
                            </td>
                        </tr>
                    <%  } %>
                    </tbody>
                </table>
            </div>
            <%
                } catch (Exception e) {
                    dbError = e.getMessage();
                } finally {
                    if (rs != null) try { rs.close(); } catch (Exception ignored) {}
                    if (ps != null) try { ps.close(); } catch (Exception ignored) {}
                    if (derbyConn != null) try { derbyConn.close(); } catch (Exception ignored) {}
                }
                if (dbError != null) {
            %>
                <div class="error-message">Derby Error: <%= dbError %></div>
            <% } %>
        </div>
    </div>
</body>
</html>
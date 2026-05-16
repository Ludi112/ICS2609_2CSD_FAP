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
    <title>PostgreSQL Database | Active Learning, Inc.</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>
    <header class="top-bar">
        <span class="brand">Active Learning, Inc.</span>
        <span class="page-label">PostgreSQL — Audit Logs</span>
    </header>
    <%@ include file="/includes/navbar_admin.jsp" %>
    <div class="page-content">
        <h2 class="page-title">PostgreSQL Database</h2>

        <div class="summary-grid">
            <div class="summary-card">
                <h3>Database</h3>
                <div class="summary-value" style="font-size:1.1rem;">lms_logs_db</div>
                <div class="summary-sub">PostgreSQL — localhost:5432</div>
            </div>
            <div class="summary-card">
                <h3>Purpose</h3>
                <div class="summary-value" style="font-size:1.1rem;">Audit</div>
                <div class="summary-sub">Logs &amp; Report History</div>
            </div>
        </div>

        <%-- ── AUDIT LOGS ───────────────────────────────────────────── --%>
        <div class="card">
            <h3 style="color:var(--primary);margin-bottom:16px;">Audit Logs</h3>
            <%
                Connection pgConn = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                String dbError = null;
                try {
                    pgConn = DBConnectionManager.getPostgresConnection(application);
                    ps = pgConn.prepareStatement(
                        "SELECT Log_ID, Username, Action_Type, Description, Timestamp " +
                        "FROM System_Audit_Logs ORDER BY Timestamp DESC");
                    rs = ps.executeQuery();
                    boolean hasLogs = false;
            %>
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Log ID</th>
                            <th>Username</th>
                            <th>Action</th>
                            <th>Description</th>
                            <th>Timestamp</th>
                        </tr>
                    </thead>
                    <tbody>
                    <%
                        int rowNum = 1;
                        while (rs.next()) {
                            hasLogs = true;
                    %>
                        <tr>
                            <td><%= rowNum++ %></td>
                            <td><%= rs.getInt("Log_ID") %></td>
                            <td><%= rs.getString("Username") %></td>
                            <td><%= rs.getString("Action_Type") %></td>
                            <td><%= rs.getString("Description") %></td>
                            <td><%= rs.getTimestamp("Timestamp") %></td>
                        </tr>
                    <%
                        }
                        if (!hasLogs) {
                    %>
                        <tr>
                            <td colspan="6" style="text-align:center;color:var(--text-muted);padding:24px;">
                                No audit logs yet.
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
                }
                if (dbError != null) {
            %>
                <div class="error-message">PostgreSQL Error (Audit Logs): <%= dbError %></div>
            <% } %>
        </div>

        <%-- ── REPORT HISTORY ──────────────────────────────────────── --%>
        <div class="card">
            <h3 style="color:var(--primary);margin-bottom:16px;">Report History</h3>
            <%
                PreparedStatement ps2 = null;
                ResultSet rs2 = null;
                String dbError2 = null;
                try {
                    ps2 = pgConn.prepareStatement(
                        "SELECT Report_ID, Username, Report_Type, Generated_At " +
                        "FROM Report_History ORDER BY Generated_At DESC");
                    rs2 = ps2.executeQuery();
                    boolean hasReports = false;
            %>
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Report ID</th>
                            <th>Generated By</th>
                            <th>Report Type</th>
                            <th>Date</th>
                        </tr>
                    </thead>
                    <tbody>
                    <%
                        int rowNum2 = 1;
                        while (rs2.next()) {
                            hasReports = true;
                    %>
                        <tr>
                            <td><%= rowNum2++ %></td>
                            <td><%= rs2.getInt("Report_ID") %></td>
                            <td><%= rs2.getString("Username") %></td>
                            <td><%= rs2.getString("Report_Type") %></td>
                            <td><%= rs2.getTimestamp("Generated_At") %></td>
                        </tr>
                    <%
                        }
                        if (!hasReports) {
                    %>
                        <tr>
                            <td colspan="5" style="text-align:center;color:var(--text-muted);padding:24px;">
                                No report history yet.
                            </td>
                        </tr>
                    <%  } %>
                    </tbody>
                </table>
            </div>
            <%
                } catch (Exception e) {
                    dbError2 = e.getMessage();
                } finally {
                    if (rs2 != null) try { rs2.close(); } catch (Exception ignored) {}
                    if (ps2 != null) try { ps2.close(); } catch (Exception ignored) {}
                    if (pgConn != null) try { pgConn.close(); } catch (Exception ignored) {}
                }
                if (dbError2 != null) {
            %>
                <div class="error-message">PostgreSQL Error (Report History): <%= dbError2 %></div>
            <% } %>
        </div>

    </div>
</body>
</html>
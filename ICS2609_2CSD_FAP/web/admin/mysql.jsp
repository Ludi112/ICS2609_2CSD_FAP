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
    <title>MySQL Database | Active Learning, Inc.</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body>
    <header class="top-bar">
        <span class="brand">Active Learning, Inc.</span>
        <span class="page-label">MySQL — Business Data</span>
    </header>
    <%@ include file="/includes/navbar_admin.jsp" %>
    <div class="page-content">
        <h2 class="page-title">MySQL Database</h2>

        <div class="summary-grid">
            <div class="summary-card">
                <h3>Database</h3>
                <div class="summary-value" style="font-size:1.1rem;">lms_business_db</div>
                <div class="summary-sub">MySQL — localhost:3306</div>
            </div>
            <div class="summary-card">
                <h3>Purpose</h3>
                <div class="summary-value" style="font-size:1.1rem;">Business</div>
                <div class="summary-sub">Courses &amp; Enrollments</div>
            </div>
        </div>

        <%-- ── COURSES TABLE ──────────────────────────────────────── --%>
        <div class="card">
            <h3 style="color:var(--primary);margin-bottom:16px;">Courses Table</h3>
            <%
                Connection mysqlConn = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                String dbError = null;
                try {
                    mysqlConn = DBConnectionManager.getMySQLConnection(application);
                    ps = mysqlConn.prepareStatement(
                        "SELECT Course_ID, Course_Code, Title, Description, Credits, Instructor_Username, Created_Date " +
                        "FROM Courses ORDER BY Course_ID ASC");
                    rs = ps.executeQuery();
                    boolean hasCourses = false;
            %>
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Course ID</th>
                            <th>Code</th>
                            <th>Title</th>
                            <th>Credits</th>
                            <th>Instructor</th>
                            <th>Created</th>
                        </tr>
                    </thead>
                    <tbody>
                    <%
                        int rowNum = 1;
                        while (rs.next()) {
                            hasCourses = true;
                    %>
                        <tr>
                            <td><%= rowNum++ %></td>
                            <td><%= rs.getInt("Course_ID") %></td>
                            <td><%= rs.getString("Course_Code") %></td>
                            <td><%= rs.getString("Title") %></td>
                            <td><%= rs.getInt("Credits") %></td>
                            <td><%= rs.getString("Instructor_Username") %></td>
                            <td><%= rs.getTimestamp("Created_Date") %></td>
                        </tr>
                    <%
                        }
                        if (!hasCourses) {
                    %>
                        <tr>
                            <td colspan="7" style="text-align:center;color:var(--text-muted);padding:24px;">
                                No courses found.
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
                <div class="error-message">MySQL Error (Courses): <%= dbError %></div>
            <% } %>
        </div>

        <%-- ── ENROLLMENTS TABLE ───────────────────────────────────── --%>
        <div class="card">
            <h3 style="color:var(--primary);margin-bottom:16px;">Enrollments Table</h3>
            <%
                PreparedStatement ps2 = null;
                ResultSet rs2 = null;
                String dbError2 = null;
                try {
                    ps2 = mysqlConn.prepareStatement(
                        "SELECT e.Enrollment_ID, e.Student_Username, c.Title, e.Enrollment_Date, e.Status " +
                        "FROM Enrollments e JOIN Courses c ON e.Course_ID = c.Course_ID " +
                        "ORDER BY e.Enrollment_ID ASC");
                    rs2 = ps2.executeQuery();
                    boolean hasEnrollments = false;
            %>
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Enrollment ID</th>
                            <th>Student</th>
                            <th>Course</th>
                            <th>Date</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                    <%
                        int rowNum2 = 1;
                        while (rs2.next()) {
                            hasEnrollments = true;
                    %>
                        <tr>
                            <td><%= rowNum2++ %></td>
                            <td><%= rs2.getInt("Enrollment_ID") %></td>
                            <td><%= rs2.getString("Student_Username") %></td>
                            <td><%= rs2.getString("Title") %></td>
                            <td><%= rs2.getDate("Enrollment_Date") %></td>
                            <td><%= rs2.getString("Status") %></td>
                        </tr>
                    <%
                        }
                        if (!hasEnrollments) {
                    %>
                        <tr>
                            <td colspan="6" style="text-align:center;color:var(--text-muted);padding:24px;">
                                No enrollments found.
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
                    if (mysqlConn != null) try { mysqlConn.close(); } catch (Exception ignored) {}
                }
                if (dbError2 != null) {
            %>
                <div class="error-message">MySQL Error (Enrollments): <%= dbError2 %></div>
            <% } %>
        </div>

    </div>
</body>
</html>
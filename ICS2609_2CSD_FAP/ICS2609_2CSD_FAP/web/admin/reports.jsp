<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Protect the page (Admin Only)
    javax.servlet.http.HttpSession sess = request.getSession(false);
    if (sess == null || sess.getAttribute("username") == null || !"Admin".equalsIgnoreCase((String) sess.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Report Generation</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
    
    <script>
        function toggleDateFields() {
            var type = document.getElementById("reportType").value;
            var dateGroup = document.getElementById("dateInputs");
            if (type === "time_bound") {
                dateGroup.style.display = "block";
                document.getElementById("startDate").required = true;
                document.getElementById("endDate").required = true;
            } else {
                dateGroup.style.display = "none";
                document.getElementById("startDate").required = false;
                document.getElementById("endDate").required = false;
            }
        }
    </script>
</head>
<body id="dashboard-page">
    
    <jsp:include page="/includes/navbar_admin.jsp" />

    <div class="main-content">
        <div class="center-card dashboard-card">
            <h1 class="page-title text-center">Report Generation</h1>
            <p class="subtitle text-center">Export Database Records</p>
            
            <%
                String error = request.getParameter("error");
                if ("date_invalid".equals(error)) {
                    out.print("<div class='error-message'>Invalid Date Range: Start date must be before End date.</div>");
                } else if ("no_records".equals(error)) {
                    out.print("<div class='info-message'>No records found for the selected criteria.</div>");
                }
            %>

            <div class="admin-form-container">
                <form action="<%= request.getContextPath() %>/ReportServlet" method="POST" target="_blank" class="admin-form">
                    
                    <div class="input-group" style="width: 100%;">
                        <label for="reportType">Select Report Type:</label>
                        <select id="reportType" name="reportType" class="admin-select" onchange="toggleDateFields()" required style="width: 100%;">
                            <option value="all_records">All Records Report (Users)</option>
                            <option value="admin_records">My Admin Records</option>
                            <option value="mysql_records">Additional DBMS Report (Courses/Enrollments)</option>
                            <option value="time_bound">Time-Bound Report (Enrollments)</option>
                        </select>
                    </div>

                    <div id="dateInputs" style="display: none; width: 100%;">
                        <div class="input-group">
                            <label for="startDate">Start Date:</label>
                            <input type="date" id="startDate" name="startDate" class="admin-input">
                        </div>
                        <div class="input-group">
                            <label for="endDate">End Date:</label>
                            <input type="date" id="endDate" name="endDate" class="admin-input">
                        </div>
                    </div>

                    <div class="action-row" style="justify-content: center; width: 100%; margin-top: 20px;">
                        <button type="submit" class="btn-submit btn-admin-form">Generate & Download PDF</button>
                    </div>
                </form>
            </div>

            <div style="font-size: 0.85rem; color: rgba(227, 241, 251, 0.7); text-align: left; margin-top: 20px;">
                <strong>Report Rules:</strong>
                <ul style="margin-left: 20px; margin-top: 10px;">
                    <li>Passwords are strictly omitted from all reports.</li>
                    <li>Downloaded files are securely generated on the client side.</li>
                    <li>All actions are logged for auditing purposes.</li>
                </ul>
            </div>
        </div>
    </div>
    
    <jsp:include page="/footer.jsp" />
</body>
</html>
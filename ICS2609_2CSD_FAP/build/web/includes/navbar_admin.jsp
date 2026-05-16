<%-- 
    navbar_admin.jsp
    Reusable navigation bar for Admin pages.
    Usage: <%@ include file="/includes/navbar_admin.jsp" %>

    Displays: Dashboard | Users | Reports | MySQL | PostgreSQL | Logout | Welcome, Admin: <username>
    Based on Figure 3 Admin Dashboard wireframe from the SRS.
--%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    HttpSession navSession = request.getSession(false);
    String navUsername = (navSession != null) ? (String) navSession.getAttribute("username") : "Admin";
    String currentURI  = request.getRequestURI();
    String ctx         = request.getContextPath();
%>
<nav class="navbar">
    <a href="<%= ctx %>/admin/dashboard.jsp"
       class="<%= currentURI.contains("dashboard") ? "active" : "" %>">Dashboard</a>

    <a href="<%= ctx %>/admin/users.jsp"
       class="<%= currentURI.contains("users") ? "active" : "" %>">Users</a>

    <a href="<%= ctx %>/admin/reports.jsp"
       class="<%= currentURI.contains("reports") ? "active" : "" %>">Reports</a>

    <a href="<%= ctx %>/admin/mysql.jsp"
       class="<%= currentURI.contains("mysql") ? "active" : "" %>">MySQL</a>

    <a href="<%= ctx %>/admin/postgresql.jsp"
       class="<%= currentURI.contains("postgresql") ? "active" : "" %>">PostgreSQL</a>

    <a href="<%= ctx %>/LogoutServlet">Logout</a>

    <span class="nav-welcome">Welcome, Admin: <%= navUsername %></span>
</nav>

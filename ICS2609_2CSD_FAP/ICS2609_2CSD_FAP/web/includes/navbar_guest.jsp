<%--
    navbar_guest.jsp
    Reusable navigation bar for Guest (Student) pages.
    Usage: <%@ include file="/includes/navbar_guest.jsp" %>

    Displays: Home | My Profile | 2nd DBMS | 3rd DBMS | Logout | Welcome, Guest: <username>
    Based on Figure 4 Guest Dashboard wireframe from the SRS.
--%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    HttpSession gNavSession = request.getSession(false);
    String gNavUsername = (gNavSession != null) ? (String) gNavSession.getAttribute("username") : "Guest";
    String gCurrentURI  = request.getRequestURI();
    String gCtx         = request.getContextPath();
%>
<nav class="navbar">
    <a href="<%= gCtx %>/guest/dashboard.jsp"
       class="<%= gCurrentURI.contains("dashboard") ? "active" : "" %>">Home</a>

    <a href="<%= gCtx %>/guest/profile.jsp"
       class="<%= gCurrentURI.contains("profile") ? "active" : "" %>">My Profile</a>

    <a href="<%= gCtx %>/guest/courses.jsp"
       class="<%= gCurrentURI.contains("courses") ? "active" : "" %>">2nd DBMS</a>

    <a href="<%= gCtx %>/guest/logs.jsp"
       class="<%= gCurrentURI.contains("logs") ? "active" : "" %>">3rd DBMS</a>

    <a href="<%= gCtx %>/LogoutServlet">Logout</a>

    <span class="nav-welcome">Welcome, Guest: <%= gNavUsername %></span>
</nav>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Root page — redirect to login (or dashboard if already logged in)
    HttpSession s = request.getSession(false);
    if (s != null && s.getAttribute("username") != null) {
        String role = (String) s.getAttribute("role");
        if ("Admin".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/guest/dashboard.jsp");
        }
    } else {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
%>

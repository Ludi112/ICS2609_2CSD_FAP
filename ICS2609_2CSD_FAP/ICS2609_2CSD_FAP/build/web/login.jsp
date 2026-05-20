<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // If already logged in, redirect away from login page
    HttpSession existingSession = request.getSession(false);
    if (existingSession != null && existingSession.getAttribute("username") != null) {
        String role = (String) existingSession.getAttribute("role");
        if ("Admin".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/guest/dashboard.jsp");
        }
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login | Active Learning, Inc.</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style.css">
</head>
<body class="login-body">

    <header class="top-bar">
        <span class="brand">Active Learning, Inc.</span>
        <span class="page-label">Login Page</span>
    </header>

    <main class="login-wrapper">
        <div class="login-card">
            <h2 class="login-title">Login</h2>
            <p class="login-subtitle">Full-Stack Secure Web Application</p>

            <%-- Error message display --%>
            <%
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage != null && !errorMessage.isEmpty()) {
            %>
                <div class="error-message" role="alert">
                    <span>&#9888;</span> <%= errorMessage %>
                </div>
            <% } %>

            <form action="<%= request.getContextPath() %>/LoginServlet" method="post" novalidate>

                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text"
                           id="username"
                           name="username"
                           placeholder="Enter username"
                           value="<%= request.getParameter("username") != null ? request.getParameter("username") : "" %>"
                           autocomplete="username"
                           required>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password"
                           id="password"
                           name="password"
                           placeholder="Enter password"
                           autocomplete="current-password"
                           required>
                </div>

                <div class="form-group">
                    <label for="captcha">CAPTCHA</label>
                    <div class="captcha-box">
                        <img id="captchaImage"
                             src="<%= request.getContextPath() %>/CaptchaServlet"
                             alt="CAPTCHA Image"
                             title="CAPTCHA">
                        <a href="#"
                           class="refresh-link"
                           onclick="refreshCaptcha(); return false;">
                            &#8635; Refresh CAPTCHA
                        </a>
                    </div>
                    <input type="text"
                           id="captcha"
                           name="captcha"
                           placeholder="Enter CAPTCHA"
                           autocomplete="off"
                           required>
                </div>

                <button type="submit" class="btn-login">LOGIN</button>

            </form>
        </div>
    </main>

    <script>
        function refreshCaptcha() {
            var img = document.getElementById('captchaImage');
            img.src = '<%= request.getContextPath() %>/CaptchaServlet?ts=' + new Date().getTime();
            document.getElementById('captcha').value = '';
            document.getElementById('captcha').focus();
        }
    </script>
</body>
</html>

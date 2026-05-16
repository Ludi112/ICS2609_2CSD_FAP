package Servlets;

import dao.DerbyDAO;
import Model.DBConnectionManager;
import Model.User;
import util.LoggerUtil;

import java.io.IOException;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * LoginServlet
 * Handles POST from login.jsp.
 *
 * Responsibilities (Authentication & Security):
 *  1. Validate CAPTCHA against session value (FR-AUTH-008, FR-AUTH-009)
 *  2. Validate credentials against Apache Derby via DerbyDAO (FR-AUTH-001, FR-AUTH-002)
 *  3. Determine role — Admin or Student/Guest (FR-AUTH-004)
 *  4. Create session with username + role (FR-AUTH-006)
 *  5. Redirect Admin → /admin/dashboard.jsp, Guest → /guest/dashboard.jsp (FR-AUTH-004)
 *  6. Log login event to PostgreSQL via LoggerUtil
 *
 * Mapped to: /LoginServlet
 */
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // If already logged in, skip the login page
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            String role = (String) session.getAttribute("role");
            if ("Admin".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/guest/dashboard.jsp");
            }
            return;
        }
        // Show login page
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username     = request.getParameter("username");
        String password     = request.getParameter("password");
        String captchaInput = request.getParameter("captcha");

        // ── Step 1: CAPTCHA validation (FR-AUTH-008, FR-AUTH-009) ─────────────
        HttpSession session     = request.getSession(false);
        String      captchaAnswer = (session != null)
                ? (String) session.getAttribute("captchaAnswer")
                : null;

        boolean captchaOk = (captchaAnswer != null)
                && (captchaInput != null)
                && captchaAnswer.equalsIgnoreCase(captchaInput.trim());

        // Invalidate the CAPTCHA answer immediately (one-time use)
        if (session != null) {
            session.removeAttribute("captchaAnswer");
        }

        if (!captchaOk) {
            request.setAttribute("errorMessage", "Invalid username, password, or CAPTCHA.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // ── Step 2: Basic null / empty check ──────────────────────────────────
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Invalid username, password, or CAPTCHA.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // ── Step 3: Authenticate against Apache Derby (FR-AUTH-002) ──────────
        User user = null;
        Connection derbyConn = null;
        try {
            derbyConn = DBConnectionManager.getDerbyConnection(getServletContext());
            DerbyDAO derbyDAO = new DerbyDAO(derbyConn);
            user = derbyDAO.authenticateUser(username.trim(), password.trim());
        } catch (Exception e) {
            getServletContext().log("LoginServlet: Derby connection error", e);
            request.setAttribute("errorMessage", "A system error occurred. Please try again later.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        } finally {
            if (derbyConn != null) {
                try { derbyConn.close(); } catch (Exception ignored) {}
            }
        }
getServletContext().log("DEBUG: user result = " + (user == null ? "NULL" : user.getUsername()));  //TESTING LNGGGGGGGGGGGGG
        if (user == null) {
            // Invalid credentials
            request.setAttribute("errorMessage", "Invalid username, password, or CAPTCHA.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // ── Step 4: Session fixation protection — invalidate old session ──────
        if (session != null) {
            session.invalidate();
        }

        // ── Step 5: Create new session and store user info (FR-AUTH-006) ──────
        session = request.getSession(true);
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role",     user.getRole());

        // ── Step 6: Log login event to PostgreSQL (via LoggerUtil) ───────────
        try {
            LoggerUtil.log(getServletContext(), user.getUsername(), "LOGIN", "LoginServlet");
        } catch (Exception e) {
            // Log failure must never break login; just record to server log
            getServletContext().log("LoginServlet: Could not write audit log", e);
        }

        // ── Step 7: Redirect based on role (FR-AUTH-004) ──────────────────────
        if ("Admin".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        } else {
            // Role = "Student" → Guest dashboard
            response.sendRedirect(request.getContextPath() + "/guest/dashboard.jsp");
        }
    }
}

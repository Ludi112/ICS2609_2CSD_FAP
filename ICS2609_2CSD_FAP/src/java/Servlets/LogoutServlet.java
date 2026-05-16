package Servlets;

import util.LoggerUtil;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * LogoutServlet
 * Properly ends the user session and prevents browser back-button access.
 *
 * Responsibilities (Authentication & Security):
 *  - Logout functionality (properly invalidate the session) (FR-AUTH-007)
 *  - Prevent users from going back to protected pages after logout (FR-AUTH-005, FR-AUTH-007)
 *  - Log logout event to PostgreSQL via LoggerUtil
 *
 * Mapped to: /LogoutServlet
 */
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleLogout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleLogout(request, response);
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            String username = (String) session.getAttribute("username");

            // ── Log logout event to PostgreSQL ────────────────────────────────
            if (username != null) {
                try {
                    LoggerUtil.log(getServletContext(), username, "LOGOUT", "LogoutServlet");
                } catch (Exception e) {
                    getServletContext().log("LogoutServlet: Could not write audit log", e);
                }
            }

            // ── Invalidate session (FR-AUTH-007) ──────────────────────────────
            session.invalidate();
        }

        // ── Prevent browser from caching protected pages (FR-AUTH-005, FR-AUTH-007) ──
        // User cannot press Back to return to protected pages after logout
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}

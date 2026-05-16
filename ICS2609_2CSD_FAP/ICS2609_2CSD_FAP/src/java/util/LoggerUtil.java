package util;

import Model.DBConnectionManager;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * LoggerUtil
 * Shared utility for writing audit entries to:
 *   1. PostgreSQL — System_Audit_Logs table (for the 3rd DBMS requirement)
 *   2. A .log file inside the application/WEB-INF/logs/ folder
 *      (SRS: "Tracing logs must be stored within the application folder, not on the server")
 *
 * Called from LoginServlet, LogoutServlet, and any servlet that needs auditing.
 * Usage:
 *   LoggerUtil.log(getServletContext(), username, "LOGIN", "LoginServlet");
 */
public final class LoggerUtil {

    private static final Logger LOGGER    = Logger.getLogger(LoggerUtil.class.getName());
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String LOG_DIR   = "logs";
    private static final String LOG_FILE  = "lms_audit.log";

    private LoggerUtil() { /* utility class */ }

    /**
     * Writes one audit entry to PostgreSQL and to the local log file.
     *
     * @param ctx        ServletContext — used for DB connection and real path resolution
     * @param username   The user performing the action
     * @param actionType e.g. "LOGIN", "LOGOUT", "GENERATE_REPORT"
     * @param module     e.g. "LoginServlet", "ReportServlet"
     */
    public static void log(ServletContext ctx,
                           String username,
                           String actionType,
                           String module) {

        String timestamp = LocalDateTime.now().format(FORMATTER);

        // ── 1. Write to PostgreSQL ──────────────────────────────────────────
        logToPostgres(ctx, username, actionType, module);

        // ── 2. Write to local .log file inside WEB-INF/logs/ ───────────────
        logToFile(ctx, username, actionType, module, timestamp);
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    private static void logToPostgres(ServletContext ctx,
                                       String username,
                                       String actionType,
                                       String module) {
        final String SQL = "INSERT INTO System_Audit_Logs "
                         + "(Username, Action_Type, Description) VALUES (?, ?, ?)";
        String description = actionType + " performed via " + module;

        Connection pgConn = null;
        try {
            pgConn = DBConnectionManager.getPostgresConnection(ctx);
            try (PreparedStatement ps = pgConn.prepareStatement(SQL)) {
                ps.setString(1, username != null ? username : "unknown");
                ps.setString(2, actionType);
                ps.setString(3, description);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            // Audit failure must never crash the application
            LOGGER.log(Level.WARNING, "LoggerUtil: Could not write to PostgreSQL", e);
        } finally {
            if (pgConn != null) {
                try { pgConn.close(); } catch (Exception ignored) {}
            }
        }
    }

    private static void logToFile(ServletContext ctx,
                                   String username,
                                   String actionType,
                                   String module,
                                   String timestamp) {
        try {
            // Resolve path to WEB-INF/logs/ inside the deployed app folder
            String webInfPath = ctx.getRealPath("/WEB-INF");
            if (webInfPath == null) {
                LOGGER.warning("LoggerUtil: getRealPath returned null — skipping file log");
                return;
            }
            String logDirPath  = webInfPath + java.io.File.separator + LOG_DIR;
            String logFilePath = logDirPath + java.io.File.separator + LOG_FILE;

            // Create the logs directory if it does not exist
            Files.createDirectories(Paths.get(logDirPath));

            // Append the log entry
            String entry = String.format("[%s] User=%-40s Action=%-20s Module=%s%n",
                    timestamp, username, actionType, module);

            try (PrintWriter pw = new PrintWriter(new FileWriter(logFilePath, true))) {
                pw.print(entry);
            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "LoggerUtil: Could not write to log file", e);
        }
    }
}

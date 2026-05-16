package Model;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * DBConnectionManager
 * Reads all database credentials from the Deployment Descriptor (web.xml).
 * Credentials are NEVER hard-coded here (FR-DBMS-006 / NFR-SEC-003).
 *
 * Usage:
 *   Connection conn = DBConnectionManager.getDerbyConnection(getServletContext());
 */
public class DBConnectionManager 
{

    private DBConnectionManager()
    {
        /* utility class — no instances */ 
    }

    /**
     * DBMS 1 — Apache Derby (Embedded)
     * Used for: Authentication — Users table
     * web.xml params: derby.driver, derby.url, derby.username, derby.password
     */
    public static Connection getDerbyConnection(ServletContext ctx) throws Exception 
    {
        Class.forName(ctx.getInitParameter("derby.driver"));
        String url = ctx.getInitParameter("derby.url");
        String username = ctx.getInitParameter("derby.username");
        String password = ctx.getInitParameter("derby.password");
        // Derby embedded uses url-only when username/password are null
        if (username == null || username.trim().isEmpty()) 
        {
            return DriverManager.getConnection(url);
        }
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * DBMS 2 — MySQL
     * Used for: Business data — Courses, Enrollments
     * web.xml params: mysql.driver, mysql.url, mysql.username, mysql.password
     */
    public static Connection getMySQLConnection(ServletContext ctx) throws Exception 
    {
        Class.forName(ctx.getInitParameter("mysql.driver"));
        return DriverManager.getConnection(ctx.getInitParameter("mysql.url"),ctx.getInitParameter("mysql.username"),ctx.getInitParameter("mysql.password"));
    }

    /**
     * DBMS 3 — PostgreSQL
     * Used for: Audit logs, Report history
     * web.xml params: pg.driver, pg.url, pg.username, pg.password
     */
    public static Connection getPostgresConnection(ServletContext ctx) throws Exception 
    {
        Class.forName(ctx.getInitParameter("pg.driver"));
        return DriverManager.getConnection(ctx.getInitParameter("pg.url"),ctx.getInitParameter("pg.username"),ctx.getInitParameter("pg.password"));
    }
}

package Servlets;

import Utils.PDFGeneratorUtil;
import dao.DerbyDAO;
import dao.MySQLDAO;
import dao.PostgreSQLDAO;
import Model.DBConnectionManager;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ReportServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");
        String reportType = request.getParameter("reportType");
        
        // Access Control: Block Guests from Admin-only reports
        if ("Guest".equalsIgnoreCase(role) && !"mysql_records".equals(reportType)) {
            response.sendRedirect(request.getContextPath() + "/errors/error403.jsp");
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String filename = reportType.toUpperCase() + "_" + timestamp + ".pdf";

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        String pdfHeader = getServletContext().getInitParameter("pdf.header");
        String pdfFooter = getServletContext().getInitParameter("pdf.footer");

        String reportTitle = "";
        String[] columns = null;
        List<String[]> data = new ArrayList<>();

        Connection derbyConn = null;
        Connection mysqlConn = null;
        Connection pgConn = null;

        try {
            if ("all_records".equals(reportType)) {
                derbyConn = DBConnectionManager.getDerbyConnection(getServletContext());
                DerbyDAO derbyDAO = new DerbyDAO(derbyConn);
                reportTitle = "All Users Record";
                columns = new String[]{"#", "Username", "Role", "Created Date"};
                data = derbyDAO.getAllUsersForReport(username);
                
            } else if ("admin_records".equals(reportType)) {
                derbyConn = DBConnectionManager.getDerbyConnection(getServletContext());
                DerbyDAO derbyDAO = new DerbyDAO(derbyConn);
                reportTitle = "My Admin Records";
                columns = new String[]{"#", "Username", "Role", "Created Date"};
                data = derbyDAO.getAdminRecordsForReport(username);
                
            } else if ("time_bound".equals(reportType)) {
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");
                
                if (startDate.compareTo(endDate) > 0) {
                    response.sendRedirect(request.getContextPath() + "/admin/reports.jsp?error=date_invalid");
                    return;
                }
                mysqlConn = DBConnectionManager.getMySQLConnection(getServletContext());
                MySQLDAO mysqlDAO = new MySQLDAO(mysqlConn);
                reportTitle = "Time-Bound Enrollments (" + startDate + " to " + endDate + ")";
                columns = new String[]{"Enrollment ID", "Student", "Course", "Date", "Status"};
                data = mysqlDAO.getEnrollmentsByDateForReport(startDate, endDate);
                
            } else if ("mysql_records".equals(reportType)) {
                mysqlConn = DBConnectionManager.getMySQLConnection(getServletContext());
                MySQLDAO mysqlDAO = new MySQLDAO(mysqlConn);
                reportTitle = "Complete Course Catalog";
                columns = new String[]{"Course ID", "Code", "Title", "Credits", "Instructor"};
                data = mysqlDAO.getAllCoursesForReport();
            }

            // Generate the PDF
            PDFGeneratorUtil.generateReport(response.getOutputStream(), reportTitle, username, pdfHeader, pdfFooter, columns, data);
            
            // Log the generation event to PostgreSQL
            pgConn = DBConnectionManager.getPostgresConnection(getServletContext());
            PostgreSQLDAO pgDAO = new PostgreSQLDAO(pgConn);
            pgDAO.logReportGeneration(username, reportType, filename, data.size());
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/errors/error500.jsp");
        } finally {
            // Clean up connections
            if (derbyConn != null) try { derbyConn.close(); } catch (Exception ignored) {}
            if (mysqlConn != null) try { mysqlConn.close(); } catch (Exception ignored) {}
            if (pgConn != null) try { pgConn.close(); } catch (Exception ignored) {}
        }
    }
}
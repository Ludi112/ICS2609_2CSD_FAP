
package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import Model.ReportHistory;

public class PostgreSQLDAO 
{
    
    private static final Logger logger = Logger.getLogger(PostgreSQLDAO.class.getName());
    private Connection conn;

    public PostgreSQLDAO(Connection conn) 
    {
        this.conn = conn;
    }

    public boolean insertAuditLog(String username, String activity, String module)
    {
        String sql = "INSERT INTO System_Audit_Logs (Username, Action_Type, Description) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, username);
            ps.setString(2, activity);
            ps.setString(3, activity + " performed via " + module);
            return ps.executeUpdate() > 0;
        } 
        catch (SQLException e) 
        {
            logger.log(Level.SEVERE, "Database error while inserting audit log", e);
            return false;
        }
    }

    public boolean insertReportHistory(String generatedBy, String reportType, String filePath) 
    {
        String sql = "INSERT INTO Report_History (Generated_By, Report_Type, File_Path) VALUES (?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, generatedBy);
            ps.setString(2, reportType);
            ps.setString(3, filePath);
            
            return ps.executeUpdate() > 0;
        } 
        catch (SQLException e) 
        {
            logger.log(Level.SEVERE, "Database error while inserting report history", e);
            return false;
        }
    }

    public List<ReportHistory> getReportHistory() 
    {
        List<ReportHistory> reportList = new ArrayList<>();
        String sql = "SELECT REPORT_ID, USERNAME, REPORT_TYPE, GENERATED_AT FROM REPORT_HISTORY ORDER BY GENERATED_AT DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) 
        {
            while (rs.next())
            {
                ReportHistory report = new ReportHistory(rs.getInt("REPORT_ID"),rs.getString("USERNAME"),rs.getString("REPORT_TYPE"),rs.getTimestamp("GENERATED_AT"), "" ); // no file path column
                reportList.add(report);
            }
        } 
        catch (SQLException e) 
        {
            logger.log(Level.SEVERE, "Database error while retrieving report history", e);
        }
        return reportList;
    }
    
    /**
     * Logs a report generation event to the report_logs table.
     */
    public boolean logReportGeneration(String username, String reportType, String filename, int recordCount) {
        String sql = "INSERT INTO report_logs (username, report_type, filename, record_count) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, reportType);
            ps.setString(3, filename);
            ps.setInt(4, recordCount);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "PostgreSQL: error logging report generation", e);
            return false;
        }
    }
}



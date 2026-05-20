package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class MySQLDAO {
    
    private static final Logger logger = Logger.getLogger(MySQLDAO.class.getName());
    private Connection conn;

    public MySQLDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean enrollStudent(int courseId, String studentUsername) {
        String sql = "INSERT INTO Enrollments (Course_ID, Student_Username, Enrollment_Date, Status) VALUES (?, ?, CURRENT_DATE, 'ACTIVE')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setString(2, studentUsername);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during student enrollment", e);
            return false;
        }
    }

    // ── PDF Report Methods (Added for PDF Generation) ──

    /**
     * Retrieves enrollments within a specific date range for the Time-Bound Report.
     */
    public List<String[]> getEnrollmentsByDateForReport(String startDate, String endDate) {
        List<String[]> data = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.Username, c.course_name, e.enrollment_date, e.grade " +
                     "FROM enrollments e " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "WHERE e.enrollment_date >= ? AND e.enrollment_date <= ? " +
                     "ORDER BY e.enrollment_date ASC";
                     
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Append times to ensure full day coverage
            ps.setString(1, startDate + " 00:00:00");
            ps.setString(2, endDate + " 23:59:59");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String grade = rs.getString("grade");
                    data.add(new String[]{
                        String.valueOf(rs.getInt("enrollment_id")),
                        rs.getString("Username"),
                        rs.getString("course_name"),
                        rs.getTimestamp("enrollment_date").toString(),
                        grade != null ? grade : "N/A"
                    });
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "MySQL: getEnrollmentsByDateForReport error", e);
        }
        return data;
    }

    /**
     * Retrieves complete course records for the Additional DBMS Report.
     */
    public List<String[]> getAllCoursesForReport() {
        List<String[]> data = new ArrayList<>();
        String sql = "SELECT course_id, course_code, course_name, credits, instructor_id FROM courses ORDER BY course_id ASC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                data.add(new String[]{
                    String.valueOf(rs.getInt("course_id")),
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    String.valueOf(rs.getInt("credits")),
                    String.valueOf(rs.getInt("instructor_id"))
                });
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "MySQL: getAllCoursesForReport error", e);
        }
        return data;
    }
}
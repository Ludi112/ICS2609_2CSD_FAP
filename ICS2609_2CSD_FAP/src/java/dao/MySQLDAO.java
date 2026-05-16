
package dao;

import java.sql.*;
import java.util.logging.*;

public class MySQLDAO 
{
    
    private static final Logger logger = Logger.getLogger(MySQLDAO.class.getName());
    private Connection conn;

    public MySQLDAO(Connection conn)
    {
        this.conn = conn;
    }

    public boolean enrollStudent(int courseId, String studentUsername) 
    {
        String sql = "INSERT INTO Enrollments (Course_ID, Student_Username, Enrollment_Date, Status) VALUES (?, ?, CURRENT_DATE, 'ACTIVE')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setInt(1, courseId);
            ps.setString(2, studentUsername);
            return ps.executeUpdate() > 0;
        } 
        catch (SQLException e)
        {
            logger.log(Level.SEVERE, "Database error during student enrollment", e);
            return false;
        }
    }
}




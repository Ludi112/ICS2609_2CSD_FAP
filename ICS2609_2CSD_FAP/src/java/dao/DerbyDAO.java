package dao;

import Model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DerbyDAO
 * Data Access Object for Apache Derby — lms_auth_db.
 * Handles all CRUD operations on the Users table.
 *
 * Derby column names (from the SQL seed script):
 *   Username | Password | Role | Created_Date
 *
 * NOTE: Derby does NOT support multi-row INSERT; each row is inserted separately.
 */
public class DerbyDAO 
{

    private static final Logger LOGGER = Logger.getLogger(DerbyDAO.class.getName());
    private final Connection conn;

    public DerbyDAO(Connection conn) 
    {
        this.conn = conn;
    }

    // ── Authentication 

    /**
     * Authenticates a user by username and password.
     * Returns the matching User object, or null if credentials are invalid.
     * (FR-AUTH-001, FR-AUTH-002)
     */
    public User authenticateUser(String username, String password)
    {
        final String SQL = "SELECT Username, Password, Role, Created_Date "
                         + "FROM Users "
                         + "WHERE TRIM(Username) = ? AND TRIM(Password) = ?";

        try (PreparedStatement ps = conn.prepareStatement(SQL)) 
        {
            ps.setString(1, username.trim());
            ps.setString(2, password.trim());

            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next()) 
                {
                    User user = new User(rs.getString("Username").trim(),rs.getString("Password").trim(),rs.getString("Role").trim(),rs.getTimestamp("Created_Date"));
                    LOGGER.log(Level.INFO, "Auth success: {0}", username);
                    return user;
                }
            }
        } 
        catch (SQLException e) 
        {
            LOGGER.log(Level.SEVERE, "Derby: authenticateUser error", e);
        }
        LOGGER.log(Level.WARNING, "Auth failure for: {0}", username);
        return null;
    }

    // ── User Lookup

    /**
     * Retrieves a single user by username. Returns null if not found.
     */
    public User getUserByUsername(String username) 
    {
        final String SQL = "SELECT Username, Password, Role, Created_Date "
                         + "FROM Users WHERE TRIM(Username) = ?";

        try (PreparedStatement ps = conn.prepareStatement(SQL)) 
        {
            ps.setString(1, username != null ? username.trim() : "");

            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    return mapRow(rs);
                }
            }
        }
        catch (SQLException e) 
        {
            LOGGER.log(Level.SEVERE, "Derby: getUserByUsername error", e);
        }
        return null;
    }

    /**
     * Returns all users ordered by Role then Username.
     * Used for the All Records Report (FR-REP-001, FR-REP-005).
     */
    public List<User> getAllUsers() 
    {
        List<User> list = new ArrayList<>();
        final String SQL = "SELECT Username, Password, Role, Created_Date "
                         + "FROM Users ORDER BY Role ASC, Username ASC";

        try (PreparedStatement ps = conn.prepareStatement(SQL);ResultSet rs = ps.executeQuery()) 
        {
            while (rs.next()) 
            {
                list.add(mapRow(rs));
            }
        } 
        catch (SQLException e) 
        {
            LOGGER.log(Level.SEVERE, "Derby: getAllUsers error", e);
        }
        return list;
    }

    /**
     * Returns the total number of users. 
     */
    public int getTotalUserCount()
    {
        final String SQL = "SELECT COUNT(*) FROM Users";
        try (PreparedStatement ps = conn.prepareStatement(SQL);ResultSet rs = ps.executeQuery())
        {
            if (rs.next()) return rs.getInt(1);
        } 
        catch (SQLException e) 
        {
            LOGGER.log(Level.SEVERE, "Derby: getTotalUserCount error", e);
        }
        return 0;
    }

    /**
     * Returns users created within a date range (for time-bound report).
     * (FR-REP-003)
     */
    public List<User> getUsersByDateRange(Timestamp start, Timestamp end) 
    {
        List<User> list = new ArrayList<>();
        final String SQL = "SELECT Username, Password, Role, Created_Date "
                         + "FROM Users "
                         + "WHERE Created_Date >= ? AND Created_Date <= ? "
                         + "ORDER BY Created_Date ASC";

        try (PreparedStatement ps = conn.prepareStatement(SQL)) 
        {
            ps.setTimestamp(1, start);
            ps.setTimestamp(2, end);

            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next())
                {
                    list.add(mapRow(rs));
                }
            }
        } 
        catch (SQLException e)
        {
            LOGGER.log(Level.SEVERE, "Derby: getUsersByDateRange error", e);
        }
        return list;
    }

    // ── CRUD 

    /**
     * Inserts a new user. Returns true on success.
     */
    public boolean addUser(String username, String password, String role)
    {
        if (!isValidInput(username, password, role))
        {
            return false;
        }

        final String SQL = "INSERT INTO Users (Username, Password, Role) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(SQL)) 
        {
            ps.setString(1, username.trim());
            ps.setString(2, password.trim());
            ps.setString(3, role.trim());
            return ps.executeUpdate() > 0;
        } 
        catch (SQLException e) 
        {
            LOGGER.log(Level.SEVERE, "Derby: addUser error", e);
            return false;
        }
    }

    /**
     * Updates an existing user's password and role. Returns true on success.
     */
    public boolean updateUser(String username, String password, String role) 
    {
        if (!isValidInput(username, password, role)) 
        {
            return false;
        }

        final String SQL = "UPDATE Users SET Password = ?, Role = ? WHERE TRIM(Username) = ?";
        try (PreparedStatement ps = conn.prepareStatement(SQL))
        {
            ps.setString(1, password.trim());
            ps.setString(2, role.trim());
            ps.setString(3, username.trim());
            return ps.executeUpdate() > 0;
        } 
        catch (SQLException e)
        {
            LOGGER.log(Level.SEVERE, "Derby: updateUser error", e);
            return false;
        }
    }

    /**
     * Deletes a user by username. Returns true on success.
     */
    public boolean deleteUser(String username) 
    {
        final String SQL = "DELETE FROM Users WHERE TRIM(Username) = ?";
        try (PreparedStatement ps = conn.prepareStatement(SQL)) 
        {
            ps.setString(1, username != null ? username.trim() : "");
            return ps.executeUpdate() > 0;
        } 
        catch (SQLException e) 
        {
            LOGGER.log(Level.SEVERE, "Derby: deleteUser error", e);
            return false;
        }
    }

    /**
     * Bulk deletes users by username array. Returns true if at least one row deleted.
     */
    public boolean bulkDeleteUsers(String[] usernames)
    {
        if (usernames == null || usernames.length == 0) 
        {
            return false;
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < usernames.length; i++) 
        {
            placeholders.append(i == 0 ? "?" : ",?");
        }

        final String SQL = "DELETE FROM Users WHERE TRIM(Username) IN (" + placeholders + ")";
        try (PreparedStatement ps = conn.prepareStatement(SQL))
        {
            for (int i = 0; i < usernames.length; i++) 
            {
                ps.setString(i + 1, usernames[i].trim());
            }
            return ps.executeUpdate() > 0;
        } 
        catch (SQLException e) 
        {
            LOGGER.log(Level.SEVERE, "Derby: bulkDeleteUsers error", e);
            return false;
        }
    }

    // ── Helpers 
    
    /**
     * Maps a ResultSet row to a User object.
     */
    private User mapRow(ResultSet rs) throws SQLException 
    {
        String uname = rs.getString("Username");
        String pass = rs.getString("Password");
        String role = rs.getString("Role");
        Timestamp created = rs.getTimestamp("Created_Date");

        return new User(
            uname != null ? uname.trim() : "",
            pass != null ? pass.trim() : "",
            role != null ? role.trim() : "",
            created
        );
    }

    /**
     * Basic validation: all fields must be non-null and non-empty;
     * role must be Admin or Student.
     */
    private boolean isValidInput(String username, String password, String role) 
    {
        if (username == null || username.trim().isEmpty()) 
        {
            return false;
        }
        if (password == null || password.trim().isEmpty()) 
        {
            return false;
        }
        if (role == null || role.trim().isEmpty())   
        {
            return false;
        }
        String r = role.trim();
        if (!r.equalsIgnoreCase("Admin") && !r.equalsIgnoreCase("Student")) 
        {
            LOGGER.log(Level.WARNING, "DerbyDAO: invalid role ''{0}''", r);
            return false;
        }
        return true;
    }
}

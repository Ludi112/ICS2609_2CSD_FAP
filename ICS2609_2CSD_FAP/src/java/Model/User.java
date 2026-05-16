package Model;

import java.sql.Timestamp;

/**
 * User — maps to the Derby Users table.
 * Role is either "Admin" (teacher/instructor) or "Student" (learner/guest).
 *
 * IMPORTANT: Password must NEVER be printed in any generated PDF report (FR-REP-004).
 */
public class User {

    private String    username;
    private String    password;    // Never exposed in reports
    private String    role;        // "Admin" or "Student"
    private Timestamp createdDate;

    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role     = role;
    }

    public User(String username, String password, String role, Timestamp createdDate) {
        this.username    = username;
        this.password    = password;
        this.role        = role;
        this.createdDate = createdDate;
    }

    public String    getUsername()                  { return username; }
    public void      setUsername(String u)          { this.username = u; }

    public String    getPassword()                  { return password; }
    public void      setPassword(String p)          { this.password = p; }

    public String    getRole()                      { return role; }
    public void      setRole(String r)              { this.role = r; }

    public Timestamp getCreatedDate()               { return createdDate; }
    public void      setCreatedDate(Timestamp t)    { this.createdDate = t; }

    @Override
    public String toString() {
        return "User{username='" + username + "', role='" + role + "'}";
    }
}

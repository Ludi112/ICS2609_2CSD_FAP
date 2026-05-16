package listener;

import Model.DBConnectionManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        ctx.log("AppStartupListener: Initializing Derby database...");

        Connection conn = null;
        try {
            conn = DBConnectionManager.getDerbyConnection(ctx);
            createUsersTable(conn);
            seedUsers(conn);
            ctx.log("AppStartupListener: Derby initialized successfully.");
        } catch (Exception e) {
            ctx.log("AppStartupListener: Derby init FAILED", e);
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    private void createUsersTable(Connection conn) throws SQLException {
        String createSQL =
            "CREATE TABLE Users (" +
            "    Username      VARCHAR(100) PRIMARY KEY, " +
            "    Password      VARCHAR(100) NOT NULL, " +
            "    Role          VARCHAR(20)  NOT NULL, " +
            "    Created_Date  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP" +
            ")";

        try (Statement st = conn.createStatement()) {
            st.executeUpdate(createSQL);
        } catch (SQLException e) {
            // X0Y32 = table already exists — safe to ignore
            if ("X0Y32".equals(e.getSQLState())) {
                // Table already exists, skip
            } else {
                throw e;
            }
        }
    }
        
        private void seedUsers(Connection conn) throws SQLException {
    String[] inserts = {
        "INSERT INTO Users (Username, Password, Role) VALUES ('chloebernardo@admin.gmail.com', 'chloekirsten123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('ludwigcalayo@admin.gmail.com', 'johnludwig123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('miguelfineza@admin.gmail.com', 'juanmiguel123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('dansantos@admin.gmail.com', 'danbenedict123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('isaactilos@admin.gmail.com', 'isaacclarenz123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('marklee@admin.gmail.com', 'mark123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('leehaechan@admin.gmail.com', 'haechan123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('jeonghanyoon@admin.gmail.com', 'jeonghan123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('hoshikwon@admin.gmail.com', 'hoshi123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('mingyukim@admin.gmail.com', 'mingyu123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('minhaoxu@admin.gmail.com', 'the8123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('seungkwanboo@admin.gmail.com', 'kwannie123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('joshuahong@admin.gmail.com', 'joshua123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('jisungpark@admin.gmail.com', 'jisung123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('rikumaeda@admin.gmail.com', 'riku123', 'Admin')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('cedrickeusebio.student@gmail.com', 'cedrick123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('kirstenromero.student@gmail.com', 'kirsten123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('arjiannaelento.student@gmail.com', 'arji123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('mattheusymballa.student@gmail.com', 'mattheus123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('gianty.student@gmail.com', 'giancarlo123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('mickaalegria.student@gmail.com', 'micka123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('alessandraalcantara.student@gmail.com', 'alex123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('aldrinsylato.student@gmail.com', 'aldrin123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('cyprusmejia.student@gmail.com', 'cyprus123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('jesmaelacanilao.student@gmail.com', 'jesmae123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('cassandraramos.student@gmail.com', 'cass123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('cathlyndeborja.student@gmail.com', 'cath123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('citadelney.student@gmail.com', 'citadel123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('clangcuento.student@gmail.com', 'clang123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('henriannemagsino.student@gmail.com', 'anne123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('iangarcia.student@gmail.com', 'ian123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('ivanroque.student@gmail.com', 'ivan123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('jallainemendoza.student@gmail.com', 'jall123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('kimberlyespinoza.student@gmail.com', 'kim123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('loraineempleo.student@gmail.com', 'lori123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('rizellesantos.student@gmail.com', 'rizelle123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('glyzamargen.student@gmail.com', 'gly123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('amherstiadeguzman.student@gmail.com', 'mer123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('taeyonglee.student@gmail.com', 'tyong123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('johnnyseo.student@gmail.com', 'johnny123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('yutanakamoto.student@gmail.com', 'yuta123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('kunqian.student@gmail.com', 'kun123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('kimdoyoung.student@gmail.com', 'doie123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('tenlee.student@gmail.com', 'ten123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('jaehyunjeong.student@gmail.com', 'jaehyun123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('winwindong.student@gmail.com', 'winwin123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('jungwookim.student@gmail.com', 'jungwoo123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('seuncheolchoi.student@gmail.com', 'scoups123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('vernonhansol.student@gmail.com', 'vernon123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('wonwoojeon.student@gmail.com', 'wonwoo123', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student36.lms@gmail.com', 'pass36', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student37.lms@gmail.com', 'pass37', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student38.lms@gmail.com', 'pass38', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student39.lms@gmail.com', 'pass39', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student40.lms@gmail.com', 'pass40', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student41.lms@gmail.com', 'pass41', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student42.lms@gmail.com', 'pass42', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student43.lms@gmail.com', 'pass43', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student44.lms@gmail.com', 'pass44', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student45.lms@gmail.com', 'pass45', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student46.lms@gmail.com', 'pass46', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student47.lms@gmail.com', 'pass47', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student48.lms@gmail.com', 'pass48', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student49.lms@gmail.com', 'pass49', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student50.lms@gmail.com', 'pass50', 'Student')",
        "INSERT INTO Users (Username, Password, Role) VALUES ('student51.lms@gmail.com', 'pass51', 'Student')"
    };


        for (String sql : inserts) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(sql);
            } catch (SQLException e) {
                // 23505 = duplicate key — user already seeded, skip
                if (!"23505".equals(e.getSQLState())) {
                    throw e;
                }
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}   
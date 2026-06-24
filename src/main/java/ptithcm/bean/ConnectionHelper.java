package ptithcm.bean;

import javax.servlet.http.HttpSession;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

/**
 * Helper tạo JdbcTemplate dựa trên SQL Server Login
 * lưu trong session, thực thi phân quyền SQL Server.
 *
 * Hỗ trợ cấu hình qua biến môi trường (cho Docker):
 *   DB_HOST, DB_NAME, DB_USER, DB_PASSWORD
 * Nếu không có biến môi trường, dùng mặc định localhost\SQLEXPRESS.
 */
@Component
public class ConnectionHelper {
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    /**
     * Lấy JDBC URL từ biến môi trường hoặc dùng mặc định.
     */
    private static String getDbUrl() {
        String host = System.getenv("DB_HOST");
        String dbName = System.getenv("DB_NAME");
        if (host != null && !host.isEmpty()) {
            // Docker mode: kết nối qua hostname (không cần instance name)
            if (dbName == null || dbName.isEmpty()) dbName = "QLDSV_HTC";
            return "jdbc:sqlserver://" + host + ";databaseName=" + dbName
                    + ";encrypt=false;trustServerCertificate=true";
        }
        // Local mode: kết nối localhost\SQLEXPRESS
        return "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=QLDSV_HTC;encrypt=false;trustServerCertificate=true";
    }

    private static String getDefaultUser() {
        String user = System.getenv("DB_USER");
        if (user == null || user.isEmpty() || "sa".equalsIgnoreCase(user.trim())) {
            return "sv";
        }
        return user;
    }

    private static String getDefaultPassword() {
        String pass = System.getenv("DB_PASSWORD");
        String user = System.getenv("DB_USER");
        if (user == null || user.isEmpty() || "sa".equalsIgnoreCase(user.trim())) {
            return "sv123";
        }
        return (pass != null && !pass.isEmpty()) ? pass : "sv123";
    }

    /**
     * Tạo JdbcTemplate với SQL Server login tương ứng nhóm quyền user.
     * - PGV  -> login pgv_admin
     * - KHOA -> login khoa_cntt / khoa_vt
     * - SV   -> login sv
     * Nếu chưa login, dùng sa (cho trang login).
     */
    public JdbcTemplate getJdbcTemplate(HttpSession session) {
        String login = (String) session.getAttribute("sqlLogin");
        String password = (String) session.getAttribute("sqlPassword");
        if (login == null) {
            login = getDefaultUser();
            password = getDefaultPassword();
        }
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(DRIVER);
        ds.setUrl(getDbUrl());
        ds.setUsername(login);
        ds.setPassword(password);
        return new JdbcTemplate(ds);
    }

    /**
     * Tạo JdbcTemplate mặc định (sa) cho xác thực ban đầu.
     */
    public JdbcTemplate getDefaultJdbcTemplate() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(DRIVER);
        ds.setUrl(getDbUrl());
        ds.setUsername(getDefaultUser());
        ds.setPassword(getDefaultPassword());
        return new JdbcTemplate(ds);
    }
}

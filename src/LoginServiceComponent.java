import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginServiceComponent {
 
    // 資料庫連線資訊
    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_job_portal_db";
    private static final String DB_USER = "your_db_user";
    private static final String DB_PASSWORD = "your_db_password";

    // 從外部請求中獲取輸入
    // 回傳從 Header 讀取到的使用者名稱字串
    public String getUsernameFromRequestSource(String dataSourceUrl, String headerName) {
        HttpURLConnection httpConn = null;
        String username = null;
        try {
            URL url = new URL(dataSourceUrl);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("HEAD");
            httpConn.setConnectTimeout(1000);
            httpConn.setReadTimeout(1000);

            // 外部輸入 API返回值
            username = httpConn.getHeaderField(headerName);

            if (username == null) {
                username = "guest_user";
            }
        } catch (IOException e) {
            username = "error_user";
        } finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
        return username;
    }

    //驗證使用者登入
    //登入憑證有效則回傳 true，否則回傳 false
    public boolean authenticateUser(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return false;
        }
        // SQL Injection
        String sqlQuery = "SELECT id, username, role FROM users WHERE username = '" + username + "' AND password = 'fixed_password_for_demo'";
        //注意：'fixed_password_for_demo' 僅為簡化範例，真實系統密碼永遠不應硬編碼，且應使用雜湊比對。

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlQuery)) {

            if (rs.next()) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        LoginServiceComponent loginService = new LoginServiceComponent();

        String taintedUsername = loginService.getUsernameFromRequestSource(
                "http://sast-dummy-source.com/userinput",
                "X-Login-Username"
        );

        boolean loginSuccess = loginService.authenticateUser(taintedUsername, "some_password_from_form");

        if (loginSuccess) {
            System.out.println("Main驅動：登入嘗試結果 - 成功 (基於元件回傳)");
        } else {
            System.out.println("Main驅動：登入嘗試結果 - 失敗 (基於元件回傳)");
        }
    }
}
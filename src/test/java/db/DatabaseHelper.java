package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {

    /**
     * Xóa dữ liệu trong bảng (có hoặc không có điều kiện WHERE)
     */
    public static void clearTable(String tableName, String whereClause) {
        String sql = (whereClause == null || whereClause.trim().isEmpty())
                ? "TRUNCATE TABLE " + tableName
                : "DELETE FROM " + tableName + " WHERE " + whereClause;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("Đã xóa dữ liệu trong bảng: " + tableName);

        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa dữ liệu trong bảng " + tableName + ": " + e.getMessage());
        }
    }

    /**
     * Xóa toàn bộ dữ liệu liên quan đến 1 user phục vụ test E2E
     */
    public static void clearTestDataForUser(String userId) {
        String[] deleteQueries = {
                String.format("DELETE FROM check_days WHERE user_id = '%s';", userId),
                String.format("DELETE FROM check_times WHERE created_by = '%s';", userId),
                String.format("DELETE FROM check_day_histories WHERE created_by = '%s';", userId),
                "DELETE FROM check_time_histories WHERE created_at >= NOW() - INTERVAL 2 MINUTE;",
                "DELETE FROM payrolls;",
                "DELETE FROM payroll_histories WHERE created_at >= NOW() - INTERVAL 2 MINUTE;",
                String.format("DELETE FROM time_workings WHERE user_id = '%s';", userId),
                String.format("DELETE FROM overtime_submissions WHERE user_id = '%s';", userId),
                String.format("DELETE FROM overtime_submission_histories WHERE user_id = '%s';", userId),
                "DELETE FROM shift_plans WHERE NAME = 'Test API Shift Plan';",
                "DELETE FROM holiday_managements;",
                String.format("DELETE FROM debts WHERE user_id = '%s';", userId),
                String.format("DELETE FROM payslips WHERE user_id = '%s';", userId),
                String.format("DELETE FROM payslip_histories WHERE user_id = '%s';", userId),
                "DELETE FROM paysheets WHERE NAME like '%test api payroll%';",
                String.format("DELETE FROM shift_plan_users WHERE user_id = '%s';", userId),
                String.format("DELETE FROM leave_applications WHERE user_id = '%s';", userId)
        };

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String query : deleteQueries) {
                stmt.executeUpdate(query);
            }
            System.out.println("Xóa toàn bộ dữ liệu test cho user_id = " + userId + " thành công!");

        } catch (SQLException e) {
            System.err.println("Xóa dữ liệu test thất bại cho user_id = " + userId + ": " + e.getMessage());
        }
    }
}

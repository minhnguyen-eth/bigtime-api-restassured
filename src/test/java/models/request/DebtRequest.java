package models.request;

import base.CommonRequest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DebtRequest extends CommonRequest {

    public String createDebt(String token, String userId, String note, int value, String createdBy) {
        String createdAt = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String body = """
                {
                  "id": "",
                  "user_id": "%s",
                  "note": "%s",
                  "value": %d,
                  "created_by": "%s",
                  "created_at": "%s"
                }
                """.formatted(userId, note, value, createdBy, createdAt);

        Response res = sendPost("/api/debt/create", token, body);
        assertSuccess(res, "Tạo khoản nợ/tạm ứng thành công!");
        return note;
    }

    // Hàm tự động lấy ID của khoản nợ mới nhất theo user
    public String getLatestDebtId(String token, String userId) {
        String startDate = LocalDate.now()
                .withDayOfMonth(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endDate = LocalDate.now()
                .withDayOfMonth(LocalDate.now().lengthOfMonth())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String url = String.format(
                "/api/debt/get-list-or-by-auth?page=1&itemsPerPage=20&start_date=%s&end_date=%s",
                startDate, endDate
        );

        Response res = sendGet(url, token);

        JsonPath json = res.jsonPath();
        List<Object> dataList = json.getList("data.data");
        if (dataList == null || dataList.isEmpty()) {
            throw new RuntimeException("Không tìm thấy khoản nợ nào cho user: " + userId);
        }

        //  Lấy ID của record mới nhất (thường ở đầu danh sách)
        return json.getString("data.data[0].id");
    }

    //  Hàm gửi khoản nợ cho nhân viên
    public void submitDebt(String token, String debtId) {
        String body = """
                {
                  "id": "%s"
                }
                """.formatted(debtId);

        Response res = sendPost("/api/debt/submit", token, body);
        assertSuccess(res, "Gửi khoản nợ/tạm ứng thành công!");
    }

    public void approveDebt(String token, String debtId, int status) {
        String body = """
                {
                  "id": "%s",
                  "status": %d
                }
                """.formatted(debtId, status);

        Response res = sendPost("/api/debt/approved", token, body);
        assertSuccess(res, "Nhân viên duyệt khoản nợ/tạm ứng thành công!");
    }

    public void verifyDebtValue(String token, String name, int expectedValue) {
        String url = String.format(
                "/api/debt/get-list-or-by-auth?page=1&itemsPerPage=20&name=%s",
                name
        );

        Response res = sendGet(url, token);
        assertSuccess(res, "Lấy danh sách khoản nợ thành công!");

        JsonPath json = res.jsonPath();
        List<Object> debts = json.getList("data.data");

        if (debts == null || debts.isEmpty()) {
            throw new RuntimeException("Không tìm thấy khoản nợ nào của nhân viên: " + name);
        }

        int actualValue = json.getInt("data.data[0].value");
        String debtId = json.getString("data.data[0].id");
        String note = json.getString("data.data[0].note");

        System.out.printf(
                "🔍 Kiểm tra khoản nợ ID=%s | Note=%s | Value=%d | Expected=%d\n",
                debtId, note, actualValue, expectedValue
        );

        Assert.assertEquals(
                actualValue,
                expectedValue,
                "Giá trị khoản nợ sau khi xử lý không đúng!"
        );

        System.out.println("Giá trị khoản nợ đúng như mong đợi.");
    }

}

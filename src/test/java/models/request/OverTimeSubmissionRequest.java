package models.request;

import base.CommonRequest;
import io.restassured.response.Response;

public class OverTimeSubmissionRequest extends CommonRequest {

    public void submitOverSubmission(String token, String overtimeId, int status) {
        String body = """
                {
                  "id": "%s",
                  "status": %d
                }
                """.formatted(overtimeId, status);

        Response res = sendPost("/api/overtime-submission/submit-for-admin", token, body);
        assertSuccess(res, "Submit overtime submission thành công!");
    }

    // Bây giờ hàm này trả về id của phiếu tăng ca vừa tạo
    public String createOvertimeTicket(
            String token,
            String dayOverTime,
            String userId,
            String userName,
            String day,
            String startTime,
            String endTime
    ) {
        String body = """
            {
              "id": "",
              "day": "%s",
              "start_time": "%s",
              "end_time": "%s",
              "reason": "test api",
              "status": 1,
              "canceled_reason": "",
              "created_at": "%s",
              "created_by": "%s",
              "rejected_reason": "",
              "user_id": "%s",
              "user_name": "%s"
            }
            """.formatted(day, startTime, endTime, dayOverTime, userName, userId, userName);

        Response res = sendPost("/api/overtime-submission/create", token, body);
        assertSuccess(res, "Tạo đơn tăng ca thành công!");

        // Lấy id từ response
        String overtimeId = res.jsonPath().getString("data");
        return overtimeId;
    }

}

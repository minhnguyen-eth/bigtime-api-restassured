package models.request;

import base.CommonRequest;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;

public class CheckDayRequest extends CommonRequest {

    public void createCheckDay(String token, String userCode, String workDay, List<String> checkTimes) {
        // Chuyển List<String> thành JSON array dạng ["08:00:00","17:00:00"]
        String checkTimesJson = checkTimes.stream()
                .map(t -> "\"" + t + "\"")
                .collect(Collectors.joining(", ", "[", "]"));

        String body = """
                {
                  "user_code": "%s",
                  "check_day": "%s",
                  "check_times": %s
                }
                """.formatted(userCode, workDay, checkTimesJson);

        Response res = sendPost("/api/check-day/create", token, body);
        assertSuccess(res, "Chấm công thành công!");
    }

    public void submitCheckDay(String token, int type, String userId, String workDay) {
        String body = """
                {
                  "id": "",
                  "day": "%s",
                  "time_in": "08:00:00",
                  "time_out": "17:00:00",
                  "reason": "",
                  "type": "%d",
                  "status": 2,
                  "user_id": "%s",
                  "created_at": ""
                }
                """.formatted(workDay, type, userId);

        Response res = sendPost("/api/check-day/submit-checkday", token, body);
        assertSuccess(res, "Employee submit check day thành công!");
    }

    public void approveMonthlyWork(String token, String userId) {
        String body = """
                {
                  "month": 10,
                  "year": 2025,
                  "user_id": "%s"
                }
                """.formatted(userId);

        Response res = sendPost("/api/check-day/approved-monthly-work", token, body);
        assertSuccess(res, "Admin phê duyệt công tháng thành công!");
    }
}

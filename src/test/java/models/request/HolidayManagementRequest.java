package models.request;

import base.CommonRequest;
import io.restassured.response.Response;

public class HolidayManagementRequest extends CommonRequest {

    public void createHoliday(String token, String name, String startDay, String endDay) {
        String body = """
                {
                  "id": "",
                  "name": "%s",
                  "start_day": "%s",
                  "end_day": "%s",
                  "total_day": 1,
                  "type": 0,
                  "reason": "123",
                  "is_workday": true,
                  "status": true,
                  "created_at": "2025-10-08 14:47:06",
                  "created_by": "0000000000"
                }
                """.formatted(name, startDay, endDay);

        Response res = sendPost("/api/holiday-management/create", token, body);

        // In ra response để debug
        System.out.println(res.asString());

        assertSuccess(res, "Thêm ngày nghỉ thành công!");
    }
}

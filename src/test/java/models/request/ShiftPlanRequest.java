package models.request;

import base.CommonRequest;
import io.restassured.response.Response;

public class ShiftPlanRequest extends CommonRequest {

    public void createShiftPlan(String token, String userId, String workDay) {
        String body = """
                {
                  "id": "",
                  "name": "Test API Shift Plan",
                  "start_date": "%s",
                  "end_date": "%s",
                  "object_type": 0,
                  "objects": [],
                  "repeat_config": {"repeat_working_time": 1, "repeat_time_off": 0},
                  "repeat_type": 1,
                  "shift_plan_users": ["%s"],
                  "status": true,
                  "working_shift_options": [{"shift": "PxdxhOfve5", "day_of_week": [1,2,3,4,5,6,7]}]
                }
                """.formatted(workDay, workDay, userId);

        Response res = sendPost("/api/shift-plan/create", token, body);
        assertSuccess(res, "Admin tạo phân ca thành công!");
    }
}

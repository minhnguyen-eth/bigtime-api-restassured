package models.request;

import base.CommonRequest;
import io.restassured.response.Response;

public class PayrollRequest extends CommonRequest {

    public void closePayroll(String token, String userId, String workDay) {
        String body = """
                {
                  "user_id": "%s",
                  "day": "%s"
                }
                """.formatted(userId, workDay);

        Response res = sendPost("/api/payroll/create", token, body);
        assertSuccess(res, "Admin chốt công tháng thành công!");
    }
}


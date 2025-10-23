package models.request;

import base.CommonRequest;
import io.restassured.response.Response;

public class AuthRequest extends CommonRequest {

    public Response loginResponse(String username, String password) {
        String loginPayload = String.format("""
            {
                "username": "%s",
                "password": "%s",
                "remember": true
            }
        """, username, password);

        return sendPost("/api/auth/login", "", loginPayload);
    }

    public String login(String username, String password) {
        Response response = loginResponse(username, password);

        if (response.statusCode() == 200) {
            System.out.printf("Login thành công: %s%n", username);
            return response.jsonPath().getString("data.access_token");
        } else {
            System.err.printf("Login thất bại (%s): %s%n", username, response.getBody().asString());
            return null;
        }
    }
}

package base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;

public class CommonRequest {
    public Response sendPost(String endpoint, String token, String body) {
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(CommonHeaders.getAuthHeaders(token))
                .contentType(ContentType.JSON)
                .body(body)
                .post(endpoint);
    }

    public Response sendGet(String endpoint, String token) {
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .headers(CommonHeaders.getAuthHeaders(token))
                .contentType(ContentType.JSON)
                .get(endpoint);
    }

    public void assertSuccess(Response res, String message) {
        Assert.assertEquals(res.statusCode(), 200, "HTTP status phải là 200");
        Assert.assertEquals(res.jsonPath().getInt("code"), 200, "Code trả về phải là 200");
        System.out.println(message);
    }
}

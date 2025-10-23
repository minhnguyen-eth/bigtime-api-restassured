package tests;

import base.BaseTest;
import io.restassured.response.Response;
import models.request.AuthRequest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AuthTest extends BaseTest {

    private final AuthRequest authRequest = new AuthRequest();

    /**
     *  DataProvider: gom các case login thành công vào 1 nơi
     */
    @DataProvider(name = "validCredentials")
    public Object[][] validCredentials() {
        return new Object[][]{
                {dotenv.get("ADMIN_USERNAME"), dotenv.get("ADMIN_PASSWORD"), "Admin"},
                {dotenv.get("ADMIN_DEPARTMENT_USERNAME"), dotenv.get("ADMIN_DEPARTMENT_PASSWORD"), "Admin Department"},
                {dotenv.get("ADMIN_TEAM_USERNAME"), dotenv.get("ADMIN_TEAM_PASSWORD"), "Admin Team"},
                {dotenv.get("EMPLOYEE_USERNAME"), dotenv.get("EMPLOYEE_PASSWORD"), "Employee"}
        };
    }

    /**
     *  Kiểm tra các trường hợp login thành công
     */
    @Test(dataProvider = "validCredentials", description = "Login with valid credentials")
    public void testLoginWithValidCredentials(String username, String password, String role) {
        Response response = authRequest.loginResponse(username, password);
        verifyResponse(response, 200, "Đăng nhập thành công");
    }

    /**
     *  Kiểm tra login sai thông tin
     */
    @Test(description = "Login with invalid credentials")
    public void testLoginWithInvalidCredentials() {
        Response response = authRequest.loginResponse("invalid@gmail.com", "123456");
        verifyResponse(response, 401, "Tên đăng nhập hoặc mật khẩu không đúng");
    }

    /**
     * Hàm dùng chung để verify response
     */
    private void verifyResponse(Response response, int expectedStatus, String expectedMessage) {
        Assert.assertEquals(response.statusCode(), expectedStatus, "Status code không đúng!");
        String actualMessage = response.jsonPath().getString("message");
        Assert.assertEquals(actualMessage, expectedMessage, "Thông báo lỗi không khớp!");
    }
}

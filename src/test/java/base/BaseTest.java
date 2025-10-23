package base;

import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import models.request.AuthRequest;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    protected static Dotenv dotenv;
    protected static String adminToken;
    protected static String employeeToken;
    protected static String employee1DependentToken;
    protected static String employeeNoDependentToken;
    protected AuthRequest authRequest;

    @BeforeClass
    public void baseSetup() {
        dotenv = Dotenv.load();
        RestAssured.baseURI = dotenv.get("BASE_URI");

        authRequest = new AuthRequest();

        adminToken = authRequest.login(
                dotenv.get("ADMIN_USERNAME"),
                dotenv.get("ADMIN_PASSWORD")
        );

        employeeToken = authRequest.login(
                dotenv.get("EMPLOYEE_USERNAME"),
                dotenv.get("EMPLOYEE_PASSWORD")
        );

//        employee1DependentToken = authRequest.login(
//                dotenv.get("EMPLOYEE_1DEPENDENT_USERNAME"),
//                dotenv.get("EMPLOYEE_1DEPENDENT_PASSWORD")
//        );
//
//        employeeNoDependentToken = authRequest.login(
//                dotenv.get("EMPLOYEE_NO_DEPENDENT_USERNAME"),
//                dotenv.get("EMPLOYEE_NO_DEPENDENT_PASSWORD")
//        );

    }
}

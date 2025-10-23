import com.google.gson.Gson;
import com.google.gson.JsonObject;
import base.CommonHeaders;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import static io.restassured.RestAssured.given;

public class GenerateUser {

    public static void main(String[] args) throws Exception {
        Dotenv dotenv = Dotenv.load();
        //  get env variables
        String BASE_URI = dotenv.get("BASE_URI");
        String USERNAME = dotenv.get("ADMIN_USERNAME");
        String PASSWORD = dotenv.get("ADMIN_PASSWORD");

        if (BASE_URI == null || USERNAME == null || PASSWORD == null) {
            System.err.println("Lack of env variables: BASE_URI, USERNAME or PASSWORD.");
            return;
        }

        String loginUrl = BASE_URI + "/api/auth/login";
        String createUserUrl = BASE_URI + "/api/user/create";

        String loginPayload = String.format("""
                {
                    "username": "%s",
                    "password": "%s",
                    "remember": true
                }
                """, USERNAME, PASSWORD);

        RestAssured.useRelaxedHTTPSValidation();

        //  Request login
        Response loginResponse = given()
                .headers(CommonHeaders.getCommonHeaders())
                .body(loginPayload)
                .post(loginUrl);

        String token = loginResponse.jsonPath().getString("data.access_token");
        System.out.println("Access Token: " + token);

        if (token == null || token.isEmpty()) {
            System.err.println("Login failed.");
            return;
        }

        String originalBody = new String(Files.readAllBytes(Paths.get("src/test/resources/bodyCreateUser.txt")));
        Gson gson = new Gson();

        for (int i = 1; i <= 5; i++) {
            JsonObject user = gson.fromJson(originalBody, JsonObject.class);

            String name = "UserTest " + i;
            String code = "CODE " + UUID.randomUUID().toString().substring(0, 8);
            String email = "usertest" + i + "@gmail.com";

            user.addProperty("name", name);
            user.addProperty("code", code);
            user.addProperty("email", email);

            JsonObject wrappedBody = new JsonObject();
            wrappedBody.addProperty("JsonData", gson.toJson(user));

            String requestBody = gson.toJson(wrappedBody);

            // In log
            System.out.println("\n======== REQUEST BODY =========");
            System.out.println(requestBody);
            System.out.println("========================================\n");

            // Request create user
            Map<String, String> headers = CommonHeaders.getAuthHeaders(token);
            Response response = given()
                    .headers(headers)
                    .body(requestBody)
                    .post(createUserUrl);

            System.out.println("User " + i + ": " + response.statusCode());
            System.out.println("Response body: " + response.asString());
        }
    }
}

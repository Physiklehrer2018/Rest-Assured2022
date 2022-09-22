
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUserTest {

    private RequestSpecification reqSpec;
    private Map<String,String> requestBody;
    private Object id;

    @BeforeClass
    public void setup(){

        RestAssured.baseURI = "https://gorest.co.in/";

        reqSpec = given()
                .log().body()
                .header("Authorization","Bearer 976e5357003cefdc2c1d66557ad1d5309e785bf4df3d1ccb2eb08a5406e9e673")
                .contentType(ContentType.JSON);

        requestBody = new HashMap<>();
        requestBody.put("name","Test User from KZ");
        requestBody.put("email","Tester18@dieburger.com");
        requestBody.put("gender","male");
        requestBody.put("status","active");
    }
    @Test
    public void createUserTest(){

        id = given()
                .spec(reqSpec)
                .body(requestBody)
                .when()
                .post("/public/v2/users")
                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id");
    }

    @Test(dependsOnMethods = "createUserTest")
    public void createUserNegativeTest(){
        given()
                .spec(reqSpec)
                .body(requestBody)
                .when()
                .post("/public/v2/users")
                .then()
                .log().body()
                .statusCode(422);
    }
    @Test(dependsOnMethods = "createUserNegativeTest")
    public void editUserTest(){

        String newName = "Updated New Name";

        Map<String,String> updated = new HashMap<>();
        updated.put("name",newName);

        given()
                .spec(reqSpec)
                .body(updated)
                .when()
                .put("/public/v2/users/" + id)
                .then()
                .body("name",equalTo(newName))
                .statusCode(200);
    }
    @Test(dependsOnMethods = "editUserTest")
    public void deleteUserTest(){
        given()
                .spec(reqSpec)
                .when()
                .delete("public/v2/users/" + id)
                .then()
                .log().body()
                .statusCode(204);
    }
}

import POJO.GoRestUser;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GoRestUserTestsWithPOJO {

    private RequestSpecification reqSpec;
    private GoRestUser user;

    @BeforeClass
    public void setup(){
        RestAssured.baseURI = "https://gorest.co.in";

        reqSpec = given()
                .log().uri()
                .header("Authorization", "Bearer 976e5357003cefdc2c1d66557ad1d5309e785bf4df3d1ccb2eb08a5406e9e673")
                .contentType(ContentType.JSON);

        user = new GoRestUser();
        user.setName("NewTesters by Tr");
        user.setEmail("tester22@tester.com");
        user.setGender("male");
        user.setStatus("inactive");
    }

    @Test
    public void createUserTest(){
        user.setId(
                given()
                        .spec(reqSpec)
                        .body(user)
                        .when()
                        .post("/public/v2/users")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id")
        );

        System.out.println("user ID:" + user.getId());
    }

    @Test(dependsOnMethods = "createUserTest")
    public void getUserTest(){
        given()
                .spec(reqSpec)
                .when()
                .get("/public/v2/users/" + user.getId())
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(user.getName()))
                .body("email", equalTo(user.getEmail()));
    }

    @Test(dependsOnMethods = "getUserTest")
    public void createUserNegativeTest(){
        given()
                .spec(reqSpec)
                .body(user)
                .when()
                .post("/public/v2/users")
                .then()
                .log().body()
                .statusCode(422);

    }

    @Test(dependsOnMethods = "createUserNegativeTest")
    public void editUserTest(){
        String newName = "uPDATED UserName";

        Map<String,String> editUserBody = new HashMap<>();
        editUserBody.put("name",newName);

        given()
                .spec(reqSpec)
                .body(editUserBody)
                .when()
                .put("/public/v2/users/" + user.getId())
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(newName));

    }

    @Test(dependsOnMethods = "editUserTest")
    public void deleteUserTest(){
        given()
                .spec(reqSpec)
                .when()
                .delete("/public/v2/users/" + user.getId())
                .then()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "deleteUserTest")
    public void deleteNegativeUserTest(){
        given()
                .spec(reqSpec)
                .when()
                .delete("/public/v2/users/" + user.getId())
                .then()
                .statusCode(404);

    }
}

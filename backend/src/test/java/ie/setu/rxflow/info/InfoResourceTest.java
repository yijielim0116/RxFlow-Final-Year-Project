package ie.setu.rxflow.info;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class InfoResourceTest {

    @Test
    void infoReturnsServiceNameAndVersion() {
        given()
            .when().get("/api/info")
            .then()
            .statusCode(200)
            .body("name", is("rxflow-backend"))
            .body("version", notNullValue());
    }

    @Test
    void healthIsUpWhenDatabaseIsReachable() {
        given()
            .when().get("/q/health/ready")
            .then()
            .statusCode(200)
            .body("status", is("UP"));
    }
}

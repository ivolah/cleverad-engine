package it.cleverad.engine;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import it.cleverad.engine.web.exception.ApiError;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomErrors {
    private static final String URL_PREFIX = "http://195.231.64.79:8088/cleverad/campaign/33";

    private RequestSpecification givenAuth() {
        // return RestAssured.given().auth().form("user", "userPass", formConfig);
        // if (cookie == null) {
        // cookie = RestAssured.given().contentType("application/x-www-form-urlencoded").formParam("password", "userPass").formParam("username", "user").post(URL_PREFIX + "/login").getCookie("JSESSIONID");
        // }
        // return RestAssured.given().cookie("JSESSIONID", cookie);
        return RestAssured.given()
                .auth().preemptive()
                .basic("user", "userPass");
    }

    @Test
    public void whenMethodArgumentMismatch_thenBadRequest() {
        Response response = givenAuth().get(URL_PREFIX );
        ApiError error = response.as(ApiError.class);

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0).contains("should be of type"));
    }
}

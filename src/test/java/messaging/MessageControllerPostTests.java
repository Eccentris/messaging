package messaging;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import java.lang.String;
import javax.ws.rs.core.Application;

import static io.restassured.RestAssured.given;
import static messaging.MessageController.messageDao;

public class MessageControllerPostTests extends JerseyTest {

    // Need to set port as the tests run on a different port than
    // the standard application
    private static final int PORT = 9998;

    @Override
    protected Application configure() {
        return new ResourceConfig(MessageController.class);
    }

    @After
    public void after() throws Exception {
        super.tearDown();
        messageDao.clear();
    }

    /**
     * When basic auth and message content is provided, post should succeed
     * with accurate resource location provided
     * @result Status 201, location header set
     */
    @Test
    public void postShouldSucceedWith201WhenBasicAuthProvidedTest() {

        given()
            .port(PORT)
            .auth()
            .preemptive()
            .basic("username", "password")
            .body("Hello")
        .when()
            .post("/message")
        .then()
            .statusCode(201)
            .header("Location",
                    getBaseUri() + "message/1");

    }

    /**
     * When basic auth is not provided, post should fail
     * @result Status 401, error message
     */
    @Test
    public void postShouldFailWith401WhenBasicAuthNotProvidedTest() {

        Response response = given()
                                .port(PORT)
                                .body("Hello")
                            .when()
                                .post("/message");

        String errorMsg = "Please provide user as part of Basic Auth";

        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals(errorMsg, response.getBody().asString());

    }

    /**
     * Post should fail when no content provided
     * @result Status 400, error message
     */
    @Test
    public void postShouldFailWith400WhenNoContentProvidedTest() {

        Response response = given()
                                .port(PORT)
                                .auth()
                                .preemptive()
                                .basic("username", "password")
                            .when()
                                .post("/message");

        String errorMsg = "Please provide message content between" +
                " 1 and 500 characters in length";

        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals(errorMsg, response.getBody().asString());

    }

    /**
     * Post should fail when empty content provided
     * @result Status 400, error message
     */
    @Test
    public void postShouldFailWith400WhenEmptyContentProvidedTest() {

        Response response = given()
                                .port(PORT)
                                .auth()
                                .preemptive()
                                .basic("username", "password")
                                .body("")
                            .when()
                                .post("/message");

        String errorMsg = "Please provide message content between" +
                " 1 and 500 characters in length";

        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals(errorMsg, response.getBody().asString());

    }

    /**
     * Post should fail when content exceeding length 500 provided
     * @result Status 400, error message
     */
    @Test
    public void postShouldFailWith400WhenTooLongContentProvidedTest() {

        String generatedString = RandomStringUtils.randomAlphabetic(550);

        Response response = given()
                                .port(PORT)
                                .auth()
                                .preemptive()
                                .basic("username", "password")
                                .body(generatedString)
                            .when()
                                .post("/message");

        String errorMsg = "Please provide message content between" +
                " 1 and 500 characters in length";

        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals(errorMsg, response.getBody().asString());

    }

    //TODO test for URISyntaxException

}

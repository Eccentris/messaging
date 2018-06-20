package messaging;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import javax.ws.rs.core.Application;

import static io.restassured.RestAssured.given;
import static messaging.MessageController.messageDao;
import static org.hamcrest.CoreMatchers.equalTo;

public class MessageControllerUpdateTests extends JerseyTest {

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
     * When basic auth is provided, user matches message author
     * a message exists and new content is valid, update should succeed
     * @result Status 200, Expected JSON body
     */
    @Test
    public void updateShouldSucceedWith200Test() {

        //Add message so there is one to edit
        Message message = new Message("username", "hello");
        messageDao.add(message);

        given()
            .port(PORT)
            .auth()
            .preemptive()
            .basic("username", "password")
            .body("Another")
        .when()
            .put("/message/1")
        .then()
            .statusCode(200)
            .body("content", equalTo("Another"))
            .body("author", equalTo("username"));

    }

    /**
     * Update should fail if no basic auth provided
     * @result Status 401, error message
     */
    @Test
    public void updateShouldFailWith401WhenBasicAuthNotProvidedTest() {

        //Add message so there is one to update
        Message message = new Message("username", "hello");
        messageDao.add(message);

        Response response = given()
                                .port(PORT)
                                .body("Another")
                            .when()
                                .put("/message/1");

        String errorMsg = "Please provide user as part of Basic Auth";

        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals(errorMsg, response.getBody().asString());

    }

    /**
     * Update should fail when no content provided
     * @result Status 400, error message
     */
    @Test
    public void updateShouldFailWith400WhenNoContentProvidedTest() {

        //Add message so there is one to update
        Message message = new Message("username", "hello");
        messageDao.add(message);

        Response response = given()
                                .port(PORT)
                                .auth()
                                .preemptive()
                                .basic("username", "password")
                            .when()
                                .put("/message/1");

        String errorMsg = "Please provide message content between" +
                " 1 and 500 characters in length";

        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals(errorMsg, response.getBody().asString());

    }

    /**
     * Update should fail when empty content provided
     * @result Status 400, error message
     */
    @Test
    public void updateShouldFailWith400WhenEmptyContentProvidedTest() {

        //Add message so there is one to update
        Message message = new Message("username", "hello");
        messageDao.add(message);

        Response response = given()
                                .port(PORT)
                                .auth()
                                .preemptive()
                                .basic("username", "password")
                                .body("")
                            .when()
                                .put("/message/1");

        String errorMsg = "Please provide message content between" +
                " 1 and 500 characters in length";

        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals(errorMsg, response.getBody().asString());

    }

    /**
     * Update should fail when content exceeding length 500 provided
     * @result Status 400, error message
     */
    @Test
    public void updateShouldFailWith400WhenTooLongContentProvidedTest() {

        //Add message so there is one to update
        Message message = new Message("username", "hello");
        messageDao.add(message);

        String generatedString = RandomStringUtils.randomAlphabetic(550);

        Response response = given()
                                .port(PORT)
                                .auth()
                                .preemptive()
                                .basic("username", "password")
                                .body(generatedString)
                            .when()
                                .put("/message/1");

        String errorMsg = "Please provide message content between" +
                " 1 and 500 characters in length";

        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals(errorMsg, response.getBody().asString());

    }

    /**
     * Update should fail if requested message to edit does not exist
     * @result Status 404, error message
     */
    @Test
    public void updateShouldFailWith404WhenMessageDoesNotExistTest() {

        Response response = given()
                                .port(PORT)
                                .auth()
                                .preemptive()
                                .basic("username", "password")
                                .body("hello")
                            .when()
                                .put("/message/1");

        String errorMsg = "Could not find a message with id=1";

        Assert.assertEquals(404, response.getStatusCode());
        Assert.assertEquals(errorMsg, response.getBody().asString());

    }

    /**
     * Delete should fail if user does not match Message author
     * @result Status 403, error message
     */
    @Test
    public void updateShouldFailWith403WhenUserDoesNotMatchAuthorTest() {

        //Add message so there is one to update
        Message message = new Message("username", "hello");
        messageDao.add(message);

        Response response = given()
                                .port(PORT)
                                .auth()
                                .preemptive()
                                .basic("another", "password")
                                .body("new")
                            .when()
                                .put("/message/1");

        String errorMsg = "Only user=username is allowed to edit this message";

        Assert.assertEquals(403, response.getStatusCode());
        Assert.assertEquals(errorMsg, response.getBody().asString());

    }

}


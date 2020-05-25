package messaging;

import io.restassured.response.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import javax.ws.rs.core.Application;

import static io.restassured.RestAssured.given;
import static messaging.MessageController.messageDao;
import static org.hamcrest.CoreMatchers.equalTo;

public class MessageControllerGetTests extends JerseyTest {

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
     * When a message exists, get should succeed
     * @result Status 200, Expected JSON body
     */
    @Test
    public void getShouldSucceedWith200Test() {

        //Add message so there is one to retrieve
        Message message = new Message("username", "hello");
        messageDao.add(message);

        given()
            .port(PORT)
            .auth()
            .preemptive()
            .basic("username", "password")
        .when()
            .get("/message/1")
        .then()
            .statusCode(200)
            .body("content", equalTo("hello"))
            .body("author", equalTo("username"));

    }

    /**
     * get should succeed for any message, even if not same user that posted
     * @result Status 200, Expected JSON body
     */
    @Test
    public void getShouldSucceedWith200WithDifferentUserTest() {

        //Add message so there is one to retrieve
        Message message = new Message("username", "hello");
        messageDao.add(message);

        given()
            .port(PORT)
            .auth()
            .preemptive()
            .basic("different_username", "password")
        .when()
            .get("/message/1")
        .then()
            .statusCode(200)
            .body("content", equalTo("hello"))
            .body("author", equalTo("username"));
    }

    /**
     * get should fail if requested Message does not exist
     * @result Status 404, Error message
     */
    @Test
    public void getShouldFailWith404WhenMessageDoesNotExistTest() {

        Response response = given()
                                .port(PORT)
                                .auth()
                                .preemptive()
                                .basic("username", "password")
                            .when()
                                .get("/message/1");

        String errorMsg = "Could not find a message with id=1";

        Assert.assertEquals(404, response.getStatusCode());
        Assert.assertEquals(errorMsg, response.getBody().asString());
    }


}

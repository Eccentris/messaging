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

public class MessageControllerGetAllTests extends JerseyTest {

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
     * When basic auth is provided and a message exists, get all should succeed
     * @result Status 200, Expected JSON body
     */
    @Test
    public void getAllShouldSucceedWith200OneMessageTest() {

        //Add message so there is one to retrieve
        Message message = new Message("username", "hello");
        messageDao.add(message);

        given()
            .port(PORT)
            .auth()
            .preemptive()
            .basic("username", "password")
        .when()
            .get("/message")
        .then()
            .statusCode(200)
            .body("1.content", equalTo("hello"))
            .body("1.author", equalTo("username"));

    }

    /**
     * When basic auth is provided and two messages exist, get all should succeed
     * @result Status 200, Expected JSON body
     */
    @Test
    public void getAllShouldSucceedWith200TwoMessagesTest() {

        //Add two messages so there are two to retrieve
        Message message = new Message("username", "hello");
        messageDao.add(message);

        message = new Message("another", "other content");
        messageDao.add(message);

        given()
            .port(PORT)
            .auth()
            .preemptive()
            .basic("username", "password")
        .when()
            .get("/message")
         .then()
            .statusCode(200)
            .body("1.content", equalTo("hello"))
            .body("1.author", equalTo("username"))
            .body("2.content", equalTo("other content"))
            .body("2.author", equalTo("another"));

    }

    /**
     * get all should succeed without any messages
     * @result Status 200, expected JSON body
     */
    @Test
    public void getAllShouldSucceedWith200WhenNoMessagesTest() {

        Response response = given()
                                .port(PORT)
                                .auth()
                                .preemptive()
                                .basic("username", "password")
                            .when()
                                .get("/message");

        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertEquals("{}", response.getBody().asString());
    }

    /**
     * get all should successfully retrieve messages authored by other users
     * @result Status 200, Expected JSON body
     */
    @Test
    public void getAllShouldSucceedWith200WhenAnotherAuthorTest() {

        //Add message so there is one to retrieve
        Message message = new Message("username", "hello");
        messageDao.add(message);

        given()
            .port(PORT)
            .auth()
            .preemptive()
            .basic("another", "password")
        .when()
            .get("/message")
        .then()
            .statusCode(200)
            .body("1.content", equalTo("hello"))
            .body("1.author", equalTo("username"));

    }

    /**
     * get all should fail if no Basic auth
     * @result Status 401, Error message
     */
    @Test
    public void getAllShouldFailWith401WhenBasicAuthNotProvidedTest() {

        Response response = given()
                                .port(PORT)
                            .when()
                                .get("/message");

        String errorMsg = "Please provide user as part of Basic Auth";

        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals(errorMsg, response.getBody().asString());
    }

}
